package com.picsauditing.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageTranslations extends ReportActionSupport {

	private String key;
	private String search;
	private String searchType;
	private Locale localeFrom = Locale.ENGLISH;
	private Locale localeTo = null;
	private List<Translation> list;
	private AppTranslation translation;
	private ReportFilter filter;
	private boolean showDoneButton;

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.Translator)
	public String execute() throws Exception {
		if (localeTo == null) {
			localeTo = new Locale(permissions.getLocale().getLanguage());
		}

		if (button != null) {
			if (button.startsWith("tracing")) {
				Map<String, Object> session = ActionContext.getContext().getSession();
				if (button.contains("On")) {
					session.put(i18nTracing, true);
				}
				if (button.contains("Off")) {
					session.put(i18nTracing, false);
				}
				if (button.contains("Clear")) {
					getI18nUsedKeys().clear();
				}
			}
			if (button.toLowerCase().contains("save") && translation != null) {
				JSONObject out = new JSONObject();

				try {
					if (translation.getId() > 0 && Strings.isEmpty(translation.getValue())) {
						dao.deleteData(AppTranslation.class, "id = " + translation.getId());
					} else {
						if (Strings.isEmpty(translation.getKey())) {
							addActionError("Missing Translation Key");
							throw new Exception("Missing Translation Key");
						}
						translation.setAuditColumns(permissions);
						translation.setSourceLanguage(localeFrom.getLanguage());
						dao.save(translation);
						out.put("id", translation.getId());
					}
					I18nCache.getInstance().clear();
					flagClearCache();

					out.put("success", true);
				} catch (Exception e) {
					out.put("success", false);
					out.put("reason", e.getMessage());
				}

				if (getRequestURL().toLowerCase().contains("ajax")) {
					output = out.toJSONString();
					return BLANK;
				}

				if (translation.getKey().indexOf(".") > 0) {
					key = translation.getKey().substring(0, translation.getKey().indexOf("."));
				} else {
					key = translation.getKey();
				}
			}
		}

		SelectSQL sql = buildAndRunSQL();

		list = new ArrayList<Translation>();
		for (BasicDynaBean row : data) {
			list.add(new Translation(row));
		}
		if (download) {
			addExcelColumns(sql);
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String popupAjax() throws Exception {
		buildAndRunSQL();

		list = new ArrayList<Translation>();
		for (BasicDynaBean row : data) {
			list.add(new Translation(row));
		}
		
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String updateQualityRating() {
		if (translation != null) {
			translation.setAuditColumns();
			dao.save(translation);
		}

		return SUCCESS;
	}

	private SelectSQL buildAndRunSQL() throws Exception {
		SelectSQL sql = new SelectSQL("app_translation t1");
		sql.setSQL_CALC_FOUND_ROWS(true);
		sql.addWhere("t1.locale = '" + localeFrom + "'");
		sql.addJoin("LEFT JOIN app_translation t2 ON t1.msgKey = t2.msgKey AND t2.locale = '" + localeTo + "'");
		sql.addField("t1.msgKey");
		sql.addField("t1.msgValue fromValue");
		sql.addField("t1.lastUsed fromLastUsed");
		sql.addField("t1.id fromID");
		sql.addField("t1.updatedBy fromUpdatedBy");
		if (download) {
			sql.addField("t1.updateDate fromUpdateDate");
			sql.addField("t1.createdBy fromCreatedBy");
			sql.addField("t1.creationDate fromCreationDate");
			sql.addField("t1.locale fromLocale");
		}
		sql.addField("t1.qualityRating fromQualityRating");
		sql.addField("t2.id toID");
		sql.addField("t2.msgValue toValue");
		sql.addField("t2.lastUsed toLastUsed");
		sql.addField("t2.updatedBy toUpdatedBy");
		if (download) {
			sql.addField("t2.updateDate toUpdateDate");
			sql.addField("t2.createdBy toCreatedBy");
			sql.addField("t2.creationDate toCreationDate");
			sql.addField("t2.locale toLocale");
		}
		sql.addField("t2.qualityRating toQualityRating");

		sql.addOrderBy("t2.updatedBy, t2.lastUsed DESC, t1.updatedBy, t1.lastUsed DESC");

		if (searchType != null) {
			if (searchType.equals("Common")) {
				String select = "SELECT msgValue, count(*) total "
						+ "FROM app_translation WHERE locale = 'en' GROUP BY msgValue";
				sql.addJoin("JOIN (" + select + ") tcount ON tcount.msgValue = t1.msgValue");
				// sql.addField("tcount.total");
				sql.addWhere("tcount.total > 10");
				sql.addWhere("t1.msgValue != 'Translation missing'");
			}
			if (searchType.equals("MissingTo")) {
				sql.addWhere("t2.id IS NULL AND t1.msgValue != 'Translation missing'");
			}
			if (searchType.equals("MissingFrom")) {
				sql.addWhere("t1.msgValue = 'Translation missing'");
			}
			if (searchType.equals("Updated")) {
				sql.addWhere("t1.updateDate > t2.updateDate");
			}
			if (searchType.equals("Unused")) {
				sql.addWhere("t1.lastUsed IS NULL OR t1.lastUsed < DATE_SUB(NOW(), INTERVAL 1 WEEK)");
			}
		}

		if (!Strings.isEmpty(search))
			sql.addWhere("t1.msgValue LIKE '%" + Utilities.escapeQuotes(search) + "%' OR LOWER(t1.msgKey) LIKE '%"
					+ Utilities.escapeQuotes(search).toLowerCase() + "%'");

		if (!Strings.isEmpty(key)) {
			sql.addWhere("LOWER(t1.msgKey) LIKE '%" + Utilities.escapeQuotes(key).toLowerCase() + "%'");
		}

		if (isTracingOn()) {
			if (getI18nUsedKeys().size() > 0)
				sql.addWhere("t1.msgKey IN (" + Strings.implodeForDB(getI18nUsedKeys(), ",") + ")");
			else {
				addActionMessage("Open pages containing internationalized text and then return to this report.");
				return null;
			}
		} else if (permissions.getAdminID() == 0 && (permissions.isContractor() || permissions.isOperatorCorporate())) {
			// addAlertMessage("Turn On Tracing to Use this report");
			// return SUCCESS;
		}

		run(sql);

		return sql;
	}

	public class Translation {

		public AppTranslation from;
		public AppTranslation to;

		public Translation(BasicDynaBean row) {
			from = new AppTranslation();
			from.setId(Integer.parseInt(row.get("fromID").toString()));
			from.setKey(row.get("msgKey").toString());
			from.setValue(row.get("fromValue").toString());
			from.setLocale(localeFrom.getLanguage());
			from.setQualityRating(TranslationQualityRating.getRatingFromOrdinal(Integer.parseInt(row.get(
					"fromQualityRating").toString())));
			Object fromLastUsed = row.get("fromLastUsed");
			if (fromLastUsed != null)
				from.setLastUsed(DateBean.parseDate(fromLastUsed.toString()));
			Object fromUpdatedBy = row.get("fromUpdatedBy");
			if (fromUpdatedBy != null)
				from.setUpdatedBy(new User(Integer.parseInt(fromUpdatedBy.toString())));

			Object toID = row.get("toID");
			if (toID != null) {
				to = new AppTranslation();
				to.setId(Integer.parseInt(toID.toString()));
				if (to.getId() > 0) {
					to.setKey(from.getKey());
					to.setValue(row.get("toValue").toString());
					to.setLocale(localeTo.getLanguage());
					to.setQualityRating(TranslationQualityRating.getRatingFromOrdinal(Integer.parseInt(row.get(
							"toQualityRating").toString())));
					Object toLastUsed = row.get("toLastUsed");
					if (toLastUsed != null)
						to.setLastUsed(DateBean.parseDate(toLastUsed.toString()));
					Object toUpdatedBy = row.get("toUpdatedBy");
					if (toUpdatedBy != null)
						to.setUpdatedBy(new User(Integer.parseInt(toUpdatedBy.toString())));
				} else {
					to = null;
				}
			}
		}

		public List<AppTranslation> getItems() {
			List<AppTranslation> list = new ArrayList<AppTranslation>();
			list.add(from);
			list.add(to);
			return list;
		}
	}

	public AppTranslation getTranslation() {
		return translation;
	}

	public void setTranslation(AppTranslation translation) {
		this.translation = translation;
	}

	public List<Translation> getList() {
		return list;
	}

	public Locale getLocaleFrom() {
		return localeFrom;
	}

	public void setLocaleFrom(Locale localeFrom) {
		this.localeFrom = localeFrom;
	}

	public Locale getLocaleTo() {
		return localeTo;
	}

	public void setLocaleTo(Locale localeTo) {
		this.localeTo = localeTo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public ReportFilter getFilter() {
		return filter;
	}

	public void setFilter(ReportFilter filter) {
		this.filter = filter;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public boolean isShowDoneButton() {
		return showDoneButton;
	}

	public void setShowDoneButton(boolean showDoneButton) {
		this.showDoneButton = showDoneButton;
	}

	public boolean isTracingOn() {
		try {
			String tracing = ActionContext.getContext().getSession().get(i18nTracing).toString();
			return Boolean.parseBoolean(tracing) == true;
		} catch (Exception e) {
			return false;
		}
	}

	public void addExcelColumns(SelectSQL sql) throws IOException {
		excelSheet.setData(data);
		excelSheet.buildWorkbook();

		excelSheet = addColumnsFromSQL(excelSheet, sql);

		String filename = this.getClass().getSimpleName();
		excelSheet.setName(filename);
		HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

		filename += ".xls";

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		outstream.close();
	}
}
