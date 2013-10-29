package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.Date;

public class ProfileDocumentBuilder {

    private ProfileDocument profileDocument;

    public ProfileDocumentBuilder() {
        profileDocument = new ProfileDocument();
    }

    public ProfileDocumentBuilder id(int id) {
        profileDocument.setId(id);
        return this;
    }

    public ProfileDocumentBuilder documentType(DocumentType documentType) {
        profileDocument.setDocumentType(documentType);
        return this;
    }

    public ProfileDocumentBuilder profile(Profile profile) {
        profileDocument.setProfile(profile);
        return this;
    }

    public ProfileDocumentBuilder name(String name) {
        profileDocument.setName(name);
        return this;
    }

    public ProfileDocumentBuilder startDate(Date startDate) {
        profileDocument.setStartDate(startDate);
        return this;
    }

    public ProfileDocumentBuilder endDate(Date endDate) {
        profileDocument.setEndDate(endDate);
        return this;
    }

    public ProfileDocument build() {
        return profileDocument;
    }
}