package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.ProfileDocument;

import javax.persistence.TypedQuery;
import java.util.List;

public class ProfileDocumentDAO extends BaseEntityDAO<ProfileDocument> {
	private static final String FIND_DOCUMENTS_BY_PROFILE_ID = "FROM ProfileDocument d " +
			"WHERE d.profile.id = :profileId";

	private static final String FIND_DOCUMENT_BY_DOCUMENT_ID_AND_PROFILE_ID = "FROM ProfileDocument d " +
			"WHERE d.id = :documentId " +
			"AND d.profile.id = :profileId";

	private static final String DOCUMENT_SEARCH = "FROM ProfileDocument d " +
			"WHERE d.profile.id = :profileId " +
			"AND d.name LIKE :searchTerm";

	public ProfileDocumentDAO() {
		super.type = ProfileDocument.class;
	}

	public List<ProfileDocument> findByProfileId(int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(FIND_DOCUMENTS_BY_PROFILE_ID, ProfileDocument.class);
		query.setParameter("profileId", profileId);
		return query.getResultList();
	}

	public ProfileDocument findByDocumentIdAndProfileId(int documentId, int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(FIND_DOCUMENT_BY_DOCUMENT_ID_AND_PROFILE_ID,
				ProfileDocument.class);
		query.setParameter("documentId", documentId);
		query.setParameter("profileId", profileId);
		return query.getSingleResult();
	}

	public List<ProfileDocument> search(String searchTerm, int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(DOCUMENT_SEARCH, ProfileDocument.class);
		query.setParameter("profileId", profileId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		return query.getResultList();
	}
}
