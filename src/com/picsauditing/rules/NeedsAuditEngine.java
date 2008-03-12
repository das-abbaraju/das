package com.picsauditing.rules;

import java.util.HashMap;
import java.util.List;

public class NeedsAuditEngine {
	protected RulesSet rs;
	
	public boolean calculate(String auditType, int risk, int facility) throws Exception {
		setUp();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("auditType", auditType);
		parameters.put("risk", risk);
		parameters.put("facility", facility);
		boolean result = (Boolean)rs.evaluate(parameters);
		return result;
	}

	public void setUp(List<RulesRowBean> rows) throws Exception {
		rs = new RulesSet();
		// Setup the columns
		rs.setReturnType("Boolean"); // Needs Audit
		rs.addColumn("auditType", "String");
		rs.addColumn("risk", "Integer");
		rs.addColumn("facility", "Integer");
		
		for (RulesRowBean row: rows) {
			RulesRow rsRow = new RulesRow();
			rsRow.setValue(row.getResult());
			rsRow.addQuestion("auditType", new RulesQuestion(row.getOperator1(), row.getValue1()));
			rsRow.addQuestion("risk", new RulesQuestion(row.getOperator2(), row.getValue2()));
			rsRow.addQuestion("facility", new RulesQuestion(row.getOperator3(), row.getValue3()));
			rs.addRow(rsRow);
		}
	}
	public void setUp() throws Exception {
		if (rs != null) return;
		RulesRowDAO dao = new RulesRowDAO();
		List<RulesRowBean> rows = dao.getRowsByTable("needsAudit");
		setUp(rows);
	}
}
