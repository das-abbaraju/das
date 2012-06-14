package com.picsauditing.mail.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

public class ForcedFlagsSubscription extends SqlSubscriptionBuilder {
	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		try {
			OperatorAccount o = (OperatorAccount) subscription.getUser().getAccount();

			SelectSQL sql = new SelectAccount();
			String forceFlagsJoin = "JOIN (SELECT conid,opid,forcebegin,forceend,forcedby,forceflag,label FROM"
					+ " (SELECT gc.subid as conid, gc.genid as opid, gc.forcebegin, gc.forceend, gc.forcedBy, gc.forceflag, 'FlagCriteria.Overall' AS label FROM generalcontractors gc"
					+ " WHERE gc.forceFlag IS NOT NULL"
					+ " UNION"
					+ " SELECT fdo.conid, fdo.opid, fdo.updateDate as forcebegin, fdo.forceend, fdo.updatedBy as forcedby, fdo.forceflag, CONCAT('FlagCriteria.', fc1.id, '.label') as label FROM flag_data_override fdo"
					+ " JOIN flag_criteria fc1 ON fdo.criteriaID = fc1.id" + " WHERE fdo.forceFlag IS NOT NULL) t"
					+ " ) ff ON a.id = ff.conid";

			sql.addJoin(forceFlagsJoin);
			sql.addJoin("JOIN accounts o ON o.id = ff.opid");
			sql.addJoin("LEFT JOIN users u ON u.id = ff.forcedBy");
			sql.addJoin("LEFT JOIN accounts fa ON fa.id = u.accountID");
			sql.addJoin("LEFT JOIN generalcontractors gc ON gc.genid = o.id and gc.subid = ff.conid");
			sql.addWhere("a.status = 'Active'");
			sql.addWhere("o.status = 'Active'");
			sql.addWhere("ff.forceFlag IS NOT NULL");
			if (o.isOperatorCorporate()) {
				String opIds = " ff.opid = " + o.getId() + " OR ";
				if (o.isOperator())
					opIds += " ff.opid IN (SELECT corporateID from facilities where opID = " + o.getId() + ")";
				else
					opIds += " ff.opid IN (SELECT opID from facilities where corporateID = " + o.getId() + ")";
				sql.addWhere(opIds);
			}
			sql.addField("o.name AS opName");
			sql.addField("o.type AS opType");
			sql.addField("o.id AS opId");
			sql.addField("lower(ff.forceFlag) AS flag");
			sql.addField("label AS fLabel");
			sql.addField("ff.forceend");
			sql.addField("ff.forceBegin");
			sql.addField("u.id as forcedById");
			sql.addField("u.name AS forcedBy");
			sql.addField("fa.name AS forcedByAccount");
			sql.addField("gc.workStatus");

			report.setLimit(100);
			report.setSql(sql);

			data = report.getPage(false);

			List<DynaBean> forcedFlags = new ArrayList<DynaBean>();

			for (DynaBean bean : data) {
				forcedFlags.add(bean);
			}

			if (forcedFlags.size() > 0) {
				tokens.put("forcedflags", forcedFlags);
				tokens.put("date", subscription.getTimePeriod().getComparisonDate());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tokens;
	}
}
