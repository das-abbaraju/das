package com.picsauditing.report;

import static com.picsauditing.report.access.ReportUtil.COLUMNS;
import static com.picsauditing.report.access.ReportUtil.FILTERS;
import static com.picsauditing.report.access.ReportUtil.FILTER_EXPRESSION;
import static com.picsauditing.report.access.ReportUtil.SORTS;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

	public Definition() {
	}

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

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	/**
	 * TODO: There should be a check that this is valid before this gets set.
	 * 
	 * Should only contain the following characters: 0-9,(,),AND,OR,SPACE
	 * 
	 * @param filterExpression
	 */
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	public JSONObject toJSON(boolean full) {
		return toJSON();
	}

	public boolean hasPivotColumns() {
		for (Column column : columns) {
			if (column.getPivotDimension() != null) {
				// TBD We may want to only consider it a valid pivot when it has
				// all three dimensions
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		if (filterExpression != null)
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
		if (json == null)
			return;

		filterExpression = (String) json.get(FILTER_EXPRESSION);

		filters = parseJsonToList(json.get(FILTERS), Filter.class);
		columns = parseJsonToList(json.get(COLUMNS), Column.class);
		sorts = parseJsonToList(json.get(SORTS), Sort.class);
	}

	private static <T extends JSONable> List<T> parseJsonToList(Object jsonObject, Class<T> c) {
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

	// TODO: Rewrite this to either modify this object and return
	// nothing, or create a new Definition, merge the one passed
	// in to this one and return the new Definition.
	/**
	 * This merges the definition passed with this Definition instance and
	 * returns the definition passed in.
	 */
	public Definition merge(Definition definition) {
		if (definition == null) {
			return definition;
		}

		columns.addAll(definition.getColumns());
		sorts.addAll(definition.getSorts());
		filters.addAll(definition.getFilters());

		if (!Strings.isEmpty(definition.getFilterExpression())) {
			if (Strings.isEmpty(filterExpression)) {
				filterExpression = definition.getFilterExpression();
			} else {
				filterExpression = filterExpression + " AND " + definition.getFilterExpression();
			}
		}

		return definition;
	}
}
