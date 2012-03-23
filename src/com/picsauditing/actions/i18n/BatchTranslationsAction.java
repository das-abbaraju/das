package com.picsauditing.actions.i18n;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.GoogleTranslate;

@SuppressWarnings("serial")
public class BatchTranslationsAction extends PicsActionSupport {
	private int limit = 100;
	private String localeTo = "fr";
	private int count = 0;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public String count() throws Exception {
		SelectSQL sql = getBaseSQL();
		sql.addField("COUNT(*) total");

		Database db = new Database();
		List<BasicDynaBean> data = db.select(sql.toString(), false);
		count = Integer.parseInt(data.get(0).get("total").toString());

		return SUCCESS;
	}

	public String process() throws Exception {

		SelectSQL sql = getBaseSQL();
		sql.addField("t_from.id");
		sql.addField("t_from.msgKey");
		sql.addField("t_from.msgKey");
		sql.addField("t_from.msgValue");
		sql.setLimit(limit);

		Database db = new Database();
		List<BasicDynaBean> data = db.select(sql.toString(), true);

		for (BasicDynaBean d : data) {
			String key = d.get("msgKey").toString();
			String text = d.get("msgValue").toString();

			String translation = GoogleTranslate.translate(text, "en", localeTo);

			AppTranslation a = new AppTranslation();
			a.setLocale(localeTo);
			a.setKey(key);
			a.setValue(translation);
			a.setAuditColumns(new User(User.SYSTEM));
			a.setSourceLanguage("en");
			a.setQualityRating(TranslationQualityRating.Bad);

			dao.save(a);
		}

		return SUCCESS;
	}

	private SelectSQL getBaseSQL() {
		SelectSQL sql = new SelectSQL("app_translation t_from");
		sql.addJoin("LEFT JOIN app_translation t_to ON t_from.msgKey = t_to.msgKey AND t_to.locale = '" + localeTo
				+ "'");
		sql.addWhere("t_from.locale = 'en'");
		sql.addWhere("t_to.id IS NULL");
		sql.addWhere("t_from.msgValue NOT LIKE '%{_,%'");
		sql.addWhere("t_from.msgValue NOT LIKE '%${%'");

		return sql;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getLocaleTo() {
		return localeTo;
	}

	public void setLocaleTo(String locateTo) {
		this.localeTo = locateTo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
