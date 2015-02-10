package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.HootThereFriendComparator;
import com.sumeet.comparators.InvitingFriendComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.ContactMember;
import com.sumeet.model.FBFriend;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.HoothereInvitingFriend;
import com.sumeet.util.AlertDialogManager;
import com.sumeet.util.AnimUtils;

public class FragFriends extends Fragment{
	
	private View mRootView;
	private static final int TAB_BAR_FRIENDS_CNT = 4;
	private static final int TAB_BAR_FRIENDS_FRIENDS = 0;
	private static final int TAB_BAR_FRIENDS_FB = 1;
	private static final int TAB_BAR_FRIENDS_CONTACTS = 2;
	private static final int TAB_BAR_FRIENDS_SEARCH = 3;
	private static final int TAB_BAR_ANIMATION_DELAY = 350;
	private int mHorizontalProportion;
	private int mFriendType = TAB_BAR_FRIENDS_FRIENDS;
	private EditText mEdtSearch;
	private ListView mLvFriends;
	private FriendListAdapter mAdapterFriends;
	private InvitingFriendAdapter mAdapterInvitingFriends;
	private ContactListAdapter mAdapterContacts;
	private FBFriendAdapter mAdapterFBFriends;
	
	private ArrayList<HooThereFriend> mArrHootThereFriendsSearch;
	private ArrayList<HooThereFriend> mArrHootThereFriends;
	private ArrayList<ContactMember> mArrContactsSearch;
	private ArrayList<HoothereInvitingFriend> mArrInvitingFriends;
	private ArrayList<FBFriend> mArrFBFriends;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		
		try {
			DisplayMetrics dimensions = new DisplayMetrics();
			MainActivity.instance.getWindowManager().getDefaultDisplay().getMetrics(dimensions);
			mHorizontalProportion = dimensions.widthPixels / TAB_BAR_FRIENDS_CNT;
			mRootView = inflater.inflate(R.layout.fragment_friends, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initData();
		MainActivity.instance.handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_FRIENDS);
	}
	
