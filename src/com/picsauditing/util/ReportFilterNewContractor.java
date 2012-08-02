package com.picsauditing.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;

@SuppressWarnings("serial")
public class ReportFilterNewContractor extends ReportFilterContractor {
	protected boolean showFollowUpDate = true;
	protected boolean showViewAll = false;
	protected boolean showCreationDate = true;
	protected boolean showClosedOnDate = true;
	protected boolean showRequestStatus = true;
	protected boolean showExcludeOperators = false;
	protected boolean showOperatorTags = false;
	protected boolean showMarketingUsers = false;

	protected String handledBy;
	protected Date followUpDate;
	protected boolean viewAll = false;
	protected Date creationDate1;
	protected Date creationDate2;
	protected Date closedOnDate1;
	protected Date closedOnDate2;
	protected String requestStatus = "Active";
	protected int[] excludeOperators;
	protected int[] operatorTags;
	protected int[] marketingUsers;

	@Override
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
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

	public boolean isShowClosedOnDate() {
		return showClosedOnDate;
	}

	public void setShowClosedOnDate(boolean showClosedOnDate) {
		this.showClosedOnDate = showClosedOnDate;
	}

	public boolean isShowRequestStatus() {
		return showRequestStatus;
	}

	public void setShowRequestStatus(boolean showRequestStatus) {
		this.showRequestStatus = showRequestStatus;
	}

	public boolean isShowExcludeOperators() {
		return showExcludeOperators;
	}

	public void setShowExcludeOperators(boolean showExcludeOperators) {
		this.showExcludeOperators = showExcludeOperators;
	}

	public boolean isShowOperatorTags() {
		return showOperatorTags;
	}

	public void setShowOperatorTags(boolean showOperatorTags) {
		this.showOperatorTags = showOperatorTags;
	}

	public boolean isShowMarketingUsers() {
		return showMarketingUsers;
	}

	public void setShowMarketingUsers(boolean showMarketingUsers) {
		this.showMarketingUsers = showMarketingUsers;
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

	public Date getClosedOnDate1() {
		return closedOnDate1;
	}

	public void setClosedOnDate1(Date closedOnDate1) {
		this.closedOnDate1 = closedOnDate1;
	}

	public Date getClosedOnDate2() {
		return closedOnDate2;
	}

	public void setClosedOnDate2(Date closedOnDate2) {
		this.closedOnDate2 = closedOnDate2;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public int[] getExcludeOperators() {
		return excludeOperators;
	}

	public void setExcludeOperators(int[] excludeOperators) {
		this.excludeOperators = excludeOperators;
	}

	public int[] getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(int[] operatorTags) {
		this.operatorTags = operatorTags;
	}

	public int[] getMarketingUsers() {
		return marketingUsers;
	}

	public void setMarketingUsers(int[] marketingUsers) {
		this.marketingUsers = marketingUsers;
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

	public List<CountrySubdivision> getCountrySubdivisionList() {
		CountrySubdivisionDAO countrySubdivisionDAO = (CountrySubdivisionDAO) SpringUtils.getBean("CountrySubdivisionDAO");
		return countrySubdivisionDAO.findAll();
	}

	public List<OperatorTag> getOperatorTagsList() {
		OperatorTagDAO tagDAO = (OperatorTagDAO) SpringUtils.getBean("OperatorTagDAO");

		List<OperatorTag> tags = tagDAO.findAll();
		Collections.sort(tags, new Comparator<OperatorTag>() {
			@Override
			public int compare(OperatorTag o1, OperatorTag o2) {
				if (o1.getOperator().getName().equals(o2.getOperator().getName())) {
					return o1.getTag().compareTo(o2.getTag());
				}

				return o1.getOperator().getName().compareTo(o2.getOperator().getName());
			}
		});

		return tags;
	}

	public List<User> getMarketingUsersList() {
		UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
		return userDAO.findByGroup(User.GROUP_MARKETING);
	}
}