package com.picsauditing.actions.audits;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class QuestionTranslationsAjax extends PicsActionSupport {

	private int id;

	private Database db = new Database();

	private List<BasicDynaBean> values;

	@Override
	public String execute() throws Exception {

		// SELECT locale, msgValue FROM app_translation WHERE msgKey = "AuditQuestion.37.name";

		SelectSQL selectTranslation = new SelectSQL("app_translation a");
		selectTranslation.addField("a.locale, a.msgValue");

		selectTranslation.addWhere("a.msgKey = 'AuditQuestion." + id + ".name'");

		values = db.select(selectTranslation.toString(), false);

		return SUCCESS;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<BasicDynaBean> getValues() {
		return values;
	}

	public void setValues(List<BasicDynaBean> values) {
		this.values = values;
	}

}
