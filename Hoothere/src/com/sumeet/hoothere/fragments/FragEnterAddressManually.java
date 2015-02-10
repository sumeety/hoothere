package com.sumeet.hoothere.fragments;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.communication.NetAPIClient;
import com.sumeet.global.Define;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragEnterAddressManually extends Fragment{
	
	private View mRootView;
	private HoothereEvent mEvent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_enter_address_manually, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}

	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void initUI(){
		((TextView)mRootView.findViewById(R.id.txt_frag_enter_address_manually_header_title)).setText(mEvent == null || mEvent.name == null ? "" : mEvent.name);
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_venue)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_street)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_city)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_state)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_country)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_zipcode)).getWindowToken(), 0);
	}
	
	private void initEditTexts(){
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_zipcode)).setText("");
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_country)).setText("");
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_venue)).setText("");
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_street)).setText("");
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_state)).setText("");
		((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_city)).setText("");
	}

	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_enter_address_manually_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_enter_address_manually_location).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().post(new Runnable(){
					@Override
					public void run(){
						Location location = MainActivity.instance.myTracker.getLocation();
						if (!MainActivity.instance.myTracker.canGetLocation()){
							MainActivity.instance.myTracker.showSettingsAlert();
							return;
						}
						if (location == null){
							displayOKAlertDialog(R.string.alert_dlg_retrieve_current_location_error);
							initEditTexts();
							return;
						}
						Geocoder geocoder = new Geocoder(MainActivity.instance);
						final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading));
						try {
							final List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
							if (progressDlg != null) progressDlg.dismiss();
							if (addresses != null && addresses.size() > 0){
								MainActivity.instance.runOnUiThread(new Runnable(){
									@Override
									public void run(){
										Address address = addresses.get(0);
										if (address == null){
											displayOKAlertDialog(R.string.alert_dlg_retrieve_current_location_error);
											initEditTexts();
											return;
										}
										
										String zipcode = address.getPostalCode();
										String strVenue = address.getSubThoroughfare();
										String strCountry = address.getCountryName();
										String strCity = address.getLocality();
										String strState = address.getAdminArea();
										String street = address.getThoroughfare();
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_zipcode)).setText(zipcode == null || zipcode.equals("null") ? "" : zipcode);
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_country)).setText(strCountry == null || strCountry.equals("null") ? "" : strCountry);
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_venue)).setText(strVenue == null || strVenue.equals("null") ? "" : strVenue);
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_street)).setText(street == null || street.equals("null") ? "" : street);
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_state)).setText(strState == null || strState.equals("null") ? "" : strState);
										((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_city)).setText(strCity == null || strCity.equals("null") ? "" : strCity);
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
							initEditTexts();
						}
						if (progressDlg != null) progressDlg.dismiss();
					}
				});
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_enter_address_manually_skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																						getString(R.string.alert_dlg_location_map_skip), 
																						getString(R.string.alert_dlg_btn_ok), 
																						getString(R.string.alert_dlg_cancel), 
																						new AlertDlgCallback() {
																							@Override
																							public void onOK() {
																								hideKeyboard();
																								sendCreateEventRequest();
																							}
																							
																							@Override
																							public void onCancel() {
																								
																							}
																						});
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_enter_address_manually_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strVenue = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_venue)).getText().toString();
				String strStreet = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_street)).getText().toString();
				String strCity = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_city)).getText().toString();
				String strState = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_state)).getText().toString();
				String strCountry = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_country)).getText().toString();
				String strZip = ((EditText)mRootView.findViewById(R.id.edt_frag_enter_address_manually_zipcode)).getText().toString();
				hideKeyboard();
				if (strVenue.isEmpty() || strStreet.isEmpty() || strCity.isEmpty() || strState.isEmpty() || strCountry.isEmpty() || strZip.isEmpty()){
					displayOKAlertDialog(R.string.alert_dlg_manual_location_empty);
					return;
				}
				if (mEvent == null) mEvent = new HoothereEvent();
				mEvent.venueName = strVenue;
				mEvent.city = strCity;
				mEvent.country = strCountry;
				mEvent.state = strState;
				mEvent.zipcode = strZip;
				mEvent.streetName = strStreet;
				mEvent.address = String.format("%s %s %s %s %s", strStreet, strCity, strState, strZip, strCountry);
				mEvent.latitude = "0";
				mEvent.longitude = "0";
				getLocationLatLng(mEvent.address);
			}
		});
	}
	
	private void sendCreateEventRequest(){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_creating_event));
		new CommunicationAPIManager(MainActivity.instance).sendRequestCreateEvent(mEvent == null ? new JSONObject() : mEvent.asJSON(), new NetAPICallBack(){

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
							}else{
								HoothereEvent event = new HoothereEvent(responseObj);
								FragEventDetail fragEventDetail = new FragEventDetail();
								fragEventDetail.setFlagReturnToHome(true);
								fragEventDetail.setEvent(event);
								MainActivity.instance.pushFragment(fragEventDetail, true);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(final JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void getLocationLatLng(String locationName){
		if (locationName == null) return;
	    locationName = locationName.replace(" ", "+");
	    String url = String.format(Define.GEOCODE_URL,locationName);
	    
	    final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading));
	    
	    NetAPIClient.sharedInstance(MainActivity.instance).jsonRequestByGET(url, null, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj != null){
								String status;
								try {
									status = responseObj.getString(Define.GEOCODER_STATUS);
									if (status != null && status.equals(Define.GEOCODER_STATUS_OK)){
										JSONArray result = responseObj.optJSONArray(Define.GEOCODER_RESULTS);
										if (result != null && result.length() > 0){
											if (mEvent == null) mEvent = new HoothereEvent();
											mEvent.latitude = Double.toString(result.getJSONObject(0).getJSONObject(Define.GEOCODER_GEOMETRY).getJSONObject(Define.GEOCODER_LOCATION).getDouble(Define.GEOCODER_LAT));
											mEvent.longitude = Double.toString(result.getJSONObject(0).getJSONObject(Define.GEOCODER_GEOMETRY).getJSONObject(Define.GEOCODER_LOCATION).getDouble(Define.GEOCODER_LNG));
										}else{
											displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_manual_location_unable_connect_service);
									}
								} catch (JSONException e) {
									displayOKAlertDialog(R.string.alert_dlg_manual_location_unable_connect_service);
								}catch(NullPointerException e){
									e.printStackTrace();
									displayOKAlertDialog(R.string.alert_dlg_manual_location_unable_connect_service);
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_manual_location_unable_connect_service);
							}
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
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_common_error);
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
			public void failed(VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_common_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
	    });
	}
}