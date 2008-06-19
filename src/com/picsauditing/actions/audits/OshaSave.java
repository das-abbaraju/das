package com.picsauditing.actions.audits;

import java.io.File;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OshaLogDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.YesNo;

public class OshaSave extends PicsActionSupport {

	private int auditID;
	private int conID;
	private int catDataID;
	private int oshaID;
	private OshaLog osha;
	private OshaLogDAO oshaDAO;
	private OshaLogYear year1;
	private OshaLogYear year2;
	private OshaLogYear year3;
	private File uploadFile1;
	private File uploadFile2;
	private File uploadFile3;
	private String submit;

	public OshaSave(OshaLogDAO oshaDAO) {
		this.oshaDAO = oshaDAO;
	}

	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		// we could investigate using the prepare statement 
		// and finding the osha data before the setters get run
		// However, I think I had problems setting/creating the 
		// embedded year[1-3] private properties. Trevor
		String description = this.osha.getDescription(); 
		String location = this.osha.getLocation();	
		String oshaType = this.osha.getType().toString();	
		if (oshaID > 0) {
			osha = oshaDAO.find(oshaID);
		} else {
			osha = new OshaLog();
		}
		osha.setDescription(description);
		osha.setLocation(location);
		osha.setType(OshaType.valueOf(oshaType));
		if(submit.equals("Delete")) {
			oshaDAO.remove(oshaID);
			return SUCCESS;
		}
		
		if(submit.equals("Add New Location")) {
			osha = new OshaLog();
			osha.setContractorAccount(new ContractorAccount());
			osha.getContractorAccount().setId(conID);
			osha.setType(OshaType.OSHA);
			osha.setYear1(new OshaLogYear());
			osha.setYear2(new OshaLogYear());
			osha.setYear3(new OshaLogYear());
			oshaDAO.save(osha);
			return SUCCESS;
		}
		merge(osha.getYear1(), year1);
		merge(osha.getYear2(), year2);
		merge(osha.getYear3(), year3);
		
		saveFile(uploadFile1, osha.getYear1(), 1);
		saveFile(uploadFile2, osha.getYear2(), 2);
		saveFile(uploadFile3, osha.getYear3(), 3);

		oshaDAO.save(osha);

		return SUCCESS;
	}

	private boolean saveFile(File file, OshaLogYear logYear, int year) {
		if (file == null)
			return false;
		
		String ext = new javax.activation.MimetypesFileTypeMap().getContentType(file);
		if (ext == null || ext == "")
			return false;
		
		String[] validExtensions = {"pdf","doc","txt","xls","jpg"};
		boolean valid = false;
		for(String exte : validExtensions) {
			if (exte.equals(ext))
				valid = true;
		}
		if (!valid)
			return false;

		String oshaDir = getFtpDir() + "/files/oshas/";
		// TODO create the oshaDir if it doesn't exist
		
		String fileName = "osha" + year + "_" + oshaID;
		String newFileName = oshaDir + fileName + "." + ext;
		File f = new File(newFileName);
		f.delete();
		
		if (file.renameTo(f)) {
			logYear.setFile(YesNo.Yes);
			logYear.setVerified(false);
			logYear.setVerifiedDate(null);
			return true;
		}
		return false;
	}

	private void merge(OshaLogYear oldYear, OshaLogYear newYear) {
		if (oldYear == null) {
			oldYear = newYear;
			return;
		}
		oldYear.setFatalities(newYear.getFatalities());
		oldYear.setInjuryIllnessCases(newYear.getInjuryIllnessCases());
		oldYear.setLostWorkCases(newYear.getLostWorkCases());
		oldYear.setLostWorkDays(newYear.getLostWorkDays());
		oldYear.setManHours(newYear.getManHours());
		oldYear.setRecordableTotal(newYear.getRecordableTotal());
		oldYear.setRestrictedWorkCases(newYear.getRestrictedWorkCases());
		oldYear.setNa(newYear.getNa());
	}

	public OshaLogYear getYear1() {
		return year1;
	}

	public void setYear1(OshaLogYear year1) {
		this.year1 = year1;
	}

	public OshaLogYear getYear2() {
		return year2;
	}

	public void setYear2(OshaLogYear year2) {
		this.year2 = year2;
	}

	public OshaLogYear getYear3() {
		return year3;
	}

	public void setYear3(OshaLogYear year3) {
		this.year3 = year3;
	}

	public void setUploadFile1(File uploadFile1) {
		this.uploadFile1 = uploadFile1;
	}

	public void setUploadFile2(File uploadFile2) {
		this.uploadFile2 = uploadFile2;
	}

	public void setUploadFile3(File uploadFile3) {
		this.uploadFile3 = uploadFile3;
	}

	public OshaLog getOsha() {
		return osha;
	}

	public void setOsha(OshaLog osha) {
		this.osha = osha;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public File getUploadFile1() {
		return uploadFile1;
	}

	public File getUploadFile2() {
		return uploadFile2;
	}

	public File getUploadFile3() {
		return uploadFile3;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
}
