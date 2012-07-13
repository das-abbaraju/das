package com.picsauditing.mail.subscription;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.search.SelectSQL;

public class InsuranceRejectedNoUpdateSubscription extends SqlSubscriptionBuilder {
	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		try {
			SelectSQL sql = new SelectSQL();

			sql.setFromTable("contractor_audit_operator a");
			sql.addWhere("a.status='Incomplete'");
			sql.addWhere("a.updateDate < DATE_ADD(NOW(), INTERVAL -7 DAY)");
			sql.addWhere("a.updateDate > DATE_ADD(NOW(), INTERVAL -8 DAY)");
			report.setLimit(100000);

			report.setSql(sql);

			data = report.getPage(false);

			if (data.size() > 0) {
				tokens.put("data", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tokens;
	}
}
