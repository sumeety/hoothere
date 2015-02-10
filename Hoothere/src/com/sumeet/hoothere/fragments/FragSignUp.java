package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.FBFriendComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.FBFriend;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragSignUp extends Fragment{
	
	private View mRootView;
	public static Session session;
	HashMap<String, Object> param;
	// image getting
	private UiLifecycleHelper uiHelper;
	private GraphUser mFBUser;
	private boolean mFlagFBSignupable;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mFlagFBSignupable = false;
			mRootView = inflater.inflate(R.layout.fragment_signup, container, false);
			initFB();
			uiHelper = new UiLifecycleHelper(MainActivity.instance, statusCallback);
			uiHelper.onCreate(savedInstanceState);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initUI(){
	}
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.GONE);
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
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
	
	private void initFB(){
		LoginButton btnFB = ((LoginButton)mRootView.findViewById(R.id.fb_login_button));
		btnFB.setFragment(this);
		btnFB.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_signup_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_signup_termsof_service).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_signup_create_account).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strFirstName = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_name)).getText().toString();
				String strEMail = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_email)).getText().toString();
				String strPhone = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_mobile_number)).getText().toString();
				String strPassword = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_password)).getText().toString();
				String strConfirmPassword = ((EditText)mRootView.findViewById(R.id.edt_frag_signup_confirm_password)).getText().toString();
				
				if (strFirstName.isEmpty()){
					displayOKAlertDialog(R.string.enter_first_name);
					return;
				}
				
				if (strEMail.isEmpty()){
					displayOKAlertDialog(R.string.enter_valid_email);
					return;
				}
				
				if (strPhone.isEmpty()){
					displayOKAlertDialog(R.string.enter_valid_number);
					return;
				}
				
				if (strPassword.isEmpty() || strConfirmPassword.isEmpty()){
					displayOKAlertDialog(R.string.enter_valid_password);
					return;
				}
				
				if (!strPassword.equals(strConfirmPassword)){
					displayOKAlertDialog(R.string.password_mismatch);
					return;
				}
				hideKeyboard();
				sendSignupRequest(strFirstName, strEMail, strPassword, strPhone);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_signup_fb).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				navigateFBSignupPage();
