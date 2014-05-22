package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.forms.employee.ProfilePhotoForm;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProfileDocumentService {

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired
	private ProfileDocumentDAO profileDocumentDAO;
	@Autowired
	private PhotoUtil photoUtil;

	public ProfileDocument getDocument(final int id) {
		return profileDocumentDAO.find(id);
	}

	public List<ProfileDocument> getDocumentsForProfile(final int profileId) {
		return profileDocumentDAO.findByProfileId(profileId);
	}

	public File getDocumentFile(final ProfileDocument document, final String directory) throws Exception {
		return new File(directory + "/files/" + FileUtils.thousandize(document.getId()) + document.getFileName());
	}

	public ProfileDocument create(final Profile profile, final DocumentForm documentForm, final String directory,
								  final int appUserId, final int skillId) throws Exception {
		ProfileDocument newProfileDocument = create(profile, documentForm, directory, appUserId);

		addAccountSkillProfileForDocument(profile, skillId, newProfileDocument, appUserId);

		return newProfileDocument;
	}

	public ProfileDocument create(final Profile profile, final DocumentForm documentForm, final String directory,
								  final int appUserId) throws Exception {
		ProfileDocument newProfileDocument = documentForm.buildProfileDocument();
		newProfileDocument.setProfile(profile);
		newProfileDocument.setStartDate(new Date());
		newProfileDocument.setDocumentType(DocumentType.Certificate);
		newProfileDocument.setFileName(documentForm.getFileFileName());
		newProfileDocument.setFileType(documentForm.getFileContentType());
		newProfileDocument.setFileSize((int) documentForm.getFile().length());

		EntityHelper.setCreateAuditFields(newProfileDocument, appUserId, new Date());

		newProfileDocument = profileDocumentDAO.save(newProfileDocument);

		String extension = FileUtils.getExtension(documentForm.getFileFileName()).toLowerCase();
		String filename = PICSFileType.profile_certificate.filename(profile.getId()) + "-" + newProfileDocument.getId();
		FileUtils.moveFile(documentForm.getFile(), directory, "files/" + FileUtils.thousandize(profile.getId()), filename, extension, true);

		newProfileDocument.setFileName(filename + "." + extension);
		newProfileDocument = profileDocumentDAO.save(newProfileDocument);

		return newProfileDocument;
	}

	public ProfileDocument update(final int documentId, final Profile profile, final ProfileDocument updatedProfileDocument,
								  final int appUserId, final File file, final String filename, final String directory) throws Exception {
		ProfileDocument profileDocumentFromDatabase =
				profileDocumentDAO.findByDocumentIdAndProfileId(documentId, profile.getId());
		profileDocumentFromDatabase.setEndDate(updatedProfileDocument.getEndDate());
		profileDocumentFromDatabase.setName(updatedProfileDocument.getName());
		profileDocumentFromDatabase.setDocumentType(updatedProfileDocument.getDocumentType());

		if (Strings.isNotEmpty(updatedProfileDocument.getFileName())) {
			profileDocumentFromDatabase.setFileName(updatedProfileDocument.getFileName());
			profileDocumentFromDatabase.setFileType(updatedProfileDocument.getFileType());
			profileDocumentFromDatabase.setFileSize(updatedProfileDocument.getFileSize());
		}

		EntityHelper.setUpdateAuditFields(profileDocumentFromDatabase, appUserId, new Date());
		profileDocumentFromDatabase = profileDocumentDAO.save(profileDocumentFromDatabase);

		if (Strings.isNotEmpty(updatedProfileDocument.getFileName())) {
			String extension = FileUtils.getExtension(profileDocumentFromDatabase.getFileName()).toLowerCase();
			String newFileName = PICSFileType.profile_certificate.filename(profile.getId()) + "-" + profileDocumentFromDatabase.getId();
			FileUtils.moveFile(file, directory, "files/" + FileUtils.thousandize(profile.getId()), newFileName, extension, true);

			profileDocumentFromDatabase.setFileName(newFileName + "." + extension);
			profileDocumentFromDatabase = profileDocumentDAO.save(profileDocumentFromDatabase);
		}

		updateAccountSkillProfiles(profileDocumentFromDatabase);

		return profileDocumentFromDatabase;
	}

	private void updateAccountSkillProfiles(final ProfileDocument profileDocument) {
		List<AccountSkillProfile> accountSkillProfiles = accountSkillProfileDAO.findByProfileDocument(profileDocument);

		if (CollectionUtils.isEmpty(accountSkillProfiles)) {
			return;
		}

		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			accountSkillProfile.setStartDate(profileDocument.getStartDate());
			accountSkillProfile.setEndDate(profileDocument.getEndDate());
		}

		accountSkillProfileDAO.save(accountSkillProfiles);
	}

	private void addAccountSkillProfileForDocument(final Profile profile,
												   final int skillId,
												   final ProfileDocument profileDocument,
												   final int appUserId) {
		AccountSkillProfile accountSkillProfile = accountSkillProfileDAO.findByProfileAndSkillId(profile, skillId);
		if (accountSkillProfile == null) {
			accountSkillProfile = buildAccountSkillProfile(profile, skillId, profileDocument);
		} else {
			accountSkillProfile.setProfileDocument(profileDocument);
			accountSkillProfile.setUpdatedBy(appUserId);
			accountSkillProfile.setStartDate(DateBean.today());
			accountSkillProfile.setUpdatedDate(DateBean.today());
		}

		accountSkillProfileDAO.save(accountSkillProfile);
	}

	private AccountSkillProfile buildAccountSkillProfile(final Profile profile, final int skillId,
														 final ProfileDocument profileDocument) {
		return new AccountSkillProfileBuilder()
				.accountSkill(new AccountSkill(skillId))
				.profile(profile)
				.createdBy(1)
				.createdDate(DateBean.today())
				.profileDocument(profileDocument)
				.startDate(DateBean.today())
				.endDate(profileDocument.getEndDate())
				.build();
	}

	public void delete(final int documentId) {
		ProfileDocument profileDocument = profileDocumentDAO.find(documentId);

		profileDocumentDAO.delete(profileDocument);
	}

	public List<ProfileDocument> search(String searchTerm, int profileId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return profileDocumentDAO.search(searchTerm, profileId);
		}

		return Collections.emptyList();
	}

	public void update(final ProfilePhotoForm profilePhotoForm, final String directory, final Profile profile,
					   final int appUserID) throws Exception {
		String extension = FileUtils.getExtension(profilePhotoForm.getPhotoFileName());
		if (photoUtil.isValidExtension(extension)) {
			Date now = new Date();
			String filename = PICSFileType.profile_photo.filename(profile.getId());
			ProfileDocument profileDocument = getPhotoDocumentFromProfile(profile);

			if (profileDocument == null) {
				profileDocument = new ProfileDocument();
				profileDocument.setCreatedBy(appUserID);
				profileDocument.setCreatedDate(now);
				profileDocument.setDocumentType(DocumentType.Photo);
				profileDocument.setProfile(profile);
			} else {
				profileDocument.setUpdatedBy(appUserID);
				profileDocument.setUpdatedDate(now);
				photoUtil.deleteExistingProfilePhoto(directory, getPhotoDocumentFromProfile(profile));
			}

			profileDocument.setName("Profile photo");
			profileDocument.setFileName(filename + "." + extension);
			profileDocument.setFileType(profilePhotoForm.getPhotoContentType());
			profileDocument.setFileSize((int) profilePhotoForm.getPhoto().length());
			profileDocument.setStartDate(now);
			profileDocument.setEndDate(ProfileDocument.END_OF_TIME);

			photoUtil.sendPhotoToFilesDirectory(profilePhotoForm.getPhoto(), directory, profile.getId(), extension, filename);
			profileDocumentDAO.save(profileDocument);
		}
	}

	public ProfileDocument getPhotoDocumentFromProfile(final Profile profile) {
		List<ProfileDocument> profileDocuments = profileDocumentDAO.findProfilePhotos(profile);

		// It is very unlikely to happen, but since there isn't any restrictions, just return the first
		// profile photo in case there are multiple profile photos.
		if (CollectionUtils.isEmpty(profileDocuments)) {
			return null;
		}

		return profileDocuments.get(0);
	}
}
