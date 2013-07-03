package com.picsauditing.actions;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@SuppressWarnings("serial")
public class ProcessQBResponseXML extends PicsActionSupport {

	private static final String ACCOUNT = "Account";
	private static final String INVOICE = "Invoice";

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Qualifier("InvoiceDAO")
	@Autowired
	private InvoiceDAO invoiceDAO;



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
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new ByteArrayInputStream(this.getQbresponsexml().getBytes()));
		// normalize text representation
		doc.getDocumentElement ().normalize ();
		Element rootElement = doc.getDocumentElement();

		if (!("QBXML".equals(rootElement.getNodeName())))   {
			    addActionError("Didn't find 'QBXML' as root element");
			return ERROR;
		}

		addActionMessage("Root element of the doc is QBXML");

		NodeList childNodes = rootElement.getChildNodes();
		Boolean qbxmlmsgsrsFound = false;
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("QBXMLMsgsRs")) {
				qbxmlmsgsrsFound = true;
				addActionMessage("Processing QBXMLMsgsRs child node");
				processQBXMLMsgsRs(childNode.getChildNodes());
				break;
			}

		}
		if (!qbxmlmsgsrsFound)   {
			addActionError("Didn't find 'QBXMLMsgsRs' node");
			return ERROR;
		}

		return SUCCESS;
	}

	private void processQBXMLMsgsRs(NodeList nodeList) {

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals("#text")) {
				continue;
			}

			addActionMessage("Processing node of type '"+node.getNodeName()+"'");

			switch (node.getNodeName()) {
				case "InvoiceAddRs":
					processInvoiceAdd(node);
					break;
				default:
					addActionError("Need code to process node of type '"+node.getNodeName()+"'");
					continue;
			}

		}


	}

	private void processInvoiceAdd(Node invoiceRsNode) {
		NamedNodeMap attributes = invoiceRsNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		if (statusMessage.getNodeValue().equals("Status OK")) {
			NodeList childNodes = invoiceRsNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
			  if (childNodes.item(i).getNodeName().equals("InvoiceRet")) {
				  Node invoiceRet = childNodes.item(i);
				  NodeList invoiceRetChildren = invoiceRet.getChildNodes();
				  String qbListID = "";
				  String invoiceID = "";
				  for (int j = 0; j < invoiceRetChildren.getLength(); ++j) {
					  Node nodeInQuestion = invoiceRetChildren.item(j);
					  switch (nodeInQuestion.getNodeName()) {
						  case "TxnID":
							  qbListID = nodeInQuestion.getTextContent();
							  break;
						  case "RefNumber":
							  invoiceID = nodeInQuestion.getTextContent();
							  break;
						  default:
							  break;
					  }
					  if (!qbListID.isEmpty() && !invoiceID.isEmpty()) {
						  addActionMessage("Executing: UPDATE invoice SET qbListID = '"+qbListID+"',qbSync = 0 where id = "+invoiceID+";");
						  Invoice invoice = invoiceDAO.find(Integer.parseInt(invoiceID));
						  invoice.setQbSync(false);
						  invoice.setQbListID(qbListID);
						  invoiceDAO.save(invoice);
						  return;
					  }
				  }
			  }
			}
		} else {
			return;
		}


	}

	/*
	private void saveContractor(String[] idList) {
		List<ContractorAccount> contractors = contractorAccountDAO.findWhere(ContractorAccount.class, "t.id IN (" + ids
				+ ")");

		if (contractors.isEmpty())
			addActionError("We could not find any account you were looking for");
		else if (contractors.size() != idList.length)
			addActionError("We could not find all of the accounts you were looking for");

		if (!hasActionErrors()) {
			for (ContractorAccount contractor : contractors) {
				editContractor(contractor);
				addActionMessage(getTextParameterized("ContractorEdit.message.SaveContractor", contractor.getName()));
			}
		}
	}

	private void editContractor(ContractorAccount contractor) {
		contractor.setQbSync(needSync);
		if (clearListID) {
			contractor.setQbListID(null);
			contractor.setQbListCAID(null);
			contractor.setQbListUKID(null);
			contractor.setQbListEUID(null);
		}
		contractorAccountDAO.save(contractor);
	}

	private void saveTransaction(String[] idList) {
		List<Transaction> transactions = invoiceDAO.findWhere(Transaction.class, "t.id IN (" + ids + ")");

		if (transactions.isEmpty())
			addActionError(getText("InvoiceDetail.error.CantFindInvoice"));
		else if (transactions.size() != idList.length)
			addActionError("We could not find all of the invoice you were looking for");

		if (!hasActionErrors()) {
			for (Transaction transaction : transactions) {
				editTranaction(transaction);
				addActionMessage(getText("InvoiceDetail.message.SavedInvoice") + " #" + transaction.getId());
			}
		}
	}

	private void editTranaction(Transaction transaction) {
		transaction.setQbSync(needSync);
		if (clearListID) {
			transaction.setQbListID(null);
		}
		invoiceDAO.save(transaction);
	}
       */
	public String getQbresponsexml() {
		return qbresponsexml;
	}

	public void setQbresponsexml(String qbresponsexml) {
		this.qbresponsexml = qbresponsexml;
	}
}
