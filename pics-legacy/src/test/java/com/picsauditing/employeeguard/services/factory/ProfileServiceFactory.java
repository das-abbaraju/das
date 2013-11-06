package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.jpa.entities.User;
import org.mockito.Mockito;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class ProfileServiceFactory {
	private static ProfileService profileService = Mockito.mock(ProfileService.class);

	public static ProfileService getProfileService() {
		Mockito.reset(profileService);

		Profile profile = new Profile();
		profile.setId(User.SYSTEM);
		when(profileService.findByAppUserId(anyInt())).thenReturn(profile);
		when(profileService.findById(anyString())).thenReturn(profile);
		when(profileService.update(any(EmployeeProfileEditForm.class), anyString(), anyInt())).thenReturn(profile);

		return profileService;
	}
}
