package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.RulesRowDAO;
import com.picsauditing.beans.RulesRowBean;

public class RulesAction extends ActionSupport {
	protected RulesRowBean bean = null;
	protected RulesRowDAO dao = null;

	public RulesAction(RulesRowDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {
		if (bean == null) {
			bean = new RulesRowBean();

			bean.setTableName("struts");
		} else {
			dao.save(bean);
		}

		return SUCCESS;
	}

	public RulesRowBean getRulesRowBean() {
		return bean;
	}

	public void setTestBean(RulesRowBean bean) {
		this.bean = bean;
	}
}
