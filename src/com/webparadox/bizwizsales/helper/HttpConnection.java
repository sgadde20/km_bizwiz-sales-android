package com.webparadox.bizwizsales.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;

import android.net.Uri;
import android.util.Log;

import com.webparadox.bizwizsales.libraries.Constants;

public class HttpConnection {

	private String ServerUrl;
	private String data1;

	public String result;
	private Header[] headers;
	private MultipartEntity entity;

	public HttpConnection() {
	}

	private String callWebService() {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost request = new HttpPost(ServerUrl);
			request.setEntity(entity);
			request.setHeaders(headers);
			ResponseHandler<String> handler = new BasicResponseHandler();
			result = httpclient.execute(request, handler);
			Log.d("RESULT", result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return "IOException";
		}

		httpclient.getConnectionManager().shutdown();
		return result;
	}

	public String sending() throws JSONException, UnsupportedEncodingException {
		String result = "";
		result = callWebService();
		return result;
	}

	public String httpGetRequest(String requestString)
			throws UnsupportedEncodingException, JSONException {
		Header[] headers = { new BasicHeader("Content-Type",
				"application/x-www-form-urlencoded") };

		this.headers = headers;

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(requestString);
		request.setHeaders(headers);
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return "IOException";
		}

		httpclient.getConnectionManager().shutdown();

		return result;
	}

	public String sendPhoto(int dealerId, int customerId, int employeeId, String appointMentResultId,
			String desc, int sectionId, String filePath) throws JSONException,
			FileNotFoundException, IOException {
		Log.e("filePath ===> ", filePath);
		this.ServerUrl = Constants.SEND_PHOTO_URL;
		Header[] header = { new BasicHeader("ContentType",
				"multipart/form-data") };
		this.headers = header;
		entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			entity.addPart("dealerId", new StringBody(String.valueOf(dealerId)));
			entity.addPart("customerId",
					new StringBody(String.valueOf(customerId)));
			entity.addPart("userId", new StringBody(String.valueOf(employeeId)));
			
			if(appointMentResultId != null){
				if(appointMentResultId.length() >0){
					entity.addPart("AppointmentResultId", new StringBody(appointMentResultId));
				}
			}
			
			entity.addPart("description", new StringBody(desc));
			entity.addPart("sectionId",
					new StringBody(String.valueOf(sectionId)));
			byte[] fileBytes = getFileBytes(filePath);
			String photoName = Uri.parse(filePath).getLastPathSegment();
			Log.d("photoName", "photoName" + photoName);
			entity.addPart("fileData", new ByteArrayBody(fileBytes,
					"image/jpeg", photoName));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		data1 = sending();

		entity = null;

		return data1;
	}

	@SuppressWarnings("resource")
	private byte[] getFileBytes(String fileName) throws IOException,
	FileNotFoundException {
		File f = new File(fileName);

		byte[] fileBytes = new byte[(int) f.length()];
		new FileInputStream(f).read(fileBytes);
		return fileBytes;
	}

}
