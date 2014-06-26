package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.quickbooks.qbxml.BillAddress;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

public class QBXmlAdaptor {

    public static final String EURO_FULL_NAME = "Euro";
    public static final String CHF_FULL_NAME = "Swiss Franc";
    public static final String GBP_FULL_NAME = "British Pound Sterling";
    public static final String PLN_FULL_NAME = "Polish Zloty";
    protected static JAXBContext jc = null;

    public static final String ACCOUNTS_RECEIVABLE_EURO = "Accounts Receivable EURO";
    public static final String ACCOUNTS_RECEIVABLE = "Accounts Receivable";
    public static final String ACCOUNTS_RECEIVABLE_CHF = "Accounts Receivable - CHF";
    public static final String ACCOUNTS_RECEIVABLE_PLN = "Accounts Receivable - PLN";


    private boolean proceed = true; // keep going with the integration
	private boolean repeat = false; // do this step again

	private ContractorAccountDAO contractorDao;
	private InvoiceDAO invoiceDao;
	private InvoiceItemDAO invoiceItemDao;
	private AppPropertyDAO appPropertyDao;

	private final static Logger logger = LoggerFactory.getLogger(QBXmlAdaptor.class);
	static {
		try {
			logger.info("setting up jaxb context");
			jc = JAXBContext.newInstance("com.picsauditing.quickbooks.qbxml");
			logger.info("finished setting up jaxb context");

		} catch (Exception e) {
			logger.error("ERROR SETTING UP JAXBContext.  Quickbooks integration will not work");
		}
	}

	protected StringWriter makeWriter() {
		StringWriter stringWriter = new StringWriter();
		AppProperty utf = getAppPropertyDao().find("PICSQBLOADER.use_utf");
		if (utf != null && utf.getValue().equals("Y")) {
			stringWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><?qbxml version=\"8.0\"?>");
			// .write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><?qbxml version=\"8.0\"?>");
		} else {
			stringWriter.write("<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?>");
		}

