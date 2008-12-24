package com.picsauditing.actions.audits;

import java.io.File;

import javax.persistence.NoResultException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class AuditDataUpload extends AuditActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	protected AuditQuestionDAO questionDAO;

	private AuditData answer;
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;

	public AuditDataUpload(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, AuditQuestionDAO questionDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.questionDAO = questionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if (answer == null) {
			addActionError("No question supplied for upload");
			return SUCCESS;
		}

		int questionID = answer.getQuestion().getId();
		int dataID = 0;

		try {
			// Try to find the previous version using the passed in auditData
			// record
			int auditID = conAudit.getId();
			int parentAnswerID = answer.getParentAnswer().getId();

			answer = auditDataDao.findAnswerToQuestion(auditID, questionID, parentAnswerID);
		} catch (NoResultException notReallyAProblem) {
		}
		
		if (answer == null) {
			answer = new AuditData();
			answer.setAudit(conAudit);
			AuditQuestion question = questionDAO.find(questionID);
			if (question == null) {
				addActionError("Failed to find question");
				return BLANK;
			}
			answer.setQuestion(question);
			return SUCCESS;
		} else
			dataID = answer.getId();

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

				auditDataDao.remove(answer.getId());
				addActionMessage("Successfully removed file");
			}
			if (button.startsWith("Upload")) {
				if (file == null || file.length() == 0) {
					addActionError("File was missing or empty");
					return SUCCESS;
				}
				String extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);

				if (!FileUtils.checkFileExtension(extension)) {
					addActionError("Bad File Extension");
					return SUCCESS;
				}

				addActionMessage("Successfully uploaded <b>" + fileFileName + "</b> file");
				answer.setAnswer(extension);

				answer.setAuditColumns(getUser());
				auditDataDao.save(answer);
				dataID = answer.getId();

				FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID), getFileName(dataID),
						extension, true);
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

		return SUCCESS;
	}

	private String getFileName(int dataID) {
		return PICSFileType.data + "_" + dataID;
	}

	private File[] getFiles(int dataID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(dataID));
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

}
