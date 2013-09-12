package com.picsauditing.model.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;

public class FacilitiesEditModel {
    private static final Logger logger = LoggerFactory.getLogger(FacilitiesEditModel.class);

    public static final int PICS_US = 5;
    public static final int PICS_CANADA = 6;
    public static final int PICS_UAE = 7;
    public static final int PICS_UK = 9;
    public static final int PICS_FRANCE = 10;
    public static final int PICS_GERMANY = 11;

    @Autowired
	private AccountUserDAO accountUserDAO;
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
				if (consortium.getId() == OperatorAccount.PicsConsortium) {
					continue;
				}
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

    public FacilitiesEditStatus manageSingleCurrentAccountUser(Permissions permissions, OperatorAccount operator, AccountUser accountUser) {
        FacilitiesEditStatus status = new FacilitiesEditStatus();
        if (accountUser.getRole() == UserAccountRole.PICSAccountRep) {
            // we're using 0 to mean remove in single select box case (not great, but works for now)
            if (accountUser.getUser().getId() == 0) {
                status = removeCurrentAccountRepresentative(permissions, operator, accountUser);
            } else if (operator.getCurrentAccountRepresentative() == null ||
                operator.getCurrentAccountRepresentative().getId() != accountUser.getUser().getId()) {
                // if they're trying to save the same one, just ignore it
                operator.setCurrentAccountRepresentative(accountUser.getUser(), permissions.getUserId());
                operatorDAO.save(operator);
                status.isOkMessage = "Successfully Saved Account Manager.";
            }
        }
        return status;
    }

    private FacilitiesEditStatus removeCurrentAccountRepresentative(Permissions permissions, OperatorAccount operator, AccountUser accountUser) {
        FacilitiesEditStatus status = isOperatorAllowedToHaveNoAccountUserOfThisRole(operator, accountUser.getRole());
        if (status.isOk) {
            operator.setCurrentAccountRepresentative(null, permissions.getUserId());
            operatorDAO.save(operator);
            status.isOkMessage = "Successfully Removed Account Manager.";
        }
        return status;
    }

    public FacilitiesEditStatus addOneToManyAccountUser(Permissions permissions, OperatorAccount operator, AccountUser accountUser) {
        FacilitiesEditStatus status = new FacilitiesEditStatus();
        if (accountUser.getRole() == UserAccountRole.PICSSalesRep) {
            AccountUser newAccountUser = new AccountUser();
            setCommissionableServiceLevel(newAccountUser, accountUser.getServiceLevel());
            newAccountUser.setUser(accountUser.getUser());
            newAccountUser.setRole(accountUser.getRole());
            setAccountUserDuration(newAccountUser);
            newAccountUser.setAccount(operator);
            newAccountUser.setAuditColumns(permissions);
            operator.getAccountUsers().add(newAccountUser);

            operatorDAO.save(operator);
            status.isOkMessage = "Successfully Added Sales Representative.";
        }
		return status;
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
		if (newAccountUser.getRole() != null && newAccountUser.getRole().isAccountManager()) {
			calendar.add(Calendar.YEAR, 20);
		} else {
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

	/**
	 * For each child facility, if the child facility does not have an existing
	 * Account Rep, or Reps, that add up to 100% ownership, then copy down the
	 * rep from the parent.
	 */
	public void copyOneToManyAccountUserToChildAccounts(OperatorAccount operator, AccountUser accountUser) throws Exception {
		for (Facility facility : operator.getOperatorFacilities()) {
			if (facility.getOperator().getStatus().isActiveOrDemo()) {

				// Does the child facility have an existing AccountUser(s) of this role that total >= 100%?
				boolean hasAccountRep = false;
				int percent = 0;
				for (AccountUser childAccountRep : facility.getOperator().getAccountUsers()) {
					if (childAccountRep.isCurrent() && (childAccountRep.getRole() == accountUser.getRole())) {
						percent += childAccountRep.getOwnerPercent();
						if (childAccountRep.getUser().equals(accountUser.getUser()) || percent >= 100) {
							hasAccountRep = true;
							break;
						}
					}
				}

				// If not, copy down the rep from the parent
				if (!hasAccountRep) {
					AccountUser au = (AccountUser) accountUser.clone();
					au.setAccount(facility.getOperator());
					accountUserDAO.save(au);
				}
			}
		}

	}

    public void copySingleCurrentAccountUserToChildAccounts(Permissions permissions, OperatorAccount operator, AccountUser accountUser) throws Exception {
        for (Facility facility : operator.getOperatorFacilities()) {
            if (facility.getOperator().isActiveOrDemo()) {
                OperatorAccount childOperator = facility.getOperator();
                if (accountUser.getRole() == UserAccountRole.PICSAccountRep) {
                    childOperator.setCurrentAccountRepresentative(accountUser.getUser(), permissions.getUserId());
                    operatorDAO.save(childOperator);
                }
            }
        }
    }

    public FacilitiesEditStatus isOperatorAllowedToHaveNoAccountUserOfThisRole(OperatorAccount operator, UserAccountRole role) {
        FacilitiesEditStatus status = new FacilitiesEditStatus();
        if (role.isAccountManager() && operator.isActiveOrDemo()) {
            status.isOk = false;
            if (operator.isActive()) {
                status.notOkErrorMessage = "Active accounts are required to have at least one active Account Manager.";
            } else {
                status.notOkErrorMessage = "Demo accounts are required to have at least one active Account Manager.";
            }
        }
        return status;
    }
}