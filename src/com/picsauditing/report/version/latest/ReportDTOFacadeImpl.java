package com.picsauditing.report.version.latest;

import static com.picsauditing.report.access.ReportUtil.COLUMNS;
import static com.picsauditing.report.access.ReportUtil.FILTERS;
import static com.picsauditing.report.access.ReportUtil.FILTER_EXPRESSION;
import static com.picsauditing.report.access.ReportUtil.SORTS;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.Filter;
import com.picsauditing.report.FilterExpression;
import com.picsauditing.report.ReportElement;
import com.picsauditing.report.Sort;
import com.picsauditing.report.version.ReportDTOFacade;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericUtil;

public class ReportDTOFacadeImpl implements ReportDTOFacade {

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(Report reportDTO) {
		JSONObject json = new JSONObject();

		json.put("id", reportDTO.getId());
		json.put("type", reportDTO.getModelType().toString());
		json.put("description", reportDTO.getDescription());

		if (!Strings.isEmpty(reportDTO.getFilterExpression()))
			json.put(FILTER_EXPRESSION, reportDTO.getFilterExpression());

		if (CollectionUtils.isNotEmpty(reportDTO.getColumns()))
			json.put(COLUMNS, JSONUtilities.convertFromList(reportDTO.getColumns()));

		if (CollectionUtils.isNotEmpty(reportDTO.getFilters()))
			json.put(FILTERS, JSONUtilities.convertFromList(reportDTO.getFilters()));

		if (CollectionUtils.isNotEmpty(reportDTO.getSorts()))
			json.put(SORTS, JSONUtilities.convertFromList(reportDTO.getSorts()));
		return json;
	}

	public void fromJSON(JSONObject json, Report dto) {
		if (json == null) {
			return;
		}
		dto.setName((String) json.get("name"));
		String filterExpressionFromJson = (String) json.get(FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson))
			dto.setFilterExpression(filterExpressionFromJson);

		dto.setFilters(parseJsonToList(json.get(FILTERS), Filter.class));
		dto.setColumns(parseJsonToList(json.get(COLUMNS), Column.class));
		dto.setSorts(parseJsonToList(json.get(SORTS), Sort.class));
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
