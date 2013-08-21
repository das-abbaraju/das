package com.picsauditing.actions.report;

import java.util.*;

import com.google.common.base.Joiner;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.converter.JsonReportElementsBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.report.models.ModelType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class ReportTester extends PicsActionSupport {

    @Autowired
    private ReportDAO reportDAO;

    ModelType modelType;
	int reportID;

	Report report;

	List<ReportElement> reportElements;
	Map<String, Field> availableFields;

	@Override
	public String execute() throws Exception {
        if (modelType != null) {
            return showModelFocus();
        }
		else if (reportID > 0) {
            return showReportFocus();
		}

		return SUCCESS;
	}

    public String directory() {
        JSONArray fieldDirectoryJson = new JSONArray();
        for (ModelType type : ModelType.values()) {

            AbstractModel model = ReportModelFactory.build(type, permissions);

            JSONArray availableFieldsJson = JsonReportElementsBuilder.buildFields(model, permissions);
            for (Object obj : availableFieldsJson) {
                JSONObject fieldJson = (JSONObject) obj;
                fieldJson.put("modelType",type.toString());
            }

            fieldDirectoryJson.addAll(availableFieldsJson);
        }

        json.put("directory",fieldDirectoryJson);

        return JSON;
    }

    private String showModelFocus() throws ReportValidationException {
        AbstractModel model = ReportModelFactory.build(modelType, permissions);

        availableFields = model.getAvailableFields();

        return "model";
    }

    private String showReportFocus() throws ReportValidationException {
        report = dao.find(Report.class, reportID);

        SqlBuilder sqlBuilder = new SqlBuilder();
        report.setSql(sqlBuilder.initializeReportAndBuildSql(report, permissions).toString());

        ReportUtil.addTranslatedLabelsToReport(report, permissions.getLocale());

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

        AbstractModel model = ReportModelFactory.build(modelType, permissions);

        availableFields = model.getAvailableFields();

        return "single";
    }

    public List<Report> getReports() {
		return reportDAO.findByModel(modelType);
	}

	public Report getReport() {
		return report;
	}

	public void setReportID(int reportID) {
		this.reportID = reportID;
	}

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public List<ReportElement> getReportElements() {
		return reportElements;
	}

	public List<Field> getAvailableField() {
        final List<Field> fields = new ArrayList<Field>();
        fields.addAll(availableFields.values());
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                if (o1.getCategoryTranslation() != o2.getCategoryTranslation()) {
                    return o1.getCategoryTranslation().compareTo(o2.getCategoryTranslation());
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fields;
	}
}