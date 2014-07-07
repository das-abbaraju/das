package com.picsauditing.report;

import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.EnumType;
import java.util.Locale;

/**
 * This is a utility class for Dynamic Reports. It should handle all heavy
 * lifting not directly related to routing, persistence, or business logic.
 */
public final class ReportUtil {

    public static final String NAME_KEY_SUFFIX = ".name";
    public static final String DESCRIPTION_KEY_SUFFIX = ".description";
    public static final String CATEGORY_KEY_SUFFIX = ".category";
	public static final String HELP_KEY_SUFFIX = ".help";
	public static final String REPORT_KEY_PREFIX = "Report.";
	public static final String REPORT_CATEGORY_KEY_PREFIX = "Report.Category.";
	public static final String REPORT_FUNCTION_KEY_PREFIX = "Report.Function.";

	private ReportUtil() {
	}

	public static String getText(String key, Locale locale) {
		return TranslationServiceFactory.getTranslationService().getText(key, locale);
	}

	public static void translateField(Field field, Locale locale) {
		field.setText(translateLabel(field, locale));
		field.setCategoryTranslation(translateCategory(field, locale));
		field.setHelp(translateHelp(field, locale));
	}

	public static void addTranslatedLabelsToReport(Report report, Locale locale) {
        addTranslationLabelsToReport(report, locale);
		addTranslationLabelsToColumns(report, locale);
		addTranslationLabelsToFilters(report, locale);
		addTranslationLabelsToSorts(report, locale);
	}

    private static void addTranslationLabelsToReport(Report report, Locale locale) {
        if (StringUtils.isEmpty(report.getSlug())) {
            return;
        }
        String prefix = REPORT_KEY_PREFIX + report.getSlug();

        String nameKey = prefix + NAME_KEY_SUFFIX;
        String descriptionKey = prefix + DESCRIPTION_KEY_SUFFIX;
        String nameText = getText(nameKey, locale);
        String descriptionText = getText(descriptionKey, locale);
        if (StringUtils.equals(nameText,nameKey)) {
            nameText = null;
        }

        if (StringUtils.equals(descriptionText,descriptionKey)) {
            descriptionText = null;
        }

        report.setNameText(nameText);
        report.setDescriptionText(descriptionText);
    }

    private static void addTranslationLabelsToColumns(Report report, Locale locale) {
		if (CollectionUtils.isEmpty(report.getColumns())) {
			return;
		}

		for (Column column : report.getColumns()) {
			Field field = column.getField();

			if (field == null) {

				field = new Field(column.getFieldNameWithoutMethod());
				column.setField(field);
			}

			translateField(field, locale);

			if (column.getSqlFunction() != null) {
				field.setName(column.getFieldNameWithoutMethod());
				field.setTranslationPrefixAndSuffix(null, null);
				String functionTranslation = getText(REPORT_FUNCTION_KEY_PREFIX + column.getSqlFunction().toString(),
						locale);
				field.setText(functionTranslation + ": " + translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
				field.setName(column.getName());
			}

		}
	}

	private static void addTranslationLabelsToFilters(Report report, Locale locale) {
		if (CollectionUtils.isEmpty(report.getFilters())) {
			return;
		}

		for (Filter filter : report.getFilters()) {
			Field field = filter.getField();
			if (field != null) {
				field.setName(filter.getFieldNameWithoutMethod());
				field.setText(translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
				field.setName(filter.getName());
			}
		}
	}

	private static void addTranslationLabelsToSorts(Report report, Locale locale) {
		if (CollectionUtils.isEmpty(report.getSorts())) {
			return;
		}

		for (Sort sort : report.getSorts()) {
			Field field = sort.getField();
			if (field != null) {
				field.setName(sort.getFieldNameWithoutMethod());
				field.setText(translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
				field.setName(sort.getName());
			}
		}
	}

	public static String translateLabel(Field field, Locale locale) {
		String translatedText = null;

		if (field != null) {
			translatedText = getText(REPORT_KEY_PREFIX + field.getName(), locale);

			if (Strings.isEmpty(translatedText)) {
				return field.getName();
			}
		}

		return translatedText;
	}

	public static String translateHelp(Field field, Locale locale) {
		String translatedText = null;

		if (field != null) {
			translatedText = getText(REPORT_KEY_PREFIX + field.getName() + HELP_KEY_SUFFIX, locale);
		}

		return translatedText;
	}

	public static String translateCategory(Field field, Locale locale) {
        String translatedText = null;

        if (field != null) {
            translatedText = getText(REPORT_KEY_PREFIX + field.getName() + CATEGORY_KEY_SUFFIX, locale);
        }

        if (translatedText == null)
            return getText("Report.Category.General", locale);

		return translatedText;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject renderEnumFieldAsJson(FieldType fieldType, Permissions permissions)
			throws ClassNotFoundException {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();

		Class<? extends Enum> enumClass = fieldType.getEnumClass();
		for (Enum enumValue : enumClass.getEnumConstants()) {
			if (enumValue instanceof PermissionAware) {
				if (!((PermissionAware) enumValue).isVisibleTo(permissions)) {
					continue;
				}
			}

			JSONObject enumAsJson = new JSONObject();
			enumAsJson.put("key", getKeyForEnum(fieldType, enumValue));
			enumAsJson.put("value", getValueForEnum(enumValue, permissions.getLocale()));
			jsonArray.add(enumAsJson);
		}
		json.put("result", jsonArray);

		return json;
	}

	@SuppressWarnings("rawtypes")
	private static Object getKeyForEnum(FieldType fieldType, Enum enumValue) {
		if (fieldType.getEnumType() == EnumType.ORDINAL) {
			return enumValue.ordinal();
		} else if (fieldType.getEnumType() == EnumType.STRING) {
			return enumValue.name();
		} else {
            return ((ReportEnum) enumValue).getValue();
        }
	}

	@SuppressWarnings("rawtypes")
	private static Object getValueForEnum(Enum enumValue, Locale locale) {
		if (enumValue instanceof Translatable) {
			return getText(((Translatable) enumValue).getI18nKey(), locale);
		} else {
			return enumValue.name();
		}
	}

	public static String buildLocationString(String city, String countrySubdivision, Locale locale) {
		String location = Strings.EMPTY_STRING;

		String cityLocation = Strings.EMPTY_STRING;
		if (StringUtils.isNotEmpty(city)) {
			cityLocation = city + ", ";
		}

		if (StringUtils.isNotEmpty(countrySubdivision)) {
			String key = "CountrySubdivision." + countrySubdivision;
			String translatedCountrySubdivision = TranslationServiceFactory.getTranslationService().getText(key, locale);

			if (!StringUtils.equals(key, translatedCountrySubdivision)) {
				location = cityLocation + translatedCountrySubdivision;
			}
		}

		return location;
	}
}