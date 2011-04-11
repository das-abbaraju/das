package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.mail.ContractorAddedSubscription;
import com.picsauditing.mail.ContractorRegistrationSubscription;
import com.picsauditing.mail.FlagChangesSubscription;
import com.picsauditing.mail.FlagColorSubscription;
import com.picsauditing.mail.InsuranceCertificateSubscription;
import com.picsauditing.mail.OQChangesSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionBuilder;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.mail.TrialContractorAccountsSubscription;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class SubscriptionCron extends PicsActionSupport {

	private int userID = 0;
	private int accountID = 0;
	private SubscriptionTimePeriod timePeriod = SubscriptionTimePeriod.Weekly;

	private AppPropertyDAO appPropDAO;
	private EmailSubscriptionDAO subscriptionDAO;
	private ContractorAccountDAO conDAO;
	private UserDAO userDAO;
	private AccountDAO accountDAO;
	private ContractorOperatorDAO contractorOperatorDAO;

	Set<Subscription> subs = new HashSet<Subscription>();
	private String serverName;

	public SubscriptionCron(AppPropertyDAO appPropDAO, EmailSubscriptionDAO subscriptionDAO,
			ContractorAccountDAO conDAO, UserDAO userDAO, AccountDAO accountDAO,
			ContractorOperatorDAO contractorOperatorDAO) {
		this.appPropDAO = appPropDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.conDAO = conDAO;
		this.userDAO = userDAO;
		this.accountDAO = accountDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	@Anonymous
	@Override
	public String execute() throws Exception {
		Calendar calendar = Calendar.getInstance();
		String name = requestURL.toString();
		serverName = name.replace(ActionContext.getContext().getName() + ".action", "");

		if (userID > 0) {
			runSubscriptions(timePeriod);
			return SUCCESS;
		}

		for (Subscription subscription : Subscription.values()) {
			AppProperty app = appPropDAO.find(subscription.getAppPropertyKey());
			if (app != null && "1".equals(app.getValue()))
				subs.add(subscription);
		}

		List<SubscriptionTimePeriod> timePeriods = new ArrayList<SubscriptionTimePeriod>();

		// Handle the daily subscriptions
		timePeriods.add(SubscriptionTimePeriod.Daily);

		// Handle the weekly subscriptions
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			timePeriods.add(SubscriptionTimePeriod.Weekly);

		// Handle the monthly subscriptions (1st of the month)
		if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			timePeriods.add(SubscriptionTimePeriod.Monthly);

			// Send OQ notifications -- ad-hoc, subscriptions are not in the
			// database
			List<ContractorAccount> oqContractors = conDAO.findWhere("a.requiresOQ = 1 AND a.status = 'Active'");
			if (oqContractors.size() > 0) {
				SubscriptionBuilder builder = new OQChangesSubscription(Subscription.OQChanges,
						SubscriptionTimePeriod.Monthly, subscriptionDAO);

				for (ContractorAccount con : oqContractors) {
					for (User u : con.getUsers()) {
						if (u.isActiveB()) {
							boolean needsSubscription = false;
							for (UserAccess ua : u.getOwnedPermissions()) {
								if (ua.getOpPerm().equals(OpPerms.ContractorAdmin)
										|| ua.getOpPerm().equals(OpPerms.ContractorSafety)) {
									needsSubscription = true;
								}
							}

							if (needsSubscription) {
								builder.setServerName(serverName);
								builder.setUser(u);
								builder.setAccount(con);
								builder.process();
							}
						}
					}
				}
			}
		}

		for (SubscriptionTimePeriod timePeriod : timePeriods) {
			runSubscriptions(timePeriod);
		}

		addActionMessage("Finished " + Strings.implode(subs, ", ") + " subscriptions for the time periods "
				+ Strings.implode(timePeriods, ", "));

		return SUCCESS;
	}

	private void runSubscriptions(SubscriptionTimePeriod timePeriod) throws Exception {
		User user = null;
		Account account = null;
		if (userID > 0) {
			user = userDAO.find(userID);
			if (user == null) {
				addActionError("Failed to find userID = " + userID);
				return;
			}

			if (accountID > 0) {
				account = accountDAO.find(accountID);
				if (account == null) {
					addActionError("Failed to find accountID = " + accountID);
				}
			}
		}

		for (Subscription subscription : subs) {
			SubscriptionBuilder builder = null;

			if (subscription.equals(Subscription.FlagChanges)) {
				builder = new FlagChangesSubscription(timePeriod, subscriptionDAO);
			}

			if (subscription.equals(Subscription.PendingInsuranceCerts)) {
				builder = new InsuranceCertificateSubscription(Subscription.PendingInsuranceCerts, timePeriod,
						subscriptionDAO);
			}

			if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
				builder = new InsuranceCertificateSubscription(Subscription.VerifiedInsuranceCerts, timePeriod,
						subscriptionDAO);
			}

			if (subscription.equals(Subscription.ContractorRegistration)) {
				builder = new ContractorRegistrationSubscription(timePeriod, subscriptionDAO, conDAO);
			}

			if (subscription.equals(Subscription.ContractorAdded)) {
				builder = new ContractorAddedSubscription(timePeriod, subscriptionDAO);
			}

			if (subscription.equals(Subscription.RedFlags)) {
				builder = new FlagColorSubscription(Subscription.RedFlags, timePeriod, subscriptionDAO);
			}

			if (subscription.equals(Subscription.AmberFlags)) {
				builder = new FlagColorSubscription(Subscription.AmberFlags, timePeriod, subscriptionDAO);
			}

			if (subscription.equals(Subscription.GreenFlags)) {
				builder = new FlagColorSubscription(Subscription.GreenFlags, timePeriod, subscriptionDAO);
			}

			if (subscription.equals(Subscription.TrialContractorAccounts)) {
				builder = new TrialContractorAccountsSubscription(Subscription.TrialContractorAccounts, timePeriod,
						subscriptionDAO, contractorOperatorDAO);
			}

			if (builder != null) {
				builder.setServerName(serverName);
				builder.setUser(user);
				builder.setAccount(account);
				builder.process();
			}
		}
	}

	public void setSubs(Set<Subscription> subs) {
		this.subs = subs;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public void setTimePeriod(SubscriptionTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

}
