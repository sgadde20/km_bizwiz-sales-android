package com.webparadox.bizwizsales.helper;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PaymentStatusHandler extends DefaultHandler {

	String currentValue = "";
	String status = "";

	public String getStatus() {
		return status;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
		currentValue = "";
	};

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if (localName.equals("Status")) {
			status = currentValue;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
			currentValue = currentValue+new String(ch, start, length);
	}

}
