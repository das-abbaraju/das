package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.Facilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;


public class AuditOperatorList extends PicsActionSupport {
	private static final long serialVersionUID = -618269908092576272L;
	protected int operatorId;
	protected List<AuditOperator> data;
	protected List<BasicDynaBean> operators;
	protected List<AuditType> auditTypes;
	protected int auditId;
	
	protected Facilities facilityUtility = null;
	public AuditOperatorList( Facilities facilityUtility )
	{
		this.facilityUtility = facilityUtility;
	}
	
	public String execute() throws Exception {
		if (!getPermissions(OpPerms.ManageOperators, OpType.View))
			return LOGIN;
		
		operators = facilityUtility.listAll("a.type = 'Operator'");
		
		AuditOperatorDAO dao = new AuditOperatorDAO();
		if (auditId > 0)
			data = dao.findByAudit(auditId);
		if (operatorId > 0)
			data = dao.findByOperator(operatorId);
		
		AuditTypeDAO auditDao = new AuditTypeDAO();
		//auditTypes = auditDao.findAll();
		auditTypes = new ArrayList<AuditType>();
		
		return SUCCESS;
	}

	public int getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}

	public int getAuditId() {
		return auditId;
	}

	public void setAuditId(int auditId) {
		this.auditId = auditId;
	}

	public List<AuditOperator> getList() {
		return data;
	}

	public void setList(List<AuditOperator> data) {
		this.data = data;
	}
	
	public List<BasicDynaBean> getOperators() {
		return operators;
	}

	public void setOperators(List<BasicDynaBean> operators) {
		this.operators = operators;
	}
}
