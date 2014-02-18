package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private ProfileDocumentService profileDocumentService;

	private InputStream inputStream;

	public String employeePhoto() throws FileNotFoundException {
		String ftpDir = getFtpDir();

		Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
		inputStream = getPhotoStreamForEmployee(employee, ftpDir);

		return "photo";
	}

	public String profilePhoto() throws FileNotFoundException {
		String ftpDir = getFtpDir();

		Profile profile = profileService.findById(id);
		inputStream = getPhotoStreamForProfile(profile, ftpDir);

		return "photo";
	}

	private InputStream getPhotoStreamForEmployee(Employee employee, String ftpDir) throws FileNotFoundException {
		if (photoUtil.photoExistsForEmployee(employee, employee.getAccountId(), ftpDir)) {
			return photoUtil.getPhotoStreamForEmployee(employee, permissions.getAccountId(), ftpDir);
		}

		if (employee != null && photoUtil.photoExistsForProfile(employee.getProfile(), ftpDir)) {
			return photoUtil.getPhotoStreamForProfile(profileDocumentService.getPhotoDocumentFromProfile(employee.getProfile()), ftpDir);
		}

		return defaultPhoto(ftpDir);
	}

	private InputStream getPhotoStreamForProfile(Profile profile, String ftpDir) throws FileNotFoundException {
		if (photoUtil.photoExistsForProfile(profile, ftpDir)) {
			return photoUtil.getPhotoStreamForProfile(profileDocumentService.getPhotoDocumentFromProfile(profile), ftpDir);
		}

		return defaultPhoto(ftpDir);
	}

	private InputStream defaultPhoto(String ftpDir) throws FileNotFoundException {
		return inputStream = photoUtil.getDefaultPhotoStream(ftpDir);
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
