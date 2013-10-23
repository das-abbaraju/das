package com.picsauditing.actions.flags;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectAccount.Type;

@SuppressWarnings("serial")
public class FlagChangesWidget extends PicsActionSupport {
	private int allrows;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public int getAllrows() {
		return allrows;
	}

	public List<BasicDynaBean> getFlagChanges() {
		SelectAccount sql = new SelectAccount();
		sql.setType(Type.Contractor);

		sql.addWhere("a.status IN ('Active')");

        sql.addJoin("JOIN account_user au on au.accountID = a.id and au.role='PICSCustomerServiceRep' and au.startDate < now() and au.endDate > now()");
		sql.addWhere("au.userID = " + permissions.getShadowedUserID());

		sql.addJoin("JOIN contractor_operator co_flag ON co_flag.conID = a.id AND co_flag.flag != co_flag.baselineFlag");
		sql.addField("co_flag.id coID");
		sql.addField("co_flag.flag");
		sql.addField("co_flag.baselineFlag");
		sql.addField("co_flag.baselineApproved");
		sql.addField("co_flag.baselineApprover");

		sql.addJoin("JOIN accounts operator on operator.id = co_flag.opID");
		sql.addField("operator.name AS opName");
		sql.addField("operator.id AS opId");
		sql.addWhere("operator.status IN ('Active') AND operator.type = 'Operator'");
		sql.addField("case when co_flag.baselineFlag = 'Green' then 1 when co_flag.baselineFlag is null then "
				+ "2 when co_flag.baselineFlag = 'Amber' then 3 when co_flag.baselineFlag = 'Red' then 4 "
				+ "when co_flag.baselineFlag = 'Clear' then 5 end as `baselineEnum`");
		sql.addField("case when co_flag.flag = 'Green' then 1 when co_flag.flag is null then "
				+ "2 when co_flag.flag = 'Amber' then 3 when co_flag.flag = 'Red' then 4 "
				+ "when co_flag.flag = 'Clear' then 5 end as `flagEnum`");
		sql.addField("c.membershipDate");
		sql.addField("co_flag.flagLastUpdated");
		sql.addOrderBy("baselineEnum, flagEnum DESC, a.name, operator.name");

		sql.setLimit(10);

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), true);
			allrows = db.getAllRows() - 10;

			return pageData;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
}
