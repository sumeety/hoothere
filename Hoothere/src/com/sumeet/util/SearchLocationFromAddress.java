package com.sumeet.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.sumeet.callback.OnFinishLocationSearch;
import com.sumeet.hoothere.MainActivity;


public class SearchLocationFromAddress extends AsyncTask<Object, Void, Void> {

	private ProgressDialog pd;
	private double lon;
	private double lat;
	
	private static final String SEARCHADDRESS_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?sensor=true&key=AIzaSyDlFXsZsC7XoYCZ1v6sEL2autBMyJehwUE&input=";
	private static final String SEARCHADDRESS_PREDICTIONS = "predictions";

	/**
	 * Get location information from address
	 * <p>
	 * 
	 * <ul>
	 * SearchLocationFromAddress sf = new SearchLocationFromAddress(((MainActivity)getActivity(),address,true);<br>
	 * sf.start();<br>
	 * </ul>
	 * 
	 * @param content
	 *            The context of activity
	 * @param params
	 *            The address required
	 */	
	public SearchLocationFromAddress(final MainActivity content, String address,
			boolean willAppear) {		
		content.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				pd = new ProgressDialog(content);
				pd.setCancelable(false);
				pd.setMessage("Waiting....");	
			}
		});
	}

	/**
	 * 
	 * @return longitude for address
	 */
	public Double getLon() {
		return lon;
	}

	/**
	 * @return latitude for address
	 */
	public Double getLat() {
		return lat;
	}

	private JSONObject getLocationInfo(String addressToSearch) {

//		HttpGet httpGet = new HttpGet(
//				"http://maps.google.com/maps/api/geocode/json?address="
//						+ addressToSearch + "&ka&sensor=false");

		HttpGet httpGet = new HttpGet(SEARCHADDRESS_URL + addressToSearch);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response;

		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			String str = stringBuilder.toString();
			str = str.replace("\n", "");
			str = str.replace("\t", "");
			str = str.replace("\r", "");
			jsonObject = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	private void getGeoPoint(JSONObject jsonObject, OnFinishLocationSearch callback) {
		JSONArray jsonResult = jsonObject.optJSONArray(SEARCHADDRESS_PREDICTIONS);
		if (callback != null) callback.onSucceed(jsonResult);
	}

	@Override
	public Void doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		lat = 0.0f;
		lon = 0.0f;
		if (arg0.length > 1 && arg0[1] instanceof GPSTracker){
			if (((GPSTracker)arg0[1]).canGetLocation){
				lat = ((GPSTracker)arg0[1]).getLatitude();
				lon = ((GPSTracker)arg0[1]).getLongitude();
			}
		}
		OnFinishLocationSearch callback = null;
		if (arg0.length > 2 && arg0[2] instanceof OnFinishLocationSearch){
			callback = (OnFinishLocationSearch) arg0[2];
		}
		getGeoPoint(getLocationInfo(((String)arg0[0]).replace("\n", " ").replace(" ", "%20")), callback);
		return null;
	}
}
