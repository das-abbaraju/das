package com.picsauditing.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

public class BillingRS {
	private RulesTable rs;
	
	public int calculate(int facilityCount, HashSet<Integer> facilities, int riskLevel, boolean mustPay, boolean isOnlyCerts) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("facilityCount", facilityCount);
		parameters.put("facilities", facilities);
		parameters.put("riskLevel", riskLevel);
		parameters.put("mustPay", mustPay);
		parameters.put("isOnlyCerts", isOnlyCerts);
		int result = (Integer)rs.evaluate(parameters);
		return result;
	}

	public void setUp(List<RuleRowBean> rows) throws Exception {
		rs = new RulesTable();
		// Setup the columns
		rs.addColumn("mustPay", "Boolean");
		rs.addColumn("needsAudit", "Boolean");
		rs.addColumn("facilities", "Collection<Integer>");
		rs.addColumn("facilityCount", "Integer");
		rs.setReturnType("Integer"); // Price
		
		for (RuleRowBean row: rows) {
			RulesRow rsRow = new RulesRow();
			rsRow.addQuestion("mustPay", new ResultSetQuestion(row.getOperator1(), row.getValue1()));
			rsRow.addQuestion("needsAudit", new ResultSetQuestion(row.getOperator2(), row.getValue2()));
			rsRow.addQuestion("facilities", new ResultSetQuestion(row.getOperator3(), row.getValue3()));
			rsRow.addQuestion("facilityCount", new ResultSetQuestion(row.getOperator4(), row.getValue4()));
			rsRow.setValue(row.getResult());
			rs.addRow(rsRow);
		}
	}
	
}
