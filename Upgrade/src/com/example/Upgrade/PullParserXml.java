package com.example.Upgrade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml; 

public class PullParserXml {
	public final static String SERVER_INFORMATION = "ServiceInformation";
	public final static String SERVER_VERSION = "Version";
	public final static String SERVER_TIME = "ServerTime";
	public final static String SERVER_RESPONSE = "Response";
	public final static String SERVER_ERRORCODE = "ErrorCode";
	public final static String DEVICE = "Device";
	public final static String PANDIGITALSKU = "PandigitalSku";
	public final static String PAYLOAD = "Payload";
	public final static String DOWNLOADKEY = "DownloadKey";
	public final static String DOWNLOADNAME = "Name";
	public final static String DOWNLOADURL = "URL";
	public final static String DOWNLOADMD5 = "MD5";
	public final static String DOWNLOADSIZEINB = "SizeInB";
	public final static String DOWNLOADFILETIME = "File_time";
	
	
	public List<UNameValuePair> getServerAbout(InputStream inputStream) throws Exception { 
		List<UNameValuePair> list = null;
		XmlPullParser parser = Xml.newPullParser();
		
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		
		while(event!=XmlPullParser.END_DOCUMENT) {
			switch(event) {
			    case XmlPullParser.START_DOCUMENT:
			    	list = new ArrayList<UNameValuePair>();
			    	break;
			    case XmlPullParser.START_TAG:
			    	if(SERVER_INFORMATION.equals(parser.getName())) {
				    	if(SERVER_VERSION.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(SERVER_VERSION, parser.getAttributeValue(0)));
				    	}
				    	if(SERVER_TIME.equals(parser.getAttributeName(1))) {
				    		list.add(new UNameValuePair(SERVER_TIME, parser.getAttributeValue(1)));
				    	}
			    		list.add(new UNameValuePair(SERVER_INFORMATION, parser.nextText()));
			    	}
			    	break;
			    case XmlPullParser.END_TAG:
			    	break;
			}
			event = parser.next();
		}
		return list;
	}
	
	public List<UNameValuePair> getSkuName(InputStream inputStream) throws Exception { 
		List<UNameValuePair> list = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		while(event!=XmlPullParser.END_DOCUMENT) {
			switch(event) {
			    case XmlPullParser.START_DOCUMENT:
			    	list = new ArrayList<UNameValuePair>();
			    	break;
			    case XmlPullParser.START_TAG:
			    	if(SERVER_RESPONSE.equals(parser.getName())) {
			    		int error_code = 0;
				    	if(SERVER_ERRORCODE.equals(parser.getAttributeName(0))) {
				    		String attr_value = parser.getAttributeValue(0);
				    		error_code = Integer.parseInt(attr_value);
				    		list.add(new UNameValuePair(SERVER_ERRORCODE, attr_value));
				    	}
				    	if(error_code != 0) {
				    		list.add(new UNameValuePair(SERVER_RESPONSE, parser.nextText()));
				    		return list;
				    	}
			    	}
			    	else if(DEVICE.equals(parser.getName())) {
				    	if(PANDIGITALSKU.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(PANDIGITALSKU, parser.getAttributeValue(0)));
				    	}
			    	}
			    	break;
			    case XmlPullParser.END_TAG:
			    	break;
			}
			event = parser.next();
		}
		return list;
	}
	
	public List<UNameValuePair> getDownloadKey(InputStream inputStream) throws Exception { 
		List<UNameValuePair> list = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		while(event!=XmlPullParser.END_DOCUMENT) {
			switch(event) {
			    case XmlPullParser.START_DOCUMENT:
			    	list = new ArrayList<UNameValuePair>();
			    	break;
			    case XmlPullParser.START_TAG:
			    	if(SERVER_RESPONSE.equals(parser.getName())) {
			    		int error_code = 0;
				    	if(SERVER_ERRORCODE.equals(parser.getAttributeName(0))) {
				    		String attr_value = parser.getAttributeValue(0);
				    		error_code = Integer.parseInt(attr_value);
				    		list.add(new UNameValuePair(SERVER_ERRORCODE, attr_value));
				    	}
				    	if(error_code != 0) {
				    		list.add(new UNameValuePair(SERVER_RESPONSE, parser.nextText()));
				    		return list;
				    	}
			    	}
			    	else if(DEVICE.equals(parser.getName())) {
				    	if(PANDIGITALSKU.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(PANDIGITALSKU, parser.getAttributeValue(0)));
				    	}
			    	}
			    	else if(PAYLOAD.equals(parser.getName())) {
				    	if(DOWNLOADKEY.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(DOWNLOADKEY, parser.getAttributeValue(0)));
				    	}
				    	if(DOWNLOADNAME.equals(parser.getAttributeName(1))) {
				    		list.add(new UNameValuePair(DOWNLOADNAME, parser.getAttributeValue(1)));
				    	}
			    	}
			    	break;
			    case XmlPullParser.END_TAG:
			    	break;
			}
			event = parser.next();
		}
		return list;
	}
	
	public List<UNameValuePair> getDownloadUrl(InputStream inputStream) throws Exception { 
		List<UNameValuePair> list = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		int event = parser.getEventType();
		while(event!=XmlPullParser.END_DOCUMENT) {
			switch(event) {
			    case XmlPullParser.START_DOCUMENT:
			    	list = new ArrayList<UNameValuePair>();
			    	break;
			    case XmlPullParser.START_TAG:
			    	if(SERVER_RESPONSE.equals(parser.getName())) {
			    		int error_code = 0;
				    	if(SERVER_ERRORCODE.equals(parser.getAttributeName(0))) {
				    		String attr_value = parser.getAttributeValue(0);
				    		error_code = Integer.parseInt(attr_value);
				    		list.add(new UNameValuePair(SERVER_ERRORCODE, attr_value));
				    	}
				    	if(error_code != 0) {
				    		list.add(new UNameValuePair(SERVER_RESPONSE, parser.nextText()));
				    		return list;
				    	}
			    	}
			    	else if(DEVICE.equals(parser.getName())) {
				    	if(PANDIGITALSKU.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(PANDIGITALSKU, parser.getAttributeValue(0)));
				    	}
			    	}
			    	else if(PAYLOAD.equals(parser.getName())) {
				    	if(DOWNLOADKEY.equals(parser.getAttributeName(0))) {
				    		list.add(new UNameValuePair(DOWNLOADKEY, parser.getAttributeValue(0)));
				    	}
				    	if(DOWNLOADNAME.equals(parser.getAttributeName(1))) {
				    		list.add(new UNameValuePair(DOWNLOADNAME, parser.getAttributeValue(1)));
				    	}
				    	if(DOWNLOADURL.equals(parser.getAttributeName(2))) {
				    		list.add(new UNameValuePair(DOWNLOADURL, parser.getAttributeValue(2)));
				    	}
				    	if(DOWNLOADMD5.equals(parser.getAttributeName(3))) {
				    		list.add(new UNameValuePair(DOWNLOADMD5, parser.getAttributeValue(3)));
				    	}
				    	if(DOWNLOADSIZEINB.equals(parser.getAttributeName(4))) {
				    		list.add(new UNameValuePair(DOWNLOADSIZEINB, parser.getAttributeValue(4)));
				    	}
				    	if(DOWNLOADFILETIME.equals(parser.getAttributeName(5))) {
				    		list.add(new UNameValuePair(DOWNLOADFILETIME, parser.getAttributeValue(5)));
				    	}
			    	}
			    	break;
			    case XmlPullParser.END_TAG:
			    	break;
			}
			event = parser.next();
		}
		return list;
	}	
}
