package com.picsauditing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

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
	static public void moveFile(File f, String ftpDir, String partialPath, String fileName, String extension,
			boolean deleteDuplicates) throws Exception {
		
		File theNewFile = picsUploadLogic(ftpDir, partialPath, fileName,
				extension, deleteDuplicates);

		// finally, do the copy
		if (!f.renameTo(theNewFile))
			throw new Exception("Could not move file to " + theNewFile.getAbsolutePath());
	}

	
	static public void copyFile(File f, String ftpDir, String partialPath, String fileName, String extension,
			boolean deleteDuplicates) throws Exception {
		
		File theNewFile = picsUploadLogic(ftpDir, partialPath, fileName,
				extension, deleteDuplicates);

		// finally, do the copy
		copyFile(f, theNewFile);
	}


	protected static File picsUploadLogic(String ftpDir, String partialPath,
			String fileName, String extension, boolean deleteDuplicates)
			throws FileNotFoundException, Exception {
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
		return theNewFile;
	}
	
	
	
	
	
	
	
	
	static public File[] getSimilarFiles(File folder, final String fileName) {
		File[] fileList = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !(new File(dir, name).isDirectory())
						&& name.substring(0, name.lastIndexOf(".")).equals(fileName);
			}
		});
		if (fileList == null)
			return fileList = new File[0];
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
		String[] validExtensions = { "pdf", "doc", "txt", "xls", "jpg", "gif", "png", "docx", "xlsx" };
		return checkFileExtension(extension, validExtensions);
	}

	static public boolean checkFileExtension(String extension, String[] validExtensions) {
		if (extension == null || extension.equals(""))
			return false;

		extension = extension.toLowerCase();

		boolean valid = false;
		for (String exte : validExtensions) {
			if (exte.equals(extension))
				valid = true;
		}
		if (!valid)
			return false;

		return true;
	}

	static public String size(File file) {
		long length = file.length();
		if (length == 0)
			return "empty or missing";
		if (length > 1000000000)
			return Math.round(length / 1000000000) + " GB";
		if (length > 1000000)
			return Math.round(length / 1000000) + " MB";
		if (length > 1000)
			return Math.round(length / 1000) + " KB";
		return length + " Bytes";
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

	static public void copyFile(File oldFile, File newFile) throws IOException {
		FileInputStream fis = new FileInputStream(oldFile);
		FileOutputStream fos = new FileOutputStream(newFile);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}
	
	static public String readFile(String fileLocation) {
		File file = new File(fileLocation);

		StringBuilder noteText = new StringBuilder();
		try {
			Scanner scanner = new Scanner(file);
			try {
				// first use a Scanner to get each line
				while (scanner.hasNextLine()) {
					noteText.append(scanner.nextLine()).append("\n");
				}
			} finally {
				// ensure the underlying stream is always closed
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			noteText.append("ERROR: failed to find file");
		}
		return noteText.toString();
	}
	
	

	/**
	 * This method will take and int and convert it into a folder structure
	 * where each folder contains up to a thousand subfolders and a thousand 
	 * child files.  This is designed to be used as part of other algorithms
	 * which build the whole path of a file.  <br/><br/><strong>Some examples:</strong><br/>
	 * <table border="1"><tr><th>id</th><th>Expected Result</th></tr>
	 * <tr><td>100</td><td>&nbsp;</td></tr>
	 * <tr><td>1000</td><td>100/</td></tr>
	 * <tr><td>10000</td><td>100/</td></tr> 
	 * <tr><td>100000</td><td>100/</td></tr> 
	 * <tr><td>100001</td><td>100/</td></tr>
	 * <tr><td>100999</td><td>100/</td></tr> 
	 * <tr><td>101000</td><td>101/</td></tr> 
	 * <tr><td>1000000</td><td>100/000/</td></tr> 
	 * <tr><td>2215356</td><td>221/535/</td></tr> 
	 * </table>
	 * @param id the number to be converted into a path
	 * @return String that represents the number converted into a path.
	 */
	public static String thousandize( int id ) {
		
		StringBuilder response = new StringBuilder();
		
		int workingCopy = id;
		String asString = String.valueOf( workingCopy );
		
		while( asString.length() > 3 ) {
			String firstThree = asString.substring(0, 3);
			asString = asString.substring(3);
			
			response.append(firstThree);
			response.append("/");
		}
		
		return response.toString();
	}

}
