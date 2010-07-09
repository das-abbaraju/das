package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetDateRecords;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetDateRecordsResponse;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetNewRecords;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.KS_GetNewRecordsResponse;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.Record;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetDateRecords;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetDateRecordsResponse;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetNewRecords;
import com.picsauditing.actions.imports.oqsg.ExportServicesStub.T_GetNewRecordsResponse;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageImportData extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	
	private int id;
	private int stageID;
	private Account center;
	private AssessmentResultStage stage;
	private Date start;
	private Date end = new Date();
	private String subHeading = "Imported Data";
	
	// OQSG Web service
	private ExportServicesStub oqsgService;
	private final String oqsgUser = "PICS_Export";
	private final String oqsgPass = "cHA4ufeTR2c2drUjer68aT2eVev5praz"; // Encrypt this somehow???
	
	public ManageImportData(AccountDAO accountDAO, AssessmentTestDAO testDAO) {
		this.accountDAO = accountDAO;
		this.testDAO = testDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (!permissions.isAdmin() && !permissions.isAssessment())
			throw new NoRightsException("Administrator or Assessment Center");
		
		if (permissions.isAssessment() && id != permissions.getAccountId())
			id = permissions.getAccountId();
		
		if (id > 0)
			center = accountDAO.find(id);
		
		if (button != null) {
			if ("Import New Records".equals(button)) {
				try {
					oqsgService = new ExportServicesStub();
					List<Record> all = new ArrayList<Record>();
					
					// Knowledge and Skills
					KS_GetNewRecords ks = new KS_GetNewRecords();
					ks.setUN(oqsgUser);
					ks.setPW(oqsgPass);
					
					KS_GetNewRecordsResponse ksR = oqsgService.KS_GetNewRecords(ks);
					all.addAll(Arrays.asList(ksR.getKS_GetNewRecordsResult().getRecord()));
					
					// Training
					T_GetNewRecords t = new T_GetNewRecords();
					t.setUN(oqsgUser);
					t.setPW(oqsgPass);
					
					T_GetNewRecordsResponse tR = oqsgService.T_GetNewRecords(t);
					all.addAll(Arrays.asList(tR.getT_GetNewRecordsResult().getRecord()));
					
					addOQSGImport(all);
				} catch (Exception e) {
					addActionError("Could not import new records using the OQSG Webservice");
				}
			}
			
			if ("Import By Date".equals(button)) {
				try {
					oqsgService = new ExportServicesStub();
					List<Record> all = new ArrayList<Record>();
					
					// Knowledge and Skills
					KS_GetDateRecords ks = new KS_GetDateRecords();
					ks.setUN(oqsgUser);
					ks.setPW(oqsgPass);
					ks.setStartDate(DateBean.format(start, "MM/dd/yyyy"));
					ks.setEndDate(DateBean.format(end, "MM/dd/yyyy"));
					
					KS_GetDateRecordsResponse ksR = oqsgService.KS_GetDateRecords(ks);
					all.addAll(Arrays.asList(ksR.getKS_GetDateRecordsResult().getRecord()));
					
					// Training
					T_GetDateRecords t = new T_GetDateRecords();
					t.setUN(oqsgUser);
					t.setPW(oqsgPass);
					t.setStartDate(DateBean.format(start, "MM/dd/yyyy"));
					t.setEndDate(DateBean.format(end, "MM/dd/yyyy"));
					
					T_GetDateRecordsResponse tR = oqsgService.T_GetDateRecords(t);
					all.addAll(Arrays.asList(tR.getT_GetDateRecordsResult().getRecord()));
					
					addOQSGImport(all);
				} catch (Exception e) {
					addActionError("Could not import records from " + start.toString() + " to " + 
							end.toString() + " using the OQSG Webservice");
				}
			}
			
			return redirect("ManageImportData.action" + (permissions.isAssessment() ? "" : "?id=" + id));
		}
		
		return SUCCESS;
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getStageID() {
		return stageID;
	}
	
	public void setStageID(int stageID) {
		this.stageID = stageID;
	}
	
	public Account getCenter() {
		return center;
	}
	
	public AssessmentResultStage getStage() {
		return stage;
	}
	
	public void setStage(AssessmentResultStage stage) {
		this.stage = stage;
	}
	
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}
	
	public String getSubHeading() {
		return subHeading;
	}
	
	// DAO access
	public List<AssessmentResultStage> getStaged() {
		return testDAO.findUnmatched(id);
	}
	
	// Private methods
	private void addOQSGImport(List<Record> records) throws Exception {
		AssessmentResultStage ars;
		
		for (Record record : records) {
			ars = new AssessmentResultStage();
			ars.setAuditColumns(permissions);
			ars.setCenter(center);
			ars.setCompanyName(record.getCompany_Name());
			ars.setDescription(record.getCovered_Task_Name());
			ars.setEmployeeID(record.getEmployee_ID());
			ars.setFirstName(record.getUser_First_Name() + 
					(Strings.isEmpty(record.getUser_Middle_Initial()) ? 
							"" : " " + record.getUser_Middle_Initial() + "."));
			ars.setLastName(record.getUser_Last_Name());
			ars.setQualificationDate(DateBean.parseDate(record.getQualification_Date()));
			ars.setQualificationMethod(record.getCovered_Task_Number());
			ars.setQualificationType(record.getQualification_Type());
			ars.setResultID(record.getQualID() + "");
			
			testDAO.save(ars);
		}
	}
}