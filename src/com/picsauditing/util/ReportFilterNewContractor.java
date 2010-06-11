package com.picsauditing.util;

import java.util.Date;
import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class ReportFilterNewContractor extends ReportFilterContractor {
	// If we need to add any specialized filters?
	protected boolean showOpen = true;
	protected boolean showState = false;
	protected boolean showCountry = false;
	protected boolean showHandledBy = false;
	protected boolean showFollowUpDate = true;
	
	protected int open = 1;
	protected String handledBy;
	protected Date followUpDate;
	
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		
		if (permissions.isOperatorCorporate()) {
			this.setShowHandledBy(true);
			this.setShowConAuditor(true);
		} else if (permissions.hasGroup(981)) {
			// Administrators only?
			this.setShowConAuditor(true);
			this.setShowState(true);
			this.setShowCountry(true);
			this.setShowOperator(true);
		}
	}
	
	// Booleans
	public void setShowOpen(boolean showOpen) {
		this.showOpen = showOpen;
	}

	public boolean isShowOpen() {
		return showOpen;
	}
	
	public boolean isShowState() {
		return showState;
	}
	
	public void setShowState(boolean showState) {
		this.showState = showState;
	}
	
	public boolean isShowCountry() {
		return showCountry;
	}

	public void setShowCountry(boolean showCountry) {
		this.showCountry = showCountry;
	}
	
	public boolean isShowHandledBy() {
		return showHandledBy;
	}
	
	public void setShowHandledBy(boolean showHandledBy) {
		this.showHandledBy = showHandledBy;
	}
	
	public boolean isShowFollowUpDate() {
		return showFollowUpDate;
	}
	
	public void setShowFollowUpDate(boolean showFollowUpDate) {
		this.showFollowUpDate = showFollowUpDate;
	}
	
	// Parameters
	public int getOpen() {
		return open;
	}
	
	public void setOpen(int open) {
		this.open = open;
	}
	
	public String getHandledBy() {
		return handledBy;
	}
	
	public void setHandledBy(String handledBy) {
		this.handledBy = handledBy;
	}
	
	public Date getFollowUpDate() {
		return followUpDate;
	}
	
	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}
	
	// Lists
	public List<State> getStateList() {
		StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
		return stateDAO.findAll();
	}
	
	public List<Country> getCountryList() {
		CountryDAO countryDAO = (CountryDAO) SpringUtils.getBean("CountryDAO");
		return countryDAO.findAll();
	}
	
	public String[] getHandledByList() throws Exception {
		return new String[] { WaitingOn.PICS.name(), WaitingOn.Operator.name() };
	}
}