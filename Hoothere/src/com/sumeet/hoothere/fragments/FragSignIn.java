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
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragSignIn extends Fragment{
	
	private View mRootView;
	private EditText mEdtEmail;
	private EditText mEdtPwd;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_signin, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		mEdtEmail = (EditText)mRootView.findViewById(R.id.edt_frag_signin_content_email);
		mEdtPwd = (EditText)mRootView.findViewById(R.id.edt_frag_signin_content_pwd);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.GONE);
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdtEmail.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mEdtPwd.getWindowToken(), 0);
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
		mRootView.findViewById(R.id.txt_frag_signin_content_forgot_pwd).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.pushFragment(new FragForgotPwd(), true);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_signin_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_signin_content_signin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_signing_in));
				final String strEmail = mEdtEmail.getText().toString();
				final String strPwd = mEdtPwd.getText().toString();
				hideKeyboard();
				
				new CommunicationAPIManager(MainActivity.instance).signInWithUserInfo(strEmail, strPwd, new NetAPICallBack() {
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
										}else if (responseObj.has(Define.USERINFO_ACTIVATIONSTATUS)){
											Global.GUser = new UserInformation(responseObj);
											MainActivity.config.saveLoggedIn(true);
											MainActivity.config.saveUserLoginInfo(new JSONObject(Global.GUser.asHashMap()));
											try {
												if (responseObj.getString(Define.USERINFO_ACTIVATIONSTATUS).equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
													MainActivity.config.saveUserInformation(responseObj);
													MainActivity.instance.pushFragment(new FragEvents(), false);
												}else if(responseObj.getString(Define.USERINFO_ACTIVATIONSTATUS).equals(Define.EVENTGUESTSTATUS_PENDING)){
													FragVerifyNumber fragVerifyNumber = new FragVerifyNumber();
													if (responseObj.has(Define.USERINFO_ID)) fragVerifyNumber.setUserID(responseObj.getString(Define.USERINFO_ID));
													if (responseObj.has(Define.USERINFO_MOBILE)) fragVerifyNumber.setPhoneNumber(responseObj.getString(Define.USERINFO_MOBILE));
													MainActivity.instance.pushFragment(fragVerifyNumber, true);
												}
											} catch (JSONException e) {
												displayOKAlertDialog(R.string.alert_dlg_signin_error);
												e.printStackTrace();
											}catch(NullPointerException e1){
												displayOKAlertDialog(R.string.alert_dlg_signin_error);
												e1.printStackTrace();
											}
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_signin_error);
									}
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
									displayOKAlertDialog(R.string.alert_dlg_signin_error);
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
									displayOKAlertDialog(R.string.alert_dlg_signin_error);
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
