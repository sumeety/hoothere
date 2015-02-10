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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.HoothereEventAscComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventAlbum;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.HoothereStatistics;
import com.sumeet.util.AlertDialogManager;

@SuppressLint({ "DefaultLocale", "ViewHolder", "InflateParams" })
public class FragEvents extends Fragment{

	private View mRootView;
	private PullToRefreshListView mLvEvents;
	private EventListAdapter mAdapterEvents;
	
	private RelativeLayout mRlHeaderAvatar;
	private RelativeLayout mRlChangeStatus;		
	private TextView mTxtCurrentStatus;
	private int mHorizontalProportion;
	private ArrayList<HoothereEvent> mArrHoothereEvents;
	
	private int mCurrentStatus = Define.STATUS_BUSY;
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.VISIBLE);
		MainActivity.instance.handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_EVENTS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_events, container, false);
			initUI();
			eventHandler();
			changeStatus();
			initData();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
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
							if (responseObj != null && responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
								if (mArrHoothereEvents != null) mArrHoothereEvents.clear();
								mArrHoothereEvents = new ArrayList<HoothereEvent>();
								if (Global.GArrHooThereEvents != null) Global.GArrHooThereEvents.clear();
								Global.GArrHooThereEvents = new ArrayList<HoothereEvent>();
								try {
									JSONArray array = responseObj.getJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
									if (array != null){
										for (int i = 0; i < array.length(); i++){
											HoothereEvent event = new HoothereEvent(array.getJSONObject(i));
											mArrHoothereEvents.add(event);
										}
									}
									Collections.sort(mArrHoothereEvents, new HoothereEventAscComparator());
									if (mAdapterEvents == null){
										mAdapterEvents = new EventListAdapter();
										mLvEvents.setAdapter(mAdapterEvents);
									}
									mAdapterEvents.notifyDataSetChanged();
									Global.GArrHooThereEvents.addAll(mArrHoothereEvents);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							if (Global.GArrHooThereEvents == null || Global.GArrHooThereEvents.size() == 0){
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.GONE);
							}else{
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.GONE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.VISIBLE);
							}
							if (progressDlg != null) progressDlg.dismiss();
							if (mLvEvents != null) mLvEvents.onRefreshComplete();
							MainActivity.instance.displayNotificationBadge();
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
							if (Global.GArrHooThereEvents == null || Global.GArrHooThereEvents.size() == 0){
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.GONE);
							}else{
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.GONE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.VISIBLE);
							}
							if (progressDlg != null) progressDlg.dismiss();
							if (mLvEvents != null) mLvEvents.onRefreshComplete();
						}catch(Exception e){
							e.printStackTrace();
							if (progressDlg != null) progressDlg.dismiss();
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
							if (Global.GArrHooThereEvents == null || Global.GArrHooThereEvents.size() == 0){
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.VISIBLE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.GONE);
							}else{
								mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.GONE);
								mRootView.findViewById(R.id.lv_frag_main_events).setVisibility(View.VISIBLE);
							}
							if (progressDlg != null) progressDlg.dismiss();
							if (mLvEvents != null) mLvEvents.onRefreshComplete();
						}catch(Exception e){
							e.printStackTrace();
							if (progressDlg != null) progressDlg.dismiss();
						}
					}
				});
			}
		});
	}
	
	private void initData(){
		getUpComingEvents();
	}
	
	private void initUI(){
		android.view.ViewGroup.LayoutParams lp = mRootView.findViewById(R.id.iv_frag_main_friends_tab_underbar).getLayoutParams();
		lp.width = mHorizontalProportion;
		mRootView.findViewById(R.id.iv_frag_main_friends_tab_underbar).setLayoutParams(lp);
		mRootView.findViewById(R.id.iv_frag_main_friends_tab_underbar).requestLayout();
		mRlHeaderAvatar = (RelativeLayout) mRootView.findViewById(R.id.rl_frag_main_header_avatar);
		mTxtCurrentStatus = (TextView)mRootView.findViewById(R.id.txt_frag_main_header_status);
		mRlChangeStatus = (RelativeLayout) mRootView.findViewById(R.id.rl_frag_main_events_status_parent);
		mRlChangeStatus.setVisibility(View.GONE);
		mRootView.findViewById(R.id.rl_frag_main_friends).setVisibility(View.GONE);
		mRootView.findViewById(R.id.rl_frag_main_events).setVisibility(View.VISIBLE);
		mLvEvents = (PullToRefreshListView)mRootView.findViewById(R.id.lv_frag_main_events);
		mAdapterEvents = new EventListAdapter();
		mLvEvents.setAdapter(mAdapterEvents);
		mAdapterEvents.notifyDataSetChanged();
		mRootView.findViewById(R.id.ll_frag_main_events_no_events).setVisibility(View.GONE);
		loadMyAvatar();
	}
	
	private void loadMyAvatar(){
		ImageView ivPhoto = (ImageView)mRootView.findViewById(R.id.iv_frag_main_header_avatar);
		AQuery aq = new AQuery(ivPhoto);
		ImageOptions imageOption = new ImageOptions();			
		imageOption.animation = AQuery.FADE_IN_FILE;
		imageOption.memCache = false;
		imageOption.fileCache = false;
		imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
		imageOption.fallback = R.drawable.defaultpic;
		String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
		aq.image(strAvatarUrl, imageOption);
		AQuery aq1 = new AQuery((ImageView)mRootView.findViewById(R.id.iv_frag_main_events_status_content_avatar));
		aq1.image(strAvatarUrl, imageOption);
	}
	
	private void eventHandler(){
		mLvEvents.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView){
				getUpComingEvents();
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_main_header_addevent).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.pushFragment(new FragCreateEventName(), true);
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_main_events_no_event_create).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.pushFragment(new FragCreateEventName(), true);
			}
		});
		
		mLvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FragEventDetail fragEventDetail = new FragEventDetail();
				fragEventDetail.setEvent((HoothereEvent)mAdapterEvents.getItem(position - 1));
				MainActivity.instance.pushFragment(fragEventDetail, true);
			}
		});
		
		mTxtCurrentStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				mRlChangeStatus.setVisibility(View.VISIBLE);
			}
		});
		
		mRlHeaderAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				mRlChangeStatus.setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_main_events_status_content_want_plan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentStatus = Define.STATUS_WANT_PLAN;
				changeStatus();
				mRlChangeStatus.setVisibility(View.GONE);
			}
		});

		mRootView.findViewById(R.id.rl_frag_main_events_status_content_go_out).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentStatus = Define.STATUS_GOING_OUT;
				changeStatus();
				mRlChangeStatus.setVisibility(View.GONE);
			}
		});

		mRootView.findViewById(R.id.rl_frag_main_events_status_content_busy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentStatus = Define.STATUS_BUSY;
				changeStatus();
				mRlChangeStatus.setVisibility(View.GONE);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_main_change_status_fade).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRlChangeStatus.setVisibility(View.GONE);
			}
		});
	}
	
	private void changeStatus(){
		switch(mCurrentStatus){
		case Define.STATUS_AVAILABLE:
			mTxtCurrentStatus.setText(getString(R.string.change_status_available));
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_header_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_available);
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_events_status_content_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_available);
			((TextView)mRootView.findViewById(R.id.txt_frag_main_events_status_content_currentstatus)).setText(getString(R.string.change_status_available));
			break;
		case Define.STATUS_BUSY:
			mTxtCurrentStatus.setText(getString(R.string.change_status_busy));
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_header_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_busy);
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_events_status_content_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_busy);
			((TextView)mRootView.findViewById(R.id.txt_frag_main_events_status_content_currentstatus)).setText(getString(R.string.change_status_busy));
			break;
		case Define.STATUS_GOING_OUT:
			mTxtCurrentStatus.setText(getString(R.string.change_status_go_out));
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_header_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_going_out);
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_events_status_content_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_going_out);
			((TextView)mRootView.findViewById(R.id.txt_frag_main_events_status_content_currentstatus)).setText(getString(R.string.change_status_go_out));
			break;
		case Define.STATUS_WANT_PLAN:
			mTxtCurrentStatus.setText(getString(R.string.change_status_want));
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_header_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_wantplan);
			((ImageView)mRootView.findViewById(R.id.iv_frag_main_events_status_content_avatar_status)).setImageResource(R.drawable.icon_avatar_status_ring_wantplan);
			((TextView)mRootView.findViewById(R.id.txt_frag_main_events_status_content_currentstatus)).setText(getString(R.string.change_status_want));
			break;
		}
	}
	
	private void navigateEventMemberPage(HoothereEvent event, int nType){
		if (event == null) return;
		FragEventMembers fragEventMembers = new FragEventMembers();
		fragEventMembers.setMemberType(nType);
		fragEventMembers.setEventId(event.id);
		fragEventMembers.setStatistics(event.statistics);
		MainActivity.instance.pushFragment(fragEventMembers, true);
	}
	
	private class EventItemViewHolder extends Object{
		TextView txtStatus;
		ImageView ivStatus;
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
	
	private class EventListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if (mArrHoothereEvents == null) return 0;
			return mArrHoothereEvents.size();
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
			View rootView = convertView;
			final HoothereEvent event = (HoothereEvent)getItem(position);
			if (event == null) return null;

			EventItemViewHolder viewHolder;
			if (rootView == null){
				rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.main_events_rowitem, null);
				viewHolder = new EventItemViewHolder();
				viewHolder.ivLocation = rootView.findViewById(R.id.iv_main_events_rowitem_content_location);
				viewHolder.ivStatus = (ImageView)rootView.findViewById(R.id.iv_main_events_rowitem_content_status);
				viewHolder.txtStatus = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_content_status);
				viewHolder.rlGoing = rootView.findViewById(R.id.rl_main_events_rowitem_statistics_going_parent);
				viewHolder.rlHoo = rootView.findViewById(R.id.rl_main_events_rowitem_statistics_hoo_parent);
				viewHolder.rlInvited = rootView.findViewById(R.id.rl_main_events_rowitem_statistics_invited_parent);
				viewHolder.txtDateTime = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_dedailinfo_datetime);
				viewHolder.txtGoing = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_statistics_going);
				viewHolder.txtHoo = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_statistics_hoo);
				viewHolder.txtInvited = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_statistics_invited);
				viewHolder.txtHoster = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_dedailinfo_hoster);
				viewHolder.txtLocation = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_content_location);
				viewHolder.txtTitle = (TextView)rootView.findViewById(R.id.txt_main_events_rowitem_detailinfo_title);
				viewHolder.ivEventImage = (ImageView)rootView.findViewById(R.id.iv_main_events_rowitem_avatar);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (EventItemViewHolder)rootView.getTag();
			}
			
			if (viewHolder == null) return null;
			
			if (event.guestStatus != null){
				if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)){
					viewHolder.txtStatus.setText(R.string.event_status_pending);
					viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.color_text_pending_a5a5a5));
					viewHolder.ivStatus.setImageResource(R.drawable.icon_pending_gray);
					viewHolder.ivStatus.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							acceptEvent(event);
						}
					});
					
					viewHolder.txtStatus.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							acceptEvent(event);
						}
					});
					
				}else{
					viewHolder.txtStatus.setText(getString(R.string.event_status_accepted));
					viewHolder.txtStatus.setTextColor(getResources().getColor(R.color.color_text_accepted_light_green_9de7bd));
					viewHolder.ivStatus.setImageResource(R.drawable.icon_accepted_green);
				}
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
			viewHolder.txtDateTime.setText(event.endDateTime == 0 ? getString(R.string.date_not_specified) : strDate);
			
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
					if (event.user != null && Global.GUser != null && event.user.userid == Global.GUser.userid){
						MainActivity.instance.pushFragment(new FragMyProfile(), true);
					}else{
						FragOtherProfile fragOtherProfile = new FragOtherProfile();
						fragOtherProfile.setData(event.user);
						MainActivity.instance.pushFragment(fragOtherProfile, true);
					}
				}
			});
			
			viewHolder.txtLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragEventLocation fragEventLocation = new FragEventLocation();
					event.radius = event.radius == null || event.radius.isEmpty() || event.radius.equals("null") ? String.format("%d", Define.RANGE_MAX_VAL / 2) : event.radius;
					fragEventLocation.setEvent(event);
					fragEventLocation.setEditable(false);
					MainActivity.instance.pushFragment(fragEventLocation, true);
				}
			});
			
			viewHolder.ivLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragEventLocation fragEventLocation = new FragEventLocation();
					event.radius = event.radius == null || event.radius.isEmpty() || event.radius.equals("null") ? String.format("%d", Define.RANGE_MAX_VAL / 2) : event.radius;
					fragEventLocation.setEvent(event);
					fragEventLocation.setEditable(false);
					MainActivity.instance.pushFragment(fragEventLocation, true);
				}
			});
			
			viewHolder.ivEventImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (event.eventAlbum != null && event.eventAlbum.size() > 0){
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
				}
			});
			
			return rootView;
		}
	}
	
	private void acceptEvent(final HoothereEvent event){
		if (event == null) return;
		if (event.guestStatus != null && !event.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, "", getString(R.string.progress_accepting_invitation));
		new CommunicationAPIManager(MainActivity.instance).sendRequestAcceptEvent(event.id, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
								HoothereEvent event1 = event;

								if (event1.statistics == null) event.statistics = new HoothereStatistics();
								event1.statistics.acceptedCount++;
								event1.statistics.invitedCount--;
								event1.guestStatus = Define.EVENTGUESTSTATUS_ACCEPTED;
								if (mArrHoothereEvents != null){
									int nIndex = mArrHoothereEvents.indexOf(event);
									mArrHoothereEvents.remove(event);
									if (nIndex != -1){
										mArrHoothereEvents.add(nIndex, event1);
									}else{
										mArrHoothereEvents.add(event1);
									}
								}else{
									mArrHoothereEvents = new ArrayList<HoothereEvent>();
									mArrHoothereEvents.add(event1);
								}
								mAdapterEvents.notifyDataSetChanged();
							}else if(responseObj.has(Define.EVENT_ID)){
								displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
								HoothereEvent event1 = event;

								if (event1.statistics == null) event.statistics = new HoothereStatistics();
								event1.statistics.acceptedCount++;
								event1.statistics.invitedCount--;
								event1.guestStatus = Define.EVENTGUESTSTATUS_ACCEPTED;
								if (mArrHoothereEvents != null){
									int nIndex = mArrHoothereEvents.indexOf(event);
									mArrHoothereEvents.remove(event);
									if (nIndex != -1){
										mArrHoothereEvents.add(nIndex, event1);
									}else{
										mArrHoothereEvents.add(event1);
									}
								}else{
									mArrHoothereEvents = new ArrayList<HoothereEvent>();
									mArrHoothereEvents.add(event1);
								}
								mAdapterEvents.notifyDataSetChanged();
							}else if (responseObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
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
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_accept_event_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}