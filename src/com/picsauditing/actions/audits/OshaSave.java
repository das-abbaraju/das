package com.picsauditing.actions.audits;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;

public class OshaSave extends AuditActionSupport implements Preparable {
	private static final long serialVersionUID = 2529955727521548069L;
	private int conID;
	private int catDataID;
	private OshaAudit osha;
	private OshaAuditDAO oshaDAO;
	private File uploadFile;
	private String uploadFileFileName;
	private AuditPercentCalculator auditPercentCalculator;

	public OshaSave(ContractorAccountDAO accountDAO, OshaAuditDAO oshaDAO, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDAO, AuditDataDAO dao, AuditPercentCalculator auditPercentCalculator) {

		super(accountDAO, auditDao, catDataDAO, dao);
		this.oshaDAO = oshaDAO;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	@Override
	public void prepare() throws Exception {
		id = this.getParameter("id");
		if (id > 0) {
			osha = oshaDAO.find(id);
		}
	}

	public String execute() throws Exception {
		if (!forceLogin("Home.action"))
			return LOGIN;

		if (button == null) {
			oshaDAO.clear();
			return INPUT;
		}

		if (button.equals("download")) {
			Downloader downloader = new Downloader(ServletActionContext.getResponse(), ServletActionContext
					.getServletContext());
			try {
				File[] files = getFiles();
				downloader.download(files[0], null);
				return null;
			} catch (Exception e) {
				addActionError("Failed to download file: " + e.getMessage());
				return BLANK;
			}
		}

		if (button.equals("Delete")) {
			try {
				// remove all osha files ie (pdf, jpg)
				for (File oldFile : getFiles())
					FileUtils.deleteFile(oldFile);
			} catch (Exception e) {
				addActionError("Failed to save file: " + e.getMessage());
				e.printStackTrace();
				return INPUT;
			}
			osha.getConAudit().setLastRecalculation(null);
			auditDao.save(osha.getConAudit());
			String note = "Deleted "+ osha.getLocation() +" " + osha.getType() + " log for " + osha.getConAudit().getAuditType().getAuditName() + " " + osha.getConAudit().getAuditFor();
				addNote(osha.getConAudit().getContractorAccount(), note, 
					NoteCategory.Audits, LowMedHigh.Low, false, Account.PicsID, getUser());
			oshaDAO.remove(id);
			return SUCCESS;
		}

		if (button.equals("Delete File")) {
			try {
				// remove all osha files ie (pdf, jpg)
				for (File oldFile : getFiles())
					FileUtils.deleteFile(oldFile);
			} catch (Exception e) {
				addActionError("Failed to save file: " + e.getMessage());
				e.printStackTrace();
				oshaDAO.clear(); // Don't save
				return INPUT;
			}
			osha.setFileUploaded(false);
		}

		if (button.equals("Add New Location")) {
			osha = new OshaAudit();
			osha.setConAudit(new ContractorAudit());
			osha.getConAudit().setId(auditID);

			AuditCatData auditCatData = catDataDao.find(catDataID);
			osha.setType(OshaTypeConverter.getTypeFromCategory(auditCatData.getCategory().getId()));
			osha.setLocation("Division");
			osha.setCreationDate(new Date());
			oshaDAO.save(osha);
			return SUCCESS;
		}

		// TODO verify data is saved correctly

		if (uploadFile != null) {
			String ext = uploadFileFileName.substring(uploadFileFileName.lastIndexOf(".") + 1);

			if (!FileUtils.checkFileExtension(ext)) {
				addActionError(ext + " is not a valid file type for OSHA logs");
				return INPUT;
			}

			try {
				FileUtils.moveFile(uploadFile, getFtpDir(), "files/" + FileUtils.thousandize(id), getFileName(), ext,
						true);
			} catch (Exception e) {
				addActionError("Failed to save file: " + e.getMessage());
				e.printStackTrace();
				oshaDAO.clear(); // Don't save
				return INPUT;
			}
			osha.setFileUploaded(true);
		}

		// osha.setVerified(false);

		if (button.equals("toggleVerify")) {
			if (osha.isVerified()) {
				osha.setVerifiedDate(null);
				osha.setAuditor(null);
			} else {
				osha.setVerifiedDate(new Date());
				osha.setAuditor(getUser());
			}
		} else {
			osha.setVerifiedDate(null);
		}
		
		//AutoPopulating Total OSHA Recordable Injuries and Illnesses
		int recordableTotalCalc = 0;
		recordableTotalCalc = osha.getLostWorkCases() + osha.getInjuryIllnessCases() + osha.getRestrictedWorkCases();
		if(!osha.getType().equals(OshaType.COHS)) { 
			recordableTotalCalc += osha.getFatalities();
		}
		osha.setRecordableTotal(recordableTotalCalc);

		osha.setUpdateDate(new Date());
		oshaDAO.save(osha);

		if (osha.isCorporate()) {
			AuditCatData catData = catDataDao.findAuditCatData(osha.getConAudit().getId(),
					OshaTypeConverter.getCategoryFromType(osha.getType()));
			if (catData != null) {
				auditPercentCalculator.percentOshaComplete(osha, catData);
			}
			auditPercentCalculator.percentCalculateComplete(osha.getConAudit());
			if (!button.equals("toggleVerify")) {
				findConAudit();
				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Resubmitted)) {
						cao.changeStatus(AuditStatus.Resubmitted, permissions);
						auditDao.save(cao);
					}
				}
			}
		}

		return SUCCESS;
	}

	private String getFileName() {
		return PICSFileType.osha.toString() + "_" + id;
	}

	private File[] getFiles() {
		File oshaDir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(id));
		return FileUtils.getSimilarFiles(oshaDir, getFileName());
	}

	public OshaAudit getOsha() {
		return osha;
	}

	public void setOsha(OshaAudit osha) {
		this.osha = osha;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileFileName() {
		return uploadFileFileName;
	}

	public void setUploadFileFileName(String fileName) {
		this.uploadFileFileName = fileName;
	}

	public void setUploadFileContentType(String temp) {
	}

	public ArrayList<String> getOshaProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Contradicting Data");
		list.add("Missing 300");
		list.add("Missing 300a");
		list.add("Incomplete");
		list.add("Incorrect Form");
		list.add("Incorrect Year");
		return list;
	}

}
