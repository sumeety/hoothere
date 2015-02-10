package com.sumeet.hoothere.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
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
import com.sumeet.util.ActionSheet;
import com.sumeet.util.ActionSheet.ActionSheetListener;
import com.sumeet.util.AlertDialogManager;

public class FragEventDetail extends Fragment implements ActionSheetListener{
	
	private View mRootView;
	private HoothereEvent mEvent;
	private boolean mFlagEditable = true;
	private boolean mFlagPast = false;
	private boolean mFlagReturnToHome = false;
	private HashMap<View, Object> mMapEventAlbums;
	private ArrayList<Object> mArrEventAlbums;
	private boolean mFlagUploadable = false;
	private static final int EA_CNT_PER_ROW = 3;
	private File mTempNewEventAlbumFile = null;
	private Uri mUriNewEventAlbum = null;
	private Bitmap mBmpNewEventAlbum = null;
	private File mTempCropFile = null;
	private boolean mFlagIsEventAlbumsVisible;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
			initUI();
			eventHandler();
			if (mFlagIsEventAlbumsVisible){
				mRootView.findViewById(R.id.rl_frag_event_detail_tabs_eventalbums).performClick();
			}else{
				mRootView.findViewById(R.id.rl_frag_event_detail_tabs_info).performClick();
			}
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	public void setEditable(boolean bFlag){
		mFlagEditable = bFlag;
	}
	
	public void setPast(boolean bFlag){
		mFlagPast = bFlag;
	}
	
	public void setFlagReturnToHome(boolean bFlag){
		mFlagReturnToHome = bFlag;
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mFlagIsEventAlbumsVisible = mRootView.findViewById(R.id.rl_frag_event_detail_event_albums_content).getVisibility() == View.VISIBLE;
	}
	
