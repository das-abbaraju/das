package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ColorAlternater;
import com.picsauditing.util.excel.ExcelSheet;

@SuppressWarnings("serial")
public class ReportActionSupport extends PicsActionSupport {
	protected Report report = new Report();
	protected ExcelSheet excelSheet = new ExcelSheet();
	protected List<BasicDynaBean> data;

	protected ListType listType;
	protected String orderByDefault = null;
	private String orderBy = null;
	protected int showPage;
	protected ColorAlternater color = new ColorAlternater();
	protected String reportName = null;

	protected boolean download = false;
	protected boolean mailMerge = false;
	private Boolean filtered = null;
	protected boolean filteredDefault = false;

	public void run(SelectSQL sql) throws SQLException, IOException {
		
		if (download) {
			this.report.setLimit(100000);
			showPage = 1;
		}

		if (mailMerge) {
			this.report.setLimit(100000);
			showPage = 1;
		}

		isFiltered();
		if (filtered == null)
			filtered = filteredDefault;
		
		if (orderBy == null && orderByDefault != null)
			orderBy = orderByDefault;
		report.setOrderBy(this.orderBy, orderByDefault);
		report.setSql(sql);

		if (showPage > 0)
			report.setCurrentPage(showPage);

		data = report.getPage();
	}

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

	public boolean filterOn(Object value, Object defaultValue) {
		if (value == null)
			return false;
		if (value.equals(defaultValue))
			return false;
		return value.toString().trim().length() > 0;
	}

	public boolean filterOn(Object value) {
		if (value == null)
			return false;
		return value.toString().trim().length() > 0;
	}
	
	public boolean filterOn(Object[] value) {
		if (value == null)
			return false;
		return value.length > 0;
	}

	public boolean filterOn(int[] value) {
		if (value == null)
			return false;
		if (value.length == 1) {
			if (value[0] == 0)
				return false;
		}
		return value.length > 0;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
}
