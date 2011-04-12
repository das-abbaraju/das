package com.picsauditing.mail;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private AuditStatus caoStatus;
	protected Report report = new Report();
	protected List<BasicDynaBean> data;

	public InsuranceCertificateSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
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
			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			sql.addJoin("JOIN users u ON u.id = a.contactID");
			sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
			sql.addJoin("JOIN audit_type atype on atype.id = ca.auditTypeID AND atype.classType = 'Policy'");
			sql.addJoin("JOIN pqfdata d ON d.auditID = ca.id");
			sql.addJoin("JOIN audit_question q ON q.id = d.questionID " + "AND q.columnHeader = 'Certificate' "
					+ "AND q.questionType = 'FileCertificate' " + "AND q.number = 1");
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id"
					+ (o.isOperator() ? " AND gc.genID = " + o.getInheritInsuranceCriteria().getId() : ""));
			if (o.isCorporate())
				sql.addJoin("JOIN facilities f ON f.corporateID = " + o.getId() + " AND f.opID = gc.genID");
			sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1 AND cao.status = '"
					+ caoStatus + "'");
			sql.addJoin("JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id AND caop.opID = "
					+ o.getId());
			sql.addJoin("JOIN accounts o ON o.id = gc.genID");
			sql.addJoin("JOIN audit_category_rule acr ON acr.opID = gc.genID AND acr.catID = q.categoryID");
			sql.addField("a.id AS conID");
			sql.addField("a.name AS conName");
			sql.addField("o.id AS opID");
			sql.addField("o.name AS opName");
			sql.addField("CONCAT('AuditType.',aType.id,'.name') `atype.name`");
			sql.addField("ca.expiresDate AS expiresDate");
			sql.addField("d.answer AS certID");
			sql.addField("ca.id AS auditID");
			sql.addField("u.name AS primaryContactName");
			sql.addField("u.email AS primaryContactEmail");
			sql.addWhere("a.status = 'Active'");
			sql.addWhere("a.type = 'Contractor'");
			sql.addWhere("o.status = 'Active'");
			sql.addWhere("d.answer > ''");

			sql.addOrderBy("o.name,a.name,auditID");

			report.setLimit(100000);
			report.setSql(sql);

			data = report.getPage();

			if (data.size() > 0) {
				tokens.put("data", data);
				tokens.put("caoStatus", caoStatus);
				tokens.put("i18nCache", i18nCache);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
