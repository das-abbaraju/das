package com.picsauditing.employeeguard.services;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class ProfileServiceTest {

    public static final String PROFILE_PHONE = "555-555-5555";
    public static final String PROFILE_FIRST_NAME = "Bob";
    public static final String PROFILE_LAST_NAME = "Smith";
    private ProfileService profileService;

    private static final String PROFILE_EMAIL = "my_email@test.com";

    private static final int PROFILE_UPDATE_ID = 123;
    private static final int USER_ID = 45;

    @Mock
    private ProfileDAO profileDAO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        profileService = new ProfileService();

        Whitebox.setInternalState(profileService, "profileDAO", profileDAO);
    }

    @Test
    public void testCreate() {
        Profile profile = buildProfile();
        when(profileDAO.save(profile)).thenReturn(profile);

        Profile result = profileService.create(profile);

        verifyCreate(profile, result);
    }

    private void verifyCreate(Profile profile, Profile result) {
        assertEquals(Identifiable.SYSTEM, result.getCreatedBy());
        assertNotNull(result.getCreatedDate());
        assertEquals("PID-E8ACE7CB", profile.getSlug());
        verify(profileDAO).save(profile);
    }

    @Test
    public void testUpdate() {
        Profile profile = buildProfile();
        when(profileDAO.find(PROFILE_UPDATE_ID)).thenReturn(profile);
        when(profileDAO.save(profile)).thenReturn(profile);

        Profile result = profileService.update(buildEmployeeProfileEditForm(), Integer.toString(PROFILE_UPDATE_ID), USER_ID);

        verifyUpdate(profile, result);
    }

    private void verifyUpdate(Profile profile, Profile result) {
        assertEquals(USER_ID, result.getUpdatedBy());
        assertNotNull(result.getUpdatedDate());
        assertEquals("Bob", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(PROFILE_EMAIL, result.getEmail());
        Assert.assertEquals("555-555-5555", result.getPhone());
        verify(profileDAO).save(profile);
    }

    private Profile buildProfile() {
        Profile profile = new Profile();
        profile.setEmail(PROFILE_EMAIL);
        return profile;
    }

    private EmployeeProfileEditForm buildEmployeeProfileEditForm() {
        EmployeeProfileEditForm employeeProfileEditForm = new EmployeeProfileEditForm();
        employeeProfileEditForm.setFirstName(PROFILE_FIRST_NAME);
        employeeProfileEditForm.setLastName(PROFILE_LAST_NAME);
        employeeProfileEditForm.setEmail(PROFILE_EMAIL);
        employeeProfileEditForm.setPhone(PROFILE_PHONE);
        return employeeProfileEditForm;
    }
}
