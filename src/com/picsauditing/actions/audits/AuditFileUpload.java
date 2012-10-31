package com.picsauditing.actions.audits;

import java.io.File;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

public class AuditFileUpload extends AuditActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	protected int fileID;
	protected String fileName = null;
	private ContractorAuditFile contractorAuditFile = null;
	private int question;
	private String desc;

	@Autowired
	private ContractorAuditFileDAO contractorAuditFileDAO  = null;
	@Autowired
	private AuditQuestionDAO auditQuestionDAO = null;
	
	public String execute() throws Exception {
		this.findConAudit();
		
		if (fileID > 0) {
			contractorAuditFile = contractorAuditFileDAO.find(fileID);
			if (contractorAuditFile != null) {
				fileName = contractorAuditFile.getDescription();
			}
		}
		
		if (button != null) {
			if (fileID > 0 && button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
						.getServletContext());
				try {
					File[] files = getFiles(fileID);
					downloader.download(files[0], contractorAuditFile.getDescription() + "." + contractorAuditFile.getFileType());
					return null;
				} catch (Exception e) {
					addActionError(getText("AuditFileUpload.error.FailedDownload") + e.getMessage());
					return BLANK;
				}
			}

			if (fileID > 0 && button.startsWith("Delete")) {
				if (contractorAuditFile.isReviewed()) {
					addActionError(getText("AuditFileUpload.error.FailedRemove"));
					return SUCCESS;
				}
				try {
					for (File oldFile : getFiles(fileID))
						FileUtils.deleteFile(oldFile);
					fileID = 0;
				} catch (Exception e) {
					addActionError(getText("AuditFileUpload.error.FailedSaved") + e.getMessage());
					e.printStackTrace();
					return SUCCESS;
				}
				contractorAuditFileDAO.remove(contractorAuditFile);
				addActionMessage(getText("AuditFileUpload.message.FileRemoved"));
				return SUCCESS;
			}

			if (button.startsWith("Save")) {
				if (fileID == 0) {
					if (file == null || file.length() == 0) {
						addActionError(getText("AuditFileUpload.error.FileMissing"));
						return SUCCESS;
					}
					if (contractorAuditFile == null) {
						contractorAuditFile = new ContractorAuditFile();
						contractorAuditFile.setAudit(conAudit);
					} 
				}
				String extension = null;
				if (file != null && file.length() > 0) {
					extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
					if (!FileUtils.checkFileExtension(extension)) {
						file = null;
						addActionError(getText("AuditFileUpload.error.BadExtension"));
						return SUCCESS;
					}
					if (contractorAuditFile.getId() > 0) {
						// delete older files
						File[] files = getFiles(contractorAuditFile.getId());
						for (File f : files)
							FileUtils.deleteFile(f);
					}
					contractorAuditFile.setFileType(extension);
				}
				
				if (Strings.isEmpty(fileName))
					fileName = fileFileName;
				
				if (!Strings.isEmpty(desc))
					fileName = desc + " "+ fileName;
				
				contractorAuditFile.setDescription(fileName);
				contractorAuditFile.setAuditColumns(permissions);
				contractorAuditFile = contractorAuditFileDAO.save(contractorAuditFile);

				fileID = contractorAuditFile.getId();

				if (file != null && file.length() > 0) {
					try {
						FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(fileID),
								getFileName(fileID), extension, true);
						addActionMessage(getTextParameterized("AuditFileUpload.message.FileUploaded", fileFileName));
					} catch (Exception e) {
						addActionError(getText("AuditDataUpload.error.FailedSavingFile") + fileFileName);
						contractorAuditFileDAO.remove(contractorAuditFile.getId());
						file = null;
						return SUCCESS;
					}
				}
			}
		}

		if (fileID > 0) {
			File[] files = getFiles(fileID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError(getText("AuditFileUpload.error.TwoFiles"));
			}
		}

		return SUCCESS;
	}

	private String getFileName(int fileID) {
		return PICSFileType.audit + "_" + fileID;
	}

	private File[] getFiles(int fileID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(fileID));
		return FileUtils.getSimilarFiles(dir, getFileName(fileID));
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileSize() {
		return FileUtils.size(file);
	}

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ContractorAuditFile getContractorAuditFile() {
		return contractorAuditFile;
	}

	public void setContractorAuditFile(ContractorAuditFile contractorAuditFile) {
		this.contractorAuditFile = contractorAuditFile;
	}


	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public AuditQuestion getAuditQuestion() {
		if(question > 0) {
			return auditQuestionDAO.find(question);
		}
		return null;
	}	
}
