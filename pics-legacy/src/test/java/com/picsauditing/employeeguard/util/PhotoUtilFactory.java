package com.picsauditing.employeeguard.util;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import org.mockito.Mockito;

import java.io.File;
import java.io.InputStream;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class PhotoUtilFactory {
	private static PhotoUtil photoUtil = Mockito.mock(PhotoUtil.class);
	private static File file = Mockito.mock(File.class);
	private static InputStream inputStream = Mockito.mock(InputStream.class);

	public static PhotoUtil getPhotoUtil() throws Exception {
		Mockito.reset(photoUtil, file, inputStream);

		when(file.exists()).thenReturn(true);
		when(file.length()).thenReturn(1l);
		when(file.getPath()).thenReturn("name");
		when(photoUtil.getDefaultPhoto(anyString())).thenReturn(file);
		when(photoUtil.getPhotoForEmployee(any(Employee.class), anyInt(), anyString())).thenReturn(file);
		when(photoUtil.getPhotoForProfile(any(ProfileDocument.class), anyString())).thenReturn(file);
		when(photoUtil.getDefaultPhotoStream(anyString())).thenReturn(inputStream);
		when(photoUtil.getPhotoStreamForEmployee(any(Employee.class), anyInt(), anyString())).thenReturn(inputStream);
		when(photoUtil.getPhotoStreamForProfile(any(ProfileDocument.class), anyString())).thenReturn(inputStream);

		return photoUtil;
	}
}
