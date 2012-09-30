package com.picsauditing.report;

import static com.picsauditing.report.access.ReportUtil.COLUMNS;
import static com.picsauditing.report.access.ReportUtil.FILTERS;
import static com.picsauditing.report.access.ReportUtil.FILTER_EXPRESSION;
import static com.picsauditing.report.access.ReportUtil.SORTS;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericUtil;

public class Definition implements JSONable {

	private List<Column> columns = new ArrayList<Column>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Sort> sorts = new ArrayList<Sort>();

	private String filterExpression;

	public Definition(String json) {
		if (StringUtils.isEmpty(json)) {
			return;
		}

		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public JSONObject toJSON(boolean full) {
		return toJSON();
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		if (!Strings.isEmpty(filterExpression))
			json.put(FILTER_EXPRESSION, filterExpression);

		if (CollectionUtils.isNotEmpty(columns))
			json.put(COLUMNS, JSONUtilities.convertFromList(columns));

		if (CollectionUtils.isNotEmpty(filters))
			json.put(FILTERS, JSONUtilities.convertFromList(filters));

		if (CollectionUtils.isNotEmpty(sorts))
			json.put(SORTS, JSONUtilities.convertFromList(sorts));

		return json;
	}

	public void fromJSON(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson))
			this.filterExpression = filterExpressionFromJson;

		filters = parseJsonToList(json.get(FILTERS), Filter.class);
		columns = parseJsonToList(json.get(COLUMNS), Column.class);
		sorts = parseJsonToList(json.get(SORTS), Sort.class);
	}

	private static <T extends ReportElement> List<T> parseJsonToList(Object jsonObject, Class<T> c) {
		List<T> parsedJsonObjects = new ArrayList<T>();
		if (jsonObject == null)
			return parsedJsonObjects;

		JSONArray jsonArray = (JSONArray) jsonObject;
		for (Object object : jsonArray) {
			T t = (T) GenericUtil.newInstance(c);
			if (object instanceof JSONObject) {
				t.fromJSON((JSONObject) object);
				parsedJsonObjects.add(t);
			}
		}

		return parsedJsonObjects;
	}
}
