/**
 * QBWebConnectorSvcMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package com.intuit.developer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.SpringUtils;

/**
 * QBWebConnectorSvcMessageReceiverInOut message receiver
 */

public class QBWebConnectorSvcMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {

	public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext,
			org.apache.axis2.context.MessageContext newMsgContext) throws org.apache.axis2.AxisFault {

		try {

			// get the implementation class for the Web Service
			Object obj = getTheImplementationObject(msgContext);

			QBWebConnectorSvcSkeleton skel = (QBWebConnectorSvcSkeleton) obj;
			// Out Envelop
			org.apache.axiom.soap.SOAPEnvelope envelope = null;
			// Find the axisOperation that has been set by the Dispatch phase.
			org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
			if (op == null) {
				throw new org.apache.axis2.AxisFault(
						"Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
			}

			java.lang.String methodName;
			if ((op.getName() != null)
					&& ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJava(op.getName().getLocalPart())) != null)) {

				if ("sendRequestXML".equals(methodName)) {

					com.intuit.developer.SendRequestXMLResponse sendRequestXMLResponse1 = null;
					com.intuit.developer.SendRequestXML wrappedParam = (com.intuit.developer.SendRequestXML) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.SendRequestXML.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					sendRequestXMLResponse1 =

					skel.sendRequestXML(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), sendRequestXMLResponse1, false);
				} else

				if ("connectionError".equals(methodName)) {

					com.intuit.developer.ConnectionErrorResponse connectionErrorResponse3 = null;
					com.intuit.developer.ConnectionError wrappedParam = (com.intuit.developer.ConnectionError) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.ConnectionError.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					connectionErrorResponse3 =

					skel.connectionError(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), connectionErrorResponse3, false);
				} else

				if ("closeConnection".equals(methodName)) {

					com.intuit.developer.CloseConnectionResponse closeConnectionResponse5 = null;
					com.intuit.developer.CloseConnection wrappedParam = (com.intuit.developer.CloseConnection) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.CloseConnection.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					closeConnectionResponse5 =

					skel.closeConnection(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), closeConnectionResponse5, false);
				} else

				if ("getLastError".equals(methodName)) {

					com.intuit.developer.GetLastErrorResponse getLastErrorResponse7 = null;
					com.intuit.developer.GetLastError wrappedParam = (com.intuit.developer.GetLastError) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.GetLastError.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					getLastErrorResponse7 =

					skel.getLastError(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), getLastErrorResponse7, false);
				} else

				if ("authenticate".equals(methodName)) {

					com.intuit.developer.AuthenticateResponse authenticateResponse9 = null;
					com.intuit.developer.Authenticate wrappedParam = (com.intuit.developer.Authenticate) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.Authenticate.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					authenticateResponse9 =

					skel.authenticate(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), authenticateResponse9, false);
				} else

				if ("receiveResponseXML".equals(methodName)) {

					com.intuit.developer.ReceiveResponseXMLResponse receiveResponseXMLResponse11 = null;
					com.intuit.developer.ReceiveResponseXML wrappedParam = (com.intuit.developer.ReceiveResponseXML) fromOM(
							msgContext.getEnvelope().getBody().getFirstElement(),
							com.intuit.developer.ReceiveResponseXML.class, getEnvelopeNamespaces(msgContext
									.getEnvelope()));

					receiveResponseXMLResponse11 =

					skel.receiveResponseXML(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), receiveResponseXMLResponse11, false);

				} else {
					throw new java.lang.RuntimeException("method not found");
				}

				// This is for the stupidity of QuickBooks, delete under penalty of death while 
				// we are still using QuickBooks.				
				AppPropertyDAO appPropertyDAO = SpringUtils.getBean("AppPropertyDAO");
				if (appPropertyDAO != null) {
					AppProperty property = appPropertyDAO.find("QuickBooks.Axis.ASCII.Encoding");
					if ("Y".equals(property.getValue())) {
						newMsgContext.getOptions().setProperty(org.apache.axis2.Constants.Configuration.CHARACTER_SET_ENCODING, "US-ASCII");
					}
				}
				
				newMsgContext.setEnvelope(envelope);
			}
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.SendRequestXML param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.SendRequestXML.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.SendRequestXMLResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.SendRequestXMLResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.ConnectionError param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.ConnectionError.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.ConnectionErrorResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.ConnectionErrorResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.CloseConnection param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.CloseConnection.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.CloseConnectionResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.CloseConnectionResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.GetLastError param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.GetLastError.MY_QNAME, org.apache.axiom.om.OMAbstractFactory
					.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.GetLastErrorResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.GetLastErrorResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.Authenticate param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.Authenticate.MY_QNAME, org.apache.axiom.om.OMAbstractFactory
					.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.AuthenticateResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.AuthenticateResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.ReceiveResponseXML param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.ReceiveResponseXML.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(com.intuit.developer.ReceiveResponseXMLResponse param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault {

		try {
			return param.getOMElement(com.intuit.developer.ReceiveResponseXMLResponse.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.SendRequestXMLResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.SendRequestXMLResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.SendRequestXMLResponse wrapsendRequestXML() {
		com.intuit.developer.SendRequestXMLResponse wrappedElement = new com.intuit.developer.SendRequestXMLResponse();
		return wrappedElement;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.ConnectionErrorResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.ConnectionErrorResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.ConnectionErrorResponse wrapconnectionError() {
		com.intuit.developer.ConnectionErrorResponse wrappedElement = new com.intuit.developer.ConnectionErrorResponse();
		return wrappedElement;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.CloseConnectionResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.CloseConnectionResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.CloseConnectionResponse wrapcloseConnection() {
		com.intuit.developer.CloseConnectionResponse wrappedElement = new com.intuit.developer.CloseConnectionResponse();
		return wrappedElement;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.GetLastErrorResponse param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.GetLastErrorResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.GetLastErrorResponse wrapgetLastError() {
		com.intuit.developer.GetLastErrorResponse wrappedElement = new com.intuit.developer.GetLastErrorResponse();
		return wrappedElement;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.AuthenticateResponse param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.AuthenticateResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.AuthenticateResponse wrapauthenticate() {
		com.intuit.developer.AuthenticateResponse wrappedElement = new com.intuit.developer.AuthenticateResponse();
		return wrappedElement;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			com.intuit.developer.ReceiveResponseXMLResponse param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		try {
			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

			emptyEnvelope.getBody().addChild(
					param.getOMElement(com.intuit.developer.ReceiveResponseXMLResponse.MY_QNAME, factory));

			return emptyEnvelope;
		} catch (org.apache.axis2.databinding.ADBException e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private com.intuit.developer.ReceiveResponseXMLResponse wrapreceiveResponseXML() {
		com.intuit.developer.ReceiveResponseXMLResponse wrappedElement = new com.intuit.developer.ReceiveResponseXMLResponse();
		return wrappedElement;
	}

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
		return factory.getDefaultEnvelope();
	}

	private java.lang.Object fromOM(org.apache.axiom.om.OMElement param, java.lang.Class type,
			java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {

		try {

			if (com.intuit.developer.SendRequestXML.class.equals(type)) {

				return com.intuit.developer.SendRequestXML.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.SendRequestXMLResponse.class.equals(type)) {

				return com.intuit.developer.SendRequestXMLResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.ConnectionError.class.equals(type)) {

				return com.intuit.developer.ConnectionError.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.ConnectionErrorResponse.class.equals(type)) {

				return com.intuit.developer.ConnectionErrorResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.CloseConnection.class.equals(type)) {

				return com.intuit.developer.CloseConnection.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.CloseConnectionResponse.class.equals(type)) {

				return com.intuit.developer.CloseConnectionResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.GetLastError.class.equals(type)) {

				return com.intuit.developer.GetLastError.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.GetLastErrorResponse.class.equals(type)) {

				return com.intuit.developer.GetLastErrorResponse.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.Authenticate.class.equals(type)) {

				return com.intuit.developer.Authenticate.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.AuthenticateResponse.class.equals(type)) {

				return com.intuit.developer.AuthenticateResponse.Factory
						.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.ReceiveResponseXML.class.equals(type)) {

				return com.intuit.developer.ReceiveResponseXML.Factory.parse(param.getXMLStreamReaderWithoutCaching());

			}

			if (com.intuit.developer.ReceiveResponseXMLResponse.class.equals(type)) {

				return com.intuit.developer.ReceiveResponseXMLResponse.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
		org.apache.axis2.AxisFault f;
		Throwable cause = e.getCause();
		if (cause != null) {
			f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
		} else {
			f = new org.apache.axis2.AxisFault(e.getMessage());
		}

		return f;
	}

}// end of class
