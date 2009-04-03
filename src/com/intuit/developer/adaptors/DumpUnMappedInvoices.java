package com.intuit.developer.adaptors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.quickbooks.qbxml.InvoiceQueryRqType;
import com.picsauditing.quickbooks.qbxml.InvoiceRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.util.log.PicsLogger;


public class DumpUnMappedInvoices extends InvoiceAdaptor {

	
	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Writer writer = makeWriter();
		
		
		ObjectFactory factory = new ObjectFactory();		
		QBXML xml = factory.createQBXML();
		
		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");
		
		
		InvoiceQueryRqType query = factory.createInvoiceQueryRqType();
		
		query.setRequestID(new Long(System.currentTimeMillis()).toString());
		
		query.getIncludeRetElement().add("TxnID");
		query.getIncludeRetElement().add("TxnNumber");
		query.getIncludeRetElement().add("TxnDate");
		query.getIncludeRetElement().add("RefNumber");
		query.getIncludeRetElement().add("Subtotal");
		query.getIncludeRetElement().add("IsPaid");
		
		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
				query);
		xml.setQBXMLMsgsRq(request);
		
		Marshaller m = makeMarshaller();
		
		m.marshal(xml, writer);
		return writer.toString();
	}

	
	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml)
			throws Exception {

		Map<String, Map<String,Object>> parsedResponses = parseInvoiceQueryResponse(qbXml);
		
		FileWriter fw = null;
		FileWriter fw2 = null;
		
		try {
		
			File outputFile = new File("invoices.out"); 
			File outputFile2 = new File("paid_in_pics_but_not_qb.out"); 
			
			if( outputFile.isFile() ) {
				outputFile.delete();
			}
			if( outputFile2.isFile() ) {
				outputFile2.delete();
			}
			
			fw = new FileWriter( outputFile);
			fw2 = new FileWriter( outputFile2);
			
			for( String listId : parsedResponses.keySet() ) {
				Map<String, Object> dataForThisListId = parsedResponses.get(listId);
				
				Invoice targetObject = (Invoice) dataForThisListId.get("invoice");
				
				if( targetObject != null && targetObject.getId() != 0 ) {
					fw.write("update invoice set qbListID = '" + dataForThisListId.get("TxnID") + "' where id = " + targetObject.getId() + ";\n");

					
					try {
						InvoiceRet invoiceRet = (InvoiceRet) dataForThisListId.get("invoiceRet");
						Invoice connectedInvoice = getInvoiceDao().find(targetObject.getId());
						
						if( invoiceRet.getIsPaid().equals("false") && connectedInvoice.isPaid() ) {
							fw2.write(connectedInvoice.getAccount().getId());
							fw2.write("\t");
							fw2.write(connectedInvoice.getId());
							fw2.write("\n");
						}
					}
					catch( Exception e ) {
						e.printStackTrace();
					}
				}
				
				
				
				
			}
	
			fw.write(qbXml);
		}
		finally {
			if( fw != null ) fw.close();	
			if( fw2 != null ) fw2.close();	
		}

		return null;
	}

}
