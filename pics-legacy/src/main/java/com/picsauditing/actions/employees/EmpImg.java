package com.picsauditing.actions.employees;

import java.io.File;
import java.io.InputStream;

import java.io.FileInputStream;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.LegacyEmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.FileUtils;

public class EmpImg extends PicsActionSupport {

	private LegacyEmployeeDAO legacyEmployeeDAO;

	protected InputStream inputStream;
	protected Employee employee;

	private int employeeID;

	public EmpImg(LegacyEmployeeDAO legacyEmployeeDAO) {
		this.legacyEmployeeDAO = legacyEmployeeDAO;
	}

	public String execute() {

		if (button != null) {
			if (button.equals("photo")) {
				try {
					File photo = new File(getFtpDir() + "/files/"
							+ FileUtils.thousandize(employeeID)
							+ getFileName(employeeID) + ".jpg");
					if (photo.exists()) {
						inputStream = new FileInputStream(photo);
						return "photo";
					} else
						return BLANK;
				} catch (Exception e) {
					addActionError("Failed to load img");
					return BLANK;
				}
			}
		}

		return SUCCESS;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public String getFileName(int eID) {
		return PICSFileType.emp + "_" + eID;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
