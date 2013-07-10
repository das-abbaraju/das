package com.picsauditing.billing;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;


/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/9/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLStrategyTest extends TestCase {

	private Document goodXmlObject;
	private Document badXmlObject;


	@Test
	public void testProcessParentNodeTestFailure() throws Exception {
		System.out.println(goodXmlObject.getDocumentElement().getNodeName());

	}

	@Test
	public void testUpdateInvoice() throws Exception {

	}

	@Test
	public void testUpdateContractor() throws Exception {

	}

	@Test
	public void testGetNodeRequestIDAttribute() throws Exception {

	}

	@Test
	public void testIsStatusMessageOk() throws Exception {

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();    //To change body of overridden methods use File | Settings | File Templates.

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

		URL goodXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlGood.xml");
		File goodXmlFile = new File(goodXmlUrl.getPath());
		goodXmlObject = docBuilder.parse(new FileInputStream(goodXmlFile));

		URL badXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlBad.xml");
		File badXmlFile = new File(badXmlUrl.getPath());
		badXmlObject = docBuilder.parse(new FileInputStream(badXmlFile));
	}
}
