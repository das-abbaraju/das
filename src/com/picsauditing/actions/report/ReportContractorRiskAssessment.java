package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectAccount.Type;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ReportContractorRiskAssessment extends ReportAccount {
	protected int conID;
	protected String auditorNotes;
	protected Note note;
	protected String type = "All";
	protected ContractorAccount con;

	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	private EmailSenderSpring emailSender;

	public ReportContractorRiskAssessment() {
		this.orderByDefault = "a.creationDate DESC, a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.RiskRank);
	}

	public void buildQuery() {
		super.buildQuery();

		if ("Safety".equals(type)) {
			sql.addJoin("JOIN (" + getRiskSQL("Safety", "d.answer", AuditQuestion.RISK_LEVEL_ASSESSMENT)
					+ ") r ON r.id = a.id");
		} else if ("Product".equals(type)) {
			sql.addJoin("JOIN ("
					+ getRiskSQL("Product", "GROUP_CONCAT(CONCAT(CASE d.questionID "
							+ "WHEN 7678 THEN 'Business Interruption: ' ELSE 'Product Safety: ' END, "
							+ "d.answer) SEPARATOR '<br />') answer",
							new int[] { AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT,
									AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT }) + ") r ON r.id = a.id");
		} else {
			String safetyRisk = getRiskSQL("Safety", "d.answer", AuditQuestion.RISK_LEVEL_ASSESSMENT);
			String productRisk = getRiskSQL("Product", "GROUP_CONCAT(CONCAT(CASE d.questionID "
					+ "WHEN 7678 THEN 'Business Interruption: ' ELSE 'Product Safety: ' END, "
					+ "d.answer) SEPARATOR '<br />') answer", new int[] { AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT,
					AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT });

			sql.addJoin("JOIN (" + safetyRisk + "\nUNION\n" + productRisk + ") r ON r.id = a.id");
		}

		sql.addField("r.riskType");
		sql.addField("r.risk");
		sql.addField("r.answer");
		sql.addField("r.lastVerifiedDate");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String accept() throws Exception {
		if (!Strings.isEmpty(type)) {
			String noteMessage = type + " risk adjusted from ";

			if ("Safety".equals(type)) {
				LowMedHigh newSafetyRisk = getContractorAnswer(AuditQuestion.RISK_LEVEL_ASSESSMENT);
				LowMedHigh currentSafetyRisk = con.getSafetyRisk();

				noteMessage += currentSafetyRisk.toString() + " to " + newSafetyRisk.toString();

				// How can this happen?
				if (newSafetyRisk.ordinal() > currentSafetyRisk.ordinal())
					con.setLastUpgradeDate(new Date());
				else if (newSafetyRisk.ordinal() < currentSafetyRisk.ordinal()) {
					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setTemplate(159);
					emailBuilder.setFromAddress("\"PICS IT Team\"<it@picsauditing.com>");
					emailBuilder.setToAddresses("billing@picsauditing.com");
					emailBuilder.addToken("contractor", con);
					emailBuilder.addToken("currentSafetyRisk", currentSafetyRisk);
					emailBuilder.addToken("newSafetyRisk", newSafetyRisk);

					EmailQueue emailQueue;
					try {
						emailQueue = emailBuilder.build();
						emailQueue.setPriority(60);
						emailQueue.setViewableById(Account.PicsID);
						emailSender.send(emailQueue);
					} catch (Exception e) {
						PicsLogger.log("Cannot send email to  " + con.getName() + " (" + con.getId() + ")");
					}

				}
				con.setSafetyRisk(newSafetyRisk);
				if (con.getAccountLevel().isListOnly() && !con.isListOnlyEligible())
					con.setAccountLevel(AccountLevel.Full);
				con.setSafetyRiskVerified(new Date());
			} else {
				LowMedHigh businessRisk = getContractorAnswer(AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT);
				LowMedHigh productRisk = getContractorAnswer(AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT);
				// Get highest
				if (productRisk.ordinal() < businessRisk.ordinal())
					productRisk = businessRisk;

				noteMessage += con.getProductRisk().toString() + " to " + productRisk.toString();

				// How can this happen?
				if (productRisk.ordinal() > con.getProductRisk().ordinal())
					con.setLastUpgradeDate(new Date());

				con.setProductRisk(productRisk);
				if (con.getAccountLevel().isListOnly() && !con.isListOnlyEligible())
					con.setAccountLevel(AccountLevel.Full);
				con.setProductRiskVerified(new Date());
			}

			Note note = new Note(con, getUser(), noteMessage + " - " + auditorNotes);
			note.setNoteCategory(NoteCategory.RiskRanking);
			noteDAO.save(note);

			con.setAuditColumns(permissions);
			con.syncBalance();
			contractorAccountDAO.save(con);

			auditorNotes = "";
		} else {
			addActionError("Missing Risk Assessment Type");
		}

		return super.execute();
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String reject() throws Exception {
		String noteMessage = "Rejected " + type.toLowerCase() + " adjustment from ";

		if (type.equals("Safety")) {
			LowMedHigh safetyRisk = getContractorAnswer(AuditQuestion.RISK_LEVEL_ASSESSMENT);

			noteMessage += con.getSafetyRisk().toString() + " to " + safetyRisk.toString();
			con.setSafetyRiskVerified(new Date());
		} else {
			LowMedHigh businessRisk = getContractorAnswer(AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT);
			LowMedHigh productRisk = getContractorAnswer(AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT);
			// Get highest
			if (productRisk.ordinal() < businessRisk.ordinal())
				productRisk = businessRisk;

			noteMessage += con.getProductRisk().toString() + " to " + productRisk.toString();
			con.setProductRiskVerified(new Date());
		}

		contractorAccountDAO.save(con);
		Note note = new Note(con, getUser(), noteMessage + (!Strings.isEmpty(auditorNotes) ? " - " + auditorNotes : ""));
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		auditorNotes = "";
		return super.execute();
	}

	private String getRiskSQL(String type, String answer, int... questionIDs) {
		String questionString = "";

		if (questionIDs.length == 1)
			questionString = "= " + questionIDs[0];
		else
			questionString = String.format("IN (%s)", Strings.implode(questionIDs));

		SelectAccount sql2 = new SelectAccount();
		sql2.setType(Type.Contractor);
		sql2.addJoin("JOIN contractor_audit ca ON ca.conID = a.id AND ca.auditTypeID = 1");
		sql2.addJoin("JOIN pqfdata d ON d.auditID = ca.id AND d.questionID " + questionString);

		String where = String
				.format("(d.answer = 'Low' AND c.%1$sRisk > 1) OR (d.answer = 'Medium' AND c.%1$sRisk > 2)",
						type.toLowerCase());

		if (type.equals("Product")) {
			sql2.addWhere("a.materialSupplier = 1");
			sql2.addGroupBy("a.id");
		} else {
			sql2.addWhere("a.onsiteServices = 1 OR a.offsiteServices = 1");
		}

		sql2.addField("'" + type + "' riskType");
		sql2.addField("c." + type.toLowerCase() + "Risk risk");
		sql2.addField(answer);
		sql2.addField("c." + type.toLowerCase() + "RiskVerified lastVerifiedDate");

		sql2.addWhere("a.status = 'Active'");
		sql2.addWhere(String.format("c.%1$sRiskVerified IS NULL "
				+ "OR DATE_ADD(c.%1$sRiskVerified, INTERVAL 3 YEAR) < NOW()", type.toLowerCase()));
		sql2.addWhere(where);

		return sql2.toString();
	}

	private LowMedHigh getContractorAnswer(int questionID) {
		if (con == null)
			con = contractorAccountDAO.find(conID);

		if (Strings.isEmpty(auditorNotes))
			auditorNotes = null;

		for (ContractorAudit audit : con.getAudits()) {
			if (audit.getAuditType().isPqf()) {
				for (AuditData d : audit.getData()) {
					if (d.getQuestion().getId() == questionID) {
						// Save audit data
						d.setDateVerified(new Date());
						d.setComment(auditorNotes);
						d.setAuditColumns(permissions);
						auditDataDAO.save(d);

						String answer = d.getAnswer();
						if (answer.equals("Medium"))
							answer = "Med";

						return LowMedHigh.valueOf(answer);
					}
				}
			}
		}

		return null;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAuditorNotes() {
		return auditorNotes;
	}

	public void setAuditorNotes(String auditorNotes) {
		this.auditorNotes = auditorNotes;
	}
}
