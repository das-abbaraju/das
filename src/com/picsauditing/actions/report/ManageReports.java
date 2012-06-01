package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {
	private ReportUser report;
	private List<ReportUser> reportsByUser = new ArrayList<ReportUser>();

	private String name = "";
	private String description = "";
	private String reportType = "";
	private int id;
	private boolean favorite;

	public String execute() throws Exception {
		super.execute();
		loadPermissions();
		getCustomReport(reportType);
		return SUCCESS;
	}

	private void getCustomReport(String reportType) {
		if (reportType.equals("template")) {
			setReportsByUser(dao.findWhere(ReportUser.class, "createdBy=" + permissions.getUserId(), 2));
		} else if (reportType.equals("favorite")) {
			setReportsByUser(dao.findWhere(ReportUser.class,
					"favorite=1 and createdBy=" + permissions.getUserId()));
		} else if (reportType.equals("saved")) {
			setReportsByUser(dao.findWhere(ReportUser.class, "createdBy=" + permissions.getUserId()));
		} else {
			setReportsByUser(dao.findWhere(ReportUser.class, "createdBy=" + permissions.getUserId()));
		}

	}

	public String deleteReport() throws Exception {
		setReport(dao.find(ReportUser.class, id));
		dao.remove(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public String changeReportName() throws Exception {
		setReport(dao.find(ReportUser.class, id));
		report.getReport().setName(name);
		report.getReport().setDescription(description);
		dao.save(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public String changeFavorite() throws Exception{
		setReport(dao.find(ReportUser.class, id));
		report.setFavorite(favorite);
		dao.save(report);
		getCustomReport(reportType);
		return SUCCESS;
	}

	public String createReport(){
		setReport(dao.find(ReportUser.class, id));

		ReportDynamic dr = new ReportDynamic();
		dr.setReport((Report)report.getReport());
		dr.create();

		getCustomReport(reportType);
		return SUCCESS;
	}

	public ReportUser getReport() {
		return report;
	}

	public void setReport(ReportUser report) {
		this.report = report;
	}

	public void setReportsByUser(List<ReportUser> reportsByUser) {
		this.reportsByUser = reportsByUser;
	}

	public List<ReportUser> getReportsByUser() {
		return reportsByUser;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
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
