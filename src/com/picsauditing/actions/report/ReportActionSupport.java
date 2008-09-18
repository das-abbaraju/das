package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ColorAlternater;

public class ReportActionSupport extends PicsActionSupport {
	protected Report report = new Report();
	protected List<BasicDynaBean> data;

	protected String orderBy;
	protected int showPage;
	protected ColorAlternater color = new ColorAlternater();
	
	protected boolean download = false;
	protected Boolean filtered = null;

	public int getShowPage() {
		return showPage;
	}

	public void setShowPage(int showPage) {
		this.showPage = showPage;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void run(SelectSQL sql) throws SQLException {
		if (download) {
			ServletActionContext.getResponse().setContentType("text/csv");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename=tempfile.csv");
			this.report.setLimit(100000);
			showPage = 1;
		}
		
		isFiltered();
		report.setOrderBy(this.orderBy, null);
		report.setSql(sql);

		if (showPage > 0)
			report.setCurrentPage(showPage);

		data = report.getPage();
	}

	public ColorAlternater getColor() {
		return color;
	}

	public void setColor(ColorAlternater color) {
		this.color = color;
	}

	public boolean isFiltered() {
		for (String filterName : report.getFilters().keySet()) {
			if (report.getFilters().get(filterName).isSet())
				filtered = true;
		}
		if (filtered == null)
			return false;

		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}
}
