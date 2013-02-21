package com.picsauditing.report;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EnumType;
import javax.servlet.ServletOutputStream;

import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.CollectionUtils;
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

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.util.Strings;

/**
 * This is a utility class for Dynamic Reports. It should handle all heavy
 * lifting not directly related to routing, persistence, or business logic.
 */
public final class ReportUtil {

	public static final String HELP_KEY_SUFFIX = ".help";
	public static final String REPORT_KEY_PREFIX = "Report.";
	public static final String REPORT_CATEGORY_KEY_PREFIX = "Report.Category.";
	public static final String REPORT_FUNCTION_KEY_PREFIX = "Report.Function.";

	private static I18nCache i18nCache = I18nCache.getInstance();

	private ReportUtil() {
	}

	public static String getText(String key, Locale locale) {
		return i18nCache.getText(key, locale);
	}

	public static void translateField(Field field, Locale locale) {
		field.setText(translateLabel(field, locale));
		field.setCategoryTranslation(translateCategory(field, locale));
		field.setHelp(translateHelp(field, locale));
	}

	public static void addTranslatedLabelsToReportParameters(Report report, Locale locale) {
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
				String functionTranslation = getText(REPORT_FUNCTION_KEY_PREFIX + column.getSqlFunction().toString(), locale);
				field.setText(functionTranslation + ": " + translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
				field.setName(column.getName());
			}
			
		}
	}

	public static Map<String, String> getTranslatedFunctionsForField(Locale locale, DisplayType type) {
		Map<String,String> translatedFunctions = new TreeMap<String, String>();
/*
		for (SqlFunction function : type.getFunctions()) {
			translatedFunctions.put(function.toString(), getText("Report.Function." + function.toString(), locale));
		}
*/
		return translatedFunctions;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray convertTranslatedFunctionstoJson(Map<String, String> map) {
		JSONArray functionsArray = new JSONArray();
		for (String key : map.keySet()) {
			JSONObject translatedFunction = new JSONObject();
			translatedFunction.put("key", key);
			translatedFunction.put("value", map.get(key));
			functionsArray.add(translatedFunction);
		}
		return functionsArray;
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
		String translatedText = getText(REPORT_CATEGORY_KEY_PREFIX + field.getCategory(), locale);

		if (translatedText == null) {
			translatedText = getText("Report.Category.General", locale);
		}

		if (translatedText == null) {
			translatedText = "?" + field.getCategory();
		}

		return translatedText;
	}

	public static void findColumnsToTranslate(List<Report> allReports) throws IOException {
		// Set up
		Map<String, String> translations = new TreeMap<String, String>();
		LanguageModel languageModel = (LanguageModel) SpringUtils.getBean("LanguageModel");
		List<Locale> locales = languageModel.getStableLanguageLocales();
		SqlFunction[] methods = SqlFunction.values();
		String fileName = "Column translations for DR";

		// Excel setup
		HSSFWorkbook workBook = new HSSFWorkbook();

		HSSFDataFormat df = workBook.createDataFormat();
		HSSFFont font = workBook.createFont();
		font.setFontHeightInPoints((short) 12);

		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setDataFormat(df.getFormat("@"));

		cellStyle.setFont(font);

		HSSFFont headerFont = workBook.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = workBook.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		int sheetNumber = 0;

		for (Locale locale : locales) {
			translations.clear();
			// get the translations
			populateTranslationToPrint(translations, allReports, methods, locale);

			// convert to Excel sheet
			sheetNumber = createExcelSheet(translations, workBook, cellStyle, headerStyle, sheetNumber, locale);
		}

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + fileName);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workBook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}

	private static int createExcelSheet(Map<String, String> translations, HSSFWorkbook workBook,
			HSSFCellStyle cellStyle, HSSFCellStyle headerStyle, int sheetNumber, Locale locale) {
		HSSFSheet sheet = workBook.createSheet();

		sheet.setDefaultColumnStyle(0, cellStyle);
		sheet.setDefaultColumnStyle(1, cellStyle);
		sheet.setDefaultColumnStyle(2, cellStyle);

		workBook.setSheetName(sheetNumber, "DR Translations for " + locale.getDisplayLanguage());
		sheetNumber++;

		int rowNumber = 0;

		// Add the Column Headers to the top of the report
		HSSFRow row = sheet.createRow(rowNumber);
		rowNumber++;

		HSSFCell col1 = row.createCell(0);
		col1.setCellValue(new HSSFRichTextString("MsgKey"));
		col1.setCellStyle(headerStyle);
		HSSFCell col2 = row.createCell(1);
		col2.setCellValue(new HSSFRichTextString("MsgValue"));
		col2.setCellStyle(headerStyle);
		HSSFCell col3 = row.createCell(2);
		col3.setCellValue(new HSSFRichTextString("Description"));
		col3.setCellStyle(headerStyle);

		for (String msgKey : translations.keySet()) {
			String msgValue = translations.get(msgKey);

			row = sheet.createRow(rowNumber);
			rowNumber++;
			col1 = row.createCell(0);
			col1.setCellValue(new HSSFRichTextString(msgKey));
			col2 = row.createCell(1);
			col2.setCellValue(new HSSFRichTextString(msgValue));
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		return sheetNumber;
	}

	private static void populateTranslationToPrint(Map<String, String> translations, List<Report> reports,
			SqlFunction[] methods, Locale locale) {
		for (Report report : reports) {
			Map<String, Field> availableFields = ModelFactory
					.build(report.getModelType(), createSuperUserPermissions()).getAvailableFields();

			for (Field field : availableFields.values()) {
				String category = field.getCategory().toString();
				String fieldCategoryKey = REPORT_CATEGORY_KEY_PREFIX + category;
				String fieldKey = REPORT_KEY_PREFIX + field.getName();
				String fieldHelpKey = fieldKey + HELP_KEY_SUFFIX;

				translations.put(fieldKey, translateLabel(field, locale));
				translations.put(fieldHelpKey, getText(fieldHelpKey, locale));
				translations.put(fieldCategoryKey, translateCategory(field, locale));
			}
		}

		for (SqlFunction queryMethod : methods) {
			String fieldSuffixKey = REPORT_FUNCTION_KEY_PREFIX + queryMethod.name();
			translations.put(fieldSuffixKey, getText(fieldSuffixKey, locale));
		}
	}

	private static Permissions createSuperUserPermissions() {
		Permissions permissions = new Permissions();
		for (OpPerms opPerm : OpPerms.values()) {
			UserAccess userAccess = new UserAccess();
			userAccess.setOpPerm(opPerm);
			permissions.getPermissions().add(userAccess);
		}
		permissions.setAccountType("Corporate");
		return permissions;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject renderEnumFieldAsJson(FieldType fieldType, Permissions permissions) throws ClassNotFoundException {
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
		} else {
			return enumValue.name();
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
}
