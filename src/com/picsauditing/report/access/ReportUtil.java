package com.picsauditing.report.access;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EnumType;
import javax.servlet.ServletOutputStream;

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
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.model.report.ReportParameterConverter;
import com.picsauditing.report.Column;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
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

	public static final String REPORT = "report";
	public static final String REPORT_DATA = "report_data";
	public static final String DATA = "data";
	public static final String TOTAL = "total";
	public static final String COLUMNS = "columns";
	public static final String FILTERS = "filters";
	public static final String SORTS = "sorts";
	public static final String FILTER_EXPRESSION = "filterExpression";
	public static final String SQL = "sql";
	public static final String SUCCESS = "success";
	public static final String MESSAGE = "message";

	private final static I18nCache i18nCache = I18nCache.getInstance();

	private ReportUtil() {
	}

	public static String getText(String key, Locale locale) {
		return i18nCache.getText(key, locale);
	}

	@SuppressWarnings("unchecked")
	public static JSONArray translateAndJsonify(Map<String, Field> availableFields, Permissions permissions,
			Locale locale) {
		JSONArray fieldsJsonArray = new JSONArray();

		for (Field field : availableFields.values()) {
			if (!field.canUserSeeQueryField(permissions))
				continue;

			field.setText(translateLabel(field, locale));

			applyFunctionsToField(locale, field);
			// TODO Change this to the new Impl that will generate visibleFields and filterableFields
			JSONObject obj = ReportParameterConverter.toJSON(field);

			obj.put("category", translateCategory(field.getCategory().toString(), locale));

			String help = getText("Report." + field.getName() + ".help", locale);
			if (help != null) {
				obj.put("help", help);
			}

			fieldsJsonArray.add(obj);
		}

		return fieldsJsonArray;
	}

	public static void addTranslatedLabelsToReportParameters(Report definition, Locale locale) {
		addTranslationLabelsToFields(definition, locale);
		addTranslationLabelsToFilters(definition, locale);
		addTranslationLabelsToSorts(definition, locale);
	}

	private static void addTranslationLabelsToFields(Report definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getColumns()))
			return;

		for (Column column : definition.getColumns()) {
			Field field = column.getField();

			if (field == null) {
				field = new Field(column.getFieldNameWithoutMethod());
				column.setField(field);
			}
			field.setName(column.getFieldNameWithoutMethod());
			String translateLabel = translateLabel(field, locale);
			String translateHelp = translateHelp(field, locale);
			field.setName(column.getName());

			if (column.getSqlFunction() != null) {
				field.setTranslationPrefixAndSuffix(null, null);
				translateLabel = getText("Report.Function." + column.getSqlFunction().toString(), locale) + ": "
						+ translateLabel;
			}

			applyFunctionsToField(locale, field);
			field.setText(translateLabel);
			field.setHelp(translateHelp);
		}
	}

	private static void applyFunctionsToField(Locale locale, Field field) {
		List<SqlFunction> functions = field.getType().getDisplayType().getFunctions();
		Map<String,String> translatedFunctions = new TreeMap<String, String>();
		for (SqlFunction function : functions) {
			translatedFunctions.put(function.toString(), getText("Report.Function." + function.toString(), locale));
		}
		
		field.setFunctions(translatedFunctions);
	}

	private static void addTranslationLabelsToFilters(Report definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getFilters()))
			return;

		for (Filter filter : definition.getFilters()) {
			Field field = filter.getField();
			if (field != null) {
				field.setName(filter.getFieldNameWithoutMethod());
				field.setText(translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
				field.setName(filter.getName());
			}
		}
	}

	private static void addTranslationLabelsToSorts(Report definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getSorts()))
			return;

		for (Sort sort : definition.getSorts()) {
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
			translatedText = getText("Report." + field.getName(), locale);

			if (Strings.isEmpty(translatedText)) {
				return field.getName();
			}
		}

		return translatedText;
	}

	public static String translateHelp(Field field, Locale locale) {
		String translatedText = null;

		if (field != null) {
			translatedText = getText("Report." + field.getName() + ".help", locale);
		}

		return translatedText;
	}

	public static String translateCategory(String category, Locale locale) {
		String translatedText = getText("Report.Category." + category, locale);

		if (translatedText == null) {
			translatedText = getText("Report.Category.General", locale);
		}

		if (translatedText == null) {
			translatedText = "?Report.Category." + category;
		}

		return translatedText;
	}

	public static void findColumnsToTranslate(List<Report> allReports) throws IOException {
		// Set up
		Map<String, String> translations = new TreeMap<String, String>();
		Locale[] locales = TranslationActionSupport.getSupportedLocales();
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
				String fieldCategoryKey = "Report.Category." + category;
				String fieldKey = "Report." + field.getName();
				String fieldHelpKey = fieldKey + ".help";

				translations.put(fieldKey, translateLabel(field, locale));
				translations.put(fieldHelpKey, getText(fieldHelpKey, locale));
				translations.put(fieldCategoryKey, translateCategory(category, locale));
			}
		}

		for (SqlFunction queryMethod : methods) {
			String fieldSuffixKey = "Report.Function." + queryMethod.name();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JSONObject renderEnumFieldAsJson(FieldType fieldType, Permissions permissions)
			throws ClassNotFoundException {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();

		Class enumClass = getEnumClassForName(fieldType);
		for (Object enumValue : enumClass.getEnumConstants()) {
			if (enumValue instanceof PermissionAware) {
				if (!((PermissionAware) enumValue).isVisibleTo(permissions)) {
					continue;
				}
			}

			JSONObject enumAsJson = new JSONObject();
			enumAsJson.put("key", setKeyForEnum(fieldType, enumValue));
			enumAsJson.put("value", setValueForEnum(fieldType, enumValue, permissions.getLocale()));
			jsonArray.add(enumAsJson);
		}
		json.put("result", jsonArray);

		return json;
	}
	
	@SuppressWarnings("rawtypes")
	private static Class getEnumClassForName(FieldType fieldType) throws ClassNotFoundException {
		if (fieldType == FieldType.UserAccountRole) {
			return Class.forName("com.picsauditing.actions.users." + fieldType.name());
		}
		
		return Class.forName("com.picsauditing.jpa.entities." + fieldType.toString());
	}

	private static Object setKeyForEnum(FieldType fieldType, Object enumValue) {
		Enum<?> enumValue2 = (Enum<?>) enumValue;
		if (fieldType.getEnumType() == EnumType.ORDINAL) {
			return enumValue2.ordinal();
		} else {
			return enumValue2.toString();
		}
	}

	private static Object setValueForEnum(FieldType fieldType, Object enumValue, Locale locale) {
		if (enumValue instanceof Translatable) {
			return getText(((Translatable) enumValue).getI18nKey(), locale);
		} else {
			return ((Enum<?>) enumValue).toString();
		}
	}
}
