package com.picsauditing.actions.audits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class AuditDataUpload extends AuditActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	protected AuditQuestionDAO questionDAO;

	private String divId;
	private AuditData answer;
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	private AuditPercentCalculator auditPercentCalculator;

	private int copyDataID = 0;

	public AuditDataUpload(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditQuestionDAO questionDAO,
			AuditPercentCalculator auditPercentCalculator) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.questionDAO = questionDAO;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public List<AuditData> getFileList() {
		List<AuditData> temp = auditDataDao
				.findAnswersByContractorAndUniqueCode(conAudit
						.getContractorAccount().getId(), "policyFile");
		List<AuditData> results = new ArrayList<AuditData>();
		
		for (AuditData ad : temp) {
			if (ad.getParentAnswer() == null) {
				ad.setParentAnswer(new AuditData());
				ad.getParentAnswer().setAnswer("All");
			}
			if (ad.getAudit().getId() != this.auditID) {
				results.add(ad);
			}
		}

		return results;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if (answer == null) {
			addActionError("No question supplied for upload");
			return SUCCESS;
		}

		int dataID = answer.getId();
		int questionID = 0;
		int parentAnswerID = 0;
		if (answer.getQuestion() != null)
			questionID = answer.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				answer = auditDataDao.find(dataID);
			else {
				int auditID = conAudit.getId();
				if (answer.getParentAnswer() != null)
					parentAnswerID = answer.getParentAnswer().getId();

				answer = auditDataDao.findAnswerToQuestion(auditID, questionID,
						parentAnswerID);
			}
		} catch (NoResultException notReallyAProblem) {
		}

		if (answer == null) {
			dataID = 0;
			answer = new AuditData();
			answer.setAudit(conAudit);
			if (parentAnswerID > 0) {
				answer.setParentAnswer(new AuditData());
				answer.getParentAnswer().setId(parentAnswerID);
			}
			AuditQuestion question = null;
			if (questionID > 0)
				question = questionDAO.find(questionID);
			if (question == null) {
				addActionError("Failed to find question");
				return BLANK;
			}
			answer.setQuestion(question);
		} else
			dataID = answer.getId();

		if (button != null) {
			if (dataID > 0 && button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext
						.getResponse(), ServletActionContext
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

				auditDataDao.remove(answer.getId());

				answer = new AuditData();
				answer.setAudit(conAudit);
				AuditQuestion question = null;
				if (questionID > 0)
					question = questionDAO.find(questionID);
				if (question == null) {
					addActionError("Failed to find question");
					return BLANK;
				}
				answer.setQuestion(question);

				addActionMessage("Successfully removed file");
			}
			if (button.startsWith("Save")) {
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
					addActionMessage("Successfully copied file");
				} else {
					// UPLOAD FILE

					if (file == null || file.length() == 0) {
						addActionError("File was missing or empty");
						return SUCCESS;
					}
	
					addActionMessage("Successfully uploaded <b>" + fileFileName
							+ "</b> file");
				}
				String extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
				if (!FileUtils.checkFileExtension(extension)) {
					addActionError("Bad File Extension");
					return SUCCESS;
				}

				answer.setAnswer(extension);

				answer.setAuditColumns(getUser());
				auditDataDao.save(answer);
				dataID = answer.getId();

				if (copyDataID > 0) {
					FileUtils.copyFile(file, getFtpDir(), "files/"
							+ FileUtils.thousandize(dataID), getFileName(dataID),
							extension, true);
				} else {
					FileUtils.moveFile(file, getFtpDir(), "files/"
							+ FileUtils.thousandize(dataID), getFileName(dataID),
							extension, true);
				}
				if( conAudit.getAuditType() != null && conAudit.getAuditType().getClassType() == AuditTypeClass.Policy ) {
					
					if( conAudit.getAuditStatus() == AuditStatus.Active ) {
						conAudit.changeStatus(AuditStatus.Resubmitted, getUser());
						auditDao.save(conAudit);
					}
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

		for (AuditCatData auditCatData : getCategories()) {
			if (auditCatData.getCategory() == answer.getQuestion()
					.getSubCategory().getCategory())
				auditPercentCalculator.updatePercentageCompleted(auditCatData);
		}
		
		return SUCCESS;
	}

	private String getFileName(int dataID) {
		return PICSFileType.data + "_" + dataID;
	}

	private File[] getFiles(int dataID) {
		File dir = new File(getFtpDir() + "/files/"
				+ FileUtils.thousandize(dataID));
		return FileUtils.getSimilarFiles(dir, getFileName(dataID));
	}

	public AuditData getAnswer() {
		return answer;
	}

	public void setAnswer(AuditData answer) {
		this.answer = answer;
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
