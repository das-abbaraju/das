package com.picsauditing.actions.audits;

import java.io.File;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
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
	private ContractorAuditFileDAO contractorAuditFileDAO  = null;
	private AuditQuestionDAO auditQuestionDAO = null;
	protected int fileID;
	protected String fileName = null;
	private ContractorAuditFile contractorAuditFile = null;
	private int question;
	private String desc;
	private AuditQuestion auditQuestion;

	public AuditFileUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, ContractorAuditFileDAO contractorAuditFileDAO, AuditQuestionDAO auditQuestionDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.contractorAuditFileDAO = contractorAuditFileDAO;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		if (fileID > 0) {
			contractorAuditFile = contractorAuditFileDAO.find(fileID);
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
					addActionError("Failed to download file: " + e.getMessage());
					return BLANK;
				}
			}

			if (fileID > 0 && button.startsWith("Delete")) {
				if (contractorAuditFile.isReviewed()) {
					addActionError("Failed to remove the file reviewed by the Safety Professional.");
					return SUCCESS;
				}
				try {
					for (File oldFile : getFiles(fileID))
						FileUtils.deleteFile(oldFile);
					fileID = 0;
				} catch (Exception e) {
					addActionError("Failed to save file: " + e.getMessage());
					e.printStackTrace();
					return SUCCESS;
				}
				contractorAuditFileDAO.remove(contractorAuditFile);
				addActionMessage("Successfully removed file");
				return SUCCESS;
			}

			if (button.startsWith("Save")) {
				if (fileID == 0) {
					if (file == null || file.length() == 0) {
						addActionError("File was missing or empty");
						return SUCCESS;
					}
					if(Strings.isEmpty(fileName)) {
						addActionError("Please provide a description for your document");
						file = null;
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
						addActionError("Bad File Extension");
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
				if (!Strings.isEmpty(desc))
					fileName = desc + " "+ fileName;
				
				contractorAuditFile.setDescription(fileName);
				contractorAuditFile.setAuditColumns(permissions);
				contractorAuditFile = contractorAuditFileDAO.save(contractorAuditFile);

				fileID = contractorAuditFile.getId();

				if (file != null && file.length() > 0) {
					FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(fileID),
							getFileName(fileID), extension, true);
					addActionMessage("Successfully uploaded <b>" + fileFileName + "</b> file");
				}
			}
		}

		if (fileID > 0) {
			File[] files = getFiles(fileID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError("Somehow, two files were uploaded.");
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
