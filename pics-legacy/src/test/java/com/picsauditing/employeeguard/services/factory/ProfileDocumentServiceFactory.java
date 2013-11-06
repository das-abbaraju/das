package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class ProfileDocumentServiceFactory {
	private static ProfileDocumentService profileDocumentService = Mockito.mock(ProfileDocumentService.class);
	private static File file = Mockito.mock(File.class);

	public static ProfileDocumentService getProfileDocumentService() throws Exception {
		Mockito.reset(profileDocumentService, file);

		ProfileDocument profileDocument = new ProfileDocumentBuilder().name("Document 1").endDate(new Date()).build();
		List<ProfileDocument> profileDocuments = Arrays.asList(profileDocument, new ProfileDocumentBuilder().name("Document 2").build());
		when(profileDocumentService.getDocumentsForProfile(anyInt())).thenReturn(profileDocuments);
		when(profileDocumentService.getDocument(anyString())).thenReturn(profileDocument);
		when(profileDocumentService.search(anyString(), anyInt())).thenReturn(profileDocuments);
		when(profileDocumentService.getDocumentFile(any(ProfileDocument.class), anyString())).thenReturn(file);
		when(profileDocumentService.create(any(Profile.class), any(DocumentForm.class), anyString(), anyInt())).thenReturn(profileDocument);
		when(profileDocumentService.update(anyString(), anyInt(), any(ProfileDocument.class), anyInt(), any(File.class), anyString(), anyString())).thenReturn(profileDocument);
		when(profileDocumentService.getPhotoDocumentFromProfile(any(Profile.class))).thenReturn(profileDocument);

		return profileDocumentService;
	}
}
