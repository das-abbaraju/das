package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.factory.EmployeeServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileServiceFactory;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PhotoUtilFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.NoResultException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PhotoActionTest extends PicsActionTest {
	public static final String ID = Integer.toString(45);
	public static final int CONTRACTOR_ID = 123;
	private PhotoAction photoAction;

	private EmployeeService employeeService;
	private PhotoUtil photoUtil;
	private ProfileService profileService;
	private ProfileDocumentService profileDocumentService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupMocks();

		photoAction = new PhotoAction();

		employeeService = EmployeeServiceFactory.getEmployeeService();
		photoUtil = PhotoUtilFactory.getPhotoUtil();
		profileService = ProfileServiceFactory.getProfileService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		Whitebox.setInternalState(photoAction, "employeeService", employeeService);
		Whitebox.setInternalState(photoAction, "photoUtil", photoUtil);
		Whitebox.setInternalState(photoAction, "profileService", profileService);
		Whitebox.setInternalState(photoAction, "profileDocumentService", profileDocumentService);
	}

	@Test
	public void testEmployeePhoto_ValidEmployee() throws Exception {
		photoAction.setContractorId(CONTRACTOR_ID);
		photoAction.setId(ID);

		when(photoUtil.photoExistsForEmployee(any(Employee.class), anyInt(), anyString())).thenReturn(true);

		String result = photoAction.employeePhoto();

		assertEquals("photo", result);
		assertNotNull(photoAction.getInputStream());

		verify(employeeService).findEmployee(anyInt(), anyInt());
		verify(photoUtil).photoExistsForEmployee(any(Employee.class), anyInt(), anyString());
		verify(photoUtil).getPhotoStreamForEmployee(any(Employee.class), anyInt(), anyString());
		verify(photoUtil, never()).getDefaultPhotoStream(anyString());
	}

	@Test
	public void testEmployeePhoto_EmployeeWithProfile() throws Exception {
		photoAction.setContractorId(CONTRACTOR_ID);
		photoAction.setId(ID);

		when(photoUtil.photoExistsForEmployee(any(Employee.class), anyInt(), anyString())).thenReturn(false);
		when(photoUtil.photoExistsForProfile(any(Profile.class), anyString())).thenReturn(true);

		String result = photoAction.employeePhoto();

		assertEquals("photo", result);
		assertNotNull(photoAction.getInputStream());

		verify(employeeService).findEmployee(anyInt(), anyInt());
		verify(photoUtil).photoExistsForEmployee(any(Employee.class), anyInt(), anyString());
		verify(photoUtil).photoExistsForProfile(any(Profile.class), anyString());
		verify(photoUtil).getPhotoStreamForProfile(any(ProfileDocument.class), anyString());
		verify(photoUtil, never()).getDefaultPhotoStream(anyString());
	}

	@Test
	public void testEmployeePhoto_Null() throws Exception {
		photoAction.setContractorId(CONTRACTOR_ID);
		photoAction.setId(ID);

		when(employeeService.findEmployee(anyInt(), anyInt())).thenThrow(NoResultException.class);
		when(photoUtil.photoExistsForEmployee(any(Employee.class), anyInt(), anyString())).thenReturn(false);
		when(photoUtil.photoExistsForProfile(any(Profile.class), anyString())).thenReturn(true);

		String result = photoAction.employeePhoto();

		assertEquals("photo", result);
		assertNotNull(photoAction.getInputStream());

		verify(employeeService).findEmployee(anyInt(), anyInt());
		verify(photoUtil, never()).photoExistsForEmployee(any(Employee.class), anyInt(), anyString());
		verify(photoUtil, never()).photoExistsForProfile(any(Profile.class), anyString());
		verify(photoUtil).getDefaultPhotoStream(anyString());
	}

	@Test
	public void testProfilePhoto() throws Exception {
		photoAction.setId(ID);

		when(photoUtil.photoExistsForProfile(any(Profile.class), anyString())).thenReturn(true);

		String result = photoAction.profilePhoto();

		assertEquals("photo", result);
		assertNotNull(photoAction.getInputStream());

		verify(employeeService, never()).findEmployee(anyInt(), anyInt());
		verify(photoUtil, never()).photoExistsForEmployee(any(Employee.class), anyInt(), anyString());
		verify(photoUtil).photoExistsForProfile(any(Profile.class), anyString());
		verify(photoUtil).getPhotoStreamForProfile(any(ProfileDocument.class), anyString());
		verify(photoUtil, never()).getDefaultPhotoStream(anyString());
		verify(profileService).findById(anyString());
	}
}
