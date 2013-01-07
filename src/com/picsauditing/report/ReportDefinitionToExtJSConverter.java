package com.picsauditing.report;

import static com.picsauditing.report.access.ReportUtil.COLUMNS;
import static com.picsauditing.report.access.ReportUtil.FILTERS;
import static com.picsauditing.report.access.ReportUtil.FILTER_EXPRESSION;
import static com.picsauditing.report.access.ReportUtil.SORTS;

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
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ReportDefinitionToExtJSConverter {

	private enum ReportListType {
		Columns, Filters, Sorts;

		String getKey() {
			return this.toString().toLowerCase();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ReportDefinitionToExtJSConverter.class);

	// From Report to JSON

	public static JSONObject toJSON(Report report) {
		JSONObject json = new JSONObject();

		convertReportLevelData(report, json);
		convertListToJson(report, json, ReportListType.Columns);
		convertListToJson(report, json, ReportListType.Filters);
		convertListToJson(report, json, ReportListType.Sorts);

		return json;
	}

	private static void convertReportLevelData(Report report, JSONObject json) {
		json.put("id", report.getId());
		json.put("type", report.getModelType().toString());
		json.put("name", report.getName());
		json.put("description", report.getDescription());

		if (!Strings.isEmpty(report.getFilterExpression()))
			json.put(FILTER_EXPRESSION, report.getFilterExpression());
		json.put("num_times_favorited", report.getNumTimesFavorited());
		if (report.getCreatedBy() != null) {
			json.put("created_by", report.getCreatedBy().getName());
		}
		json.put("creation_date", report.getCreationDate().getTime());
		if (report.getCreatedBy() != null) {
			json.put("updated_by", report.getUpdatedBy().getName());
		}
		json.put("update_date", report.getUpdateDate().getTime());
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
		JSONObject json = new JSONObject();
		json.put("id", obj.getName());
		if (obj.getSqlFunction() != null)
			json.put("method", obj.getSqlFunction().toString());
		json.put("name", obj.getField().getText());
		json.put("description", obj.getField().getHelp());
		{
			// TODO get the 
			json.put("type", obj.getField().getType().toString().toLowerCase());
			json.put("fieldType", obj.getField().getType().toString());
			json.put("filterType", obj.getField().getType().getFilterType().toString());
			json.put("displayType", obj.getField().getType().toString().toLowerCase());
		}

		if (!Strings.isEmpty(obj.getField().getUrl()))
			json.put("url", obj.getField().getUrl());

		if (obj.getWidth() > 0)
			json.put("width", obj.getWidth());

		JSONArray functionsArray = new JSONArray();
		for (String key : obj.getField().getFunctions().keySet()) {
			JSONObject translatedFunction = new JSONObject();
			translatedFunction.put("key", key);
			translatedFunction.put("value", obj.getField().getFunctions().get(key));
			functionsArray.add(translatedFunction);
		}

		json.put("functions", functionsArray);

		return json;
	}

	private static JSONObject toJSON(Filter obj) {
		JSONObject json = new JSONObject();
		json.put("id", obj.getName());
		json.put("operator", obj.getOperator().toString());

		if (obj.getOperator().isValueUsed()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(obj.getValues());
			// json.put("values", valueArray);

			// Until we phase out the old code, we need this for backwards
			// compatibility
			if (obj.getValues().size() == 1) {
				json.put("value", obj.getValues().get(0));
			} else {
				json.put("value", StringUtils.join(obj.getValues(), ", "));
			}
		}

		if (obj.getFieldForComparison() != null)
			json.put(Filter.FIELD_COMPARE, obj.getFieldForComparison().getName());

		return json;
	}

	private static JSONObject toJSON(Sort obj) {
		JSONObject json = new JSONObject();
		json.put("id", obj.getName());
		if (!obj.isAscending())
			json.put("direction", "DESC");

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
		return ModelType.valueOf((String) json.get("modelType"));
	}

	private static String parseFilterExpression(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson))
			return filterExpressionFromJson;
		return null;
	}

	private static void addColumns(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(COLUMNS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getColumns().add(toColumn((JSONObject) object));
			}
		}
	}

	private static void addFilters(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(FILTERS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getFilters().add(toFilter((JSONObject) object));
			}
		}
	}

	private static void addSorts(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(SORTS);
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
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			return false;
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
		obj.setName((String) json.get("name"));
	}

	private static QueryFilterOperator parseOperator(JSONObject json) {
		String object = (String) json.get("operator");
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
				if (valueSplit.length == 1 && value.contains(","))
					valueSplit = value.split(",");
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

	// END FROM JSON to Filters ///
}
