package com.sumeet.hoothere.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.callback.OnFinishLocationSearch;
import com.sumeet.communication.NetAPIClient;
import com.sumeet.global.Define;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.LocSearchResult;
import com.sumeet.util.SearchLocationFromAddress;

public class FragSearchLocation extends Fragment{
	
	private View mRootView;
	private ListView mLvLocationSearchResult;
	private SearchLocationAdapter mAdapterSearchResult;
	private static final String TAG_SUBTITLE = "sub_title";
	private static final String TAG_TITLE = "title";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_PLACEID = "place_id";
	private static final String TAG_RESULT = "result";
	private static final String TAG_FORMATTED_ADDRESS = "formatted_address";
	private static final String TAG_NAME = "name";
	private static final String TAG_STREET = "street";
	private static final String TAG_CITY = "city";
	private static final String TAG_COUNTRY = "country";
	private static final String TAG_STATE = "state";
	private static final String TAG_ZIP = "zip";
	private static final String TAG_GEOMETRY = "geometry";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LNG = "lng";
	
	private HoothereEvent mEvent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_search_location, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		mAdapterSearchResult = new SearchLocationAdapter();
		mLvLocationSearchResult = (ListView)mRootView.findViewById(R.id.lv_frag_search_location_result);
		mLvLocationSearchResult.setAdapter(mAdapterSearchResult);
		mAdapterSearchResult.notifyDataSetChanged();
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void eventHandler(){
		mLvLocationSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				hideKeyboard();
				try{
					getAddressDetailInfo((JSONObject)mAdapterSearchResult.getItem(position));
				}catch(ClassCastException e){
					e.printStackTrace();
				}
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_search_location_content_search_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_search_location_content_search_close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
			}
		});
		
		((EditText)mRootView.findViewById(R.id.edt_frag_search_location_content_searchbox)).addTextChangedListener(new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (arg0.length() > 0){
					searchLocation(String.format("%s", arg0));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_search_location_content_searchbox)).getWindowToken(), 0);
	}
	
	@SuppressLint("InflateParams")
	private class SearchLocationAdapter extends BaseAdapter{

		private JSONArray mResult;
		
		public void setData(JSONArray result){
			mResult = result;
		}
		
		@Override
		public int getCount() {
			if (mResult != null) return mResult.length();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mResult == null || position >= mResult.length()) return null;
			return mResult.optJSONObject(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.location_search_result_rowitem, null);
			}
			try{
				JSONObject obj = (JSONObject)getItem(position);
				if (obj == null) return null;
				
				JSONObject addressComponents = getTitleForAddress(obj);
				((TextView)rootView.findViewById(R.id.txt_location_search_result_venue)).setText(addressComponents.optString(TAG_TITLE));
				((TextView)rootView.findViewById(R.id.txt_location_search_result_city)).setText(addressComponents.optString(TAG_SUBTITLE));
			}catch(Exception e){
				e.printStackTrace();
			}
			return rootView;
		}
	}
	
	private JSONObject getTitleForAddress(JSONObject source){
		JSONObject result = new JSONObject();
		if (!source.has(TAG_DESCRIPTION)) return result;
		String description = source.optString(TAG_DESCRIPTION);
		String [] addresses = description.split(",");
		String subTitle = "";
		if (addresses == null) return result;
		if (addresses.length == 1){
			try {
				result.put(TAG_TITLE, addresses[0]);
				result.put(TAG_SUBTITLE, "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if (addresses.length > 1){
			try {
				result.put(TAG_TITLE, addresses[0]);
				subTitle = addresses[1];
				for (int i = 2; i < addresses.length; i++){
					subTitle = subTitle + ", " + addresses[i];
				}
				result.put(TAG_SUBTITLE, subTitle);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private void searchLocation(final String sIndex) {
		new Thread() {
			@Override
			public void run() {
				SearchLocationFromAddress searchAddress = new SearchLocationFromAddress(MainActivity.instance, "", false);
				searchAddress.doInBackground(sIndex, MainActivity.instance.myTracker, new OnFinishLocationSearch(){
					@Override
					public void onSucceed(final ArrayList<LocSearchResult> result) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								
							}
						});
					}

					@Override
					public void onFailure() {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
								 	Toast.makeText(MainActivity.instance, getString(R.string.alert_unknown_error), Toast.LENGTH_SHORT).show();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onSucceed(final JSONArray result) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (mAdapterSearchResult == null){
										mAdapterSearchResult = new SearchLocationAdapter();
										mLvLocationSearchResult.setAdapter(mAdapterSearchResult);
									}
									mAdapterSearchResult.setData(result);
									mAdapterSearchResult.notifyDataSetChanged();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		}.start();
	}
	
	private void getAddressDetailInfo(JSONObject source){
		if (source == null) return;
		String strURL = String.format(Define.REVERSEGEOCODE_URL, source.optString(TAG_PLACEID));
		NetAPIClient.sharedInstance(MainActivity.instance).jsonRequestByGET(strURL, null, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (mEvent == null) mEvent = new HoothereEvent();
							if (responseObj == null) return;
							JSONObject result = responseObj.optJSONObject(TAG_RESULT) == null ? new JSONObject() : responseObj.optJSONObject(TAG_RESULT);
							JSONObject addressComponents = extractAddressComponents(result.optString(TAG_FORMATTED_ADDRESS));
							mEvent.address = result.optString(TAG_FORMATTED_ADDRESS);
							mEvent.venueName = result.optString(TAG_NAME);
							mEvent.city = addressComponents.optString(TAG_CITY);
							mEvent.country = addressComponents.optString(TAG_COUNTRY);
							mEvent.state = addressComponents.optString(TAG_STATE);
							mEvent.streetName = addressComponents.optString(TAG_STREET);
							mEvent.zipcode = addressComponents.optString(TAG_ZIP);
							mEvent.latitude = "0.0";
							mEvent.longitude = "0.0";
							if (result.optJSONObject(TAG_GEOMETRY) != null && result.optJSONObject(TAG_GEOMETRY).optJSONObject(TAG_LOCATION) != null){
								try {
									mEvent.latitude = Double.toString(result.getJSONObject(TAG_GEOMETRY).getJSONObject(TAG_LOCATION).getDouble(TAG_LAT));
									mEvent.longitude = Double.toString(result.getJSONObject(TAG_GEOMETRY).getJSONObject(TAG_LOCATION).getDouble(TAG_LNG));
								} catch (JSONException e) {
									e.printStackTrace();
								} catch(NumberFormatException e1){
									e1.printStackTrace();
								}
							}
							hideKeyboard();
							FragEventLocation fragEventLocation = new FragEventLocation();
							mEvent.radius = String.format("%d", Define.RANGE_MAX_VAL / 2);
							fragEventLocation.setEvent(mEvent);
							MainActivity.instance.pushFragment(fragEventLocation, true);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(JSONObject errorObj) {
				
			}

			@Override
			public void failed(VolleyError error) {
				
			}
		});
	}
	
	private JSONObject extractAddressComponents(String address){
		try {
			JSONObject result = new JSONObject();
			if (address == null) return result;
			String [] locationArray = address.split(",");
			String city = "";
			String country = "";
			String state1 = "";
			String street = "";
			String zip = "";
			if (locationArray.length == 4 || locationArray.length > 4){
				String []stateZipArray = locationArray[locationArray.length - 2].split(" ");
				city = locationArray[locationArray.length - 3];
		        if (stateZipArray.length > 2) {
		            zip = stateZipArray[stateZipArray.length - 1];
		            String state = "";
		            for (int j = 0; j < stateZipArray.length - 1; j++) {
		                state = String.format("%s %s", state, stateZipArray[j]);
		            }
		            state1 = state;
		        }
		        country = locationArray[locationArray.length-1];
		        String location = "";
		        for (int j = 0; j < locationArray.length - 3; j++) {
		            location = String.format("%s %s",location, locationArray[j]);
		        }
		        street = location;
			}
			result.put(TAG_STREET, street);
			result.put(TAG_CITY, city);
			result.put(TAG_COUNTRY, country);
			result.put(TAG_ZIP, zip);
			result.put(TAG_STATE, state1);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception e1){
			e1.printStackTrace();
		}
		return null;
	}
}