package com.picsauditing.mail.subscription;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.Subscription;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class FlagColorSubscription extends SqlSubscriptionBuilder {
	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		try {
			FlagColor flagColor = FlagColor.Red;
			if (subscription.getSubscription().equals(Subscription.RedFlags))
				flagColor = FlagColor.Red;
			else if (subscription.getSubscription().equals(Subscription.AmberFlags))
				flagColor = FlagColor.Amber;
			else if (subscription.getSubscription().equals(Subscription.GreenFlags))
				flagColor = FlagColor.Green;

			OperatorAccount o = (OperatorAccount) subscription.getUser().getAccount();

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
				// for (UserSwitch user : getUser().getSwitchTos())
				// if (user.getUser().getAccount().isOperator())
				// operators.add(user.getUser().getAccount().getId());
			}

			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			// Only send out results on the contractors in the general
			// contractor table that have approved work status
			sql.addJoin("JOIN generalcontractors gc ON gc.flag = '" + flagColor.toString()
					+ "' AND a.id = gc.subID AND gc.genID IN (" + Strings.implode(operators, ",") + ")");
			sql.addJoin("JOIN operators o ON gc.genID = o.id");
			sql.addJoin("JOIN accounts oa ON o.id = oa.id");
			if (flagColor != FlagColor.Green || o.isOperator())
				sql.addWhere("(gc.workStatus = 'Y' OR o.approvesRelationships = 'No')");
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

			data = report.getPage(false);

			if (data.size() > 0) {
				tokens.put("data", data);
				tokens.put("flagColor", flagColor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tokens;
	}

	private Comparator<ContractorOperator> getCOComparator() {
		return new Comparator<ContractorOperator>() {
			public int compare(ContractorOperator co1, ContractorOperator co2) {
				return co1.getContractorAccount().getName().compareToIgnoreCase(co2.getContractorAccount().getName());
			}
		};
	}

}
