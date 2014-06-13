package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class ProcessQBResponseXMLStrategyTest extends TestCase {

    public static final String tablePK = "12345";
    public static final String qbListId = "8003462";
    public static final int ID = 12345;
    private NodeList goodXmlChildNodes;
    private NodeList badXmlChildNodes;

    @MockitoAnnotations.Mock
    private ContractorAccountDAO contractorAccountDAO;
    @MockitoAnnotations.Mock
    private InvoiceDAO invoiceDAO;
    @MockitoAnnotations.Mock
    private Invoice invoice;


    @Test
    public void testProcessParentNodeBadXML() throws Exception {

        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, invoiceDAO);
        Node badXml = badXmlChildNodes.item(1);
        processor.processParentNode(badXml);
        assertNotSame(errorMessages.length(), 0);
    }

    @Test
    public void testProcessParentNodeGoodXML() throws Exception {

        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();

        Node goodXml = goodXmlChildNodes.item(1);
        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, invoiceDAO);
        when(invoiceDAO.find(eq(Transaction.class), anyInt())).thenReturn(invoice);
        when(invoiceDAO.save(invoice)).thenReturn(invoice);
        processor.processParentNode(goodXml);
        assertEquals("Got the following error messages: " + errorMessages.toString(), 0, errorMessages.length());
        assertNotSame(actionMessages.length(), 0);
    }

    @Test
    public void testGetNodeRequestIDAttribute() throws Exception {
        assertEquals(ProcessQBResponseXMLStrategy.getNodeRequestIDAttribute(goodXmlChildNodes.item(1)), "insert_invoice_220032");
    }

    @Test
    public void testIsStatusMessageOk() throws Exception {
        assertFalse(ProcessQBResponseXMLStrategy.isStatusMessageOk(badXmlChildNodes.item(1)));
        assertTrue(ProcessQBResponseXMLStrategy.isStatusMessageOk(goodXmlChildNodes.item(1)));
    }

    @Test
    public void testUpdateDatabaseTableContractor_CHF() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.CHF);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains("qbListCHFID"));
    }

    @Test
    public void testUpdateDatabaseTableContractor_GBP() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.GBP);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains("qbListUKID"));
    }

    @Test
    public void testUpdateDatabaseTableContractor_EUR() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.EUR);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains("qbListEUID"));
    }

    @Test
    public void testUpdateDatabaseTableContractor_USD() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.USD);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains("qbListID"));
    }

    @Test
    public void testUpdateDatabaseTableContractor_CAD() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.CAD);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains("qbListCAID"));
    }

    @Test
    public void testUpdateDatabaseTableContractor_Others() throws Exception {
        StringBuilder actionMessages = new StringBuilder(), errorMessages = new StringBuilder();
        ContractorAccount contractorAccount = mock(ContractorAccount.class);
        Country country = mock(Country.class);
        when(contractorAccount.getCountry()).thenReturn(country);
        when(contractorAccount.getCurrency()).thenReturn(Currency.TRY);
        when(contractorAccountDAO.find(ID)).thenReturn(contractorAccount);

        ProcessQBResponseXMLInvoiceAddOrUpdate processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages, errorMessages, contractorAccountDAO);
        processor.setContractorAccountDAO(contractorAccountDAO);

        Whitebox.invokeMethod(processor, "updateDatabaseTableContractor", qbListId, tablePK, true);

        verify(contractorAccountDAO).find(anyInt());
        String actionMessage = processor.getActionMessages().toString();
        assertTrue(actionMessages.toString().contains(""));
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
