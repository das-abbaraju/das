package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ProfileDocumentDAO extends AbstractBaseEntityDAO<ProfileDocument> {

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

	public List<ProfileDocument> findByProfileId(final int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(FIND_DOCUMENTS_BY_PROFILE_ID, ProfileDocument.class);

		query.setParameter("profileId", profileId);

		return query.getResultList();
	}

	public ProfileDocument findByDocumentIdAndProfileId(final int documentId, final int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(FIND_DOCUMENT_BY_DOCUMENT_ID_AND_PROFILE_ID,
				ProfileDocument.class);

		query.setParameter("documentId", documentId);
		query.setParameter("profileId", profileId);

		return query.getSingleResult();
	}

	public List<ProfileDocument> search(final String searchTerm, final int profileId) {
		TypedQuery<ProfileDocument> query = em.createQuery(DOCUMENT_SEARCH, ProfileDocument.class);

		query.setParameter("profileId", profileId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void delete(final int documentId, final int profileId) {
		Query query = em.createQuery("DELETE FROM ProfileDocument pd " +
				"WHERE pd.id = :documentId " +
				"AND pd.profile.id = :profileId");

		query.setParameter("documentId", documentId);
		query.setParameter("profileId", profileId);

		query.executeUpdate();
	}

	public List<ProfileDocument> findProfilePhotos(final Profile profile) {
		TypedQuery<ProfileDocument> query = em.createQuery("SELECT pd FROM ProfileDocument pd " +
				"JOIN pd.profile p " +
				"WHERE p = :profile " +
				"AND pd.documentType = :documentType", ProfileDocument.class);

		query.setParameter("profile", profile);
		query.setParameter("documentType", DocumentType.Photo);

		return query.getResultList();
	}
}
