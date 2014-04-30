package com.picsauditing.employeeguard.util;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.entities.DocumentType;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.FileSystemAccessor;
import com.picsauditing.util.Strings;
import edu.emory.mathcs.backport.java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PhotoUtil {

	public static final Set<String> VALID_PHOTO_EXTENSIONS =
			Collections.unmodifiableSet(new HashSet<>(Arrays.asList("jpg", "png", "gif", "jpeg")));
	public static final String DEFAULT_PHOTO_FILE = "/files/dummy.jpg";

	@Autowired
	private FileSystemAccessor fileSystemAccessor;

	public void sendPhotoToFilesDirectory(final File photo, final String directory, final int id, final String extension, final String filename) throws Exception {
		fileSystemAccessor.moveFile(photo, directory, "files/" + fileSystemAccessor.thousandize(id), filename, extension, true);
	}

	public File getPhotoForProfile(final ProfileDocument photoDocument, final String directory) {
		if (photoDocument == null) {
			return null;
		}

		String filePath = getFilePath(directory, photoDocument.getProfile().getId(), photoDocument.getFileName());
		File file = fileSystemAccessor.getFile(filePath);
		if (file.exists()) {
			return file;
		}

		return null;
	}

	public File getDefaultPhoto(final String directory) {
		File file = fileSystemAccessor.getFile(directory + DEFAULT_PHOTO_FILE);
		if (file.exists()) {
			return file;
		}

		return null;
	}

	public void deleteExistingProfilePhoto(final String directory, final ProfileDocument photoDocument) {
		File file = getPhotoForProfile(photoDocument, directory);
		if (file != null && file.exists()) {
			fileSystemAccessor.deleteFile(file);
		}
	}

	public File getPhotoForEmployee(final Employee employee, final int accountId, final String directory) {
		int id = employee.getId();
		String filename = PICSFileType.employee_photo.filename(id) + "-" + accountId;

		File file;
		for (String extension : VALID_PHOTO_EXTENSIONS) {
			String pathname = getFilePath(directory, id, filename, extension);
			file = fileSystemAccessor.getFile(pathname);

			if (file != null && file.exists()) {
				return file;
			}
		}

		return null;
	}

	public String getFilePath(final String directory, int id, String filename) {
		return getFilePath(directory, id, filename, Strings.EMPTY_STRING);
	}

	public String getFilePath(final String directory, int id, String filename, String extention) {
		return directory + "/files/" + fileSystemAccessor.thousandize(id) + filename + ((Strings.isEmpty(extention)) ? "" : ("." + extention));
	}

	public boolean isValidExtension(final String extension) {
		return fileSystemAccessor.checkExtentions(extension, VALID_PHOTO_EXTENSIONS);
	}

	public File getFile(final String directory, final ProfileDocument document) {
		if (document == null) {
			return null;
		}

		String pathName = directory + "/files/" + fileSystemAccessor.thousandize(document.getProfile().getId())
				+ document.getFileName();
		File file = fileSystemAccessor.getFile(pathName);

		if (file.exists()) {
			return file;
		}

		return null;
	}

	public InputStream getPhotoStreamForEmployee(final Employee employee, final int accountId, final String directory) throws FileNotFoundException {
		File file = getPhotoForEmployee(employee, accountId, directory);
		return streamPhoto(file);
	}

	public InputStream getPhotoStreamForProfile(final ProfileDocument profileDocument, final String directory) throws FileNotFoundException {
		File file = getPhotoForProfile(profileDocument, directory);
		return streamPhoto(file);
	}

	public InputStream getDefaultPhotoStream(final String directory) throws FileNotFoundException {
		File file = getDefaultPhoto(directory);
		return streamPhoto(file);
	}

	private InputStream streamPhoto(File file) throws FileNotFoundException {
		if (file != null && file.exists()) {
			return new FileInputStream(file);
		}

		return null;
	}

	public boolean photoExistsForEmployee(final Employee employee, final int accountId, final String directory) {
		if (employee == null) {
			return false;
		}

		File file = getPhotoForEmployee(employee, accountId, directory);
		return file != null && file.exists();
	}

	public boolean photoExistsForProfile(final Profile profile, final String directory) {
		if (profile == null) {
			return false;
		}

		for (ProfileDocument profileDocument : profile.getDocuments()) {
			if (profileDocument.getDocumentType() == DocumentType.Photo) {
				File file = getPhotoForProfile(profileDocument, directory);
				return file != null && file.exists();
			}
		}

		return false;
	}
}
