package com.picsauditing.actions.report;

import java.util.List;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class ReportCriteriaOperators extends ReportActionSupport implements Preparable {
	private FlagCriteriaOperatorDAO fcoDAO;
	private int criteriaID;
	
	public ReportCriteriaOperators(FlagCriteriaOperatorDAO fcoDAO) {
		this.fcoDAO = fcoDAO;
	}

	@Override
	public void prepare() throws Exception {
		return;
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		return SUCCESS;
	}
	
	public void buildQuery() {
		return;		
	}
	
	public List<FlagCriteriaOperator> getCriteriaOperators() {
		return fcoDAO.findByCriteriaID(criteriaID);
	}

	public int getCriteriaID() {
		return criteriaID;
	}

	public void setCriteriaID(int criteriaID) {
		this.criteriaID = criteriaID;
	}
}
