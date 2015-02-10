package com.sumeet.hoothere.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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
import com.sumeet.comparators.HoothereEventAscComparator;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventAlbum;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.HoothereStatistics;
import com.sumeet.util.AlertDialogManager;

public class FragSearchEvent extends Fragment{
	
	private View mRootView;
	private ListView mLvSearchResult;
	private ArrayList<HoothereEvent> mArrSearchedResult;
	private EventItemAdapter mAdapterSearchResult;
	private EditText mEdtSearch;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_search_event, container, false);
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
	}
	
	private void initUI(){
		mEdtSearch = (EditText)mRootView.findViewById(R.id.edt_frag_search_event_search);
		mLvSearchResult = (ListView)mRootView.findViewById(R.id.lv_frag_search_event);
		mAdapterSearchResult = new EventItemAdapter();
		mLvSearchResult.setAdapter(mAdapterSearchResult);
		mAdapterSearchResult.notifyDataSetChanged();
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
		
		mRootView.findViewById(R.id.rl_frag_search_event_search_hint).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.rl_frag_search_event_search_hint_text).setVisibility(View.GONE);				
				mEdtSearch.setVisibility(View.VISIBLE);
				mEdtSearch.requestFocus();
				InputMethodManager imm = (InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEdtSearch, InputMethodManager.SHOW_FORCED);
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_search_event_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_search_event_search_hint_text).setVisibility(View.VISIBLE);				
	 			mEdtSearch.setVisibility(View.GONE);
	 			mEdtSearch.setText("");
				MainActivity.instance.onBackPressed();
			}
		});

		mRootView.findViewById(R.id.iv_frag_search_event_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				MainActivity.instance.onBackPressed();
			}
		});
		
		mLvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				hideKeyboard();
				mRootView.findViewById(R.id.rl_frag_search_event_search_hint_text).setVisibility(View.VISIBLE);				
				mEdtSearch.setVisibility(View.GONE);
				HoothereEvent event = (HoothereEvent)mAdapterSearchResult.getItem(position);
				if (event == null) return;
				FragEventDetail fragEventDetail = new FragEventDetail();
				fragEventDetail.setEvent(event);
				if (event.guestStatus == null || event.guestStatus.isEmpty() || event.guestStatus.equals("null") || event.endDateTime <= Calendar.getInstance().getTimeInMillis()){
					fragEventDetail.setEditable(false);
					fragEventDetail.setPast(event.endDateTime <= Calendar.getInstance().getTimeInMillis());
				}
				MainActivity.instance.pushFragment(fragEventDetail, true);
			}
		});
		
		mEdtSearch.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					hideKeyboard();
					mRootView.findViewById(R.id.rl_frag_search_event_search_hint_text).setVisibility(View.VISIBLE);				
					mEdtSearch.setVisibility(View.GONE);
					searchEvent(mEdtSearch.getText().toString());
					return true;
				}
				return false;
			}
		});
	}
	
	private void searchEvent(String sIndex){
		if (Global.GUser == null) return;
		if (mAdapterSearchResult == null){
			mAdapterSearchResult = new EventItemAdapter();
		}
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_searching_event));
		new CommunicationAPIManager(MainActivity.instance).sendRequestSearchEvent(Global.GUser.userid, sIndex, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (mArrSearchedResult != null) mArrSearchedResult.clear();
							mArrSearchedResult = new ArrayList<HoothereEvent>();
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									try {
										JSONArray events = responseObj.getJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
										if (events != null){
											for (int i = 0; i < events.length(); i++){
												HoothereEvent event = new HoothereEvent(events.getJSONObject(i));
												mArrSearchedResult.add(event);
											}
											Collections.sort(mArrSearchedResult, new HoothereEventAscComparator());
										}
									} catch (JSONException e) {
										displayOKAlertDialog(getString(R.string.alert_dlg_search_event_error));
									}
								}else if(responseObj.has(Define.ERROR_TAG)){
									displayOKAlertDialog(responseObj.optString(Define.ERROR_TAG));
								}
							}
							mAdapterSearchResult.notifyDataSetChanged();
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
									displayOKAlertDialog(getString(R.string.alert_dlg_search_event_error));
								}
							}else{
								displayOKAlertDialog(getString(R.string.alert_dlg_search_event_error));
							}
							mAdapterSearchResult.notifyDataSetChanged();
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
							displayOKAlertDialog(getString(R.string.alert_dlg_search_event_error));
							mAdapterSearchResult.notifyDataSetChanged();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void joinEvent(final HoothereEvent event){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_joining_event));
		new CommunicationAPIManager(MainActivity.instance).sendRequestJoinEvent(event == null ? 0 : event.id, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_join_event_success);
								HoothereEvent event1 = event;
								if (mArrSearchedResult == null) mArrSearchedResult = new ArrayList<HoothereEvent>();
								int nIndex = mArrSearchedResult.indexOf(event);
								if (event1.statistics == null) event1.statistics = new HoothereStatistics();
								event1.statistics.acceptedCount++;
								event1.statistics.invitedCount--;
								if (nIndex != -1){
									mArrSearchedResult.remove(event);
									mArrSearchedResult.add(nIndex, event1);
								}else{
									mArrSearchedResult.add(event1);
								}
							}else{
								if (responseObj.has(Define.ERRORMESSAGE_TAG)){
									displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
								}else if (responseObj.has(Define.EVENT_ID)){
									displayOKAlertDialog(R.string.alert_dlg_join_event_success);
									HoothereEvent event1 = new HoothereEvent(responseObj);
									if (mArrSearchedResult != null){
										int nIndex = mArrSearchedResult.indexOf(event);
										if (nIndex != -1){
											mArrSearchedResult.remove(event);
											mArrSearchedResult.add(nIndex, event1);
										}else{
											mArrSearchedResult.add(event1);
										}
									}else{
										mArrSearchedResult = new ArrayList<HoothereEvent>();
										mArrSearchedResult.add(event1);
									}
								}else{
									displayOKAlertDialog(R.string.alert_dlg_join_event_error);
								}
							}
							if (mAdapterSearchResult == null) mAdapterSearchResult = new EventItemAdapter();
							mAdapterSearchResult.notifyDataSetChanged();
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
								displayOKAlertDialog(R.string.alert_dlg_join_event_error);
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
							displayOKAlertDialog(R.string.alert_dlg_join_event_error);
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
	
	private void navigateEventAlbumSlidePage(HoothereEvent event)
	{
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
	
	private void navigateProfilePage(HoothereEvent event){
		if (event.user != null && Global.GUser != null && event.user.userid == Global.GUser.userid){
			MainActivity.instance.pushFragment(new FragMyProfile(), true);
		}else{
			FragOtherProfile fragOtherProfile = new FragOtherProfile();
			fragOtherProfile.setData(event.user);
			MainActivity.instance.pushFragment(fragOtherProfile, true);
		}
	}
	
	private class SearchEventItemViewHolder{
		TextView txtHoster;
		TextView txtInvited;
		TextView txtGoing;
		TextView txtHoo;
		TextView txtLocation;
		TextView txtTitle;
		TextView txtDateTime;
		TextView txtInvite;
		View rlInvited;
		View rlGoing;
		View rlHoo;
		View ivLocation;
		ImageView ivEventImage;
	}
	
	@SuppressLint("InflateParams")
	private class EventItemAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (mArrSearchedResult == null) return 0;
			return mArrSearchedResult.size();
		}

		@Override
		public Object getItem(int position) {
			if (mArrSearchedResult == null || position >= mArrSearchedResult.size()) return null;
			return mArrSearchedResult.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try{
				final HoothereEvent event = (HoothereEvent)getItem(position);
				if (event == null) return null;
	
				View rootView = convertView;
				SearchEventItemViewHolder viewHolder;
				if (rootView == null){
					rootView = LayoutInflater.from(MainActivity.instance).inflate(R.layout.inviting_events_rowitem, null);
					viewHolder = new SearchEventItemViewHolder();
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
					viewHolder.txtInvite = (TextView)rootView.findViewById(R.id.txt_upcoming_events_rowitem_content_invite);
					viewHolder.ivEventImage = (ImageView)rootView.findViewById(R.id.iv_upcoming_events_rowitem_avatar);
					rootView.setTag(viewHolder);
				}else{
					try{
						viewHolder = (SearchEventItemViewHolder)rootView.getTag();
					}catch(ClassCastException e){
						e.printStackTrace();
						return null;
					}
				}
				
				if (viewHolder == null) return null;
				
				viewHolder.txtHoster.setText(event.user != null ? event.user.firstName : "");
				
				if (event.statistics != null){
					viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.invitedCount, getString(R.string.invited_title)));//"%d %s"
					viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.acceptedCount, getString(R.string.going_title)));
					viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, event.statistics.hoothereCount, getString(R.string.hoo_title)));
				}else{
					viewHolder.txtInvited.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.invited_title)));
					viewHolder.txtGoing.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.going_title)));
					viewHolder.txtHoo.setText(String.format(Define.EVENTMEMBER_COUNT_TITLE, 0, getString(R.string.hoo_title)));
				}
				
				if (event.guestStatus == null || event.guestStatus.isEmpty() || event.guestStatus.equals("null")){
					viewHolder.txtInvite.setText(R.string.join_title);
				}else if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)){
					viewHolder.txtInvite.setText(R.string.invited_title);
				}else if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
					viewHolder.txtInvite.setText(R.string.going_title);
				}else if (event.guestStatus.equals(Define.EVENTGUESTSTATUS_HT)){
					viewHolder.txtInvite.setText(R.string.hoothere_title);
				}
				
				if (event.endDateTime < Calendar.getInstance().getTimeInMillis()){
					viewHolder.txtInvite.setText(R.string.past_event);
				}
				
				final TextView txtInvite = viewHolder.txtInvite;
				
				viewHolder.txtLocation.setText((event.venueName == null || event.venueName.equals("null")) ? "" : event.venueName);
				viewHolder.txtTitle.setText(event.name == null || event.name.equals("null") ? "" : event.name);
				String strDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, event.startDateTime), DateFormat.format(Define.DT_FORMAT_TIME, event.startDateTime));
				viewHolder.txtDateTime.setText(event.endDateTime == 0 ? getString(R.string.date_not_specified) : strDate);
				
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
				
				viewHolder.txtInvite.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v){
						if (!txtInvite.getText().toString().equals(getString(R.string.join_title))) return;
						joinEvent(event);
					}
				});
				
				viewHolder.rlInvited.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						navigateEventMemberPage(event, Define.MEMBER_TYPE_INVITED);
					}
				});
				
				viewHolder.rlGoing.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						navigateEventMemberPage(event, Define.MEMBER_TYPE_GOING_THERE);
					}
				});
				
				viewHolder.rlHoo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						navigateEventMemberPage(event, Define.MEMBER_TYPE_HOO_THERE);
					}
				});
				
				viewHolder.txtHoster.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						navigateProfilePage(event);
					}
				});
				
				viewHolder.txtLocation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v){
						hideKeyboard();
						navigateEventLocationPage(event);
					}
				});
				
	 			viewHolder.ivLocation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
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
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}
}