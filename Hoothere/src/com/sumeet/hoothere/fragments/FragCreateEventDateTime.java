package com.sumeet.hoothere.fragments;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sumeet.global.Define;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragCreateEventDateTime extends Fragment{
	
	private View mRootView;
	private static final int SELECTING_DATE_NONE = 0;
	private static final int SELECTING_DATE_START = 1;
	private static final int SELECTING_DATE_END = 2;
	private int mSelectingDateFrom = SELECTING_DATE_NONE;
	
	private HoothereEvent mEvent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_ce_date, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		((TextView)mRootView.findViewById(R.id.txt_frag_ce_date_header_title)).setText(mEvent == null || mEvent.name == null ? "" : mEvent.name);
		mRootView.findViewById(R.id.rl_frag_ce_date_selection_parent).setVisibility(View.GONE);
	}

	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void displayOKAlertDialog(String strMessage, AlertDlgCallback callback){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				callback);
	}
	
	private void displayOKAlertDialog(int resId, AlertDlgCallback callback){
		displayOKAlertDialog(getString(resId), callback);
	}
	
	private void eventHandler(){
		
		mRootView.findViewById(R.id.iv_frag_ce_date_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_ce_date_skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																						getString(R.string.alert_dlg_message_ce_date), 
																						getString(R.string.alert_dlg_yes), 
																						getString(R.string.alert_dlg_cancel),
																						new AlertDlgCallback() {
																							@Override
																							public void onOK() {
																								FragSelectingAddressType fragSelectingAddressType = new FragSelectingAddressType();
																								fragSelectingAddressType.setEvent(mEvent);
																								MainActivity.instance.pushFragment(fragSelectingAddressType, true);
																							}
																							
																							@Override
																							public void onCancel() {
																								
																							}
																						});
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_ce_date_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				String strDateStart = ((TextView)mRootView.findViewById(R.id.edt_frag_ce_date_starting)).getText().toString();
				String strDateEnd = ((TextView)mRootView.findViewById(R.id.edt_frag_ce_date_ending)).getText().toString();
				if (strDateStart.isEmpty()){
					displayOKAlertDialog(R.string.alert_dlg_message_ce_date_invalid_start, null);
					return;
				}
				
				if (strDateEnd.isEmpty()){
					displayOKAlertDialog(R.string.alert_dlg_message_ce_date_invalid_end, null);
					return;
				}
				if (mEvent.startDateTime >= mEvent.endDateTime){
					displayOKAlertDialog(R.string.alert_dlg_message_ce_date_start_later_than_end, null);
					return;
				}
				if (mEvent.startDateTime < Calendar.getInstance().getTimeInMillis()){
					displayOKAlertDialog(R.string.alert_dlg_message_ce_date_start_earlier_than_now, null);
					return;
				}
				FragSelectingAddressType fragSelectingAddressType = new FragSelectingAddressType();
				fragSelectingAddressType.setEvent(mEvent);
				MainActivity.instance.pushFragment(fragSelectingAddressType, true);
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_ce_date_selection_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_ce_date_selection_parent).setVisibility(View.GONE);
				mSelectingDateFrom = SELECTING_DATE_NONE;
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_ce_date_selection_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_ce_date_selection_parent).setVisibility(View.GONE);
				DatePicker dp = (DatePicker)mRootView.findViewById(R.id.dp_frag_ce_date);
				int mon = dp.getMonth();
				int day = dp.getDayOfMonth();
				int year = dp.getYear();
				TimePicker tp = (TimePicker)mRootView.findViewById(R.id.tp_frag_ce_time);
				int hour = tp.getCurrentHour();
				int min = tp.getCurrentMinute();
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, mon, day, hour, min);
				String strDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, cal.getTime()), DateFormat.format(Define.DT_FORMAT_TIME, cal.getTime()));
				if (mSelectingDateFrom == SELECTING_DATE_START){
					((TextView)mRootView.findViewById(R.id.edt_frag_ce_date_starting)).setText(strDate);
					if (mEvent == null) mEvent = new HoothereEvent();
					mEvent.startDateTime = cal.getTimeInMillis();
				}else if (mSelectingDateFrom == SELECTING_DATE_END){
					((TextView)mRootView.findViewById(R.id.edt_frag_ce_date_ending)).setText(strDate);
					if (mEvent == null) mEvent = new HoothereEvent();
					mEvent.endDateTime = cal.getTimeInMillis();
				}
				mSelectingDateFrom = SELECTING_DATE_NONE;
			}
		});
		
		mRootView.findViewById(R.id.edt_frag_ce_date_starting).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView)mRootView.findViewById(R.id.txt_frag_ce_date_selection_hint)).setText(getString(R.string.ce_date_hint_start));
				mSelectingDateFrom = SELECTING_DATE_START;
				mRootView.findViewById(R.id.rl_frag_ce_date_selection_parent).setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.edt_frag_ce_date_ending).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView)mRootView.findViewById(R.id.txt_frag_ce_date_selection_hint)).setText(getString(R.string.ce_date_hint_end));
				mSelectingDateFrom = SELECTING_DATE_END;
				mRootView.findViewById(R.id.rl_frag_ce_date_selection_parent).setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((DatePicker)mRootView.findViewById(R.id.dp_frag_ce_date)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((TimePicker)mRootView.findViewById(R.id.tp_frag_ce_time)).getWindowToken(), 0);
	}
}
