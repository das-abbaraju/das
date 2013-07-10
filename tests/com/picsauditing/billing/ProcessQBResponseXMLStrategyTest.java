package com.picsauditing.billing;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;


/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/9/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLStrategyTest extends TestCase {

	private NodeList goodXmlChildNodes;
	private NodeList badXmlChildNodes;


	@Test
	public void testProcessParentNodeTestFailure() throws Exception {
		  Node badXml = badXmlChildNodes.item(1);

	}

	@Test
	public void testUpdateInvoice() throws Exception {

	}

	@Test
	public void testUpdateContractor() throws Exception {

	}

	@Test
	public void testGetNodeRequestIDAttribute() throws Exception {
		     assertEquals(ProcessQBResponseXMLStrategy.getNodeRequestIDAttribute(goodXmlChildNodes.item(1)),"insert_invoice_220032");
	}

	@Test
	public void testIsStatusMessageOk() throws Exception {
		 assertFalse(ProcessQBResponseXMLStrategy.isStatusMessageOk(badXmlChildNodes.item(1)));
		assertTrue(ProcessQBResponseXMLStrategy.isStatusMessageOk(goodXmlChildNodes.item(1)));
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();    //To change body of overridden methods use File | Settings | File Templates.

		URL goodXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlGood.xml");
		File goodXmlFile = new File(goodXmlUrl.getPath());
		InputStream goodXmlInputStream = new FileInputStream(goodXmlFile);
		StringBuilder goodXmlActionMessages = new StringBuilder(), goodXmlErrorMessages = new StringBuilder();
		goodXmlChildNodes = ProcessQBResponseXMLStrategy.findQBXMLMsgsRsChildNodes(goodXmlInputStream,goodXmlActionMessages,goodXmlErrorMessages);

		URL badXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlBad.xml");
		File badXmlFile = new File(badXmlUrl.getPath());
		InputStream badXmlInputStream = new FileInputStream(badXmlFile);
		StringBuilder badXmlActionMessages = new StringBuilder(), badXmlErrorMessages = new StringBuilder();
		badXmlChildNodes = ProcessQBResponseXMLStrategy.findQBXMLMsgsRsChildNodes(badXmlInputStream,badXmlActionMessages,badXmlErrorMessages);

	}
}
