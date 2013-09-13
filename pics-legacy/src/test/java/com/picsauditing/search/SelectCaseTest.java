package com.picsauditing.search;

import org.junit.Assert;
import org.junit.Test;

public class SelectCaseTest {

	@Test
	public void testSimpleSwitch() throws Exception {
		SelectCase sCase = new SelectCase("field");
		sCase.addCondition("'Y'", "1");
		Assert.assertEquals("CASE field WHEN 'Y' THEN 1 END", sCase.toString());
	}

	@Test
	public void testSimple() throws Exception {
		SelectCase sCase = new SelectCase();
		sCase.addCondition("field == 'Y'", "1");
		Assert.assertEquals("CASE WHEN field == 'Y' THEN 1 END", sCase.toString());
	}

	@Test
	public void testElse() throws Exception {
		SelectCase sCase = new SelectCase();
		sCase.setElse("0");
		Assert.assertEquals("CASE ELSE 0 END", sCase.toString());
	}

	@Test
	public void testDba() throws Exception {
		SelectCase sCase = new SelectCase();
		sCase.setElse("a.dbaName");
		sCase.addCondition("a.dbaName IS NULL", "a.name");
		sCase.addCondition("a.dbaName = ''", "a.name");
		Assert.assertEquals("CASE WHEN a.dbaName IS NULL THEN a.name WHEN a.dbaName = '' THEN a.name "
				+ "ELSE a.dbaName END", sCase.toString());
	}
}
