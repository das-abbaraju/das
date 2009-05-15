package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;

public class Finished extends QBXmlAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		currentSession.setLastError("No wore work to do");

		return super.getQbXml(currentSession);
	}
}
