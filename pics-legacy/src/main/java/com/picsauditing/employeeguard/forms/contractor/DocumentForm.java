package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class DocumentForm implements AddAnotherForm, DuplicateInfoProvider {

	private int id;
	private String name;
	private File file;
	private String fileFileName;
	private String fileContentType;
	private String validate_filename;
	private int expireYear;
	private int expireMonth;
	private int expireDay;
	private boolean noExpiration;
	private boolean addAnother;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getValidate_filename() {
		return validate_filename;
	}

	public void setValidate_filename(String validate_filename) {
		this.validate_filename = validate_filename;
	}

	public int getExpireYear() {
		return expireYear;
	}

	public void setExpireYear(int expireYear) {
		this.expireYear = expireYear;
	}

	public int getExpireMonth() {
		return expireMonth;
	}

	public void setExpireMonth(int expireMonth) {
		this.expireMonth = expireMonth;
	}

	public int getExpireDay() {
		return expireDay;
	}

	public void setExpireDay(int expireDay) {
		this.expireDay = expireDay;
	}

	public boolean isNoExpiration() {
		return noExpiration;
	}

	public void setNoExpiration(boolean noExpiration) {
		this.noExpiration = noExpiration;
	}

	public ProfileDocument buildProfileDocument() {
		Date endDate = ProfileDocument.END_OF_TIME;
		if (!noExpiration) {
			endDate = getEndDateFromForm();
		}

		return new ProfileDocumentBuilder().name(name).fileName(fileFileName).fileType(fileContentType)
				.fileSize(file == null ? 0 : (int) file.length()).endDate(endDate).build();
	}

	private Date getEndDateFromForm() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(expireYear, expireMonth - 1, expireDay, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	@Override
	public boolean isAddAnother() {
		return addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
		this.addAnother = addAnother;
	}

	@Override
	public UniqueIndexable getUniqueIndexable() {
		return new ProfileDocument.ProfileDocumentUniqueIndex(id,
				SessionInfoProviderFactory.getSessionInfoProvider().getAppUserId(), name);
	}

	@Override
	public Class<?> getType() {
		return ProfileDocument.class;
	}

	public static class Builder {
		private ProfileDocument profileDocument;
		private File file;

		public Builder profileDocument(final ProfileDocument profileDocument) {
			this.profileDocument = profileDocument;
			return this;
		}

		public Builder profileDocument(final ProfileDocument profileDocument, final File file) {
			this.profileDocument = profileDocument;
			this.file = file;
			return this;
		}

		public DocumentForm build() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(profileDocument.getEndDate());

			DocumentForm documentForm = new DocumentForm();
			documentForm.setName(profileDocument.getName());
			documentForm.setFile(file);
			documentForm.setFileFileName(profileDocument.getFileName());
			documentForm.setFileContentType(profileDocument.getFileType());

			if (calendar.get(Calendar.YEAR) > 3000) {
				documentForm.setNoExpiration(true);
			} else {
				documentForm.setNoExpiration(false);
				documentForm.setExpireYear(calendar.get(Calendar.YEAR));
				documentForm.setExpireMonth(calendar.get(Calendar.MONTH) + 1);
				documentForm.setExpireDay(calendar.get(Calendar.DAY_OF_MONTH));
			}

			return documentForm;
		}
	}
}