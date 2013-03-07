package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.quickbooks.qbxml.*;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class InsertInvoices extends CustomerAdaptor {

	public static String getWhereClause(String qbID, String currency) {
		return "i.account." + qbID + " is not null AND i.status != 'Void' AND i.qbSync = true AND i.qbListID is null "
				+ "AND i.account." + qbID + " not like 'NOLOAD%'  and i.account.status != 'Demo' AND i.currency like '"
				+ currency + "'";
	}

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<Invoice> invoices = getInvoiceDao().findWhere(
				getWhereClause(currentSession.getQbID(), currentSession.getCurrencyCode()), 10);

		// no work to do
		if (invoices.size() == 0) {
			return super.getQbXml(currentSession);
		}

		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		for (Invoice invoiceJPA : invoices) {

			if (invoiceJPA != null) {
				InvoiceAddRqType invoiceAddRequest = factory.createInvoiceAddRqType();
				invoiceAddRequest.setRequestID("insert_invoice_" + invoiceJPA.getId());

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(invoiceAddRequest);

				InvoiceAdd invoice = factory.createInvoiceAdd();
				invoiceAddRequest.setInvoiceAdd(invoice);

				invoice.setCustomerRef(factory.createCustomerRef());
				invoice.getCustomerRef().setListID(
						invoiceJPA.getAccount().getQbListID(currentSession.getCurrencyCode()));

				invoice.setClassRef(factory.createClassRef());
				invoice.getClassRef().setFullName("Contractors");

				invoice.setARAccountRef(factory.createARAccountRef());
				if (currentSession.isEUR()) {
					invoice.getARAccountRef().setFullName("Accounts Receivable EURO");
				} else {
					invoice.getARAccountRef().setFullName("Accounts Receivable");
				}

				invoice.setTemplateRef(factory.createTemplateRef());
				invoice.getTemplateRef().setFullName("PICS  Contractor Membership");

				invoice.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(invoiceJPA.getCreationDate()));

				invoice.setRefNumber(new Integer(invoiceJPA.getId()).toString());

				invoice.setBillAddress(factory.createBillAddress());

				ContractorAccount contractor = (ContractorAccount) invoiceJPA.getAccount();

				invoice.setBillAddress(updateBillAddress(contractor, invoice.getBillAddress()));

				invoice.setIsPending("false");

				invoice.setPONumber(invoiceJPA.getPoNumber());
				invoice.setTermsRef(factory.createTermsRef());
				invoice.getTermsRef().setFullName("Net 30");

				invoice.setDueDate(DateBean.format(invoiceJPA.getDueDate(), "yyyy-MM-dd"));

				invoice.setCustomerMsgRef(factory.createCustomerMsgRef());
				invoice.getCustomerMsgRef().setFullName("Thank you for your business.");

				invoice.setIsToBePrinted("false");
				invoice.setIsToBeEmailed("false");

				if (!(invoiceJPA.getStatus().equals(TransactionStatus.Void))) {
					for (InvoiceItem item : invoiceJPA.getItemsSortedByTaxFirst()) {
						if (skipCanadianTax(invoiceJPA, item)) {
							continue;
						}
						
						InvoiceLineAdd lineItem = factory.createInvoiceLineAdd();

						lineItem.setItemRef(factory.createItemRef());
						lineItem.getItemRef().setFullName(item.getInvoiceFee().getQbFullName());

						lineItem.setDesc(item.getDescription());

						lineItem.setClassRef(factory.createClassRef());
						lineItem.getClassRef().setFullName("Contractors");

						lineItem.setAmount(item.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

						invoice.getInvoiceLineAddOrInvoiceLineGroupAdd().add(lineItem);

						setTaxExemptionBecauseQuickBooksDoesNotCalculateCanadianTaxesProperly(factory, invoiceJPA, item, lineItem);
					}
				}

				currentSession.getCurrentBatch().put(invoiceAddRequest.getRequestID(),
						new Integer(invoiceJPA.getId()).toString());
			}
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		// logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();

	}
	
	private boolean skipCanadianTax(Invoice invoiceJPA, InvoiceItem item) {
		return item.getInvoiceFee() != null && item.getInvoiceFee().isTax()
				&& invoiceJPA.getCurrency().isCAD() && !invoiceJPA.isQbSyncWithTax();
	}

	private void setTaxExemptionBecauseQuickBooksDoesNotCalculateCanadianTaxesProperly(ObjectFactory factory, Invoice invoice, InvoiceItem item,
			InvoiceLineAdd lineItem) {
		if (item.getInvoiceFee() == null || item.getInvoiceFee().isTax() || invoice.getCurrency() != Currency.CAD) {
			return;
		}

		SalesTaxCodeRef salesTaxCodeRef = factory.createSalesTaxCodeRef();
		salesTaxCodeRef.setFullName("E"); // this is for the exception
		lineItem.setSalesTaxCodeRef(salesTaxCodeRef);
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			InvoiceAddRsType thisQueryResponse = (InvoiceAddRsType) result;

			InvoiceRet invoiceRet = thisQueryResponse.getInvoiceRet();

			int invoiceId = new Integer(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()))
					.intValue();
			Invoice connected = getInvoiceDao().find(invoiceId);

			if (invoiceRet != null) {

				try {
					String invoiceIdString = invoiceRet.getRefNumber();
					int refNumber = Integer.parseInt(invoiceIdString);

					if (refNumber != 0) {
						connected.setQbListID(invoiceRet.getTxnID());
						connected.setQbSync(false);
						
						if (!connected.isQbSyncWithTax()) {
							connected.setQbSyncWithTax(true);
							connected.setQbSync(true);
						}

						// if( connected.isPaid() ) {
						// currentSession.getPaymentsToInsert().add(connected);
						// }
					}
				} catch (Exception e) {
				}
			} else {
				StringBuilder errorMessage = new StringBuilder("Problem inserting invoice\t");

				errorMessage.append(thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusMessage());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusSeverity());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusCode());

				currentSession.getErrors().add(errorMessage.toString());

				connected.setQbListID(null);
				connected.setQbSync(true);
			}

			getInvoiceDao().save(connected);
		}

		return null;
	}

}
