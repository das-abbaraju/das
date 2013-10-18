package com.picsauditing.employeeguard.services;

import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ProfileService {
	@Autowired
	private ProfileDAO profileDAO;

	public Profile findById(String id) {
		return profileDAO.find(NumberUtils.toInt(id));
	}

	public Profile findByAppUserId(int appUserId) {
		return profileDAO.findByAppUserId(appUserId);
	}

	public Profile create(Profile profile) {
		EntityHelper.setCreateAuditFields(profile, Identifiable.SYSTEM, new Date());

		String hash = Strings.hashUrlSafe(profile.getId() + profile.getEmail());
		profile.setSlug("PID-" + hash.substring(0, 8).toUpperCase());

		return profileDAO.save(profile);
	}

	public Profile update(EmployeeProfileEditForm employeeProfileEditForm, String profileID, int userId) {
		Profile profile = profileDAO.find(NumberUtils.toInt(profileID));
		profile.setFirstName(employeeProfileEditForm.getFirstName());
		profile.setLastName(employeeProfileEditForm.getLastName());
		profile.setEmail(employeeProfileEditForm.getEmail());
		profile.setPhone(employeeProfileEditForm.getPhone());

		EntityHelper.setUpdateAuditFields(profile, userId, new Date());

		return profileDAO.save(profile);
	}
}
