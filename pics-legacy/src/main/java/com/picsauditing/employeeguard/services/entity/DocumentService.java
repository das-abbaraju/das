package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DocumentService implements EntityService<ProfileDocument, Integer>, Searchable<ProfileDocument> {

	@Autowired
	private ProfileDocumentDAO documentDAO;

	/* All Find Methods */

	@Override
	public ProfileDocument find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		return documentDAO.find(id);
	}

	/* All Search Methods */

	@Override
	public List<ProfileDocument> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return documentDAO.search(searchTerm, accountId);
	}

	/* All Save Methods */

	@Override
	public ProfileDocument save(ProfileDocument profileDocument, final int createdBy, final Date createdDate) {
		profileDocument.setCreatedBy(createdBy);
		profileDocument.setCreatedDate(createdDate);
		return documentDAO.save(profileDocument);
	}

	/* All Update Methods */

	@Override
	public ProfileDocument update(ProfileDocument profileDocument, final int updatedBy, final Date updatedDate) {
		ProfileDocument profileDocumentToUpdate = find(profileDocument.getId());

		profileDocumentToUpdate.setName(profileDocument.getName());
		profileDocumentToUpdate.setDocumentType(profileDocument.getDocumentType());
		profileDocumentToUpdate.setEndDate(profileDocument.getEndDate());
		profileDocumentToUpdate.setStartDate(profileDocument.getStartDate());
		profileDocumentToUpdate.setFileName(profileDocument.getFileName());
		profileDocumentToUpdate.setFileSize(profileDocument.getFileSize());
		profileDocumentToUpdate.setFileType(profileDocument.getFileType());
		profileDocumentToUpdate.setUpdatedBy(updatedBy);
		profileDocumentToUpdate.setUpdatedDate(updatedDate);

		return profileDocumentToUpdate;
	}

	/* All Delete Methods */

	@Override
	public void delete(final ProfileDocument profileDocument) {
		if (profileDocument == null) {
			throw new NullPointerException("profileDocument cannot be null");
		}

		documentDAO.delete(profileDocument);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		ProfileDocument document = find(id);
		delete(document);
	}
}
