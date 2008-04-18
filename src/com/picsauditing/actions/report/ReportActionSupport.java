package com.picsauditing.actions.report;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ColorAlternater;

public class ReportActionSupport extends PicsActionSupport {
	@Autowired
	protected Report report = new Report();
	protected List<BasicDynaBean> data;

	protected String startsWith;
	protected String orderBy;
	protected int showPage;
	protected ColorAlternater color = new ColorAlternater();
	
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

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public void run(SelectSQL sql) throws SQLException {
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
}