//				Toast.makeText(MainActivity.instance, getString(R.string.facebook_coming_soon), Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void navigateFBSignupPage(){
		if (mFBUser == null){
			if (mFlagFBSignupable == false){
				AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
						getString(R.string.alert_dlg_fb_signin_prior), 
						getString(R.string.alert_dlg_btn_ok), 
						getString(R.string.alert_dlg_cancel), 
						new AlertDlgCallback() {
							@Override
							public void onOK() {
								((LoginButton)mRootView.findViewById(R.id.fb_login_button)).performClick();
							}
							
							@Override
							public void onCancel() {
								
							}
						});
				return;
			}
			AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
					getString(R.string.alert_dlg_fb_signin_prior), 
					getString(R.string.alert_dlg_btn_ok), 
					getString(R.string.alert_dlg_cancel), 
					new AlertDlgCallback() {
						@Override
						public void onOK() {
							((LoginButton)mRootView.findViewById(R.id.fb_login_button)).performClick();
						}
						
						@Override
						public void onCancel() {
							
						}
					});
			return;
		}
		FragFBSignUp fragFBSignUp = new FragFBSignUp();
		fragFBSignUp.setUser(mFBUser);
		MainActivity.instance.pushFragment(fragFBSignUp, true);
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_name)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_email)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_mobile_number)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_password)).getWindowToken(), 0);
		imm.hideSoftInputFromWindow(((EditText)mRootView.findViewById(R.id.edt_frag_signup_confirm_password)).getWindowToken(), 0);
	}
	
	private void sendSignupRequest(String firstName, String email, String password, String mobile){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_registering));
		new CommunicationAPIManager(MainActivity.instance).sendSignupRequest(firstName, email, password, mobile, Define.SIGNUP_TYPE_HOOTHERE, Global.deviceToken, new NetAPICallBack() {
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
										String strActivation = responseObj.getString(Define.USERINFO_ACTIVATIONSTATUS);
										if (strActivation != null && strActivation.equals(Define.EVENTGUESTSTATUS_PENDING)){//P
											FragVerifyNumber fragVerifyNumber = new FragVerifyNumber();
											fragVerifyNumber.setPhoneNumber(((EditText)mRootView.findViewById(R.id.edt_frag_signup_mobile_number)).getText().toString());
											MainActivity.instance.pushFragment(new FragVerifyNumber(), true);
										}else if (strActivation != null && strActivation.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){//A
											MainActivity.instance.pushFragment(new FragEvents(), false);
										}
									} catch (JSONException e) {
										displayOKAlertDialog(R.string.alert_dlg_signup_error);
										e.printStackTrace();
									}
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_signup_error);
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
							displayOKAlertDialog(R.string.alert_dlg_signup_error);
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
							displayOKAlertDialog(R.string.alert_dlg_signup_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
//	public void onClickFBRegister() {
//		if (Session.getActiveSession() != null) {
//			Session.getActiveSession().closeAndClearTokenInformation();
//		}
//
//		Session.OpenRequest openRequest = new Session.OpenRequest(this);
//		ArrayList<String> p = new ArrayList<String>();
//		p.add("email");
//		p.add("public_profile");
//		p.add("user_friends");
//		openRequest.setPermissions(p);
//		openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
//		session = new Session(getActivity());
//		Session.setActiveSession(session);
//		session.addCallback(new StatusCallback() {
//
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				if (exception != null)
//					Logger.writeLog(exception.getMessage());
//				if (session.isOpened()) {
//					Request req = Request.newMeRequest(session, new GraphUserCallback() {
//						@Override
//						public void onCompleted(GraphUser user, Response response) {
//							String img_value = null;
//							param = new HashMap<String, Object>();
//							String m_strFName, m_strLName;
//							if (user != null) {
//								if (user.getFirstName() != null)
//									m_strFName = user.getFirstName();
//								else
//									m_strFName = "";
//
//								if (user.getLastName() != null)
//									m_strLName = user.getLastName();
//								else
//									m_strLName = "";
//
//								filePath = "http://graph.facebook.com/" + user.getId() + "/picture?type=normal";
//							} else{
//								
//							}
//						}
//					});
//
//					Bundle b = req.getParameters();
//					b.putString("fields", "email,first_name,last_name");
//					req.setParameters(b);
//
//					req.executeAsync();
//
//				} else if (session.isClosed()) {
//					Logger.writeLog("new session is closed");
//				}
//			}
//		});
//		session.openForRead(openRequest);
//	}
	
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			mFlagFBSignupable = true;
			if (state.isOpened()) {
				Log.d("MainActivity", "Facebook session opened.");
				
				((LoginButton)mRootView.findViewById(R.id.fb_login_button)).setUserInfoChangedCallback(new UserInfoChangedCallback() {
					
					@Override
					public void onUserInfoFetched(GraphUser user) {
						
						if (user != null) {
							mFBUser = user;
							
							// to set parameter
//							String strUserName = user.getName();
//			                String strUserAvatar = String.format("https://graph.facebook.com/%s/picture?type=large", user.getId());
//			                String strEmail = (String)user.getProperty("email");
//			                String strUserId = user.getId();
						} else {
							MainActivity.instance.runOnUiThread(new Runnable(){
								@Override
								public void run(){
									AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
											getString(R.string.alert_dlg_fb_signup_error), 
											getString(R.string.alert_dlg_btn_ok), 
											null, 
											null);
								}
							});							
						}
					}
				});
				
				new Request(session, "/me/friends", null, HttpMethod.GET, new Request.Callback() {
				        public void onCompleted(Response response) {
				        	
				        	FacebookRequestError error = null;
				        	if (response != null){
				        		error = response.getError();
				        	}
				        	if (error != null){
				        		Log.d("MainActivity", error.toString());
				        	}else{
				        		GraphObject graphObject = response.getGraphObject();
				        		if (Global.GArrFBFriends != null){
				        			Global.GArrFBFriends.clear();
				        		}
				        		
				        		Global.GArrFBFriends = new ArrayList<FBFriend>();
				        		
				        		if (graphObject == null){
				        			
				        		}else{
				        			try{
				        				JSONArray dataArray = (JSONArray) graphObject.getProperty("data");
				        				if (dataArray != null){
				        					for (int i = 0; i < dataArray.length(); i++){
				        						try{
				        							FBFriend fbFriend = new FBFriend(dataArray.getJSONObject(i));
				        							Global.GArrFBFriends.add(fbFriend);
				        						}catch(JSONException e){
				        							e.printStackTrace();
				        						}
				        					}
				        					Collections.sort(Global.GArrFBFriends, new FBFriendComparator());
				        				}
				        				MainActivity.config.saveFBFriendsList(dataArray);
				        			}catch(ClassCastException e){
				        				e.printStackTrace();
				        			}
				        		}
				        	}
				        }
				    }
				).executeAsync();
			} else if (state.isClosed()) {
				Log.d("MainActivity", "Facebook session closed.");
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
}
