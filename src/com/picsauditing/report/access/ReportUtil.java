package com.picsauditing.report.access;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
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
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.Column;
import com.picsauditing.report.Definition;
import com.picsauditing.report.Filter;
import com.picsauditing.report.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.report.tables.AbstractTable;

/**
 * This is a utility class for Dynamic Reports. It should handle all heavy
 * lifting not directly related to routing, persistence, or business logic.
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
					} else if (field.getName().contains("StatusSubstatus")) {
						String[] valueString = ((String) value).split(":");
						
						String statusI18nKey = "AuditStatus." + valueString[0];
						String statusTranslation = getText(statusI18nKey, locale);
						String valueTranslated = statusTranslation;
						
						if (valueString.length > 1) {
							String subStatusI18nKey = "AuditSubStatus." + valueString[1];
							String subStatusTranslation = getText(subStatusI18nKey, locale);
							valueTranslated += ": " + subStatusTranslation;
						}
						
						jsonRow.put(column, valueTranslated);
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

	@SuppressWarnings("unchecked")
	public static JSONArray translateAndJsonify(Map<String, Field> availableFields, Permissions permissions,
			Locale locale) {
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
			Field field = column.getField();

			if (field == null) {
				field = new Field(column.getFieldNameWithoutMethod(), "", FilterType.String);
			}
			String translateLabel = translateLabel(field, locale);
			String translateHelp = translateHelp(field, locale);
			field.setName(column.getFieldName());

			if (column.getMethod() != null) {
				translateLabel += " " + getText("Report.Suffix." + column.getMethod().toString(), locale);
			}

			field.setText(translateLabel);
			field.setHelp(translateHelp);
		}
	}

	private static void addTranslationLabelsToFilters(Definition definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getFilters()))
			return;

		for (Filter filter : definition.getFilters()) {
			Field field = filter.getField();
			if (field != null) {
				field.setText(translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
			}
		}
	}

	private static void addTranslationLabelsToSorts(Definition definition, Locale locale) {
		if (CollectionUtils.isEmpty(definition.getSorts()))
			return;

		for (Sort sort : definition.getSorts()) {
			Field field = sort.getField();
			if (field != null) {
				field.setText(translateLabel(field, locale));
				field.setHelp(translateHelp(field, locale));
			}
		}
	}

	public static String translateLabel(Field field, Locale locale) {
		String translatedText = null;

		if (field != null) {
			translatedText = getText("Report." + field.getName(), locale);
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

	public static boolean hasColumns(Report report) {
		return (report.getDefinition().getColumns().size() >= 1);
	}

	public static boolean containsReportWithId(List<ReportUser> userReports, int reportId) {
		for (ReportUser userReport : userReports) {
			if (userReport.getReport().getId() == reportId)
				return true;
		}

		return false;
	}

	public static void findColumnsToTranslate(List<Report> allReports) throws IOException {
		// Set up
		Map<String, String> translations = new TreeMap<String, String>();
		Locale[] locales = TranslationActionSupport.getSupportedLocales();
		QueryMethod[] methods = QueryMethod.values();
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
			ReportUtil.populateTranslationToPrint(translations, allReports, methods, locale);

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
			QueryMethod[] methods, Locale locale) {
		for (Report report : reports) {
			AbstractTable table = report.getTable();
			if (table == null)
				continue;

			Map<String, Field> availableFields = ReportModel.buildAvailableFields(table, createSuperUserPermissions());

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

		for (QueryMethod queryMethod : methods) {
			String fieldSuffixKey = "Report.Suffix." + queryMethod.name();
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
		return permissions;
	}
}
