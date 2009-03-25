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
		
		try {
		
			File outputFile = new File("invoices.out"); 
			
			if( outputFile.isFile() ) {
				outputFile.delete();
			}
			
			fw = new FileWriter( outputFile);
			
			for( String listId : parsedResponses.keySet() ) {
				Map<String, Object> dataForThisListId = parsedResponses.get(listId);
				
				Invoice targetObject = (Invoice) dataForThisListId.get("invoice");
				
				if( targetObject != null && targetObject.getId() != 0 ) {
					
					fw.write("update invoices set qbListID = '" + dataForThisListId.get("TxnID") + "' where id = " + targetObject.getId() + ";\n");
					
				}
			}
	
			fw.write(qbXml);
		}
		finally {
			if( fw != null ) fw.close();	
		}

		return null;
	}

}
