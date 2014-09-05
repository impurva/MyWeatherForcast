package com.example.myweatherforcast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;


public class servletConnectTask extends AsyncTask<String, Void, String>{
	
	private String response;
	private MainActivity ma;
	
	public String getResponse() {
		return response;
	}

	public servletConnectTask(MainActivity ma){
		this.ma=ma;
		
	}
	
	@Override
	protected String doInBackground(String... urls) {
		response = "";
		for (String url : urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					response += s;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	/*	JSONObject mainObject;
		JSONObject weather;
		try {
			mainObject = new JSONObject(response);
			weather=mainObject.getJSONObject("weather");
//			JSONArray result=results.getJSONArray("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		ma.setJsonString(response);
	}
	

}
