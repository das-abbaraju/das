package com.intuit.developer.adaptors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.BillAddress;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class QBXmlAdaptor {

	protected static JAXBContext jc = null;

	private boolean proceed = true; // keep going with the integration
	private boolean repeat = false; // do this step again

	private ContractorAccountDAO contractorDao;
	private InvoiceDAO invoiceDao;
	private InvoiceItemDAO invoiceItemDao;
	private AppPropertyDAO appPropertyDao;

	static {
		try {
			System.out.println("setting up jaxb context");
			jc = JAXBContext.newInstance("com.picsauditing.quickbooks.qbxml");
			System.out.println("finished setting up jaxb context");

		} catch (Exception e) {
			System.out.println("ERROR SETTING UP JAXBContext.  Quickbooks integration will not work");
		}
	}

	protected StringWriter makeWriter() {
		StringWriter stringWriter = new StringWriter();

		// stringWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?qbxml version=\"8.0\"?>");
		stringWriter.write("<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?>");

		return stringWriter;
	}

	protected Marshaller makeMarshaller() throws JAXBException {

		Marshaller createMarshaller = jc.createMarshaller();
		createMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		return createMarshaller;
	}

	public String getQbXml(QBSession currentSession) throws Exception {
		return "<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?><QBXML><QBXMLMsgsRq onError=\"stopOnError\"/></QBXML>";
	}

	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {
		return null;
	}

	protected ContractorAccountDAO getContractorDao() {
		if (contractorDao == null)
			contractorDao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");

		return contractorDao;
	}

	protected InvoiceDAO getInvoiceDao() {
		if (invoiceDao == null)
			invoiceDao = (InvoiceDAO) SpringUtils.getBean("InvoiceDAO");

		return invoiceDao;
	}

	protected InvoiceItemDAO getInvoiceItemDao() {
		if (invoiceItemDao == null)
			invoiceItemDao = (InvoiceItemDAO) SpringUtils.getBean("InvoiceItemDAO");

		return invoiceItemDao;
	}

	protected AppPropertyDAO getAppPropertyDao() {
		if (appPropertyDao == null)
			appPropertyDao = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");

		return appPropertyDao;
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

	static public String nullSafeSubString(String in, int start, int end) {
		if (in == null)
			return "";

		if (in.length() < start + end) {
			return in.substring(start);
		}

		return in.substring(start, end);
	}

	static public String nullSafePhoneFormat(String in) {
		if (in == null)
			return "";

		in = in.trim();
		in = in.replaceAll("  ", " ");
		in = in.toUpperCase().replaceAll("Extension ", "x");
		in = in.toUpperCase().replaceAll("Extension", "x");
		in = in.toUpperCase().replaceAll("EXT. ", "x");
		in = in.toUpperCase().replaceAll("EXT.", "x");
		in = in.toUpperCase().replaceAll("EXT ", "x");
		in = in.toUpperCase().replaceAll("EXT", "x");
		in = in.toUpperCase().replaceAll("X ", "x");

		return nullSafeSubString(in, 0, 20);
	}

	static public BillAddress updateBillAddress(ContractorAccount contractor, BillAddress billAddress) {
		if (Strings.isEmpty(contractor.getAddress())) {
			billAddress.setAddr1(nullSafeSubString(contractor.getName(), 0, 41));
			billAddress.setAddr2(nullSafeSubString(contractor.getContact(), 0, 41));
			billAddress.setAddr3(nullSafeSubString(contractor.getAddress(), 0, 41));
			billAddress.setCity(contractor.getCity());
			if(contractor.getState() != null)
				billAddress.setState(contractor.getState().getIsoCode());
			billAddress.setPostalCode(contractor.getZip());
		} else {
			billAddress.setAddr1(nullSafeSubString(contractor.getName(), 0, 41));
			billAddress.setAddr2("c/o " +
					nullSafeSubString(contractor.getBillingContact(), 0, 39));
			billAddress.setAddr3(nullSafeSubString(contractor.getBillingAddress(), 0, 41));
			billAddress.setCity(contractor.getBillingCity());
			if(contractor.getBillingState() != null)
				billAddress.setState(contractor.getBillingState().getIsoCode());
			billAddress.setPostalCode(contractor.getBillingZip());
		}
		billAddress.setCountry(contractor.getCountry().getName());
		return billAddress;
	}
}
