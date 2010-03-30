package com.picsauditing.actions.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.FileAttachment;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class FileIndexingEngine extends PicsActionSupport {

	private int limit = 0;
	private int counter = 0;

	private PicsDAO dao;

	public FileIndexingEngine(PicsDAO dao) {
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
		for (File file : directory.listFiles()) {
			counter++;
			if (counter > limit)
				return;
			if (file.isDirectory()) {
				process(file);
			} else {
				System.out.println(new Date() + file.getAbsolutePath());
				try {
					String[] fileName = file.getName().split("[/_/.]");
					if (fileName.length != 3)
						throw new Exception("Invalid format");
					FileAttachment fileBase = new FileAttachment();
					fileBase.setFileHash(FileUtils.getFileSHA(file));
					fileBase.setModifiedDate(new Date(file.lastModified()));
					String extension = fileName[2];
					int foreignKeyID = Integer.parseInt(fileName[1]);
					fileBase.setExtension(extension);
					fileBase.setTableType(PICSFileType.valueOf(fileName[0]));
					fileBase.setForeignKeyID(foreignKeyID);
					fileBase.setFileSize(file.length());

					dao.save(fileBase);
					addActionMessage("Saved file: " + fileBase.getFileName());
				} catch (Exception e) {
					if (file.getAbsolutePath().endsWith("Thumbs.db"))
						file.delete();
					else
						addActionError("Failed to move file: " + file.getAbsolutePath() + " " + e.getMessage());
				}
			}
		}
		if (directory.listFiles().length == 0)
			directory.delete();
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
