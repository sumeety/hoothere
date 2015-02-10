package com.sumeet.hoothere.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereNotification;
import com.sumeet.util.AlertDialogManager;

public class FragNotifications extends Fragment{
	
	private View mRootView;
	private ListView mLvNotifications;
	private NotificationAdapter mAdapterNotifications;
	private ArrayList<HoothereNotification> mArrNotifications;
	private boolean mFlagLoadMore;
	private int mPageCnt;
	private boolean mFlagFirst = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_notifications, container, false);
			MainActivity.instance.handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_NOTIFICATIONS);
			MainActivity.instance.hideBadge();
			initUI();
			eventHandler();
			initData();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	private void initUI(){
		mLvNotifications = (ListView)mRootView.findViewById(R.id.lv_frag_notification_content);
		mAdapterNotifications = new NotificationAdapter();
		mLvNotifications.setAdapter(mAdapterNotifications);
		mAdapterNotifications.notifyDataSetChanged();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		markAllNotificationsAsRead();
		mFlagFirst = false;
	}
	
	private void markAllNotificationsAsRead(){
		if (mArrNotifications == null) return;
		for (int i = 0; i < mArrNotifications.size(); i++){
			if (mArrNotifications.get(i).isRead == false){
				markSingleNotificationAsRead(mArrNotifications.get(i).id);
			}
		}
	}
	
	private void markSingleNotificationAsRead(long id){
		new CommunicationAPIManager(MainActivity.instance).sendNotificationAsReadRequest(Global.GUser == null ? 0 : Global.GUser.userid, id, new NetAPICallBack(){
			@Override
			public void succeed(JSONObject responseObj) {
			}

			@Override
			public void failed(JSONObject errorObj) {
			}

			@Override
			public void failed(VolleyError error) {
				
			}
		});
	}
	
	private void initData(){
		if (!mFlagFirst) return;
		mPageCnt = 0;
		if (mArrNotifications != null) mArrNotifications.clear();
		mArrNotifications = new ArrayList<HoothereNotification>();
		fetchListOfNotifications(String.format("%d", mPageCnt), true);
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
	
	private void fetchListOfNotifications(String strOffset, final boolean bFlagShow){
		ProgressDialog progressDlgTemp = null;
		if (bFlagShow) progressDlgTemp = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading));
		final ProgressDialog progressDlg = progressDlgTemp;
		new CommunicationAPIManager(MainActivity.instance).getListOfNotifications(Global.GUser == null ? 0 : Global.GUser.userid, strOffset, true, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (mArrNotifications == null) mArrNotifications = new ArrayList<HoothereNotification>();
							
							for (int j = 0; j < mArrNotifications.size(); j++){
								if (mArrNotifications.get(j).isLoadMore == true){
									mArrNotifications.remove(j);
									j--;
								}
							}
							if (responseObj != null && responseObj.has(Define.HOO_THERE_NOTIFICATION_ARRAY_TAG)){
								try {
									JSONArray array = responseObj.getJSONArray(Define.HOO_THERE_NOTIFICATION_ARRAY_TAG);
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											HoothereNotification notification = new HoothereNotification(array.getJSONObject(i));
											mArrNotifications.add(notification);
										}
										mFlagLoadMore = array.length() == 20;
									}
									if (mFlagLoadMore) mArrNotifications.add(new HoothereNotification());
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							if (progressDlg != null) progressDlg.dismiss();
							if (mAdapterNotifications == null){
								mAdapterNotifications = new NotificationAdapter();
								mLvNotifications.setAdapter(mAdapterNotifications);
							}
							mAdapterNotifications.notifyDataSetChanged();
						}catch(Exception e){
							if (progressDlg != null) progressDlg.dismiss();
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
							displayOKAlertDialog(R.string.alert_dlg_getting_notifications_error);
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
							displayOKAlertDialog(R.string.alert_dlg_getting_notifications_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_notification_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mLvNotifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try{
					HoothereNotification notification = (HoothereNotification)mAdapterNotifications.getItem(position);
					if (notification == null) return;
					if (notification.isLoadMore == true) return;
					if (notification.type != null && (notification.type.equals(Define.NOTIFICATIONTYPE_FRA) || 
							notification.type.equals(Define.NOTIFICATIONTYPE_FRF) || 
							notification.type.equals(Define.NOTIFICATIONTYPE_EHT) || 
							notification.type.equals(Define.NOTIFICATIONTYPE_HFR))){
						if (notification.sender != null && Global.GUser != null && Global.GUser.userid == notification.sender.userid){
							FragMyProfile fragMyProfile = new FragMyProfile();
							MainActivity.instance.pushFragment(fragMyProfile, true);
						}else{
							FragOtherProfile fragOtherProfile = new FragOtherProfile();
							fragOtherProfile.setData(notification.sender);
							MainActivity.instance.pushFragment(fragOtherProfile, true);
						}
					}else{
						int nEventId = notification.eventId;
						if (Global.GArrHooThereEvents == null) return;
						for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
							if (nEventId == Global.GArrHooThereEvents.get(i).id){
								FragEventDetail fragEventDetail = new FragEventDetail();
								fragEventDetail.setEvent(Global.GArrHooThereEvents.get(i));
								MainActivity.instance.pushFragment(fragEventDetail, true);
								return;
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	@SuppressLint("InflateParams")
	private class NotificationAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (mArrNotifications == null) return 0;
			return mArrNotifications.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrNotifications == null || position >= mArrNotifications.size()) return null;
			return mArrNotifications.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			Object item = getItem(position);
			if (item == null) return null;
			final HoothereNotification notification = (HoothereNotification)item;
			if (notification.isLoadMore == true){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.notification_rowitem_loadmore, null);
				mPageCnt++;
				fetchListOfNotifications(String.format("%d", mPageCnt), false);
			}else{				
				String strType = notification.type;
				if (strType.equals(Define.NOTIFICATIONTYPE_FRA)){//FRA
					rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.notification_rowitem_accepted, null);
					AQuery aq = new AQuery((ImageView)rootView.findViewById(R.id.iv_notification_rowitem_accepted_avatar));
					ImageOptions imageOption = new ImageOptions();			
					imageOption.animation = AQuery.FADE_IN_FILE;
					imageOption.memCache = true;
					imageOption.fileCache = false;
					imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
					imageOption.fallback = R.drawable.defaultpic;
					String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, notification.sender == null ? 0 : notification.sender.userid);
					aq.image(strAvatarUrl, imageOption);
					TextView txtMessage = (TextView)rootView.findViewById(R.id.txt_notification_rowitem_accepted_content);
					txtMessage.setText(notification.message);
					if (notification.isRead == false) txtMessage.setTypeface(txtMessage.getTypeface(), Typeface.BOLD);
					rootView.findViewById(R.id.iv_notification_rowitem_accepted_accept).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							acceptFriendRequest(notification.sender != null ? notification.sender.userid : -1);
						}
					});
					
					rootView.findViewById(R.id.iv_notification_rowitem_accepted_decline).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							rejectFriendRequest(notification.sender != null ? notification.sender.userid : -1);
						}
					});

					rootView.findViewById(R.id.iv_notification_rowitem_accepted_avatar).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							navigateProfilePage(notification);
						}
					});
				}else if (strType.equals(Define.NOTIFICATIONTYPE_EI)){
					rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.notification_rowitem_invited, null);
					TextView txtMessage = (TextView)rootView.findViewById(R.id.txt_notification_rowitem_invited_content);
					txtMessage.setText(notification.message);
					if (notification.isRead == false) txtMessage.setTypeface(txtMessage.getTypeface(), Typeface.BOLD);
					rootView.findViewById(R.id.iv_notification_rowitem_invited_avatar).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							acceptEventInvitation(notification);
						}
					});
					
				}else{
					rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.notification_rowitem_info, null);
					AQuery aq = new AQuery((ImageView)rootView.findViewById(R.id.iv_notification_rowitem_info_avatar));
					ImageOptions imageOption = new ImageOptions();			
					imageOption.animation = AQuery.FADE_IN_FILE;
					imageOption.memCache = true;
					imageOption.fileCache = false;
					imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
					imageOption.fallback = R.drawable.defaultpic;
					String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, notification.sender == null ? 0 : notification.sender.userid);
					aq.image(strAvatarUrl, imageOption);
					TextView txtMessage = (TextView)rootView.findViewById(R.id.txt_notification_rowitem_info_content);
					txtMessage.setText(notification.message);
					if (notification.isRead == false) txtMessage.setTypeface(txtMessage.getTypeface(), Typeface.BOLD);
					
					rootView.findViewById(R.id.iv_notification_rowitem_info_avatar).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							navigateProfilePage(notification);
						}
					});
				}
			}
			return rootView;
		}
	}
	
	private void navigateProfilePage(HoothereNotification notification){
		if (notification.sender != null && Global.GUser != null && notification.sender.userid == Global.GUser.userid){
			MainActivity.instance.pushFragment(new FragMyProfile(), true);
		}else{
			FragOtherProfile fragOtherProfile = new FragOtherProfile();
			fragOtherProfile.setData(notification.sender);
			MainActivity.instance.pushFragment(fragOtherProfile, true);
		}
	}
	
	private void acceptEventInvitation(final HoothereNotification notification){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_accepting_invitation));
		new CommunicationAPIManager(MainActivity.instance).sendRequestAcceptEvent(notification.eventId, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_accept_event_error);
								mArrNotifications.remove(notification);
								mAdapterNotifications.notifyDataSetChanged();
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									if (responseObj.optString(Define.ERRORMESSAGE_TAG) != null && responseObj.optString(Define.ERRORMESSAGE_TAG).equals(Define.JSONEXCEPTION_SUCCESS)){
										displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
									}else{
										displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
									}
								}else if (responseObj.has(Define.USERINFO_ID)){
									displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
									mArrNotifications.remove(notification);
									mAdapterNotifications.notifyDataSetChanged();
								}else{
									displayOKAlertDialog(R.string.alert_dlg_accept_event_error);
								}
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
								displayOKAlertDialog(R.string.alert_dlg_accept_event_error);
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
						if (progressDlg != null) progressDlg.dismiss();
					}
				});
			}
		});
	}
	
	private void acceptFriendRequest(long senderId){
		if (Global.GUser == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_accepting));
		new CommunicationAPIManager(MainActivity.instance).acceptFriendRequest(Global.GUser.userid, senderId, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_success);
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
								}else{
									displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_success);
								}
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
							if (errorObj != null){
								if (errorObj.has(Define.ERRORMESSAGE_TAG)){
									if (errorObj.optString(Define.ERRORMESSAGE_TAG, "").contains(Define.JSONEXCEPTION_SUCCESS)){
										displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_success);
									}else{
										displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG));
									}
								}else{
									displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_error);
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_error);
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
							displayOKAlertDialog(R.string.alert_dlg_accept_friend_request_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void rejectFriendRequest(long senderId){
		if (Global.GUser == null) return;
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_declining));
		new CommunicationAPIManager(MainActivity.instance).rejectFriendRequest(Global.GUser.userid, senderId, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_reject_friend_request_success);
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
								}else{
									displayOKAlertDialog(R.string.alert_dlg_reject_friend_request_success);
								}
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
							if (errorObj != null){
								if (errorObj.has(Define.ERRORMESSAGE_TAG)){
									displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG));
								}else{
									displayOKAlertDialog(R.string.alert_dlg_reject_friend_request_error);
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_reject_friend_request_error);
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
							displayOKAlertDialog(R.string.alert_dlg_reject_friend_request_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}