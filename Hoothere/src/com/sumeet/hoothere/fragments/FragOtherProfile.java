package com.sumeet.hoothere.fragments;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventGuest;
import com.sumeet.model.EventMemberFriend;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

@SuppressLint("DefaultLocale")
public class FragOtherProfile extends Fragment{
	
	private View mRootView;
	private Object mUser;
	private boolean mConnectionType;
	private Object mCaller;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_other_profile, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void displayData(UserInformation user){
		if (user == null) return;
		((EditText)mRootView.findViewById(R.id.edt_frag_other_profile_content_name)).setText(user.fullName == null || user.fullName.equals("null") ? "" : user.fullName);
		((EditText)mRootView.findViewById(R.id.edt_frag_other_profile_content_phone)).setText(user.mobile == null || user.mobile.equals("null") ? "" : user.mobile);
		((TextView)mRootView.findViewById(R.id.txt_frag_other_profile_content_email)).setText(user.email == null || user.email.equals("null") ? "" : user.email);
		((TextView)mRootView.findViewById(R.id.txt_frag_other_profile_content_status)).setText(user.availabilityStatus == null || user.availabilityStatus.equals("null") ? 
				getString(R.string.change_status_want) : user.availabilityStatus);
		if (user.availabilityStatus != null && !user.availabilityStatus.equals("null")){
			if (user.availabilityStatus.equals(getString(R.string.change_status_want))){
				((ImageView)mRootView.findViewById(R.id.iv_frag_other_profile_content_status)).setImageResource(R.drawable.icon_status_want_plan);
			}else if (user.availabilityStatus.equals(getString(R.string.change_status_go_out))){
				((ImageView)mRootView.findViewById(R.id.iv_frag_other_profile_content_status)).setImageResource(R.drawable.icon_status_go_out);
			}else{
				((ImageView)mRootView.findViewById(R.id.iv_frag_other_profile_content_status)).setImageResource(R.drawable.icon_status_busy);
			}
		}else{
			((ImageView)mRootView.findViewById(R.id.iv_frag_other_profile_content_status)).setImageResource(R.drawable.icon_status_want_plan);
		}
		
		AQuery aq = new AQuery((ImageView)mRootView.findViewById(R.id.iv_frag_other_profile_content_avatar));
		ImageOptions imageOption = new ImageOptions();			
		imageOption.animation = AQuery.FADE_IN_FILE;
		imageOption.memCache = true;
		imageOption.fileCache = false;
		imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic_large);
		imageOption.fallback = R.drawable.defaultpic_large;
		String strAvatarUrl = String.format(Define.USER_AVATAR_IMAGE_URL, WebConfig.BASE_URL, user.userid);
		aq.image(strAvatarUrl, imageOption);
	}
	
	public void setType(boolean bNewConnection){
		mConnectionType = bNewConnection;
	}
	
	public void setCaller(Object caller){
		mCaller = caller;
	}
	
	private void initUI(){
		if (mUser == null) return;
		if (mUser instanceof UserInformation){
			UserInformation user = (UserInformation)mUser;
			displayData(user);
			mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
			mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
		}else if (mUser instanceof EventGuest){
			UserInformation user = ((EventGuest)mUser).user;
			displayData(user);
			if (((EventGuest)mUser).statusOfGuest != null){
				if (((EventGuest)mUser).statusOfGuest.equals(Define.EVENTGUESTSTATUS_PENDING)){//P
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_pending_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
				}else if (((EventGuest)mUser).statusOfGuest.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else if (((EventGuest)mUser).statusOfGuest.equals(Define.RELATIONSHIP_FRIEND)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else{
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				}
			}else{
				((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
			}
		}else if (mUser instanceof EventMemberFriend){
			UserInformation user = ((EventMemberFriend)mUser).user;
			displayData(user);
			if (((EventMemberFriend)mUser).status != null){
				if (((EventMemberFriend)mUser).status.equals(Define.EVENTGUESTSTATUS_PENDING)){
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_pending_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
				}else if (((EventMemberFriend)mUser).status.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else if (((EventMemberFriend)mUser).status.equals(Define.RELATIONSHIP_FRIEND)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else{
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				}
			}else{
				((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
			}
		}else if (mUser instanceof HooThereFriend){
			UserInformation user = ((HooThereFriend)mUser).friend;
			displayData(user);
			if (((HooThereFriend)mUser).status != null){
				if (((HooThereFriend)mUser).status.equals(Define.EVENTGUESTSTATUS_PENDING)){
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_pending_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
					mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
				}else if (((HooThereFriend)mUser).status.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else if (((HooThereFriend)mUser).status.equals(Define.RELATIONSHIP_FRIEND)){
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.GONE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.VISIBLE);
				}else{
					((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				}
			}else{
				((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).setText(R.string.other_profile_action_send_request);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
			}
		}
		if (mConnectionType == true){
			mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setVisibility(View.VISIBLE);
			mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setVisibility(View.GONE);
			mRootView.findViewById(R.id.rl_frag_other_profile_content_phone).setVisibility(View.GONE);
			mRootView.findViewById(R.id.rl_frag_other_profile_content_email).setVisibility(View.GONE);
		}
	}
	
	public void setData(Object user){
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
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_other_profile_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_other_profile_content_action_remove).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Global.GUser == null) return;
				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_removing_friend));
				long friendId = 0;
				if (mUser instanceof UserInformation){
					friendId = ((UserInformation)mUser) == null ? 0 : ((UserInformation)mUser).userid;
				}else if (mUser instanceof EventGuest){
					friendId = ((EventGuest)mUser).user == null ? 0 : ((EventGuest)mUser).user.userid;
				}else if (mUser instanceof EventMemberFriend){
					friendId = ((EventMemberFriend)mUser).user == null ? 0 : ((EventMemberFriend)mUser).user.userid;
				}else if (mUser instanceof HooThereFriend){
					friendId = ((HooThereFriend)mUser).friend == null ? 0 : ((HooThereFriend)mUser).friend.userid;
				}
				new CommunicationAPIManager(MainActivity.instance).sendRemoveFriendRequest(Global.GUser.userid, friendId, new NetAPICallBack(){
					@Override
					public void succeed(JSONObject responseObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																											getString(R.string.alert_dlg_removing_friend_success), 
																											getString(R.string.alert_dlg_btn_ok), 
																											null, 
																											new AlertDlgCallback() {
																												@Override
																												public void onOK() {
																													if (mCaller != null && mCaller instanceof FragEventMembers){
																														((FragEventMembers)mCaller).setFlagDataUpdate(true);
																													}
																													MainActivity.instance.onBackPressed();
																												}
																												
																												@Override
																												public void onCancel() {
																													
																												}
																											});
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
									displayOKAlertDialog(R.string.alert_dlg_removing_friend_error);
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
											AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																													getString(R.string.alert_dlg_removing_friend_success), 
																													getString(R.string.alert_dlg_btn_ok), 
																													null, 
																													new AlertDlgCallback(){
																														@Override
																														public void onOK(){
																															if (mCaller != null && mCaller instanceof FragEventMembers){
																																((FragEventMembers)mCaller).setFlagDataUpdate(true);
																															}
																															MainActivity.instance.onBackPressed();
																														}
																														
																														@Override
																														public void onCancel(){
																															
																														}
																													});
										}else if (error.getMessage() == null){
											displayOKAlertDialog(R.string.alert_dlg_removing_friend_error);
										}else{
											displayOKAlertDialog(error.getMessage());
										}
									}else{
										displayOKAlertDialog(R.string.alert_dlg_removing_friend_error);
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
		
		mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!((Button)mRootView.findViewById(R.id.btn_frag_other_profile_content_action_sendrequest)).getText().equals(getString(R.string.other_profile_action_send_request))) return;
				
				final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_sending_request));
				long friendId = 0;
				if (mUser instanceof UserInformation){
					friendId = ((UserInformation)mUser) == null ? 0 : ((UserInformation)mUser).userid;
				}else if (mUser instanceof EventGuest){
					friendId = ((EventGuest)mUser).user == null ? 0 : ((EventGuest)mUser).user.userid;
				}else if (mUser instanceof EventMemberFriend){
					friendId = ((EventMemberFriend)mUser).user == null ? 0 : ((EventMemberFriend)mUser).user.userid;
				}else if (mUser instanceof HooThereFriend){
					friendId = ((HooThereFriend)mUser).friend == null ? 0 : ((HooThereFriend)mUser).friend.userid;
				}
				
				new CommunicationAPIManager(MainActivity.instance).sendAddFriendRequest(Global.GUser == null ? 0 : Global.GUser.userid, friendId, new NetAPICallBack(){
					@Override
					public void succeed(final JSONObject responseObj) {
						MainActivity.instance.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								try{
									if (progressDlg != null) progressDlg.dismiss();
									if (responseObj == null){
										AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																												getString(R.string.alert_dlg_adding_friend_success), 
																												getString(R.string.alert_dlg_btn_ok), 
																												null, 
																												new AlertDlgCallback() {
																													@Override
																													public void onOK() {
																														if (mCaller != null && mCaller instanceof FragEventMembers){
																															((FragEventMembers)mCaller).setFlagDataUpdate(true);
																														}
																														MainActivity.instance.onBackPressed();
																													}
																													
																													@Override
																													public void onCancel() {
																														
																													}
																												});
									}else{
										if (responseObj.has(Define.ERRORMESSAGE_TAG)){
											displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
										}else{
											displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
										}
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
									displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
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
									displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
								}catch (Exception e){
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