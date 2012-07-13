package com.picsauditing.report.access;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.sql.Timestamp;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

/**
 * This is a utility class for Dynamic Reports. It should handle all heavy lifting
 * not directly related to routing, persistence, or business logic.
 */
public final class ReportUtil {

	public static final String COLUMNS = "columns";
	public static final String FILTERS = "filters";
	public static final String SORTS = "sorts";
	public static final String FILTER_EXPRESSION = "filterExpression";

	private final static I18nCache i18nCache = I18nCache.getInstance();

	private ReportUtil() {
	}

	public static String getText(String key, Locale locale) {
		return i18nCache.getText(key, locale);
	}

	public static Column getColumnFromFieldName(String fieldName, List<Column> columns) {
		if (fieldName == null)
			return null;

		for (Column column : columns) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray convertQueryResultsToJson(List<BasicDynaBean> queryResults,
			Map<String, Field> availableFields, Permissions permissions, Locale locale) {

		JSONArray jsonRows = new JSONArray();

		for (BasicDynaBean row : queryResults) {
			JSONObject jsonRow = new JSONObject();

			for (DynaProperty property : row.getDynaClass().getDynaProperties()) {
				String column = property.getName();
				Object value = row.get(column);

				if (value == null)
					continue;

				Field field = availableFields.get(column.toUpperCase());

				if (field == null) {
					// TODO we get nulls if the column name is custom such
					// as contractorNameCount. Convert this to contractorName
					jsonRow.put(column, value);

				} else if (field.canUserSeeQueryField(permissions)) {

					if (field.isTranslated()) {
						String key = field.getI18nKey(value.toString());
						jsonRow.put(column, getText(key, locale));

					} else if (value instanceof java.sql.Date) {
						java.sql.Date valueAsDate = (java.sql.Date) value;
						jsonRow.put(column, valueAsDate.getTime());

					} else if (value instanceof java.sql.Timestamp) {
						Timestamp valueAsTimestamp = (Timestamp) value;
						jsonRow.put(column, valueAsTimestamp.getTime());

					} else {
						jsonRow.put(column, value.toString());
					}
				}
			}
			jsonRows.add(jsonRow);
		}

		return jsonRows;
	}

	public static void localize(Report report, Locale locale) {
		if (CollectionUtils.isEmpty(report.getDefinition().getFilters()))
			return;

		for (Filter filter : report.getDefinition().getFilters()) {
			if (!filter.isHasTranslations())
				continue;

			String filterValue = Strings.escapeQuotes(filter.getValue());
			if (Strings.isEmpty(filterValue))
				return;

			String[] values = filterValue.split(",");
			String[] translationValueNameArray = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				String translationKey = filter.getField().getI18nKey(values[i]);
				translationValueNameArray[i] = getText(translationKey, locale);
			}

			String translatedValueNames = StringUtils.join(translationValueNameArray, ",");
			filter.setValueNames(translatedValueNames);
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONArray translateAndJsonify(Map<String, Field> availableFields, Permissions permissions, Locale locale) {
		JSONArray fieldsJsonArray = new JSONArray();

		for (Field field : availableFields.values()) {
			if (!field.canUserSeeQueryField(permissions))
				continue;

			field.setText(translateLabel(field, locale));

			JSONObject obj = field.toJSONObject();
			obj.put("category", translateCategory(field.getCategory().toString(), locale));

			String help = getText("Report." + field.getName() + ".help", locale);
			if (help != null) {
				obj.put("help", help);
			}

			fieldsJsonArray.add(obj);
		}

		return fieldsJsonArray;
	}

	public static void addTranslatedLabelsToReportParameters(Definition definition, Locale locale) {
		addTranslationLabelsToFields(definition, locale);
		addTranslationLabelsToFilters(definition, locale);
		addTranslationLabelsToSorts(definition, locale);
	}

	private static void addTranslationLabelsToFields(Definition definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getColumns()))
			return;

		for (Column column : definition.getColumns()) {
			String translateLabel = translateLabel(column.getField(), locale);
			if (column.getMethod() != null)
				translateLabel += " " + getText("ReportSuffix." + column.getMethod().toString(),locale);
			column.getField().setText(translateLabel);
		}
	}

	private static void addTranslationLabelsToFilters(Definition definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getFilters()))
			return;

		for (Filter filter : definition.getFilters()) {
			filter.getField().setText(translateLabel(filter.getField(), locale));
		}
	}

	private static void addTranslationLabelsToSorts(Definition definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getSorts()))
			return;

		for (Sort sort : definition.getSorts()) {
			sort.getField().setText(translateLabel(sort.getField(), locale));
		}
	}

	public static String translateLabel(Field field, Locale locale) {
		String translatedText = null;
		
		if (field != null) {
			translatedText = getText("Report." + field.getName(), locale);
			if (translatedText == null || translatedText.equals(I18nCache.DEFAULT_TRANSLATION)) {
				translatedText = "?Report." + field.getName();
			}
		}

		return translatedText;
	}

	private static String translateCategory(String category, Locale locale) {
		String translatedText = getText("Report.Category." + category, locale);

		if (translatedText == null)
			translatedText = getText("Report.Category.General", locale);

		if (translatedText == null)
			translatedText = "?Report.Category." + category;

		return translatedText;
	}

	public static boolean hasNoColumns(Report report) {
		return (report.getDefinition().getColumns().size() < 1);
	}

	public static boolean containsReportWithId(List<ReportUser> userReports, int reportId) {
		for (ReportUser userReport : userReports) {
			if (userReport.getReport().getId() == reportId)
				return true;
		}

		return false;
	}
}
