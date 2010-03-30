package com.picsauditing.actions.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FileDAO;
import com.picsauditing.jpa.entities.FileAttachment;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class FileIndexingEngine extends PicsActionSupport {

	private FileDAO dao;

	public FileIndexingEngine(FileDAO dao) {
		this.dao = dao;
	}

	@Override
	public String execute() throws Exception {
		File root = new File(getFtpDir());

		if (root == null || !root.exists())
			throw new FileNotFoundException("FtpDir: (" + getFtpDir() + ") does not exist");
		process(root);
		return BLANK;
	}

	private void process(File directory) {
		Map<String, FileAttachment> originals = dao.findByDirectory(directory.getAbsolutePath());
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				process(file);
			} else {
				System.out.println(new Date() + file.getAbsolutePath());
				try {
					String[] fileName = file.getName().split("[/_/.]");
					if (fileName.length != 3)
						throw new Exception("Invalid format");
					FileAttachment originalAttachment = originals.get(file.getName());
					if (originalAttachment == null
							|| !originalAttachment.getModifiedDate().equals(new Date(file.lastModified()))
							|| originalAttachment.getFileSize() != file.length()) {
						FileAttachment fileBase = new FileAttachment();
						fileBase.setModifiedDate(new Date(file.lastModified()));
						String extension = fileName[2];
						int foreignKeyID = Integer.parseInt(fileName[1]);
						fileBase.setExtension(extension);
						fileBase.setTableType(PICSFileType.valueOf(fileName[0]));
						fileBase.setForeignKeyID(foreignKeyID);
						fileBase.setFileSize(file.length());
						fileBase.setFileName(file.getName());
						fileBase.setDirectory(file.getParent());
						fileBase.setFileHash(FileUtils.getFileSHA(file));

						dao.save(fileBase);
						originals.remove(file.getName());
					}
				} catch (Exception e) {
				}
			}
		}

		for (FileAttachment originalAttachment : originals.values()) {
			dao.remove(originalAttachment);
		}
	}

}
