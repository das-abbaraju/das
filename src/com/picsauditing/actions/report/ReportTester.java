package com.picsauditing.actions.report;

import java.util.*;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.service.FieldInfo;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class ReportTester extends PicsActionSupport {

    @Autowired
    private ReportDAO reportDAO;

    private ModelType modelType;
    private int reportID;

    private Report report;

    private List<ReportElement> reportElements;
    private Map<String, Field> availableFields;

    private List<FieldInfo> allModelFieldInfos;

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
        allModelFieldInfos = new ArrayList<FieldInfo>();

        for (ModelType type : ModelType.values()) {
            AbstractModel model = ReportModelFactory.build(type, permissions);

            List modelFieldInfos = buildModelFieldInfomation(type, model, permissions);

            allModelFieldInfos.addAll(modelFieldInfos);
        }

        return "directory";
    }

    public static List<FieldInfo> buildModelFieldInfomation(ModelType modelType, AbstractModel model, Permissions permissions) {
        List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

        if (model != null) {
            Collection<Field> fields = model.getAvailableFields().values();

            for (Field field : fields) {
                if (field.canUserSeeQueryField(permissions)) {
                    FieldInfo fieldInfo = buildFieldInformation(permissions, field);
                    fieldInfo.setModelType(modelType);
                    fieldInfos.add(fieldInfo);
                }
            }
        }

        return fieldInfos;
    }

    private static FieldInfo buildFieldInformation(Permissions permissions, Field field) {
        ReportUtil.translateField(field, permissions.getLocale());

        FieldInfo fieldInfo = new FieldInfo();

        fieldInfo.setFieldId(field.getName());
        fieldInfo.setCategory(field.getCategoryTranslation());
        fieldInfo.setName(field.getText());
        fieldInfo.setHelp(field.getHelp());
        fieldInfo.setVisible(field.isVisible());
        fieldInfo.setFilterable(field.isFilterable());
        fieldInfo.setSortable(field.isSortable());
        return fieldInfo;
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

    public List<FieldInfo> getAllModelFieldInfos() {
        return allModelFieldInfos;
    }

    public void setAllModelFieldInfos(List<FieldInfo> allModelFieldInfos) {
        this.allModelFieldInfos = allModelFieldInfos;
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