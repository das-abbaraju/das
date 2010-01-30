package com.picsauditing.mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;

public class FlagChangesSubscription extends SubscriptionBuilder {

	protected Report report = new Report();
	protected List<BasicDynaBean> data;

	public FlagChangesSubscription(SubscriptionTimePeriod timePeriod, EmailSubscriptionDAO subscriptionDAO) {
		super(Subscription.FlagChanges, timePeriod, subscriptionDAO);
		this.templateID = 60;
	}

	@Override
	protected void setup(Account a) {
		try {
			OperatorAccount o = (OperatorAccount) a;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			// Only send out results on the contractors in the general contractor table that have approved work status
			sql.addJoin("JOIN flags f1 ON a.id = f1.conID AND f1.opID = " + o.getId());
			sql.addJoin("JOIN flag_archive f2 ON f1.conID = f2.conID AND f1.opID = f2.opID AND f1.flag <> f2.flag");
			sql.addJoin("JOIN generalcontractors gc ON f1.conID = gc.subID AND f1.opID = gc.genID AND gc.workStatus = 'Y'");
			sql.addJoin("JOIN operators o ON gc.genID = o.id");
			
			sql.addWhere("f2.creationDate = '" + df.format(timePeriod.getComparisonDate()) + "'");
			sql.addWhere("((o.approvesRelationships = 'Yes' AND gc.workStatus = 'Y') OR (o.approvesRelationships = 'No'))");

			sql.addField("a.name AS name");
			sql.addField("a.id AS conID");
			sql.addField("f2.flag AS oldFlag");
			sql.addField("f1.flag AS flag");

			sql.addOrderBy("a.name");

			report.setLimit(100000);
			report.setSql(sql);

			data = report.getPage();

			List<DynaBean> upgrades = new ArrayList<DynaBean>();
			List<DynaBean> downgrades = new ArrayList<DynaBean>();

			for (DynaBean bean : data) {
				FlagColor flag = FlagColor.valueOf((String) bean.get("flag"));
				FlagColor oldFlag = FlagColor.valueOf((String) bean.get("oldFlag"));
				// This seems backwards, but Green < Red according to the enum
				if (flag.compareTo(oldFlag) < 0)
					upgrades.add(bean);
				else
					downgrades.add(bean);
			}

			if (upgrades.size() > 0 || downgrades.size() > 0) {
				tokens.put("upgrades", upgrades);
				tokens.put("downgrades", downgrades);
				tokens.put("date", timePeriod.getComparisonDate());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
