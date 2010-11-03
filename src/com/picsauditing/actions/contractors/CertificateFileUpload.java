package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.Calendar;

import org.apache.struts2.ServletActionContext;
import org.jboss.util.Strings;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class CertificateFileUpload extends ContractorActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	private CertificateDAO certificateDAO = null;
	protected int certID;
	protected String fileName = null;
	private Certificate certificate = null;
	protected int caoID;
	private boolean changed = false;
	
	// Save to audit data
	private AuditQuestionDAO questionDAO;
	private AuditDataDAO dataDAO;
	private int questionID;
	private int auditID;
	
	public CertificateFileUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDAO, AuditQuestionDAO questionDAO, AuditDataDAO dataDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
		this.questionDAO = questionDAO;
		this.dataDAO = dataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findContractor();

		if (certID > 0) {
			certificate = certificateDAO.find(certID);
			if (certificate == null) {
				addActionError("Certificate " + certID + " was not found.");
				return SUCCESS;
			}
		}
		if (button != null) {
			if (certID > 0 && button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
						.getServletContext());
				try {
					File[] files = getFiles(certID);
					downloader.download(files[0], certificate.getDescription() + "." + certificate.getFileType());
					return null;
				} catch (Exception e) {
					addActionError("Failed to download file: " + e.getMessage());
					return BLANK;
				}
			}

			if (certificate != null && button.startsWith("Delete")) {
				try {
					for (File oldFile : getFiles(certID))
						FileUtils.deleteFile(oldFile);
					certID = 0;
					changed = true;
				} catch (Exception e) {
					addActionError("Failed to save file: " + e.getMessage());
					e.printStackTrace();
					return SUCCESS;
				}
				certificateDAO.remove(certificate);
				addActionMessage("Successfully removed file");
				return SUCCESS;
			}

			if (button.startsWith("Save")) {
				if (certID == 0) {
					if (file == null || file.length() == 0) {
						addActionError("File was missing or empty");
						return SUCCESS;
					}
					certificate = certificateDAO.findByFileHash(FileUtils.getFileMD5(file), contractor.getId());
					if (certificate == null) {
						certificate = new Certificate();
						certificate.setContractor(contractor);
					} else {
						certID = certificate.getId();
						addActionMessage("This file has already been uploaded.");
						changed = true;
						return SUCCESS;
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
					if (certificate.getId() > 0) {
						// delete older files
						File[] files = getFiles(certificate.getId());
						for (File f : files)
							FileUtils.deleteFile(f);
					}
					certificate.setFileType(extension);
					certificate.setFileHash(FileUtils.getFileMD5(file));
					if (Strings.isEmpty(fileName))
						certificate.setDescription(fileFileName.substring(0, fileFileName.lastIndexOf(".")));
				}

				if (!Strings.isEmpty(fileName))
					certificate.setDescription(fileName);
				certificate.setAuditColumns(permissions);
				certificate = certificateDAO.save(certificate);

				certID = certificate.getId();

				if (file != null && file.length() > 0) {
					FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(certID),
							getFileName(certID), extension, true);
					addActionMessage("Successfully uploaded <b>" + fileFileName + "</b> file");
				}
				
				if (questionID > 0 && auditID > 0) {
					AuditQuestion q = questionDAO.find(questionID);
					AuditData d = dataDAO.findAnswerByConQuestion(id, questionID);
					
					if (d == null)
						d = new AuditData();
					
					d.setAuditColumns(permissions);
					d.setAnswer(certID + "");
					d.setQuestion(q);
					d.setAudit(auditDao.find(auditID));

					if(d.getAudit().getExpiresDate() == null) {
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.MONTH, 6);
						certificate.setExpirationDate(cal.getTime());
					}
					else if(certificate.getExpirationDate() == null || d.getAudit().getExpiresDate().after(certificate.getExpirationDate())) {
						certificate.setExpirationDate(d.getAudit().getExpiresDate());
					}
					certificateDAO.save(certificate);
					dataDAO.save(d);
				}

				changed = true;
			}
		}

		if (certID > 0) {
			File[] files = getFiles(certID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError("Somehow, two files were uploaded.");
			}
		}

		return SUCCESS;
	}

	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
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

	public int getCertID() {
		return certID;
	}

	public void setCertID(int certID) {
		this.certID = certID;
	}

	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Certificate getCertificate() {
		return certificate;
	}

	public boolean isChanged() {
		return changed;
	}
	
	public int getQuestionID() {
		return questionID;
	}
	
	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}
	
	public int getAuditID() {
		return auditID;
	}
	
	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}
}
