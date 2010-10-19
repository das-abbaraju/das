package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class WatchListManager extends PicsActionSupport {
	private ContractorAccountDAO conDAO;
	private ContractorOperatorDAO coDAO;
	private UserDAO userDAO;
	
	private int userID;
	private int conID;
	private String userInfo = "";
	
	public WatchListManager(ContractorAccountDAO conDAO, UserDAO userDAO, ContractorOperatorDAO coDAO) {
		this.conDAO = conDAO;
		this.userDAO = userDAO;
		this.coDAO = coDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (button != null) {
			tryPermissions(OpPerms.WatchListManager, OpType.Edit);
			
			if ("Remove".equals(button)) {
				List<ContractorWatch> watches = userDAO.findContractorWatch(userID);
				
				for (ContractorWatch watch : watches) {
					if (watch.getContractor().getId() == conID) {
						userDAO.remove(watch);
						break;
					}
				}
				
				return redirect("WatchListManager.action");
			}
			
			if ("Save".equals(button)) {
				if (userID > 0 && conID > 0) {
					User user = userDAO.find(userID);
					ContractorAccount contractor = conDAO.find(conID);
					
					ContractorWatch watch = new ContractorWatch();
					watch.setContractor(contractor);
					watch.setUser(user);
					watch.setAuditColumns(permissions);
					
					userDAO.save(watch);
				} else {
					if (userID == 0)
						addActionError("Contractor is missing or could not be found");
					if (conID == 0)
						addActionError("User is missing or could not be found");
				}
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
	
	public FlagColor getCoFlag(int conID, int opID){
		ContractorOperator co = coDAO.find(conID, opID);
		return co.getFlagColor();
	}
}
