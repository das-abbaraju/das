package com.picsauditing.actions.report;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportInsuranceSupport {

	public ReportInsuranceApproval(AuditDataDAO auditDataDao,
			AuditQuestionDAO auditQuestionDao, OperatorAccountDAO operatorAccountDAO) {
		// sql = new SelectContractorAudit();
		super( auditDataDao, auditQuestionDao, operatorAccountDAO );
		this.report.setLimit(25);
		orderByDefault = "ca.auditStatus ASC, ca.completedDate DESC";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();
		//sql.addField("a_op.requiredAuditStatus");
		sql.addField("ca.expiresDate");
		sql.addField("cao.status as caoStatus");
		sql.addField("cao.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("cao.recommendedStatus as caoRecommendedStatus");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		//sql.addJoin("JOIN audit_operator a_op on a_op.auditTypeID = atype.id AND a_op.opID = cao.opID");
	
		//sql.addWhere("a.active = 'Y'");

		sql.addWhere("cao.opid = " + permissions.getAccountId());
		
		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowHasClosedDate(true);
		getFilter().setShowExpiredDate(false);
		getFilter().setShowPercentComplete(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowCaoStatus(true);
		getFilter().setShowAuditStatus(false);
		getFilter().setShowRecommendedStatus(true);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowAuditFor(false);
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
