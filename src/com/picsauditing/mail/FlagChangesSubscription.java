package com.picsauditing.mail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class FlagChangesSubscription extends SubscriptionBuilder {

	public FlagChangesSubscription() {
		super(Subscription.FlagChanges);
	}

	@Override
	protected void buildSql(Account a, SubscriptionTimePeriod timePeriod) {
		// select *
		// from contractor_info c
		// join flags f
		// on c.id = f.conID
		// where f.opID = a.id
		// AND f.lastUpdate > timePeriod.comparisonDate

		sql = new SelectSQL("contractor_info c");
		sql.addJoin("JOIN accounts a ON c.id = a.id");
		sql.addJoin("JOIN flags f ON f.conID = c.id");
		sql.addWhere("f.opID = " + a.getId());
		if (!timePeriod.equals(SubscriptionTimePeriod.None))
			sql.addWhere("f.lastUpdate > " + timePeriod.getCompaisonDate());
	}

	@Override
	protected List<BasicDynaBean> runSql() {
		// TODO Auto-generated method stub
		Database db = new Database();

		try {
			return db.select(sql.toString(), true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<BasicDynaBean>();
	}

	@Override
	protected EmailQueue buildEmail() {
		EmailQueue email = null;
		List<BasicDynaBean> data = runSql();

		if (data.size() > 0) {
			email = new EmailQueue();
			// build the email based on the data
			email.setSubject(subscription.getDescription());

			// TODO build the body based on data. Maybe an email template would
			// work here.
		}

		return email;
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		Map<Account, Map<SubscriptionTimePeriod, Set<EmailSubscription>>> subMap = getSubscriptionsByLastSentAndAccount();

		for (Map.Entry<Account, Map<SubscriptionTimePeriod, Set<EmailSubscription>>> accountMap : subMap.entrySet()) {
			for (Map.Entry<SubscriptionTimePeriod, Set<EmailSubscription>> timeMap : accountMap.getValue().entrySet()) {
				// this is the point where we can generate a unique email
				// Hopefully this process can be abstracted to the super class
				// if it is general enough.
				System.out.println(accountMap.getKey().getName() + " - " + timeMap.getKey() + " - "
						+ timeMap.getValue().size());
				buildSql(accountMap.getKey(), timeMap.getKey());
				EmailQueue emailToSend = buildEmail();

				if (emailToSend != null) {
					// get the recipients
					// TODO Maybe change the value of the second map to be a set
					// of
					// email strings
					Set<String> recipients = getRecipients(timeMap.getValue());

					// All are from the same Account, so CC should be safe
					emailToSend.setCcAddresses(Strings.implode(recipients, ","));

					// Send the email
					// EmailSender.send(emailToSend);
				}
			}
		}

	}

}
