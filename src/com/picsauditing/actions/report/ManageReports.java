package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.access.UserAccess;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ManageReports extends PicsActionSupport {
	private User user;
	private List<Report> reportsByUser = new ArrayList<Report>();
	private int reportID;

	@Autowired
	protected UserDAO userDAO;

	public String execute() throws Exception {
		super.execute();
		setUser(permissions.getUserId());
		
		return SUCCESS;
	}
	
	public String deleteReport() throws NoRightsException {
		
		setUser(permissions.getUserId());
		reportsByUser = user.getReports();
		for (int i = 0; i < reportsByUser.size(); i++) {
			if (reportsByUser.get(i).getId() == reportID) {
				// remove from the DB
				dao.remove(reportsByUser.get(i));
				// remove from the list
				reportsByUser.remove(i);
			}
		}
		return SUCCESS;
	}

	private void setUser(int userId) {
		this.user = userDAO.find(userId);
	}

	public User getUser() {
		return user;
	}

	public List<Report> getReportsByUser() {
		return user.getReports();
	}

	public int getReportID() {
		return reportID;
	}

	public void setReportID(int reportID) {
		this.reportID = reportID;
	}

}
