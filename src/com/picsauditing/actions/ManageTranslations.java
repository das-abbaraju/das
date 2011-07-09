package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
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

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.Translator);

		if (localeTo == null) {
			localeTo = permissions.getLocale();
		}
		
		if (button != null) {
			if (button.startsWith("tracing")) {
				Map<String, Object> session = ActionContext.getContext().getSession();
				if (button.equals("tracingOn")) {
					session.put(i18nTracing, true);
				}
				if (button.equals("tracingOff")) {
					session.put(i18nTracing, false);
				}
				if (button.equals("tracingClear")) {
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
						dao.save(translation);
						out.put("id", translation.getId());
					}
					I18nCache.getInstance().clear();

					out.put("success", true);
				} catch (Exception e) {
					out.put("success", false);
					out.put("reason", e.getMessage());
				}

				if (getRequestURL().toLowerCase().contains("ajax")) {
					output = out.toJSONString();
					return BLANK;
				}
				key = translation.getKey().substring(0, translation.getKey().indexOf("."));
			}
		}

		SelectSQL sql = new SelectSQL("app_translation t1");
		sql.addOrderBy("t1.updateDate DESC");
		sql.setSQL_CALC_FOUND_ROWS(true);
		sql.addWhere("t1.locale = '" + localeFrom + "'");
		sql.addJoin("LEFT JOIN app_translation t2 ON t1.msgKey = t2.msgKey AND t2.locale = '" + localeTo + "'");
		sql.addField("t1.msgKey");
		sql.addField("t1.id fromID");
		sql.addField("t1.msgValue fromValue");
		sql.addField("t2.id toID");
		sql.addField("t2.msgValue toValue");

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
				return SUCCESS;
			}
		} else if(permissions.getAdminID() == 0 && (permissions.isContractor() || permissions.isOperatorCorporate())) {
			//addAlertMessage("Turn On Tracing to Use this report");
			//return SUCCESS;
		}

		run(sql);

		list = new ArrayList<Translation>();
		for (BasicDynaBean row : data) {
			list.add(new Translation(row));
		}

		return SUCCESS;
	}

	public class Translation {

		public AppTranslation from;
		public AppTranslation to;

		public Translation(BasicDynaBean row) {
			from = new AppTranslation();
			from.setId(Integer.parseInt(row.get("fromID").toString()));
			from.setKey(row.get("msgKey").toString());
			from.setValue(row.get("fromValue").toString());

			Object toID = row.get("toID");
			if (toID != null) {
				to = new AppTranslation();
				to.setId(Integer.parseInt(toID.toString()));
				if (to.getId() > 0) {
					to.setKey(from.getKey());
					to.setValue(row.get("toValue").toString());
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

	public boolean isTracingOn() {
		try {
			String tracing = ActionContext.getContext().getSession().get(i18nTracing).toString();
			return Boolean.parseBoolean(tracing) == true;
		} catch (Exception e) {
			return false;
		}
	}
}
