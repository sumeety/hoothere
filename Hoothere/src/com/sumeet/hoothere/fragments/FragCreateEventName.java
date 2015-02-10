package com.sumeet.hoothere.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;

import com.sumeet.global.Define;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragCreateEventName extends Fragment{
	
	private View mRootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_ce_name, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		((Switch)mRootView.findViewById(R.id.sw_frag_ce_name_event_setting_switch)).setChecked(false);
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_ce_name_event_name)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_ce_name_event_description)).getWindowToken(), 0);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.btn_frag_ce_name_event_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.pushFragment(new FragSearchEvent(), true);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_ce_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_ce_name_event_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_ce_name_event_create).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				String strName = ((EditText)mRootView.findViewById(R.id.edt_frag_ce_name_event_name)).getText().toString();
				String strDescription = ((EditText)mRootView.findViewById(R.id.edt_frag_ce_name_event_description)).getText().toString();
				if (strName.isEmpty() || strDescription.isEmpty()){
					AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																							getString(R.string.alert_dlg_message_ce), 
																							getString(R.string.alert_dlg_btn_ok), 
																							null,
																							null);
					return;
				}
				HoothereEvent event = new HoothereEvent();
				event.name = strName;
				event.description = strDescription;
				event.eventType = ((Switch)mRootView.findViewById(R.id.sw_frag_ce_name_event_setting_switch)).isChecked() ? Define.EVENT_TYPE_PUBLIC : Define.EVENT_TYPE_PRIVATE;
				FragCreateEventDateTime fragCreateEventDateTime = new FragCreateEventDateTime();
				fragCreateEventDateTime.setEvent(event);
				MainActivity.instance.pushFragment(fragCreateEventDateTime, true);
			}
		});
	}
}