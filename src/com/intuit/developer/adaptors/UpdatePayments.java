package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.quickbooks.qbxml.AppliedToTxnMod;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.PaymentMethodRef;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentMod;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class UpdatePayments extends CustomerAdaptor {
	
	@Override
	public String getQbXml( QBSession currentSession) throws Exception {
	
		//no work to do
		//if( currentSession.getToUpdate().size() == 0 ) {
			return super.getQbXml(currentSession);
		//}
		
//		int threshold = 10;
//		
//		Map<String, Map<String,Object>> data = currentSession.getToUpdate();
//		currentSession.setCurrentBatch(new HashMap<String, String>());
//		
//		Writer writer = makeWriter();	
//		
//		ObjectFactory factory = new ObjectFactory();		
//		QBXML xml = factory.createQBXML();
//		
//		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
//		request.setOnError("continueOnError");
//		
//		Map<String, Object> thisInvoiceParms = null;
//		InvoiceDAO invoiceDao = (InvoiceDAO) SpringUtils.getBean("InvoiceDAO");
//		
//		int x = 0;
//		for( String thePk : data.keySet() ) {
//
//			if( ++x == threshold ) {
//				break;
//			}
//
//			thisInvoiceParms = data.get( thePk );
//
//			Invoice invoiceJPA = (Invoice) thisInvoiceParms.get("invoice");
//			invoiceJPA = invoiceDao.find(invoiceJPA.getId());
//			
//			ReceivePaymentRet paymentRet = (ReceivePaymentRet) thisInvoiceParms.get("paymentRet"); 
//			
//			ReceivePaymentModRqType modRequest = factory.createReceivePaymentModRqType();
//			modRequest.setRequestID("update_payment_" + invoiceJPA.getId());
//
//			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
//					modRequest);
//
//			
//			ReceivePaymentMod payment = factory.createReceivePaymentMod();
//			modRequest.setReceivePaymentMod(payment);
//			
//			payment.setTxnID(paymentRet.getTxnID());
//			payment.setEditSequence(paymentRet.getEditSequence());
//			
//			payment.setCustomerRef(factory.createCustomerRef());
//			payment.getCustomerRef().setListID( invoiceJPA.getAccount().getQbListID() );
//			
//			payment.setARAccountRef(factory.createARAccountRef() );
//			payment.getARAccountRef().setFullName("Accounts Receivable");
//
//			payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(invoiceJPA.getPaidDate()));
//			
//			//payment.setRefNumber(invoiceJPA.getCheckNumber());  they're not using this field in quickbooks anymore
//			payment.setTotalAmount(new Integer(invoiceJPA.getTotalAmount()).toString() + ".00");
//			
//			payment.setPaymentMethodRef(new PaymentMethodRef());
//			
//			if( invoiceJPA.getTransactionID() == null ) {
//				payment.getPaymentMethodRef().setFullName("Check");
//				
//			} 
//			else {
//				payment.setMemo(invoiceJPA.getTransactionID());
//				
//				BrainTreeService.CreditCard card = new BrainTreeService.CreditCard( invoiceJPA.getCcNumber() );
//				
//				if( "American Express".equals(card.getCardType())) {
//					payment.getPaymentMethodRef().setFullName("Braintree AMEX");
//				}
//				else if ( "Visa".equals(card.getCardType()) || "Mastercard".equals(card.getCardType())) {
//					payment.getPaymentMethodRef().setFullName("Braintree VISA/MC");
//				}
//				else if ( "Discover".equals(card.getCardType()) ) {
//					payment.getPaymentMethodRef().setFullName("Braintree DISCOVER");
//				}
//				else {
//					payment.getPaymentMethodRef().setFullName("Braintree Credit");	
//				}
//			}
//
//			
//			AppliedToTxnMod application = factory.createAppliedToTxnMod();
//			payment.getAppliedToTxnMod().add(application);
//
//			application.setTxnID(invoiceJPA.getQbListID());
//			application.setPaymentAmount(new Integer(invoiceJPA.getTotalAmount()).toString() + ".00");
//			
//			currentSession.getCurrentBatch().put(modRequest.getRequestID(), new Integer(invoiceJPA.getId()).toString());
//		}
//		
//		xml.setQBXMLMsgsRq(request);
//		
//		Marshaller m = makeMarshaller();
//		
//		m.marshal(xml, writer);
//		return writer.toString();
	}
	
	
	@Override
	public Object parseQbXml( QBSession currentSession, String qbXml ) throws Exception {

//		Unmarshaller unmarshaller = jc.createUnmarshaller();
//		
//		StringReader stringReader = new StringReader(qbXml);
//		
//		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);
//		
//		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();
//		
//		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();
//		
//		for( Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs ) {
//			
//			ReceivePaymentModRsType thisQueryResponse = (ReceivePaymentModRsType) result;
//			
//			ReceivePaymentRet receivePaymentRet = thisQueryResponse.getReceivePaymentRet();
//
//			int invoiceId = new Integer( currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID())).intValue();
//			
//			currentSession.getToUpdate().remove(new Integer( invoiceId) .toString());
//			
//			if( receivePaymentRet == null ) { 
//				StringBuilder errorMessage = new StringBuilder("Problem updating payment\t");
//				
//				errorMessage.append( thisQueryResponse.getRequestID());
//				errorMessage.append("\t");
//				errorMessage.append( currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
//				errorMessage.append("\t");
//				errorMessage.append( thisQueryResponse.getStatusMessage() );
//				errorMessage.append("\t");
//				errorMessage.append( thisQueryResponse.getStatusSeverity() );
//				errorMessage.append("\t");
//				errorMessage.append( thisQueryResponse.getStatusCode() );
//				
//				currentSession.getErrors().add(errorMessage.toString());
//			}
//
//		}
//		
//		
//		if( currentSession.getToUpdate().size() > 0 ) {
//			setRepeat(true);
//		}

		return null;
	}

}
