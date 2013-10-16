package com.picsauditing.employeeguard.util;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.FileUtils;

import java.io.File;

public class PhotoUtil {
	public static final String[] VALID_PHOTO_EXTENSIONS = new String[]{"jpg", "png", "gif"};

	public static void sendPhotoToFilesDirectory(File photo, String directory, int id, String extension, String filename) throws Exception {
		FileUtils.moveFile(photo, directory, "files/" + FileUtils.thousandize(id), filename, extension, true);
	}

	public static File getPhotoForProfile(final Profile profile, final String directory) {
		ProfileDocument photoDocument = getPhotoDocumentFromProfile(profile);
		if (photoDocument != null) {
			File file = new File(directory + "/files/" + FileUtils.thousandize(profile.getId()) + photoDocument.getFileName());
			if (file.exists()) {
				return file;
			}
		}

		return null;
	}

	public static File getDefaultPhoto(final String directory) {
		File file = new File(directory + "/files/dummy.jpg");
		if (file.exists()) {
			return file;
		}

		return null;
	}

	public static ProfileDocument getPhotoDocumentFromProfile(final Profile profile) {
		for (ProfileDocument profileDocument : profile.getDocuments()) {
			if (profileDocument.getDocumentType() == DocumentType.Photo) {
				return profileDocument;
			}
		}

		return null;
	}

	public static void deleteExistingProfilePhoto(String directory, Profile profile) {
		File file = getPhotoForProfile(profile, directory);
		if (file != null && file.exists()) {
			FileUtils.deleteFile(file);
		}
	}

	public static File getPhotoForEmployee(Employee employee, int accountId, String directory) {
		int id = employee.getId();
		String filename = PICSFileType.employee_photo.filename(id) + "-" + accountId;

		File file;
		for (String extension : VALID_PHOTO_EXTENSIONS) {
			file = new File(directory + "/files/" + FileUtils.thousandize(id) + filename + "." + extension);

			if (file != null && file.exists()) {
				return file;
			}
		}

		return null;
	}

	public static boolean isValidExtension(String extension) {
		return FileUtils.checkFileExtension(extension, VALID_PHOTO_EXTENSIONS);
	}

    public static File getFile(String directory, ProfileDocument document) {
        if (document == null) {
            return null;
        }

        File file = new File(directory + "/files/" + FileUtils.thousandize(document.getProfile().getId())
                + document.getFileName());

        if (file.exists()) {
            return file;
        }

        return null;
    }
}
