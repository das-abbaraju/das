package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.SiteAssignment;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ProfileEntityService implements EntityService<Profile, Integer> {

	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProfileDAO profileDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;

	/* All Find Methods */

	@Override
	public Profile find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return profileDAO.find(id);
	}

	public Profile findByAppUserId(final int appUserId) {
		return profileDAO.findByAppUserId(appUserId);
	}

	public Set<Employee> getEmployeesForProfile(final Profile profile) {
		return new HashSet<>(employeeDAO.findByProfile(profile));
	}

	public Set<Integer> getSiteAssignments(final Profile profile) {
		return PicsCollectionUtil.extractPropertyToSet(siteAssignmentDAO.findByProfile(profile),

				new PicsCollectionUtil.PropertyExtractor<SiteAssignment, Integer>() {

					@Override
					public Integer getProperty(SiteAssignment siteAssignment) {
						return siteAssignment.getSiteId();
					}
				});
	}

	/* All Save Methods */

	@Override
	public Profile save(Profile profile, final EntityAuditInfo entityAuditInfo) {
		profile = EntityHelper.setCreateAuditFields(profile, entityAuditInfo);

		if (Strings.isEmpty(profile.getSlug())) {
			String hash = Strings.hashUrlSafe(profile.getId() + profile.getEmail());
			profile.setSlug("PID-" + hash.substring(0, 8).toUpperCase());
		}

		return profileDAO.save(profile);
	}

	/* All Update Methods */

	@Override
	public Profile update(final Profile profile, final EntityAuditInfo entityAuditInfo) {
		Profile profileToUpdate = find(profile.getId());

		profileToUpdate.setFirstName(profile.getFirstName());
		profileToUpdate.setLastName(profile.getLastName());
		profileToUpdate.setEmail(profile.getEmail());
		profileToUpdate.setPhone(profile.getPhone());

		profileToUpdate = EntityHelper.setUpdateAuditFields(profileToUpdate, entityAuditInfo);

		return profileDAO.save(profileToUpdate);
	}

	public Profile update(final EmployeeProfileEditForm employeeProfileEditForm,
						  final String profileId,
						  final int userId) {

		Profile profile = profileDAO.find(NumberUtils.toInt(profileId));
		profile.setFirstName(employeeProfileEditForm.getFirstName());
		profile.setLastName(employeeProfileEditForm.getLastName());
		profile.setEmail(employeeProfileEditForm.getEmail());
		profile.setPhone(employeeProfileEditForm.getPhone());

		EntityHelper.setUpdateAuditFields(profile, userId, new Date());

		return profileDAO.save(profile);
	}

	/* All Delete Methods */

	@Override
	public void delete(final Profile profile) {
		if (profile == null) {
			throw new NullPointerException("profile cannot be null");
		}

		profileDAO.delete(profile);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Profile profile = find(id);
		delete(profile);
	}
}
