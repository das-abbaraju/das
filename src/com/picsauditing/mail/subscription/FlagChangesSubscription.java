package com.picsauditing.mail.subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class FlagChangesSubscription extends SqlSubscriptionBuilder {

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		try {
			OperatorAccount o = (OperatorAccount) subscription.getUser().getAccount();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			Set<Integer> operators = new HashSet<Integer>();
			if (!o.isCorporate())
				operators.add(o.getId());
			// Adding child facilities and switch tos
			for (Facility f : o.getOperatorFacilities())
				operators.add(f.getOperator().getId());

			SelectSQL sql = new SelectSQL();

			sql.setFromTable("accounts a");
			// Only send out results on the contractors in the general
			// contractor table that have approved work status
			sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID AND gc.genID IN ("
					+ Strings.implode(operators, ",") + ")");
			sql.addJoin("JOIN operators o ON gc.genID = o.id");
			sql.addJoin("JOIN accounts oa ON o.id = oa.id");
			sql.addJoin("JOIN flag_archive f2 ON gc.subID = f2.conID AND gc.genID = f2.opID AND gc.flag <> f2.flag");

			sql.addWhere("f2.creationDate = '" + df.format(subscription.getTimePeriod().getNearestComparisonDate()) + "'");
			sql.addWhere("(gc.workStatus = 'Y' OR o.approvesRelationships = 'No')");
			sql.addWhere("a.status = 'Active'");
			sql.addField("a.name AS name");
			sql.addField("a.id AS conID");
			sql.addField("f2.flag AS oldFlag");
			sql.addField("gc.flag AS flag");
			sql.addField("oa.name AS opName");
			sql.addField("o.id AS opID");

			if (o.isOperator()) {
				sql.addGroupBy("a.id, oa.id");
			}

			sql.addOrderBy("a.name, oa.name");

			report.setLimit(100000);
			report.setSql(sql);

			data = report.getPage(false);

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
				tokens.put("date", subscription.getTimePeriod().getNearestComparisonDate());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tokens;
	}
}
