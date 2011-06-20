package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorRiskAssessment extends ReportAccount {
	protected int conID;
	protected String auditorNotes;
	protected Note note;
	protected String type;
	protected ContractorAccount con;

	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	protected NoteDAO noteDAO;

	public ReportContractorRiskAssessment() {
		this.orderByDefault = "a.creationDate DESC, a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.RiskRank);
	}

	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		String safetyRisk = getRiskSQL("Safety", "d.answer", AuditQuestion.RISK_LEVEL_ASSESSMENT);
		String productRisk = getRiskSQL("Product",
				"GROUP_CONCAT(CONCAT(CASE d.questionID WHEN 7678 THEN 'Business Interruption' "
						+ "ELSE 'Product Safety' END, ': ', d.answer) SEPARATOR '<br />') answer", new int[] {
						AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT, AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT });

		sql.addJoin("JOIN (" + safetyRisk + "\nUNION\n" + productRisk + ") r ON r.id = a.id");

		sql.addField("r.riskType");
		sql.addField("r.risk");
		sql.addField("r.answer");
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String accept() throws Exception {
		String noteMessage = type + " risk adjusted from ";

		if (type.equals("Safety")) {
			LowMedHigh safetyRisk = getContractorAnswer(AuditQuestion.RISK_LEVEL_ASSESSMENT);

			noteMessage += con.getSafetyRisk().toString() + " to " + safetyRisk.toString();

			// How can this happen?
			if (safetyRisk.ordinal() > con.getSafetyRisk().ordinal())
				con.setLastUpgradeDate(new Date());

			con.setSafetyRisk(safetyRisk);
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
		}

		Note note = new Note(con, getUser(), noteMessage);
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		con.setAuditColumns(permissions);
		contractorAccountDAO.save(con);

		auditorNotes = "";
		return super.execute();
	}

	@RequiredPermission(value = OpPerms.RiskRank)
	public String reject() throws Exception {
		String noteMessage = "Rejected " + type.toLowerCase() + " adjustment from ";

		if (type.equals("Safety")) {
			LowMedHigh safetyRisk = getContractorAnswer(AuditQuestion.RISK_LEVEL_ASSESSMENT);

			noteMessage += con.getSafetyRisk().toString() + " to " + safetyRisk.toString();
		} else {
			LowMedHigh businessRisk = getContractorAnswer(AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT);
			LowMedHigh productRisk = getContractorAnswer(AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT);
			// Get highest
			if (productRisk.ordinal() < businessRisk.ordinal())
				productRisk = businessRisk;

			noteMessage += con.getProductRisk().toString() + " to " + productRisk.toString();
		}

		Note note = new Note(con, getUser(), noteMessage);
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

		SelectSQL sql2 = new SelectSQL("accounts a");
		sql2.addJoin("JOIN contractor_info c ON c.id = a.id");
		sql2.addJoin("JOIN contractor_audit ca ON ca.conID = a.id AND ca.auditTypeID = 1");
		sql2.addJoin("JOIN pqfdata d ON d.auditID = ca.id AND d.questionID " + questionString
				+ " AND d.dateVerified IS NULL");

		sql2.addField("a.id");
		sql2.addField("'" + type + "' riskType");
		sql2.addField("c." + type.toLowerCase() + "Risk risk");
		sql2.addField(answer);
		
		String checkMaterialSupplier = "";
		if (type.equals("Product")) {
			checkMaterialSupplier = " AND a.materialSupplier = 1";
			sql2.addGroupBy("a.id");
		}

		sql2.addWhere("(d.answer = 'Low' AND c." + type.toLowerCase() + "Risk > 1" + checkMaterialSupplier
				+ ") OR (d.answer = 'Medium' AND c." + type.toLowerCase() + "Risk > 2" + checkMaterialSupplier + ")");

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

	public String getAuditorNotes() {
		return auditorNotes;
	}

	public void setAuditorNotes(String auditorNotes) {
		this.auditorNotes = auditorNotes;
	}
}