	private void displayOKAlertDialog(String strMessage){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				strMessage, 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void displayOKAlertDialog(int resId){
		displayOKAlertDialog(getString(resId));
	}
	
	private void initUI(){
		MainActivity.instance.setTheme(R.style.ActionSheetStyleIOS7);
		if (!checkRelationBetweenHosterAndMe() || mFlagPast){
			mRootView.findViewById(R.id.txt_frag_event_detail_edit).setVisibility(View.GONE);
		}
		if (mEvent != null){
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_content_title)).setText(mEvent.name == null || mEvent.name.equals("null") ? "" : mEvent.name);
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_hoster_name)).setText(mEvent.user != null ? mEvent.user.fullName : "");
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_description)).setText(mEvent.description == null || mEvent.description.equals("null") ? "" : mEvent.description);
			mRootView.findViewById(R.id.rl_frag_event_detail_description).setVisibility(
					((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_description)).getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_location_1)).setText(mEvent.venueName == null || mEvent.venueName.equals("null") ? "" : mEvent.venueName);
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_location_venue)).setText(mEvent.venueName == null || mEvent.venueName.equals("null") ? "" : mEvent.venueName);
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_location_address)).setText(mEvent.address == null || mEvent.address.equals("null") ? "" : mEvent.address);
			
			mRootView.findViewById(R.id.ll_frag_event_detail_location).setVisibility(
					((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_location_venue)).getText().toString().isEmpty() && 
					((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_location_address)).getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
			String strStartDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, mEvent.startDateTime), DateFormat.format(Define.DT_FORMAT_TIME, mEvent.startDateTime));
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_start_time)).setText(mEvent.startDateTime == 0 ? getString(R.string.date_not_specified) : strStartDate);
			String strEndDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, mEvent.endDateTime), DateFormat.format(Define.DT_FORMAT_TIME, mEvent.endDateTime));
			((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_start_end_time)).setText(
					String.format(Define.DT_FORMAT_MERGED, mEvent.startDateTime == 0 ? getString(R.string.date_not_specified) : strStartDate , mEvent.endDateTime == 0 
					? getString(R.string.date_not_specified) : strEndDate));
			mRootView.findViewById(R.id.rl_frag_event_detail_end_time).setVisibility(mEvent.startDateTime == 0 && mEvent.endDateTime == 0 ? View.GONE : View.VISIBLE);
			if (mEvent.statistics != null){
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_guests_hoocame_cnt)).setText(String.format("%d", mEvent.statistics != null ? mEvent.statistics.hooCameCount : 0));
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_guests_goingthere_cnt)).setText(String.format("%d", mEvent.statistics != null ? mEvent.statistics.acceptedCount : 0));
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_guests_hoothere_cnt)).setText(String.format("%d", mEvent.statistics != null ? mEvent.statistics.hoothereCount : 0));
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_guests_invited_cnt)).setText(String.format("%d", mEvent.statistics != null ? mEvent.statistics.invitedCount : 0));
			}
			if (mEvent.guestStatus != null){
				if (mEvent.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)){
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setText(R.string.accept_event);
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setTextColor(getResources().getColor(R.color.color_purple));
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setEnabled(true);
				}else if (mEvent.guestStatus.equals(Define.EVENTGUESTSTATUS_HT)){
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setText(R.string.checked_in_event);
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setEnabled(false);
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setTextColor(getResources().getColor(R.color.color_text_gray_858585));
				}else{
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setText(R.string.check_in_event);
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setTextColor(getResources().getColor(R.color.color_text_dark_gray_666666));
					((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).setEnabled(true);
				}
			}
			if (mEvent.eventType != null && mEvent.eventType.equals(Define.EVENT_TYPE_PUBLIC)){
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_event_type)).setImageResource(R.drawable.icon_public_event);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_event_type)).setText(R.string.event_type_public);
			}else{
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_event_type)).setImageResource(R.drawable.icon_private_event);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_detail_event_type)).setText(R.string.event_type_private);
			}
			ImageView ivPhoto = (ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_content_avatar);
			AQuery aq = new AQuery(ivPhoto);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOption.fallback = R.drawable.defaultpic;
			String strAvatarUrl = String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, mEvent.user != null ? mEvent.user.userid : 0);
			aq.image(strAvatarUrl, imageOption);
			displayEventAlbums();
		}
		
		if (!mFlagEditable){
			mRootView.findViewById(R.id.txt_frag_event_detail_edit).setVisibility(View.GONE);
			mRootView.findViewById(R.id.rl_frag_event_detail_action).setVisibility(View.GONE);
			if (!mFlagPast){
				mRootView.findViewById(R.id.rl_frag_event_detail_action_join).setVisibility(View.VISIBLE);
			}
		}
		if (mFlagPast){
			mRootView.findViewById(R.id.rl_frag_event_detail_event_type).setVisibility(View.INVISIBLE);
			mRootView.findViewById(R.id.ll_frag_event_detail_location).setVisibility(View.INVISIBLE);
			mRootView.findViewById(R.id.ll_frag_event_detail_location).setEnabled(false);
			
		}
	}
	
	private void navigateEventAlbumSlidePage(EventAlbum initialEA){
		FragEventAlbumSlide fragEventAlbumSlide = new FragEventAlbumSlide();
		fragEventAlbumSlide.setEvent(mEvent);
		fragEventAlbumSlide.setInitalEventAlbum(initialEA);
		MainActivity.instance.pushFragment(fragEventAlbumSlide, true);
	}
	
	private void displayEventAlbums(){
		if (mMapEventAlbums != null) mMapEventAlbums.clear();
		mMapEventAlbums = new HashMap<View, Object>();
		LinearLayout llEventAlbums = (LinearLayout)mRootView.findViewById(R.id.ll_frag_event_detail_event_albums_content);
		llEventAlbums.removeAllViews();
		mFlagUploadable = checkUploadable();
		if (mArrEventAlbums != null) mArrEventAlbums.clear();
		mArrEventAlbums = new ArrayList<Object>();
		if (mFlagUploadable && !mFlagPast){
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon_uploadnew_image);
			mArrEventAlbums.add(bm);
		}
		if (mEvent != null && mEvent.eventAlbum != null){
			for (int i = 0; i < mEvent.eventAlbum.size(); i++){
				mArrEventAlbums.add(mEvent.eventAlbum.get(i));
			}
		}

		int nIndex = 0;
		View rlEARow = null;
		for (Object ea : mArrEventAlbums){
			if (nIndex % EA_CNT_PER_ROW == 0){
				rlEARow = LayoutInflater.from(MainActivity.instance).inflate(R.layout.item_eventalbums, null);
				rlEARow.findViewById(R.id.iv_item_eventalbums_content_1).setVisibility(View.GONE);
				rlEARow.findViewById(R.id.iv_item_eventalbums_content_2).setVisibility(View.GONE);
				rlEARow.findViewById(R.id.iv_item_eventalbums_content_3).setVisibility(View.GONE);
				llEventAlbums.addView(rlEARow);
			}
			if (ea instanceof Bitmap){
				switch(nIndex % EA_CNT_PER_ROW){
				case 0:
					ImageView ivEA1 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_1);
					ivEA1.setImageBitmap((Bitmap)ea);
					ivEA1.setVisibility(View.VISIBLE);
					ivEA1.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showActionSheet();
						}
					});
					break;
				case 1:
					ImageView ivEA2 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_2);
					ivEA2.setImageBitmap((Bitmap)ea);
					ivEA2.setVisibility(View.VISIBLE);
					ivEA2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showActionSheet();
						}
					});
					break;
				case 2:
					ImageView ivEA3 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_3);
					ivEA3.setImageBitmap((Bitmap)ea);
					ivEA3.setVisibility(View.VISIBLE);
					ivEA3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showActionSheet();
						}
					});
					break;
				}
			}else if(ea instanceof EventAlbum){
				switch(nIndex % EA_CNT_PER_ROW){
				case 0:
					final ImageView ivEA1 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_1);
					ivEA1.setVisibility(View.VISIBLE);
					AQuery aq = new AQuery(ivEA1);
					ivEA1.setTag(ea);
					ImageOptions imageOption = new ImageOptions();			
					imageOption.animation = AQuery.FADE_IN;
					imageOption.memCache = true;
					imageOption.fileCache = false;
					imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defualt_event);
					imageOption.fallback = R.drawable.defualt_event;
					aq.image(((EventAlbum)ea).thumbnailUrl, imageOption);
					ivEA1.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Object ea = ivEA1.getTag();
							if (ea instanceof EventAlbum){
								navigateEventAlbumSlidePage((EventAlbum)ea);
							}
						}
					});
					break;
				case 1:
					final ImageView ivEA2 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_2);
					ivEA2.setVisibility(View.VISIBLE);
					AQuery aq1 = new AQuery(ivEA2);
					ivEA2.setTag(ea);
					ImageOptions imageOption1 = new ImageOptions();
					imageOption1.animation = AQuery.FADE_IN;
					imageOption1.memCache = true;
					imageOption1.fileCache = false;
					imageOption1.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defualt_event);
					imageOption1.fallback = R.drawable.defualt_event;
					aq1.image(((EventAlbum)ea).thumbnailUrl, imageOption1);
					ivEA2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Object ea = ivEA2.getTag();
							if (ea instanceof EventAlbum){
								navigateEventAlbumSlidePage((EventAlbum)ea);
							}
						}
					});
					break;
				case 2:
					final ImageView ivEA3 = (ImageView)rlEARow.findViewById(R.id.iv_item_eventalbums_content_3);
					ivEA3.setVisibility(View.VISIBLE);
					AQuery aq2 = new AQuery(ivEA3);
					ivEA3.setTag(ea);
					ImageOptions imageOption2 = new ImageOptions();			
					imageOption2.animation = AQuery.FADE_IN;
					imageOption2.memCache = true;
					imageOption2.fileCache = false;
					imageOption2.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defualt_event);
					imageOption2.fallback = R.drawable.defualt_event;
					aq2.image(((EventAlbum)ea).thumbnailUrl, imageOption2);
					ivEA3.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Object ea = ivEA3.getTag();
							if (ea instanceof EventAlbum){
								navigateEventAlbumSlidePage((EventAlbum)ea);
							}
						}
					});
					break;
				}
			}
			nIndex++;
		}
	}
	
	private void navigateEventMemberPage(int nType){
		FragEventMembers fragEventMembers = new FragEventMembers();
		fragEventMembers.setMemberType(nType);
		fragEventMembers.setEventId(mEvent.id);
		fragEventMembers.setStatistics(mEvent.statistics);
		MainActivity.instance.pushFragment(fragEventMembers, true);
	}
	
	private void eventHandler(){
		
		mRootView.findViewById(R.id.ll_frag_event_detail_guests_hoocame).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent == null) return;
				navigateEventMemberPage(Define.MEMBER_TYPE_HOO_CAME);
			}
		});
		
		mRootView.findViewById(R.id.ll_frag_event_detail_guests_goingthere).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent == null) return;
				navigateEventMemberPage(Define.MEMBER_TYPE_GOING_THERE);
			}
		});

		mRootView.findViewById(R.id.ll_frag_event_detail_guests_hoothere).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent == null) return;
				navigateEventMemberPage(Define.MEMBER_TYPE_HOO_THERE);
			}
		});
		
		mRootView.findViewById(R.id.ll_frag_event_detail_guests_invited).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent == null) return;
				navigateEventMemberPage(Define.MEMBER_TYPE_INVITED);
			}
		});

		mRootView.findViewById(R.id.btn_frag_event_detail_action_join).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				joinEvent();
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_detail_tabs_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_tabs_info)).setImageResource(R.drawable.icon_event_detailinfo_purple);
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_tabs_eventalbums)).setImageResource(R.drawable.icon_event_album_gray);
				mRootView.findViewById(R.id.rl_frag_event_detail_event_albums_content).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_event_detail_information_detailed).setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_detail_tabs_eventalbums).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_tabs_info)).setImageResource(R.drawable.icon_event_detailinfo_gray);
				((ImageView)mRootView.findViewById(R.id.iv_frag_event_detail_tabs_eventalbums)).setImageResource(R.drawable.icon_event_album_purple);
				mRootView.findViewById(R.id.rl_frag_event_detail_information_detailed).setVisibility(View.GONE);
				mRootView.findViewById(R.id.rl_frag_event_detail_event_albums_content).setVisibility(View.VISIBLE);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_event_detail_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if (mFlagReturnToHome == false){
						MainActivity.instance.onBackPressed();
					}else{
				      	MainActivity.instance.pushFragment(new FragEvents(), false);
					}
				}catch(Exception e){
					e.printStackTrace();
					MainActivity.instance.onBackPressed();
				}
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_event_detail_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragEditEvent fragEditEvent = new FragEditEvent();
				fragEditEvent.setEvent(mEvent);
				fragEditEvent.setCaller(FragEventDetail.this);
				MainActivity.instance.pushFragment(fragEditEvent, true);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_detail_location_1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragEventLocation fragEventLocation = new FragEventLocation();
				if (mEvent == null) mEvent = new HoothereEvent();
				mEvent.radius = mEvent.radius == null || mEvent.radius.isEmpty() ? String.format("%d", Define.RANGE_MAX_VAL / 2) : mEvent.radius;
				fragEventLocation.setEvent(mEvent);
				fragEventLocation.setEditable(false);
				MainActivity.instance.pushFragment(fragEventLocation, true);
			}
		});
		
		mRootView.findViewById(R.id.ll_frag_event_detail_location).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragEventLocation fragEventLocation = new FragEventLocation();
				if (mEvent == null) mEvent = new HoothereEvent();
				mEvent.radius = mEvent.radius == null || mEvent.radius.isEmpty() ? String.format("%d", Define.RANGE_MAX_VAL / 2) : mEvent.radius;
				fragEventLocation.setEvent(mEvent);
				fragEventLocation.setEditable(false);
				MainActivity.instance.pushFragment(fragEventLocation, true);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_event_detail_content_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.txt_frag_event_detail_hoster_name).performClick();
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_event_detail_hoster_name).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent.user != null && Global.GUser != null && mEvent.user.userid == Global.GUser.userid){
					MainActivity.instance.pushFragment(new FragMyProfile(), true);
				}else{
					FragOtherProfile fragOtherProfile = new FragOtherProfile();
					fragOtherProfile.setData(mEvent.user);
					MainActivity.instance.pushFragment(fragOtherProfile, true);
				}
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent.guestStatus == null || !mEvent.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED)){
					if (((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).getText().toString().equals(getString(R.string.alert_dlg_cancel))){
						MainActivity.instance.onBackPressed();
					}else if (((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).getText().toString().equals(getString(R.string.check_in_event))){
						manualCheckInEvent();
					}
				}else if (((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).getText().toString().equals(getString(R.string.accept_event))){
					acceptEvent();
				}else if (((Button)mRootView.findViewById(R.id.btn_frag_event_detail_action_cancel)).getText().toString().equals(getString(R.string.alert_dlg_cancel))){
					MainActivity.instance.onBackPressed();
				}
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_event_detail_action_invite_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragInviteFriends fragInviteFriends = new FragInviteFriends();
				fragInviteFriends.setEvent(mEvent);
				fragInviteFriends.setCaller(FragEventDetail.this);
				MainActivity.instance.pushFragment(fragInviteFriends, true);
			}
		});
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	public boolean checkRelationBetweenHosterAndMe(){
		if (mEvent.user == null) return false;
		if (Global.GUser == null) return false;
		return mEvent.user.userid == Global.GUser.userid;
	}
	
	public boolean checkUploadable(){
		if (mEvent == null) return true;
		if (mEvent.guestStatus == null) return true;
		return !mEvent.guestStatus.equals(Define.EVENTGUESTSTATUS_INVITED);
	}
	
	private boolean checkEventStarted(long startDateTime){
		return Calendar.getInstance().getTimeInMillis() > startDateTime;
	}
	
	private void manualCheckInEvent(){
		if (mEvent == null) return;
		if (!checkEventStarted(mEvent.startDateTime)){
			displayOKAlertDialog(R.string.alert_dlg_event_detail_event_not_started);
			return;
		}
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_checkingin_event));
		JSONObject param = new JSONObject();
		try{
			param.put(Define.USERINFO_ID, Global.GUser == null ? 0 : Global.GUser.userid);
			param.put(Define.TAG_CHECKIN_TYPE, Define.CHECKINTYPE_MANUAL);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		new CommunicationAPIManager(MainActivity.instance).sendRequestCheckIn(mEvent.id, param, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				if (MainActivity.instance != null){
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							try{
								if (progressDlg != null) progressDlg.dismiss();
								if (responseObj == null){
									String strMessage = String.format(Define.ALERT_DLG_CHECKIN_SUCCESS, mEvent.name);
									if (Global.GArrHooThereEvents != null){
										for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
											if (mEvent.id == Global.GArrHooThereEvents.get(i).id){
												if (Global.GArrHooThereEvents.get(i).statistics == null){
													Global.GArrHooThereEvents.get(i).statistics = new HoothereStatistics();
												}
												if (Global.GArrHooThereEvents.get(i).guestStatus != null && Global.GArrHooThereEvents.get(i).guestStatus.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
													Global.GArrHooThereEvents.get(i).statistics.acceptedCount--;
												}
												Global.GArrHooThereEvents.get(i).statistics.hoothereCount++;
												Global.GArrHooThereEvents.get(i).guestStatus = Define.EVENTGUESTSTATUS_HT;
												mEvent = Global.GArrHooThereEvents.get(i);
												initUI();
												eventHandler();
												break;
											}
										}
									}
									displayOKAlertDialog(strMessage);
								}else{
									if (responseObj.has(Define.ERRORMESSAGE_TAG)){
										String strMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
										if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
											displayOKAlertDialog(String.format(Define.ALERT_DLG_CHECKIN_SUCCESS, mEvent.name));
											if (Global.GArrHooThereEvents != null){
												for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
													if (mEvent.id == Global.GArrHooThereEvents.get(i).id){
														if (Global.GArrHooThereEvents.get(i).statistics == null){
															Global.GArrHooThereEvents.get(i).statistics = new HoothereStatistics();
														}
														if (Global.GArrHooThereEvents.get(i).guestStatus != null && Global.GArrHooThereEvents.get(i).guestStatus.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
															Global.GArrHooThereEvents.get(i).statistics.acceptedCount--;
														}
														Global.GArrHooThereEvents.get(i).statistics.hoothereCount++;
														Global.GArrHooThereEvents.get(i).guestStatus = Define.EVENTGUESTSTATUS_HT;
														mEvent = Global.GArrHooThereEvents.get(i);
														initUI();
														eventHandler();
														break;
													}
												}
											}
										}else{
											displayOKAlertDialog(strMessage);
										}
									}
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void failed(final JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							displayOKAlertDialog(R.string.alert_dlg_checkin_event_error);
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
							displayOKAlertDialog(R.string.alert_dlg_checkin_event_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}		
		});
	}
	
	private void acceptEvent(){
		if (mEvent == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_accepting_invitation));
		new CommunicationAPIManager(MainActivity.instance).sendRequestAcceptEvent(mEvent.id, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
								if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
								mEvent.statistics.acceptedCount++;
								mEvent.statistics.invitedCount--;
								initUI();
								eventHandler();
							}else if (responseObj.has(Define.ERRORMESSAGE_TAG)){
								String strMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
								if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
									displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
									if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
									mEvent.statistics.acceptedCount++;
									mEvent.statistics.invitedCount--;
									initUI();
									eventHandler();
								}else{
									displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
								}
							}else if (responseObj.has(Define.USERINFO_ID)){
								mEvent = new HoothereEvent(responseObj);
								initUI();
								eventHandler();
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
			public void failed(final JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (errorObj != null && errorObj.has(Define.ERRORMESSAGE_TAG)){
								String strMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
								if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
									displayOKAlertDialog(R.string.alert_dlg_accept_event_success);
									if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
									mEvent.statistics.acceptedCount++;
									mEvent.statistics.invitedCount--;
									initUI();
									eventHandler();
								}else{
									displayOKAlertDialog(errorObj.optString(Define.ERRORMESSAGE_TAG));
								}
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
	
	private void joinEvent(){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_joining_event));
		new CommunicationAPIManager(MainActivity.instance).sendRequestJoinEvent(mEvent.id, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_join_event_success);
								if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
								mEvent.statistics.acceptedCount++;
//								mEvent.statistics.invitedCount--;
								initUI();
								eventHandler();
								setFlagReturnToHome(true);
							}else if (responseObj.has(Define.ERRORMESSAGE_TAG)){
								String strMessage = responseObj.optString(Define.ERRORMESSAGE_TAG);
								if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
									displayOKAlertDialog(R.string.alert_dlg_join_event_success);
									if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
									mEvent.statistics.acceptedCount++;
//									mEvent.statistics.invitedCount--;
									initUI();
									eventHandler();
									setFlagReturnToHome(true);
								}else{
									displayOKAlertDialog(strMessage);
								}
							}else{
								mEvent = new HoothereEvent(responseObj);
								initUI();
								eventHandler();
								setFlagReturnToHome(true);
								displayOKAlertDialog(R.string.alert_dlg_join_event_success);
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
								String strMessage = errorObj.optString(Define.ERRORMESSAGE_TAG);
								if (strMessage != null && strMessage.contains(Define.JSONEXCEPTION_SUCCESS)){
									displayOKAlertDialog(R.string.alert_dlg_join_event_success);
									if (mEvent.statistics == null) mEvent.statistics = new HoothereStatistics();
									mEvent.statistics.acceptedCount++;
//									mEvent.statistics.invitedCount--;
									initUI();
									eventHandler();
									setFlagReturnToHome(true);
								}else{
									displayOKAlertDialog(strMessage);
								}
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
	
	private void showActionSheet(){
		ActionSheet.createBuilder(MainActivity.instance, getChildFragmentManager())
					.setCancelButtonTitle(R.string.alert_dlg_cancel)
					.setOtherButtonTitles(getString(R.string.actionsheet_choose_from_library), 
											getString(R.string.actionsheet_take_picture))
					.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		switch(index){
		case 0:
			mTempNewEventAlbumFile = new File(Environment.getExternalStorageDirectory(), Define.TEMP_EVENTALBUM_FILENAME);
			mUriNewEventAlbum = Uri.fromFile(mTempNewEventAlbumFile);
			mTempCropFile = new File(Environment.getExternalStorageDirectory(), Define.TEMP_EVENTALBUM_CROPFILE);
			Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	        startActivityForResult(galleryIntent, Define.ACTION_REQUEST_GALLERY);
			break;
		case 1:
			mTempNewEventAlbumFile = new File(Environment.getExternalStorageDirectory(), Define.TEMP_EVENTALBUM_FILENAME);
			mUriNewEventAlbum = Uri.fromFile(mTempNewEventAlbumFile);
			mTempCropFile = new File(Environment.getExternalStorageDirectory(), Define.TEMP_EVENTALBUM_CROPFILE);
			Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUriNewEventAlbum);
			startActivityForResult(cameraIntent, Define.ACTION_REQUEST_CAMERA);
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		
		case Define.ACTION_REQUEST_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				cropImage(mUriNewEventAlbum);
			}
			break;
		case Define.ACTION_REQUEST_GALLERY:
			if (resultCode == Activity.RESULT_OK) {
				mUriNewEventAlbum = data.getData();
				cropImage(mUriNewEventAlbum);
			}
			break;
		case Define.ACTION_REQUEST_PIC_CROP:
			if (resultCode == Activity.RESULT_OK && data != null) {
				BitmapFactory.Options options = new BitmapFactory.Options();
			    options.inDensity = 72;
			    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				mBmpNewEventAlbum = BitmapFactory.decodeFile(mTempCropFile.getPath(), options);
				if (mTempCropFile != null) mTempCropFile.delete();
				if (mTempNewEventAlbumFile != null) mTempNewEventAlbumFile.delete();
				uploadNewEventAlbum();
			}
			break;
		}
	}
	
	private void cropImage(Uri uri) {
		if (uri == null) return;
		try{
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(uri, "image/*");
			cropIntent.putExtra("crop", "true");
	        cropIntent.putExtra("scale", true);
	        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCropFile));
	        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	        cropIntent.putExtra("return-data", false);
	        startActivityForResult(cropIntent, Define.ACTION_REQUEST_PIC_CROP);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void uploadNewEventAlbum(){
    	if (mBmpNewEventAlbum == null) return;
		byte[] eventAlbumData = null;
        if (mBmpNewEventAlbum != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBmpNewEventAlbum.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            eventAlbumData = stream.toByteArray();
        }
        
        if (eventAlbumData == null) return;
        final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_uploading_eventimage));
        JSONObject json = new JSONObject();
        try {
			json.put(Define.TAG_FILE, eventAlbumData);
			new CommunicationAPIManager(MainActivity.instance).sendUploadNewEventAlbum(eventAlbumData, mEvent == null ? 0 : mEvent.id, "", new NetAPICallBack(){
				@Override
				public void succeed(final JSONObject responseObj) {
					getUpComingEvents(progressDlg);
				}

				@Override
				public void failed(final JSONObject errorObj) {
					MainActivity.instance.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							try{
								if (progressDlg != null) progressDlg.dismiss();
								displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
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
								displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			});
		} catch (JSONException e) {
			MainActivity.instance.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					try{
						if (progressDlg != null) progressDlg.dismiss();
						displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			e.printStackTrace();
		} catch (Exception e) {
			MainActivity.instance.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					try{
						if (progressDlg != null) progressDlg.dismiss();
						displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			e.printStackTrace();
		}
    }
	
	private void getUpComingEvents(final ProgressDialog progressDlg){
		if (Global.GUser == null) return;
		new CommunicationAPIManager(MainActivity.instance).getUpComingEvents(Global.GUser.userid, new NetAPICallBack() {

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (responseObj != null){
								if (responseObj.has(Define.HOO_THERE_EVENTS_ARRAY_TAG)){
									if (Global.GArrHooThereEvents != null) Global.GArrHooThereEvents.clear();
									Global.GArrHooThereEvents = new ArrayList<HoothereEvent>();
									try {
										JSONArray array = responseObj.getJSONArray(Define.HOO_THERE_EVENTS_ARRAY_TAG);
										if (array != null){
											for (int i = 0; i < array.length(); i++){
												HoothereEvent event = new HoothereEvent(array.getJSONObject(i));
												Global.GArrHooThereEvents.add(event);
											}
											Collections.sort(Global.GArrHooThereEvents, new HoothereEventAscComparator());
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								if (Global.GArrHooThereEvents != null){
									for (HoothereEvent event : Global.GArrHooThereEvents){
										if (event.id == mEvent.id){
											mEvent = event;
											break;
										}
									}
								}
							}
							displayEventAlbums();
							if (progressDlg != null) progressDlg.dismiss();
						}catch (Exception e){
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
							displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
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
							displayOKAlertDialog(R.string.alert_dlg_uploading_ea_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}