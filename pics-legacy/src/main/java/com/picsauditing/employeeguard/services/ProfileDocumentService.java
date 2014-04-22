package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.employeeguard.forms.employee.ProfilePhotoForm;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProfileDocumentService {

	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private ProfileDocumentDAO profileDocumentDAO;
	@Autowired
	private PhotoUtil photoUtil;

	public ProfileDocument getDocument(String id) {
		return profileDocumentDAO.find(NumberUtils.toInt(id));
	}

	public List<ProfileDocument> getDocumentsForProfile(int profileId) {
		return profileDocumentDAO.findByProfileId(profileId);
	}

	public File getDocumentFile(final ProfileDocument document, final String directory) throws Exception {
		return new File(directory + "/files/" + FileUtils.thousandize(document.getId()) + document.getFileName());
	}

	public ProfileDocument create(final Profile profile, final DocumentForm documentForm, final String directory,
								  final int appUserId, final int skillId) throws Exception {
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

		insertEmployeeSkillsForDocument(profile, skillId, newProfileDocument);

		return newProfileDocument;
	}

	public ProfileDocument update(final String documentId, final Profile profile, final ProfileDocument updatedProfileDocument,
								  final int appUserId, final int skillId, final File file,
								  final String filename, final String directory) throws Exception {
		ProfileDocument profileDocumentFromDatabase =
				profileDocumentDAO.findByDocumentIdAndProfileId(NumberUtils.toInt(documentId), profile.getId());
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

		insertEmployeeSkillsForDocument(profile, skillId, profileDocumentFromDatabase);

		return profileDocumentFromDatabase;
	}

	private void insertEmployeeSkillsForDocument(final Profile profile, final int skillId,
												 final ProfileDocument profileDocument) {
		accountSkillEmployeeDAO.delete(profileDocument);
		accountSkillEmployeeDAO.save(buildAccountSkillEmployees(profile, skillId, profileDocument));
	}

	private List<AccountSkillEmployee> buildAccountSkillEmployees(final Profile profile, final int skillId,
																  final ProfileDocument profileDocument) {
		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();

		List<Employee> employees = profile.getEmployees();
		for (Employee employee : employees) {
			accountSkillEmployees.add(new AccountSkillEmployeeBuilder()
					.accountSkill(new AccountSkill(skillId))
					.employee(employee)
					.profileDocument(profileDocument)
					.startDate(DateBean.today())
					.endDate(profileDocument.getEndDate())
					.build());
		}

		return accountSkillEmployees;
	}

	public void delete(final int documentId, final int profileId) {
		profileDocumentDAO.delete(documentId, profileId);
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
