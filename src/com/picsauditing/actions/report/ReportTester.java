package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;

@SuppressWarnings("serial")
public class ReportTester extends PicsActionSupport {

	int reportID;

	Report report;

	List<ReportElement> reportElements;
	Map<String, Field> availableFields;

	@Override
	public String execute() throws Exception {
		if (reportID > 0) {
			report = dao.find(Report.class, reportID);

			SqlBuilder sqlBuilder = new SqlBuilder();
			report.setSql(sqlBuilder.initializeReportAndBuildSql(report, permissions).toString());

			ReportUtil.addTranslatedLabelsToReportParameters(report, permissions.getLocale());

			reportElements = new ArrayList<ReportElement>();
			reportElements.add(new Column("COLUMNS"));
			reportElements.addAll(report.getColumns());
			reportElements.add(new Column("--------------"));
			reportElements.add(new Column("FILTERS"));
			reportElements.addAll(report.getFilters());
			reportElements.add(new Column("--------------"));
			reportElements.add(new Column("SORTS"));
			reportElements.addAll(report.getSorts());

			ModelType modelType = report.getModelType();

			AbstractModel model = ModelFactory.build(modelType, permissions);

			availableFields = model.getAvailableFields();

			return "single";
		}

		return SUCCESS;
	}

	public List<Report> getReports() {
		return dao.findAll(Report.class);
	}

	public Report getReport() {
		return report;
	}

	public void setReportID(int reportID) {
		this.reportID = reportID;
	}

	public List<ReportElement> getReportElements() {
		return reportElements;
	}

	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}
}