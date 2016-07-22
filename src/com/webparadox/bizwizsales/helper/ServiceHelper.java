package com.webparadox.bizwizsales.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;

public class ServiceHelper {
	JSONObject JSONresponseText;
	Context context;
	JSONObject jsonResultText;

	public ServiceHelper(Context context) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.context = context;

	}

	public JSONObject jsonSendHTTPRequest(String requestData,
			String requestURL, String requestType) {
		try {
			if (!Utilities.isConnectingToInternet(context)) {
				JSONresponseText = new JSONObject();
				JSONresponseText.put(Constants.KEY_ERROR,
						Constants.TOAST_INTERNET);
			} else {
				HttpURLConnection connection = null;
				try {
					Log.e("request data", requestData + Constants.EMPTY_STRING
							+ requestURL);
					URL object = new URL(requestURL);
					connection = (HttpURLConnection) object.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod(requestType);
					connection.setRequestProperty(Constants.KEY_CONTENT_TYPE,
							Constants.VALUE_CONTENT_TYPE);
					connection.setRequestProperty(Constants.KEY_ACCEPT,
							Constants.VALUE_CONTENT_TYPE);
					connection.setRequestProperty(Constants.KEY_AUTHENTICATION,
							Constants.VALUE_ENCODING_AUTHENTICATION);

					if (requestType
							.equalsIgnoreCase(Constants.REQUEST_TYPE_POST)) {
						OutputStreamWriter streamWriter = new OutputStreamWriter(
								connection.getOutputStream());
						streamWriter.write(requestData);
						streamWriter.flush();
					}else{
						requestURL = requestURL.replaceAll(" ", "%20");
					}

					StringBuilder stringBuilder = new StringBuilder();
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						InputStreamReader streamReader = new InputStreamReader(
								connection.getInputStream());
						BufferedReader bufferedReader = new BufferedReader(
								streamReader);
						String response = null;
						while ((response = bufferedReader.readLine()) != null) {
							stringBuilder.append(response + "\n");
						}
						bufferedReader.close();
						Log.d("Result Value ", stringBuilder.toString());
						jsonResultText = new JSONObject(
								stringBuilder.toString());
						return jsonResultText;
					} else {
						Log.e("Error = ", connection.getResponseMessage());
						return null;
					}
				} catch (Exception exception) {
					Log.e("Error = ", exception.toString());
					return null;
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		} catch (Exception e) {
			Log.e("Error = ", e.toString());
		}
		return JSONresponseText;
	}
}
