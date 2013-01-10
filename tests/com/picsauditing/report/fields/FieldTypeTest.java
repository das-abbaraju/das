package com.picsauditing.report.fields;

import org.junit.Assert;
import org.junit.Test;

public class FieldTypeTest {

	@Test
	public void testString() throws Exception {
		Assert.assertEquals(FilterType.String, FieldType.String.getFilterType());
		Assert.assertEquals(DisplayType.LeftAlign, FieldType.String.getDisplayType());
	}
}
