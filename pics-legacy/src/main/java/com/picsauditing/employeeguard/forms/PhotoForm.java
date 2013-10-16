package com.picsauditing.employeeguard.forms;

import java.io.File;

public interface PhotoForm {
	File getPhoto();

	void setPhoto(File photo);

	String getPhotoFileName();

	void setPhotoFileName(String photoFileName);

	String getPhotoContentType();

	void setPhotoContentType(String photoContentType);
}
