package com.picsauditing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

public class FileUtils {
	/**
	 * Save an existing File object to the file system at a particular location
	 * 
	 * @param f
	 *            file handle for the file we want to copy
	 * @param root
	 *            The FTP-DIR from web context
	 * @param partialPath
	 *            remaining part of the file path to put file into
	 * @param deleteDuplicates
	 *            Set to true if you want to remove files with the same name but
	 *            different extensions
	 * @return
	 */
	static public void copyFile(File f, String ftpDir, String partialPath, String fileName, String extension,
			boolean deleteDuplicates) throws Exception {
		File rootFile = new File(ftpDir);

		if (rootFile == null || !rootFile.exists())
			throw new FileNotFoundException("Can't copy file: directory (" + ftpDir + ") does not exist");

		if (partialPath.charAt(0) == '/')
			partialPath = partialPath.substring(1);

		// make sure the folder exists
		String parentFolderString = rootFile.getAbsolutePath() + "/" + partialPath;
		File parentFolder = ensurePathExists(parentFolderString);

		if (parentFolder == null || !parentFolder.exists())
			throw new FileNotFoundException("Can't copy file: directory (" + parentFolderString + ") can't be created");

		File theNewFile = new File(parentFolderString + "/" + fileName + "." + extension);
		System.out.println("Attempting to create/replace " + theNewFile.getAbsolutePath());

		if (deleteDuplicates) {
			// Delete all files with same name but different extensions
			File[] deleteList = getSimilarFiles(parentFolder, fileName);

			for (File toDelete : deleteList) {
				toDelete.delete();
			}
		} else {
			// if the file exists, delete it
			if (theNewFile.exists() && !theNewFile.delete())
				throw new Exception("unable to delete existing file");
		}

		// finally, do the copy
		if (!f.renameTo(theNewFile))
			throw new Exception("Could not move file to " + theNewFile.getAbsolutePath());
	}

	static public File[] getSimilarFiles(File folder, final String fileName) {
		File[] fileList = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !(new File(dir, name).isDirectory())
						&& name.substring(0, name.lastIndexOf(".")).equals(fileName);
			}
		});
		return fileList;
	}

	static public File ensurePathExists(String newPath) {
		String[] folders = newPath.split("/");
		String path = "";
		File thisDir = null;

		for (int i = 0; i < folders.length; i++) {
			String currentLevel = folders[i];
			if (currentLevel.contains(".")) // don't create dir for files
				break;

			if (currentLevel.length() > 0) {
				path = path + currentLevel;

				thisDir = new File(path);

				if (!thisDir.exists()) {
					if (!thisDir.mkdir()) {
						return null;
					}
				}
				path = path + "/";
			}
		}
		return thisDir;
	}

	static public boolean checkFileExtension(String extension) {
		if (extension == null || extension.equals(""))
			return false;

		extension = extension.toLowerCase();

		String[] validExtensions = { "pdf", "doc", "txt", "xls", "jpg", "gif", "png" };
		boolean valid = false;
		for (String exte : validExtensions) {
			if (exte.equals(extension))
				valid = true;
		}
		if (!valid)
			return false;

		return true;
	}

	/**
	 * Delete a file if it exists. Does not work on directories
	 * 
	 * @param path
	 * @return return false if it isn't able to delete the file
	 */
	static public boolean deleteFile(String path) {
		File fileToDelete = new File(path);
		if (deleteFile(fileToDelete))
			return true;
		return false;
	}

	static public boolean deleteFile(File fileToDelete) {
		if (fileToDelete.exists()) {
			if (!fileToDelete.isFile())
				return false;
			return fileToDelete.delete();
		}
		return true;
	}
}
