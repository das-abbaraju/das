package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Report;

@SuppressWarnings("serial")
public class ReportTester extends PicsActionSupport {
	public List<Report> getReports() {
		return dao.findAll(Report.class);
	}
}
