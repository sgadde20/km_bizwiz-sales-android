package com.webparadox.bizwizsales.helper;

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.webparadox.bizwizsales.models.PaymentType;


public class CCTypesHandler extends DefaultHandler {

	String currentValue = "";
	PaymentType item = new PaymentType();
	int k =0;
	private static ArrayList<String> itemsListId = new ArrayList<String>();
	private static ArrayList<String> itemsType = new ArrayList<String>();

	public ArrayList<String> getItemsListId() {
		return itemsListId;
	}
	public ArrayList<String> getItemsTypeId() {
		return itemsType;
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
		currentValue ="";
		if (localName.equals("BizwizCCTypes")) {
			item = new PaymentType();
			itemsListId.clear();
			itemsType.clear();
		}
	};

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);

		if (localName.equals("Id")) {
			itemsListId.add(currentValue);
		} else if (localName.equals("Type")) {
			itemsType.add(currentValue);
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
