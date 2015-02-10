package com.sumeet.hoothere.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;

public class FragLogo extends Fragment{
	
	private View mRootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_logo, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.btn_frag_logo_get_started).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.pushFragment(new FragSignUp(), true);
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_logo_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				MainActivity.instance.pushFragment(new FragSignIn(), true);
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.GONE);
	}
}
