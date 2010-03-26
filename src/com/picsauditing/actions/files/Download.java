package com.picsauditing.actions.files;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FileDAO;
import com.picsauditing.jpa.entities.FileBase;

@SuppressWarnings("serial")
public class Download extends PicsActionSupport {

	private InputStream inputStream;

	private FileDAO fileDAO;
	private PICSFileType fileType;
	private int id;

	public Download(FileDAO fileDAO) {
		this.fileDAO = fileDAO;
	}

	@Override
	public String execute() throws Exception {
		if (fileType == null)
			throw new Exception();

		try {
			FileBase file = fileDAO.find(fileType, id);
			switch (fileType) {
			case osha:
				// TODO find the OshaAudit file.getForeignKeyID()

				break;

			default:
				break;
			}
			inputStream = new ByteArrayInputStream(file.getFileData());
			ServletActionContext.getResponse().setContentType(file.getMimeType());
			ServletActionContext.getResponse().setContentLength((int) file.getFileSize());
			ServletActionContext.getResponse().setHeader("Content-Disposition", "inline; filename=\"" + file.getFileName() + "\"");
			ServletActionContext.getResponse().setDateHeader("Last-Modified", file.getModifiedDate().getTime());
			return "stream";
		} catch (Exception e) {
			addActionError("Failed to download file: " + e.getMessage());
			return BLANK;
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public PICSFileType getFileType() {
		return fileType;
	}

	public void setFileType(PICSFileType fileType) {
		this.fileType = fileType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
