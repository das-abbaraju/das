package com.picsauditing.actions.users;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class WatchListManager extends PicsActionSupport {
	@Autowired
	protected ContractorOperatorDAO coDAO;
	@Autowired
	protected UserDAO userDAO;

	protected User user;
	protected ContractorAccount contractor;
	private String userInfo = "";

	@RequiredPermission(value = OpPerms.WatchListManager, type = OpType.Edit)
	public String save() {
		if (user != null && contractor != null) {
			ContractorWatch watch = new ContractorWatch();
			watch.setContractor(contractor);
			watch.setUser(user);
			watch.setAuditColumns(permissions);

			userDAO.save(watch);
		} else {
			if (user == null)
				addActionError(getText("WatchListManager.message.UserMissing"));
			if (contractor == null)
				addActionError(getText("WatchListManager.message.ContractorMissing"));
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.WatchListManager, type = OpType.Edit)
	public String remove() throws Exception {
		User user = userDAO.find(permissions.getUserId());

		if (contractor != null) {
			Iterator<ContractorWatch> iterator = user.getWatchedContractors().iterator();

			while (iterator.hasNext()) {
				ContractorWatch watch = iterator.next();

				if (contractor.equals(watch.getContractor())) {
					iterator.remove();
					userDAO.remove(watch);
					break;
				}
			}
		}

		return redirect("WatchListManager.action");
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public List<ContractorWatch> getWatchLists() {
		List<Integer> contractorIds = coDAO.getContractorIdsForOperator("operatorAccount.id = " + permissions.getAccountId() 
				+ " AND workStatus in ('" + ApprovalStatus.C + "', '" + ApprovalStatus.Y + "')");
		
		String baseQuery = "c.contractor.status = '" + AccountStatus.Active + "' " 
					+ "AND c.contractor.id IN (" + Strings.implode(contractorIds) + ") ";
		if (permissions.isOperator())
			return userDAO.findContractorWatchWhere(baseQuery + "AND c.user.account.id = " + permissions.getAccountId());
		
		if (permissions.isCorporate())
			return userDAO.findContractorWatchWhere(baseQuery + "AND c.user.account IN (SELECT o FROM OperatorAccount o "
					+ "WHERE o.parent.id = " + permissions.getAccountId() + ") " + "OR c.user.account.id = "
					+ permissions.getAccountId());

		return userDAO.findContractorWatchWhere(null);
	}

	public List<User> getNewUsers() {
		// If PICS admin
		// Warn if the user being added isn't associated with this contractor... somehow
		String where = "u.account.type != 'Contractor'";

		if (permissions.isCorporate())
			where = "(u.account IN (SELECT o FROM OperatorAccount o WHERE o.parent.id = " + permissions.getAccountId()
					+ ") OR u.account.id = " + permissions.getAccountId() + ")";
		if (permissions.isOperator())
			where = "u.account.id = " + permissions.getAccountId();

		where += " AND (u.name LIKE '%" + userInfo + "%' OR u.email LIKE '%" + userInfo + "%' OR u.username LIKE '%"
				+ userInfo + "%') AND u.isGroup = 'No'";

		return userDAO.findWhere(where);
	}

	public FlagColor getCoFlag(int conID, int opID) {
		ContractorOperator co = coDAO.find(conID, opID);
		return co.getFlagColor();
	}
}
