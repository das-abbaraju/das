package com.picsauditing.util;

import java.util.Date;
import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class ReportFilterNewContractor extends ReportFilterContractor {
	protected boolean showOpen = true;
	protected boolean showHandledBy = true;
	protected boolean showFollowUpDate = true;
	protected boolean showViewAll = false;
	protected boolean showCreationDate = true;
	protected boolean showRequestStatus = true;

	protected int open = 1;
	protected String handledBy;
	protected Date followUpDate;
	protected boolean viewAll = false;
	protected Date creationDate1;
	protected Date creationDate2;
	protected String requestStatus = "Active";

	@Override
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	// Show
	public boolean isShowOpen() {
		return showOpen;
	}

	public void setShowOpen(boolean showOpen) {
		this.showOpen = showOpen;
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

	public boolean isShowViewAll() {
		return showViewAll;
	}

	public void setShowViewAll(boolean showViewAll) {
		this.showViewAll = showViewAll;
	}

	public boolean isShowCreationDate() {
		return showCreationDate;
	}

	public void setShowCreationDate(boolean showCreationDate) {
		this.showCreationDate = showCreationDate;
	}
	
	public boolean isShowRequestStatus() {
		return showRequestStatus;
	}

	public void setShowRequestStatus(boolean showRequestStatus) {
		this.showRequestStatus = showRequestStatus;
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

	public boolean isViewAll() {
		return viewAll;
	}

	public void setViewAll(boolean viewAll) {
		this.viewAll = viewAll;
	}

	public Date getCreationDate1() {
		return creationDate1;
	}

	public void setCreationDate1(Date creationDate1) {
		this.creationDate1 = creationDate1;
	}

	public Date getCreationDate2() {
		return creationDate2;
	}

	public void setCreationDate2(Date creationDate2) {
		this.creationDate2 = creationDate2;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	// Lists
	public WaitingOn[] getHandledByList() throws Exception {
		return new WaitingOn[] { WaitingOn.PICS, WaitingOn.Operator };
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");

		if (permissions.hasGroup(User.GROUP_MANAGER))
			return dao.findWhere(true,
					"a IN (SELECT au.account FROM AccountUser au WHERE au.user.id = " + permissions.getUserId()
							+ " AND au.startDate < NOW() AND au.endDate > NOW())", permissions);
		return dao.findWhere(false, "", permissions);
	}

	public List<OperatorAccount> getOperatorListWithCorporate() throws Exception {
		if (permissions == null)
			return null;
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(true, "", permissions);
	}

	public List<State> getStateList() {
		StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
		return stateDAO.findAll();
	}
 }