package com.picsauditing.actions.audits;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class QuestionTranslationsAjax extends PicsActionSupport {

	private int id;

	private List<AppTranslation> values;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		values = (List<AppTranslation>) dao.findWhere(AppTranslation.class,
				"key = 'AuditQuestion." + id + ".name'", 0, "locale");

		return SUCCESS;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AppTranslation> getValues() {
		return values;
	}

	public void setValues(List<AppTranslation> values) {
		this.values = values;
	}

}
