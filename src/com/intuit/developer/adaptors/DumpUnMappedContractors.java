package com.intuit.developer.adaptors;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.CustomerQueryRqType;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.util.log.PicsLogger;


public class DumpUnMappedContractors extends CustomerAdaptor {

	
	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Writer writer = makeWriter();
		
		
		ObjectFactory factory = new ObjectFactory();		
		QBXML xml = factory.createQBXML();
		
		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");
		
		CustomerQueryRqType customerQuery = factory.createCustomerQueryRqType();
		
		customerQuery.setRequestID(new Long(System.currentTimeMillis()).toString());
		
		customerQuery.getIncludeRetElement().add("ListID");
		customerQuery.getIncludeRetElement().add("AccountNumber");
		customerQuery.getIncludeRetElement().add("CompanyName");
		
		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(
				customerQuery);
		xml.setQBXMLMsgsRq(request);
		
		Marshaller m = makeMarshaller();
		
		m.marshal(xml, writer);
		return writer.toString();
	}

	
	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml)
			throws Exception {

		Map<String, Map<String,Object>> parsedResponses = parseCustomerQueryResponse(qbXml);

		FileWriter fw = null;
		
		try {
		
			File outputFile = new File("contractors.out"); 
			
			if( outputFile.isFile() ) {
				outputFile.delete();
			}
			
			fw = new FileWriter( outputFile);

		
			for( String listId : parsedResponses.keySet() ) {
				Map<String, Object> dataForThisListId = parsedResponses.get(listId);
				
				ContractorAccount contractor = ( ContractorAccount) dataForThisListId.get("contractor");
				
				if( contractor != null && contractor.getId() != 0 ) {
					
					fw.write("update accounts set qbListID = '" + listId + "' where id = " + contractor.getId() + ";\n");
				}
			}
	
			fw.write(qbXml);
			return null;
		}
		finally {
			if( fw != null ) fw.close();
		}
	}

}
