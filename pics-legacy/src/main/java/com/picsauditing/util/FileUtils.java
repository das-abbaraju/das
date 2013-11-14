package com.picsauditing.util;

import com.picsauditing.actions.PicsActionSupport;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	/**
	 * Move an existing File object to another location. This is NFS safe because it actually copies the new file over and deletes the old one.
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

		File theNewFile = picsUploadLogic(ftpDir, partialPath, fileName, extension, deleteDuplicates);

		// finally, do the copy
		copyFile(f, theNewFile);
		deleteFile(f);
	}

	static public void moveFile(File f, String path) throws Exception {
		if (!f.renameTo(new File(path + f.getName())))
			throw new Exception("Could not move file to " + path);
	}

	static public void copyFile(File f, String ftpDir, String partialPath, String fileName, String extension,
			boolean deleteDuplicates) throws Exception {

		File theNewFile = picsUploadLogic(ftpDir, partialPath, fileName, extension, deleteDuplicates);

		// finally, do the copy
		copyFile(f, theNewFile);
	}

	/**
	 * Prepare the file system to receive a new pics file. Add folder hierarchy, delete existing file(s), and return a file handle
	 * @param ftpDir
	 * @param partialPath
	 * @param fileName
	 * @param extension
	 * @param deleteDuplicates
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	protected static File picsUploadLogic(String ftpDir, String partialPath, String fileName, String extension,
			boolean deleteDuplicates) throws FileNotFoundException, Exception {
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
		logger.debug("Attempting to create/replace {}", theNewFile.getAbsolutePath());

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
			// if (currentLevel.contains(".")) // don't create dir for files
			// break;

			if (currentLevel.length() > 0) {
				path = path + currentLevel;

				thisDir = new File(path);

				if (!thisDir.exists()) {
					if (!thisDir.mkdir()) {
						throw new RuntimeException("unable to create directory: " + thisDir.getAbsolutePath());
					}
				}
			}
			path = path + "/";

		}
		return thisDir;
	}

	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index == -1)
			return "";
		return fileName.substring(index+1);
	}

	static public boolean checkFileExtension(String extension) {
		String[] validExtensions = { "pdf", "doc", "txt", "xls", "jpg", "gif", "png", "docx", "xlsx", "zip", "tif",
				"tiff", "ppt", "pptx" };
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
		return size(file.length());
	}

	static public String size(long length) {
		if (length == 0)
			return "empty or missing";
		if (length > 1000000000)
			return Math.round(length / 100000000f) / 10f + " GB";
		if (length > 1000000)
			return Math.round(length / 100000f) / 10f + " MB";
		if (length > 1000)
			return Math.round(length / 100f) / 10f + " KB";
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
			logger.error("Failed to find file "+fileLocation);
		}
		return noteText.toString();
	}

	/**
	 * This method will take an int and convert it into a folder structure
	 * where each folder contains up to a thousand subfolders and a thousand
	 * child files. This is designed to be used as part of other algorithms
	 * which build the whole path of a file. In other words, it's a sinple
	 * hashing algorithm. See the JUnit test for examples.
	 * 
	 * @param id
	 *            the number to be converted into a path
	 * @return String that represents the number converted into a path.
	 */
	public static String thousandize(int id) {

		StringBuilder response = new StringBuilder();

		int workingCopy = id;
		String asString = String.valueOf(workingCopy);

		while (asString.length() > 3) {
			String firstThree = asString.substring(0, 3);
			asString = asString.substring(3);

			response.append(firstThree);
			response.append("/");
		}

		return response.toString();
	}

	public static String getFileMD5(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			is.close();
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			return bigInt.toString(16);
		} catch (Exception e) {
			logger.error("failed to get MD5 for file: "+file.getName());
			return null;
		}
	}

	public static String getFileSHA(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			InputStream is = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			is.close();
			byte[] sha = digest.digest();
			BigInteger bigInt = new BigInteger(1, sha);
			return bigInt.toString(16);
		} catch (Exception e) {
			logger.error("failed to get fileSHA for file: "+file.getName());
			return null;
		}
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		// If not, close the open file. (Memory Leak!!)
		if (offset < bytes.length) {
			String name = file.getName();
			is.close();
			throw new IOException("Could not completely read file " + name);
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public static File getFileFromBytes(byte[] data) throws IOException {
		File file = new File("Attachment");
		FileOutputStream os = new FileOutputStream(file);

		os.write(data);
		os.flush();
		os.close();
		return file;
	}

	/**
	 * Creates a shell script (according to whatever shell script template is
	 * provided) that could in some way manipulate a file (copy, move, delete,
	 * convert, whatever). Support is provided for the file being manipulated to
	 * be identified by a list of pairings of ID numbers (fromID and toID), and
	 * also for the folder paths to hashed on those numbers (using the
	 * thousandize() method). The template will be invoked once for each pairing
	 * to build a combined script. The template is a Velocity template, so all
	 * Velocity syntax is supported. Bindings are provided for the following
	 * tokens:
	 * 
	 * ${sourceHashFolder} = FileUtils.thousandize(fromID)
	 * 
	 * ${destinationHashFolder} = FileUtils.thousandize(toID)
	 * 
	 * ${fromID} = asgiven
	 * 
	 * ${toID} = as given
	 */
	public static String massManipulateScript(Map<Integer, Integer> pairings, String scriptTemplate) {
		StringBuffer script = new StringBuffer();
		int count = 0;
		VelocityAdaptor adaptor = new VelocityAdaptor();
		for (Integer fromID : pairings.keySet()) {
			if (count++ % 100 == 0) {
				if (count++ % 1000 == 0) {
					System.out.println("");
				}
			}
			Integer toID = pairings.get(fromID);
			script.append(singleManipulateScript(adaptor, fromID, toID, scriptTemplate));
		}
		System.out.println("");

		return script.toString();
	}

	/**
	 * Creates a shell script (according to whatever shell script template is
	 * provided) that could in some way manipulate a file (copy, move, delete,
	 * convert, whatever). Support is provided for the file being manipulated to
	 * be identified by a pair of ID numbers (fromID and toID), and also for the
	 * folder paths to hashed on those numbers (using the thousandize() method).
	 * The template is a Velocity template, so all Velocity syntax is supported.
	 * Bindings are provided for the following tokens:
	 * 
	 * ${sourceHashFolder} = FileUtils.thousandize(fromID)
	 * 
	 * ${destinationHashFolder} = FileUtils.thousandize(toID) 
	 * 
	 * ${fromID} = asgiven 
	 * 
	 * ${toID} = as given
	 */
	public static String singleManipulateScript(VelocityAdaptor adaptor, int fromID, int toID, String scriptTemplate) {
		String template = scriptTemplate;
		Map<String, Object> tokens = new HashMap<String, Object>();
		tokens.put("sourceHashFolder", FileUtils.thousandize(fromID));
		tokens.put("destinationHashFolder", FileUtils.thousandize(toID));
		tokens.put("fromID", fromID);
		tokens.put("toID", toID);
		String script = "";
		try {
			script = adaptor.merge(template, tokens);
		} catch (Exception e) {
			logger.error("adptor failed merge template "+template+" "+tokens.toString());
			// do nothng
		}
		return script;
	}

	public static String getFtpDir() {
		String ftpDir = System.getProperty("pics.ftpDir");
		if (ftpDir != null && ftpDir.length() > 0) {
			return ftpDir;
		}

		try {
			ftpDir = ServletActionContext.getServletContext().getInitParameter("FTP_DIR");
		} catch (Exception exception) {
			// Most likely thrown during testing
			Logger logger = LoggerFactory.getLogger(PicsActionSupport.class);
			logger.error("Error getting ftp dir", exception);
		}

		if (ftpDir != null && ftpDir.length() > 0) {
			return ftpDir;
		}

		return "C:/temp";
	}
}
