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

	// Pseudo filters
	private int[] fromQualityRatings = null;
	private Boolean fromShowApplicable = null;
	private String[] fromSourceLanguages = null;
	private int[] toQualityRatings = null;
	private Boolean toShowApplicable = null;
	private String[] toSourceLanguages = null;

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
				if (button.contains("Ajax")) {
					return BLANK;
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

						if (Strings.isEmpty(translation.getSourceLanguage())
								&& !localeFrom.getLanguage().equals(translation.getLocale())) {
							translation.setSourceLanguage(localeFrom.getLanguage());
						}

						if (translation.getQualityRating() == null) {
							translation.setQualityRating(TranslationQualityRating.Good);
						}

						dao.save(translation);
						out.put("id", translation.getId());

						updateOtherLanguagesToQuestionable();
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

		SelectSQL sql = buildSQL();
		run(sql);

		list = new ArrayList<Translation>();
		for (BasicDynaBean row : data) {
			list.add(new Translation(row));
		}
		if (download) {
			addExcelColumns(sql);
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	private void updateOtherLanguagesToQuestionable() {
		List<AppTranslation> nowQuestionable = (List<AppTranslation>) dao.findWhere(AppTranslation.class, "t.key = '"
				+ translation.getKey() + "' AND t.sourceLanguage = '" + translation.getLocale() + "'");
		for (AppTranslation questionable : nowQuestionable) {
			questionable.setQualityRating(TranslationQualityRating.Questionable);
			questionable.setAuditColumns(permissions);
			dao.save(questionable);
		}
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String update() {
		if (translation != null) {
			translation.setAuditColumns();
			dao.save(translation);
		}

		return SUCCESS;
	}

	private SelectSQL buildSQL() throws Exception {
		SelectSQL sql = new SelectSQL("app_translation t1");
		
		sql.setSQL_CALC_FOUND_ROWS(true);
		
		sql.addJoin("LEFT JOIN app_translation t2 ON t1.msgKey = t2.msgKey AND t2.locale = '" + localeTo + "'");
		sql.addJoin("LEFT JOIN users from_user ON from_user.id = t1.updatedBy");
		sql.addJoin("LEFT JOIN users to_user ON to_user.id = t2.updatedBy");
		
		sql.addField("t1.msgKey");
		sql.addField("t1.msgValue fromValue");
		sql.addField("t1.lastUsed fromLastUsed");
		sql.addField("t1.id fromID");
		sql.addField("t1.updatedBy fromUpdatedBy");
		sql.addField("t1.applicable fromApplicable");
		sql.addField("t1.sourceLanguage fromSourceLanguage");
		sql.addField("t1.qualityRating fromQualityRating");
		sql.addField("from_user.name fromUpdatedByName");
		sql.addField("t1.updateDate fromUpdateDate");
		
		if (download) {
			sql.addField("t1.createdBy fromCreatedBy");
			sql.addField("t1.creationDate fromCreationDate");
			sql.addField("t1.locale fromLocale");
		}
		
		sql.addField("t2.id toID");
		sql.addField("t2.msgValue toValue");
		sql.addField("t2.lastUsed toLastUsed");
		sql.addField("t2.updatedBy toUpdatedBy");
		sql.addField("t2.applicable toApplicable");
		sql.addField("t2.sourceLanguage toSourceLanguage");
		sql.addField("t2.qualityRating toQualityRating");
		sql.addField("to_user.name toUpdatedByName");
		sql.addField("t2.updateDate toUpdateDate");
		
		if (download) {
			sql.addField("t2.createdBy toCreatedBy");
			sql.addField("t2.creationDate toCreationDate");
			sql.addField("t2.locale toLocale");
		}
		
		sql.addWhere("t1.locale = '" + localeFrom + "'");

		sql.addOrderBy("t1.msgKey");

		addFiltersToSql(sql);

		return sql;
	}

	private void addFiltersToSql(SelectSQL sql) {
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

		if (fromQualityRatings != null && fromQualityRatings.length > 0) {
			sql.addWhere(String.format("t1.qualityRating IN (%1$s)",
					Strings.implode(fromQualityRatings)));
		}

		if (fromShowApplicable != null) {
			sql.addWhere(String.format("t1.applicable = %1$d", (fromShowApplicable ? 1 : 0)));
		}

		if (filterOn(fromSourceLanguages)) {
			sql.addWhere(String.format("t1.sourceLanguage IN (%1$s)",
					Strings.implodeForDB(fromSourceLanguages, ",")));
		}

		if (toQualityRatings != null && toQualityRatings.length > 0) {
			sql.addWhere(String.format("t2.qualityRating IN (%1$s)",
					Strings.implode(toQualityRatings)));
		}

		if (toShowApplicable != null) {
			sql.addWhere(String.format("t2.applicable = %1$d", (toShowApplicable ? 1 : 0)));
		}

		if (filterOn(toSourceLanguages)) {
			sql.addWhere(String.format("t2.sourceLanguage IN (%1$s)",
					Strings.implodeForDB(toSourceLanguages, ",")));
		}

		if (isTracingOn()) {
			if (getI18nUsedKeys().size() > 0)
				sql.addWhere("t1.msgKey IN (" + Strings.implodeForDB(getI18nUsedKeys(), ",") + ")");
			else {
				addActionMessage("Open pages containing internationalized text and then return to this report.");
			}
		}
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
			from.setApplicable(Integer.parseInt(row.get("fromApplicable").toString()) == 1);
			from.setSourceLanguage(row.get("fromSourceLanguage") == null ? null : row.get("fromSourceLanguage")
					.toString());
			
			Object fromLastUsed = row.get("fromLastUsed");
			
			if (fromLastUsed != null) {
				from.setLastUsed(DateBean.parseDate(fromLastUsed.toString()));
			}
			
			Object fromUpdatedBy = row.get("fromUpdatedBy");
			Object fromUpdatedByName = row.get("fromUpdatedByName");
			
			if (fromUpdatedBy != null && fromUpdatedByName != null) {
				from.setUpdatedBy(new User(Integer.parseInt(fromUpdatedBy.toString())));
				from.getUpdatedBy().setName(fromUpdatedByName.toString());
			}
			
			Object fromUpdateDate = row.get("fromUpdateDate");
			
			if (fromUpdateDate != null) {
			    from.setUpdateDate(DateBean.parseDate(fromUpdateDate.toString()));
			}
			

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
					to.setApplicable(Integer.parseInt(row.get("toApplicable").toString()) == 1);
					to.setSourceLanguage(row.get("toSourceLanguage") == null ? null : row.get("toSourceLanguage")
							.toString());
					
					Object toLastUsed = row.get("toLastUsed");
					
					if (toLastUsed != null) {
						to.setLastUsed(DateBean.parseDate(toLastUsed.toString()));
					}
					
					Object toUpdatedBy = row.get("toUpdatedBy");
					Object toUpdatedByName = row.get("toUpdatedByName");
					
					if (toUpdatedBy != null && toUpdatedByName != null) {
					    to.setUpdatedBy(new User(Integer.parseInt(toUpdatedBy.toString())));
					    to.getUpdatedBy().setName(toUpdatedByName.toString());
					}
					
					Object toUpdateDate = row.get("toUpdateDate");
		            
		            if (toUpdateDate != null) {
		                to.setUpdateDate(DateBean.parseDate(toUpdateDate.toString()));
		            }
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

	public String getLanguageNameFromISOCode(String isocode) {
		if (!Strings.isEmpty(isocode)) {
			Locale locale = new Locale(isocode);
			return locale.getDisplayLanguage();
		}

		return isocode;
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

	public int[] getFromQualityRatings() {
		return fromQualityRatings;
	}

	public void setFromQualityRatings(int[] fromQualityRatings) {
		this.fromQualityRatings = fromQualityRatings;
	}

	public Boolean getFromShowApplicable() {
		return fromShowApplicable;
	}

	public void setFromShowApplicable(Boolean fromShowApplicable) {
		this.fromShowApplicable = fromShowApplicable;
	}

	public String[] getFromSourceLanguages() {
		return fromSourceLanguages;
	}

	public void setFromSourceLanguages(String[] fromSourceLanguages) {
		this.fromSourceLanguages = fromSourceLanguages;
	}

	public int[] getToQualityRatings() {
		return toQualityRatings;
	}

	public void setToQualityRatings(int[] toQualityRatings) {
		this.toQualityRatings = toQualityRatings;
	}

	public Boolean getToShowApplicable() {
		return toShowApplicable;
	}

	public void setToShowApplicable(Boolean toShowApplicable) {
		this.toShowApplicable = toShowApplicable;
	}

	public String[] getToSourceLanguages() {
		return toSourceLanguages;
	}

	public void setToSourceLanguages(String[] toSourceLanguages) {
		this.toSourceLanguages = toSourceLanguages;
	}

	public void setList(List<Translation> list) {
		this.list = list;
	}
}
