package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageTranslations extends PicsActionSupport {

	private String key;
	private Locale localeFrom = Locale.ENGLISH;
	private Locale localeTo = Locale.FRENCH;
	private List<Translation> list;
	private AppTranslation translation;

	private AuditTypeDAO dao;

	public ManageTranslations(AuditTypeDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.Translator);

		if (button != null) {
			if (button.equalsIgnoreCase("save") && translation != null) {
				JSONObject out = new JSONObject();

				try {
					if (translation.getId() > 0 && Strings.isEmpty(translation.getValue())) {
						dao.deleteData(AppTranslation.class, "id = " + translation.getId());
					} else {
						translation.setAuditColumns(permissions);
						dao.save(translation);
						out.put("id", translation.getId());
					}

					out.put("success", true);
				} catch (Exception e) {
					out.put("success", false);
					out.put("reason", e.getMessage());
				}

				output = out.toJSONString();
				return BLANK;
			}
		}

		String where = "t.locale = '" + localeFrom + "'";
		key = Utilities.escapeQuotes(key);
		if (!Strings.isEmpty(key))
			where += " AND (t.key LIKE '%" + key + "%' OR t.value LIKE '%" + key + "%')";
		List<AppTranslation> listFrom = (List<AppTranslation>) dao.findWhere(AppTranslation.class, where, 100, "t.key");

		List<String> keys = new ArrayList<String>();
		for (AppTranslation from : listFrom) {
			keys.add(from.getKey());
		}

		Map<String, AppTranslation> mapTo = new HashMap<String, AppTranslation>();

		if (localeTo != null && keys.size() > 0) {
			where = "t.locale = '" + localeTo + "' AND t.key IN (" + Strings.implodeForDB(keys, ",") + ")";

			List<AppTranslation> listTo = (List<AppTranslation>) dao.findWhere(AppTranslation.class, where, 0);
			for (AppTranslation to : listTo) {
				mapTo.put(to.getKey(), to);
			}
		}

		list = new ArrayList<Translation>();
		for (AppTranslation from : listFrom) {
			list.add(new Translation(from, mapTo.get(from.getKey())));
		}

		return SUCCESS;
	}

	public class Translation {

		public AppTranslation from;
		public AppTranslation to;

		public Translation(AppTranslation from, AppTranslation to) {
			this.from = from;
			if (to == null) {
				to = new AppTranslation();
				to.setLocale(from.getLocale());
			}
			this.to = to;
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

}
