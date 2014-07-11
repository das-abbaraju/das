package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.contractors.risk.RiskAssessment;
import com.picsauditing.actions.contractors.risk.SafetyAssessment;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator;
import com.picsauditing.actions.contractors.risk.ServiceRiskCalculator.RiskCategory;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.WizardSession;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorRiskAssessment extends ReportAccount {
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private EmailBuilder emailBuilder;
    @Autowired
    private BillingService billingService;
    @Autowired
    private ServiceRiskCalculator serviceRiskCalculator;

	protected int conID;
	protected String auditorNotes;
	protected Note note;
	protected ContractorAccount con;
	protected LowMedHigh manuallySetRisk;

	Map<RiskCategory, LowMedHigh> highestRisks = null;


    public ReportContractorRiskAssessment() {
		this.orderByDefault = "a.creationDate DESC, a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.RiskRank);
	}

	public void buildQuery() {
		super.buildQuery();

		String safetyRisk = getRiskSQL("Safety", "d.answer");

		sql.addJoin("JOIN (" + safetyRisk + ") r ON r.id = a.id");

		sql.addField("r.riskType");
		sql.addField("r.risk");
		sql.addField("r.answer");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String accept() throws Exception {
		recallWizardSessionFilter();

		if (con == null) {
			con = contractorAccountDAO.find(conID);
		}

		String noteMessage = updateSafetyRisk("Safety risk adjusted from ");

		Note note = new Note(con, getUser(), noteMessage + " - " + auditorNotes);
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		if (con.getAccountLevel().isListOnly() && !con.isListOnlyEligible()) {
			con.setAccountLevel(AccountLevel.Full);
		}

		con.setAuditColumns(permissions);
        billingService.syncBalance(con);
		contractorAccountDAO.save(con);

		auditorNotes = "";
		manuallySetRisk = LowMedHigh.None;

		return setUrlForRedirect("ReportContractorRiskLevel.action");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String reject() throws Exception {
		recallWizardSessionFilter();

		if (con == null) {
			con = contractorAccountDAO.find(conID);
		}

		LowMedHigh safetyRisk = getHighestRiskLevels().get(RiskCategory.SELF_SAFETY);

		String noteMessage = "Rejected safety adjustment from " + con.getSafetyRisk().toString() + " to "
				+ safetyRisk.toString();
		con.setSafetyRiskVerified(new Date());

		contractorAccountDAO.save(con);
		Note note = new Note(con, getUser(), noteMessage + (!Strings.isEmpty(auditorNotes) ? " - " + auditorNotes : ""));
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		auditorNotes = "";

		return setUrlForRedirect("ReportContractorRiskLevel.action");
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public String getAuditorNotes() {
		return auditorNotes;
	}

	public void setAuditorNotes(String auditorNotes) {
		this.auditorNotes = auditorNotes;
	}

	public LowMedHigh getManuallySetRisk() {
		return manuallySetRisk;
	}

	public void setManuallySetRisk(LowMedHigh manuallySetRisk) {
		this.manuallySetRisk = manuallySetRisk;
	}

	private void recallWizardSessionFilter() {
		// TODO: I have a feeling that this is not the way that Wizard Session
		// should be used. Need to find a better
		// way.
		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		setFilter(wizardSession.getContractorFilter());
	}

	private String getRiskSQL(String type, String answer) {
		List<Integer> questionIDs = getQuestionIDsFor(type);

		String questionString = String.format("IN (%s)", Strings.implode(questionIDs));

		SelectAccount sql2 = new SelectAccount();
		sql2.setType(Type.Contractor);
		sql2.addJoin("JOIN contractor_audit ca ON ca.conID = a.id AND ca.auditTypeID = 1");
		sql2.addJoin("JOIN pqfdata d ON d.auditID = ca.id"
				+ (!Strings.isEmpty(questionString) ? " AND d.questionID " + questionString : ""));

		String where = String.format("(d.answer = 'Low' AND c.%1$sRisk != 1) OR "
				+ "(d.answer = 'Medium' AND c.%1$sRisk != 2) OR (c.%1$sRisk = 0)", type.toLowerCase());

		sql2.addWhere("a.onsiteServices = 1 OR a.offsiteServices = 1");

		sql2.addField("'" + type + "' riskType");
		sql2.addField("c." + type.toLowerCase() + "Risk risk");

		if (!Strings.isEmpty(answer)) {
			sql2.addField(answer);
		} else {
			sql2.addField("'' answer");
		}

		sql2.addWhere("c.accountLevel = 'Full'");
		sql2.addWhere("a.status = 'Active'");
		sql2.addWhere(String.format("c.%1$sRiskVerified IS NULL", type.toLowerCase()));
		sql2.addWhere(where);

		return sql2.toString();
	}

	private List<Integer> getQuestionIDsFor(String type) {
		List<Integer> questionIDs = new ArrayList<Integer>();

		for (RiskAssessment safetyAssessment : SafetyAssessment.values()) {
			if (safetyAssessment.isSelfEvaluation()) {
				questionIDs.add(safetyAssessment.getQuestionID());
			}
		}

		return questionIDs;
	}

	private Map<RiskCategory, LowMedHigh> getHighestRiskLevels() {
		if (highestRisks == null) {

			for (ContractorAudit contractorAudit : con.getAudits()) {
				if (contractorAudit.getAuditType().isPicsPqf()) {
					highestRisks = serviceRiskCalculator.getHighestRiskLevelMap(contractorAudit.getData());
				}
			}
		}

		return highestRisks;
	}

	private String updateSafetyRisk(String noteMessage) {
		LowMedHigh newSafetyRisk = getHighestRiskLevels().get(RiskCategory.SELF_SAFETY);
		LowMedHigh currentSafetyRisk = con.getSafetyRisk();

		noteMessage += currentSafetyRisk.toString() + " to " + newSafetyRisk.toString();

		if (newSafetyRisk.ordinal() < currentSafetyRisk.ordinal()) {
			buildAndSendBillingRiskDowngradeEmail(currentSafetyRisk, newSafetyRisk);
		}

		con.setSafetyRisk(newSafetyRisk);
		con.setSafetyRiskVerified(new Date());
		return noteMessage;
	}

	private void buildAndSendBillingRiskDowngradeEmail(LowMedHigh currentRisk, LowMedHigh newRisk) {
		emailBuilder.setTemplate(EmailTemplate.RISK_LEVEL_DOWNGRADED_EMAIL_TEMPLATE);
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
		emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(con.getCurrency()));
		emailBuilder.addToken("contractor", con);
		emailBuilder.addToken("currentSafetyRisk", currentRisk);
		emailBuilder.addToken("newSafetyRisk", newRisk);

		EmailQueue emailQueue;
		try {
			emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setSubjectViewableById(Account.PICS_ID);
			emailQueue.setBodyViewableById(Account.PICS_ID);
			emailSender.send(emailQueue);
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(this.getClass());
			logger.error("Cannot send email to  " + con.getName() + " (" + con.getId() + ")");
		}
	}
}
