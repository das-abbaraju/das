package com.picsauditing.actions.report;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportIncidenceRate extends ReportAnnualAddendum {

	protected String auditorNotes;
	protected int conID;
	protected int oshaAuditID;

	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected OshaAuditDAO oshaAuditDAO;

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.TRIRReport);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowIncidenceRate(true);
		getFilter().setShowIncidenceRateAvg(true);
		getFilter().setShowShaType(true);
		getFilter().setShowShaLocation(true);
		getFilter().setShowCohsStats(true);

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addJoin("JOIN naics n ON n.code = a.naics");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.conID = a.id AND fcc.criteriaID = 559");
		sql.addWhere("(os.recordableTotal*200000/os.manHours >= " + getFilter().getIncidenceRate() + ")");
		sql.addWhere("(os.recordableTotal*200000/os.manHours < " + getFilter().getIncidenceRateMax() + ")");
		sql.addWhere("(c.trirAverage >= " + getFilter().getIncidenceRateAvg() + "AND c.trirAverage < "
				+ getFilter().getIncidenceRateAvgMax() + ")"
				+ (getFilter().getIncidenceRateAvg() == -1.0f ? " OR c.trirAverage IS NULL" : ""));
		sql.addField("os.id AS oshaAuditID");
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("ROUND(fcc.answer, 2) trirAverage");
		sql.addField("os.recordableTotal*200000/os.manHours AS incidenceRate");
		sql.addField("os.verifiedDate");
		sql.addField("os.cad7");
		sql.addField("os.neer");
		sql.addField("n.trir");

		setVerifiedAnnualUpdateFilter("verifiedDate");

		if (getFilter().getCad7() > 0) {
			sql.addWhere("os.cad7 IS NOT NULL AND os.cad7 >= " + getFilter().getCad7());
		}
		if (getFilter().getNeer() > 0) {
			sql.addWhere("os.neer IS NOT NULL AND os.neer >= " + getFilter().getNeer());
		}
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditFor", getText("Filters.label.ForYear"), ExcelCellType.Integer), 30);
		excelSheet.addColumn(new ExcelColumn("location", getText("ReportIncidenceRate.Location")));
		excelSheet.addColumn(new ExcelColumn("description", getText("global.Description")));
		excelSheet.addColumn(new ExcelColumn("SHAType", getText("Filters.label.SHAType")));
		excelSheet
				.addColumn(new ExcelColumn("incidenceRate", getText("ReportIncidenceRate.Rate"), ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("trirAverage", getText("global.Average"), ExcelCellType.Double));
		excelSheet.addColumn(new ExcelColumn("trir", getText("ReportIncidenceRate.TrirIndustryAverage"), ExcelCellType.Double));
	}

	public String verify() throws Exception {
		OshaAudit oshaAudit = oshaAuditDAO.find(oshaAuditID);
		oshaAudit.setVerifiedDate(new Date());
		oshaAuditDAO.save(oshaAudit);

		ContractorAccount contractor = contractorAccountDAO.find(conID);
		Note note = new Note(contractor, getUser(), "Incidence Rate for year " + oshaAudit.getConAudit().getAuditFor()
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

	public int getOshaAuditID() {
		return oshaAuditID;
	}

	public void setOshaAuditID(int oshaAuditID) {
		this.oshaAuditID = oshaAuditID;
	}
}