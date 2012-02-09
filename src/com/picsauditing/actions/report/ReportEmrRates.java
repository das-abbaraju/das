package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmrRates extends ReportAnnualAddendum {

	protected String auditorNotes;
	protected int conID;
	protected int pqfID;

	@Autowired
	protected NoteDAO noteDAO;

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.EMRReport);
		super.checkPermissions();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowEmrRange(true);

		sql.addJoin("JOIN pqfdata d ON d.auditID = ca.id");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.criteriaID = " + FlagCriteria.EMR_AVERAGE_ID
				+ " AND fcc.conID = a.id");
		sql.addField("d.answer");
		sql.addField("fcc.answer emrAverage");
		sql.addWhere("d.questionID = " + AuditQuestion.EMR);
		sql.addWhere("d.answer >= " + getFilter().getMinEMR());
		sql.addWhere("d.answer < " + getFilter().getMaxEMR());
		sql.addWhere("d.answer > ''");
		sql.addField("d.dateVerified");
		sql.addField("d.id AS pqfID");

		setVerifiedAnnualUpdateFilter("dateVerified");
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", getText("ReportEmrRates.header.Year"), ExcelCellType.Integer),
				30);
		excelSheet.addColumn(new ExcelColumn("answer", getText("ReportEmrRates.header.Rate"), ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("emrAverage", getText("global.Average"), ExcelCellType.Double));
	}

	public String verify() throws Exception {
		AuditData auditData = auditDataDao.find(pqfID);
		auditData.setDateVerified(new Date());
		auditDataDao.save(auditData);

		ContractorAccount contractor = contractorAccountDAO.find(conID);
		Note note = new Note(contractor, getUser(), "EMR Rate for year " + auditData.getAudit().getAuditFor()
				+ " has been verified - " + auditorNotes);
		note.setNoteCategory(NoteCategory.Audits);
		noteDAO.save(note);

		auditorNotes = "";

		return super.execute();
	}

	public String getAuditorNotes() {
		return auditorNotes;
	}

	public void setAuditorNotes(String auditorNotes) {
		this.auditorNotes = auditorNotes;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getPqfID() {
		return pqfID;
	}

	public void setPqfID(int pqfID) {
		this.pqfID = pqfID;
	}
}