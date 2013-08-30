package com.picsauditing.service.fileimport;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class EmployeeFileParser {
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeFileParser.class);

	private OperatorAccountDAO operatorAccountDAO;
	private Account account;
	private Permissions permissions;

	private Set<Employee> employees = Collections.emptySet();
	private List<OperatorAccount> operators = Collections.emptyList();

	public EmployeeFileParser(OperatorAccountDAO operatorAccountDAO, Account account, Permissions permissions) {
		this.operatorAccountDAO = operatorAccountDAO;

		this.account = account;
		this.permissions = permissions;
	}

	public Set<Employee> parseFile(final File file) {
		importData(file);

		return employees;
	}

	private void importData(final File file) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(file));
			employees = new HashSet<>();

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);

				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					Row row = rows.next();

					if (row.getCell(0) != null) {
						String cellValue = row.getCell(0).getRichStringCellValue().getString().trim();

						// Skip headers, but extract the operators from it
						if (extractOperatorsFromHeader(row, cellValue)) {
							continue;
						}

						Employee employee = parseRow(row);

						if (employees.contains(employee)) {
							// The employee is duplicated in the import coming in
							copyEmployeeSitesToExistingEmployee(employee, employees);
						} else {
							employees.add(employee);
						}
					}
				}
			}
		} catch (Exception exception) {
			LOG.error(exception.toString());
			throw new FileImportException(getText("ManageEmployeesUpload.message.ErrorInFile"));
		}
	}

	private Employee parseRow(Row row) throws Exception {
		// Check first name, last name and title only
		if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
			Employee employee = new Employee();
			employee.setAccount(account);
			employee.setAuditColumns(permissions);

			Iterator<Cell> iterator = row.cellIterator();
			while (iterator.hasNext()) {
				Cell cell = iterator.next();
				if (cell != null && !Strings.isEmpty(cell.toString())) {
					switch (cell.getColumnIndex()) {
						case 0:
							employee.setFirstName(cell.toString());
							break;
						case 1:
							employee.setLastName(cell.toString());
							break;
						case 2:
							employee.setTitle(cell.toString());
							break;
						case 3:
							employee.setHireDate(cell.getDateCellValue());
							break;
						case 4:
							employee.setTwicExpiration(cell.getDateCellValue());
							break;
						case 5:
							employee.setEmail(EmailAddressUtils.validate(cell.toString()));
							break;
						case 6:
							employee.setPhone(cell.toString());
							break;
						default:
							int index = cell.getColumnIndex() - getEmployeeInfo().length;
							// Get operators?
							if (index >= 0 && operators.size() > index && operators.get(index) != null) {
								employee.getEmployeeSites().add(buildEmployeeSite(employee, index));
							}
					}
				}
			}

			return employee;
		}

		LOG.error("Could not parse row {} of {}", row.getRowNum(), row.getSheet().getSheetName());
		throw new Exception();
	}

	private EmployeeSite buildEmployeeSite(Employee employee, int index) {
		EmployeeSite employeeSite = new EmployeeSite();
		employeeSite.setEmployee(employee);
		employeeSite.setAuditColumns(permissions);
		employeeSite.setOperator(operators.get(index));
		employeeSite.setEffectiveDate(new Date());
		return employeeSite;
	}

	private void copyEmployeeSitesToExistingEmployee(Employee employee, Set<Employee> employees) {
		for (Employee existingEmployee : employees) {
			if (existingEmployee.equals(employee)) {
				Set<EmployeeSite> uniqueSites = new HashSet<>(existingEmployee.getEmployeeSites());
				uniqueSites.addAll(employee.getEmployeeSites());

				for (EmployeeSite employeeSite : uniqueSites) {
					employeeSite.setEmployee(existingEmployee);
				}

				existingEmployee.setEmployeeSites(new ArrayList<>(uniqueSites));
			}
		}
	}

	/**
	 * In the excel template download, there are additional columns for every operator that the contractor works for,
	 * e.g., "Works In: Shell Puget Sound" and "Works In: Valspar".
	 * <p/>
	 * If we don't find them by name (after stripping out the "Works For: "), we keep processing and the employee
	 * won't get automatically added to that site.
	 *
	 * @param row
	 * @param cellValue
	 * @return
	 */
	private boolean extractOperatorsFromHeader(Row row, String cellValue) {
		if (row.getRowNum() == 0 && cellValue.contains("*")) {
			int start = getEmployeeInfo().length;
			int end = row.getLastCellNum();
			operators = new ArrayList<>();

			List<String> names = new ArrayList<>();
			int prefixLength = getText("ManageEmployeesUpload.label.WorksIn").length() - 3;

			for (int j = 0; j < (end - start); j++) {
				int currentColumn = j + start;

				if (row.getCell(currentColumn) != null && !Strings.isEmpty(row.getCell(currentColumn).toString())) {
					String name = row.getCell(currentColumn).getStringCellValue();

					if (name.length() > 0 && name.length() > prefixLength) {
						name = name.substring(prefixLength).trim();
					}

					names.add(name);
				}
			}

			operators = operatorAccountDAO.findWhere(true, String.format("a.name IN (%s)", Strings.implodeForDB(names)));

			return true;
		}

		return false;
	}

	private String[] getEmployeeInfo() {
		return new String[]{getText("ManageEmployeesUpload.label.EmployeeFirstName"),
				getText("ManageEmployeesUpload.label.EmployeeLastName"), getText("ManageEmployeesUpload.label.Title"),
				getText("ManageEmployeesUpload.label.HireDate"), getText("Employee.twicExpiration"),
				getText("Employee.email"), getText("Employee.phone")};
	}

	private String getText(String key) {
		return TranslationServiceFactory.getTranslationService().getText(key, TranslationServiceFactory.getLocale());
	}
}
