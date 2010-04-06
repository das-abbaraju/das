package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobSites extends OperatorActionSupport {
	protected JobSiteDAO siteDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	
	protected int siteID;
	protected JobSite newSite = new JobSite();
	
	public ManageJobSites(OperatorAccountDAO operatorDao, JobSiteDAO siteDAO, JobSiteTaskDAO siteTaskDAO) {
		super(operatorDao);
		this.siteDAO = siteDAO;
		this.siteTaskDAO = siteTaskDAO;
		
		subHeading = "Manage Job Sites";
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
				
		findOperator();
		tryPermissions(OpPerms.ManageJobSites);
		
		if (button != null) {
			if ("Tasks".equalsIgnoreCase(button)) {
				newSite = siteDAO.find(siteID);
				return SUCCESS;
			}
			
			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (Strings.isEmpty(newSite.getLabel()))
					addActionError("Please add a label to this job site.");
				// Operators are required, but if one isn't set,
				// this operator should be added by default
				if (newSite.getOperator() == null && operator != null)
					newSite.setOperator(operator);
				
				if (getActionErrors().size() > 0)
					return SUCCESS;
				
				newSite.setActive(true);
			}
			
			if ("Remove".equalsIgnoreCase(button)) {
				newSite = siteDAO.find(siteID);
				newSite.setActive(false);
			}
			
			if ("Reactivate".equalsIgnoreCase(button)) {
				newSite = siteDAO.find(siteID);
				newSite.setActive(true);
			}
			
			siteDAO.save(newSite);
			
			if (permissions.isOperator())
				return redirect("ManageJobSites.action");
			else
				return redirect("ManageJobSites.action?id=" + operator.getId());
		}
		
		return SUCCESS;
	}
	
	public JobSite getNewSite() {
		return newSite;
	}
	
	public void setNewSite(JobSite newSite) {
		this.newSite = newSite;
	}
	
	public int getSiteID() {
		return siteID;
	}
	
	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}
	
	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.ManageJobSites, OpType.Edit);
	}
	
	public List<JobSite> getActiveSites() {
		return siteDAO.findByOperatorWhere(operator.getId(), "active = 1");
	}
	
	public List<JobSite> getInactiveSites() {
		return siteDAO.findByOperatorWhere(operator.getId(), "active = 0");
	}
	
	public List<JobSiteTask> getTasks(int job) {
		return siteTaskDAO.findByJob(job);
	}
}
