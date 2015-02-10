package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.HootThereFriendComparator;
import com.sumeet.comparators.HoothereEventAscComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.ContactMember;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.HoothereStatistics;
import com.sumeet.model.InvitingEventMemberContact;
import com.sumeet.model.InvitingEventMemberFriend;
import com.sumeet.util.AlertDialogManager;
import com.sumeet.util.AnimUtils;

@SuppressLint({ "DefaultLocale", "ViewHolder", "InflateParams" })
public class FragInviteFriends extends Fragment{
	
	private View mRootView;
	private HoothereEvent mEvent;
	private ArrayList<HooThereFriend> mArrHootThereFriends;
	private ArrayList<HooThereFriend> mArrHootThereFriendsSearch;
	private ArrayList<ContactMember> mArrContactsSearch;
	private ArrayList<Object> mArrFBFriends;
	private ArrayList<Object> mArrSelectedFriends;
	
	private ListView mLvFriends;
	private FriendListAdapter mAdapterFriends;
	private ContactListAdapter mAdapterContacts;
	private FBFriendAdapter mAdapterFBFriends;
	private EditText mEdtSearch;
	private RelativeLayout mRlSearchHint;
	
	private DisplayMetrics mDimensions;
	private int mHorizontalProportion;
	
	private static final int TAB_BAR_FRIENDS_FRIENDS = 0;
	private static final int TAB_BAR_FRIENDS_FB = 1;
	private static final int TAB_BAR_FRIENDS_CONTACTS = 2;
	private static final int TAB_BAR_ANIMATION_DELAY = 350;
	private int mFriendType = TAB_BAR_FRIENDS_FRIENDS;
	private static final int TAB_BAR_FRIENDS_CNT = 3;
	private Object mCaller;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_invite_friends, container, false);
			mDimensions = new DisplayMetrics();
			MainActivity.instance.getWindowManager().getDefaultDisplay().getMetrics(mDimensions);
			mHorizontalProportion = mDimensions.widthPixels / TAB_BAR_FRIENDS_CNT;
			initUI();
			eventHandler();
			initData();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	public void setCaller(Object caller){
		mCaller = caller;
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void initUI(){
		mLvFriends = (ListView) mRootView.findViewById(R.id.lv_frag_invite_friends_friends);
		mEdtSearch = (EditText)mRootView.findViewById(R.id.edt_frag_invite_friends_friends_search);
		mRlSearchHint = (RelativeLayout)mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint);
		android.view.ViewGroup.LayoutParams lp = mRootView.findViewById(R.id.iv_frag_invite_friends_friends_tab_underbar).getLayoutParams();
		lp.width = mHorizontalProportion;
		mRootView.findViewById(R.id.iv_frag_invite_friends_friends_tab_underbar).setLayoutParams(lp);
		mRootView.findViewById(R.id.iv_frag_invite_friends_friends_tab_underbar).requestLayout();
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.rl_frag_invite_friends_friends_tab_facebook).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint_text).setVisibility(View.VISIBLE);				
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_FB);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_invite_friends_friends_tab_contacts).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint_text).setVisibility(View.VISIBLE);				
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_CONTACTS);
			}
		});		

		mRootView.findViewById(R.id.rl_frag_invite_friends_friends_tab_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint_text).setVisibility(View.VISIBLE);				
				mEdtSearch.setVisibility(View.GONE);
				handleFriendsTabMenuClick(TAB_BAR_FRIENDS_FRIENDS);
			}
		});		
		
		mRlSearchHint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint_text).setVisibility(View.GONE);				
				mEdtSearch.setVisibility(View.VISIBLE);
				mEdtSearch.requestFocus();
				InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEdtSearch, InputMethodManager.SHOW_FORCED);
			}
		});

		mRootView.findViewById(R.id.iv_frag_invite_friends_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_invite_friends_action_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_invite_friends_action_invite).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				((Button)mRootView.findViewById(R.id.btn_frag_invite_friends_action_invite)).setText(getString(R.string.invite_friends_inviting));
				inviteFriends();
			}
		});
		
		mEdtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (mFriendType == TAB_BAR_FRIENDS_FRIENDS){
					searchHoothereFriends(mEdtSearch.getText().toString());
				}else if (mFriendType == TAB_BAR_FRIENDS_CONTACTS){
					searchContacts(mEdtSearch.getText().toString());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		
		mEdtSearch.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					hideKeyboard();
					mRootView.findViewById(R.id.rl_frag_invite_friends_friends_search_hint_text).setVisibility(View.VISIBLE);				
					mEdtSearch.setVisibility(View.GONE);
					return true;
				}
				return false;
			}
		});
	}
	
	private void navigateToDetailPage(final HoothereEvent event, final ProgressDialog progressDlg, final boolean bFlagSuccess){
		new CommunicationAPIManager(MainActivity.instance).getUpComingEvents(Global.GUser == null ? 0 : Global.GUser.userid,  new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									ArrayList<HoothereEvent> arrHoothereEvents = new ArrayList<HoothereEvent>();
									if (Global.GArrHooThereEvents != null) Global.GArrHooThereEvents.clear();
									Global.GArrHooThereEvents = new ArrayList<HoothereEvent>();
									try {
										HoothereEvent event1 = event;
										JSONArray array = responseObj.getJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
										if (array != null){
											for (int i = 0; i < array.length(); i++){
												HoothereEvent event2 = new HoothereEvent(array.getJSONObject(i));
												if (event2.id == event1.id){
													event1 = event2;
												}
												arrHoothereEvents.add(event2);
											}
										}
										Collections.sort(arrHoothereEvents, new HoothereEventAscComparator());
										Global.GArrHooThereEvents.addAll(arrHoothereEvents);
										if (progressDlg != null) progressDlg.dismiss();
										if (bFlagSuccess){
											displayOKAlertDialog(R.string.alert_dlg_inviting_multi_success);
										}else{
											displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
										}
										if (mCaller != null && mCaller instanceof FragEventDetail){
											((FragEventDetail)mCaller).setEvent(event1);
											MainActivity.instance.onBackPressed();
										}else{
											FragEventDetail fragEventDetail = new FragEventDetail();
											fragEventDetail.setEvent(event1);
											fragEventDetail.setFlagReturnToHome(true);
											MainActivity.instance.pushFragment(fragEventDetail, true);
										}
									} catch (JSONException e) {
										e.printStackTrace();
										if (progressDlg != null) progressDlg.dismiss();
										if (bFlagSuccess){
											displayOKAlertDialog(R.string.alert_dlg_inviting_multi_success);
										}else{
											displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
										}
									}
								}else{
									if (progressDlg != null) progressDlg.dismiss();
									if (bFlagSuccess){
										displayOKAlertDialog(R.string.alert_dlg_inviting_multi_success);
									}else{
										displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
									}
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
							}
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
							if (bFlagSuccess){
								displayOKAlertDialog(R.string.alert_dlg_inviting_multi_success);
							}else{
								displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
							}
							if (mCaller != null && mCaller instanceof FragEventDetail){
								((FragEventDetail)mCaller).setEvent(mEvent);
								MainActivity.instance.onBackPressed();
							}else{
								FragEventDetail fragEventDetail = new FragEventDetail();
								fragEventDetail.setEvent(mEvent);
								fragEventDetail.setFlagReturnToHome(true);
								MainActivity.instance.pushFragment(fragEventDetail, true);
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
							if (bFlagSuccess){
								displayOKAlertDialog(R.string.alert_dlg_inviting_multi_success);
							}else{
								displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
							}
							if (mCaller != null && mCaller instanceof FragEventDetail){
								((FragEventDetail)mCaller).setEvent(mEvent);
								MainActivity.instance.onBackPressed();
							}else{
								FragEventDetail fragEventDetail = new FragEventDetail();
								fragEventDetail.setEvent(mEvent);
								fragEventDetail.setFlagReturnToHome(true);
								MainActivity.instance.pushFragment(fragEventDetail, true);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	public void inviteFriends(){
		if (Global.GUser == null) return;
		
		if (mArrSelectedFriends != null && mArrSelectedFriends.size() > 0){
			final JSONArray finalFriends = new JSONArray();
			final JSONArray finalContacts = new JSONArray();
			final JSONArray finalFBFriends = new JSONArray();
			
			for (int i = 0; i < mArrSelectedFriends.size(); i++){
				Object item = mArrSelectedFriends.get(i);
				if (item instanceof HooThereFriend){
					finalFriends.put(new InvitingEventMemberFriend((HooThereFriend)item).asJSON());
					continue;
				}
				if (item instanceof ContactMember){
					finalContacts.put(new InvitingEventMemberContact((ContactMember)item).asJSON());
					continue;
				}
			}
			
			final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_inviting));
			new CommunicationAPIManager(MainActivity.instance).sendInviteRequest(mEvent.id, Global.GUser.userid, finalFriends, finalContacts, finalFBFriends, new NetAPICallBack() {
				@Override
				public void succeed(final JSONObject responseObj) {
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							try{
								((Button)mRootView.findViewById(R.id.btn_frag_invite_friends_action_invite)).setText(getString(R.string.invite_friends_invite));
								if (responseObj == null){
									navigateToDetailPage(mEvent, progressDlg, false);
								}else{
									if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
									mEvent.statistics.invitedCount += finalFriends.length() + finalContacts.length() + finalFBFriends.length();
									navigateToDetailPage(mEvent, progressDlg, true);
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
								((Button)mRootView.findViewById(R.id.btn_frag_invite_friends_action_invite)).setText(getString(R.string.invite_friends_invite));
								displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
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
								((Button)mRootView.findViewById(R.id.btn_frag_invite_friends_action_invite)).setText(getString(R.string.invite_friends_invite));
								displayOKAlertDialog(R.string.alert_dlg_inviting_single_error);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			});
		}
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void initData(){
		getListOfHoothereFriends();
		searchContacts(((EditText)mRootView.findViewById(R.id.edt_frag_invite_friends_friends_search)).getText().toString());
	}
	
	private void getListOfHoothereFriends(){
		if (mArrSelectedFriends != null) mArrSelectedFriends.clear();
		mArrSelectedFriends = new ArrayList<Object>();
		if (mArrHootThereFriends != null) mArrHootThereFriends.clear();
		mArrHootThereFriends = new ArrayList<HooThereFriend>();
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading));
		new CommunicationAPIManager(MainActivity.instance).fetchListOfHootThereFriendsForEvent(Global.GUser == null ? 0 : Global.GUser.userid, mEvent.id, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (mArrHootThereFriends != null) mArrHootThereFriends.clear();
							mArrHootThereFriends = new ArrayList<HooThereFriend>();
							if (responseObj != null){
								try {
									JSONArray array = responseObj.getJSONArray(Define.HOOT_THERE_FRIEND_ARRAY_TAG);
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											HooThereFriend friend = new HooThereFriend(array.getJSONObject(i));
											if (friend.status == null) continue;
											if (!friend.status.equals(Define.RELATIONSHIP_FRIEND)) continue;
											mArrHootThereFriends.add(friend);
										}
										Collections.sort(mArrHootThereFriends, new HootThereFriendComparator());
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							searchHoothereFriends(((EditText)mRootView.findViewById(R.id.edt_frag_invite_friends_friends_search)).getText().toString());
						}catch(Exception e){
							e.printStackTrace();
						}
						if (progressDlg != null) progressDlg.dismiss();
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
							searchHoothereFriends(((EditText)mRootView.findViewById(R.id.edt_frag_invite_friends_friends_search)).getText().toString());
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
							searchHoothereFriends(((EditText)mRootView.findViewById(R.id.edt_frag_invite_friends_friends_search)).getText().toString());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void searchContacts(String sIndex){
		try{
			if (mArrContactsSearch != null) mArrContactsSearch.clear();
			mArrContactsSearch = new ArrayList<ContactMember>();
			if (Global.GArrContacts != null){
				for (int i = 0; i < Global.GArrContacts.size(); i++){
					ContactMember contact = Global.GArrContacts.get(i);
					if (contact.mName.toLowerCase().contains(sIndex.toLowerCase())){
						mArrContactsSearch.add(contact);
					}
				}
			}
			
			if (mFriendType == TAB_BAR_FRIENDS_CONTACTS){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (mAdapterContacts == null) mAdapterContacts = new ContactListAdapter();
						mLvFriends.setAdapter(mAdapterContacts);
						mAdapterContacts.notifyDataSetChanged();
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void handleFriendsTabMenuClick(int nIndex){
		AnimUtils.AnimationMoveTo(mRootView.findViewById(R.id.iv_frag_invite_friends_friends_tab_underbar), 300, nIndex * mHorizontalProportion, 0);
		switch (nIndex){
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
							mEdtSearch.setText("");
							searchContacts(mEdtSearch.getText().toString());
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
							mEdtSearch.setText("");
							if (mArrFBFriends != null) mArrFBFriends.clear();
							mArrFBFriends = new ArrayList<Object>();
							mAdapterFBFriends = new FBFriendAdapter();
							mLvFriends.setAdapter(mAdapterFBFriends);
							Toast.makeText(MainActivity.instance, "This feature is coming soon.", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}, TAB_BAR_ANIMATION_DELAY);
			break;
		}
	}
	
	private void searchHoothereFriends(String sIndex){
		try{
			sIndex = sIndex == null ? "" : sIndex.toLowerCase();
			if (mArrHootThereFriendsSearch != null) mArrHootThereFriendsSearch.clear();
			mArrHootThereFriendsSearch = new ArrayList<HooThereFriend>();
			if (mArrHootThereFriends != null){
				for (int i = 0; i < mArrHootThereFriends.size(); i++){
					if (mArrHootThereFriends.get(i).friend != null &&
						mArrHootThereFriends.get(i).friend.fullName != null &&
						mArrHootThereFriends.get(i).friend.fullName.toLowerCase().contains(sIndex)){
						mArrHootThereFriendsSearch.add(mArrHootThereFriends.get(i));
					}
				}
			}
			if (mFriendType == TAB_BAR_FRIENDS_FRIENDS){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (mAdapterFriends == null) mAdapterFriends = new FriendListAdapter();
						mLvFriends.setAdapter(mAdapterFriends);
						mAdapterFriends.notifyDataSetChanged();
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private class FBFriendAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if (mArrFBFriends == null) return 0;
			return mArrFBFriends.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrFBFriends == null || position >= mArrFBFriends.size()) return null;
			return mArrFBFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			if (rootView == null){
				
			}else{
				
			}
			return null;
		}
	}
	
	private class FriendItemViewHolder{
		ImageView ivPhoto;
		View rlStatus;
		TextView txtName;
		ImageView ivCheckMark;
		ImageView ivStatus;
	}
	
	private class FriendListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (mArrHootThereFriendsSearch == null) return 0;
			return mArrHootThereFriendsSearch.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrHootThereFriendsSearch == null || position >= mArrHootThereFriendsSearch.size()) return null;
			return mArrHootThereFriendsSearch.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			final HooThereFriend friend = (HooThereFriend)getItem(position);
			if (friend == null) return null;
			
			FriendItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.invite_friends_rowitem_friend, null);
				viewHolder = new FriendItemViewHolder();
				viewHolder.ivCheckMark = (ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_action_check);
				viewHolder.ivPhoto = (ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_avatar);
				viewHolder.ivStatus = (ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_friend_status);
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_invite_friends_rowitem_name);
				viewHolder.rlStatus = rootView.findViewById(R.id.rl_invite_friends_rowitem_status);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (FriendItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
//			((ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_status)).setImageResource(R.drawable.icon_status_available);
//			((TextView)rootView.findViewById(R.id.txt_invite_friends_rowitem_status)).setText(getString(R.string.change_status_available));
			AQuery aq = new AQuery(viewHolder.ivPhoto);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOption.fallback = R.drawable.defaultpic;
			String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, friend.friend != null ? friend.friend.userid : 0);
			aq.image(strAvatarUrl, imageOption);

			viewHolder.rlStatus.setVisibility(View.GONE);
			viewHolder.txtName.setText((friend.friend != null) ? friend.friend.fullName : "");
			final ImageView ivCheckMark = viewHolder.ivCheckMark;
			ivCheckMark.setImageResource(mArrSelectedFriends.contains(friend) ? R.drawable.icon_checkmark : R.drawable.icon_checkmark_uncheck);
			
			if (friend.eventGuestStatus != null && friend.eventGuestStatus.isEmpty()){
				ivCheckMark.setVisibility(View.VISIBLE);
				viewHolder.ivStatus.setVisibility(View.GONE);
			}else if (friend.eventGuestStatus.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
				ivCheckMark.setVisibility(View.GONE);
				viewHolder.ivStatus.setVisibility(View.VISIBLE);
				viewHolder.ivStatus.setImageResource(R.drawable.going_there);
			}else if (friend.eventGuestStatus.equals(Define.EVENTGUESTSTATUS_HT)){
				ivCheckMark.setVisibility(View.GONE);
				viewHolder.ivStatus.setVisibility(View.VISIBLE);
				viewHolder.ivStatus.setImageResource(R.drawable.hoothere_blue);
			}else{
				ivCheckMark.setVisibility(View.GONE);
				viewHolder.ivStatus.setVisibility(View.VISIBLE);
				viewHolder.ivStatus.setImageResource(R.drawable.invited_blue);
			}
//			if (friend.status.equals(Define.HOOT_THERE_FRIEND_STATUS_A)){
//				rootView.findViewById(R.id.rl_invite_friends_rowitem_action_pending).setVisibility(View.GONE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_check_decline).setVisibility(View.VISIBLE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_accept_logo).setVisibility(View.GONE);
//			}else if (friend.status.equals(Define.HOOT_THERE_FRIEND_STATUS_P)){
//				rootView.findViewById(R.id.rl_invite_friends_rowitem_action_pending).setVisibility(View.VISIBLE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_check_decline).setVisibility(View.GONE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_accept_logo).setVisibility(View.GONE);
//			}else{
//				rootView.findViewById(R.id.rl_invite_friends_rowitem_action_pending).setVisibility(View.GONE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_check_decline).setVisibility(View.GONE);
//				rootView.findViewById(R.id.ll_invite_friends_rowitem_action_accept_logo).setVisibility(View.VISIBLE);
//			}
			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ivCheckMark.getVisibility() == View.GONE) return;
					if (mArrSelectedFriends.contains(friend)){
						mArrSelectedFriends.remove(friend);
						ivCheckMark.setImageResource(R.drawable.icon_checkmark_uncheck);
					}else{
						mArrSelectedFriends.add(friend);
						ivCheckMark.setImageResource(R.drawable.icon_checkmark);
					}
				}
			});
			return rootView;
		}
	}
	
	private class ContactItemViewHolder{
		TextView txtName;
		TextView txtPhone;
		ImageView ivCheckMark;
		ImageView ivAvatar;
	}
	
	private class ContactListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (mArrContactsSearch == null) return 0;
			return mArrContactsSearch.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrContactsSearch == null || position >= mArrContactsSearch.size()) return null;
			return mArrContactsSearch.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			final ContactMember contact = (ContactMember) getItem(position);
			if (contact == null) return null;
			
			ContactItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.invite_friends_rowitem_contact, null);
				viewHolder = new ContactItemViewHolder();
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_invite_friends_rowitem_contact_name);
				viewHolder.txtPhone = (TextView)rootView.findViewById(R.id.txt_invite_friends_rowitem_contact_number);
				viewHolder.ivAvatar = (ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_contact_avatar);
				viewHolder.ivCheckMark = (ImageView)rootView.findViewById(R.id.iv_invite_friends_rowitem_contact_action_accept);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (ContactItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			final ImageView ivCheckMark = viewHolder.ivCheckMark;
			viewHolder.ivCheckMark.setImageResource(mArrSelectedFriends.contains(contact) ? R.drawable.icon_checkmark : R.drawable.icon_checkmark_uncheck);

			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mArrSelectedFriends.contains(contact)){
						mArrSelectedFriends.remove(contact);
						ivCheckMark.setImageResource(R.drawable.icon_checkmark_uncheck);
					}else{
						mArrSelectedFriends.add(contact);
						ivCheckMark.setImageResource(R.drawable.icon_checkmark);
					}
				}
			});
			
			viewHolder.txtName.setText(contact.mName);
			viewHolder.txtPhone.setText(contact.mNumber == null || contact.mMainNumberIndex >= contact.mNumber.size() ? "" : contact.mNumber.get(contact.mMainNumberIndex).mNumber);
			AQuery aq = new AQuery(viewHolder.ivAvatar);
			if (contact.mBitmap != null){
				aq.image(contact.mBitmap);
			}else{
				aq.image(R.drawable.defaultpic);
			}
			return rootView;
		}
	}
}