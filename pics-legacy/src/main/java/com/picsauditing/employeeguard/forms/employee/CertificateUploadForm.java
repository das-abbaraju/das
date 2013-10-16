package com.picsauditing.employeeguard.forms.employee;

import java.io.File;
import java.util.Date;

public class CertificateUploadForm {

    private String name;
    private File file;
    private Date expirationDate;
    private boolean doesNotExpire;

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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isDoesNotExpire() {
        return doesNotExpire;
    }

    public void setDoesNotExpire(boolean doesNotExpire) {
        this.doesNotExpire = doesNotExpire;
    }
}
