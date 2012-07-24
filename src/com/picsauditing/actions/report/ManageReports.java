package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.provider.ReportProvider;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {

	// TODO make this an enum or something
	private static final String FAVORITE = "favorite";
	private static final String MY_REPORTS = "saved";
	private static final String ALL_REPORTS = "all";

	public static final String MY_REPORTS_URL = "ManageReports!myReports.action";
	public static final String FAVORITE_REPORTS_URL = "ManageReports!favorites.action";

	@Autowired
	private BasicDAO basicDao;
	@Autowired
	private ReportProvider reportProvider;

	private List<ReportUser> userReports = new ArrayList<ReportUser>();
	private String viewType;
	private int reportId;

	private static final Logger logger = LoggerFactory.getLogger(ManageReports.class);

	public String execute() {
		viewType = MY_REPORTS;
		runQueryForCurrentView();

		return "myReports";
	}

	public String favorites() {
		viewType = FAVORITE;
		runQueryForCurrentView();

		return "favorites";
	}

	public String myReports() {
		viewType = MY_REPORTS;
		runQueryForCurrentView();

		return "myReports";
	}

	public String search() {
		viewType = ALL_REPORTS;
		runQueryForCurrentView();

		return "search";
	}

	public boolean viewingFavoriteReports() {
		return FAVORITE.equals(viewType);
	}

	public boolean viewingMyReports() {
		return MY_REPORTS.equals(viewType);
	}

	public boolean viewingAllReports() {
		return ALL_REPORTS.equals(viewType);
	}

	private void runQueryForCurrentView() {
		if (Strings.isEmpty(viewType))
			viewType = MY_REPORTS;

		try {
			int userId = permissions.getUserId();

			if (FAVORITE.equals(viewType)) {
				userReports = reportProvider.findFavoriteUserReports(userId);
			} else if (MY_REPORTS.equals(viewType)) {
				userReports = reportProvider.findAllUserReports(userId);
			} else if (ALL_REPORTS.equals(viewType)) {
				userReports = reportProvider.findAllUserReports(userId);

				List<Report> publicReports = reportProvider.findPublicReports();
				for (Report report : publicReports) {
					if (!ReportUtil.containsReportWithId(userReports, report.getId())) {
						userReports.add(new ReportUser(permissions.getUserId(), report));
					}
				}
			}
		} catch (Exception e) {
			addActionError(getText("ManageReports.error.problemFindingReports"));
			logger.error("Problem with runQueryForCurrentView() in ManageReports", e);

			if (userReports == null) {
				userReports = Collections.emptyList();
			}
		}
	}

	public String getPageDescription() {
		String pageDescription = "";

		if (FAVORITE.equals(viewType)) {
			pageDescription = getText("ManageReports.pageDescription.Favorites");
		} else if (MY_REPORTS.equals(viewType)) {
			pageDescription = getText("ManageReports.pageDescription.MyReports");
		} else if (ALL_REPORTS.equals(viewType)) {
			pageDescription = getText("ManageReports.pageDescription.AllReports");
		}

		return pageDescription;
	}

	public String removeUserReport() throws Exception {
		try {
			reportProvider.removeUserReport(permissions.getUserId(), reportId);
			addActionMessage(getText("ManageReports.message.ReportRemoved"));
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.NoReportToRemove"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	public String deleteReport() throws IOException {
		try {
			Report report = reportProvider.findOneReport(reportId);
			if (ReportDynamicModel.canUserDelete(permissions.getUserId(), report)) {
				reportProvider.deleteReport(report);
				addActionMessage(getText("ManageReports.message.ReportDeleted"));
			} else {
				addActionError(getText("ManageReports.error.NoDeletePermissions"));
			}
		} catch (NoResultException nre) {
			addActionError(getText("ManageReports.error.NoReportToDelete"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	public String toggleFavorite() {
		try {
			reportProvider.toggleReportUserFavorite(permissions.getUserId(), reportId);
		} catch (NoResultException nre) {
			addActionMessage(getText("ManageReports.message.FavoriteNotFound"));
			logger.warn(nre.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return redirectToFavoriteReports();
	}

	private String redirectToFavoriteReports() {
		try {
			setUrlForRedirect(FAVORITE_REPORTS_URL);
		} catch (IOException ioe) {
			logger.error(ioe.toString());
		}

		return REDIRECT;
	}

	// TODO: Get a button/link for debug only
	public String columnsToTranslate() throws Exception {
		// Set up
		Map<String, String> translations = new TreeMap<String, String>();
		Locale[] locales = TranslationActionSupport.getSupportedLocales();
		List<Report> reports = basicDao.findAll(Report.class);
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
			populateTranslationToPrint(translations, reports, methods, locale);

			// convert to Excel sheet
			sheetNumber = createExcelSheet(translations, workBook, cellStyle, headerStyle, sheetNumber, locale);
		}

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + fileName);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		workBook.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();

		return SUCCESS;
	}

	private void populateTranslationToPrint(Map<String, String> translations, List<Report> reports, QueryMethod[] methods,
			Locale locale) {
		for (Report report : reports) {
			AbstractTable table = report.getTable();
			if (table != null) {
				Map<String, Field> availableFields = ReportDynamicModel.buildAvailableFields(table);

				for (Field field : availableFields.values()) {
					String category = field.getCategory().toString();
					String fieldCategoryKey = "Report.Category." + category;
					String fieldKey = "Report." + field.getName();
					String fieldHelpKey = fieldKey + ".help";

					translations.put(fieldKey, ReportUtil.translateLabel(field, locale));
					translations.put(fieldHelpKey, ReportUtil.getText(fieldHelpKey, locale));
					translations.put(fieldCategoryKey, ReportUtil.translateCategory(category, locale));
				}
			}
		}

		for (QueryMethod queryMethod : methods) {
			String fieldSuffixKey = "Report.Suffix." + queryMethod.name();
			translations.put(fieldSuffixKey, ReportUtil.getText(fieldSuffixKey, locale));
		}
	}

	private int createExcelSheet(Map<String, String> translations, HSSFWorkbook workBook, HSSFCellStyle cellStyle,
			HSSFCellStyle headerStyle, int sheetNumber, Locale locale) {
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

	public void setUserReports(List<ReportUser> userReports) {
		this.userReports = userReports;
	}

	public List<ReportUser> getUserReports() {
		return userReports;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}
