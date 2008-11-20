package com.picsauditing.actions.audits;

import java.io.File;
import java.util.Date;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class OshaSave extends PicsActionSupport implements Preparable {
	private static final long serialVersionUID = 2529955727521548069L;
	private int auditID;
	private int conID;
	private int catDataID;
	private int id;
	private OshaAudit osha;
	private OshaAuditDAO oshaDAO;
	private File uploadFile;
	private String uploadFileFileName;

	public OshaSave(OshaAuditDAO oshaDAO) {
		this.oshaDAO = oshaDAO;
	}

	@Override
	public void prepare() throws Exception {
		id = this.getParameter("id");
		if (id > 0) {
			osha = oshaDAO.find(id);
		}
	}
	
	public String execute() throws Exception {
		if (!forceLogin("Home.action"))
			return LOGIN;
		
		if (button == null) {
			oshaDAO.clear();
			return INPUT;
		}

		if (button.equals("download")) {
			Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext.getServletContext());
			try {
				File[] files = getFiles();
				downloader.download(files[0], null);
				return null;
			} catch (Exception e) {
				addActionError("Failed to download file: " + e.getMessage());
				return BLANK;
			}
		}
		if (button.equals("Delete")) {
			oshaDAO.remove(id);
			return SUCCESS;
		}

		if (button.equals("Add New Location")) {
			osha = new OshaAudit();
			osha.setConAudit(new ContractorAudit());
			osha.getConAudit().setId(auditID);
			osha.setType(OshaType.OSHA);
			osha.setLocation("Division");
			osha.setCreationDate(new Date());
			oshaDAO.save(osha);
			return SUCCESS;
		}
		
		if (button.equals("Delete File")) {
			try {
				// remove all osha files ie (pdf, jpg)
				for(File oldFile : getFiles())
					FileUtils.deleteFile(oldFile);
			} catch (Exception e) {
				addActionError("Failed to save file: " + e.getMessage());
				e.printStackTrace();
				oshaDAO.clear(); // Don't save
				return INPUT;
			}
			osha.setFileUploaded(false);
		}
		
		// TODO verify data is saved correctly
		
		if (uploadFile != null) {
			String ext = uploadFileFileName.substring(uploadFileFileName.lastIndexOf(".") + 1);
	
			if (!FileUtils.checkFileExtension(ext)) {
				addActionError(ext + " is not a valid file type for OSHA logs");
				return INPUT;
			}
	
			try {
				FileUtils.copyFile(uploadFile, getFtpDir(), OshaAudit.OSHA_DIR, getFileName(), ext, true);
			} catch (Exception e) {
				addActionError("Failed to save file: " + e.getMessage());
				e.printStackTrace();
				oshaDAO.clear(); // Don't save
				return INPUT;
			}
			osha.setFileUploaded(true);
		}
		
		//osha.setVerified(false);
		osha.setVerifiedDate(null);
		osha.setUpdateDate(new Date());
		oshaDAO.save(osha);

		return SUCCESS;
	}
	
	private String getFileName() {
		return "osha" + "_" + id;
	}
	
	private File[] getFiles() {
		File oshaDir = new File(getFtpDir() + OshaAudit.OSHA_DIR);
		return FileUtils.getSimilarFiles(oshaDir, getFileName());
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public OshaAudit getOsha() {
		return osha;
	}

	public void setOsha(OshaAudit osha) {
		this.osha = osha;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileFileName() {
		return uploadFileFileName;
	}

	public void setUploadFileFileName(String fileName) {
		this.uploadFileFileName = fileName;
	}

	public void setUploadFileContentType(String temp) {
	}

}
