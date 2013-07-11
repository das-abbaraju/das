package com.picsauditing.actions;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.billing.ProcessQBResponseXMLStrategy;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("serial")
public class ProcessQBResponseXML extends PicsActionSupport {

	private String qbresponsexml = null;

	public String execute() throws NoRightsException, IOException, SAXException, ParserConfigurationException {
		permissions.tryPermission(OpPerms.Billing);

		if (button != null) {
			if (button.equals("save")) {
				save();
			}
		}

		return SUCCESS;
	}

	public String save() throws NoRightsException, ParserConfigurationException, IOException, SAXException {

		StringBuilder actionMessages = new StringBuilder();
		StringBuilder errorMessages = new StringBuilder();

		InputStream inputStream = new ByteArrayInputStream(this.getQbresponsexml().getBytes("UTF-8"));

		NodeList qbXmlMsgsRsChildNodes = ProcessQBResponseXMLStrategy.findQBXMLMsgsRsChildNodes(inputStream, actionMessages, errorMessages);

		if (errorMessages.length() > 0) {
			addActionMessage(actionMessages.toString());
			addActionError(errorMessages.toString());
			return ERROR;
		}

		ProcessQBResponseXMLStrategy.processQBXMLMsgsRs(qbXmlMsgsRsChildNodes, actionMessages, errorMessages);

		if (actionMessages.length() > 0) {
			addActionMessage(actionMessages.toString());
		}

		if (errorMessages.length() > 0) {
			addActionError(errorMessages.toString());
			return ERROR;
		}


		return SUCCESS;
	}


	public String getQbresponsexml() {
		return qbresponsexml;
	}

	public void setQbresponsexml(String qbresponsexml) {
		this.qbresponsexml = qbresponsexml;
	}
}
