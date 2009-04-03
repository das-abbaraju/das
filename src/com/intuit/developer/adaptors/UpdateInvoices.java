package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.quickbooks.qbxml.InvoiceLineMod;
import com.picsauditing.quickbooks.qbxml.InvoiceMod;
import com.picsauditing.quickbooks.qbxml.InvoiceModRqType;
import com.picsauditing.quickbooks.qbxml.InvoiceModRsType;
import com.picsauditing.quickbooks.qbxml.InvoiceRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.util.SpringUtils;

public class UpdateInvoices extends CustomerAdaptor {
	
	@Override
	public String getQbXml( QBSession currentSession) throws Exception {
	
		if( currentSession.getToUpdate().size() == 0 ) {
			return super.getQbXml(currentSession);
		}
		
		int threshold = 10;
		
		Map<String, Map<String,Object>> data = currentSession.getToUpdate();
		currentSession.setCurrentBatch(new HashMap<String, String>());
		
		Writer writer = makeWriter();	
		
		ObjectFactory factory = new ObjectFactory();		
		QBXML xml = factory.createQBXML();
		
		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");
		
		Map<String, Object> thisInvoiceParms = null;

		InvoiceDAO invoiceDao = (InvoiceDAO) SpringUtils.getBean("InvoiceDAO");
		int x = 0;
		
		//unlike the customer feed, the invoice data is keyed by OUR invoice.id field 
		for( String thePk : data.keySet() ) {

			thisInvoiceParms = data.get(thePk);

			Invoice invoiceJPA = (Invoice) thisInvoiceParms.get("invoice");
			InvoiceRet existingQBInvoice = (InvoiceRet) thisInvoiceParms.get("invoiceRet");
			
			invoiceJPA = invoiceDao.find(invoiceJPA.getId());
			
			if( invoiceJPA != null ) {
			
				if( ++x == threshold ) {
					break;
				}

				InvoiceModRqType invoiceModRequest = factory.createInvoiceModRqType();
				invoiceModRequest.setRequestID("update_invoice_" + thePk);

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						invoiceModRequest);

				
				InvoiceMod invoice = factory.createInvoiceMod();
				invoiceModRequest.setInvoiceMod(invoice);

				
				invoice.setTxnID((String) thisInvoiceParms.get("TxnID"));
				invoice.setEditSequence(existingQBInvoice.getEditSequence());
				
				
				invoice.setCustomerRef(factory.createCustomerRef());
				invoice.getCustomerRef().setListID(invoiceJPA.getAccount().getQbListID());
				
				invoice.setClassRef(factory.createClassRef());
				invoice.getClassRef().setFullName("Contractors");
				
				invoice.setARAccountRef(factory.createARAccountRef() );
				invoice.getARAccountRef().setFullName("Accounts Receivable");
				
				invoice.setTemplateRef(factory.createTemplateRef());
				invoice.getTemplateRef().setFullName("PICS  Contractor Membership");
				
				invoice.setTxnDate(existingQBInvoice.getTxnDate());
				
				invoice.setRefNumber(new Integer(invoiceJPA.getId()).toString());
				
				invoice.setBillAddress(factory.createBillAddress());
				
				invoice.getBillAddress().setAddr1(invoiceJPA.getAccount().getName());
				invoice.getBillAddress().setCity(invoiceJPA.getAccount().getCity());
				invoice.getBillAddress().setState(invoiceJPA.getAccount().getState());
				invoice.getBillAddress().setPostalCode(invoiceJPA.getAccount().getZip());
				invoice.getBillAddress().setCountry(invoiceJPA.getAccount().getCountryCode());
				
				invoice.setIsPending("false");
				
				invoice.setPONumber(invoiceJPA.getPoNumber());
				invoice.setTermsRef(factory.createTermsRef());
				invoice.getTermsRef().setFullName("Net 30");
				
				invoice.setDueDate(DateBean.format(invoiceJPA.getDueDate(), "yyyy-MM-dd"));

				//this may cause a problem
				invoice.setCustomerMsgRef(factory.createCustomerMsgRef());
				invoice.getCustomerMsgRef().setFullName("Thank you for your business!");

				invoice.setIsToBePrinted("false");
				invoice.setIsToBeEmailed("false");
				
				for( InvoiceItem item : invoiceJPA.getItems() ) {

					InvoiceLineMod lineItem = factory.createInvoiceLineMod();

					lineItem.setTxnLineID("-1");
					
					lineItem.setDesc(item.getDescription());
					lineItem.setQuantity("1" );

					lineItem.setClassRef(factory.createClassRef());
					lineItem.getClassRef().setFullName("Contractors");

					lineItem.setItemRef( factory.createItemRef() );
					lineItem.getItemRef().setFullName(item.getInvoiceFee().getQbFullName());
					
					lineItem.setAmount(item.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

					invoice.getInvoiceLineModOrInvoiceLineGroupMod().add(lineItem);
				}
				
				currentSession.getCurrentBatch().put(invoiceModRequest.getRequestID(), thePk);
			}
		}
		
		
		xml.setQBXMLMsgsRq(request);
		
		Marshaller m = makeMarshaller();
		
		m.marshal(xml, writer);
		return writer.toString();
		
	}
	
	
	@Override
	public Object parseQbXml( QBSession currentSession, String qbXml ) throws Exception {

		List<ContractorAccount> successes = new Vector<ContractorAccount>();
		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		StringReader stringReader = new StringReader(qbXml);
		
		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);
		
		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();
		
		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
		
		for( Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs ) {
			
			InvoiceModRsType thisQueryResponse = (InvoiceModRsType) result;
			
			InvoiceRet invoice = thisQueryResponse.getInvoiceRet();

			String thePk = currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()); 
			
			Map<String, Object> thisCustomerParms = currentSession.getToUpdate().get(thePk);
			currentSession.getToUpdate().remove(thePk);
			
			Invoice invoiceJPA = (Invoice) thisCustomerParms.get("invoice");
			invoiceJPA = getInvoiceDao().find(invoiceJPA.getId());
			
			try {
				if( invoice == null )
					throw new Exception("no invoice object");
				
				String invoiceIdString = invoice.getRefNumber();
				
				int invoiceId = Integer.parseInt(invoiceIdString);

				if( invoiceId != 0 ) {
					invoiceJPA.setQbSync(false);
					
					if( invoiceJPA.isPaid() ) {

						if( invoiceJPA.getQbPaymentListID() == null ) {
							currentSession.getPaymentsToInsert().add(invoiceJPA);
						}
//						else if( ! invoiceJPA.getQbPaymentListID().startsWith("NOLOAD") ){
//							currentSession.getPossiblePaymentUpdates().add(invoiceJPA);
//						}
					}
				}
				
			}
			catch( Exception e ) {
				StringBuilder errorMessage = new StringBuilder("Problem updating invoice\t");
				
				errorMessage.append( thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append( currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusMessage() );
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusSeverity() );
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusCode() );
				errorMessage.append("\t");
				errorMessage.append( e.getMessage() );
				
				currentSession.getErrors().add(errorMessage.toString());
			}
			
			getInvoiceDao().save(invoiceJPA);
		}
		
		
		if( currentSession.getToUpdate().size() > 0 ) {
			setRepeat(true);
		}

		return null;
	}

}
