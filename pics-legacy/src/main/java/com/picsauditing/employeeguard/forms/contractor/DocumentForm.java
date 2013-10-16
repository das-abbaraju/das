package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class DocumentForm implements AddAnotherForm {

    private String name;
    private File file;
	private String fileFileName;
	private String fileContentType;
	private int expireYear;
	private int expireMonth;
	private int expireDay;
    private boolean noExpiration;
    private boolean addAnother;

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
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(expireYear, expireMonth - 1, expireDay, 0, 0, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    Date endDate = calendar.getTime();
	    if (noExpiration) {
		    endDate = ProfileDocument.END_OF_TIME;
	    }

        return new ProfileDocumentBuilder().name(name).endDate(endDate).build();
    }

    @Override
    public boolean isAddAnother() {
        return addAnother;
    }

    @Override
    public void setAddAnother(boolean addAnother) {
        this.addAnother = addAnother;
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

	        documentForm.setExpireYear(calendar.get(Calendar.YEAR));
	        documentForm.setExpireMonth(calendar.get(Calendar.MONTH) + 1);
	        documentForm.setExpireDay(calendar.get(Calendar.DAY_OF_MONTH));

            documentForm.setNoExpiration(DateBean.getEndOfTime().equals(profileDocument.getEndDate()) ? true : false);
            return documentForm;
        }
    }
}