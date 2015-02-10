package com.sumeet.hoothere.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;

public class FragSelectingAddressType extends Fragment{
	
	private View mRootView;
	private HoothereEvent mEvent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_addresstype, container, false);
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
		((TextView)mRootView.findViewById(R.id.txt_frag_addresstype_header_title)).setText(mEvent == null || mEvent.name == null ? "" : mEvent.name); 
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_addresstype_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_addresstype_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragSearchLocation fragSearchLocation = new FragSearchLocation();
				fragSearchLocation.setEvent(mEvent);
				MainActivity.instance.pushFragment(fragSearchLocation, true);
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_addresstype_yourself).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragEnterAddressManually fragEnterAddressManually = new FragEnterAddressManually();
				fragEnterAddressManually.setEvent(mEvent);
				MainActivity.instance.pushFragment(fragEnterAddressManually, true);
			}
		});
	}
}
