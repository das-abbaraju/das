package com.picsauditing.employeeguard.util;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.FileSystemAccessor;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.File;

import static junit.framework.Assert.assertNull;

public class PhotoUtilTest {

    public static final String TEST_FILE_NAME = "Test File";
    private PhotoUtil photoUtil;

    @Mock
    private File file;
    @Mock
    private FileSystemAccessor fileSystemAccessor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(file.getName()).thenReturn("Test File");

        doCallRealMethod().when(fileSystemAccessor).thousandize(anyInt());

        photoUtil = new PhotoUtil();
        Whitebox.setInternalState(photoUtil, "fileSystemAccessor", fileSystemAccessor);
    }

    @Test
    public void testGetPhotoDocumentFromProfile() {
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);
        when(file.exists()).thenReturn(true);

        File result = photoUtil.getPhotoForProfile(buildProfileDocument(), Strings.EMPTY_STRING);

        assertEquals("Test File", result.getName());
    }

    @Test
    public void testGetPhotoDocumentFromProfile_FileDoesNotExist() {
        when(file.exists()).thenReturn(false);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        File result = photoUtil.getPhotoForProfile(buildProfileDocument(), Strings.EMPTY_STRING);

        assertNull(result);
    }

    @Test
    public void testDeleteFile() {
        when(file.exists()).thenReturn(true);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        photoUtil.deleteExistingProfilePhoto("/my/directory", buildProfileDocument());

        verify(fileSystemAccessor).deleteFile(file);
    }

    @Test
    public void testDeleteFile_FileNotFound() {
        when(file.exists()).thenReturn(false);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        photoUtil.deleteExistingProfilePhoto("/my/directory", buildProfileDocument());

        verify(fileSystemAccessor, never()).deleteFile(file);
    }

    private ProfileDocument buildProfileDocument() {
        Profile profile = new Profile();
        profile.setId(456);

        ProfileDocument profileDocument = new ProfileDocument();
        profileDocument.setId(123);
        profileDocument.setProfile(profile);
        return profileDocument;
    }

    @Test
    public void testGetPhotoDocumentFromProfile_NullProfileDocument() {
        File result = photoUtil.getPhotoForProfile(null, Strings.EMPTY_STRING);

        assertNull(result);
    }

    @Test
    public void testGetDefaultPhoto() {
        when(file.exists()).thenReturn(true);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        File result = photoUtil.getDefaultPhoto("/some/directory/");

        assertEquals(TEST_FILE_NAME, result.getName());
    }

    @Test
    public void testGetDefaultPhoto_FileNotFound() {
        when(file.exists()).thenReturn(false);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        File result = photoUtil.getDefaultPhoto("/some/directory/");

        assertNull(result);
    }

    @Test
    public void testGetPhotoForEmployee() {
        when(file.exists()).thenReturn(true);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        File result = photoUtil.getPhotoForEmployee(getEmployee(), 1100, "/my/directory");

        assertEquals(TEST_FILE_NAME, result.getName());
    }

    @Test
    public void testGetPhotoForEmployeeNoFileFound() {
        when(file.exists()).thenReturn(false);
        when(fileSystemAccessor.getFile(anyString())).thenReturn(file);

        File result = photoUtil.getPhotoForEmployee(getEmployee(), 1100, "/my/directory");

        assertNull(result);
    }

    private Employee getEmployee() {
        Employee employee = new Employee();
        employee.setId(12345);
        return employee;
    }

    @Test
    public void testGetFilePath() {
        String result = photoUtil.getFilePath("/my/directory", 12241, "my_file", "jpg");

        assertEquals("/my/directory/files/122/my_file.jpg", result);
    }
}
