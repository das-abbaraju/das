package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
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

    public ProfileDocument create(final Profile profile, final DocumentForm documentForm, final String directory, final int appUserId) throws Exception {
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
        return profileDocumentDAO.save(newProfileDocument);
    }

    public ProfileDocument update(String documentId, int profileId, ProfileDocument updatedProfileDocument,
                                  int appUserId, File file, String filename, String directory) throws Exception {
        ProfileDocument profileDocumentFromDatabase =
                profileDocumentDAO.findByDocumentIdAndProfileId(NumberUtils.toInt(documentId), profileId);
        profileDocumentFromDatabase.setEndDate(updatedProfileDocument.getEndDate());
        profileDocumentFromDatabase.setName(updatedProfileDocument.getName());
        profileDocumentFromDatabase.setDocumentType(updatedProfileDocument.getDocumentType());

        if (Strings.isNotEmpty(updatedProfileDocument.getFileName())) {
            profileDocumentFromDatabase.setFileName(updatedProfileDocument.getFileName());
            profileDocumentFromDatabase.setFileType(updatedProfileDocument.getFileType());
            profileDocumentFromDatabase.setFileSize(updatedProfileDocument.getFileSize());
        }

        for (AccountSkillEmployee accountSkillEmployee : profileDocumentFromDatabase.getEmployeeSkills()) {
            accountSkillEmployee.setEndDate(profileDocumentFromDatabase.getEndDate());
        }

        EntityHelper.setUpdateAuditFields(profileDocumentFromDatabase, appUserId, new Date());
        profileDocumentFromDatabase = profileDocumentDAO.save(profileDocumentFromDatabase);

        if (Strings.isNotEmpty(updatedProfileDocument.getFileName())) {
            Profile profile = profileDocumentFromDatabase.getProfile();

            String extension = FileUtils.getExtension(profileDocumentFromDatabase.getFileName()).toLowerCase();
            filename = PICSFileType.profile_certificate.filename(profile.getId()) + "-" + profileDocumentFromDatabase.getId();
            FileUtils.moveFile(file, directory, "files/" + FileUtils.thousandize(profile.getId()), filename, extension, true);

            profileDocumentFromDatabase.setFileName(filename + "." + extension);
            profileDocumentFromDatabase = profileDocumentDAO.save(profileDocumentFromDatabase);
        }

        return profileDocumentFromDatabase;
    }

    public void delete(final String documentId, final int profileId, final int appUserId) {
        ProfileDocument document = profileDocumentDAO.findByDocumentIdAndProfileId(NumberUtils.toInt(documentId), profileId);
        removeAccountSkillEmployeeLinkToDocument(document.getEmployeeSkills());
        deleteProfileDocument(document, appUserId);
    }

    private void deleteProfileDocument(ProfileDocument document, int appUserId) {
        EntityHelper.softDelete(document, appUserId);
        profileDocumentDAO.delete(document);
    }

    private void removeAccountSkillEmployeeLinkToDocument(final List<AccountSkillEmployee> employeeSkills) {
        List<AccountSkillEmployee> accountSkillEmployees = removeForeignKeyToDocument(employeeSkills);
        accountSkillEmployeeDAO.save(accountSkillEmployees);
    }

    private List<AccountSkillEmployee> removeForeignKeyToDocument(List<AccountSkillEmployee> accountSkillEmployees) {
        if (CollectionUtils.isEmpty(accountSkillEmployees)) {
            return Collections.emptyList();
        }

        for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
            accountSkillEmployee.setProfileDocument(null);
            accountSkillEmployee.setEndDate(null);
        }

        return accountSkillEmployees;
    }

    public List<ProfileDocument> search(String searchTerm, int profileId) {
        if (Strings.isNotEmpty(searchTerm)) {
            return profileDocumentDAO.search(searchTerm, profileId);
        }

        return Collections.emptyList();
    }

    public void update(final ProfilePhotoForm profilePhotoForm, final String directory, final Profile profile, final int appUserID) throws Exception {
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
        if (profile != null) {
            for (ProfileDocument profileDocument : profile.getDocuments()) {
                if (profileDocument.getDocumentType() == DocumentType.Photo) {
                    return profileDocument;
                }
            }
        }

        return null;
    }
}
