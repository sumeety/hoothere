package com.sumeet.hoothere.fragments;

import java.util.Calendar;
import java.util.List;

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
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragEditEvent extends Fragment{
	
	private View mRootView;
	private int mDateType;
	private static final int DATE_TYPE_START = 1;
	private static final int DATE_TYPE_END = 2;
	private static final int DATE_TYPE_NONE = 0;
	private long mDateTimeStart = 0;
	private long mDateTimeEnd = 0;
	private HoothereEvent mEvent;
	private FragEditEvent instance;
	private Fragment mCaller;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_edit_event, container, false);
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void initUI(){
		displayEventInfo();
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void displayOKAlertDialog(String strMessage){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_edit_event_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_edit_event_content_save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				modifyEvent();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_edit_event_content_set_geofence).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				FragEventLocation fragEventLocation = new FragEventLocation();
				fragEventLocation.setEvent(mEvent);
				fragEventLocation.setCaller(instance);
				fragEventLocation.setCreateOrEdit(true);
				MainActivity.instance.pushFragment(fragEventLocation, true);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_edit_event_content_end_date_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mDateType = DATE_TYPE_END;
				setDateTimePickerValue(mDateTimeEnd);
				((TextView)mRootView.findViewById(R.id.txt_frag_ee_date_selection_hint)).setText(getString(R.string.ce_date_hint_end));
				mRootView.findViewById(R.id.rl_frag_ee_date_selection_parent).setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_edit_event_content_start_date_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mDateType = DATE_TYPE_START;
				setDateTimePickerValue(mDateTimeStart);
				((TextView)mRootView.findViewById(R.id.txt_frag_ee_date_selection_hint)).setText(getString(R.string.ce_date_hint_start));
				mRootView.findViewById(R.id.rl_frag_ee_date_selection_parent).setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_edit_event_content_location).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getCurrentLocation();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_ee_date_selection_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDateType = DATE_TYPE_NONE;
				mRootView.findViewById(R.id.rl_frag_ee_date_selection_parent).setVisibility(View.GONE);
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_ee_date_selection_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePicker dp = (DatePicker)mRootView.findViewById(R.id.dp_frag_ee_date);
				int mon = dp.getMonth();
				int day = dp.getDayOfMonth();
				int year = dp.getYear();
				TimePicker tp = (TimePicker)mRootView.findViewById(R.id.tp_frag_ee_time);
				int hour = tp.getCurrentHour();
				int min = tp.getCurrentMinute();
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, mon, day, hour, min);
				String strDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, cal.getTime()), DateFormat.format(Define.DT_FORMAT_TIME, cal.getTime()));
				if (mDateType == DATE_TYPE_START){
					((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_start_date)).setText(strDate);
					mDateTimeStart = cal.getTimeInMillis();
				}else{
					((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_end_date)).setText(strDate);
					mDateTimeEnd = cal.getTimeInMillis();
				}
				mDateType = DATE_TYPE_NONE;
				mRootView.findViewById(R.id.rl_frag_ee_date_selection_parent).setVisibility(View.GONE);
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initUI();
		eventHandler();
	}
	
	public void setCaller(Fragment caller){
		mCaller = caller;
	}
	
	private void displayEventInfo(){
		if (mEvent == null) return;
		mDateTimeStart = mEvent.startDateTime;
		mDateTimeEnd = mEvent.endDateTime;
		((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_event_location_content)).setText(mEvent.venueName == null || mEvent.venueName.equals("null") ? "" : mEvent.venueName);
		((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_street)).setText(mEvent.address == null || mEvent.address.equals("null") ? "" : mEvent.address);
		String strStartDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, mEvent.startDateTime), DateFormat.format(Define.DT_FORMAT_TIME, mEvent.startDateTime));
		((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_start_date)).setText(mEvent.startDateTime == 0 ? getString(R.string.date_not_specified) : strStartDate);
		String endDateTime = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, mEvent.endDateTime), DateFormat.format(Define.DT_FORMAT_TIME, mEvent.endDateTime));
		((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_end_date)).setText(mEvent.endDateTime == 0 ? getString(R.string.date_not_specified) : endDateTime);
		((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_description_content)).setText(mEvent.description == null || mEvent.description.equals("null") ? "" : mEvent.description);
		((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_title_content)).setText(mEvent.name == null || mEvent.name.equals("null") ? "" : mEvent.name);
		((Switch)mRootView.findViewById(R.id.sw_frag_edit_event_setting_switch)).setChecked(mEvent.eventType == null ? true : mEvent.eventType.equals(Define.EVENT_TYPE_PUBLIC));
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_event_location_content)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_street)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_description_content)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_title_content)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((DatePicker)mRootView.findViewById(R.id.dp_frag_ee_date)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((TimePicker)mRootView.findViewById(R.id.tp_frag_ee_time)).getWindowToken(), 0);
	}
	
	private void modifyEvent(){
		if (mEvent == null) return;
		if (!checkValidDate()) return;
		String strTitle = ((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_title_content)).getText().toString();
		if (strTitle.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_modify_event_enter_title);
			return;
		}
		
		String strDescription = ((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_description_content)).getText().toString();
		if (strDescription.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_modify_event_enter_description);
			return;
		}

		String strAddress = ((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_street)).getText().toString();
		if (strAddress.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_modify_event_enter_address);
			return;
		}

		String strVenue = ((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_event_location_content)).getText().toString();
		if (strVenue.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_modify_event_enter_venue);
			return;
		}
		mEvent.startDateTime = mDateTimeStart;
		mEvent.endDateTime = mDateTimeEnd;
		mEvent.address = strAddress;
		mEvent.venueName = strVenue;
		mEvent.name = strTitle;
		mEvent.description = strDescription;
		mEvent.eventType = ((Switch)mRootView.findViewById(R.id.sw_frag_edit_event_setting_switch)).isChecked() ? Define.EVENT_TYPE_PUBLIC : Define.EVENT_TYPE_PRIVATE;
		JSONObject json = mEvent.asJSON();
		if (json == null) return;
		try {
			json.put(Define.EVENT_MODIFIEDBY, Global.GUser == null ? 0 : Global.GUser.userid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_modifying_event));

		new CommunicationAPIManager(MainActivity.instance).sendRequestModifyEvent(mEvent.id, json, new NetAPICallBack(){

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj != null){
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									String strMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
									if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
										displayOKAlertDialog(R.string.alert_dlg_modify_event_success);
									}else{
										displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
									}
								}else{
									displayOKAlertDialog(R.string.alert_dlg_modify_event_success);
									HoothereEvent event = new HoothereEvent(responseObj);
									if (mCaller != null && mCaller instanceof FragEventDetail){
										((FragEventDetail)mCaller).setEvent(event);
									}
									MainActivity.instance.onBackPressed();
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_modify_event_error);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(final JSONObject errorObj){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (errorObj != null && errorObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG));
							}else{
								displayOKAlertDialog(R.string.alert_dlg_title_hoothere);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(final VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_modify_event_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void getCurrentLocation(){
		final Location location = MainActivity.instance.myTracker.getLocation();
		if (!MainActivity.instance.myTracker.canGetLocation()){
			MainActivity.instance.myTracker.showSettingsAlert();
			return;
		}
		if (location == null){
			displayOKAlertDialog(R.string.alert_dlg_retrieve_current_location_error);
			return;
		}
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_getting_current_location));

		new Handler().post(new Runnable(){
			@Override
			public void run(){
				Geocoder geocoder = new Geocoder(MainActivity.instance);
				try {
					final List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
					if (addresses != null && addresses.size() > 0){
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								Address address = addresses.get(0);
								if (address == null){
									if (progressDlg != null) progressDlg.dismiss();
									displayOKAlertDialog(R.string.alert_dlg_retrieve_current_location_error);
									return;
								}
								String strVenue = address.getSubThoroughfare();
								String strAddress = "";
								for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
									if (i == 0) strAddress = address.getAddressLine(i);
									else strAddress = strAddress + "," + address.getAddressLine(i);
								}
								((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_event_location_content)).setText(strVenue == null || strVenue.equals("null") ? "" : strVenue);
								((EditText)mRootView.findViewById(R.id.edt_frag_edit_event_content_street)).setText(strAddress == null || strAddress.equals("null") ? "" : strAddress);
							}
						});
					}
					if (progressDlg != null) progressDlg.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					if (progressDlg != null) progressDlg.dismiss();
					displayOKAlertDialog(e.getMessage());
				}
			}
		});
	}
	
	private void setDateTimePickerValue(long timeStamp){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp);
		DatePicker dp = (DatePicker)mRootView.findViewById(R.id.dp_frag_ee_date);
		TimePicker tp = (TimePicker)mRootView.findViewById(R.id.tp_frag_ee_time);
		dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		tp.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		tp.setCurrentMinute(cal.get(Calendar.MINUTE));
	}

	private boolean checkValidDate(){
		String strDateStart = ((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_start_date)).getText().toString();
		String strDateEnd = ((TextView)mRootView.findViewById(R.id.txt_frag_edit_event_content_end_date)).getText().toString();
		if (strDateStart.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_message_ce_date_invalid_start);
			return false;
		}
		
		if (strDateStart.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_message_ce_date_invalid_start);
			return false;
		}
		
		if (strDateEnd.isEmpty()){
			displayOKAlertDialog(R.string.alert_dlg_message_ce_date_invalid_end);
			return false;
		}
		if (mDateTimeStart >= mDateTimeEnd){
			displayOKAlertDialog(R.string.alert_dlg_message_ce_date_start_later_than_end);
			return false;
		}
		if (mDateTimeStart < Calendar.getInstance().getTimeInMillis()){
			displayOKAlertDialog(R.string.alert_dlg_message_ce_date_start_earlier_than_now);
			return false;
		}
		return true;
	}
}