package com.picsauditing.actions.flags;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.FlagCriteria;

public class ManageFlagCriteria extends PicsActionSupport implements Preparable {

	private FlagCriteriaDAO criteriaDAO;

	private FlagCriteria criteria;

	public ManageFlagCriteria(FlagCriteriaDAO criteriaDAO) {
		this.criteriaDAO = criteriaDAO;
	}

	@Override
	public void prepare() throws Exception {
		int criteriaID = getParameter("criteria.id");
		if (criteriaID > 0)
			criteria = criteriaDAO.find(criteriaID);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if ("save".equalsIgnoreCase(button)) {

		}

		return SUCCESS;
	}

	public List<FlagCriteria> getCriteriaList() {
		return criteriaDAO.findAll();
	}

	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

}
