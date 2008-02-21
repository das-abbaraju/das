package com.picsauditing.resultset;

import java.util.HashMap;
import java.util.HashSet;

public class BillingRS {
	private ResultSet rs;
	
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

	public void setUp() throws Exception {
		rs = new ResultSet();
		// Setup the columns
		rs.addColumn("facilityCount", "Integer");
		rs.addColumn("facilities", "Collection<Integer>");
		rs.addColumn("riskLevel", "Integer");
		rs.addColumn("mustPay", "Boolean");
		rs.addColumn("isOnlyCerts", "Boolean");
		rs.setReturnType("Integer"); // Price
		
		ResultSetRow row;
		// Setup each row
		
		row = new ResultSetRow();
		row.addQuestion("mustPay", new ResultSetQuestion(ResultSetOperator.IsFalse));
		row.setValue(0);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("isOnlyCerts", new ResultSetQuestion(ResultSetOperator.IsTrue));
		row.setValue(0);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("riskLevel", new ResultSetQuestion(ResultSetOperator.Equals, 1));
		row.setValue(1699);
		rs.addRow(row);

		row = new ResultSetRow();
		row.addQuestion("facilities", new ResultSetQuestion(ResultSetOperator.Contains, 100)); // Motorola
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.Equals, 1));
		row.setValue(99);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 20));
		row.setValue(1999);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 13));
		row.setValue(1699);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 9));
		row.setValue(1299);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 5));
		row.setValue(999);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 2));
		row.setValue(699);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.GreaterThanEqual, 1));
		row.setValue(399);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("facilityCount", new ResultSetQuestion(ResultSetOperator.Equals, 0));
		row.setValue(0);
		rs.addRow(row);
	}
	
}
