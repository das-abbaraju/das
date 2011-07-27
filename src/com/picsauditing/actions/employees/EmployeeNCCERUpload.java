package com.picsauditing.actions.employees;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class EmployeeNCCERUpload extends PicsActionSupport {
	@Autowired
	private AssessmentResultDAO assessmentResultDAO;
	@Autowired
	private AssessmentTestDAO assessmentTestDAO;

	private Employee employee;
	private File upload;
	private String uploadFileName;

	public String save() throws Exception {
		if (employee != null) {
			if (upload != null && upload.length() > 0) {
				String extension = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
				if (!extension.equalsIgnoreCase("xls") && !extension.equalsIgnoreCase("xlsx")) {
					upload = null;
					addActionError(getText("EmployeeNCCERUpload.message.MustBeExcelFile"));
					return SUCCESS;
				}

				importData(upload);
			} else if (upload == null || upload.length() == 0)
				addActionError(getText("EmployeeNCCERUpload.message.NoFileSelected"));
		} else
			addActionError(getText("EmployeeNCCERUpload.message.MissingAccount"));

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	private void importData(File file) {
		List<AssessmentTest> nccerTests = assessmentTestDAO.findByAssessmentCenter(11069);
		Map<String, AssessmentTest> testMap = new HashMap<String, AssessmentTest>();
		List<AssessmentResult> results = new ArrayList<AssessmentResult>();

		for (AssessmentTest test : nccerTests) {
			testMap.put(test.getQualificationMethod(), test);
		}

		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(file));

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);

				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					Row row = rows.next();

					if (row.getCell(2) != null) {
						String qualMethod = row.getCell(1).getStringCellValue();

						if (testMap.get(qualMethod) != null) {
							Date effectiveDate = row.getCell(2).getDateCellValue();
							int monthsToExpire = (int) row.getCell(3).getNumericCellValue();
							Date expirationDate = DateBean.addMonths(effectiveDate, monthsToExpire);

							AssessmentResult result = new AssessmentResult();
							result.setAssessmentTest(testMap.get(qualMethod));
							result.setAuditColumns(permissions);
							result.setEffectiveDate(effectiveDate);
							result.setExpirationDate(expirationDate);
							result.setEmployee(employee);

							results.add(result);
						}
					}
				}
			}

			saveResults(results);

			if (results.size() == 1)
				addActionMessage(getText("EmployeeNCCERUpload.message.SuccessfullySavedOne"));
			if (results.size() > 1)
				addActionMessage(getText("EmployeeNCCERUpload.message.SuccessfullySavedMany",
						new Object[] { (Integer) results.size() }));
		} catch (Exception e) {
			addActionError(getText("EmployeeNCCERUpload.message.ErrorReadingFile"));
		}
	}

	@Transactional
	private void saveResults(List<AssessmentResult> results) {
		for (AssessmentResult result : results) {
			assessmentResultDAO.save(result);
		}
	}
}
