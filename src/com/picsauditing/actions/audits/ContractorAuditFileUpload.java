package com.picsauditing.actions.audits;

import java.io.File;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.jboss.util.Strings;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class ContractorAuditFileUpload extends AuditActionSupport {

	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	protected int fileID;
	protected String fileName;
	protected ContractorAuditFile contractorAuditFile = null;
	protected ContractorAuditFileDAO contractorAuditFileDAO;

	public ContractorAuditFileUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, ContractorAuditFileDAO contractorAuditFileDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.contractorAuditFileDAO = contractorAuditFileDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if (button != null) {
			if (fileID > 0) {
				contractorAuditFile = contractorAuditFileDAO.find(fileID);
			}
			
			if(button.equals("Review")) {
				contractorAuditFile.setReviewed(true);
				contractorAuditFile.setAuditColumns(permissions);
				contractorAuditFileDAO.save(contractorAuditFile);
			}
			
			if (fileID > 0 && button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
						.getServletContext());
				try {
					File[] files = getFiles(fileID);
					downloader.download(files[0], contractorAuditFile.getDescription() + "."
							+ contractorAuditFile.getFileType());
					return null;
				} catch (Exception e) {
					addActionError("Failed to download file: " + e.getMessage());
					return SUCCESS;
				}
			}

			if (fileID > 0 && button.startsWith("Delete")) {
				try {
					for (File oldFile : getFiles(fileID))
						FileUtils.deleteFile(oldFile);
				} catch (Exception e) {
					addActionError("Failed to save file: " + e.getMessage());
					e.printStackTrace();
					return SUCCESS;
				}
				contractorAuditFileDAO.remove(contractorAuditFile);
				addActionMessage("Successfully removed file");
			}

			if (button.startsWith("Save")) {
				if (fileID == 0) {
					if (file == null || file.length() == 0) {
						addActionError("File was missing or empty");
						return SUCCESS;
					}
					if (contractorAuditFile == null) {
						contractorAuditFile = new ContractorAuditFile();
						contractorAuditFile.setAudit(conAudit);
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
						if (Strings.isEmpty(fileName))
							contractorAuditFile
									.setDescription(fileFileName.substring(0, fileFileName.lastIndexOf(".")));
					}

					if (!Strings.isEmpty(fileName))
						contractorAuditFile.setDescription(fileName);
					contractorAuditFile.setAuditColumns(permissions);
					contractorAuditFile.setReviewed(false);
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
			if(button.equals("Add")) {
				return "upload";
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

	public List<ContractorAuditFile> getAuditFiles() {
		return contractorAuditFileDAO.findByAudit(conAudit.getId());
	}
}