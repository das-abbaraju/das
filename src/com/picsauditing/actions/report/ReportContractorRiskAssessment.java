package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.contractors.ProductAssessment;
import com.picsauditing.actions.contractors.SafetyAssessment;
import com.picsauditing.actions.contractors.ServiceRiskCalculator;
import com.picsauditing.actions.contractors.ServiceRiskCalculator.RiskCategory;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
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

	public static String SAFETY = "Safety";
	public static String PRODUCT = "Product";
	public static String TRANSPORTATION = "Transportation";

	protected int conID;
	protected String auditorNotes;
	protected Note note;
	protected String type;
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

		String safetyRisk = getRiskSQL(SAFETY, "d.answer");
		String productRisk = getRiskSQL(PRODUCT, "GROUP_CONCAT(CONCAT(CASE d.questionID "
				+ "WHEN 7678 THEN 'Business Interruption: ' ELSE 'Product Safety: ' END, "
				+ "d.answer) SEPARATOR '<br />') answer");

		if (SAFETY.equals(getFilter().getRiskType())) {
			sql.addJoin("JOIN (" + safetyRisk + ") r ON r.id = a.id");
		} else if (PRODUCT.equals(getFilter().getRiskType())) {
			sql.addJoin("JOIN (" + productRisk + ") r ON r.id = a.id");
		} else {
			sql.addJoin("JOIN (" + safetyRisk + "\nUNION\n" + productRisk + ") r ON r.id = a.id");
		}

		sql.addField("r.riskType");
		sql.addField("r.risk");
		sql.addField("r.answer");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String accept() throws Exception {
		recallWizardSessionFilter();
		if (!Strings.isEmpty(type)) {
			if (con == null) {
				con = contractorAccountDAO.find(conID);
			}

			String noteMessage = type + " risk adjusted from ";

			if (SAFETY.equals(type)) {
				noteMessage = updateSafetyRisk(noteMessage);
			} else if (PRODUCT.equals(type)) {
				noteMessage = updateProductRisk(noteMessage);
			} else if (TRANSPORTATION.equals(type)) {
				noteMessage = updateTransportationRisk(noteMessage);
			}

			Note note = new Note(con, getUser(), noteMessage + " - " + auditorNotes);
			note.setNoteCategory(NoteCategory.RiskRanking);
			noteDAO.save(note);

			if (con.getAccountLevel().isListOnly() && !con.isListOnlyEligible()) {
				con.setAccountLevel(AccountLevel.Full);
			}

			con.setAuditColumns(permissions);
			con.syncBalance();
			contractorAccountDAO.save(con);

			auditorNotes = "";
			manuallySetRisk = LowMedHigh.None;
		} else {
			addActionError("Missing Risk Assessment Type");
		}

		return setUrlForRedirect("ReportContractorRiskLevel.action");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String reject() throws Exception {
		recallWizardSessionFilter();
		if (!Strings.isEmpty(type)) {
			if (con == null) {
				con = contractorAccountDAO.find(conID);
			}

			String noteMessage = "Rejected " + type.toLowerCase() + " adjustment from ";

			if (type.equals(SAFETY)) {
				LowMedHigh safetyRisk = getHighestRiskLevels().get(RiskCategory.SELF_SAFETY);

				noteMessage += con.getSafetyRisk().toString() + " to " + safetyRisk.toString();
				con.setSafetyRiskVerified(new Date());
			} else {
				LowMedHigh productRisk = getHighestRiskLevels().get(RiskCategory.SELF_PRODUCT);

				noteMessage += con.getProductRisk().toString() + " to " + productRisk.toString();
				con.setProductRiskVerified(new Date());
			}

			contractorAccountDAO.save(con);
			Note note = new Note(con, getUser(), noteMessage
					+ (!Strings.isEmpty(auditorNotes) ? " - " + auditorNotes : ""));
			note.setNoteCategory(NoteCategory.RiskRanking);
			noteDAO.save(note);

			auditorNotes = "";
		} else {
			addActionError("Missing Risk Assessment Type");
		}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LowMedHigh getManuallySetRisk() {
		return manuallySetRisk;
	}

	public void setManuallySetRisk(LowMedHigh manuallySetRisk) {
		this.manuallySetRisk = manuallySetRisk;
	}

	public List<String> getRiskList() {
		List<String> risks = new ArrayList<String>();
		risks.add(getText("JS.Filters.status.All"));
		risks.add(SAFETY);
		risks.add(PRODUCT);

		return risks;
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

		if (PRODUCT.equals(type)) {
			sql2.addWhere("a.materialSupplier = 1");
		} else if (TRANSPORTATION.equals(type)) {
			sql2.addWhere("a.transportationServices = 1");
		} else {
			sql2.addWhere("a.onsiteServices = 1 OR a.offsiteServices = 1");
		}

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

		if (SAFETY.equals(type)) {
			for (SafetyAssessment safetyAssessment : SafetyAssessment.values()) {
				if (safetyAssessment.isSelfEvaluation()) {
					questionIDs.add(safetyAssessment.getQuestionID());
				}
			}
		} else if (PRODUCT.equals(type)) {
			for (ProductAssessment productAssessment : ProductAssessment.values()) {
				if (productAssessment.isSelfEvaluation()) {
					questionIDs.add(productAssessment.getQuestionID());
				}
			}
		}

		return questionIDs;
	}
	
	private Map<RiskCategory, LowMedHigh> getHighestRiskLevels() {
		if (highestRisks == null) {
			ServiceRiskCalculator serviceRiskCalculator = new ServiceRiskCalculator();
			
			for (ContractorAudit contractorAudit : con.getAudits()) {
				if (contractorAudit.getAuditType().isPqf()) {
					highestRisks = serviceRiskCalculator.getHighestRiskLevel(contractorAudit.getData());
				}
			}
		}
		
		return highestRisks;
	}

	private String updateSafetyRisk(String noteMessage) {
		LowMedHigh newSafetyRisk = getHighestRiskLevels().get(RiskCategory.SELF_SAFETY);
		LowMedHigh currentSafetyRisk = con.getSafetyRisk();

		noteMessage += currentSafetyRisk.toString() + " to " + newSafetyRisk.toString();

		// How can this happen?
		if (newSafetyRisk.ordinal() > currentSafetyRisk.ordinal()) {
			con.setLastUpgradeDate(new Date());
		} else if (newSafetyRisk.ordinal() < currentSafetyRisk.ordinal()) {
			buildAndSendBillingRiskDowngradeEmail(currentSafetyRisk, newSafetyRisk);
		}

		con.setSafetyRisk(newSafetyRisk);
		con.setSafetyRiskVerified(new Date());
		return noteMessage;
	}

	private String updateProductRisk(String noteMessage) {
		LowMedHigh productRisk = getHighestRiskLevels().get(RiskCategory.SELF_PRODUCT);
		noteMessage += con.getProductRisk().toString() + " to " + productRisk.toString();

		// How can this happen?
		if (productRisk.ordinal() > con.getProductRisk().ordinal())
			con.setLastUpgradeDate(new Date());

		con.setProductRisk(productRisk);
		con.setProductRiskVerified(new Date());

		return noteMessage;
	}

	private String updateTransportationRisk(String noteMessage) {
		LowMedHigh currentTransportationRisk = con.getTransportationRisk();

		noteMessage += currentTransportationRisk.name() + " to " + manuallySetRisk.name();

		con.setTransportationRisk(manuallySetRisk);
		con.setTransportationRiskVerified(new Date());

		return noteMessage;
	}

	private void buildAndSendBillingRiskDowngradeEmail(LowMedHigh currentRisk, LowMedHigh newRisk) {
		emailBuilder.setTemplate(159);
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_IT_TEAM_EMAIL);
		emailBuilder.setToAddresses(EmailAddressUtils.getBillingEmail(con.getCurrency()));
		emailBuilder.addToken("contractor", con);
		emailBuilder.addToken("currentSafetyRisk", currentRisk);
		emailBuilder.addToken("newSafetyRisk", newRisk);

		EmailQueue emailQueue;
		try {
			emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setViewableById(Account.PicsID);
			emailSender.send(emailQueue);
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(this.getClass());
			logger.error("Cannot send email to  " + con.getName() + " (" + con.getId() + ")");
		}
	}
}
