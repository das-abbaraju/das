package com.picsauditing.mail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private AuditStatus caoStatus;
	protected Report report = new Report();
	protected List<BasicDynaBean> data;
	
	public InsuranceCertificateSubscription(Subscription subscription,
			SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.templateID = 61;
	}

	@Override
	protected void setup(Account a) {
		try {
			if (subscription.equals(Subscription.PendingInsuranceCerts)) {
				caoStatus = AuditStatus.Pending;
			}
			if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
				caoStatus = AuditStatus.Complete;
			}
			
			OperatorAccount o = (OperatorAccount) a;
			//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
			Set<Integer> operators = new HashSet<Integer>();
			if(!o.isCorporate())
				operators.add(o.getId());
			// Adding child facilities and switch tos
			for(OperatorAccount oa : o.getOperatorChildren())
				operators.add(oa.getId());
//			for (UserSwitch user : getUser().getSwitchTos())
//				if (user.getUser().getAccount().isOperator())
//					operators.add(user.getUser().getAccount().getId());
			
			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			sql.addJoin("JOIN contractor_info c ON a.id = c.id");
			sql.addJoin("JOIN users u ON a.contactID = u.id");
			sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
			sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = "+o.getInheritInsuranceCriteria().getId());
			sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
			sql.addJoin("JOIN users contact ON contact.id = a.contactID");
			sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
			sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID");
			sql.addJoin("JOIN accounts caoAccount ON cao.opID = caoAccount.id");
			sql.addJoin("LEFT JOIN pqfdata d ON d.auditID = ca.id");
			sql.addJoin("LEFT JOIN audit_question q ON q.id = d.questionID");
			
			sql.addWhere("a.type = 'Contractor'");
			sql.addWhere("gc.genID = "+o.getInheritInsuranceCriteria().getId());
			sql.addWhere("1 AND a.status IN ('Active') AND a.id IN (SELECT gc.subID FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID = "+o.getInheritInsuranceCriteria().getId()+")");
			sql.addWhere("cao.status IN ('"+caoStatus+"')");
			sql.addWhere("atype.classType = 'Policy'");
			sql.addWhere("cao.visible = 1");
			sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + Strings.implode(operators,",")
					+ "))");
			sql.addWhere("a.status IN ('Active')");
			// Get certificates
			sql.addWhere("q.columnHeader = 'Certificate'");
			sql.addWhere("q.questionType = 'FileCertificate'");
			sql.addWhere("q.number = 1");
			//sql.addWhere("cao."+((caoStatus.isPending()) ? "creationDate " : "statusChangedDate ") + "> '"+df.format(timePeriod.getComparisonDate())+"'");
			
			sql.addField("a.name AS conName");
			sql.addField("c.id AS conID");
			sql.addField("caoAccount.id AS opID");
			sql.addField("caoAccount.name AS opName");
			sql.addField("atype.auditName AS auditName");
			sql.addField("ca.expiresDate AS expiresDate");
			sql.addField("d.answer AS certID");
			sql.addField("ca.id AS auditID");
			sql.addField("u.name AS primaryContactName");
			sql.addField("u.email AS primaryContactEmail");
			
			sql.addOrderBy("caoAccount.name,a.name,auditID");

			report.setLimit(100000);
			report.setSql(sql);

			data = report.getPage();

			if (data.size() > 0) {
				tokens.put("data", data);
				tokens.put("caoStatus", caoStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
