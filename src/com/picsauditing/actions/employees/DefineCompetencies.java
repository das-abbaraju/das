package com.picsauditing.actions.employees;

import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.operators.OperatorActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class DefineCompetencies extends OperatorActionSupport {
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected String role;

	protected Set<OperatorCompetency> competencies;
	
	public DefineCompetencies(OperatorAccountDAO operatorDao, OperatorCompetencyDAO operatorCompetencyDAO) {
		super(operatorDao);
		this.operatorCompetencyDAO = operatorCompetencyDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.DefineCompetencies);
		
				
		
		return SUCCESS;
	}
	
	public Set<OperatorCompetency> getCompetencies() {
		if (competencies == null)
			operatorCompetencyDAO.findByOperator(operator.getId());
		return competencies;
	}

	public void setCompetencies(Set<OperatorCompetency> competencies) {
		this.competencies = competencies;
	}
}
