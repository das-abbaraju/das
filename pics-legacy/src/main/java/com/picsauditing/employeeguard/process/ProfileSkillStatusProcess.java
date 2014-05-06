package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ProfileSkillStatusProcess {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ProfileEntityService profileEntityService;

	public ProfileSkillData buildProfileSkillData(final Profile profile) {


		return null;
	}

	private Set<Integer> allContractors(final Profile profile) {
		return PicsCollectionUtil.extractPropertyToSet(profileEntityService.getEmployeesForProfile(profile),

				new PicsCollectionUtil.PropertyExtractor<Employee, Integer>() {
					@Override
					public Integer getProperty(Employee employee) {
						return employee.getAccountId();
					}
				});
	}

	private Set<Integer> allSites(final Profile profile) {
		return profileEntityService.getSiteAssignments(profile);
	}

}
