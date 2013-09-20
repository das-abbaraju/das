package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.BusinessUnit;
import com.picsauditing.jpa.entities.Country;
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

	@Test
	public void testSubscriptionFooterShouldConvertToSingleLineAddressFormat() throws Exception {

		String template = "$contractor.country.businessUnit.displayName <br />\n" +
				"$contractor.country.businessUnit.addressSingleLine <br />" +
				"Tel: $contractor.country.csrPhone <br />\n" +
				"Website: http://www.picsauditing.com <br />\n" +
				"Email: $contractor.country.csrEmail <br />";

		Map<String, Object> data = new HashMap<>();

		BusinessUnit ukeu = new BusinessUnit();
		ukeu.setDisplayName("Pacific Industrial Contractor Screening, Ltd");
		ukeu.setAddress("Suite 220\nVandervell House\nVanwall Business Park\nMaidenhead\nSL6 4UB\nUnited Kingdom");

		Country gb = new Country();
		gb.setCsrPhone("+44 (0) 1628 450400");
		gb.setCsrEmail("info@picsauditing.com");
		gb.setBusinessUnit(ukeu);

		ContractorAccount contractor = new ContractorAccount();
		contractor.setCountry(gb);

		data.put("contractor", contractor);

		String result = VelocityAdaptor.mergeTemplate(template, data);
		assertEquals("Pacific Industrial Contractor Screening, Ltd <br />\n" +
				"Suite 220, Vandervell House, Vanwall Business Park, Maidenhead, SL6 4UB, United Kingdom <br />Tel: +44 (0) 1628 450400 <br />\n" +
				"Website: http://www.picsauditing.com <br />\n" +
				"Email: info@picsauditing.com <br />", result);
	}

}
