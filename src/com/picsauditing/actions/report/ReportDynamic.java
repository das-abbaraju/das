package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.report.ReportElement;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.Strings;

@SuppressWarnings({ "unchecked", "serial" })
public class ReportDynamic extends PicsActionSupport {

	@Autowired
	private ReportModel reportModel;
	@Autowired
	private ReportUserDAO reportUserDao;

	private Report report;

	private static final Logger logger = LoggerFactory.getLogger(ReportDynamic.class);

	public String create() {
		try {
			Report newReport = reportModel.copy(permissions, report);
			json.put("success", true);
			json.put("reportID", newReport.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			logger.error("An error occurred while copying a report for user {}", permissions.getUserId(), e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String edit() {
		try {
			reportModel.edit(permissions, report);
			json.put("success", true);
			json.put("reportID", report.getId());
		} catch (NoRightsException nre) {
			json.put("success", false);
			json.put("error", nre.getMessage());
		} catch (Exception e) {
			logger.error("An error occurred while editing a report id = {} for user {}", report.getId(),
					permissions.getUserId());
			writeJsonError(e);
		}

		return JSON;
	}

	public String configuration() {
		json.put("favorite", false);
		json.put("editable", false);

		try {
			ReportUser reportUser = reportUserDao.findOne(permissions.getUserId(), report.getId());
			json.put("editable", reportModel.canUserEdit(permissions.getUserId(), report));
			json.put("favorite", reportUser.isFavorite());
		} catch (NoResultException e) {
			logger.info("No ReportUser entry for " + permissions.getUserId() + " AND " + report.getId());
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.configuration()", e);
		}

		return JSON;
	}

	public String report() {
		try {
			ReportModel.validate(report);
			configuration();
			
			reportModel.updateLastViewedDate(permissions.getUserId(), report);
			{
				List<ReportElement> reportElements = new ArrayList<ReportElement>();
				reportElements.addAll(report.getDefinition().getColumns());
				reportElements.addAll(report.getDefinition().getSorts());
				reportElements.addAll(report.getDefinition().getFilters());

				for (ReportElement reportElement : reportElements) {
					if (reportElement.getFieldName().equalsIgnoreCase("AuditName"))
						reportElement.setFieldName("AuditTypeName");
					if (reportElement.getFieldName().equalsIgnoreCase("ContractorID"))
						reportElement.setFieldName("AccountID");
					if (reportElement.getFieldName().equalsIgnoreCase("OperatorID"))
						reportElement.setFieldName("AccountID");
					if (reportElement.getFieldName().equalsIgnoreCase("ContractorName"))
						reportElement.setFieldName("AccountName");
					if (reportElement.getFieldName().equalsIgnoreCase("OperatorName"))
						reportElement.setFieldName("AccountName");
					if (reportElement.getFieldName().equalsIgnoreCase("ContractorRequestedByOperatorReason"))
						reportElement.setFieldName("AccountReason");
					if (reportElement.getFieldName().toUpperCase().startsWith("ContractorID".toUpperCase()))
						reportElement.setFieldName(reportElement.getFieldName().replace("contractorID", "AccountID"));
				}
			}

			json.put("reportID", report.getId());

			SqlBuilder sqlBuilder = new SqlBuilder();
			sqlBuilder.initializeSql(report, permissions);

			ReportUtil.addTranslatedLabelsToReportParameters(report.getDefinition(), permissions.getLocale());

			json.put("report", report.toJSON(true));
			json.put("success", true);
		} catch (ReportValidationException rve) {
			logger.warn("Invalid report in ReportDynamic.report()", rve);
			writeJsonError(rve);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String availableFields() {
		try {
			// ReportModel.validate(report);

			ModelType modelType = report.getModelType();
			json.put("modelType", report.getModelType().toString());

			AbstractModel model = ModelFactory.build(modelType, permissions);

			Map<String, Field> availableFields = model.getAvailableFields();

			json.put("fields", ReportUtil.translateAndJsonify(availableFields, permissions, permissions.getLocale()));
			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.report()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	public String translateValues() {
		try {
			ReportModel.validate(report);

			String fieldName = getRequest().getParameter("fieldName");
			if (Strings.isEmpty(fieldName))
				throw new Exception("You need to pass a fieldName to get the translated field values.");

			Map<String, Field> availableFields = ModelFactory.build(report.getModelType(), permissions)
					.getAvailableFields();
			Field field = availableFields.get(fieldName.toUpperCase());

			if (field == null)
				throw new Exception("Available field undefined");

			String[] untranslatedValues = getRequest().getParameterValues("value");
			List<String> translatedValues = new ArrayList<String>();

			for (String value : untranslatedValues) {
				String key = field.getI18nKey(value);
				translatedValues.add(getText(key));
			}

			json.put("values", translatedValues);
			json.put("success", true);
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDynamic.translateValues()", e);
			writeJsonError(e);
		}

		return JSON;
	}

	private void writeJsonError(Exception e) {
		json.put("success", false);
		json.put("message", e.toString());
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}
}
