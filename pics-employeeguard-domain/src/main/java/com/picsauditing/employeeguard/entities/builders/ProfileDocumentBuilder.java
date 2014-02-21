package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.Date;
import java.util.List;

public class ProfileDocumentBuilder extends AbstractBaseEntityBuilder<ProfileDocument, ProfileDocumentBuilder> {


    public ProfileDocumentBuilder() {
        entity = new ProfileDocument();
		that = this;
    }

    public ProfileDocumentBuilder documentType(DocumentType documentType) {
        entity.setDocumentType(documentType);
        return this;
    }

    public ProfileDocumentBuilder profile(Profile profile) {
        entity.setProfile(profile);
        return this;
    }

    public ProfileDocumentBuilder name(String name) {
        entity.setName(name);
        return this;
    }

    public ProfileDocumentBuilder startDate(Date startDate) {
        entity.setStartDate(startDate);
        return this;
    }

    public ProfileDocumentBuilder endDate(Date endDate) {
        entity.setEndDate(endDate);
        return this;
    }

    public ProfileDocumentBuilder fileName(String filename) {
        entity.setFileName(filename);
        return this;
    }

    public ProfileDocumentBuilder fileType(String fileType) {
        entity.setFileType(fileType);
        return this;
    }

    public ProfileDocumentBuilder fileSize(int fileSize) {
        entity.setFileSize(fileSize);
        return this;
    }

    public ProfileDocumentBuilder employeeSkills(List<AccountSkillEmployee> employeeSkills) {
        entity.setEmployeeSkills(employeeSkills);
        return this;
    }

    public ProfileDocument build() {
        return entity;
    }
}
