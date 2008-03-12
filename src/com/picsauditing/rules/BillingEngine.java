package com.picsauditing.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BillingEngine {
	protected RulesSet rs;
	
	public int calculate(boolean mustPay, boolean needsAudit, HashSet<Integer> facilities, int facilityCount) throws Exception {
		setUp();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("mustPay", mustPay);
		parameters.put("needsAudit", needsAudit);
		parameters.put("facilities", facilities);
		parameters.put("facilityCount", facilityCount);
		int result = (Integer)rs.evaluate(parameters);
		return result;
	}
	public int calculate(int facilityCount) throws Exception {
		return calculate(true, true, new HashSet<Integer>(), facilityCount);
	}

	public void setUp(List<RulesRowBean> rows) throws Exception {
		rs = new RulesSet();
		// Setup the columns
		rs.addColumn("mustPay", "Boolean");
		rs.addColumn("needsAudit", "Boolean");
		rs.addColumn("facilities", "Collection<Integer>");
		rs.addColumn("facilityCount", "Integer");
		rs.setReturnType("Integer"); // Price
		
		for (RulesRowBean row: rows) {
			RulesRow rsRow = new RulesRow();
			rsRow.addQuestion("mustPay", new RulesQuestion(row.getOperator1(), row.getValue1()));
			rsRow.addQuestion("needsAudit", new RulesQuestion(row.getOperator2(), row.getValue2()));
			rsRow.addQuestion("facilities", new RulesQuestion(row.getOperator3(), row.getValue3()));
			rsRow.addQuestion("facilityCount", new RulesQuestion(row.getOperator4(), row.getValue4()));
			rsRow.setValue(row.getResult());
			rs.addRow(rsRow);
		}
	}
	public void setUp() throws Exception {
		if (rs != null) return;
		RulesRowDAO dao = new RulesRowDAO();
		List<RulesRowBean> rows = dao.getRowsByTable("pricing");
		setUp(rows);
	}
}
