package com.picsauditing.report.version;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ReportVersionFacadeFactoryTest {

	@Test
	public void testToJsonObject_NullsDontThrow() {
		JSONObject json = new JSONObject();
		json.put("version", "6.33");

		ReportDTOFacade facade = ReportVersionFacadeFactory.getFacade(json);
		assertNotNull(facade);
	}
}
