package com.intuit.developer.adaptors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.util.SpringUtils;


public class QBXmlAdaptor {

	protected static JAXBContext jc = null;

	private boolean proceed = true;  //keep going with the integration
	private boolean repeat = false; // do this step again

	private ContractorAccountDAO contractorDao;
	private InvoiceDAO invoiceDao;

	
	
	static {
		try {
			System.out.println("setting up jaxb context");
			jc = JAXBContext.newInstance("com.picsauditing.quickbooks.qbxml");
			System.out.println("finished setting up jaxb context");
			
		}
		catch(Exception e) {
			System.out.println("ERROR SETTING UP JAXBContext.  Quickbooks integration will not work");
		}
	}
	
	
	

	protected StringWriter makeWriter() {
		StringWriter stringWriter = new StringWriter();
		
		//stringWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?qbxml version=\"8.0\"?>");
		stringWriter.write("<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?>");
		
		return stringWriter;
	}
	protected Marshaller makeMarshaller() throws JAXBException {
		
		Marshaller createMarshaller = jc.createMarshaller();
		createMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration",Boolean.FALSE);
		return createMarshaller;
	}

	public String getQbXml( QBSession currentSession ) throws Exception {
		return "<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?><QBXML><QBXMLMsgsRq onError=\"stopOnError\"/></QBXML>";
	}
	
	public Object parseQbXml( QBSession currentSession, String qbXml ) throws Exception {
		return null;
	}
	
	
	protected ContractorAccountDAO getContractorDao() {
		if( contractorDao == null )
			contractorDao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		
		return contractorDao;
	}

	protected InvoiceDAO getInvoiceDao() {
		if( invoiceDao == null )
			invoiceDao = (InvoiceDAO) SpringUtils.getBean("InvoiceDAO");
		
		return invoiceDao;
	}
	
	public boolean isProceed() {
		return proceed;
	}

	public void setProceed(boolean proceed) {
		this.proceed = proceed;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
}
