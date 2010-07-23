package com.picsauditing.actions;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class CorporateWidget extends PicsActionSupport {
	protected int operatorCount;
	protected int corporateCount;
	protected int activeContractorCount;
	protected int userCount;
	protected int totalContractorCount;
	protected int amContractorCount;
	protected int amAccountCount;
	protected int amCorporateCount;
	protected int amUserCount;
	ContractorAccountDAO cAccountDAO;
	OperatorAccountDAO opAccountDAO;
	UserDAO userDAO;
	AccountUserDAO amDAO;

	public CorporateWidget(ContractorAccountDAO cAccountDAO, OperatorAccountDAO opAccountDAO, UserDAO userDAO,
			AccountUserDAO amDAO) {
		this.cAccountDAO = cAccountDAO;
		this.opAccountDAO = opAccountDAO;
		this.userDAO = userDAO;
		this.amDAO = amDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		totalContractorCount = cAccountDAO.getActiveContractorCounts("");
		activeContractorCount = cAccountDAO.getActiveContractorCounts("c.status = 'Active'");
		operatorCount = opAccountDAO.getOperatorCounts("o.type = 'Operator'");
		corporateCount = opAccountDAO.getOperatorCounts("o.type = 'Corporate'");
		userCount = userDAO.getUsersCounts();
		
		if ((permissions.hasGroup(981) || permissions.hasGroup(10801)) 
				&& !permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			// Account managers and Sales Reps
			List<AccountUser> amRoles = amDAO.findByUserSalesAM(permissions.getUserId());
			amAccountCount = amRoles.size();
			amCorporateCount = 0;
			
			for (AccountUser au : amRoles) {
				if (au.getAccount().isCorporate())
					amCorporateCount++;
			}
			
			amContractorCount = cAccountDAO.getActiveContractorCounts("c IN (SELECT contractorAccount FROM " +
					"ContractorOperator WHERE operatorAccount IN (SELECT account FROM AccountUser " +
					"WHERE user.id = " + permissions.getUserId() + "))");
			
			List<User> amUsers = userDAO.findWhere("u.account IN (SELECT account FROM AccountUser " +
					"WHERE user.id = " + permissions.getUserId() + ")");
			amUserCount = amUsers.size();
		}
		
		return SUCCESS;
	}

	public int getOperatorCount() {
		return operatorCount;
	}

	public int getActiveContractorCount() {
		return activeContractorCount;
	}

	public int getUserCount() {
		return userCount;
	}

	public int getTotalContractorCount() {
		return totalContractorCount;
	}

	public int getCorporateCount() {
		return corporateCount;
	}
	
	// For Account Managers and Sales Reps
	public int getAmAccountCount() {
		return amAccountCount;
	}
	
	public int getAmContractorCount() {
		return amContractorCount;
	}
	
	public int getAmCorporateCount() {
		return amCorporateCount;
	}
	
	public int getAmUserCount() {
		return amUserCount;
	}
}
