package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryFilterOperator;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.service.PermissionService;
import com.picsauditing.service.ReportPreferencesService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class JsonReportBuilder {

    private static int reportID;
	private static final Logger logger = LoggerFactory.getLogger(JsonReportBuilder.class);

	// FOR TESTING ONLY
	protected static PermissionService permissionService;
	protected static ReportPreferencesService reportPreferencesService;

	public static JSONObject buildReportJson(Report report, Permissions permissions, List<EmailSubscription> subscriptions) {
		JSONObject json = new JSONObject();

        reportID = report.getId();

		addReportLevelData(json, report, permissions, subscriptions);

		addColumns(json, report.getColumns());
		addFilters(json, report.getFilters());
		addSorts(json, report.getSorts());

		return json;
	}

	private static void addReportLevelData(JSONObject json, Report report, Permissions permissions, List<EmailSubscription> subscriptions) {
		json.put(REPORT_ID, report.getId());
		json.put(REPORT_MODEL_TYPE, report.getModelType().toString());
		json.put(REPORT_NAME, report.getName());
		json.put(REPORT_DESCRIPTION, report.getDescription());
		json.put(REPORT_FILTER_EXPRESSION, report.getFilterExpression());

		json.put(REPORT_EDITABLE, getPermissionService().canUserEditReport(permissions, report.getId()));
		json.put(REPORT_FAVORITE, getReportPreferencesService().isUserFavoriteReport(permissions.getUserId(), report.getId()));

        SubscriptionTimePeriod frequency;
        if (!subscriptions.isEmpty())
            frequency = subscriptions.get(0).getTimePeriod();
        else
            frequency = SubscriptionTimePeriod.None;

        json.put(REPORT_SUBSCRIPTION_FREQUENCY, frequency.toString());
	}

	public static PermissionService getPermissionService() {
		if (permissionService == null)
			return SpringUtils.getBean(SpringUtils.PermissionService);
		return permissionService;
	}

	public static ReportPreferencesService getReportPreferencesService() {
		if (reportPreferencesService == null)
			return SpringUtils.getBean(SpringUtils.ReportPreferencesService);
		return reportPreferencesService;
	}

	protected static void addColumns(JSONObject json, List<Column> columns) {
		JSONArray jsonArray = new JSONArray();

		Collections.sort(columns);
		assignColumnIds(columns);
        setDrillDownURL(columns);

		for (Column column : columns) {
			try {
				jsonArray.add(columnToJson(column));
			} catch (ReportValidationException rve) {
				logger.error(rve.getMessage());
			}
		}

		json.put(REPORT_COLUMNS, jsonArray);
	}

    private static void setDrillDownURL(List<Column> columns) {
        List <Column> aggregateColumns = new ArrayList<Column>();
        JSONArray dynamicParameters = new JSONArray();

        for (Column column : columns) {
            if (column.getSqlFunction() != null && column.getSqlFunction().isAggregate()) {
                aggregateColumns.add(column);
            }
            else {
                JSONObject param = new JSONObject();
                param.put(REPORT_ELEMENT_FIELD_ID,column.getName());
                FilterType filterType = column.getField().getFilterType();

                QueryFilterOperator operator = filterType.defaultOperator;
                if (filterType == FilterType.Date) {
                    operator = QueryFilterOperator.Equals;
                }
                param.put(FILTER_OPERATOR, operator.toString());

                if (column.getSqlFunction() != null) {
                    param.put(FILTER_SQL_FUNCTION, column.getSqlFunction().toString());
                }

                param.put(FILTER_VALUE,"{" + column.getName() + "}");
                dynamicParameters.add(param);
            }
        }

        String drillDownURL = "Report.action?report=" + reportID + "&removeAggregates=true&dynamicParameters=" + dynamicParameters;

        for (Column aggregateColumn : aggregateColumns) {
            aggregateColumn.getField().setUrl(drillDownURL);
        }
    }

    protected static void assignColumnIds(List<Column> columns) {
		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			column.setColumnId(COLUMN_ID_PREFIX + i);
		}
	}

	private static void addFilters(JSONObject json, List<Filter> filters) {
		JSONArray jsonArray = new JSONArray();

		for (Filter filter : filters) {
			try {
				jsonArray.add(filterToJson(filter));
			} catch (ReportValidationException rve) {
				logger.error(rve.getMessage());
			}
		}

		json.put(REPORT_FILTERS, jsonArray);
	}

	private static void addSorts(JSONObject json, List<Sort> sorts) {
		JSONArray jsonArray = new JSONArray();

		for (Sort sort: sorts) {
			jsonArray.add(sortToJson(sort));
		}

		json.put(REPORT_SORTS, jsonArray);
	}

	private static JSONObject columnToJson(Column column) throws ReportValidationException {
		JSONObject json = elementToCommonJson(column);

		json.put(COLUMN_ID, column.getColumnId());
		json.put(COLUMN_TYPE, column.getDisplayType().name());
		json.put(COLUMN_URL, column.getField().getUrl());
		json.put(COLUMN_SQL_FUNCTION, Strings.toStringPreserveNull(column.getSqlFunction()));
		json.put(COLUMN_WIDTH, column.getWidth());
		json.put(REPORT_ELEMENT_SORTABLE, column.getField().isSortable());
		json.put(COLUMN_SORT, column.getSortIndex());

		return json;
	}

	private static JSONObject filterToJson(Filter filter) throws ReportValidationException {
		JSONObject json = elementToCommonJson(filter);

		json.put(FILTER_TYPE, filter.getField().getFilterType().name());
		json.put(FILTER_OPERATOR, filter.getOperator().toString());
		String filterValue = makeFilterValue(filter);
		json.put(FILTER_VALUE, filterValue);
		json.put(FILTER_COLUMN_COMPARE, filter.getColumnCompare());

		return json;
	}

	private static String makeFilterValue(Filter filter) {
		String filterValue = "";

		if (filter.getOperator().isValueCurrentlySupported()) {
			if (filter.getValues().size() == 1) {
				filterValue = filter.getValues().get(0);
			} else {
				filterValue = StringUtils.join(filter.getValues(), ", ");
			}
		}

		return filterValue;
	}

	private static JSONObject sortToJson(Sort sort) {
		JSONObject json = new JSONObject();

		json.put(REPORT_ELEMENT_DB_ID, sort.getId());
		json.put(REPORT_ELEMENT_FIELD_ID, sort.getName());  // todo: Reconcile the naming
		json.put(SORT_DIRECTION, sort.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);

		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement element) throws ReportValidationException {
		JSONObject json = new JSONObject();

		if (element.getField() == null) {
			throw new ReportValidationException("Field with name '" + element.getName() + "' was not loaded correctly. The field name in the database (report_column, etc.) probably hasn't been updated to match.");
		}

		int elementId = element.getId();
		if (elementId == DEFAULT_REPORT_ELEMENT_DB_ID) {
			json.put(REPORT_ELEMENT_DB_ID, null);
		} else {
			json.put(REPORT_ELEMENT_DB_ID, elementId);
		}

		// TODO sort out these member variable names
		json.put(REPORT_ELEMENT_FIELD_ID, element.getName());   // todo: Reconcile the naming
		json.put(REPORT_ELEMENT_CATEGORY, element.getField().getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, element.getField().getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, element.getField().getHelp());

		return json;
	}

}
