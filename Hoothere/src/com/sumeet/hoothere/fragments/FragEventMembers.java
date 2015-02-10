package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.HootThereFriendComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventGuest;
import com.sumeet.model.EventMemberFriend;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.HoothereStatistics;
import com.sumeet.util.AlertDialogManager;

@SuppressLint({ "DefaultLocale", "ViewHolder", "InflateParams" })
public class FragEventMembers extends Fragment{
	
	private View mRootView;
	private ListView mLvEventMembers;
	private EventMemberListAdapter mAdapterEventMembers;
	private int mMemberType;
	private long mEventId;
	private HoothereStatistics mEventStatistics;
	private ArrayList<EventGuest> mArrEventGuests;
	private ArrayList<Object> mArrEventMembers;
	private ArrayList<EventGuest> mArrEventNewConnections;
	private String mStrType;
	
	private int mTabIndex;
	
	private static final int TAB_FRIENDS = 0;
	private static final int TAB_NEW_CONNECTIONS = 1;
	
	private Button mTabFriends;
	private Button mTabNewConnections;
	
	private boolean mFlagUpdateContent = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_all_event_members, container, false);
			initUI();
			eventHandler();
			initData(true);
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mFlagUpdateContent = false;
	}
	
	private void setTitle(){
		String strCount = "";
		switch(mMemberType){
		case Define.MEMBER_TYPE_GOING_THERE:
			strCount = mEventStatistics == null ? "0 " : String.format("%d ", mEventStatistics.acceptedCount);
			((TextView)mRootView.findViewById(R.id.txt_frag_all_event_members_header_title)).setText(strCount + getString(R.string.going_title));
			break;
		case Define.MEMBER_TYPE_HOO_THERE:
			strCount = mEventStatistics == null ? "0 " : String.format("%d ", mEventStatistics.hoothereCount);
			((TextView)mRootView.findViewById(R.id.txt_frag_all_event_members_header_title)).setText(strCount + getString(R.string.hoo_title));
			break;
		case Define.MEMBER_TYPE_INVITED:
			strCount = mEventStatistics == null ? "0 " : String.format("%d ", mEventStatistics.invitedCount);
			((TextView)mRootView.findViewById(R.id.txt_frag_all_event_members_header_title)).setText(strCount + getString(R.string.invited_title));
			break;
		case Define.MEMBER_TYPE_HOO_CAME:
			strCount = mEventStatistics == null ? "0 " : String.format("%d ", mEventStatistics.hooCameCount);
			((TextView)mRootView.findViewById(R.id.txt_frag_all_event_members_header_title)).setText(strCount + getString(R.string.hoocame_title));
			break;
		}
	}
	
	private void initUI(){
		mTabIndex = TAB_FRIENDS;
		setTitle();
		mLvEventMembers = (ListView)mRootView.findViewById(R.id.lv_frag_all_event_members);
		mAdapterEventMembers = new EventMemberListAdapter();
		mLvEventMembers.setAdapter(mAdapterEventMembers);
		mAdapterEventMembers.notifyDataSetChanged();
		mTabFriends = (Button)mRootView.findViewById(R.id.btn_frag_all_event_members_header_tab_friends);
		mTabNewConnections = (Button)mRootView.findViewById(R.id.btn_frag_all_event_members_header_tab_new_connections);
		handleTabClick(mTabIndex);
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void initData(boolean bFlagShowProgress){
		if (!mFlagUpdateContent) return;
		if (mEventStatistics == null) return;
		switch(mMemberType){
		case Define.MEMBER_TYPE_GOING_THERE:
			if (mEventStatistics.acceptedCount == 0) return;
			break;
		case Define.MEMBER_TYPE_HOO_CAME:
			if (mEventStatistics.hooCameCount == 0) return;
			break;
		case Define.MEMBER_TYPE_HOO_THERE:
			if (mEventStatistics.hoothereCount == 0) return;
			break;
		case Define.MEMBER_TYPE_INVITED:
			if (mEventStatistics.invitedCount == 0) return;
			break;
		}
		getHoothereFriends(bFlagShowProgress ? ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading)) : null);
	}
	
	public void setFlagDataUpdate(boolean bFlag){
		mFlagUpdateContent = bFlag;
	}
	
	private void getHoothereFriends(final ProgressDialog progressDlg){
		new CommunicationAPIManager(MainActivity.instance).fetchListOfHootThereFriends(Global.GUser == null ? 0 : Global.GUser.userid,  new NetAPICallBack() {

			@Override
			public void succeed(final JSONObject responseObj){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOOT_THERE_FRIEND_ARRAY_TAG)){
									ArrayList<HooThereFriend> arrHootThereFriends = new ArrayList<HooThereFriend>();
									try {
										JSONArray array = responseObj.getJSONArray(Define.HOOT_THERE_FRIEND_ARRAY_TAG);
										if (Global.GMapHoothereFriendsJSON == null){
											Global.GMapHoothereFriendsJSON = new HashMap<Object, JSONObject>();
										}
										if (array != null){
											for (int i = 0; i < array.length(); i++){
												HooThereFriend friend = new HooThereFriend(array.getJSONObject(i));
												Global.GMapHoothereFriendsJSON.put(friend, array.getJSONObject(i));
												arrHootThereFriends.add(friend);
											}
										}
										Collections.sort(arrHootThereFriends, new HootThereFriendComparator());
										if (Global.GArrHootThereFriends != null){
											Global.GArrHootThereFriends.clear();
										}
										Global.GArrHootThereFriends = new ArrayList<HooThereFriend>();
										Global.GArrHootThereFriends.addAll(arrHootThereFriends);
										getGuestList(progressDlg);
									} catch (JSONException e) {
										e.printStackTrace();
										if (progressDlg != null) progressDlg.dismiss();
									}
								}else{
									if (progressDlg != null) progressDlg.dismiss();
								}
							}
						}catch(Exception e){
							e.printStackTrace();
							if (progressDlg != null) progressDlg.dismiss();
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
							displayOKAlertDialog(R.string.alert_dlg_display_event_members_error);
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
							displayOKAlertDialog(R.string.alert_dlg_display_event_members_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void getNewConnections(){
		if (mArrEventMembers != null) mArrEventMembers.clear();
		mArrEventMembers = new ArrayList<Object>();
		if (mArrEventNewConnections != null) mArrEventNewConnections.clear();
		mArrEventNewConnections = new ArrayList<EventGuest>();
		if (Global.GArrHootThereFriends != null){
			for (int i = 0; i < mArrEventGuests.size(); i++){
				boolean bFlag = false;
				EventGuest guest = mArrEventGuests.get(i);
				for (int j = 0; j < Global.GArrHootThereFriends.size(); j++){
					HooThereFriend friend = Global.GArrHootThereFriends.get(j);
					if (guest.guestId == (friend.friend == null ? 0 : friend.friend.userid)){
						EventMemberFriend memberFriend = new EventMemberFriend(friend, guest);
						mArrEventMembers.add(memberFriend);
						bFlag = true;
						break;
					}
				}
				if (bFlag) continue;
				if (guest.guestId == Global.GUser.userid){
					mArrEventMembers.add(guest);
					continue;
				}else{
					mArrEventNewConnections.add(guest);
				}
			}
		}
	}

	private void getGuestList(final ProgressDialog progressDlg){
		mStrType = "";
		if (mMemberType == Define.MEMBER_TYPE_GOING_THERE){
			mStrType = Define.EVENT_GUEST_ACCEPTED;
		}else if (mMemberType == Define.MEMBER_TYPE_HOO_THERE){
			mStrType = Define.EVENT_GUEST_HOO;
		}else if (mMemberType == Define.MEMBER_TYPE_INVITED){
			mStrType = Define.EVENT_GUEST_INVITED;
		}else if(mMemberType == Define.MEMBER_TYPE_HOO_CAME){
			mStrType = Define.EVENT_GUEST_HOO_CAME;
		}
		
		new CommunicationAPIManager(MainActivity.instance).getGuestList(mEventId, "0", "1000", mStrType, new NetAPICallBack() {

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (mArrEventGuests != null) mArrEventGuests.clear();
							mArrEventGuests = new ArrayList<EventGuest>();
							if (responseObj != null){
								if (responseObj.has(mStrType)){
									try {
										JSONArray array = responseObj.getJSONArray(mStrType);
										if (array != null){
											for (int i = 0; i < array.length(); i++){
												EventGuest guest = new EventGuest(array.getJSONObject(i));
												mArrEventGuests.add(guest);
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
							getNewConnections();
							if (mAdapterEventMembers == null){
								mAdapterEventMembers = new EventMemberListAdapter();
							}
							if (!(mLvEventMembers.getAdapter() instanceof EventMemberListAdapter)){
								mLvEventMembers.setAdapter(mAdapterEventMembers);
							}
							mAdapterEventMembers.notifyDataSetChanged();
							if (progressDlg != null) progressDlg.dismiss();
						}catch(Exception e){
							e.printStackTrace();
							if (progressDlg != null) progressDlg.dismiss();
						}
					}
				});
			}

			@Override
			public void failed(JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (progressDlg != null) progressDlg.dismiss();
					}
				});
			}

			@Override
			public void failed(VolleyError error) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (progressDlg != null) progressDlg.dismiss();
					}
				});
			}
		});
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_all_event_members_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mLvEventMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object item = mAdapterEventMembers.getItem(position);
				if (item instanceof EventMemberFriend){
					FragOtherProfile fragOtherProfile = new FragOtherProfile();
					fragOtherProfile.setType(mTabIndex == TAB_NEW_CONNECTIONS);
					fragOtherProfile.setData(item);
					fragOtherProfile.setCaller(FragEventMembers.this);
					MainActivity.instance.pushFragment(fragOtherProfile, true);
				}else{
					if (item instanceof EventGuest){
						EventGuest guest = (EventGuest)item;
						if (guest.user != null && Global.GUser != null && guest.user.userid == Global.GUser.userid){
							MainActivity.instance.pushFragment(new FragMyProfile(), true);
						}else{
							FragOtherProfile fragOtherProfile = new FragOtherProfile();
							fragOtherProfile.setType(mTabIndex == TAB_NEW_CONNECTIONS);
							fragOtherProfile.setData(item);
							fragOtherProfile.setCaller(FragEventMembers.this);
							MainActivity.instance.pushFragment(fragOtherProfile, true);
						}
					}
				}
			}
		});
		
		mTabFriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTabClick(TAB_FRIENDS);
			}
		});
		
		mTabNewConnections.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTabClick(TAB_NEW_CONNECTIONS);
			}
		});
	}
	
	private void handleTabClick(int nTabId){
		mTabIndex = nTabId;
		switch(nTabId){
		case TAB_FRIENDS:
			mTabNewConnections.setBackgroundResource(R.drawable.bg_tabbutton_right_purple);
			mTabNewConnections.setTextColor(getResources().getColor(R.color.color_white));
			mTabFriends.setBackgroundResource(R.drawable.bg_tabbutton_left_white);
			mTabFriends.setTextColor(getResources().getColor(R.color.color_purple));
			if (mAdapterEventMembers == null){
				mAdapterEventMembers = new EventMemberListAdapter();
				mLvEventMembers.setAdapter(mAdapterEventMembers);
			}
			mAdapterEventMembers.notifyDataSetChanged();
			break;
		case TAB_NEW_CONNECTIONS:
			mTabFriends.setBackgroundResource(R.drawable.bg_tabbutton_left_purple);
			mTabFriends.setTextColor(getResources().getColor(R.color.color_white));
			mTabNewConnections.setBackgroundResource(R.drawable.bg_tabbutton_right_white);
			mTabNewConnections.setTextColor(getResources().getColor(R.color.color_purple));
			if (mAdapterEventMembers == null){
				mAdapterEventMembers = new EventMemberListAdapter();
				mLvEventMembers.setAdapter(mAdapterEventMembers);
			}
			mAdapterEventMembers.notifyDataSetChanged();
			break;
		}
	}
	
	public void setMemberType(int nMemberType){
		mMemberType = nMemberType;
	}
	
	public void setEventId(long nId){
		mEventId = nId;
	}
	
	public void setStatistics(HoothereStatistics statistics){
		mEventStatistics = statistics;
	}
	
	private class EventMemberViewHolder{
		TextView txtName;
		ImageView ivAvatar;
		View rlStatus;
		View rlAdd;
		View rlType;
		TextView txtStatus;
		ImageView ivStatus;
		View ivAdd;
		TextView txtType;
	}
	
	private class EventMemberListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (mTabIndex == TAB_FRIENDS){
				if (mArrEventMembers != null) return mArrEventMembers.size();
			}else{
				if (mArrEventNewConnections != null) return mArrEventNewConnections.size(); 
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mTabIndex == TAB_FRIENDS){
	 			if (mArrEventMembers == null || position >= mArrEventMembers.size()) return null;
	 			return mArrEventMembers.get(position);
			}else{
	 			if (mArrEventNewConnections == null || position >= mArrEventNewConnections.size()) return null;
	 			return mArrEventNewConnections.get(position);
			}
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
			EventMemberViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.event_member_rowitem, null);
				viewHolder = new EventMemberViewHolder();
				viewHolder.txtName = (TextView)rootView.findViewById(R.id.txt_event_member_rowitem_name);
				viewHolder.txtStatus = (TextView)rootView.findViewById(R.id.txt_event_member_rowitem_status);
				viewHolder.txtType = (TextView)rootView.findViewById(R.id.txt_event_member_rowitem_type);
				viewHolder.ivAdd = rootView.findViewById(R.id.iv_event_member_rowitem_add);
				viewHolder.ivAvatar = (ImageView)rootView.findViewById(R.id.iv_event_member_rowitem_avatar);
				viewHolder.ivStatus = (ImageView)rootView.findViewById(R.id.iv_event_member_rowitem_status);
				viewHolder.rlAdd = rootView.findViewById(R.id.rl_event_member_rowitem_add);
				viewHolder.rlStatus = rootView.findViewById(R.id.rl_event_member_rowitem_status);
				viewHolder.rlType = rootView.findViewById(R.id.rl_event_member_rowitem_type);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (EventMemberViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			if (item instanceof EventGuest){
				final EventGuest guest = (EventGuest)item;
				if (guest.user != null && Global.GUser != null && guest.user.userid == Global.GUser.userid){
					viewHolder.rlStatus.setVisibility(View.GONE);
					viewHolder.rlAdd.setVisibility(View.GONE);
					viewHolder.rlType.setVisibility(View.VISIBLE);
					viewHolder.txtType.setText(R.string.you);
				}else{
					viewHolder.rlType.setVisibility(View.INVISIBLE);
					viewHolder.rlAdd.setVisibility(View.VISIBLE);
					String status = guest.statusOfGuest;
					if (status != null){
						if (status.equals(Define.RELATIONSHIP_FRIEND)){
							String availabilityStatus = guest.user != null ? guest.user.availabilityStatus : "";
							if (availabilityStatus == null || availabilityStatus.equals("null")){
								viewHolder.txtStatus.setText(getString(R.string.change_status_want));
								viewHolder.ivStatus.setImageResource(R.drawable.icon_status_want_plan);
							}else{
								viewHolder.txtStatus.setText(availabilityStatus);
								if (availabilityStatus.equals(getString(R.string.change_status_busy))){
									viewHolder.ivStatus.setImageResource(R.drawable.icon_status_busy);
								}else if(availabilityStatus.equals(getString(R.string.change_status_go_out))){
									viewHolder.ivStatus.setImageResource(R.drawable.icon_status_go_out);
								}
							}
						}
					}
					viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							addFriend(guest.guestId);
						}
					});
				}
				
				AQuery aq = new AQuery(viewHolder.ivAvatar);
				ImageOptions imageOption = new ImageOptions();			
				imageOption.animation = AQuery.FADE_IN_FILE;
				imageOption.memCache = true;
				imageOption.fileCache = false;
				imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
				imageOption.fallback = R.drawable.defaultpic;
				String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, guest.user != null  ? guest.user.userid : 0);
				aq.image(strAvatarUrl, imageOption);
				
				viewHolder.txtName.setText(guest.user != null && guest.user.fullName != null && !guest.user.fullName.equals("null") ? guest.user.fullName : "");
			}else if (item instanceof EventMemberFriend){
				viewHolder.rlType.setVisibility(View.INVISIBLE);
				viewHolder.rlAdd.setVisibility(View.GONE);
				EventMemberFriend friend = (EventMemberFriend) item;
				String status = friend.status;
				if (status != null){
					if (status.equals(Define.RELATIONSHIP_FRIEND)){
						String availabilityStatus = friend != null ? friend.availabilityStatus : "";
						if (availabilityStatus == null || availabilityStatus.equals("null")){
							viewHolder.txtStatus.setText(getString(R.string.change_status_want));
							viewHolder.ivStatus.setImageResource(R.drawable.icon_status_want_plan);
						}else{
							viewHolder.txtStatus.setText(availabilityStatus);
							if (availabilityStatus.equals(getString(R.string.change_status_busy))){
								viewHolder.ivStatus.setImageResource(R.drawable.icon_status_busy);
							}else if(availabilityStatus.equals(getString(R.string.change_status_go_out))){
								viewHolder.ivStatus.setImageResource(R.drawable.icon_status_go_out);
							}else if(availabilityStatus.equals(getString(R.string.change_status_want))){
								viewHolder.ivStatus.setImageResource(R.drawable.icon_status_want_plan);
							}
						}
					}else{
						viewHolder.rlType.setVisibility(View.VISIBLE);
						viewHolder.txtType.setText(getString(R.string.pending_text));
					}
				}
				
				AQuery aq = new AQuery(viewHolder.ivAvatar);
				ImageOptions imageOption = new ImageOptions();
				imageOption.animation = AQuery.FADE_IN_FILE;
				imageOption.memCache = true;
				imageOption.fileCache = false;
				imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
				imageOption.fallback = R.drawable.defaultpic;
				String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, friend.guestId);
				aq.image(strAvatarUrl, imageOption);
				
				viewHolder.txtName.setText(friend.fullName != null && !friend.fullName.equals("null") ? friend.fullName : "");
			}
			
			return rootView;
		}
	}
	
	private void addFriend(final long guestId){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_adding_friend));
		new CommunicationAPIManager(MainActivity.instance).sendAddFriendRequest(Global.GUser == null ? 0 : Global.GUser.userid, guestId, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj != null){
								if (responseObj.has(Define.USERINFO_ID)){
									displayOKAlertDialog(R.string.alert_dlg_adding_friend_success);
									mFlagUpdateContent = true;
									initData(false);
								}else{
									displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
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
							displayOKAlertDialog(R.string.alert_dlg_adding_friend_error);
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
		});
	}
}