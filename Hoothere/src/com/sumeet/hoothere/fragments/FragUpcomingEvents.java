package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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
import com.sumeet.comparators.HoothereEventAscComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.InvitingEventMemberFriend;
import com.sumeet.model.UserInformation;
import com.sumeet.util.AlertDialogManager;

public class FragUpcomingEvents extends Fragment{
	
	private View mRootView;
	private ListView mLvInvitingEvents;
	private InviteEventListAdapter mAdapterInvitingEvents;
	private ArrayList<HoothereEvent> mArrHoothereEvents;
	private Object mFriend;
	private String mStrType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_upcoming_events, container, false);
			initUI();
			eventHandler();
			initData();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initData(){
		getUpComingEvents();
	}
	
	private void initUI(){
		mLvInvitingEvents = (ListView)mRootView.findViewById(R.id.lv_frag_upcoming_events_events);
		mAdapterInvitingEvents = new InviteEventListAdapter();
		mLvInvitingEvents.setAdapter(mAdapterInvitingEvents);
		mAdapterInvitingEvents.notifyDataSetChanged();
	}
	
	public void setFriend(Object friend){
		mFriend = friend;
	}
	
	public void setType(String  strType){
		mStrType = strType;
	}
	
	public void setFriendJSON(JSONObject friend){
	}
	
	private void getUpComingEvents(){
		if (Global.GUser == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_loading));
		new CommunicationAPIManager(MainActivity.instance).getUpComingEvents(Global.GUser.userid,  new NetAPICallBack() {

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									if (mArrHoothereEvents != null) mArrHoothereEvents.clear();
									mArrHoothereEvents = new ArrayList<HoothereEvent>();
									try {
										JSONArray array = responseObj.getJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
										for (int i = 0; i < array.length(); i++){
											HoothereEvent event = new HoothereEvent(array.getJSONObject(i));
											mArrHoothereEvents.add(event);
										}
										Collections.sort(mArrHoothereEvents, new HoothereEventAscComparator());
										if (mAdapterInvitingEvents == null){
											mAdapterInvitingEvents = new InviteEventListAdapter();
											mLvInvitingEvents.setAdapter(mAdapterInvitingEvents);
										}
										mAdapterInvitingEvents.notifyDataSetChanged();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						if (progressDlg != null) progressDlg.dismiss();
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
		mRootView.findViewById(R.id.iv_frag_upcoming_events_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mLvInvitingEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try{
					FragEventDetail fragEventDetail = new FragEventDetail();
					fragEventDetail.setEvent((HoothereEvent)mAdapterInvitingEvents.getItem(position));
					MainActivity.instance.pushFragment(fragEventDetail, true);
				}catch(ClassCastException e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private void navigateToDetailPage(final HoothereEvent event){
		if (Global.GUser == null) return;
		new CommunicationAPIManager(MainActivity.instance).getUpComingEvents(Global.GUser.userid,  new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									if (mArrHoothereEvents != null) mArrHoothereEvents.clear();
									mArrHoothereEvents = new ArrayList<HoothereEvent>();
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
												mArrHoothereEvents.add(event2);
											}
										}
										Collections.sort(mArrHoothereEvents, new HoothereEventAscComparator());
										Global.GArrHooThereEvents.addAll(mArrHoothereEvents);
										FragEventDetail fragEventDetail = new FragEventDetail();
										fragEventDetail.setEvent(event1);
										MainActivity.instance.pushFragment(fragEventDetail, true);
									} catch (JSONException e) {
										e.printStackTrace();
									}
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
						FragEventDetail fragEventDetail = new FragEventDetail();
						fragEventDetail.setEvent(event);
						MainActivity.instance.pushFragment(fragEventDetail, true);
					}
				});
			}

			@Override
			public void failed(VolleyError error){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						FragEventDetail fragEventDetail = new FragEventDetail();
						fragEventDetail.setEvent(event);
						MainActivity.instance.pushFragment(fragEventDetail, true);
					}
				});
			}
		});
	}
	
	private void inviteFriend(final HoothereEvent event){
		if (event == null || Global.GUser == null || mFriend == null) return;
		JSONObject param = getRequestParam();
		if (param == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_inviting));
		new CommunicationAPIManager(MainActivity.instance).sendInviteSingleFriend(event.id, Global.GUser.userid, mFriend, param, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																										getString(R.string.alert_dlg_inviting_single_success), 
																										getString(R.string.alert_dlg_btn_ok), 
																										null, 
																										null);
							}else{
								if (responseObj.has(Define.USERINFO_ID)){
									AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																											getString(R.string.alert_dlg_inviting_single_success), 
																											getString(R.string.alert_dlg_btn_ok), 
																											null, 
																											null);
								}else{
									AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																											getString(R.string.alert_dlg_inviting_single_error),
																											getString(R.string.alert_dlg_btn_ok),
																											null,
																											null);
								}
							}
							navigateToDetailPage(event);
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
							AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																									getString(R.string.alert_dlg_inviting_single_error),
																									getString(R.string.alert_dlg_btn_ok),
																									null,
																									null);
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
							AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																									getString(R.string.alert_dlg_inviting_single_error),
																									getString(R.string.alert_dlg_btn_ok),
																									null,
																									null);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void navigateEventMemberPage(HoothereEvent event, int nType){
		FragEventMembers fragEventMembers = new FragEventMembers();
		fragEventMembers.setMemberType(nType);
		fragEventMembers.setEventId(event.id);
		fragEventMembers.setStatistics(event.statistics);
		MainActivity.instance.pushFragment(fragEventMembers, true);
	}
	
	private void navigateEventLocationPage(HoothereEvent event){
		FragEventLocation fragEventLocation = new FragEventLocation();
		event.radius = event.radius == null || event.radius.isEmpty() || event.radius.equals("null") ? String.format("%d", Define.RANGE_MAX_VAL / 2) : event.radius;
		fragEventLocation.setEvent(event);
		fragEventLocation.setEditable(false);
		MainActivity.instance.pushFragment(fragEventLocation, true);
	}
	
	private void navigateProfilePage(UserInformation user){
		if (user != null && Global.GUser != null && user.userid == Global.GUser.userid){
			MainActivity.instance.pushFragment(new FragMyProfile(), true);
		}else{
			FragOtherProfile fragOtherProfile = new FragOtherProfile();
			fragOtherProfile.setData(user);
			MainActivity.instance.pushFragment(fragOtherProfile, true);
		}
	}
	
	private void navigateEventAlbumSlidePage(HoothereEvent event){
		if (event.eventAlbum != null && event.eventAlbum.size() > 0){
			FragEventAlbumSlide fragEventAlbumSlide = new FragEventAlbumSlide();
			fragEventAlbumSlide.setEvent(event);
			MainActivity.instance.pushFragment(fragEventAlbumSlide, true);
		}
	}
	
	private JSONObject getRequestParam(){
		if (mFriend == null) return null;
		JSONArray jsArray = new JSONArray();
		if (mFriend instanceof HooThereFriend){
			jsArray.put(new InvitingEventMemberFriend((HooThereFriend)mFriend).asJSON());
		}
		JSONObject params = new JSONObject();
		try{
			params.put(mStrType, jsArray);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return params;
	}
	
	private class InviteEventItemViewHolder{
		TextView txtHoster;
		TextView txtInvited;
		TextView txtGoing;
		TextView txtHoo;
		TextView txtLocation;
		TextView txtTitle;
		TextView txtDateTime;
		View txtInvite;
		View rlInvited;
		View rlGoing;
		View rlHoo;
		View ivLocation;
		ImageView ivEventImage;
	}
	
	@SuppressLint({ "ViewHolder", "InflateParams" })
	private class InviteEventListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if (mArrHoothereEvents != null) return mArrHoothereEvents.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mArrHoothereEvents == null || position >= mArrHoothereEvents.size()) return null;
			return mArrHoothereEvents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final HoothereEvent event = (HoothereEvent)getItem(position);
			if (event == null) return null;

			View rootView = convertView;
			InviteEventItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.inviting_events_rowitem, null);
				viewHolder = new InviteEventItemViewHolder();
				viewHolder.ivLocation = rootView.findViewById(R.id.iv_upcoming_events_rowitem_content_location);
				viewHolder.rlGoing = rootView.findViewById(R.id.rl_upcoming_events_rowitem_statistics_going_parent);
				viewHolder.rlHoo = rootView.findViewById(R.id.rl_upcoming_events_rowitem_statistics_hoo_parent);
				viewHolder.rlInvited = rootView.findViewById(R.id.rl_upcoming_events_rowitem_statistics_invited_parent);
				viewHolder.txtDateTime = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_dedailinfo_datetime);
				viewHolder.txtGoing = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_statistics_going);
				viewHolder.txtHoo = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_statistics_hoo);
				viewHolder.txtInvited = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_statistics_invited);
				viewHolder.txtLocation = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_content_location);
				viewHolder.txtHoster = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_dedailinfo_hoster);
				viewHolder.txtTitle = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_detailinfo_title);
				viewHolder.txtInvite = rootView.findViewById(R.id.txt_upcoming_events_rowitem_content_invite);
				viewHolder.ivEventImage = (ImageView)rootView.findViewById(R.id.iv_upcoming_events_rowitem_avatar);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (InviteEventItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			AQuery aq = new AQuery(viewHolder.ivEventImage);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defualt_event);
			imageOption.fallback = R.drawable.defualt_event;
			if (event.coverImage != null){
				aq.image(event.coverImage.thumbnailUrl, imageOption);
			}else if (event.eventAlbum != null && event.eventAlbum.size() >= 1){
				aq.image(event.eventAlbum.get(0).thumbnailUrl, imageOption);
			}else{
				aq.image(BitmapFactory.decodeResource(getResources(), R.drawable.defualt_event));
			}

			viewHolder.txtHoster.setText(event.user != null ? event.user.firstName : "");
			
			if (event.statistics != null){
				viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.invitedCount, getString(R.string.invited_title)));
				viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.acceptedCount, getString(R.string.going_title)));
				viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.hoothereCount, getString(R.string.hoo_title)));
			}else{
				viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.invited_title)));
				viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.going_title)));
				viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.hoo_title)));
			}
			
			viewHolder.txtLocation.setText((event.venueName == null || event.venueName.equals("null"))? "" : event.venueName);
			viewHolder.txtTitle.setText(event.name == null || event.name.equals("null") ? "" : event.name);
			String strDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, event.startDateTime), DateFormat.format(Define.DT_FORMAT_TIME, event.startDateTime));
			viewHolder.txtDateTime.setText(event.startDateTime == 0 ? getString(R.string.date_not_specified) : strDate);
			
			viewHolder.txtInvite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					inviteFriend(event);
				}
			});
			
			viewHolder.rlInvited.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(event, Define.MEMBER_TYPE_INVITED);
				}
			});
			
			viewHolder.rlGoing.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(event, Define.MEMBER_TYPE_GOING_THERE);
				}
			});
			
			viewHolder.rlHoo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(event, Define.MEMBER_TYPE_HOO_THERE);
				}
			});
			
			viewHolder.txtHoster.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateProfilePage(event.user);
				}
			});
			
			viewHolder.txtLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventLocationPage(event);
				}
			});
			
 			viewHolder.ivLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventLocationPage(event);
				}
			});
 			
 			viewHolder.ivEventImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventAlbumSlidePage(event);
				}
			});
			
			return rootView;
		}
	}
}
