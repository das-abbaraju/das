package com.picsauditing.search;

import junit.framework.TestCase;

public class SelectSQLTest extends TestCase {
	protected SelectSQL builder = new SelectSQL();
	
	public void testBasic() {
		builder.setFromTable("test_from");
		builder.addField("field1");
		builder.addField("field2");
		builder.addJoin("JOIN test_join USING (field1)");
		builder.addWhere("field1 = 'foo' OR field2 = 'bar'");
		builder.addOrderBy("field1");
		builder.addOrderBy("field2 DESC");
		builder.setSQL_CALC_FOUND_ROWS(true);
		builder.setStartRow(100);
		builder.setLimit(10);
		String sql = builder.toString();
		assertEquals(178, sql.length());
    }
}
