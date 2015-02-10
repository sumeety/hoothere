package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.sumeet.comparators.HoothereEventDescComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventAlbum;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragMyPastEvents extends Fragment{
	
	private View mRootView;
	private ListView mLvMyPastEvents;
	private AdapterMyPastEvents mAdapterMyPastEvents;
	private ArrayList<HoothereEvent> mArrMyPastEvents;
	private boolean mFlagFirstLoading = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_mypast_events, container, false);
			initUI();
			eventHandler();
			initData();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mFlagFirstLoading = false;
	}
	
	private void initUI(){
		mLvMyPastEvents = (ListView)mRootView.findViewById(R.id.lv_frag_mypast_events_events);
		mAdapterMyPastEvents = new AdapterMyPastEvents();
		mLvMyPastEvents.setAdapter(mAdapterMyPastEvents);
		mAdapterMyPastEvents.notifyDataSetChanged();
	}
	
	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_mypast_events_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mLvMyPastEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object item = mAdapterMyPastEvents.getItem(position);
				if (item == null || !(item instanceof HoothereEvent)) return;
				FragEventDetail fragEventDetail = new FragEventDetail();
				fragEventDetail.setEvent((HoothereEvent)item);
				fragEventDetail.setPast(true);
				fragEventDetail.setEditable(false);
				MainActivity.instance.pushFragment(fragEventDetail, true);
			}
		});
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
	
	private void initData(){
		if (!mFlagFirstLoading) return;
		getPastEvents();
	}
	
	private void getPastEvents(){
		if (Global.GUser == null) return;
		
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, "", getString(R.string.progress_loading));
		if (mArrMyPastEvents != null) mArrMyPastEvents.clear();
		mArrMyPastEvents = new ArrayList<HoothereEvent>();
		new CommunicationAPIManager(MainActivity.instance).getPastEvents(Global.GUser.userid, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									JSONArray array = responseObj.optJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											try {
												HoothereEvent event = new HoothereEvent(array.getJSONObject(i));
												mArrMyPastEvents.add(event);
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										Collections.sort(mArrMyPastEvents, new HoothereEventDescComparator());
									}
								}
								if (mLvMyPastEvents.getAdapter() == null){
									mAdapterMyPastEvents = new AdapterMyPastEvents();
									mLvMyPastEvents.setAdapter(mAdapterMyPastEvents);
								}
								mAdapterMyPastEvents.notifyDataSetChanged();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						if (progressDlg != null) progressDlg.dismiss();
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
								displayOKAlertDialog(error.getMessage() == null ? getString(R.string.alert_dlg_common_error) : error.getMessage());
							}else{
								displayOKAlertDialog(R.string.alert_dlg_common_error);
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
								displayOKAlertDialog(R.string.alert_dlg_common_error);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void navigateEventMemberPage(int nType, HoothereEvent event){
		if (event == null) return;
		FragEventMembers fragEventMembers = new FragEventMembers();
		fragEventMembers.setEventId(event.id);
		fragEventMembers.setStatistics(event.statistics);
		fragEventMembers.setMemberType(nType);
		MainActivity.instance.pushFragment(fragEventMembers, true);
	}
	
	private void navigateEventLocationPage(HoothereEvent event){
		FragEventLocation fragEventLocation = new FragEventLocation();
		event.radius = event.radius == null || event.radius.equals("null") || event.radius.isEmpty() ? String.format("%d", Define.RANGE_MAX_VAL / 2) : event.radius;
		fragEventLocation.setEditable(false);
		fragEventLocation.setEvent(event);
		MainActivity.instance.pushFragment(fragEventLocation, true);
	}
	
	private void navigateProfilePage(HoothereEvent event){
		if (event.user == null) return;
		if (Global.GUser != null && event.user.userid == Global.GUser.userid){
			MainActivity.instance.pushFragment(new FragMyProfile(), true);
		}else{
			FragOtherProfile fragOtherProfile = new FragOtherProfile();
			fragOtherProfile.setData(event.user);
			MainActivity.instance.pushFragment(fragOtherProfile, true);
		}
	}
	
	private void navigateEventAlbumSlidePage(HoothereEvent event){
		if (event.eventAlbum == null || event.eventAlbum.size() == 0) return;
		FragEventAlbumSlide fragEventAlbumSlide = new FragEventAlbumSlide();
		fragEventAlbumSlide.setEvent(event);
		if (event.coverImage != null){
			for (EventAlbum ea : event.eventAlbum){
				if (ea.id == event.coverImage.id){
					fragEventAlbumSlide.setInitalEventAlbum(ea);
					break;
				}
			}
		}
		MainActivity.instance.pushFragment(fragEventAlbumSlide, true);
	}
	
	private class AdapterMyPastEvents extends BaseAdapter{

		@Override
		public int getCount() {
			if (mArrMyPastEvents == null) return 0;
			return mArrMyPastEvents.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrMyPastEvents == null || position >= mArrMyPastEvents.size()) return null;
			return mArrMyPastEvents.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			Object item = getItem(position);
			if (item == null) return rootView;
			if (!(item instanceof HoothereEvent)) return rootView;
			final HoothereEvent event = (HoothereEvent)item;
			EventItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.mypast_events_rowitem, null);
				viewHolder = new EventItemViewHolder();
				viewHolder.ivEventImage = (ImageView)rootView.findViewById(R.id.iv_mypast_events_rowitem_avatar);
				viewHolder.ivLocation = (ImageView)rootView.findViewById(R.id.iv_mypast_events_rowitem_content_location);
				viewHolder.rlGoing = rootView.findViewById(R.id.rl_mypast_events_rowitem_statistics_going_parent);
				viewHolder.rlHoo = rootView.findViewById(R.id.rl_mypast_events_rowitem_statistics_hoo_parent);
				viewHolder.rlInvited = rootView.findViewById(R.id.rl_mypast_events_rowitem_statistics_invited_parent);
				viewHolder.txtDateTime = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_dedailinfo_datetime);
				viewHolder.txtGoing = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_statistics_going);
				viewHolder.txtHoo = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_statistics_hoo);
				viewHolder.txtHoster = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_dedailinfo_hoster);
				viewHolder.txtInvited = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_statistics_invited);
				viewHolder.txtLocation = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_content_location);
				viewHolder.txtStatus = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_content_status);
				viewHolder.txtTitle = (TextView)rootView.findViewById(R.id.txt_mypast_events_rowitem_detailinfo_title);
				
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (EventItemViewHolder)rootView.getTag();
			}
			if (viewHolder == null) return rootView;

			if (event.guestStatus != null){
				if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)){//I
					viewHolder.txtStatus.setText(R.string.event_status_invited);
					viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.color_invited_text_f47566));
				}else if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_WENTTHERE)){
					viewHolder.txtStatus.setText(R.string.event_status_went_there);
					viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.color_purple));
				}else{
					viewHolder.txtStatus.setText(R.string.event_status_accepted);
					viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.color_text_accepted_light_green_9de7bd));
				}
			}else{
				viewHolder.txtStatus.setText("");
			}
			
			String strDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, event.startDateTime), DateFormat.format(Define.DT_FORMAT_TIME, event.startDateTime));
			viewHolder.txtDateTime.setText(event.startDateTime == 0 ? getString(R.string.date_not_specified) : strDate);
			
			viewHolder.txtLocation.setText(event.venueName == null || event.venueName.equals("null") ? "" : event.venueName);
			viewHolder.txtHoster.setText(event.user == null || event.user.fullName == null || event.user.fullName.equals("null") ? "" : event.user.fullName);
			viewHolder.txtTitle.setText(event.name == null || event.name.equals("null") ? "" : event.name);
			
			if (event.statistics != null){
				viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.acceptedCount, getString(R.string.going_title)));
				viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.hooCameCount, getString(R.string.hoocame_title)));
				viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.invitedCount, getString(R.string.invited_title)));
			}else{
				viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.going_title)));
				viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.hoocame_title)));
				viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.invited_title)));
			}
			
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
			
			viewHolder.rlInvited.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(Define.MEMBER_TYPE_INVITED, event);
				}
			});
			
			viewHolder.rlGoing.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(Define.MEMBER_TYPE_GOING_THERE, event);
				}
			});
			
			viewHolder.rlHoo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateEventMemberPage(Define.MEMBER_TYPE_HOO_CAME, event);
				}
			});
			
			viewHolder.txtHoster.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateProfilePage(event);
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
	
	private class EventItemViewHolder extends Object{
		TextView txtStatus;
		TextView txtHoster;
		TextView txtLocation;
		TextView txtTitle;
		TextView txtInvited;
		TextView txtGoing;
		TextView txtHoo;
		View rlInvited;
		View rlGoing;
		View rlHoo;
		TextView txtDateTime;
		View ivLocation;
		ImageView ivEventImage;
	}
}