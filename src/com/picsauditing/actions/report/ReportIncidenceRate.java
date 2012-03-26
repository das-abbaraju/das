package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.CohsStatistics;
import com.picsauditing.jpa.entities.OshaStatistics;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportIncidenceRate extends ReportAnnualAddendum {

	protected String auditorNotes;
	protected int conID;
	protected int oshaAuditID;

	@Autowired
	protected NoteDAO noteDAO;

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

		sql.addJoin("JOIN pqfData pd ON pd.auditID = ca.id");
		sql.addJoin("JOIN naics n ON n.code = a.naics");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.conID = a.id AND fcc.criteriaID = 559");
		sql.addField("CASE WHEN pd.questionID = " + OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR + " THEN 'OSHA' WHEN pd.questionID = 11115 THEN 'MSHA' ELSE 'COHS' END AS shaType");
		sql.addField("ROUND(fcc.answer, 2) trirAverage");
		sql.addField("pd.answer AS incidenceRate");
		sql.addField("pd.dateVerified");
		sql.addField("n.trir");

		setVerifiedAnnualUpdateFilter("pd.dateVerified");

		if (filterOn(getFilter().getShaType())) {
			int questionID = 0;

			if (getFilter().getShaType().equals(OshaType.OSHA))
				questionID = OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR;
			else if (getFilter().getShaType().equals(OshaType.MSHA))
				questionID = 11115;
			else if (getFilter().getShaType().equals(OshaType.COHS))
				questionID = CohsStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR;

			sql.addWhere("pd.questionID = " + questionID);

			if (getFilter().getShaType().equals(OshaType.MSHA) || getFilter().getShaType().equals(OshaType.COHS)) {
				getFilter().setVerifiedAnnualUpdate(0);
			}
		}

		sql.addWhere("pd.answer = CONCAT('', 0 + pd.answer)");
		sql.addWhere("(pd.answer >= " + getFilter().getIncidenceRate() + ")");
		sql.addWhere("(pd.answer < " + getFilter().getIncidenceRateMax() + ")");
		sql.addWhere("(c.trirAverage >= " + getFilter().getIncidenceRateAvg() + "AND c.trirAverage < "
				+ getFilter().getIncidenceRateAvgMax() + ")"
				+ (getFilter().getIncidenceRateAvg() == -1.0f ? " OR c.trirAverage IS NULL" : ""));
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
	
	// TODO: FIX ME Michael, please convert this report to use the OSHA Data stored in PQF Data instead
	public String verify() throws Exception {
		/*OshaAudit oshaAudit = oshaAuditDAO.find(oshaAuditID);
		oshaAudit.setVerifiedDate(new Date());
		oshaAuditDAO.save(oshaAudit);

		ContractorAccount contractor = contractorAccountDAO.find(conID);
		Note note = new Note(contractor, getUser(), "Incidence Rate for year " + oshaAudit.getConAudit().getAuditFor()
				+ " has been verified - " + auditorNotes);
		note.setNoteCategory(NoteCategory.Audits);
		noteDAO.save(note);

		auditorNotes = "";*/

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