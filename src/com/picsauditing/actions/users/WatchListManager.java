package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class WatchListManager extends PicsActionSupport {
	private ContractorAccountDAO conDAO;
	private UserDAO userDAO;
	
	private int userID;
	private int conID;
	private String userInfo = "";
	private String userName;
	private String contractorName;
	
	public WatchListManager(ContractorAccountDAO conDAO, UserDAO userDAO) {
		this.conDAO = conDAO;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (button != null) {
			tryPermissions(OpPerms.WatchListManager, OpType.Edit);
			
			if ("Remove".equals(button)) {
				List<ContractorWatch> watches = userDAO.findContractorWatch(userID);
				
				for (ContractorWatch watch : watches) {
					if (watch.getContractor().getId() == conID)
						userDAO.remove(watch);
				}
				
				return redirect("WatchListManager.action");
			}
			
			if ("Save".equals(button)) {
				if (!Strings.isEmpty(userName) && !Strings.isEmpty(contractorName)) {
					if ((permissions.isAdmin() || permissions.isCorporate()) && userName.contains("("))
						userName = userName.substring(0, userName.indexOf("(")).trim();
					
					String restriction = "";
					
					if (permissions.isCorporate())
						restriction = " AND (u.account IN (SELECT o FROM OperatorAccount o WHERE o.parent.id = " + 
							permissions.getAccountId() + ") OR u.account.id = " + permissions.getAccountId() + ")";
					if (permissions.isOperator())
						restriction = " AND u.account.id = " + permissions.getAccountId();
					
					List<User> users = userDAO.findWhere("(u.name LIKE '%" + userName + 
							"%' OR u.username LIKE '%" + userName + "%' OR u.email LIKE '%" + userName + 
							"%') AND u.isGroup = 'No' " + restriction);
					ContractorAccount contractor = conDAO.findConID(contractorName);
					ContractorWatch watch = new ContractorWatch();
					
					if (contractor != null) {
						watch.setContractor(contractor);
						watch.setAuditColumns(permissions);
					}
					
					if (users.size() == 1) {
						watch.setUser(users.get(0));
						userDAO.save(watch);
					} else if (users.size() == 0) {
						addActionError("No user with that name, email or username exists.");
					} else
						addActionError("Please select a specific user.");
				} else
					addActionError("Please enter both user name and contractor name");
			}
		}

		return SUCCESS;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public int getConID() {
		return conID;
	}
	
	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public String getUserInfo() {
		return userInfo;
	}
	
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getContractorName() {
		return contractorName;
	}
	
	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}
	
	public List<ContractorWatch> getWatchLists() {
		if (permissions.isOperator())
			return userDAO.findContractorWatchWhere("c.user.account.id = " + permissions.getAccountId());
		if (permissions.isCorporate())
			return userDAO.findContractorWatchWhere("c.user.account IN (SELECT o FROM OperatorAccount o " +
					"WHERE o.parent.id = " + permissions.getAccountId() + ") " +
					"OR c.user.account.id = " + permissions.getAccountId());
		
		return userDAO.findContractorWatchWhere(null);
	}
	
	public List<User> getNewUsers() {
		// If PICS admin
		// Warn if the user being added isn't associated with this contractor... somehow
		String where = "u.account.type != 'Contractor'";
		
		if (permissions.isCorporate())
			where = "(u.account IN (SELECT o FROM OperatorAccount o WHERE o.parent.id = "
					+ permissions.getAccountId() + ") OR u.account.id = " + permissions.getAccountId() + ")";
		if (permissions.isOperator())
			where = "u.account.id = " + permissions.getAccountId();

		where += " AND (u.name LIKE '%" + userInfo + "%' OR u.email LIKE '%" + userInfo + 
				"%' OR u.username LIKE '%" + userInfo + "%') AND u.isGroup = 'No'";
		
		return userDAO.findWhere(where);
	}
}
