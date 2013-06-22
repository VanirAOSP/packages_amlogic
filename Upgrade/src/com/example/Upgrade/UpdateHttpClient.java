package com.example.Upgrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.content.Context;

import android.os.SystemProperties;

public class UpdateHttpClient {
	private final static String TAG = "UpdateHttpClient";
	public final String URL = SystemProperties.get("ro.customize.upgrade.url", null);//"http://10.28.8.140:8080/PanHub.RDS.Lib.svc/";
	//private List<NameValuePair> params;
	
	public UpdateHttpClient() {
		//params = new ArrayList<NameValuePair>();
	}
	
	public State getNetworkState(Context mContext) {
		ConnectivityManager conMan = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(UpgradeActivity.DEBUG) Log.d(TAG,"**************************state wifi\n:"+wifi);
		//ethernet
		State ethernet = conMan.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
		if(UpgradeActivity.DEBUG) Log.d(TAG,"**************************state ethernet\n:"+ethernet);
		
		if(ethernet==State.CONNECTED)
		{
			if(UpgradeActivity.DEBUG) Log.d(TAG,"**************************ethernet==\n:"+State.CONNECTED);
			return State.CONNECTED;
		}
		else
		{
			return wifi;
		}
		//return State.CONNECTED;
	}
	
	public boolean connectServerByURL() {
		HttpGet httpRequest = new HttpGet(URL);
		if(UpgradeActivity.DEBUG) Log.d(TAG,"*********URL:"+URL);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			}
		}
		catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
		}  
		return false;
	}
	
	public List<UNameValuePair> getServerAbout(String url) {
		InputStream inputStream = null;
		List<UNameValuePair> list = null;
		
		inputStream = getInputStreamByGet(url);
		if(UpgradeActivity.DEBUG) Log.d(TAG,"**************************getServerAbout\n:"+inputStream);
		if(inputStream == null) {
			return null;
		}
		
		try {
			PullParserXml handler = new PullParserXml();
			list = handler.getServerAbout(inputStream);
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
			return null;
		}
		try {
			inputStream.close();
		}
		catch(IOException e) {
			Log.e(TAG, e.getMessage().toString());
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	public List<String> getStringFromServer(String url, List<String> names) {
		int i = 0, count = 0;
		InputStream inputStream = null;
		List<UNameValuePair> list = null;
		List<String> values = new ArrayList<String>();
		
		if(url == null || names == null) {
			return null;	
		}
		count = names.size();
		if(count == 0)
			return null;
		
		inputStream = getInputStreamByGet(url);
		if(UpgradeActivity.DEBUG) Log.d(TAG,"**************************getStringFromServer\n:"+inputStream);
		if(inputStream == null) {
			return null;
		}
		
		String name = null;
		
		name = names.get(0);
		try {
			PullParserXml handler = new PullParserXml();
			if(name.equals(PullParserXml.PANDIGITALSKU)) {
				list = handler.getSkuName(inputStream);
			}
			else if(name.equals(PullParserXml.DOWNLOADKEY)) {
				list = handler.getDownloadKey(inputStream);
			}
			else if(name.equals(PullParserXml.DOWNLOADURL) 
					|| name.equals(PullParserXml.DOWNLOADMD5)
					|| name.equals(PullParserXml.DOWNLOADSIZEINB)) {
				list = handler.getDownloadUrl(inputStream);
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
			return null;
		}
		try {
			inputStream.close();
		}
		catch(IOException e) {
			Log.e(TAG, e.getMessage().toString());
			e.printStackTrace();
			return null;
		}
		for(i=0; i<count; i++) {
			name = names.get(i);
			String value = getStringForList(list, name);
			values.add(value);
		}
		return values;
	}
	
	public int downFile(String device,String file, String url,long startOffset,long expectedLength) {
		InputStream inputStream = null;
		FileUtils fileUtils = new FileUtils();
		Log.d(TAG,"************downfile"+url);
		if((device ==  null) || (file == null) || (url == null)) {
			Log.d(TAG,"************null");
			return -1;
		}
		
		if(fileUtils.exists(device+file)) {
			Log.d(TAG,"************update.zip exists");
			return 1;
		}
		
		//downloading
		//inputStream = getInputStreamByGet(url);
		inputStream =get(url,startOffset,expectedLength);
		
		if(inputStream == null) {
			Log.d(TAG,"************inputStream null");
			return -1;
		}
		
		int tag = fileUtils.writeFromInput(device, file, inputStream);
		if(tag < 0) {
			Log.d(TAG,"************writeFromInput error");
			return -1;
		}
		
		try {
			inputStream.close();
		}
		catch(IOException e) {
			Log.e(TAG, e.getMessage().toString());
			e.printStackTrace();
			Log.d(TAG,"***********inputstream.close");
			return -1;
		}

		return 0;
	}
	
	public InputStream getInputStreamByGet(String url) {
		InputStream inputStream = null;
		HttpGet httpRequest = new HttpGet(url);
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if(UpgradeActivity.DEBUG) Log.d(TAG, "get size:"+Long.toString(httpResponse.getEntity().getContentLength()));
				inputStream = httpResponse.getEntity().getContent();
			}
		}
		catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
		}
		return inputStream;
	}
    private InputStream get(String url, long startOffset,long expectedLength){
		InputStream inputStream = null;
		InputStream is = null;
		try{
			HttpGet mHttpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
	        int expectedStatusCode = HttpStatus.SC_OK;
	        if (startOffset > 0) {
	            String range = "bytes=" + startOffset + "-";
	            if (expectedLength >= 0) {
	                range += expectedLength-1;
	            }
	            Log.i(TAG, "requesting byte range " + range);
	            mHttpGet.addHeader("Range", range);
	            expectedStatusCode = HttpStatus.SC_PARTIAL_CONTENT;
	        }
	        HttpResponse response = httpclient.execute(mHttpGet);
	        long bytesToSkip = 0;
	        int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != expectedStatusCode) {
	            if ((statusCode == HttpStatus.SC_OK)
	                    && (expectedStatusCode
	                            == HttpStatus.SC_PARTIAL_CONTENT)) {
	                Log.i(TAG, "Byte range request ignored");
	                bytesToSkip = startOffset;
	            } 
	        }
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        if (bytesToSkip > 0) {
	            is.skip(bytesToSkip);
	        }
    	}
    	catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
		}
        return is;
    }
    
	public InputStream getInputStreamByPost(String url, List<NameValuePair> params) {
		InputStream inputStream = null;
		HttpPost httpRequest = new HttpPost(url);

		try {
			HttpEntity httpentity = new UrlEncodedFormEntity(params, "gb2312");
			httpRequest.setEntity(httpentity);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if(UpgradeActivity.DEBUG) Log.d(TAG, "get size:"+Long.toString(httpResponse.getEntity().getContentLength()));
				inputStream = httpResponse.getEntity().getContent();
			}
		}
		catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
		}
		return inputStream;
	}
	
	private String getStringForList(List<UNameValuePair> list, String name) {
		if(list == null || name == null)
			return null;
		
		int i = 0, count = 0;
		UNameValuePair item = null;
		
		count = list.size();
		for(i=0; i<count; i++) {
			item = list.get(i);
			if(name.equals(item.getName()))
				return item.getValue();
		}
		return null;
	}
}
