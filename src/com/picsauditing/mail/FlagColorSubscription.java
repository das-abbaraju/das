package com.picsauditing.mail;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class FlagColorSubscription extends SubscriptionBuilder {
	protected Report report = new Report();
	protected List<BasicDynaBean> data;

	private FlagColor flagColor;

	public FlagColorSubscription(Subscription subscription,
			SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.templateID = 65;
		if (subscription.equals(Subscription.RedFlags))
			flagColor = FlagColor.Red;
		else if (subscription.equals(Subscription.AmberFlags))
			flagColor = FlagColor.Amber;
		else if (subscription.equals(Subscription.GreenFlags))
			flagColor = FlagColor.Green;
	}

	@Override
	protected void setup(Account a) {
		try {
			OperatorAccount o = (OperatorAccount) a;

			Set<Integer> operators = new HashSet<Integer>();
			// If contractors are green flagged, then finding the overall flag
			// color is sufficient
			if (flagColor == FlagColor.Green)
				operators.add(o.getId());
			else {
				if (!o.isCorporate())
					operators.add(o.getId());
				// Adding child facilities and switch tos
				for (OperatorAccount oa : o.getOperatorChildren())
					operators.add(oa.getId());
				for (UserSwitch user : getUser().getSwitchTos())
					if (user.getUser().getAccount().isOperator())
						operators.add(user.getUser().getAccount().getId());
			}

			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			// Only send out results on the contractors in the general
			// contractor table that have approved work status
			sql.addJoin("JOIN generalcontractors gc ON gc.flag = '"
					+ flagColor.toString()
					+ "' AND a.id = gc.subID AND gc.genID IN ("
					+ Strings.implode(operators, ",") + ")");
			sql.addJoin("JOIN operators o ON gc.genID = o.id");
			sql.addJoin("JOIN accounts oa ON o.id = oa.id");
			if (flagColor != FlagColor.Green
					|| getUser().getAccount().isOperator())
				sql
						.addWhere("(gc.workStatus = 'Y' OR o.approvesRelationships = 'No')");
			sql.addWhere("a.status = 'Active'");
			sql.addField("a.name AS name");
			sql.addField("a.id AS conID");
			sql.addField("gc.flag AS flag");
			sql.addField("oa.name AS opName");
			sql.addField("o.id AS opID");
			sql.addField("gc.waitingOn AS waitingOn");
			sql.addField("gc.flagLastUpdated AS changedOn");

			sql.addOrderBy("oa.name, a.name");

			report.setLimit(100000);
			report.setSql(sql);

			data = report.getPage();
			
			if(data.size() > 0) {
				tokens.put("data", data);
				tokens.put("flagColor", flagColor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Comparator<ContractorOperator> getCOComparator() {
		return new Comparator<ContractorOperator>() {
			@Override
			public int compare(ContractorOperator co1, ContractorOperator co2) {
				return co1.getContractorAccount().getName()
						.compareToIgnoreCase(
								co2.getContractorAccount().getName());
			}
		};
	}
}
