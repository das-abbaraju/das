package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.quickbooks.qbxml.InvoiceQueryRqType;
import com.picsauditing.quickbooks.qbxml.InvoiceQueryRsType;
import com.picsauditing.quickbooks.qbxml.InvoiceRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;

public class InvoiceAdaptor extends QBXmlAdaptor {

	public Map<String, Map<String, Object>> parseInvoiceQueryResponse(String qbXml) throws Exception {

		Map<String, Map<String, Object>> response = new HashMap<String, Map<String, Object>>();

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			InvoiceQueryRsType thisQueryResponse = (InvoiceQueryRsType) result;

			for (InvoiceRet invoiceResponse : thisQueryResponse.getInvoiceRet()) {

				Invoice invoice = new Invoice();

				try {
					String invoiceNumber = invoiceResponse.getRefNumber().toString();

					int refId = Integer.parseInt(invoiceNumber);

					invoice.setId(refId);
				} catch (Exception e) {
					invoice.setId(0);
				}
				response.put(invoiceResponse.getRefNumber(), new HashMap<String, Object>());
				response.get(invoiceResponse.getRefNumber()).put("invoice", invoice);
				response.get(invoiceResponse.getRefNumber()).put("TxnID", invoiceResponse.getTxnID());
				response.get(invoiceResponse.getRefNumber()).put("invoiceRet", invoiceResponse);
			}
		}

		return response;

	}

	public String getTheseInvoices(List<Invoice> invoices) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		InvoiceQueryRqType query = factory.createInvoiceQueryRqType();

		for (Invoice invoice : invoices) {
			query.getRefNumber().add(new Integer(invoice.getId()).toString());
		}

		query.setRequestID(new Long(System.currentTimeMillis()).toString());

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(query);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
//		logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();
	}

	/*
	 * public String deleteTheseInvoices( Map<String, Map<String,Object>> qbIds ) throws Exception { Writer writer =
	 * makeWriter();
	 * 
	 * ObjectFactory factory = new ObjectFactory(); QBXML xml = factory.createQBXML();
	 * 
	 * QBXMLMsgsRq request = factory.createQBXMLMsgsRq(); request.setOnError("stopOnError");
	 * 
	 * 
	 * for( String thePk : qbIds.keySet() ) {
	 * 
	 * ListDelRqType deleteRequest = factory.createListDelRqType(); deleteRequest.setRequestID("delete_" + thePk);
	 * deleteRequest.setListDelType("Invoice"); deleteRequest.setListID(thePk);
	 * 
	 * request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add( deleteRequest); }
	 * 
	 * 
	 * xml.setQBXMLMsgsRq(request);
	 * 
	 * Marshaller m = makeMarshaller();
	 * 
	 * m.marshal(xml, writer); return writer.toString();
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * public String updateTheseInvoices( Map<String, Map<String,Object>> data ) throws Exception { Writer writer =
	 * makeWriter();
	 * 
	 * ObjectFactory factory = new ObjectFactory(); QBXML xml = factory.createQBXML();
	 * 
	 * QBXMLMsgsRq request = factory.createQBXMLMsgsRq(); request.setOnError("stopOnError");
	 * 
	 * 
	 * Map<String, Object> theseParms = null;
	 * 
	 * for( String thePk : data.keySet() ) {
	 * 
	 * theseParms = data.get(thePk);
	 * 
	 * Invoice invoiceJPA = (Invoice) theseParms.get("invoice");
	 * 
	 * if( invoiceJPA != null ) {
	 * 
	 * InvoiceModRqType requestType = factory.createInvoiceModRqType();
	 * 
	 * 
	 * requestType.setRequestID("update_invoice_" + thePk); InvoiceMod invoice = factory.createInvoiceMod();
	 * 
	 * invoice.setTxnID( (String) theseParms.get("TXNId")); invoice.setEditSequence( (String)
	 * theseParms.get("EditSequence") ); invoice.setCustomerRef(factory.createCustomerRef());
	 * invoice.getCustomerRef().setFullName(invoiceJPA.getAccount().getName());
	 * 
	 * invoice.setClassRef(factory.createClassRef()); invoice.getClassRef().setFullName("Contractors");
	 * 
	 * invoice.setARAccountRef(factory.createARAccountRef() );
	 * invoice.getARAccountRef().setFullName("Accounts Receivable");
	 * 
	 * invoice.setTemplateRef(factory.createTemplateRef());
	 * invoice.getTemplateRef().setFullName("PICS Contractor Membership");
	 * 
	 * invoice.setTxnDate((String) theseParms.get("TXNDate"));
	 * 
	 * invoice.setRefNumber(new Integer(invoiceJPA.getId()).toString());
	 * 
	 * invoice.setBillAddress(factory.createBillAddress());
	 * 
	 * invoice.getBillAddress().setAddr1(invoiceJPA.getAccount().getAddress());
	 * invoice.getBillAddress().setCity(invoiceJPA.getAccount().getCity());
	 * invoice.getBillAddress().setState(invoiceJPA.getAccount().getState());
	 * invoice.getBillAddress().setPostalCode(invoiceJPA.getAccount().getZip());
	 * invoice.getBillAddress().setCountry(invoiceJPA.getAccount().getCountryCode());
	 * 
	 * invoice.setIsPending(new Boolean(!invoiceJPA.isPaid()).toString());
	 * 
	 * invoice.setPONumber(invoiceJPA.getPoNumber()); invoice.setTermsRef(factory.createTermsRef());
	 * invoice.getTermsRef().setFullName("Net 30");
	 * 
	 * invoice.setDueDate(DateBean.format(invoiceJPA.getDueDate(), "MM/dd/yyyy"));
	 * 
	 * //this may cause a problem invoice.setCustomerMsgRef(factory.createCustomerMsgRef());
	 * invoice.getCustomerMsgRef().setFullName(invoiceJPA.getNotes());
	 * 
	 * invoice.setIsToBePrinted("false"); invoice.setIsToBeEmailed("false");
	 * 
	 * for( InvoiceItem item : invoiceJPA.getItems() ) {
	 * 
	 * InvoiceLineMod lineItem = factory.createInvoiceLineMod();
	 * 
	 * lineItem.setDesc(item.getDescription()); lineItem.setQuantity("1" );
	 * 
	 * lineItem.setClassRef(factory.createClassRef()); lineItem.getClassRef().setFullName("Contractors");
	 * 
	 * lineItem.setItemRef( factory.createItemRef() ); lineItem.getItemRef().setFullName("Contractor Membership");
	 * 
	 * lineItem.setAmount(new Integer(item.getAmount()).toString());
	 * 
	 * invoice.getInvoiceLineModOrInvoiceLineGroupMod().add(lineItem); }
	 * 
	 * request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add( invoice); } }
	 * 
	 * 
	 * xml.setQBXMLMsgsRq(request);
	 * 
	 * Marshaller m = makeMarshaller();
	 * 
	 * m.marshal(xml, writer); return writer.toString();
	 * 
	 * }
	 */

}
