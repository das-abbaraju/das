package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
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
	private String riskType;
	private ContractorAccount cAccount;
	private AuditData safetyData;
	private AuditData productData;
	private AuditData productSafetyData;

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

		sql.addField("r.calcRisk");
		sql.addField("r.riskType");
		sql.addField("r.choice");

		sql.addJoin("JOIN (" + getRiskSQL("Safety") + "\nUNION\n" + getRiskSQL("Product") + ") r ON r.id = a.id");
	}

	public String accept() throws Exception {
		setup();

		LowMedHigh risk = null;
		if ("Safety".equals(riskType)) {
			// Both Risk Assessment and Product Safety Critical categories have been answered
			if (safetyData != null && productSafetyData != null) {
				LowMedHigh safetyRisk = getLowMedHigh(safetyData.getAnswer());
				LowMedHigh productSafetyRisk = getLowMedHigh(productSafetyData.getAnswer());

				if (safetyRisk.ordinal() > productSafetyRisk.ordinal())
					risk = safetyRisk;
				else
					risk = productSafetyRisk;
			} else if (safetyData != null) {
				risk = getLowMedHigh(safetyData.getAnswer());
			} else if (productSafetyData != null) {
				risk = getLowMedHigh(productSafetyData.getAnswer());
			}
		} else if ("Product".equals(riskType) && productData != null) {
			risk = getLowMedHigh(productData.getAnswer());
		}

		if (risk == null) {
			addActionError("Missing risk type or PQF data");
			return super.execute();
		}

		String oldRisk = null;
		String noteString = "";
		if ("Safety".equals(riskType)) {
			oldRisk = cAccount.getSafetyRisk().toString();
			cAccount.setSafetyRisk(risk);

			noteString += riskType + " Risk adjusted from " + oldRisk + " to " + risk.toString() + " for "
					+ auditorNotes;

			if (safetyData != null) {
				safetyData.setDateVerified(new Date());
				if (!Strings.isEmpty(auditorNotes))
					safetyData.setComment(auditorNotes);
				safetyData.setAuditColumns(permissions);

				auditDataDAO.save(safetyData);
			}

			if (productSafetyData != null) {
				productSafetyData.setDateVerified(new Date());
				if (!Strings.isEmpty(auditorNotes))
					productSafetyData.setComment(auditorNotes);
				productSafetyData.setAuditColumns(permissions);

				auditDataDAO.save(productSafetyData);

				if (!Strings.isEmpty(noteString))
					noteString += ", Product " + noteString;
				else
					noteString = "Product " + noteString;
			}
		} else {
			oldRisk = cAccount.getProductRisk().toString();
			cAccount.setProductRisk(risk);

			productData.setDateVerified(new Date());
			if (!Strings.isEmpty(auditorNotes))
				productData.setComment(auditorNotes);
			productData.setAuditColumns(permissions);

			auditDataDAO.save(productData);

			noteString += riskType + " Risk adjusted from " + oldRisk + " to " + risk.toString() + " for "
					+ auditorNotes;
		}

		auditorNotes = "";
		cAccount.setLastUpgradeDate(new Date());
		cAccount.setAuditColumns(permissions);
		contractorAccountDAO.save(cAccount);

		Note note = new Note(cAccount, getUser(), noteString);
		addNote(note, cAccount);

		return super.execute();
	}

	public String reject() throws Exception {
		setup();

		String noteString = "Rejected ";
		if ("Safety".equals(riskType)) {
			if (safetyData != null) {
				safetyData.setDateVerified(new Date());
				if (!Strings.isEmpty(auditorNotes))
					safetyData.setComment(auditorNotes);
				safetyData.setAuditColumns(permissions);

				auditDataDAO.save(safetyData);

				noteString += riskType + " Risk adjustment from " + cAccount.getSafetyRisk() + " to "
						+ safetyData.getAnswer() + " for " + auditorNotes;
			}

			if (productSafetyData != null) {
				productSafetyData.setDateVerified(new Date());
				if (!Strings.isEmpty(auditorNotes))
					productSafetyData.setComment(auditorNotes);
				productSafetyData.setAuditColumns(permissions);

				auditDataDAO.save(productSafetyData);

				if (!Strings.isEmpty(noteString))
					noteString += ", ";

				noteString += "Rejected Product " + riskType + " Risk adjustment from " + cAccount.getSafetyRisk()
						+ " to " + productSafetyData.getAnswer() + " for " + auditorNotes;
			}
		} else {
			productData.setDateVerified(new Date());
			if (!Strings.isEmpty(auditorNotes))
				productData.setComment(auditorNotes);
			productData.setAuditColumns(permissions);

			auditDataDAO.save(productData);

			noteString += riskType + " Risk from " + cAccount.getProductRisk() + " to " + productData.getAnswer()
					+ " for " + auditorNotes;
		}

		Note note = new Note(cAccount, getUser(), noteString);
		addNote(note, cAccount);

		auditorNotes = "";

		return super.execute();
	}

	private void setup() {
		cAccount = contractorAccountDAO.find(conID);

		for (ContractorAudit audit : cAccount.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.PQF) {
				for (AuditData data : audit.getData()) {
					switch (data.getQuestion().getId()) {
					case AuditQuestion.RISK_LEVEL_ASSESSMENT:
						safetyData = data;
						break;
					case AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT:
						productData = data;
						break;
					case AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT:
						productSafetyData = data;
						break;
					}
				}

				break;
			}
		}
	}

	private void addNote(Note note, ContractorAccount account) {
		note.setNoteCategory(NoteCategory.RiskRanking);
		note.setCanContractorView(false);
		note.setViewableById(Account.EVERYONE);
		note.setAccount(account);
		note.setAuditColumns(permissions);
		noteDAO.save(note);
	}

	private String getRiskSQL(String type) {
		String ordinal = "CASE WHEN d.answer = 'Low' THEN 1 WHEN d.answer LIKE 'Med%' THEN 2 "
				+ "WHEN d.answer = 'High' THEN 3 ELSE 0 END";
		String question = null;

		if ("Safety".equals(type)) {
			ordinal = "MIN(" + ordinal + ")";
			question = "IN (" + AuditQuestion.RISK_LEVEL_ASSESSMENT + ", "
					+ AuditQuestion.PRODUCT_SAFETY_CRITICAL_ASSESSMENT + ")";
		} else {
			question = "= " + AuditQuestion.PRODUCT_CRITICAL_ASSESSMENT;
		}

		SelectSQL sql2 = new SelectSQL("contractor_info c");

		sql2.addField("c.id");
		sql2.addField("c." + type.toLowerCase() + "Risk calcRisk");
		sql2.addField("'" + type + "' riskType");
		sql2.addField("GROUP_CONCAT(d.answer SEPARATOR '<br />') choice");
		sql2.addField(ordinal + " ordinal");

		sql2.addJoin("JOIN contractor_audit ca ON c.id = ca.conID");
		sql2.addJoin("JOIN pqfdata d ON d.auditID = ca.id AND d.dateVerified IS NULL AND d.questionID " + question);
		sql2.addJoin("JOIN audit_question q ON q.id = d.questionID");
		sql2.addJoin("JOIN audit_category ac ON ac.id = q.categoryID");

		sql2.addGroupBy("c.id HAVING ordinal < calcRisk");

		return sql2.toString();
	}

	private LowMedHigh getLowMedHigh(String value) {
		if (Strings.isEmpty(value))
			return null;

		if ("Medium".equals(value))
			return LowMedHigh.Med;

		return LowMedHigh.valueOf(value);
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

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
}
