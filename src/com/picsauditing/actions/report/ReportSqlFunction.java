package com.picsauditing.actions.report;

import static com.picsauditing.report.ReportJson.writeJsonException;
import static com.picsauditing.report.ReportJson.writeJsonSuccess;

import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;

@SuppressWarnings("serial")
public class ReportSqlFunction extends PicsApiSupport {

	private ModelType type;
	private String fieldId;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		try {
			AbstractModel model = ModelFactory.build(type, permissions);
			Field field = model.getAvailableFields().get(fieldId.toUpperCase());
			json.put(ReportJson.SQL_FUNCTIONS, sqlFunctionJson(field, permissions.getLocale()));
			writeJsonSuccess(json);
		} catch (Exception e) {
			writeJsonException(json, e);
		}

		return JSON;
	}

	@SuppressWarnings("unchecked")
	private static JSONArray sqlFunctionJson(Field field, Locale locale) {
		JSONArray sqlFunctionArray = new JSONArray();

		for (SqlFunction sqlFunction : field.getType().getSqlFunctions()) {

			JSONObject sqlFunctionKeyValue = new JSONObject();
			sqlFunctionKeyValue.put(ReportJson.SQL_FUNCTIONS_KEY, sqlFunction.name());
			sqlFunctionKeyValue.put(ReportJson.SQL_FUNCTIONS_VALUE,
					ReportUtil.getText(ReportUtil.REPORT_FUNCTION_KEY_PREFIX + sqlFunction.name(), locale));
			sqlFunctionArray.add(sqlFunctionKeyValue);
		}
		return sqlFunctionArray;
	}

	public ModelType getType() {
		return type;
	}

	public void setType(ModelType type) {
		this.type = type;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

}
