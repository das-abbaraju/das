package com.picsauditing.report;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EnumType;
import javax.servlet.ServletOutputStream;

import com.picsauditing.jpa.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * This is a utility class for Dynamic Reports. It should handle all heavy
 * lifting not directly related to routing, persistence, or business logic.
 */
public final class ReportUtil {

    public static final String CATEGORY_KEY_SUFFIX = ".category";
	public static final String HELP_KEY_SUFFIX = ".help";
	public static final String REPORT_KEY_PREFIX = "Report.";
	public static final String REPORT_CATEGORY_KEY_PREFIX = "Report.Category.";
	public static final String REPORT_FUNCTION_KEY_PREFIX = "Report.Function.";

	private static TranslationService translationService = TranslationServiceFactory.getTranslationService();

	private ReportUtil() {
	}

	public static String getText(String key, Locale locale) {
		return translationService.getText(key, locale);
	}

	public static void translateField(Field field, Locale locale) {
		field.setText(translateLabel(field, locale));
		field.setCategoryTranslation(translateCategory(field, locale));
		field.setHelp(translateHelp(field, locale));
	}

	public static void addTranslatedLabelsToReport(Report report, Locale locale) {
		addTranslationLabelsToColumns(report, locale);
		addTranslationLabelsToFilters(report, locale);
		addTranslationLabelsToSorts(report, locale);
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
			String translatedCountrySubdivision = translationService.getText(key, locale);

			if (!StringUtils.equals(key, translatedCountrySubdivision)) {
				location = cityLocation + translatedCountrySubdivision;
			}
		}

		return location;
	}
}