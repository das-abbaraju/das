package com.picsauditing.employeeguard.entities.builders;


import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ProfileDocumentBuilderTest {

    private static final int ID = 123;
    private static final DocumentType DOCUMENT_TYPE = DocumentType.Photo;
    private static final Profile PROFILE = new Profile();
    private static final String DOCUMENT_NAME = "My Photo";
    private static final Date START_DATE = new Date();
    private static final Date END_DATE = DateBean.addDays(START_DATE, 7);

    @Test
    public void testBuild() {
        ProfileDocument profileDocument = new ProfileDocumentBuilder().id(ID).profile(PROFILE).name(DOCUMENT_NAME).documentType(DOCUMENT_TYPE).startDate(START_DATE).endDate(END_DATE).build();

        verifyProfileDocument(profileDocument);
    }

    private void verifyProfileDocument(ProfileDocument profileDocument) {
        assertEquals(ID, profileDocument.getId());
        assertEquals(DOCUMENT_TYPE, profileDocument.getDocumentType());
        assertEquals(PROFILE, profileDocument.getProfile());
        assertEquals(DOCUMENT_NAME, profileDocument.getName());
        assertEquals(START_DATE, profileDocument.getStartDate());
        assertEquals(END_DATE, profileDocument.getEndDate());
    }
}
