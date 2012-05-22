package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {
	private List<Report> reportsByUser = new ArrayList<Report>();
	
	public String execute() throws Exception {
		super.execute();
		
		setReportsByUser(null);
		
		return SUCCESS;
	}
	
	public String deleteReport(int ReportID){
		
		return SUCCESS;
	}
	
	private void setReportsByUser(List<Report> reportsByUser){
		this.reportsByUser = reportsByUser;
	}
	public List<Report> getReportsByUser(){		
		return reportsByUser;			
	}
}
