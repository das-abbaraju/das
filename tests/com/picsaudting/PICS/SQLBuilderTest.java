package com.picsaudting.PICS;

import junit.framework.TestCase;
import com.picsauditing.PICS.SQLBuilder;

/**
 * @author Trevor
 *
 */
public class SQLBuilderTest extends TestCase {
	protected SQLBuilder builder = new SQLBuilder();
	
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
