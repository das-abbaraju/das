package com.picsauditing.actions.report;

import java.text.DecimalFormat;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportInsuranceSupport {

	public ReportInsuranceApproval(AuditDataDAO auditDataDao,
			AuditQuestionDAO auditQuestionDao, OperatorAccountDAO operatorAccountDAO) {
		// sql = new SelectContractorAudit();
		super( auditDataDao, auditQuestionDao, operatorAccountDAO );
		this.report.setLimit(25);
		orderByDefault = "cao.status DESC, cao.updateDate ASC";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		getFilter().setShowVisible(false);
		sql.addWhere("a.active = 'Y'");
		
		sql.addField("cao.status as caoStatus");
		sql.addField("cao.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("cao.flag as caoRecommendedFlag");
		
	}

	
	public boolean isRequiresActivePolicy() {
		if(permissions.seesAllContractors())
			return true;
		for (DynaBean bean : data) {
			String status = bean.get("requiredAuditStatus").toString();
			if(status.equals(AuditStatus.Active.toString()))
				return true;
		}
		return false;
	}
	
	public String getFormattedDollarAmount( String answer )  {
		String response = "$0";
		
		try {
			String temp = answer.replaceAll(",", "");
			DecimalFormat decimalFormat = new DecimalFormat("$#,##0");
			
			Long input = new Long( temp );
			
			response = decimalFormat.format(input);
		}
		catch( Exception e ) {
			//System.out.println("unable to format as money: " + answer);
		}
		return response;
	}
	
}
