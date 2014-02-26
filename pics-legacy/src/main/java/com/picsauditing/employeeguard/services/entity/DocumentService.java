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

	@Override
	public ProfileDocument find(Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		return documentDAO.find(id);
	}

	@Override
	public List<ProfileDocument> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return documentDAO.search(searchTerm, accountId);
	}

	@Override
	public ProfileDocument save(ProfileDocument profileDocument, int createdBy, Date createdDate) {
		profileDocument.setCreatedBy(createdBy);
		profileDocument.setCreatedDate(createdDate);
		return documentDAO.save(profileDocument);
	}

	@Override
	public ProfileDocument update(ProfileDocument profileDocument, int updatedBy, Date updatedDate) {
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

	@Override
	public void delete(ProfileDocument profileDocument) {
		documentDAO.delete(profileDocument);
	}

	@Override
	public void deleteById(Integer id) {
		ProfileDocument document = find(id);
		delete(document);
	}
}
