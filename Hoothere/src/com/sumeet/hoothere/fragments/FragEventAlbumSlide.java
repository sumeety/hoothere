package com.sumeet.hoothere.fragments;

import java.io.File;
import java.io.OutputStream;

import org.json.JSONObject;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.EventAlbum;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.ActionSheet;
import com.sumeet.util.ActionSheet.ActionSheetListener;
import com.sumeet.util.AlertDialogManager;

public class FragEventAlbumSlide extends Fragment implements ActionSheetListener{
	
	private View mRootView;
	private HoothereEvent mEvent = null;
	private EventAlbum mInitialAlbum = null;
	private ViewFlow mVFEventAlbum;
	private EventAlbumSlideAdapter mEventSlideAdapter;
	private boolean mFlagHosterIsMe = false;
	private boolean mFlagUploaderIsMe = false;
	private Bitmap mCurrentBitmap = null;
	private File mTempSaveFile = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_eventalbums, container, false);
			initData();
			initUI();
			eventHandler();
		}catch(InflateException e){
			e.printStackTrace();
		}
		return mRootView;
	}
	
	private void initData(){
		if (mEvent != null && mInitialAlbum == null && mEvent.eventAlbum != null && mEvent.eventAlbum.size() > 0){
			mInitialAlbum = mEvent.eventAlbum.get(0);
		}
		if (mEvent != null && Global.GUser != null){
			mFlagHosterIsMe = mEvent.user.userid == Global.GUser.userid;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		MainActivity.instance.setFooterVisibility(View.GONE);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		MainActivity.instance.setFooterVisibility(View.VISIBLE);
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
		((TextView)mRootView.findViewById(R.id.txt_frag_eventalbums_header_title)).setText(mEvent == null || mEvent.name == null || mEvent.name.equals("null") ? "" : mEvent.name);
		displayEventAlbumInfo(mInitialAlbum);
		mVFEventAlbum = (ViewFlow)mRootView.findViewById(R.id.vf_frag_eventalbums_content);
		mEventSlideAdapter = new EventAlbumSlideAdapter(MainActivity.instance, mEvent);
		mVFEventAlbum.setAdapter(mEventSlideAdapter);
		CircleFlowIndicator indic = (CircleFlowIndicator) mRootView.findViewById(R.id.vfi_frag_eventalbums_content);
		mVFEventAlbum.setFlowIndicator(indic);
		int nIndex = 0;
		if (mEvent != null && mEvent.eventAlbum != null && mInitialAlbum != null){
			nIndex = mEvent.eventAlbum.indexOf(mInitialAlbum);
		}
		mVFEventAlbum.setSelection(nIndex);
	}

	private void eventHandler(){
		mRootView.findViewById(R.id.iv_frag_eventalbums_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_eventalbums_header_action).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showActionSheet();
			}
		});
		
		mVFEventAlbum.setOnViewSwitchListener(new ViewFlow.ViewSwitchListener() {
			@Override
			public void onSwitched(View view, int position) {
				mCurrentBitmap = null;
				displayEventAlbumInfo(getCurrentSelectedEventAlbum());
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_eventalbums_info_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventAlbum ea = getCurrentSelectedEventAlbum();
				if (ea == null) return;
				if (ea.user == null) return;
				if (Global.GUser == null) return;
				if (ea.user.userid == Global.GUser.userid){
					MainActivity.instance.pushFragment(new FragMyProfile(), true);
				}else{
					FragOtherProfile fragOtherProfile = new FragOtherProfile();
					fragOtherProfile.setData(ea.user);
					MainActivity.instance.pushFragment(fragOtherProfile, true);
				}
			}
		});
		
		mRootView.findViewById(R.id.txt_frag_eventalbums_info_name).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventAlbum ea = getCurrentSelectedEventAlbum();
				if (ea == null) return;
				if (ea.user == null) return;
				if (Global.GUser == null) return;
				if (ea.user.userid == Global.GUser.userid){
					MainActivity.instance.pushFragment(new FragMyProfile(), true);
				}else{
					FragOtherProfile fragOtherProfile = new FragOtherProfile();
					fragOtherProfile.setData(ea.user);
					MainActivity.instance.pushFragment(fragOtherProfile, true);
				}
			}
		});
	}
	
	private void displayEventAlbumInfo(EventAlbum ea){
		if (ea == null){
			((TextView)mRootView.findViewById(R.id.txt_frag_eventalbums_info_name)).setText("");
			((TextView)mRootView.findViewById(R.id.txt_frag_eventalbums_info_time)).setText(getString(R.string.date_not_specified));
			((ImageView)mRootView.findViewById(R.id.iv_frag_eventalbums_info_avatar)).setImageResource(0);
		}else{
			((TextView)mRootView.findViewById(R.id.txt_frag_eventalbums_info_name)).setText(ea.user == null 
					|| ea.user.fullName == null || ea.user.fullName.equals("null") ? "" : ea.user.fullName);
			String strUploadedDate = String.format(Define.DT_FORMAT_ALL, DateFormat.format(Define.DT_FORMAT_DATE, ea.uploadedOn), DateFormat.format(Define.DT_FORMAT_TIME, ea.uploadedOn));
			((TextView)mRootView.findViewById(R.id.txt_frag_eventalbums_info_time)).setText(ea.uploadedOn == 0 ? getString(R.string.date_not_specified) : strUploadedDate);
			ImageView ivUploaderAvatar = (ImageView)mRootView.findViewById(R.id.iv_frag_eventalbums_info_avatar);
			AQuery aq = new AQuery(ivUploaderAvatar);
			ImageOptions imageOption = new ImageOptions();
			imageOption.animation = AQuery.FADE_IN_FILE;
			imageOption.memCache = true;
			imageOption.fileCache = false;
			imageOption.targetWidth = 100;
			imageOption.preset = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
			imageOption.fallback = R.drawable.defaultpic;
			aq.image(String.format(Define.USER_AVATAR_THUMBNAIL_URL, WebConfig.BASE_URL, ea.user == null ? 0 : ea.user.userid), imageOption);
			mFlagUploaderIsMe = ea.user == null || Global.GUser == null ? false : ea.user.userid == Global.GUser.userid;
		}
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	public void setInitalEventAlbum(EventAlbum ea){
		mInitialAlbum = ea;
	}
	
	private EventAlbum getCurrentSelectedEventAlbum(){
		if (mEventSlideAdapter == null) return null;
		return (EventAlbum)mEventSlideAdapter.getItem(mVFEventAlbum.getSelectedItemPosition());
	}
	
	private int getCurrentSelectedEventAlbumIndex(){
		if (mEventSlideAdapter == null) return -1;
		return mVFEventAlbum.getSelectedItemPosition();
	}
	
	private View getCurrentSelectedEventAlbumView(){
		if (mEventSlideAdapter == null) return null;
		return mVFEventAlbum.getSelectedView();
	}

	private void showActionSheet(){
		if (mFlagHosterIsMe){
			ActionSheet.createBuilder(MainActivity.instance, getChildFragmentManager())
						.setCancelButtonTitle(R.string.alert_dlg_cancel)
						.setOtherButtonTitles(getString(R.string.actionsheet_save_image), 
												getString(R.string.actionsheet_share_image),
												getString(R.string.actionsheet_set_as_eventimage),
												getString(R.string.actionsheet_delete_image))
						.setCancelableOnTouchOutside(true).setListener(this).show();
		}else if(mFlagUploaderIsMe){
			ActionSheet.createBuilder(MainActivity.instance, getChildFragmentManager())
						.setCancelButtonTitle(R.string.alert_dlg_cancel)
						.setOtherButtonTitles(getString(R.string.actionsheet_save_image), 
												getString(R.string.actionsheet_share_image),
												getString(R.string.actionsheet_delete_image))
						.setCancelableOnTouchOutside(true).setListener(this).show();
		}else{
			ActionSheet.createBuilder(MainActivity.instance, getChildFragmentManager())
						.setCancelButtonTitle(R.string.alert_dlg_cancel)
						.setOtherButtonTitles(getString(R.string.actionsheet_save_image), 
												getString(R.string.actionsheet_share_image))
						.setCancelableOnTouchOutside(true).setListener(this).show();
		}
	}
	
	private void deleteEventAlbum(){
		final EventAlbum currentEA = getCurrentSelectedEventAlbum();
		if (currentEA == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_deleting));
		new CommunicationAPIManager(MainActivity.instance).sendRequestDeleteEventAlbumImage(mEvent.id, currentEA.name, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (progressDlg != null) progressDlg.dismiss();
					}
				});
			}

			@Override
			public void failed(final JSONObject errorObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
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
								if (error.getMessage() != null && error.getMessage().equals(Define.JSONEXCEPTION_SUCCESS)){
									if (mEvent.eventAlbum == null){
										MainActivity.instance.onBackPressed();
										return;
									}
									if (mEvent.eventAlbum.size() <= 1){
										mEvent.eventAlbum.clear();
										MainActivity.instance.onBackPressed();
										return;
									}
									int nDeletedIndex = getCurrentSelectedEventAlbumIndex();
									if (nDeletedIndex == mEvent.eventAlbum.size() - 1){
										mEvent.eventAlbum.remove(nDeletedIndex);
										mInitialAlbum = mEvent.eventAlbum.get(mEvent.eventAlbum.size() - 1);
									}else{
										mEvent.eventAlbum.remove(nDeletedIndex);
										mInitialAlbum = getCurrentSelectedEventAlbum();
									}
									initUI();
								}else if (error.getMessage() == null){
									displayOKAlertDialog(R.string.alert_dlg_event_album_delete_error);
								}else{
									displayOKAlertDialog(error.getMessage());
								}
							}else{
								displayOKAlertDialog(R.string.alert_dlg_event_album_delete_error);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void showDeleteAlert(){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(R.string.alert_dlg_event_album_delete_confirm), 
																				getString(R.string.alert_dlg_ya_sure), 
																				getString(R.string.alert_dlg_cancel), 
																				new AlertDlgCallback() {
																					@Override
																					public void onOK() {
																						deleteEventAlbum();
																					}
																					
																					@Override
																					public void onCancel() {
																						
																					}
																				});
	}
	
	private void setAsEventImage(){
		EventAlbum currentEA = getCurrentSelectedEventAlbum();
		if (currentEA == null) return;
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_setting_as_event_image));
		new CommunicationAPIManager(MainActivity.instance).sendRequestSetAsEventAlbumImage(mEvent.id, currentEA.name, new NetAPICallBack() {
			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_event_album_set_as_success);
							}else if (responseObj.has(Define.ERRORMESSAGE_TAG)){
								displayOKAlertDialog(responseObj.optString(Define.ERRORMESSAGE_TAG));
							}else{
								displayOKAlertDialog(R.string.alert_dlg_event_album_set_as_error);
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
							displayOKAlertDialog(R.string.alert_dlg_event_album_set_as_error);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
			
			@Override
			public void failed(final JSONObject errorObj){
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (errorObj != null && errorObj.has(Define.ERRORMESSAGE_TAG) && errorObj.optString(Define.ERRORMESSAGE_TAG) != null && 
								errorObj.optString(Define.ERRORMESSAGE_TAG).contains(Define.JSONEXCEPTION_SUCCESS)){
								displayOKAlertDialog(R.string.alert_dlg_event_album_set_as_success);
							}else{
								displayOKAlertDialog(R.string.alert_dlg_event_album_set_as_error);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void saveImageToGallery(){
		try{
			ViewGroup currentView = (ViewGroup)getCurrentSelectedEventAlbumView(); 
			if (currentView == null) return;
			if (currentView.getChildCount() == 0) return;
			if (!(currentView.getChildAt(0) instanceof RelativeLayout)) return;
			if (((ViewGroup)currentView.getChildAt(0)).getChildCount() == 0 || !(((ViewGroup)currentView.getChildAt(0)).getChildAt(0) instanceof ImageView)) return;
			((ViewGroup)currentView.getChildAt(0)).getChildAt(0).buildDrawingCache();
			String strUri = MediaStore.Images.Media.insertImage(MainActivity.instance.getContentResolver(), 
					mCurrentBitmap == null ? ((ViewGroup)currentView.getChildAt(0)).getChildAt(0).getDrawingCache() : mCurrentBitmap, "", "");
			if (strUri == null){
				displayOKAlertDialog(R.string.alert_dlg_event_album_save_error);
			}else{
				displayOKAlertDialog(R.string.alert_dlg_event_album_save_success);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
		
	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		if (mFlagHosterIsMe){
			switch(index){
			case 0://Save Image
				saveImageToGallery();
				break;
			case 1://Share Image
				shareImage();
				break;
			case 2://Set as event image
				setAsEventImage();
				break;
			case 3://Delete image
				showDeleteAlert();
				break;
			}
		}else if(mFlagUploaderIsMe){
			switch(index){
			case 0://Save Image
				saveImageToGallery();
				break;
			case 1://Share Image
				shareImage();
				break;
			case 2://Delete Image
				showDeleteAlert();
				break;
			}
		}else{
			switch(index){
			case 0://Save Image
				saveImageToGallery();
				break;
			case 1://Share Image
				shareImage();
				break;
			}
		}
	}
	
	private void shareImage(){
		Bitmap tempBmp = mCurrentBitmap;
		if (tempBmp == null){
			ViewGroup currentView = (ViewGroup)getCurrentSelectedEventAlbumView(); 
			if (currentView == null) return;
			if (currentView.getChildCount() == 0) return;
			if (!(currentView.getChildAt(0) instanceof RelativeLayout)) return;
			if (((ViewGroup)currentView.getChildAt(0)).getChildCount() == 0 || !(((ViewGroup)currentView.getChildAt(0)).getChildAt(0) instanceof ImageView)) return;
			((ViewGroup)currentView.getChildAt(0)).getChildAt(0).buildDrawingCache();
			tempBmp = ((ViewGroup)currentView.getChildAt(0)).getChildAt(0).getDrawingCache();
		}
			
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, "title");
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		Uri tempUri = MainActivity.instance.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
		if (tempUri == null){
			displayOKAlertDialog(R.string.alert_dlg_share_image_error);
			return;
		}
		OutputStream outStream;
		try{
			outStream = MainActivity.instance.getContentResolver().openOutputStream(tempUri);
			tempBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.close();
		}catch(Exception e){
			e.printStackTrace();
			displayOKAlertDialog(R.string.alert_dlg_share_image_error);
			return;
		}
		mTempSaveFile = new File(tempUri.getPath());
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_STREAM, tempUri);
		startActivityForResult(intent, Define.ACTION_SHARE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Define.ACTION_SHARE:
			if (mTempSaveFile != null) mTempSaveFile.delete();
			mTempSaveFile = null;
			break;
		}
	}
	
	public class EventAlbumSlideAdapter extends BaseAdapter{

		private Context mCtx;
		private HoothereEvent mSlideEvent;
		
		public EventAlbumSlideAdapter(Context ctx, HoothereEvent event){
			mCtx = ctx;
			mSlideEvent = event;
		}
		
		@Override
		public int getCount() {
			if (mSlideEvent == null || mSlideEvent.eventAlbum == null) return 0;
			return mSlideEvent.eventAlbum.size();
		}

		@Override
		public Object getItem(int position) {
			if (mSlideEvent == null || mSlideEvent.eventAlbum == null || position >= mSlideEvent.eventAlbum.size()) return null;
			return mSlideEvent.eventAlbum.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rootView = convertView;
			EventSlideViewHolder viewHolder;
			EventAlbum ea = (EventAlbum)getItem(position);
			if (ea == null) return null;

			if (rootView == null){
				rootView = LayoutInflater.from(mCtx).inflate(R.layout.item_eventalbum_slide, null);
				viewHolder = new EventSlideViewHolder();
				viewHolder.ivSlide = (ImageView)rootView.findViewById(R.id.iv_item_eventalbum_slide);
				rootView.setTag(viewHolder);
			}else{
				viewHolder = (EventSlideViewHolder)rootView.getTag();
			}
			
			new AQuery(viewHolder.ivSlide).image(ea.imageUrl, true, true, 1200, 0, new BitmapAjaxCallback(){
	            @Override
	            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
	            	mCurrentBitmap = bm;
	            	iv.setImageBitmap(bm);
	            }
			});
			return rootView;
		}
		
		class EventSlideViewHolder{
			ImageView ivSlide;			
		}
	}
}