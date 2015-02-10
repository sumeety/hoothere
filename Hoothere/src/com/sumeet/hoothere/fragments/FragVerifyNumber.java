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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragVerifyNumber extends Fragment{
	
	private View mRootView;
	private boolean mFlagVerifying = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_verify_number, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		((TextView)mRootView.findViewById(R.id.txt_frag_verify_number_your_number)).setText(Global.GUser == null || Global.GUser.mobile == null ? "" : Global.GUser.mobile); 
	}
	
	public void setPhoneNumber(String strNumber){

	}
	
	public void setUserID(String strUserId){
		
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
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.m_curFrag = this;
		MainActivity.instance.setFooterVisibility(View.GONE);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_verify_number_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_verify_number_not_received).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_sending_request));
				new CommunicationAPIManager(MainActivity.instance).sendNotReceivedCodeRequest(new NetAPICallBack(){
					@Override
					public void succeed(final JSONObject responseObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									if (responseObj == null){
										displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
									}else{
										Global.GUser = new UserInformation(responseObj);
										MainActivity.config.saveLoggedIn(true);
										MainActivity.config.saveUserLoginInfo(new JSONObject(Global.GUser.asHashMap()));
										displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
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
									if (errorObj != null && errorObj.has(Define.ERRORMESSAGE_TAG)){
										displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG));
									}else{
										displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
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
									if (error != null){
										if (error.getMessage() != null && error.getMessage().equals(Define.JSONEXCEPTION_SUCCESS)){
											displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
										}else if (error.getMessage() == null){
											displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
										}else{
											displayOKAlertDialog(error.getMessage());
										}
									}else{
										displayOKAlertDialog(String.format(Define.ALERT_DLG_VERIFICATION_SUCCESS, Global.GUser == null ? "" : Global.GUser.mobile));
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
		
		mRootView.findViewById(R.id.btn_frag_verify_number_finish).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				String strVerifyCode = ((EditText)mRootView.findViewById(R.id.edt_frag_verify_number_input)).getText().toString();
				if (strVerifyCode == null || strVerifyCode.isEmpty()){
					displayOKAlertDialog(R.string.alert_dlg_verify_number_alert_enter_code);
					return;
				}
				if (Global.GUser == null || Global.GUser.userid == 0){
					displayOKAlertDialog(R.string.alert_dlg_verify_signin_signup_prior);
					return;
				}
				if (mFlagVerifying){
					return;
				}
				mFlagVerifying = true;
				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_verifying));
				new CommunicationAPIManager(MainActivity.instance).sendVerifyCodeRequest(Global.GUser.userid, strVerifyCode, Global.deviceToken, new NetAPICallBack() {
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
										}else if (responseObj.has(Define.USERINFO_ID)){
											try {
												String userid = responseObj.getString(Define.USERINFO_ID);
												displayOKAlertDialog(R.string.alert_dlg_verify_success);
												Global.GUser = new UserInformation(responseObj);
												MainActivity.config.saveLoggedIn(true);
												MainActivity.config.saveUserLoginInfo(new JSONObject(Global.GUser.asHashMap()));
												if (userid != null && userid.length() > 0){
													MainActivity.instance.pushFragment(new FragEvents(), false);
												}
											} catch (JSONException e) {
												displayOKAlertDialog(R.string.alert_dlg_verify_error);
												e.printStackTrace();
											}
										}else if (responseObj.has(Define.ERROR_TAG)){
											try {
												displayOKAlertDialog(responseObj.getString(Define.ERROR_TAG));
											} catch (JSONException e) {
												displayOKAlertDialog(R.string.alert_dlg_verify_error);
												e.printStackTrace();
											}
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_verify_error);
									}
								}catch(Exception e){
									e.printStackTrace();
								}
								mFlagVerifying = false;
							}
						});
					}
					
					@Override
					public void failed(final JSONObject errorObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								mFlagVerifying = false;
								try{
									if (progressDlg != null) progressDlg.dismiss();
									displayOKAlertDialog(R.string.alert_dlg_verify_error);
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
								mFlagVerifying = false;
								try{
									if (progressDlg != null) progressDlg.dismiss();
									displayOKAlertDialog(R.string.alert_dlg_verify_error);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_verify_number_change).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.pushFragment(new FragChangeNumber(), true);
			}
		});
	}
	
	public void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_verify_number_input)).getWindowToken(), 0);
	}
}
