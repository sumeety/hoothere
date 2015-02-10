package com.sumeet.hoothere.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.facebook.model.GraphUser;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragFBSignUp extends Fragment{
	
	private View mRootView;
	private GraphUser mUser;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_signup_fb, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
		if (mUser == null) return;
		((TextView)mRootView.findViewById(R.id.txt_frag_signup_fb_name)).setText(mUser.getName());
		
		AQuery aq = new AQuery((ImageView)mRootView.findViewById(R.id.iv_frag_signup_fb_contents_avatar));
		ImageOptions imageOption = new ImageOptions();
		imageOption.animation = AQuery.FADE_IN;
		imageOption.memCache = true;
		imageOption.fileCache = false;
		imageOption.targetWidth = 100;
		imageOption.fallback = R.drawable.defaultpic;
		imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
		String strAvatarUrl = String.format(Define.FB_AVATAR_URL_NORMAL, mUser.getId());
		aq.image(strAvatarUrl, imageOption);
		
		((EditText)mRootView.findViewById(R.id.edt_frag_signup_fb_email)).setText((String)mUser.getProperty(Define.FB_GRAPHUSER_EMAIL_TAG));
		((TextView)mRootView.findViewById(R.id.txt_frag_signup_hint)).setText(R.string.signup_fb_message);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_signup_fb_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_signup_fb_proceed).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				hideKeyboard();
				sendSignupFBRequest();
			}
		});
	}
	
	public void setUser(GraphUser user){
		mUser = user;
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
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_fb_email)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_fb_mobile)).getWindowToken(), 0);
	}
	
	private void sendSignupFBRequest(){
		if (mUser == null) return;
		String strEmail = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_fb_email)).getText().toString();
		String strMobile = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_fb_mobile)).getText().toString();
		if (strEmail.isEmpty()){
			AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																					getString(R.string.alert_dlg_fb_signup_empty_email), 
																					getString(R.string.alert_dlg_btn_ok), 
																					null, 
																					null);
			return;
		}
		
		if (strMobile.isEmpty()){
			AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																					getString(R.string.alert_dlg_fb_signup_empty_mobile), 
																					getString(R.string.alert_dlg_btn_ok), 
																					null, 
																					null);
			return;
		}
		
		JSONObject params = new JSONObject();
		try{
			params.put(Define.USERINFO_EMAIL, strEmail);
			params.put(Define.USERINFO_MOBILE, strMobile);
			params.put(Define.USERINFO_SIGNUPTYPE, Define.SIGNUP_TYPE_FACEBOOK);
			params.put(Define.USERINFO_FACEBOOKID, mUser.getId() == null || mUser.getId().isEmpty() ? " " : mUser.getId());
			params.put(Define.USERINFO_FIRSTNAME, mUser.getFirstName() == null || mUser.getFirstName().isEmpty() ? " " : mUser.getFirstName());
			params.put(Define.USERINFO_LASTNAME, mUser.getLastName() == null || mUser.getLastName().isEmpty() ? " " : mUser.getLastName());
			params.put(Define.USERINFO_DATEOFBIRTH, mUser.getBirthday() == null || mUser.getBirthday().isEmpty() ? " " : mUser.getBirthday());
			params.put(Define.USERINFO_PLATFORM, "android");
			params.put(Define.USERINFO_TOVERIFY, false);
			params.put(Define.USERINFO_DEVICEID, Global.deviceToken);
			
			final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_registering));
			new CommunicationAPIManager(MainActivity.instance).sendFBSignUpRequest(params, new NetAPICallBack() {
				@Override
				public void succeed(final JSONObject responseObj) {
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							if (progressDlg != null) progressDlg.dismiss();
							
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_signup_error);
								return;
							}
							if (responseObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG, getString(R.string.alert_dlg_signup_error)));
								return;
							}
							
							if (responseObj.has(Define.USERINFO_ACTIVATIONSTATUS)){
								Global.GUser = new UserInformation(responseObj);
								MainActivity.config.saveLoggedIn(true);
								MainActivity.config.saveUserLoginInfo(new JSONObject(Global.GUser.asHashMap()));
//								try {
//									String strActivation = responseObj.getString(Define.USERINFO_ACTIVATIONSTATUS);
//									if (strActivation != null && strActivation.equals(Define.EVENTGUESTSTATUS_PENDING)){//P
//										FragVerifyNumber fragVerifyNumber = new FragVerifyNumber();
//										fragVerifyNumber.setPhoneNumber(((EditText)mRootView.findViewById(R.id.edt_frag_signup_mobile_number)).getText().toString());
//										MainActivity.instance.pushFragment(new FragVerifyNumber(), true);
//									}else if (strActivation != null && strActivation.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){//A
										MainActivity.instance.pushFragment(new FragEvents(), false);
//									}
//								} catch (JSONException e) {
//									displayOKAlertDialog(R.string.alert_dlg_signup_error);
//									e.printStackTrace();
//								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_signup_error);
								return;
							}
						}
					});
				}
				
				@Override
				public void failed(VolleyError error) {
					
				}
				
				@Override
				public void failed(final JSONObject errorObj) {
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							if (progressDlg != null) progressDlg.dismiss();
							if (errorObj == null){
								displayOKAlertDialog(R.string.alert_dlg_signup_error);
								return;
							}
							if (errorObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG, getString(R.string.alert_dlg_signup_error)));
								return;
							}else{
								displayOKAlertDialog(R.string.alert_dlg_signup_error);
							}
						}
					});
				}
			});
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
}