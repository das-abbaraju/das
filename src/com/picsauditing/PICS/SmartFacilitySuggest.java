package com.picsauditing.PICS;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.ibm.icu.util.Calendar;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class SmartFacilitySuggest {
	static public List<BasicDynaBean> getFirstFacility(ContractorAccount contractor) throws Exception, SQLException {
		Calendar changedSince = Calendar.getInstance();
		changedSince.add(Calendar.MONTH, -2);

		int count = 0;
		Database db = new Database();
		int zipLength = contractor.getZip().length() + 1;

		while (count < 50 && zipLength > 0) {
			// Determine Accuracy
			zipLength--;
			SelectSQL accuracyTest = new SelectSQL("accounts o");
			accuracyTest.addJoin("JOIN generalcontractors gc ON gc.genID = o.id");
			accuracyTest.addJoin("JOIN accounts c ON gc.subID = c.id");
			accuracyTest.addWhere(String.format("gc.creationDate > '%s'", DateBean.toDBFormat(changedSince.getTime())));
			accuracyTest.addWhere("o.type = 'Operator'");
			accuracyTest.addWhere("c.status IN ('Active', 'Pending')");
			if (contractor.isDemo())
				accuracyTest.addWhere("o.status in ('Active', 'Demo')");
			else
				accuracyTest.addWhere("o.status = 'Active'");
			accuracyTest.addField("COUNT(*) c");
			accuracyTest.addWhere("c.zip LIKE '" + contractor.getZip().substring(0, zipLength) + "%'");

			List<BasicDynaBean> data = db.select(accuracyTest.toString(), false);
			count = Database.toInt(data.get(0), "c");
		}

		SelectSQL inner1 = new SelectSQL("accounts o");
		inner1.addJoin("JOIN generalcontractors gc ON gc.genID = o.id");
		inner1.addJoin("JOIN accounts c ON gc.subID = c.id");
		inner1.addWhere(String.format("gc.creationDate > '%s'", DateBean.toDBFormat(changedSince.getTime())));
		inner1.addWhere("c.status IN ('Active', 'Pending')");
		inner1.addWhere("o.type = 'Operator'");
		if (contractor.isDemo())
			inner1.addWhere("o.status in ('Active', 'Demo')");
		else
			inner1.addWhere("o.status = 'Active'");
		inner1.addGroupBy("o.id");
		inner1.addField("o.id opID");
		inner1.addField("o.name");
		inner1.addField("o.status");
		inner1.addField("o.onsiteServices");
		inner1.addField("o.offsiteServices");
		inner1.addField("o.materialSupplier");
		inner1.addField("COUNT(*) total");

		SelectSQL inner2 = new SelectSQL("accounts o");
		inner2.addJoin("JOIN generalcontractors gc ON gc.genID = o.id");
		inner2.addJoin("JOIN accounts c ON gc.subID = c.id");
		inner2.addWhere(String.format("gc.creationDate > '%s'", DateBean.toDBFormat(changedSince.getTime())));
		inner2.addWhere("c.status IN ('Active', 'Pending')");
		inner2.addWhere("o.type = 'Operator'");
		if (contractor.isDemo())
			inner2.addWhere("o.status in ('Active', 'Demo')");
		else
			inner2.addWhere("o.status = 'Active'");
		inner2.addGroupBy("o.id");
		inner2.addField("o.id opID");
		inner2.addField("o.name");
		inner2.addField("o.status");
		inner2.addField("o.onsiteServices");
		inner2.addField("o.offsiteServices");
		inner2.addField("o.materialSupplier");
		inner2.addField("COUNT(*)*10 total");
		inner2.addWhere("c.zip LIKE '" + contractor.getZip().substring(0, zipLength) + "%'");

		SelectSQL sql = new SelectSQL("(" + inner1.toString() + " UNION " + inner2.toString() + ") t");
		sql.addField("opID");
		sql.addField("name");
		sql.addField("status");
		sql.addField("onsiteServices");
		sql.addField("offsiteServices");
		sql.addField("materialSupplier");
		sql.addField("SUM(total) total");
		sql.addGroupBy("opID");
		sql.addOrderBy("total DESC");
		sql.setLimit(10);

		return db.select(sql.toString(), false);
	}

	static public List<BasicDynaBean> getSimilarOperators(ContractorAccount contractor, int limit) throws SQLException {
		SelectSQL ops = new SelectSQL("generalcontractors");
		ops.addField("genID");
		ops.addWhere("subID = " + contractor.getId());

		SelectSQL sql = new SelectSQL("stats_gco_count s");
		sql.addJoin("JOIN accounts a ON s.opID2 = a.id");
		sql.addJoin("JOIN stats_gco_count s2 ON s2.opID = s.opID2 AND s2.opID2 IS NULL");
		sql.addWhere("s.opID IN (" + ops.toString() + ")");
		sql.addWhere("s.opID2 NOT IN (" + ops.toString() + ")");
		if (contractor.getStatus() == AccountStatus.Active || contractor.getStatus() == AccountStatus.Pending)
			sql.addWhere("a.status = 'Active'");
		else if (contractor.getStatus() == AccountStatus.Demo)
			sql.addWhere("a.status IN ('Active','Pending','Demo')");
		sql.addGroupBy("a.id");
		sql.addOrderBy("score DESC");
		sql.addField("(s.total*AVG(s.total)/s2.total) score");
		sql.addField("a.name");
		sql.addField("a.onsiteServices");
		sql.addField("a.offsiteServices");
		sql.addField("a.materialSupplier");
		sql.addField("a.id opID");
		sql.addField("ROUND(100*AVG(s.total)/s2.total)");
		sql.addField("SUM(s.total)");
		sql.addField("COUNT(*)");
		sql.addField("a.status");
		if (limit > 0)
			sql.setLimit(limit);

		Database db = new Database();
		return db.select(sql.toString(), false);
	}

}
