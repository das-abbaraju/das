package com.picsauditing.PICS;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SmartFacilitySuggest {
	static private int minimumSampleSize = 50;

	static public List<BasicDynaBean> getFirstFacility(ContractorAccount contractor, Permissions permissions)
			throws Exception, SQLException {

		for (int zipLength = 2; zipLength >= 0; zipLength--) {
			List<BasicDynaBean> results = createBaseQuery(contractor, permissions, zipLength, true);
			if (isBigEnough(results))
				return results;
		}

		return createBaseQuery(contractor, permissions, 0, false);
	}

	private static List<BasicDynaBean> createBaseQuery(ContractorAccount contractor, Permissions permissions,
			int zipLength, boolean recentlyAdded) throws SQLException {
		Database db = new Database();

		SelectSQL sql = new SelectSQL("accounts o");
		sql.addJoin("JOIN generalcontractors gc ON gc.genID = o.id");
		sql.addJoin("JOIN accounts c ON gc.subID = c.id");
		sql.addWhere("o.type = 'Operator'");

		sql.addWhere("c.status IN ('Active', 'Pending')");
		if (contractor.isDemo())
			sql.addWhere("o.status in ('Active', 'Demo')");
		else
			sql.addWhere("o.status = 'Active'");
		// a. PQF Only / a. Audited Unspecified
		sql.addWhere("o.id NOT IN (10403,2723)");

		sql.addWhere("c.country = '" + contractor.getCountry().getIsoCode() + "'");
		if (recentlyAdded)
			sql.addWhere("gc.creationDate > '" + getTwoMonthsAgo() + "'");
		if (zipLength > 0)
			sql.addWhere("c.zip LIKE '" + contractor.getZip().substring(0, zipLength) + "%'");

		sql.addField("o.id opID");
		sql.addField("o.generalContractor");
		sql.addGroupBy("opID");
		sql.addField("COUNT(*) total");
		sql.addOrderBy("total DESC");

		addCorporateRestrictions(sql, permissions);

		addFields(sql, "o.");
		sql.setLimit(25);

		return db.select(sql.toString(), false);
	}

	private static void addCorporateRestrictions(SelectSQL sql, Permissions permissions) {
		String operators = null;
		if (permissions.isCorporate())
			operators = Strings.implode(permissions.getOperatorChildren());
		if (!Strings.isEmpty(operators))
			sql.addWhere("o.id in (" + operators + ")");
	}

	private static String getTwoMonthsAgo() {
		Calendar changedSince = Calendar.getInstance();
		changedSince.add(Calendar.MONTH, -2);
		return DateBean.toDBFormat(changedSince.getTime());
	}

	private static boolean isBigEnough(List<BasicDynaBean> operators) {
		int totalContractors = 0;
		for (BasicDynaBean operator : operators) {
			totalContractors += Integer.parseInt(operator.get("total").toString());
			if (totalContractors > minimumSampleSize)
				return true;
		}
		return false;
	}

	static public List<BasicDynaBean> getSimilarOperators(ContractorAccount contractor, int limit) throws SQLException {
		String opIDs = Strings.implodeIDs(contractor.getOperatorAccounts());

		if (Strings.isEmpty(opIDs)) {
			return Collections.emptyList();
		}

		SelectSQL sql = new SelectSQL("stats_gco_count s");
		sql.addJoin("JOIN accounts a ON s.opID2 = a.id");
		sql.addJoin("JOIN stats_gco_count s2 ON s2.opID = s.opID2 AND s2.opID2 IS NULL");
		sql.addWhere("s.opID IN (" + opIDs + ")");
		sql.addWhere("s.opID2 NOT IN (" + opIDs + ")");
		if (contractor.getStatus() == AccountStatus.Active || contractor.getStatus() == AccountStatus.Pending)
			sql.addWhere("a.status = 'Active'");
		else if (contractor.getStatus() == AccountStatus.Demo)
			sql.addWhere("a.status IN ('Active','Pending','Demo')");
		sql.addGroupBy("a.id");
		if (limit > 0)
			sql.addOrderBy("score DESC");
		else
			sql.addOrderBy("a.name");

		addFields(sql, "a.");
		sql.addField("(s.total*AVG(s.total)/s2.total) score");
		sql.addField("a.id opID");
		sql.addField("ROUND(100*AVG(s.total)/s2.total)");
		sql.addField("SUM(s.total)");
		sql.addField("COUNT(*)");
		if (limit > 0)
			sql.setLimit(limit);

		Database db = new Database();
		return db.select(sql.toString(), false);
	}

	private static void addFields(SelectSQL sql, String alias) {
		sql.addField(alias + "name");
		sql.addField(alias + "dbaName");
		sql.addField(alias + "status");
		sql.addField(alias + "city");
		sql.addField(alias + "state");
		sql.addField(alias + "country");
		sql.addField(alias + "onsiteServices");
		sql.addField(alias + "offsiteServices");
		sql.addField(alias + "materialSupplier");
		sql.addField(alias + "transportationServices");
	}
}
