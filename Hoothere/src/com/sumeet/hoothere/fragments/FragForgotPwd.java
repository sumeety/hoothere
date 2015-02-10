package com.sumeet.hoothere.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.util.AlertDialogManager;

public class FragForgotPwd extends Fragment{
	
	private View mRootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_forgot_pwd, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_forgot_pwd_content_email)).getWindowToken(), 0);
	}
	
	private void displayOKAlertDialog(String strMessage){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_forgot_pwd_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_forgot_pwd_content_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				final String strEmail = ((EditText)mRootView.findViewById(R.id.edt_frag_forgot_pwd_content_email)).getText().toString();
				if (strEmail.isEmpty()){
					displayOKAlertDialog(R.string.enter_valid_email);
					return;
				}
				JSONObject params = new JSONObject();
				try {
					params.put(Define.USERINFO_EMAIL, strEmail);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_sending_request));
				new CommunicationAPIManager(MainActivity.instance).sendRequestForgotPwd(params, new NetAPICallBack() {
					@Override
					public void succeed(final JSONObject responseObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									if (responseObj != null){
										if (responseObj.has(Define.ERRORMESSAGE_TAG)){
											displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
										}else{
											displayOKAlertDialog(R.string.alert_dlg_forgot_pwd_success);
											MainActivity.instance.onBackPressed();
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_forgot_pwd_success);
										MainActivity.instance.onBackPressed();
									}
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
					
					@Override
					public void failed(final JSONObject errorObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									if (errorObj == null){
										displayOKAlertDialog(R.string.alert_dlg_forgot_pwd_success);
										MainActivity.instance.onBackPressed();
									}else{
										displayOKAlertDialog(R.string.alert_dlg_common_error);
									}
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		});
	}
}