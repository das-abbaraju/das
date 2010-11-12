package com.picsauditing.actions.audits;

import java.io.File;

import javax.persistence.NoResultException;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.AuditRuleCache;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class AuditDataUpload extends AuditActionSupport implements Preparable {
	private static final long serialVersionUID = 2438788697676816034L;

	protected AuditQuestionDAO questionDAO;

	private String divId;
	private AuditData auditData;
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	private AuditPercentCalculator auditPercentCalculator;

	private int copyDataID = 0;

	public AuditDataUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao, AuditQuestionDAO questionDAO,
			AuditPercentCalculator auditPercentCalculator, AuditRuleCache auditRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditRuleCache);
		this.questionDAO = questionDAO;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	@Override
	public void prepare() throws Exception {
		auditID = this.getParameter("auditData.audit.id");
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if (auditData == null) {
			addActionError("No question supplied for upload");
			return SUCCESS;
		}

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDao.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDao.findAnswerToQuestion(auditID, questionID);
			}
		} catch (NoResultException notReallyAProblem) {
		}

		if (auditData == null) {
			dataID = 0;
			auditData = new AuditData();
			auditData.setAudit(conAudit);
			AuditQuestion question = null;
			if (questionID > 0)
				question = questionDAO.find(questionID);
			if (question == null) {
				addActionError("Failed to find question");
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		if (button != null) {
			if (dataID > 0 && button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
						.getServletContext());
				try {
					File[] files = getFiles(dataID);
					downloader.download(files[0], null);
					return null;
				} catch (Exception e) {
					addActionError("Failed to download file: " + e.getMessage());
					return BLANK;
				}
			}

			if (dataID > 0 && button.startsWith("Delete")) {
				try {
					// remove all files ie (pdf, jpg)
					for (File oldFile : getFiles(dataID))
						FileUtils.deleteFile(oldFile);
				} catch (Exception e) {
					addActionError("Failed to save file: " + e.getMessage());
					e.printStackTrace();
					return INPUT;
				}

				auditDataDao.remove(auditData.getId());

				auditData = new AuditData();
				auditData.setAudit(conAudit);
				AuditQuestion question = null;
				if (questionID > 0)
					question = questionDAO.find(questionID);
				if (question == null) {
					addActionError("Failed to find question");
					return BLANK;
				}
				auditData.setQuestion(question);

				addActionMessage("Successfully removed file");
			}
			if (button.equalsIgnoreCase("Upload File")) {
				if (copyDataID > 0) {
					// COPY FILE
					AuditData toCopy = auditDataDao.find(copyDataID);

					if (toCopy != null) {

						// TODO Check permissions
						for (File toCopyFile : getFiles(copyDataID)) {
							file = toCopyFile;
						}
						fileFileName = file.getName();
					} else {
						addActionError("Could not find data record to copy");
						return BLANK;
					}
				} else {
					// UPLOAD FILE

					if (file == null || file.length() == 0) {
						addActionError("File was missing or empty");
						return SUCCESS;
					}
				}
				String extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
				if (!FileUtils.checkFileExtension(extension)) {
					file = null;
					addActionError("Bad File Extension");
					return SUCCESS;
				}

				auditData.setAnswer(extension);

				auditData.setAuditColumns(permissions);
				auditDataDao.save(auditData);
				dataID = auditData.getId();

				if (copyDataID > 0) {
					FileUtils.copyFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID),
							getFileName(dataID), extension, true);
					addActionMessage("Successfully copied file");
				} else {
					FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID),
							getFileName(dataID), extension, true);
					addActionMessage("Successfully uploaded <b>" + fileFileName + "</b> file");
				}
			}
		}

		if (dataID > 0) {
			File[] files = getFiles(dataID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError("Somehow, two files were uploaded.");
			}
		}

		for (AuditCatData auditCatData : getCategories().values()) {
			if (auditCatData.getCategory() == auditData.getQuestion().getCategory())
				auditPercentCalculator.updatePercentageCompleted(auditCatData);
		}

		return SUCCESS;
	}

	private String getFileName(int dataID) {
		return PICSFileType.data + "_" + dataID;
	}

	private File[] getFiles(int dataID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(dataID));
		return FileUtils.getSimilarFiles(dir, getFileName(dataID));
	}

	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData answer) {
		this.auditData = answer;
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

	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public int getCopyDataID() {
		return copyDataID;
	}

	public void setCopyDataID(int copyDataID) {
		this.copyDataID = copyDataID;
	}

}
