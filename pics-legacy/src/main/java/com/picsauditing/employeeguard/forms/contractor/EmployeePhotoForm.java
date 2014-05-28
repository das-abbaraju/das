package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.forms.PhotoForm;

import java.io.File;

public class EmployeePhotoForm implements PhotoForm {

	private File photo;
	private String photoFileName;
	private String photoContentType;
	private String validate_filename;

	@Override
	public File getPhoto() {
		return photo;
	}

	@Override
	public void setPhoto(File photo) {
		this.photo = photo;
	}

	@Override
	public String getPhotoFileName() {
		return photoFileName;
	}

	@Override
	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	@Override
	public String getPhotoContentType() {
		return photoContentType;
	}

	@Override
	public void setPhotoContentType(String photoContentType) {
		this.photoContentType = photoContentType;
	}

	public String getValidate_filename() {
		return validate_filename;
	}

	public void setValidate_filename(String validate_filename) {
		this.validate_filename = validate_filename;
	}
}
