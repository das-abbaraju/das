package com.picsauditing.models.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.opensymphony.xwork2.validator.ValidationException;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;

public class FacilitiesEditModel {

	@Autowired
	private FacilitiesDAO facilitiesDAO;
	@Autowired
	protected BasicDAO dao;
	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private FeatureToggle featureToggle;

	public static final int PICS_US = 5;
	public static final int PICS_CANADA = 6;
	public static final int PICS_UAE = 7;
	public static final int PICS_UK = 9;
	public static final int PICS_FRANCE = 10;
	public static final int PICS_GERMANY = 11;
	
	private static final Logger logger = LoggerFactory.getLogger(FacilitiesEditModel.class);

	public void addPicsGlobal(OperatorAccount operator, Permissions permissions) {
		for (Facility f : operator.getCorporateFacilities()) {
			if (f.getCorporate().getId() == OperatorAccount.PicsConsortium) {
				return;
			}
		}

		Facility facility = new Facility();
		facility.setCorporate(new OperatorAccount());
		facility.getCorporate().setId(OperatorAccount.PicsConsortium);
		facility.setAuditColumns(permissions);
		facility.setOperator(operator);
		facilitiesDAO.save(facility);

		operator.getCorporateFacilities().add(facility);
	}

	public void addPicsCountry(OperatorAccount operator, Permissions permissions) {
		boolean picsCountryNeedsToBeAdded = removeUnecessaryPicsCountries(operator);

		if (picsCountryNeedsToBeAdded) {
			Facility facility = new Facility();
			facility.setAuditColumns(permissions);
			facility.setOperator(operator);

			List<OperatorAccount> picsConsortiums = dao.findWhere(OperatorAccount.class, "inPicsConsortium=1");
			for (OperatorAccount consortium : picsConsortiums) {
				if (consortium.getId() == OperatorAccount.PicsConsortium)
					continue;
				if (operator.getCountry().getIsoCode().equals(consortium.getCountry().getIsoCode())) {
					facility.setCorporate(consortium);
					break;
				}
			}

			if (facility.getCorporate() != null) {
				operator.getCorporateFacilities().add(facility);
				facilitiesDAO.save(facility);
			}
		}
	}

	private boolean removeUnecessaryPicsCountries(OperatorAccount operator) {
		boolean picsCountryNeedsToBeAdded = true;

		List<Facility> facilitiesToBeRemoved = new ArrayList<Facility>();
		for (Facility currrentFacility : operator.getCorporateFacilities()) {
			OperatorAccount corporate = currrentFacility.getCorporate();

			if (corporate.isInPicsConsortium() && corporate.getId() != OperatorAccount.PicsConsortium) {
				if (!corporate.getCountry().equals(operator.getCountry())) {
					facilitiesToBeRemoved.add(currrentFacility);
				} else {
					picsCountryNeedsToBeAdded = false;
				}
			}
		}

		operator.getCorporateFacilities().removeAll(facilitiesToBeRemoved);
		return picsCountryNeedsToBeAdded;
	}

	// TODO: move this into its own validator
	public void addRoleValidation(OperatorAccount operator, AccountUser accountUser) throws ValidationException {
		List<AccountUser> accountUsers = filterForRole(operator.getAccountUsers(), accountUser.getRole());
		if (CollectionUtils.isEmpty(accountUsers) && accountUser.getOwnerPercent() != 100) {
			throw new ValidationException(accountUser.getRole().getDescription() + " is not 100 percent");
		}

		accountUsers.add(accountUser);
		int completePercent = 0;
		for (AccountUser accountUser2 : accountUsers) {
			completePercent += accountUser2.getOwnerPercent();
		}

		if (completePercent != 100) {
			throw new ValidationException(accountUser.getRole().getDescription() + " is not 100 percent");
		}
	}

	private List<AccountUser> filterForRole(List<AccountUser> accountUsers, UserAccountRole role) {
		if (role == null) {
			return Collections.emptyList();
		}

		List<AccountUser> accountUsersForRole = new ArrayList<AccountUser>();
		for (AccountUser accountUser : accountUsers) {
			if (role == accountUser.getRole()) {
				accountUsersForRole.add(accountUser);
			}
		}

		return accountUsersForRole;
	}

	public AccountUser addRole(Permissions permissions, OperatorAccount operator, AccountUser accountUser) {
		AccountUser newAccountUser = new AccountUser();		
		setCommissionableServiceLevel(newAccountUser, accountUser.getServiceLevel());
		newAccountUser.setUser(accountUser.getUser());
		newAccountUser.setRole(accountUser.getRole());
		setAccountUserDuration(newAccountUser);
		newAccountUser.setAccount(operator);
		newAccountUser.setAuditColumns(permissions);
		operator.getAccountUsers().add(newAccountUser);
		
		operatorDAO.save(operator);
		
		return newAccountUser;
	}

	private void setCommissionableServiceLevel(AccountUser newAccountUser, String commissionableServiceLevel) {
		if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_INVOICE_COMMISSION_PHASE2)) {
			return;
		}
		
		if (Strings.isEmpty(commissionableServiceLevel)) {
			newAccountUser.setServiceLevel("All");
		} else {
			newAccountUser.setServiceLevel(commissionableServiceLevel);
		}
	}

	private void setAccountUserDuration(AccountUser newAccountUser) {
		// First of this month to next year, minus a day
		// Feb 1st, 2010 to Jan 31st, 2011
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		newAccountUser.setStartDate(calendar.getTime());
		if (newAccountUser.getRole() != null && newAccountUser.getRole().isAccountManager())
			calendar.add(Calendar.YEAR, 20);
		else {
			calendar.add(Calendar.YEAR, 1);
			calendar.add(Calendar.DATE, -1);
		}

		newAccountUser.setEndDate(calendar.getTime());
	}
	
	public List<User> getAllPossibleAccountUsers() {
		List<User> possibleAccountUsers = Collections.emptyList();
		try {
			possibleAccountUsers = userDAO.findByGroup(User.GROUP_MARKETING);
		} catch (Exception e) {
			logger.error("Error while looking up possible account users.", e);
		}
		
		return possibleAccountUsers;
	}
}