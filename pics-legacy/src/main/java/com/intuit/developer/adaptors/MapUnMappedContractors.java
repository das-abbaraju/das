package com.intuit.developer.adaptors;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.quickbooks.qbxml.CustomerQueryRqType;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;

public class MapUnMappedContractors extends CustomerAdaptor {

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

		customerQuery.setNameFilter(factory.createNameFilter());
		customerQuery.getNameFilter().setMatchCriterion("StartsWith");
		customerQuery.getNameFilter().setName(getAppPropertyDao().find("DumpContractors.ContractorName").getValue());

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerQuery);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
//		logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		FileWriter fw = null;

		try {
			File fileBase = new File(System.getProperty("pics.ftpDir"));

			File outputFile = new File(fileBase, "contractors.out");

			if (outputFile.isFile()) {
				outputFile.delete();
			}

			fw = new FileWriter(outputFile);

			fw.write(qbXml);
		} finally {
			if (fw != null)
				fw.close();
		}

		return null;
	}
}
