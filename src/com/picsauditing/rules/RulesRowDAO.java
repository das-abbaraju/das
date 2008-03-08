package com.picsauditing.rules;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RulesRowDAO extends com.picsauditing.PICS.DataBean  {
	public List<RulesRowBean> getRowsByTable(String tableName) throws Exception {
		
		List<RulesRowBean> rows = new ArrayList<RulesRowBean>();
		String selectQuery = "SELECT * FROM rules_row WHERE tableName = '"+eqDB(tableName)+"' ORDER BY sequence, rowID";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				rows.add(setFromResultSet(SQLResult));
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
		return rows;
	}
	
	private RulesRowBean setFromResultSet(ResultSet SQLResult) throws Exception {
		RulesRowBean row = new RulesRowBean();
		row.setNotes(SQLResult.getString("notes"));
		row.setResult(SQLResult.getString("resultValue"));
		row.setOperator1(getOperator(SQLResult, "operator1"));
		row.setOperator2(getOperator(SQLResult, "operator2"));
		row.setOperator3(getOperator(SQLResult, "operator3"));
		row.setOperator4(getOperator(SQLResult, "operator4"));
		row.setOperator5(getOperator(SQLResult, "operator5"));
		row.setValue1(SQLResult.getString("value1"));
		row.setValue2(SQLResult.getString("value2"));
		row.setValue3(SQLResult.getString("value3"));
		row.setValue4(SQLResult.getString("value4"));
		row.setValue5(SQLResult.getString("value5"));
		return row;
	}
	
	private RulesOperator getOperator(ResultSet SQLResult, String columnName) {
		try {
			String value = SQLResult.getString(columnName);
			return RulesOperator.valueOf(value);
		} catch (Exception e) {
			return RulesOperator.Any;
		}
	}
}
