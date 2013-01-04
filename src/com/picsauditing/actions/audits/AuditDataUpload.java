package com.picsauditing.actions.audits;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.importpqf.ImportPqf;
import com.picsauditing.importpqf.ImportPqfCanQual;
import com.picsauditing.importpqf.ImportPqfComplyWorks;
import com.picsauditing.importpqf.ImportPqfIsn;
import com.picsauditing.importpqf.ImportPqfIsnUs;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.events.AuditDataSaveEvent;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.SpringUtils;

public class AuditDataUpload extends AuditActionSupport implements Preparable {
	private static final long serialVersionUID = 2438788697676816034L;

	@Autowired
	private AuditTypeDAO auditTypeDAO;

	private String divId;
	private AuditData auditData;
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	protected AuditQuestionDAO questionDAO;
	@Autowired
	protected AuditBuilder auditBuilder = null;

	private int copyDataID = 0;
	private String debugLog = null;

	@Override
	public void prepare() throws Exception {
		auditID = this.getParameter("auditData.audit.id");
	}

	public String execute() throws Exception {
		this.findConAudit();

		if (auditData == null) {
			addActionError(getText("AuditDataUpload.error.NoQuestionSupplied"));
			return SUCCESS;
		}

		debugLog = null;

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDAO.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDAO.findAnswerToQuestion(auditID, questionID);
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
				addActionError(getText("AuditDataUpload.error.CantFindQuestion"));
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		if (button != null) {
			if (button.equals("download")) {
				if (dataID > 0) {
					Downloader downloader = new Downloader(ServletActionContext.getResponse(),
							ServletActionContext.getServletContext());
					try {
						File[] files = getFiles(dataID);
						downloader.download(files[0], null);
						return null;
					} catch (Exception e) {
						addActionError(getText("AuditDataUpload.error.FailedDownload") + e.getMessage());
						return BLANK;
					}
				} else {
					addActionError(getText("AuditDataUpload.error.FileDoesntExist"));
					return BLANK;
				}
			}
		}

		if (dataID > 0) {
			File[] files = getFiles(dataID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError(getText("AuditDataUpload.error.TwoFiles"));
			}
		}

		for (AuditCatData auditCatData : getCategories().values()) {
			if (auditCatData.getCategory().equals(auditData.getQuestion().getCategory())) {
				auditPercentCalculator.updatePercentageCompleted(auditCatData);
				auditDao.save(auditCatData);
			}
		}

		if (!hasActionErrors()) {
			SpringUtils.publishEvent(new AuditDataSaveEvent(auditData));
		}

		return SUCCESS;
	}

	public String deleteFile() throws Exception {
		this.findConAudit();

		if (auditData == null) {
			addActionError(getText("AuditDataUpload.error.NoQuestionSupplied"));
			return SUCCESS;
		}

		debugLog = null;

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDAO.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDAO.findAnswerToQuestion(auditID, questionID);
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
				addActionError(getText("AuditDataUpload.error.CantFindQuestion"));
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		if (dataID > 0) {
			try {
				// remove all files ie (pdf, jpg)
				auditData.setAnswer(null);
				auditData.setDateVerified(null);
				auditDataDAO.save(auditData);
				for (File oldFile : getFiles(dataID))
					FileUtils.deleteFile(oldFile);
			} catch (Exception e) {
				addActionError(getText("AuditDataUpload.error.FailedSavingFile") + e.getMessage());
				e.printStackTrace();
				return INPUT;
			}
		}

		return SUCCESS;
	}

