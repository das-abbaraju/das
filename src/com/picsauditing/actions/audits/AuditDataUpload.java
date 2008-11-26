package com.picsauditing.actions.audits;

import java.io.File;
import java.util.Date;

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
		question.setAnswer(data);
		int dataID = data.getDataID();
		
		//String fileName = getFileName();
		//String folderName = AuditQuestion.filesFolder + "/qID_" + question.getQuestionID();
		
		if (button != null) {
			if (button.equals("download")) {
				Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext.getServletContext());
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
					for(File oldFile : getFiles(dataID))
						FileUtils.deleteFile(oldFile);
				} catch (Exception e) {
					addActionError("Failed to save file: " + e.getMessage());
					e.printStackTrace();
					return INPUT;
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
				
				FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID), getFileName(dataID), extension, true);
				
				addActionMessage("Successfully uploaded <b>"+fileFileName+"</b> file");
				data.setAnswer(extension);
				
				if (data.getCreationDate() == null)
					data.setCreationDate(new Date());
				if (data.getCreatedBy() == null)
					data.setCreatedBy(this.getUser());
				
				data.setUpdateDate(new Date());
				data.setUpdatedBy(this.getUser());
				
				auditDataDao.save(data);
			}
		}
		
		File[] files = getFiles(dataID);
		if (files != null) {
			if (files.length > 0)
				file = files[0];
			if (files.length > 1)
				addActionError("Somehow, two files were uploaded.");
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
	
	public String getFileSize() {
		return FileUtils.size(file);
	}

}
