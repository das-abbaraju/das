package com.picsauditing.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/10/13
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class XmlService {
	public static Element getRootElementFromInputStream(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc =  docBuilder.parse(inputStream);
		doc.getDocumentElement ().normalize ();
		Element rootElement = doc.getDocumentElement();
		return rootElement;
	}

}
