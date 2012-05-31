package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ReportUserReport;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {
	private ReportUserReport report;
	private List<ReportUserReport> reportsByUser = new ArrayList<ReportUserReport>();

	private String name = "";
	private String reportType = "";
	private int id;
	private boolean favorite;

	public String execute() throws Exception {
		super.execute();
		//loadPermissions();
		getCustomReport(reportType);
		return SUCCESS;
	}

	private void getCustomReport(String reportType) {
		if (reportType.equals("template")) {
			setReportsByUser(dao.findWhere(ReportUserReport.class, "createdBy=" + permissions.getUserId(), 2));
		} else if (reportType.equals("favorite")) {
			setReportsByUser(dao.findWhere(ReportUserReport.class,
					"favorite=1 and createdBy=" + permissions.getUserId()));
		} else if (reportType.equals("saved")) {
			setReportsByUser(dao.findWhere(ReportUserReport.class, "createdBy=" + permissions.getUserId()));
		} else {
			setReportsByUser(dao.findWhere(ReportUserReport.class, "createdBy=" + permissions.getUserId()));
		}

	}

	public String deleteReport() throws Exception {
		setReport(dao.find(ReportUserReport.class, id));
		//ensureValidReport();
		dao.remove(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public String changeReportName() throws Exception {
		setReport(dao.find(ReportUserReport.class, id));
		//ensureValidReport();
		report.getReport().setName(name);
		dao.save(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public String changeFavorite() throws Exception{
		setReport(dao.find(ReportUserReport.class, id));
		//ensureValidReport();
		report.setFavorite(favorite);
		dao.save(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public ReportUserReport getReport() {
		return report;
	}

	public void setReport(ReportUserReport report) {
		this.report = report;
	}

	public void setReportsByUser(List<ReportUserReport> reportsByUser) {
		this.reportsByUser = reportsByUser;
	}

	public List<ReportUserReport> getReportsByUser() {
		return reportsByUser;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFavorite(){
		return favorite;
	}

	public void setFavorite(boolean favorite){
		this.favorite = favorite;
	}
}
