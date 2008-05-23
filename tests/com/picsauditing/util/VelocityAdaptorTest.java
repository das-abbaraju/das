package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.picsauditing.jpa.entities.ContractorAccount;

import junit.framework.TestCase;

public class VelocityAdaptorTest extends TestCase {

	public VelocityAdaptorTest(String name) {
		super(name);
	}

	@Test
	public void testMerge() throws Exception {
		String template = "Hello, $contractor.name";
		Map<String, Object> data = new HashMap<String, Object>();
		ContractorAccount contractor = new ContractorAccount();
		contractor.setName("Trevor Test");
		data.put("contractor", contractor);
		
		String result = VelocityAdaptor.mergeTemplate(template, data);
		assertEquals("Hello, Trevor Test", result);
		//System.out.println(result);
	}

}