	private void initData(){
		fetchListOfHootThereFriends(false, ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading)), null);
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
	
	private void initUI(){
		ViewGroup.LayoutParams lp = mRootView.findViewById(R.id.iv_frag_friends_tab_underbar).getLayoutParams();
		lp.width = mHorizontalProportion;
		mRootView.findViewById(R.id.iv_frag_friends_tab_underbar).setLayoutParams(lp);
		mRootView.findViewById(R.id.iv_frag_friends_tab_underbar).requestLayout();
		mEdtSearch = (EditText)mRootView.findViewById(R.id.edt_frag_friends_search);
		mLvFriends = (ListView)mRootView.findViewById(R.id.lv_frag_friends);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.rl_frag_friends_search_hint).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.GONE);
				mEdtSearch.setVisibility(View.VISIBLE);
				mEdtSearch.requestFocus();
				InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEdtSearch, InputMethodManager.SHOW_FORCED);
			}
		});
		
		mEdtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (mFriendType == TAB_BAR_FRIENDS_FRIENDS){
					searchHoothereFriends(mEdtSearch.getText().toString());
				}else if (mFriendType == TAB_BAR_FRIENDS_CONTACTS){
					searchContacts(mEdtSearch.getText().toString());
				}else if(mFriendType == TAB_BAR_FRIENDS_SEARCH){
					searchFriends(mEdtSearch.getText().toString());
				}else if(mFriendType == TAB_BAR_FRIENDS_FB){
					searchFBFriends(mEdtSearch.getText().toString());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0){
				
			}
		});
		
		mEdtSearch.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					hideKeyboard();
					mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);				
					mEdtSearch.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});
		
		mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);
				mEdtSearch.setVisibility(View.GONE);
				if (mFriendType == TAB_BAR_FRIENDS_FRIENDS){
					Object friend = mAdapterFriends.getItem(position);
					if (friend == null) return;
					FragOtherProfile fragOtherProfile = new FragOtherProfile();
					fragOtherProfile.setData(friend);
					MainActivity.instance.pushFragment(fragOtherProfile, true);
				}
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_friends_tab_facebook).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_FB);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_friends_tab_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_FRIENDS);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_friends_tab_contacts).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_CONTACTS);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_friends_tab_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_friends_search_hint_text).setVisibility(View.VISIBLE);
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_SEARCH);
			}
		});
	}
	
	private void handleFriendsTabMenuClick(int nIndex){
		AnimUtils.AnimationMoveTo(mRootView.findViewById(R.id.iv_frag_friends_tab_underbar), 300,  nIndex * mHorizontalProportion, 0);
		switch(nIndex){
		case TAB_BAR_FRIENDS_FRIENDS:
			mFriendType = TAB_BAR_FRIENDS_FRIENDS;
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							mEdtSearch.setText("");
							searchHoothereFriends(mEdtSearch.getText().toString());
						}
					});
				}
			}, TAB_BAR_ANIMATION_DELAY);
			break;
		case TAB_BAR_FRIENDS_CONTACTS:
			mFriendType = TAB_BAR_FRIENDS_CONTACTS;
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							mRootView.findViewById(R.id.ll_frag_friends_contents_no_friend).setVisibility(View.GONE);
							mEdtSearch.setText("");
							searchContacts("");
						}
					});
				}
			}, TAB_BAR_ANIMATION_DELAY);
			break;
		case TAB_BAR_FRIENDS_SEARCH:
			mFriendType = TAB_BAR_FRIENDS_SEARCH;
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							mRootView.findViewById(R.id.ll_frag_friends_contents_no_friend).setVisibility(View.GONE);
							mEdtSearch.setText("");
							if (mArrInvitingFriends != null) mArrInvitingFriends.clear();
							mArrInvitingFriends = new ArrayList<HoothereInvitingFriend>();
							mAdapterInvitingFriends = new InvitingFriendAdapter();
							mLvFriends.setAdapter(mAdapterInvitingFriends);
							mAdapterInvitingFriends.notifyDataSetChanged();
						}
					});
				}
			}, TAB_BAR_ANIMATION_DELAY);
			break;
		case TAB_BAR_FRIENDS_FB:
			mFriendType = TAB_BAR_FRIENDS_FB;
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							mRootView.findViewById(R.id.ll_frag_friends_contents_no_friend).setVisibility(View.GONE);
							mEdtSearch.setText("");
							searchFBFriends("");
						}
					});
				}
			}, TAB_BAR_ANIMATION_DELAY);
		}
		
	}
	
	private void searchHoothereFriends(String sIndex){
		if (sIndex == null) sIndex = "";
		sIndex = sIndex.toLowerCase();
		if (mArrHootThereFriendsSearch != null) mArrHootThereFriendsSearch.clear();
		mArrHootThereFriendsSearch = new ArrayList<HooThereFriend>();
		if (mArrHootThereFriends != null){
			for (HooThereFriend friend : mArrHootThereFriends){
				if (friend.friend != null && friend.friend.fullName != null && friend.friend.fullName.toLowerCase().contains(sIndex)){
					mArrHootThereFriendsSearch.add(friend);
				}
			}
		}
		
		if (mFriendType == TAB_BAR_FRIENDS_FRIENDS){
			if (mAdapterFriends == null) mAdapterFriends = new FriendListAdapter();
			mLvFriends.setAdapter(mAdapterFriends);
			mAdapterFriends.notifyDataSetChanged();
			if (mArrHootThereFriends == null || mArrHootThereFriends.size() == 0){
				mRootView.findViewById(R.id.ll_frag_friends_contents_no_friend).setVisibility(View.VISIBLE);
			}else{
				mRootView.findViewById(R.id.ll_frag_friends_contents_no_friend).setVisibility(View.GONE);
			}
		}
	}
	
	private void searchContacts(String sIndex){
		try{
			sIndex = sIndex == null ? "" : sIndex.toLowerCase();
			if (mArrContactsSearch != null) mArrContactsSearch.clear();
			mArrContactsSearch = new ArrayList<ContactMember>();
			if (Global.GArrContacts != null){
				for (int i = 0; i < Global.GArrContacts.size(); i++){
					ContactMember contact = Global.GArrContacts.get(i);
					if (contact.mName != null && contact.mName.toLowerCase().contains(sIndex)){
						mArrContactsSearch.add(contact);
					}
				}
			}
			
			if (mFriendType == TAB_BAR_FRIENDS_CONTACTS){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (mAdapterContacts == null){
							mAdapterContacts = new ContactListAdapter();
						}
						mLvFriends.setAdapter(mAdapterContacts);
						mAdapterContacts.notifyDataSetChanged();
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void searchFBFriends(String sIndex){
		try{
			sIndex = sIndex == null ? "" : sIndex.toLowerCase();
			if (mArrFBFriends != null) mArrFBFriends.clear();
			mArrFBFriends = new ArrayList<FBFriend>();
			if (Global.GArrFBFriends != null){
				for (FBFriend fbFriend : Global.GArrFBFriends){
					if (fbFriend.name != null && fbFriend.name.toLowerCase().contains(sIndex)){
						mArrFBFriends.add(fbFriend);
					}
				}
			}
			if (mFriendType == TAB_BAR_FRIENDS_FB){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (mAdapterFBFriends == null){
							mAdapterFBFriends = new FBFriendAdapter();
						}
						mLvFriends.setAdapter(mAdapterFBFriends);
						mAdapterFBFriends.notifyDataSetChanged();
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
	}
	
	private String getFriendStatus(long friendId){
		String retVal = Define.INVITING_FRIEND_STATUS_NONE;
		if (Global.GArrHootThereFriends == null) return retVal;
		
		for (HooThereFriend friend : Global.GArrHootThereFriends){
			if (friend != null && friend.friend.userid == friendId){
				if (friend.status != null && !friend.status.equals("null")){
					retVal = friend.status;
				}
				return retVal;
			}
		}
		return retVal;
	}
	
	private void hootFriend(final HooThereFriend friend){
		if (friend == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_hooting));
		new CommunicationAPIManager(MainActivity.instance).sendHootAFriendRequest(Global.GUser == null ? -1 : Global.GUser.userid, friend.friend == null ? -1 : friend.friend.userid, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(String.format(Define.FORMAT_HOOT, friend.friend == null ? "" : friend.friend.fullName));
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									String errorMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
									displayOKAlertDialog(errorMessage == null || errorMessage.isEmpty() || errorMessage.equals("null") 
											? getString(R.string.alert_dlg_hoot_friend_request_error) : errorMessage);
								}else{
									displayOKAlertDialog(getString(R.string.alert_dlg_hoot_friend_request_error));
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
								String errorMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
								displayOKAlertDialog(errorMessage == null || errorMessage.isEmpty() || errorMessage.equals("null") 
										? getString(R.string.alert_dlg_hoot_friend_request_error) : errorMessage);
							}else{
								displayOKAlertDialog(getString(R.string.alert_dlg_hoot_friend_request_error));
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
							if (error == null){
								displayOKAlertDialog(String.format(Define.FORMAT_HOOT, friend.friend == null ? "" : friend.friend.fullName));
							}else if (error.getMessage() != null && error.getMessage().contains(Define.JSONEXCEPTION_SUCCESS)){
								displayOKAlertDialog(String.format(Define.FORMAT_HOOT, friend.friend == null ? "" : friend.friend.fullName));
							}else if (error.getMessage() == null){
								displayOKAlertDialog(R.string.alert_dlg_hoot_friend_request_error);
							}else{
								displayOKAlertDialog(error.getMessage());
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});						
			}
		});
	}
	
	private void navigateInviteEventPage(HooThereFriend friend){
		hideKeyboard();
		FragUpcomingEvents fragUpcomingEvents = new FragUpcomingEvents();
		fragUpcomingEvents.setFriend(friend);
		fragUpcomingEvents.setType(Define.SIGNUP_TYPE_HOOTHERE);
		if (Global.GMapHoothereFriendsJSON != null){
			fragUpcomingEvents.setFriendJSON(Global.GMapHoothereFriendsJSON.get(friend));
		}
		MainActivity.instance.pushFragment(fragUpcomingEvents, true);
	}
	
	private class FriendItemViewHolder{
		ImageView ivPhoto;
		TextView txtName;
		View rlPending;
		View llDecline;
		View llLogo;
		View rlStatus;
		View rlAccept;
		View rlDecline;
		View rlCheck;
		View rlLogo;
	}
	
	private class FriendListAdapter extends BaseAdapter{
		@Override
		public int getCount(){
			if (mArrHootThereFriendsSearch == null) return 0;
			return mArrHootThereFriendsSearch.size();
		}
		
		@Override
		public Object getItem(int position){
			if (mArrHootThereFriendsSearch == null || position >= mArrHootThereFriendsSearch.size()) return null;
			return mArrHootThereFriendsSearch.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View rootView = convertView;
			FriendItemViewHolder viewHolder;
			final HooThereFriend friend = (HooThereFriend) getItem(position);
			if (friend == null) return null;
			
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.main_friends_rowitem_friend, null);
				viewHolder = new FriendItemViewHolder();
				viewHolder.ivPhoto = (ImageView)rootView.findViewById(R.id.iv_main_friends_rowitem_avatar);
				viewHolder.llDecline = rootView.findViewById(R.id.ll_main_friends_rowitem_action_check_decline);
				viewHolder.llLogo = rootView.findViewById(R.id.ll_main_friends_rowitem_action_accept_logo);
				viewHolder.rlAccept = rootView.findViewById(R.id.rl_main_friends_rowitem_action_accept);
				viewHolder.rlCheck = rootView.findViewById(R.id.rl_main_friends_rowitem_action_check);
				viewHolder.rlDecline = rootView.findViewById(R.id.rl_main_friends_rowitem_action_decline);
				viewHolder.rlLogo = rootView.findViewById(R.id.rl_main_friends_rowitem_action_logo);
				viewHolder.rlPending = rootView.findViewById(R.id.rl_main_friends_rowitem_action_pending);
				viewHolder.rlStatus = rootView.findViewById(R.id.rl_main_friends_rowitem_status);
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_main_friends_rowitem_name);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (FriendItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			AQuery aq = new AQuery(viewHolder.ivPhoto);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOption.fallback = R.drawable.defaultpic;
			String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, friend.friend != null ? friend.friend.userid : 0);
			aq.image(strAvatarUrl, imageOption);
			viewHolder.txtName.setText(friend.friend != null ? friend.friend.fullName : "");
			viewHolder.rlStatus.setVisibility(View.GONE);

			if (friend.status != null){
				if (friend.status.equals(Define.HOOT_THERE_FRIEND_STATUS_A)){
					viewHolder.rlPending.setVisibility(View.GONE);
					viewHolder.llDecline.setVisibility(View.VISIBLE);
					viewHolder.llLogo.setVisibility(View.GONE);
				}else if (friend.status.equals(Define.HOOT_THERE_FRIEND_STATUS_P)){
					viewHolder.rlPending.setVisibility(View.VISIBLE);
					viewHolder.llDecline.setVisibility(View.GONE);
					viewHolder.llLogo.setVisibility(View.GONE);
				}else{
					viewHolder.rlPending.setVisibility(View.GONE);
					viewHolder.llDecline.setVisibility(View.GONE);
					viewHolder.llLogo.setVisibility(View.VISIBLE);
				}
			}else{
				viewHolder.rlPending.setVisibility(View.GONE);
				viewHolder.llDecline.setVisibility(View.GONE);
				viewHolder.llLogo.setVisibility(View.VISIBLE);
			}
			
			viewHolder.rlPending.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			
			viewHolder.rlAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateInviteEventPage(friend);
				}
			});
			
			viewHolder.rlDecline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (friend == null || friend.friend == null) return;
					rejectFriendRequest(friend.friend.userid);
				}
			});
			
			viewHolder.rlCheck.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (friend == null || friend.friend == null) return;
					acceptFriendRequest(friend.friend.userid);
				}
			});
			
			viewHolder.rlLogo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hootFriend(friend);
				}
			});
			return rootView;
		}
	}
	
	private class FBFriendItemViewHolder{
		TextView txtName;
		ImageView ivImage;
	}
	
	private class FBFriendAdapter extends BaseAdapter{
		
		LayoutInflater mInflater = LayoutInflater.from(MainActivity.instance);
		
		@Override
		public int getCount(){
			if (mArrFBFriends == null) return 0;
			return mArrFBFriends.size();
		}
		
		@Override
		public Object getItem(int position){
			if (mArrFBFriends == null || position >= mArrFBFriends.size()) return null;
			return mArrFBFriends.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View rootView = convertView;
			
			FBFriend friend = (FBFriend)getItem(position);
			if (friend == null) return null;

			FBFriendItemViewHolder viewHolder = null;
			if (rootView == null){
				rootView = mInflater.inflate(R.layout.main_friends_rowitem_fbfriend, null);
				viewHolder = new FBFriendItemViewHolder();
				viewHolder.ivImage = (ImageView)rootView.findViewById(R.id.iv_main_fbfriends_rowitem_avatar);
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_main_fbfriends_rowitem_name);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (FBFriendItemViewHolder)rootView.getTag();
			}
			
			viewHolder.txtName.setText(friend.name);
			AQuery aq = new AQuery(viewHolder.ivImage);
			ImageOptions imageOptions = new ImageOptions();
			imageOptions.animation = AQuery.FADE_IN_FILE;
			imageOptions.memCache = true;
			imageOptions.fileCache = false;
			imageOptions.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOptions.fallback = R.drawable.defaultpic;
			String strURL = String.format(Define.FB_AVATAR_URL_LARGE, friend.id);
			aq.image(strURL, imageOptions);
			return rootView;
		}
	}
	
	private class ContactItemViewHolder{
		TextView txtName;
		TextView txtPhone;
		View ivInvite;
		ImageView ivAvatar;
	}
	
	private class ContactListAdapter extends BaseAdapter{
		
		@Override
		public int getCount(){
			if (mArrContactsSearch == null) return 0;
			return mArrContactsSearch.size();
		}
		
		@Override
		public Object getItem(int position){
			if (mArrContactsSearch == null || position >= mArrContactsSearch.size()) return null;
			return mArrContactsSearch.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View rootView = convertView;
			final ContactMember contact = (ContactMember)getItem(position);
			if (contact == null) return null;
			
			ContactItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.main_friends_rowitem_contact, null);
				viewHolder = new ContactItemViewHolder();
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_main_friends_rowitem_contact_name);
				viewHolder.txtPhone = (TextView)rootView.findViewById(R.id.txt_main_friends_rowitem_contact_number);
				viewHolder.ivAvatar = (ImageView)rootView.findViewById(R.id.iv_main_friends_rowitem_contact_avatar);
				viewHolder.ivInvite = rootView.findViewById(R.id.iv_main_friends_rowitem_contact_action_accept);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (ContactItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			viewHolder.txtName.setText(contact.mName);
			viewHolder.txtPhone.setText(contact.mNumber == null || contact.mNumber.size() <= contact.mMainNumberIndex ? "" : contact.mNumber.get(contact.mMainNumberIndex).mNumber);
			
			viewHolder.ivInvite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideKeyboard();
					FragUpcomingEvents fragUpcomingEvents = new FragUpcomingEvents();
					fragUpcomingEvents.setFriend(contact);
					fragUpcomingEvents.setType("contacts");
					MainActivity.instance.pushFragment(fragUpcomingEvents, true);
				}
			});
			
			AQuery aq = new AQuery(viewHolder.ivAvatar);
			if (contact.mBitmap != null){
				aq.image(contact.mBitmap);
			}else{
				aq.image(R.drawable.defaultpic);
			}
			
			return rootView;
		}
	}
	
	private class InvitingFriendViewHolder{
		TextView txtName;
		ImageView ivAvatar;
		View ivAdd;
		View ivDecline;
		View ivAccept;
		ImageView ivType;
	}
	
	private class InvitingFriendAdapter extends BaseAdapter{
		
		@Override
		public int getCount(){
			if (mArrInvitingFriends != null) return mArrInvitingFriends.size();
			return 0;
		}
		
		@Override
		public Object getItem(int position){
			if (mArrInvitingFriends == null || position >= mArrInvitingFriends.size()) return null;
			return mArrInvitingFriends.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View rootView = convertView;
			final HoothereInvitingFriend friend = (HoothereInvitingFriend) getItem(position);
			if (friend == null) return null;
			
			InvitingFriendViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.search_friend_rowitem, null);
				viewHolder = new InvitingFriendViewHolder();
				viewHolder.ivAdd = rootView.findViewById(R.id.iv_search_friend_rowitem_add);
				viewHolder.ivAccept = rootView.findViewById(R.id.iv_search_friend_rowitem_accept);
				viewHolder.ivAvatar = (ImageView)rootView.findViewById(R.id.iv_search_friend_rowitem_avatar);
				viewHolder.ivDecline = rootView.findViewById(R.id.iv_search_friend_rowitem_reject);
				viewHolder.ivType = (ImageView)rootView.findViewById(R.id.iv_search_friend_rowitem_type);
//				viewHolder.txtStatus = (TextView)rootView.findViewById(R.id.txt_search_friend_rowitem_status);
//				viewHolder.ivStatus = (ImageView)rootView.findViewById(R.id.iv_search_friend_rowitem_status);
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_search_friend_rowitem_name);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (InvitingFriendViewHolder)rootView.getTag();
			}

			if (viewHolder == null) return null;
			
			String strFirstName = friend.firstName == null || friend.firstName.equals("null") ? "" : friend.firstName;
			String strMiddleName = friend.middleName == null || friend.middleName.equals("null") ? "" : friend.middleName;
			String strLastName = friend.lastName == null || friend.lastName.equals("null") ? "" : friend.lastName;
			viewHolder.txtName.setText(String.format(Define.USERNAME_TITLE, strFirstName, strMiddleName, strLastName));
			
			AQuery aq = new AQuery(viewHolder.ivAvatar);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOption.fallback = R.drawable.defaultpic;
			String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, friend.userId);
			aq.image(strAvatarUrl, imageOption);
			
			String strStatus = getFriendStatus(friend.userId);
			
			if (strStatus.equals(Define.INVITING_FRIEND_STATUS_PENDING)){
				viewHolder.ivType.setImageResource(R.drawable.pending_blue);
				viewHolder.ivType.setVisibility(View.VISIBLE);
				viewHolder.ivAdd.setVisibility(View.GONE);
				viewHolder.ivDecline.setVisibility(View.GONE);
				viewHolder.ivAccept.setVisibility(View.GONE);
			}else if (strStatus.equals(Define.INVITING_FRIEND_STATUS_NONE)){
				viewHolder.ivType.setVisibility(View.GONE);
				viewHolder.ivAdd.setVisibility(View.VISIBLE);
				viewHolder.ivDecline.setVisibility(View.GONE);
				viewHolder.ivAccept.setVisibility(View.GONE);
			}else if (strStatus.equals(Define.INVITING_FRIEND_STATUS_ACCEPTED)){
				viewHolder.ivType.setVisibility(View.GONE);
				viewHolder.ivAccept.setVisibility(View.VISIBLE);
				viewHolder.ivAdd.setVisibility(View.GONE);
				viewHolder.ivDecline.setVisibility(View.VISIBLE);
			}else{
				viewHolder.ivType.setImageResource(R.drawable.friend);
				viewHolder.ivType.setVisibility(View.VISIBLE);
				viewHolder.ivAdd.setVisibility(View.GONE);
				viewHolder.ivDecline.setVisibility(View.GONE);
				viewHolder.ivAccept.setVisibility(View.GONE);
			}
			
			viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addFriend(friend.userId);
				}
			});
			
			viewHolder.ivDecline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					rejectFriendRequest(friend.userId);
				}
			});
			
			viewHolder.ivAccept.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					acceptFriendRequest(friend.userId);
				}
			});
			
			return rootView;
		}
	}
	
	private void addFriend(final long guestId){
		if (Global.GUser == null) return;
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_adding_friend));
		new CommunicationAPIManager(MainActivity.instance).sendAddFriendRequest(Global.GUser.userid, guestId, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						String strAlert = null;
						if (responseObj == null){
							strAlert = getString(R.string.alert_dlg_adding_friend_success);
						}else if (responseObj.has(Define.USERINFO_ACTIVATIONSTATUS)){
							strAlert = getString(R.string.alert_dlg_adding_friend_success);
						}else{
							strAlert = getString(R.string.alert_dlg_adding_friend_error);
						}
						fetchListOfHootThereFriends(false, progressDlg, strAlert);
					}
				});
			}
			
			@Override
			public void failed(VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						fetchListOfHootThereFriends(false, progressDlg, getString(R.string.alert_dlg_adding_friend_error));
					}
				});
			}
			
			@Override
			public void failed(JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						fetchListOfHootThereFriends(false, progressDlg, getString(R.string.alert_dlg_adding_friend_error));
					}
				});
			}
		});
	}
	
	private void fetchListOfHootThereFriends(final boolean bSearchFriends, final ProgressDialog progressDlg, final String strAlert){
		if (Global.GUser == null){
			if (progressDlg != null) progressDlg.dismiss();
			return;
		}
		new CommunicationAPIManager(MainActivity.instance).fetchListOfHootThereFriends(Global.GUser.userid, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (mArrHootThereFriends != null) mArrHootThereFriends.clear();
							mArrHootThereFriends = new ArrayList<HooThereFriend>();
							if (responseObj != null && responseObj.has(Define.HOOT_THERE_FRIEND_ARRAY_TAG)){
								try{
									JSONArray array = responseObj.getJSONArray(Define.HOOT_THERE_FRIEND_ARRAY_TAG);
									if (Global.GMapHoothereFriendsJSON == null){
										Global.GMapHoothereFriendsJSON = new HashMap<Object, JSONObject>();
									}
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											HooThereFriend friend = new HooThereFriend(array.getJSONObject(i));
											Global.GMapHoothereFriendsJSON.put(friend, array.getJSONObject(i));
											mArrHootThereFriends.add(friend);
										}
									}
									Collections.sort(mArrHootThereFriends, new HootThereFriendComparator());
									if (Global.GArrHootThereFriends != null){
										Global.GArrHootThereFriends.clear();
									}
									Global.GArrHootThereFriends = new ArrayList<HooThereFriend>();
									Global.GArrHootThereFriends.addAll(mArrHootThereFriends);
								}catch (JSONException e){
									e.printStackTrace();
								}
							}
							if (mLvFriends.getAdapter() == null){
								searchHoothereFriends(mEdtSearch.getText().toString());
								mAdapterFriends = new FriendListAdapter();
								mLvFriends.setAdapter(mAdapterFriends);
							}
							if (progressDlg != null) progressDlg.dismiss();
							if (bSearchFriends){
								searchFriends(mEdtSearch.getText().toString());
							}else{
								((BaseAdapter)mLvFriends.getAdapter()).notifyDataSetChanged();
							}
							if (strAlert != null){
								displayOKAlertDialog(strAlert);
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
							if (bSearchFriends) searchFriends(mEdtSearch.getText().toString());
							if (strAlert != null){
								displayOKAlertDialog(strAlert);
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
							if (bSearchFriends) searchFriends(mEdtSearch.getText().toString());
							if (strAlert != null){
								displayOKAlertDialog(strAlert);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void searchFriends(String sIndex){
		if (Global.GUser == null) return;

		if (sIndex == null || sIndex.isEmpty()) return;
		if (mFriendType != TAB_BAR_FRIENDS_SEARCH) return;
		
		if (mArrInvitingFriends != null) mArrInvitingFriends.clear();
		mArrInvitingFriends = new ArrayList<HoothereInvitingFriend>();
		if (mAdapterInvitingFriends == null){
			mAdapterInvitingFriends = new InvitingFriendAdapter();
		}
			
		new CommunicationAPIManager(MainActivity.instance).sendRequestSearchFriend(Global.GUser.userid, sIndex, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								JSONObject result = responseObj.optJSONObject(Define.SEARCH_FRIENDS_RESULT);
								if (result != null){
									JSONArray array = result.optJSONArray(Define.SEARCH_FRIENDS_NAME);
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											try{
												HoothereInvitingFriend searchFriend = new HoothereInvitingFriend(array.getJSONObject(i));
												if (!mArrInvitingFriends.contains(searchFriend)) mArrInvitingFriends.add(searchFriend);
											} catch (JSONException e){
												e.printStackTrace();
											}
										}
									}
								}
							}
							Collections.sort(mArrInvitingFriends, new InvitingFriendComparator());
							if (mLvFriends == null) return;
							mLvFriends.setAdapter(mAdapterInvitingFriends);
							mAdapterInvitingFriends.notifyDataSetChanged();
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
							if (mLvFriends == null) return;
							mLvFriends.setAdapter(mAdapterInvitingFriends);
							mAdapterInvitingFriends.notifyDataSetChanged();
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
							if (mLvFriends == null) return;
							mLvFriends.setAdapter(mAdapterInvitingFriends);
							mAdapterInvitingFriends.notifyDataSetChanged();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void acceptFriendRequest(long senderId){
		if (Global.GUser == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_accepting));
		new CommunicationAPIManager(MainActivity.instance).acceptFriendRequest(Global.GUser.userid, senderId, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							String strAlert = null;
							if (responseObj == null){
								strAlert = getString(R.string.alert_dlg_accept_friend_request_success); 
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									String errorMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
									if (errorMessage != null && errorMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
										strAlert = getString(R.string.alert_dlg_accept_friend_request_success); 
									}else{
										strAlert = errorMessage == null || errorMessage.equals("null") ? getString(R.string.alert_dlg_accept_friend_request_error) : errorMessage;
									}
								}else{
									strAlert = getString(R.string.alert_dlg_accept_friend_request_success); 
								}
							}
							fetchListOfHootThereFriends(true, progressDlg, strAlert);
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
							fetchListOfHootThereFriends(true, progressDlg, getString(R.string.alert_dlg_accept_friend_request_error));
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
							String strAlert = null;
							if (errorObj != null){
								if (errorObj.has(Define.ERRORMESSAGE_TAG)){
									String errorMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
									if (errorMessage != null && errorMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
										strAlert = getString(R.string.alert_dlg_accept_friend_request_success); 
									}else{
										strAlert = errorMessage == null || errorMessage.equals("null") ? getString(R.string.alert_dlg_accept_friend_request_error) : errorMessage;
									}
								}else{
									strAlert = getString(R.string.alert_dlg_accept_friend_request_error); 
								}
							}else{
								strAlert = getString(R.string.alert_dlg_accept_friend_request_success); 
							}
							fetchListOfHootThereFriends(true, progressDlg, strAlert);
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
		new CommunicationAPIManager(MainActivity.instance).rejectFriendRequest(Global.GUser.userid, senderId, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							String strAlert = null;
							if (responseObj == null){
								strAlert = getString(R.string.alert_dlg_reject_friend_request_success);
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									String errorMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
									if (errorMessage != null && errorMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
										strAlert = getString(R.string.alert_dlg_reject_friend_request_success);
									}else{
										strAlert = errorMessage == null || errorMessage.equals("null") ? getString(R.string.alert_dlg_reject_friend_request_error) : errorMessage;
									}
								}else{
									strAlert = getString(R.string.alert_dlg_reject_friend_request_success);
								}
							}
							fetchListOfHootThereFriends(true, progressDlg, strAlert);
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
							fetchListOfHootThereFriends(true, progressDlg, getString(R.string.alert_dlg_reject_friend_request_error));
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
							String strAlert = null;
							if (errorObj != null){
								String errorMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
								if (errorMessage != null && errorMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
									strAlert = getString(R.string.alert_dlg_reject_friend_request_success);
								}else{
									strAlert = errorMessage == null || errorMessage.equals("null") ? getString(R.string.alert_dlg_reject_friend_request_error) : errorMessage;
								}
							}else{
								strAlert = getString(R.string.alert_dlg_reject_friend_request_success);
							}
							fetchListOfHootThereFriends(true, progressDlg, strAlert);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}