package com.picsauditing.actions.audits;

import java.io.File;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.FileUtils;

public class AuditDataUpload extends AuditActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;
	
	protected AuditQuestionDAO questionDAO;
	
	private AuditQuestion question;
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
		
		if (question == null) {
			addActionError("No question supplied for upload");
			return SUCCESS;
		}
		question = questionDAO.find(question.getQuestionID());
		if (question == null) {
			addActionError("Failed to find question");
			return SUCCESS;
		}
		
		AuditData data = auditDataDao.findAnswerToQuestion(conAudit.getId(), question.getQuestionID());
		if (data == null) {
			// Add a new answer
			data = new AuditData();
			data.setQuestion(question);
			data.setAudit(conAudit);
		}
		
		if (button != null) {
			String fileName = question.getQuestionID() + "_" + contractor.getId();
			String folderName = "files/pqf/qID_" + question.getQuestionID();
			
			if (data.getDataID() > 0 && button.startsWith("Delete")) {
				// Delete all files with same name but different extensions
				File parentFolder = new File(getFtpDir() + "/" + folderName);
				File[] deleteList = FileUtils.getSimilarFiles(parentFolder, fileName);
				for (File toDelete : deleteList) {
					if (!toDelete.delete()) {
						addActionError("Could not delete file " + toDelete.getName());
						return SUCCESS;
					}
				}
				auditDataDao.remove(data.getDataID());
				addActionMessage("Successfully removed file");
			}
			if (button.startsWith("Upload")) {
				if( file == null || file.length() == 0 ) {
					addActionError("File was missing or empty");
					return SUCCESS;
				}
				String extension = fileFileName.substring( fileFileName.lastIndexOf(".") + 1 );
				
				if(!FileUtils.checkFileExtension(extension)) {
					addActionError("Bad File Extension");
					return SUCCESS;
				}
				
				FileUtils.copyFile(file, getFtpDir(), folderName, fileName, extension, true);
				
				// TODO save auditdata
				addActionMessage("Successfully uploaded file");
				data.setAnswer(extension);
				auditDataDao.save(data);
			}
		}
		
		return SUCCESS;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
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

}
