package com.picsauditing.actions.report.oq;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class ManageImportDataUpload extends PicsActionSupport {
	private AccountDAO accountDAO;

	private int id;
	private Account center;
	private File file;
	protected String fileFileName = null;
	protected String fileName = null;

	public ManageImportDataUpload(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();

		if (id == 0 && !permissions.isAssessment())
			addActionError("Missing Assessment Center ID");
		else {
			if (permissions.isAssessment())
				id = permissions.getAccountId();

			center = accountDAO.find(id);
		}

		if (center == null || !center.getType().equals("Assessment"))
			addActionError("Could not find assessment center");

		if (getActionErrors().size() > 0)
			return SUCCESS;

		if (button != null) {
			if (button.startsWith("Save")) {
				String extension = null;
				if (file != null && file.length() > 0) {
					extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
					FileUtils.getExtension(fileFileName);

					if (extension.equalsIgnoreCase("txt"))
						importDataTxt(file);
					else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx"))
						importData(file);
					else {
						file = null;
						addActionError("Must be a text or excel file");
						return SUCCESS;
					}

				} else if (file == null || file.length() == 0)
					addActionError("No file was selected");
			}
		}

		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void importDataTxt(File file) {
		List<AssessmentResultStage> imported = new ArrayList<AssessmentResultStage>();
		try {
			String all = FileUtils.readFile(file.getAbsolutePath());
			String[] lines = all.split("\n");
			int start = 0;

			if (lines[0].startsWith("QualID"))
				// Remove the header?
				start = 1;

			for (int i = start; i < lines.length; i++) {
				String[] data = lines[i].split("\t");
				AssessmentResultStage stage = new AssessmentResultStage();

				stage.setResultID(data[0]);
				stage.setQualificationMethod(data[1]);
				stage.setDescription(data[2]);
				stage.setQualificationType(data[4]);
				stage.setLastName(data[5]);
				stage.setFirstName(data[6] + (data[7].length() > 0 ? " " + data[7] + "." : ""));
				stage.setCompanyName(data[8]);
				stage.setEmployeeID(data[9]);
				stage.setQualificationDate(DateBean.parseDate(data[10]));
				stage.setCenter(center);
				stage.setAuditColumns(permissions);

				imported.add(stage);
			}
		} catch (Exception e) {
			addActionError("Could not read file");
			return;
		}

		for (AssessmentResultStage stage : imported) {
			accountDAO.save(stage);
		}

		addActionMessage("Successfully imported <b>" + imported.size() + "</b> records.");
	}

	private void importData(File file) {
		List<AssessmentResultStage> imported = new ArrayList<AssessmentResultStage>();
		Workbook wb = null;

		try {
			wb = WorkbookFactory.create(new FileInputStream(file));

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);

				Iterator<Row> rowIterator = sheet.rowIterator();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();

					if (!(row.getCell(0).getStringCellValue().startsWith("Qual"))) {
						AssessmentResultStage stage = new AssessmentResultStage();
						stage.setCenter(center);
						stage.setQualificationType(row.getCell(0).getStringCellValue());
						stage.setQualificationMethod(row.getCell(1).getStringCellValue());
						stage.setDescription(row.getCell(2).getStringCellValue());
						stage.setTestID((int) row.getCell(3).getNumericCellValue());
						stage.setFirstName(row.getCell(4).getStringCellValue());
						stage.setLastName(row.getCell(5).getStringCellValue());
						stage.setEmployeeID(row.getCell(6).getNumericCellValue() + "");
//						stage.setBirthdate(row.getCell(7).getDateCellValue());
//						stage.setEmail(row.getCell(8).getStringCellValue());
						stage.setCompanyName(row.getCell(9).getStringCellValue());
						stage.setCompanyID((int) row.getCell(10).getNumericCellValue());
						stage.setQualificationDate(row.getCell(11).getDateCellValue());

						imported.add(stage);
					}
				}
			}
		} catch (Exception e) {
			if (isDebugging())
				e.printStackTrace();
			
			addActionError("Error reading in excel file, please check the format");
			return;
		}

		for (AssessmentResultStage stage : imported) {
			accountDAO.save(stage);
		}

		addActionMessage("Successfully imported <b>" + imported.size() + "</b> records.");
	}
}