	public String uploadFile() throws Exception {
		this.findConAudit();

		if (auditData == null) {
			addActionError(getText("AuditDataUpload.error.NoQuestionSupplied"));
			return SUCCESS;
		}

		debugLog = null;

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDAO.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDAO.findAnswerToQuestion(auditID, questionID);
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
				addActionError(getText("AuditDataUpload.error.CantFindQuestion"));
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		if (copyDataID > 0) {
			// COPY FILE
			AuditData toCopy = auditDataDAO.find(copyDataID);

			if (toCopy != null) {

				// TODO Check permissions
				for (File toCopyFile : getFiles(copyDataID)) {
					file = toCopyFile;
				}
				fileFileName = file.getName();
			} else {
				addActionError(getText("AuditDataUpload.error.CouldNotFindRecord"));
				return BLANK;
			}
		} else {
			// UPLOAD FILE
			if (file == null || file.length() == 0) {
				addActionError(getText("AuditDataUpload.error.FileMissing"));
				return SUCCESS;
			}
		}
		String extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
		if (!FileUtils.checkFileExtension(extension)) {
			file = null;
			addActionError(getText("AuditDataUpload.error.BadExtension"));
			return SUCCESS;
		}

		auditData.setAnswer(extension);
		auditData.setAuditColumns(permissions);
		auditData.setDateVerified(null);

		auditDataDAO.save(auditData);
		dataID = auditData.getId();

		resetCaoIfNeeded(auditData);
		
		try {
			if (copyDataID > 0) {
				FileUtils.copyFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID), getFileName(dataID),
						extension, true);
				addActionMessage(getText("AuditDataUpload.message.CopySuccess"));
			} else {
				FileUtils.moveFile(file, getFtpDir(), "files/" + FileUtils.thousandize(dataID), getFileName(dataID),
						extension, true);
				addActionMessage(this.getTextParameterized("AuditDataUpload.message.UploadSuccess", fileFileName));

				if (conAudit.getAuditType().getId() == AuditType.IMPORT_PQF && extension.toLowerCase().equals("pdf")) {
					ImportPqf importer = getImportPqf();
					if (importer != null) {
						ContractorAudit pqfAudit = getPqfAudit(conAudit.getContractorAccount(), importer.getAuditType());
						if (pqfAudit != null) {
							File[] files = getFiles(dataID);
							importer.calculate(pqfAudit, files[0]);
							debugLog = importer.getLog();
						}
					}
				}
			}
		} catch (Exception e) {
			addActionError(getText("AuditDataUpload.error.FailedSavingFile") + fileFileName);
			auditData.setAnswer("");
			auditDataDAO.save(auditData);
			file = null;
			return SUCCESS;
		}

		if (dataID > 0) {
			File[] files = getFiles(dataID);
			if (files != null) {
				if (files.length > 0)
					file = files[0];
				if (files.length > 1)
					addActionError(getText("AuditDataUpload.error.TwoFiles"));
			}
		}

		for (AuditCatData auditCatData : getCategories().values()) {
			if (auditCatData.getCategory().equals(auditData.getQuestion().getCategory())) {
				auditPercentCalculator.updatePercentageCompleted(auditCatData);
				auditDao.save(auditCatData);
			}
		}

		return SUCCESS;
	}

	private void resetCaoIfNeeded(AuditData auditData) {
		ContractorAudit audit = auditData.getAudit();
		
		auditCategoryRuleCache.initialize(auditRuleDAO);
		AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);
		boolean updateAudit = false;
		
		if (auditData.getQuestion().getId()== AuditQuestion.MANUAL_PQF) {
			for (ContractorAuditOperator cao : audit.getOperators()) {
				Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions())
					operators.add(caop.getOperator());
				builder.calculate(auditData.getAudit(), operators);

				if (cao.getStatus().between(AuditStatus.Submitted, AuditStatus.Complete)
						&& builder.isCategoryApplicable(auditData.getQuestion().getCategory(), cao)) {
					ContractorAuditOperatorWorkflow caow = cao
							.changeStatus(AuditStatus.Incomplete, permissions);
					caow.setNotes("Due to data change");
					caowDAO.save(caow);
					updateAudit = true;
				}
			}
		}
		
		if (updateAudit) {
			AuditData sigQuestion = auditDataDAO.findAnswerByAuditQuestion(audit.getId(), 10217);
			if (sigQuestion != null) {
				sigQuestion.setAnswer("");
				auditDataDAO.save(sigQuestion);
			}
			
			auditDao.save(audit);
		}
	}

	public String downloadFile() throws Exception {
		this.findConAudit();

		if (auditData == null) {
			addActionError(getText("AuditDataUpload.error.NoQuestionSupplied"));
			return SUCCESS;
		}

		debugLog = null;

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDAO.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDAO.findAnswerToQuestion(auditID, questionID);
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
				addActionError(getText("AuditDataUpload.error.CantFindQuestion"));
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		if (dataID > 0) {
			Downloader downloader = new Downloader(ServletActionContext.getResponse(),
					ServletActionContext.getServletContext());
			try {
				File[] files = getFiles(dataID);
				downloader.download(files[0], null);
				return null;
			} catch (Exception e) {
				addActionError(getText("AuditDataUpload.error.FailedDownload") + e.getMessage());
				return BLANK;
			}
		} else {
			addActionError(getText("AuditDataUpload.error.FileDoesntExist"));
			return BLANK;
		}
	}

	private String initialize() throws Exception {
		this.findConAudit();

		if (auditData == null) {
			addActionError(getText("AuditDataUpload.error.NoQuestionSupplied"));
			return SUCCESS;
		}

		debugLog = null;

		int dataID = auditData.getId();
		int questionID = 0;
		if (auditData.getQuestion() != null)
			questionID = auditData.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			if (dataID > 0)
				auditData = auditDataDAO.find(dataID);
			else {
				int auditID = conAudit.getId();
				auditData = auditDataDAO.findAnswerToQuestion(auditID, questionID);
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
				addActionError(getText("AuditDataUpload.error.CantFindQuestion"));
				return BLANK;
			}
			auditData.setQuestion(question);
		} else
			dataID = auditData.getId();

		return null;
	}

	private String postprocess() {
		return null;
	}

	private ContractorAudit getPqfAudit(ContractorAccount contractor, int auditId) {
		ContractorAudit audit = null;

		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (conAudit.getAuditType().getId() == auditId) {
				audit = conAudit;
				break;
			}
		}

		if (audit == null) {
			AuditType auditType = auditTypeDAO.find(auditId);
			if (auditType == null) {
				return null;
			}
			audit = new ContractorAudit();
			audit.setContractorAccount(contractor);
			audit.setAuditType(auditType);
			audit.setManuallyAdded(true);
			conAudit.setManuallyAdded(true);
			audit.setAuditColumns(new User(User.SYSTEM));
			contractor.getAudits().add(audit);
			auditDao.save(conAudit);
			contractorAccountDao.save(contractor);
		}

		auditBuilder.buildAudits(contractor);

		return audit;
	}

	private ImportPqf getImportPqf() {
		ImportPqf importPqf = null;

		AuditData data = auditDataDAO.findAnswerByConQuestion(getConAudit().getContractorAccount().getId(), 7727);

		if (data != null && data.isAnswered()) {
			if (data.getAnswer().equals("514")) // CanQual
				importPqf = new ImportPqfCanQual();
			else if (data.getAnswer().equals("518")) // ISN Canada
				importPqf = new ImportPqfIsn();
			else if (data.getAnswer().equals("515")) // ComplyWorks
				importPqf = new ImportPqfComplyWorks();
			else if (data.getAnswer().equals("1040")) // ISN US
				importPqf = new ImportPqfIsnUs();
		}

		return importPqf;
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

	public String getDebugLog() {
		return debugLog;
	}

	public void setDebugLog(String debugLog) {
		this.debugLog = debugLog;
	}

}