		return stringWriter;
	}

	protected Marshaller makeMarshaller() throws JAXBException {

		Marshaller createMarshaller = jc.createMarshaller();
		createMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);

		// This is for the stupidity of QuickBooks, delete under penalty of
		// death while
		// we are still using QuickBooks.
		AppProperty property = getAppPropertyDao().find(AppProperty.QB_JAXB_ENCODING);
		if ("Y".equals(property.getValue())) {
			createMarshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII"); // PICS-7937
		}

		return createMarshaller;
	}

	public String getQbXml(QBSession currentSession) throws Exception {
		return "<?xml version=\"1.0\" ?><?qbxml version=\"8.0\"?><QBXML><QBXMLMsgsRq onError=\"stopOnError\"/></QBXML>";
	}

	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {
		return null;
	}

	protected ContractorAccountDAO getContractorDao() {
		if (contractorDao == null) {
			contractorDao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		}

		return contractorDao;
	}

	protected InvoiceDAO getInvoiceDao() {
		if (invoiceDao == null) {
			invoiceDao = (InvoiceDAO) SpringUtils.getBean("InvoiceDAO");
		}

		return invoiceDao;
	}

	protected InvoiceItemDAO getInvoiceItemDao() {
		if (invoiceItemDao == null) {
			invoiceItemDao = (InvoiceItemDAO) SpringUtils.getBean("InvoiceItemDAO");
		}

		return invoiceItemDao;
	}

	protected AppPropertyDAO getAppPropertyDao() {
		if (appPropertyDao == null) {
			appPropertyDao = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
		}

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
		if (in == null) {
			return "";
		}

		if (in.length() < start + end) {
			return in.substring(start);
		}

		return in.substring(start, end);
	}

	static public String nullSafeZip(String zipCode, Country country) {
		if (country == null) {
			return nullSafeSubString(zipCode, 0, 9);
		}
		if (country.isUS()) {
			return nullSafeSubString(zipCode, 0, 6);
		}
		if (country.isCanada()) {
			return nullSafeSubString(zipCode, 0, 7);
		}
		if (country.isFrance()) {
			return nullSafeSubString(zipCode, 0, 8);
		}

		return nullSafeSubString(zipCode, 0, 9);
	}

	static public String nullSafeCity(String city, Country country) {
		if (country == null) {
			return nullSafeSubString(city, 0, 23);
		}
		if (country.isUS()) {
			return nullSafeSubString(city, 0, 26);
		}
		if (country.isCanada()) {
			return nullSafeSubString(city, 0, 25);
		}
		if (country.isFrance()) {
			return nullSafeSubString(city, 0, 24);
		}

		return nullSafeSubString(city, 0, 23);
	}

	static public String nullSafePhoneFormat(String in) {
		if (in == null) {
			return "";
		}

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

	public static BillAddress updateBillAddress(ContractorAccount contractor, BillAddress billAddress) {
		if (!Strings.isEmpty(contractor.getBillingAddress()) && !Strings.isEmpty(contractor.getBillingCity())) {
			billAddress.setAddr1(nullSafeSubString(contractor.getName(), 0, 41));
			List<User> users = contractor.getUsersByRole(OpPerms.ContractorBilling);
			if (CollectionUtils.isNotEmpty(users)) {
				billAddress.setAddr2("c/o "
						+ nullSafeSubString(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getName(), 0,
								37));
			}
			billAddress.setAddr3(nullSafeSubString(contractor.getBillingAddress(), 0, 41));
			billAddress.setCity(nullSafeSubString(nullSafeCity(contractor.getBillingCity(), contractor.getCountry()),
					0, 41));
			if (contractor.getBillingCountrySubdivision() != null) {
				String isoCode = contractor.getBillingCountrySubdivision().getIsoCode();
				billAddress.setState(nullSafeSubString(StringUtils.substring(isoCode, -2), 0, 21));
			}
			billAddress.setPostalCode(nullSafeSubString(
					nullSafeZip(contractor.getBillingZip(), contractor.getCountry()), 0, 13));
		} else {
			billAddress.setAddr1(nullSafeSubString(contractor.getName(), 0, 41));
			User primary = null;
			if (contractor.getPrimaryContact() != null) {
				primary = contractor.getPrimaryContact();
			} else {
				primary = contractor.getUsersByRole(OpPerms.ContractorBilling).get(0);
			}

			billAddress.setAddr2(nullSafeSubString(primary.getName(), 0, 41));
			billAddress.setAddr3(nullSafeSubString(contractor.getAddress(), 0, 41));
			billAddress.setCity(nullSafeSubString(nullSafeCity(contractor.getCity(), contractor.getCountry()), 0, 41));
			if (contractor.getCountrySubdivision() != null) {
				String isoCode = contractor.getCountrySubdivision().getIsoCode();
				billAddress.setState(nullSafeSubString(StringUtils.substring(isoCode, -2), 0, 21));
			}
			billAddress.setPostalCode(nullSafeSubString(nullSafeZip(contractor.getZip(), contractor.getCountry()), 0,
					13));
		}
		if (contractor.getCountry() != null) {
			billAddress.setCountry(contractor.getCountry().getIsoCode());
		}
		return billAddress;
	}

	public static String getCurrencyRefFullName(ContractorAccount contractor) {
        switch (contractor.getCountry().getCurrency()){
            case EUR:
                return EURO_FULL_NAME;
            case CHF:
                return CHF_FULL_NAME;
            case GBP:
                return GBP_FULL_NAME;
            case PLN:
                return PLN_FULL_NAME;
            default:
                return null;
        }
	}

	protected static String replaceCurrencySymbols(String value) {
		for (com.picsauditing.jpa.entities.Currency currency : com.picsauditing.jpa.entities.Currency.values()) {
			value = value.replaceAll(currency.getSymbol(), currency.getDisplay());
		}

		return value;
	}


    protected static String getQBListID(Currency currency) {
        switch (currency) {
            case CAD:
                return "qbListCAID";

            case GBP:
                return "qbListUKID";

            case EUR:
                return "qbListEUID";

            case CHF:
                return "qbListCHID";

            case PLN:
                return "qbListPLID";

            default:
                return "qbListID";
        }
    }

    protected String getAccountsReceivableAccountRef(QBSession currentSession) {
        switch (currentSession.getCurrency()){
            case EUR:
                return ACCOUNTS_RECEIVABLE_EURO;
            case CHF:
                return ACCOUNTS_RECEIVABLE_CHF;
            case PLN:
                return ACCOUNTS_RECEIVABLE_PLN;
            default:
                return ACCOUNTS_RECEIVABLE;
        }
    }
}
