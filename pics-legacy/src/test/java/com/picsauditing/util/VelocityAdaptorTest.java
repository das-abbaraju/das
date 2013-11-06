package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.BusinessUnit;
import com.picsauditing.jpa.entities.Country;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.ContractorAccount;

import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class VelocityAdaptorTest {

	private VelocityAdaptor velocityAdaptor;

	// VelocityEngine.evaluate will blow on the first occurrence of "$operatörer". "ö" (o with diaeresis) is invalid in a velocity variable.
	public static final String EmailTemplate_107_translatedBody_sv = "<SubscriptionHeader>\n" +
			"Nedan ser ni alla leverantörer som nyligen valt#if( ${operators.size()} > 1 ) en av#end era anlägg#if( ${operators.size()} > 1 )ningar \n" +
			"#else\n" +
			"y#end som arbetsplats.\n" +
			"<br/><br/>\n" +
			"#förvarje( $operatör i $operators.keySet() )\n" +
			"#om( $operatörer.får($operatör).storlek() > 0 )\n" +
			"<stark>Anläggning: <em>${operator.name}</em></strong><br/>\n" +
			"<table stil=\"gräns-komprimering: komprimering; gräns: 2px fast #003768; bakgrund: #f9f9f9;\">\n" +
			" <thead>\n" +
			" <tr stil=\"vertikaljustering: mellan; teckenstorlek: 13px;teckensnitt-vikt: fetstil; bakgrund: #003768; färg: #FFF;\">\n" +
			"   <td stil=\"gräns: 1px fast #e0e0e0; padding: 4px;\">Leverantörsnamn</td>\n" +
			"   <td stil=\"gräns: 1px fast #e0e0e0; padding: 4px;\">Tillagt datum</td>\n" +
			"  </tr>\n" +
			" </thead>\n" +
			" <tbody>\n" +
			"  #foreach( $contractor in $operators.get($operator).keySet() )\n" +
			"  <tr stil=\"marginal:0px\">\n" +
			"   <td stil=\"gräns: 1px fast #A84D10; padding: 4px; tecken-storlek: 13px;\"><a href=\"https://www.picsorganizer.com/ContractorView.action?id=$contractor.id#if( ${operators.size()} > 1 )&opID=${operator.id}#end\">$contractor.name</a></td>\n" +
			"   <td stil=\"gräns: 1px fast #A84D10; padding: 4px; tecken-storlek: 13px;\">$pics_dateTool.format(\"MM/dd/yy HH:mm\", $operators.get($operator).get($contractor).creationDate)</td>\n" +
			"  </tr>\n" +
			"  #end\n" +
			" </tbody>\n" +
			"</table>\n" +
			"<br/><br/>\n" +
			"#end\n" +
			"#end\n" +
			"<TimeStampDisclaimer>\n" +
			"<SubscriptionFooter>";

	public static final String EmailTemplate_107_translatedBody_sv_basic = "#förvarje( $operatör i $operators.keySet() )\n" +
			"#om( $operatörer.får($operatör).storlek() > 0 )\n" +
			"<stark>Anläggning: <em>${operator.name}</em></strong><br/>";

	@Before
	public void setUp() throws Exception {
		velocityAdaptor = new VelocityAdaptor();
		MockitoAnnotations.initMocks(this);
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

	@Test(expected = TemplateParseException.class)
	public void testMerge_badTemplate_shouldThrowAHelpfulException_PICS_13365() throws Exception {
		String template = EmailTemplate_107_translatedBody_sv;
		Map<String, Object> data = new HashMap<>();
		ContractorAccount contractor = new ContractorAccount();
		contractor.setName("Joe Sixpack");
		data.put("contractor", contractor);

		velocityAdaptor.merge(template, data);
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
