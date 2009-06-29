package com.intuit.developer.adaptors;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentQueryRqType;

public class DumpQuickBookPayments extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		ReceivePaymentQueryRqType query = factory.createReceivePaymentQueryRqType();
		
		query.setRequestID(new Long(System.currentTimeMillis()).toString());

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(query);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		FileWriter fw = null;

		try {
			File fileBase = new File(System.getProperty("pics.ftpDir"));

			File outputFile = new File(fileBase, "payments.out");

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
