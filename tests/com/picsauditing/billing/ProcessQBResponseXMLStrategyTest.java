package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;


public class ProcessQBResponseXMLStrategyTest extends TestCase {

	private NodeList goodXmlChildNodes;
	private NodeList badXmlChildNodes;

	@MockitoAnnotations.Mock
	private ContractorAccountDAO contractorAccountDAO;
	@MockitoAnnotations.Mock
	private InvoiceDAO invoiceDAO;
	@MockitoAnnotations.Mock
	private Invoice invoice;


	@Test
	public void testProcessParentNode() throws Exception {

		ProcessQBResponseXMLInvoice processor = new ProcessQBResponseXMLInvoice();
		processor.setInvoiceDAO(invoiceDAO);
		when(invoiceDAO.find(anyInt())).thenReturn(invoice);
		when(invoiceDAO.save(invoice)).thenReturn(invoice);
		Node badXml = badXmlChildNodes.item(1);
		StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
		processor.processParentNode(badXml, ProcessQBResponseXMLInvoice.DETAIL_NODE_NAME, ProcessQBResponseXMLInvoice.REQUEST_TYPE, actionMessages, errorMessages);
		assertNotSame(errorMessages.length(), 0);

		Node goodXml = goodXmlChildNodes.item(1);
		actionMessages = new StringBuilder();
		errorMessages = new StringBuilder();
		processor.processParentNode(goodXml, ProcessQBResponseXMLInvoice.DETAIL_NODE_NAME, ProcessQBResponseXMLInvoice.REQUEST_TYPE, actionMessages, errorMessages);
		assertEquals(errorMessages.length(), 0);
		assertNotSame(actionMessages.length(), 0);

	}

	/*
	@Test
	public void testUpdateInvoice() throws Exception {
		String qbListID = "afijaweoifjawoeifjaoiwejfoawej";
		String invoiceID = "12345";
		StringBuilder actionMessages = new StringBuilder();
		ProcessQBResponseXMLStrategy processor = new ProcessQBResponseXMLInvoice();
		processor.setInvoiceDAO(invoiceDAO);
		processor.setContractorAccountDAO(contractorAccountDAO);
		when(invoiceDAO.find(anyInt())).thenReturn(invoice);
		when(invoiceDAO.save(invoice)).thenReturn(invoice);
		processor.updateInvoice(qbListID,invoiceID,actionMessages);
		assertEquals(qbListID,invoice.getQbListID());
		assertEquals(invoiceID,invoice.getId());
		assertNotSame(actionMessages.length(),0);
	}

	@Test
	public void testUpdateContractor() throws Exception {

	}
      */

	@Test
	public void testGetNodeRequestIDAttribute() throws Exception {
		assertEquals(ProcessQBResponseXMLStrategy.getNodeRequestIDAttribute(goodXmlChildNodes.item(1)), "insert_invoice_220032");
	}

	@Test
	public void testIsStatusMessageOk() throws Exception {
		assertFalse(ProcessQBResponseXMLStrategy.isStatusMessageOk(badXmlChildNodes.item(1)));
		assertTrue(ProcessQBResponseXMLStrategy.isStatusMessageOk(goodXmlChildNodes.item(1)));
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
		MockitoAnnotations.initMocks(this);

		URL goodXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlGood.xml");
		File goodXmlFile = new File(goodXmlUrl.getPath());
		InputStream goodXmlInputStream = new FileInputStream(goodXmlFile);
		StringBuilder goodXmlActionMessages = new StringBuilder(), goodXmlErrorMessages = new StringBuilder();
		goodXmlChildNodes = ProcessQBResponseXMLStrategy.findQBXMLMsgsRsChildNodes(goodXmlInputStream, goodXmlActionMessages, goodXmlErrorMessages);

		URL badXmlUrl = Thread.currentThread().getContextClassLoader().getResource("com/picsauditing/billing/processQbResponseXmlBad.xml");
		File badXmlFile = new File(badXmlUrl.getPath());
		InputStream badXmlInputStream = new FileInputStream(badXmlFile);
		StringBuilder badXmlActionMessages = new StringBuilder(), badXmlErrorMessages = new StringBuilder();
		badXmlChildNodes = ProcessQBResponseXMLStrategy.findQBXMLMsgsRsChildNodes(badXmlInputStream, badXmlActionMessages, badXmlErrorMessages);

	}
}
