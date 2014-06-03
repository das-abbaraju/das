package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PhotoAction extends PicsRestActionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(PhotoAction.class);

	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private ProfileDocumentService profileDocumentService;

	private int contractorId;
	private InputStream inputStream;

	public String employeePhoto() throws FileNotFoundException {
		String ftpDir = getFtpDir();

		try {
			Employee employee = employeeEntityService.find(getIdAsInt(), contractorId);
			inputStream = getPhotoStreamForEmployee(employee, ftpDir);
		} catch (Exception e) {
			LOG.error("Exception finding employee {} under contractor {}", new Object[]{id, contractorId, e});
			inputStream = defaultPhoto(ftpDir);
		}

		return "photo";
	}

	public String profilePhoto() throws FileNotFoundException {
		String ftpDir = getFtpDir();

		Profile profile = profileEntityService.find(getIdAsInt());
		inputStream = getPhotoStreamForProfile(profile, ftpDir);

		return "photo";
	}

	private InputStream getPhotoStreamForEmployee(Employee employee, String ftpDir) throws FileNotFoundException {
		if (photoUtil.photoExistsForEmployee(employee, employee.getAccountId(), ftpDir)) {
			return photoUtil.getPhotoStreamForEmployee(employee, employee.getAccountId(), ftpDir);
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

	public int getContractorId() {
		return contractorId;
	}

	public void setContractorId(int contractorId) {
		this.contractorId = contractorId;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
