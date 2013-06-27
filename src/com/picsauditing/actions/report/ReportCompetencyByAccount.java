package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByAccount extends ReportEmployee {
	@Autowired
	protected AuditBuilder auditBuilder;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected FacilityChanger facilityChanger;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private EmailSender emailSender;

	protected OperatorAccount operator;
	protected ContractorAccount contractor;

	public ReportCompetencyByAccount() {
		orderByDefault = "name";
	}

	@RequiredPermission(value = OpPerms.AddContractors)
	public String add() throws Exception {
		try {
			facilityChanger.setPermissions(permissions);
			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator);
			facilityChanger.add();
			addActionMessage(getText("ReportCompetencyByAccount.message.SuccessfullyAddedContractor", new Object[] {
					(Integer) contractor.getId(), contractor.getName() }));

			if (contractor.getAccountLevel().isBidOnly() && !operator.isAcceptsBids()) {
				contractor.setAccountLevel(AccountLevel.Full);
				contractor.setRenew(true);

				auditBuilder.buildAudits(contractor);

				for (ContractorAudit cAudit : contractor.getAudits()) {
					if (cAudit.getAuditType().isPicsPqf()) {
						for (ContractorAuditOperator cao : cAudit.getOperators()) {
							if (cao.getStatus().after(AuditStatus.Pending)) {
								cao.changeStatus(AuditStatus.Pending, permissions);
								auditDataDAO.save(cao);
							}
						}

						auditBuilder.recalculateCategories(cAudit);
						auditPercentCalculator.recalcAllAuditCatDatas(cAudit);
						auditPercentCalculator.percentCalculateComplete(cAudit);
						auditDataDAO.save(cAudit);
					}
				}

				contractor.incrementRecalculation();
				contractor.setAuditColumns(permissions);
				contractorAccountDAO.save(contractor);

				// Sending a Email to the contractor for upgrade
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(73); // Trial Contractor
				// Account Approval
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
				emailBuilder.addToken("permissions", permissions);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setHighPriority();
				emailQueue.setFromAddress(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
				emailQueue.setSubjectViewableById(Account.PicsID);
				emailQueue.setBodyViewableById(Account.PicsID);
				emailSender.send(emailQueue);
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
		}

		return setUrlForRedirect("ReportCompetencyByAccount.action");
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN job_role jr ON jr.accountID = a.id AND jr.active = 1");
		sql.addJoin(buildAuditJoin(AuditType.HSE_COMPETENCY));
		sql.addJoin(buildAuditJoin(AuditType.SHELL_COMPETENCY_REVIEW));

		if (permissions.isOperator()) {
			String accountStatus = "'Active'";
			if (permissions.getAccountStatus().isDemo())
				accountStatus += ", 'Demo'";

			if (operator == null)
				operator = operatorAccountDAO.find(permissions.getAccountId());

			Set<Integer> opIDs = new HashSet<Integer>();
			opIDs.add(operator.getId());

			if (operator.getParent() != null) {
				for (Facility f : operator.getParent().getOperatorFacilities()) {
					opIDs.add(f.getOperator().getId());
				}
			}

			sql.addJoin(String.format("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID IN (%s)", Strings
					.implode(opIDs)));
			sql.addJoin(String.format(
					"LEFT JOIN (SELECT subID FROM generalcontractors WHERE genID = %d) gcw ON gcw.subID = a.id",
					permissions.getAccountId()));
			sql.addField("CASE ISNULL(gcw.subID) WHEN 0 THEN 1 ELSE 0 END worksFor");
		}

		sql.addField("COUNT(distinct e.id) eCount");
		sql.addField("COUNT(distinct jr.id) jCount");
		sql.addField(buildAuditField(AuditType.HSE_COMPETENCY));
		sql.addField(buildAuditField(AuditType.SHELL_COMPETENCY_REVIEW));

		sql.addWhere("a.requiresCompetencyReview = 1");
		if (permissions.isCorporate()) {
			PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
			sql.addWhere("1 " + permQuery.toString());
		}

		sql.addGroupBy("a.id");

		getFilter().setShowFirstName(false);
		getFilter().setShowLastName(false);
		getFilter().setShowEmail(false);
		getFilter().setShowSsn(false);
		getFilter().setShowOperators(true);
	}

	@Override
	protected void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("name", getText("global.Company")));
		excelSheet.addColumn(new ExcelColumn("eCount", getText("ReportCompetencyByAccount.label.NumberOfEmployees")));
		excelSheet.addColumn(new ExcelColumn("jCount", getText("ReportCompetencyByAccount.label.NumberOfJobRoles")));
		excelSheet.addColumn(new ExcelColumn("ca99status", getText("AuditType.99.name")));
		excelSheet.addColumn(new ExcelColumn("ca100status", getText("AuditType.100.name")));
	}

	private String buildAuditJoin(int auditTypeID) {
		Set<Integer> opIDs = new HashSet<Integer>();
		opIDs.add(permissions.getAccountId());
		if (permissions.isOperator()) {
			if (operator == null)
				operator = operatorAccountDAO.find(permissions.getAccountId());

			if (operator.getParent() != null) {
				for (Facility f : operator.getParent().getOperatorFacilities()) {
					opIDs.add(f.getOperator().getId());
				}
			}
		}

		SelectSQL sql2 = new SelectSQL("contractor_audit ca");
		String dateFormat = "DATE_FORMAT(cao.statusChangedDate, '%c/%e/%Y')";
		// Joins
		sql2.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id AND cao.visible = 1");
		// TODO Assuming here that the CAO operator is always PICS Global
		sql2.addJoin(String.format("JOIN contractor_audit_operator_permission caop "
				+ "ON caop.caoID = cao.id AND caop.opID IN (%s)", Strings.implode(opIDs)));
		// Fields
		sql2.addField("ca.id");
		sql2.addField("ca.conID");
		sql2.addField(String.format(
				"CASE cao.status WHEN 'Pending' THEN NULL ELSE CONCAT(cao.status, ' on ', %s) END status", dateFormat));
		sql2.addField(String.format("%s changedDate", dateFormat));
		// Wheres
		sql2.addWhere(String.format("ca.auditTypeID = %d", auditTypeID));
		
		sql2.addGroupBy("ca.conID");

		return String.format("LEFT JOIN (%1$s) ca%2$d ON ca%2$d.conID = a.id", sql2.toString(), auditTypeID);
	}

	private String buildAuditField(int auditTypeID) {
		return String.format("ca%1$d.id ca%1$dID, ca%1$d.status ca%1$dstatus, ca%1$d.changedDate ca%1$ddate",
				auditTypeID);
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}
