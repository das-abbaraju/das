package com.picsauditing.actions.report;

import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ReportEmailSubscriptionMatrix extends ReportActionSupport {
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionDAO;

	private Account account;

	private List<User> users;
	private DoubleMap<User, Subscription, EmailSubscription> table;

	@Override
	public String execute() throws Exception {
		if (account == null || !permissions.hasPermission(OpPerms.AllOperators))
			account = accountDAO.find(permissions.getAccountId());

		users = account.getUsers();
		Collections.sort(users);

		table = new DoubleMap<User, Subscription, EmailSubscription>();
		List<EmailSubscription> emailSubscriptions = emailSubscriptionDAO.findByAccountID(account.getId());

		for (EmailSubscription emailSubscription : emailSubscriptions) {
			table.put(emailSubscription.getUser(), emailSubscription.getSubscription(), emailSubscription);
		}

		return SUCCESS;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<User> getUsers() {
		return users;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getUsersJSON() {
		JSONArray j = new JSONArray();
		for (final User u : users) {
			j.add(new JSONObject() {
				{
					put("id", u.getId());
					put("name", u.getName());
				}
			});
		}

		return j;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getSubsJSON() {
		JSONArray j = new JSONArray();
		for (final Subscription s : Subscription.values()) {
			j.add(new JSONObject() {
				{
					put("id", s.name());
					put("name", getText(s.getI18nKey("description")));
				}
			});
		}

		return j;
	}

	public DoubleMap<User, Subscription, EmailSubscription> getTable() {
		return table;
	}
}
