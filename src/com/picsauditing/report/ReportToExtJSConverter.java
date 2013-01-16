package com.picsauditing.report;

import static com.picsauditing.report.ReportJson.BASE_CREATED_BY;
import static com.picsauditing.report.ReportJson.BASE_CREATION_DATE;
import static com.picsauditing.report.ReportJson.BASE_UPDATED_BY;
import static com.picsauditing.report.ReportJson.BASE_UPDATE_DATE;
import static com.picsauditing.report.ReportJson.COLUMN_SQL_FUNCTION;
import static com.picsauditing.report.ReportJson.FILTER_OPERATOR;
import static com.picsauditing.report.ReportJson.REPORT_COLUMNS;
import static com.picsauditing.report.ReportJson.REPORT_DESCRIPTION;
import static com.picsauditing.report.ReportJson.REPORT_EDITABLE;
import static com.picsauditing.report.ReportJson.REPORT_ELEMENT_NAME;
import static com.picsauditing.report.ReportJson.REPORT_FAVORITE;
import static com.picsauditing.report.ReportJson.REPORT_FILTERS;
import static com.picsauditing.report.ReportJson.REPORT_FILTER_EXPRESSION;
import static com.picsauditing.report.ReportJson.REPORT_ID;
import static com.picsauditing.report.ReportJson.REPORT_MODEL_TYPE;
import static com.picsauditing.report.ReportJson.REPORT_NAME;
import static com.picsauditing.report.ReportJson.REPORT_SORTS;
import static com.picsauditing.report.ReportJson.SORT_DIRECTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.ReportJson.ReportListType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ReportToExtJSConverter {

	private static final Logger logger = LoggerFactory.getLogger(ReportToExtJSConverter.class);

	public static JSONObject toJSON(Report report) {
		JSONObject json = new JSONObject();

		convertReportLevelData(report, json);
		convertListToJson(report, json, ReportListType.Columns);
		convertListToJson(report, json, ReportListType.Filters);
		convertListToJson(report, json, ReportListType.Sorts);

		return json;
	}

	private static void convertReportLevelData(Report report, JSONObject json) {
		json.put(REPORT_ID, report.getId());
		json.put(REPORT_MODEL_TYPE, report.getModelType().toString());
		json.put(REPORT_NAME, report.getName());
		json.put(REPORT_DESCRIPTION, report.getDescription());
		json.put(REPORT_FILTER_EXPRESSION, report.getFilterExpression());
		json.put(REPORT_EDITABLE, report.isEditable());
		json.put(REPORT_FAVORITE, report.isFavorite());

		setJsonForAuditColumns(report, json);
	}

	private static void setJsonForAuditColumns(Report report, JSONObject json) {
		if (report.getCreatedBy() != null && report.getCreationDate() != null) {
			json.put(BASE_CREATION_DATE, PicsDateFormat.formatDateIsoOrBlank(report.getCreationDate()));
			json.put(BASE_CREATED_BY, report.getCreatedBy().getName());
		}
		
		if (report.getUpdatedBy() != null && report.getUpdateDate() != null) {
			json.put(BASE_UPDATE_DATE,  PicsDateFormat.formatDateIsoOrBlank(report.getUpdateDate()));
			json.put(BASE_UPDATED_BY, report.getUpdatedBy().getName());
		}
	}

	private static void convertListToJson(Report report, JSONObject json, ReportListType type) {
		JSONArray jsonArray = new JSONArray();
		json.put(type.getKey(), jsonArray);
		for (ReportElement obj : getReportChild(report, type)) {
			switch (type) {
			case Columns:
				jsonArray.add(toJSON((Column) obj));
				break;
			
			case Filters:
				jsonArray.add(toJSON((Filter) obj));
				break;
			
			case Sorts:
				jsonArray.add(toJSON((Sort) obj));
				break;
			}
		}
	}

	private static List<? extends ReportElement> getReportChild(Report report, ReportListType type) {
		switch (type) {
		case Columns:
			return report.getColumns();
			
		case Filters:
			return report.getFilters();
			
		case Sorts:
			return report.getSorts();
			
		}
		
		return null;
	}

	private static JSONObject toJSON(Column obj) {
		JSONObject json = elementToCommonJson(obj);
		
		json.put("type", obj.getField().getColumnType());
		json.put("url", obj.getField().getUrl());
		json.put("sql_function", obj.getSqlFunction());
		json.put("width", obj.getWidth());
		json.put("is_sortable", obj.getField().isSortable());

		return json;
	}

	private static JSONObject toJSON(Filter obj) {
		JSONObject json = elementToCommonJson(obj);
		json.put("type", obj.getField().getFilterType());
		json.put("operator", obj.getOperator().toString());

		setFilterValue(obj, json);

		if (Strings.isNotEmpty(obj.getColumnCompare()))
			json.put(Filter.FIELD_COMPARE, obj.getColumnCompare());

		return json;
	}

	private static void setFilterValue(Filter obj, JSONObject json) {
		if (obj.getOperator().isValueUsed()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(obj.getValues());

			// TODO Make sure the value handshake is correct
			// http://intranet.picsauditing.com/display/it/Handshake
			if (obj.getValues().size() == 1) {
				json.put("value", obj.getValues().get(0));
			} else {
				json.put("value", StringUtils.join(obj.getValues(), ", "));
			}
		}
	}

	private static JSONObject toJSON(Sort obj) {
		JSONObject json = new JSONObject();
		json.put("id", obj.getName());
		json.put("direction", obj.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);
		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement obj) {
		JSONObject json = new JSONObject();
		json.put("id", obj.getName());
		json.put("category", obj.getField().getCategoryTranslation());
		json.put("name", obj.getField().getText());
		json.put("description", obj.getField().getHelp());
		return json;
	}

	/**
	 * From JSON to Report
	 */
	public static void fillParameters(Report report) {
		JSONObject json = (JSONObject) JSONValue.parse(report.getParameters());
		report.setName((String) json.get("name"));
		report.setDescription((String) json.get("description"));
		report.setModelType(parseModelType(json));

		report.setFilterExpression(parseFilterExpression(json));

		addColumns(json, report);
		addFilters(json, report);
		addSorts(json, report);
	}

	private static ModelType parseModelType(JSONObject json) {
		// We may need to consider error handling if the modelType doesn't exist
		return ModelType.valueOf((String) json.get(REPORT_MODEL_TYPE));
	}

	private static String parseFilterExpression(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(REPORT_FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson))
			return filterExpressionFromJson;
		return null;
	}

	private static void addColumns(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_COLUMNS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getColumns().add(toColumn((JSONObject) object));
			}
		}
	}

	private static void addFilters(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_FILTERS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getFilters().add(toFilter((JSONObject) object));
			}
		}
	}

	private static void addSorts(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_SORTS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getSorts().add(toSort((JSONObject) object));
			}
		}
	}

	private static Column toColumn(JSONObject json) {

		Column column = new Column();
		toElementFromJSON(json, column);

		return column;
	}

	private static Sort toSort(JSONObject json) {
		if (json == null)
			return null;

		Sort sort = new Sort();
		toElementFromJSON(json, sort);
		sort.setAscending(isAscending(json));

		return sort;
	}

	private static boolean isAscending(JSONObject json) {
		String direction = (String) json.get(SORT_DIRECTION);
		if (direction != null && direction.equals(Sort.DESCENDING)) {
			return false;
		}
		
		return true;
	}

	private static Filter toFilter(JSONObject json) {
		if (json == null)
			return null;

		Filter filter = new Filter();
		toElementFromJSON(json, filter);
		filter.setOperator(parseOperator(json));
		filter.getValues().addAll(parseValues(json));
		parseAdvancedFilter(json);
		return filter;
	}

	private static void toElementFromJSON(JSONObject json, ReportElement obj) {
		obj.setName((String) json.get(REPORT_ELEMENT_NAME));
		obj.setSqlFunction(SqlFunction.valueOf((String) json.get(COLUMN_SQL_FUNCTION)));
	}

	private static QueryFilterOperator parseOperator(JSONObject json) {
		String object = (String) json.get(FILTER_OPERATOR);
		if (Strings.isEmpty(object)) {
			return QueryFilterOperator.Equals;
		}

		return QueryFilterOperator.valueOf(object.toString());
	}

	private static List<String> parseValues(JSONObject json) {
		JSONArray valuesJsonArray = null;
		List<String> values = new ArrayList<String>();

		try {
			valuesJsonArray = (JSONArray) json.get("values");
		} catch (ClassCastException cce) {
			logger.warn("A filter's values field is not a JSONArray", cce);
		} catch (Exception e) {
			logger.warn("Old format report that doesn't have 'values' in filter");
		}

		if (valuesJsonArray != null && valuesJsonArray.size() > 0) {
			for (Object value : valuesJsonArray) {
				values.add(value.toString().trim());
			}
		} else {
			String value = (String) json.get("value");

			if (Strings.isEmpty(value))
				return values;

			if (value.contains(",")) {
				logger.warn("Old style filter value found with commas separating multiple values. "
						+ "Until we phase out the old code, we need this for backwards compatibility");

				String[] valueSplit = value.split(", ");
				if (valueSplit.length == 1 && value.contains(",")) {
					valueSplit = value.split(",");
				}
				
				values.addAll(Arrays.asList(valueSplit));
			} else {
				values.add(value);
			}
		}

		return values;
	}

	private static Field parseAdvancedFilter(JSONObject json) {
		String advancedFilterOption = (String) json.get(Filter.FIELD_COMPARE);
		if (Strings.isEmpty(advancedFilterOption) || advancedFilterOption.equals("false"))
			return null;

		return new Field(advancedFilterOption.toString());
	}
}
