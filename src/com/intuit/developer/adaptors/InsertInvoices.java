package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.quickbooks.qbxml.InvoiceAdd;
import com.picsauditing.quickbooks.qbxml.InvoiceAddRqType;
import com.picsauditing.quickbooks.qbxml.InvoiceAddRsType;
import com.picsauditing.quickbooks.qbxml.InvoiceLineAdd;
import com.picsauditing.quickbooks.qbxml.InvoiceRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;

public class InsertInvoices extends CustomerAdaptor {
	
	@Override
	public String getQbXml( QBSession currentSession) throws Exception {
	
		
		//first time, load up the inserts
		if( currentSession.getInvoicesToInsert() == null ) {
			currentSession.setInvoicesToInsert(new Vector<Invoice>());
			List<Invoice> invoices = getInvoiceDao().findWhere("i.qbSync = true and i.qbListID is null");
			currentSession.getInvoicesToInsert().addAll(invoices);
		}

		//no work to do
		if( currentSession.getInvoicesToInsert().size() == 0 ) {
			return super.getQbXml(currentSession);  
		}
		
		
		int threshold = 10;
		
		currentSession.setCurrentBatch(new HashMap<String, String>());
		
		Writer writer = makeWriter();	
		
		ObjectFactory factory = new ObjectFactory();		
		QBXML xml = factory.createQBXML();
		
		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");
		

		
		int x = 0;
		for( Invoice invoiceJPA : currentSession.getInvoicesToInsert() ) {
			
			if( invoiceJPA != null ) {
			
				if( ++x == threshold ) {
					break;
				}

				
				InvoiceAddRqType invoiceAddRequest = factory.createInvoiceAddRqType();
				invoiceAddRequest.setRequestID("insert_invoice_" + invoiceJPA.getId());

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
						invoiceAddRequest);

				
				InvoiceAdd invoice = factory.createInvoiceAdd();
				invoiceAddRequest.setInvoiceAdd(invoice);

				invoice.setCustomerRef(factory.createCustomerRef());
				invoice.getCustomerRef().setListID(invoiceJPA.getAccount().getQbListID());
				
				invoice.setClassRef(factory.createClassRef());
				invoice.getClassRef().setFullName("Contractors");
				
				invoice.setARAccountRef(factory.createARAccountRef() );
				invoice.getARAccountRef().setFullName("Accounts Receivable");
				
				invoice.setTemplateRef(factory.createTemplateRef());
				invoice.getTemplateRef().setFullName("PICS  Contractor Membership");
				
				invoice.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(invoiceJPA.getCreationDate()));
				
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

					InvoiceLineAdd lineItem = factory.createInvoiceLineAdd();

					lineItem.setItemRef( factory.createItemRef() );
					lineItem.getItemRef().setFullName(item.getInvoiceFee().getQbFullName());

					
					lineItem.setDesc(item.getDescription());
					lineItem.setQuantity("1" );

					lineItem.setClassRef(factory.createClassRef());
					lineItem.getClassRef().setFullName("Contractors");

					lineItem.setAmount(new Integer(item.getAmount()).toString() + ".00");

					invoice.getInvoiceLineAddOrInvoiceLineGroupAdd().add(lineItem);
				}
				
				currentSession.getCurrentBatch().put(invoiceAddRequest.getRequestID(), new Integer(invoiceJPA.getId()).toString());
			}
		}
		
		xml.setQBXMLMsgsRq(request);
		
		Marshaller m = makeMarshaller();
		
		m.marshal(xml, writer);
		return writer.toString();
		
	}
	
	
	@Override
	public Object parseQbXml( QBSession currentSession, String qbXml ) throws Exception {

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		
		StringReader stringReader = new StringReader(qbXml);
		
		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);
		
		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();
		
		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
		
		for( Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs ) {
			
			
			InvoiceAddRsType thisQueryResponse = (InvoiceAddRsType) result;
			
			InvoiceRet invoiceRet = thisQueryResponse.getInvoiceRet();

			int invoiceId = new Integer( currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID())).intValue();
			Invoice connected = getInvoiceDao().find(invoiceId);
			
			if( invoiceRet != null ) {
			
				try {
					String invoiceIdString = invoiceRet.getRefNumber();
					int refNumber = Integer.parseInt(invoiceIdString);
					
					if( refNumber != 0 ) {
						connected.setQbListID(invoiceRet.getTxnID());
						connected.setQbSync(false);

						if( connected.isPaid() ) {
							currentSession.getPaymentsToInsert().add(connected);
						}
					}
				}
				catch( Exception e ) {
				}
			}
			else {
				StringBuilder errorMessage = new StringBuilder("Problem inserting invoice\t");
				
				errorMessage.append( thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append( currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusMessage() );
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusSeverity() );
				errorMessage.append("\t");
				errorMessage.append( thisQueryResponse.getStatusCode() );
				
				currentSession.getErrors().add(errorMessage.toString());
				
				connected.setQbListID(null);
				connected.setQbSync(true);
			}

			getInvoiceDao().save(connected);
			
			currentSession.getInvoicesToInsert().remove(connected);
		}
		
		if( currentSession.getInvoicesToInsert().size() > 0 ) {
			setRepeat(true);
		}

		return null;
	}

}
