package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorRiskAssessment extends ReportAccount {
	protected int conID;
	protected int safetyID;
	protected int productSafetyID;
	protected int productID;
	protected String auditorNotes;
	protected Note note;

	private ContractorAccount con;
	private LowMedHigh safetyRisk;
	private LowMedHigh productSafetyRisk;
	private LowMedHigh productRisk;
	private String adjustments;

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

		sql.addField("a.onsiteServices");
		sql.addField("a.offsiteServices");
		sql.addField("a.materialSupplier");

		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("LEFT JOIN pqfdata safety ON safety.auditID = ca.id AND safety.questionID = 2444 AND safety.dateVerified IS NULL");
		sql.addJoin("LEFT JOIN pqfdata productSafety ON productSafety.auditID = ca.id AND productSafety.questionID = 7679 AND productSafety.dateVerified IS NULL");
		sql.addJoin("LEFT JOIN pqfdata product ON product.auditID = ca.id AND product.questionID = 7678 AND product.dateVerified IS NULL");

		sql.addField("safety.id safetyID");
		sql.addField("safety.answer safetyRiskAnswer");
		sql.addField("productSafety.id productSafetyID");
		sql.addField("productSafety.answer productSafetyRiskAnswer");
		sql.addField("product.id productID");
		sql.addField("product.answer productRiskAnswer");

		sql.addWhere("(safety.answer = 'Low' and c.safetyRisk > 1) OR (safety.answer = 'Medium' and c.safetyRisk > 2) "
				+ "OR (productSafety.answer = 'Low' and c.safetyRisk > 1) OR (productSafety.answer = 'Medium' and c.safetyRisk > 2) "
				+ "OR (product.answer = 'Low' and c.productRisk > 1) OR (product.answer = 'Medium' and c.productRisk > 2)");
	}

	public String accept() throws Exception {
		setup();
		
		if (safetyRisk != null && productSafetyRisk != null) {
			// Grab the highest of product safety risk or safety risk
			if (productSafetyRisk.ordinal() > safetyRisk.ordinal())
				safetyRisk = productSafetyRisk;
		}

		if (safetyRisk != null) {
			adjustments += "Safety risk adjusted from " + con.getSafetyRisk().toString() + " to "
					+ safetyRisk.toString();

			if (safetyRisk.ordinal() > con.getSafetyRisk().ordinal())
				con.setLastUpgradeDate(new Date());

			con.setSafetyRisk(safetyRisk);
		}

		if (productRisk != null) {
			if (adjustments.length() > 0)
				adjustments += ", ";

			adjustments += "Product risk adjusted from " + con.getProductRisk().toString() + " to "
					+ productRisk.toString();

			if (productRisk.ordinal() > con.getProductRisk().ordinal())
				con.setLastUpgradeDate(new Date());

			con.setProductRisk(productRisk);
		}

		note = new Note(con, getUser(), adjustments + " for " + auditorNotes);
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		con.setAuditColumns(permissions);
		contractorAccountDAO.save(con);

		auditorNotes = "";
		return super.execute();
	}

	public String reject() throws Exception {
		setup();
		
		if (safetyRisk != null && productSafetyRisk != null) {
			// Grab the lowest of product safety risk or safety risk
			if (productSafetyRisk.ordinal() < safetyRisk.ordinal())
				safetyRisk = productSafetyRisk;
		}

		if (safetyRisk != null) {
			adjustments += "Rejected safety risk adjustment from " + con.getSafetyRisk().toString() + " to "
					+ safetyRisk.toString();
		}

		if (productRisk != null) {
			if (adjustments.length() > 0)
				adjustments += ", ";

			adjustments += "Rejected product risk adjustment from " + con.getProductRisk().toString() + " to "
					+ productRisk.toString();
		}

		note = new Note(con, getUser(), adjustments + " for " + auditorNotes);
		note.setNoteCategory(NoteCategory.RiskRanking);
		noteDAO.save(note);

		auditorNotes = "";
		return super.execute();
	}

	private void setup() {
		con = contractorAccountDAO.find(conID);
		adjustments = "";

		if (safetyID > 0)
			safetyRisk = updateAuditData(safetyID);

		if (productSafetyID > 0)
			productSafetyRisk = updateAuditData(productSafetyID);

		if (productID > 0)
			productRisk = updateAuditData(productID);
	}

	private LowMedHigh updateAuditData(int riskDataID) {
		AuditData riskData = auditDataDAO.find(riskDataID);

		if (Strings.isEmpty(auditorNotes))
			auditorNotes = null;

		riskData.setDateVerified(new Date());
		riskData.setComment(auditorNotes);
		riskData.setAuditColumns(permissions);
		auditDataDAO.save(riskData);

		String answer = riskData.getAnswer();
		if (answer.equals("Medium"))
			answer = "Med";

		return LowMedHigh.valueOf(answer);
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

	public int getSafetyID() {
		return safetyID;
	}

	public void setSafetyID(int safetyID) {
		this.safetyID = safetyID;
	}

	public int getProductSafetyID() {
		return productSafetyID;
	}

	public void setProductSafetyID(int productSafetyID) {
		this.productSafetyID = productSafetyID;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}
}
