package com.picsauditing.actions;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.jpa.entities.EmailAttachment;

public class Download extends PicsActionSupport {
	private EmailAttachmentDAO emailAttachmentDAO;

	public Download(EmailAttachmentDAO emailAttachmentDAO) {
		this.emailAttachmentDAO = emailAttachmentDAO;
	}

	private int id = 0;

	public String execute() throws IOException {
		EmailAttachment emailAttachment = emailAttachmentDAO.find(id);
		byte[] fileData = emailAttachment.getContent();
		String filename = emailAttachment.getFileName();
		// File f = new File("");
		// new MimetypesFileTypeMap().getContentType(f);
		// ServletActionContext.getResponse().setContentType("application/pdf");
		ServletActionContext.getResponse().setContentType("application/octet-stream");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletActionContext.getResponse().setContentLength(fileData.length);
		ServletActionContext.getResponse().getOutputStream().write(fileData);
		ServletActionContext.getResponse().setBufferSize(1024);
		
		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
