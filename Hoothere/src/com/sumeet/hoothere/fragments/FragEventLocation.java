package com.sumeet.hoothere.fragments;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Define;
import com.sumeet.hoothere.AlertDlgCallback;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.util.AlertDialogManager;

public class FragEventLocation extends Fragment{
	
	private View mRootView;
	private HoothereEvent mEvent;
	private double mLat;
	private double mLng;
	private SeekBar mSbRange;
	private boolean mFlagEditable = true;
	private boolean mFlagCreateOrEdit = false;
	private Fragment mCaller = null;
	
	private static final int[] GMAP_TYPE = {GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_TERRAIN};
	private int mMapType = GMAP_TYPE[0];
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView != null) {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) parent.removeView(mRootView);
		}
		try {
			mRootView = inflater.inflate(R.layout.fragment_event_location_map, container, false);
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
		handleEditable();
		if (mFlagEditable) handleRange();
	}
	
	public void setCreateOrEdit(boolean bFlagCreate){
		mFlagCreateOrEdit = bFlagCreate;
	}
	
	public void setCaller(Fragment fragment){
		mCaller = fragment;
	}
	
	private void displayOKAlertDialog(int resId){
		AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere), 
																				getString(resId), 
																				getString(R.string.alert_dlg_btn_ok), 
																				null, 
																				null);
	}
	
	private void initUI(){
		((TextView)mRootView.findViewById(R.id.txt_frag_event_location_map_header_title)).setText(mEvent == null || mEvent.name == null ? "" : mEvent.name);
		mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_current).setAlpha(0.8f);
		mSbRange = (SeekBar)mRootView.findViewById(R.id.sb_frag_event_location_map_range);
		mSbRange.setMax(Define.RANGE_MAX_VAL);
		try{
			mSbRange.setProgress(mEvent.radius == null || mEvent.radius.isEmpty() || mEvent.radius.equals("null") ? Define.RANGE_MIN_VAL : (int)Double.parseDouble(mEvent.radius));
		}catch(NumberFormatException e){
			e.printStackTrace();
			mSbRange.setProgress(Define.RANGE_MIN_VAL);
		}
		initMap(true, true);
	}
	
	public void setEditable(boolean bFlag){
		mFlagEditable = bFlag;
	}
	
	private void handleEditable(){
		if (mFlagEditable) return;
		mRootView.findViewById(R.id.sb_frag_event_location_map_range).setVisibility(View.GONE);
		mRootView.findViewById(R.id.ll_frag_event_location_map_actions).setVisibility(View.GONE);
		mRootView.findViewById(R.id.rl_frag_event_location_map_divider).setVisibility(View.GONE);
	}
	
	private void eventHandler(){
		mSbRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress < Define.RANGE_MIN_VAL) mSbRange.setProgress(Define.RANGE_MIN_VAL);
				handleRange();
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_current).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRootView.findViewById(R.id.ll_frag_event_location_map_type).getVisibility() == View.VISIBLE){
					mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
				}else{
					mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.VISIBLE);
				}
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_hybrid).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_location_map_type_current)).setText(getString(R.string.map_type_hybrid));
				MainActivity.map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_terrain).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_location_map_type_current)).setText(getString(R.string.map_type_terrain));
				MainActivity.map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_satellite).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_location_map_type_current)).setText(getString(R.string.map_type_satellite));
				MainActivity.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}
		});
		
		mRootView.findViewById(R.id.rl_frag_event_location_map_type_normal).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRootView.findViewById(R.id.ll_frag_event_location_map_type).setVisibility(View.GONE);
				((TextView)mRootView.findViewById(R.id.txt_frag_event_location_map_type_current)).setText(getString(R.string.map_type_normal));
				MainActivity.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		});
		
		mRootView.findViewById(R.id.iv_frag_event_location_map_header_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.instance.onBackPressed();
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_event_location_map_actions_skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialogManager.sharedManager(MainActivity.instance).showConfirmDialog(getString(R.string.alert_dlg_title_hoothere),
																						getString(R.string.alert_dlg_location_map_skip), 
																						getString(R.string.alert_dlg_btn_ok), 
																						getString(R.string.alert_dlg_cancel), 
																						new AlertDlgCallback() {
																							@Override
																							public void onOK(){
																								if (mFlagCreateOrEdit == false){
																									sendCreateEventRequest();
																								}else{
																									MainActivity.instance.onBackPressed();
																								}
																							}
																							
																							@Override
																							public void onCancel() {
																								
																							}
																						});
			}
		});
		
		mRootView.findViewById(R.id.btn_frag_event_location_map_actions_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEvent == null) mEvent = new HoothereEvent();
				mEvent.radius = String.format("%d", mSbRange.getProgress());
				mEvent.latitude = Double.toString(mLat);
				mEvent.longitude = Double.toString(mLng);
				if (mFlagCreateOrEdit == false){
					sendCreateEventRequest();
				}else{
					if (mCaller != null && mCaller instanceof FragEditEvent){
						((FragEditEvent)mCaller).setEvent(mEvent);
					}
					MainActivity.instance.onBackPressed();
				}
			}
		});
	}
	
	public void setEvent(HoothereEvent event){
		mEvent = event;
	}
	
	private void sendCreateEventRequest(){
		final ProgressDialog progressDlg = ProgressDialog.show(MainActivity.instance, Define.PROGRESS_DLG_TITLE, getString(R.string.progress_creating_event));
		new CommunicationAPIManager(MainActivity.instance).sendRequestCreateEvent(mEvent == null ? new JSONObject() : mEvent.asJSON(), new NetAPICallBack(){

			@Override
			public void succeed(final JSONObject responseObj) {
				MainActivity.instance.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							if (progressDlg != null) progressDlg.dismiss();
							if (responseObj == null){
								displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
							}else{
								HoothereEvent event = new HoothereEvent(responseObj);
								FragEventDetail fragEventDetail = new FragEventDetail();
								fragEventDetail.setFlagReturnToHome(true);
								fragEventDetail.setEvent(event);
								MainActivity.instance.pushFragment(fragEventDetail, true);
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
							displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
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
							displayOKAlertDialog(R.string.alert_dlg_manual_location_wrong_address);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	
	private void handleRange(){
		if (MainActivity.map != null){
			mLat = MainActivity.map.getCameraPosition().target.latitude;
			mLng = MainActivity.map.getCameraPosition().target.longitude;
			MainActivity.map.clear();
			CircleOptions circle = new CircleOptions();
			circle.center(new LatLng(mLat, mLng));
			circle.radius(mSbRange == null ? 0 : mSbRange.getProgress());
			circle.fillColor(getResources().getColor(R.color.color_geofence_338E44AD));
			circle.strokeColor(getResources().getColor(R.color.color_geofence_888E44AD));
			circle.strokeWidth(Define.GEOFENCE_STROKE_WIDTH);
			MainActivity.map.addCircle(circle);
		}
	}
	
	public void initMap(boolean bMove, boolean bFirst) {
		FragmentManager myFM = getChildFragmentManager();
		SupportMapFragment myMAPF = (SupportMapFragment) myFM.findFragmentById(R.id.map_frag_event_location_map_destination);
		
		MainActivity.map = myMAPF.getMap();
		MainActivity.map.clear();
		MainActivity.map.getUiSettings().setZoomControlsEnabled(false);
		MainActivity.map.setMapType(mMapType);
		mapEventHandler();
		mLat = 0.0f;
		mLng = 0.0f;
		if (mEvent == null) return;
		if (mEvent.latitude != null && !mEvent.latitude.isEmpty() && !mEvent.latitude.equals("null")){
			try{
				mLat = Double.parseDouble(mEvent.latitude);
			}catch(NumberFormatException e){
				e.printStackTrace();
				mLat = 0.0f;
			}
		}
		if (mEvent.longitude != null && !mEvent.longitude.isEmpty() && !mEvent.longitude.equals("null")){
			try{
				mLng = Double.parseDouble(mEvent.longitude);
			}catch(NumberFormatException e){
				e.printStackTrace();
				mLng = 0.0f;
			}
		}
		
		if (!mFlagEditable){
			mRootView.findViewById(R.id.iv_map_frag_event_location_map_destination_marker).setVisibility(View.GONE);
			MarkerOptions options = new MarkerOptions();
			options.position(new LatLng(mLat, mLng));
			options.title(mEvent.venueName == null || mEvent.venueName.equals("null") ? "" : mEvent.venueName);
			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_pin_purple_small));
			MainActivity.map.addMarker(options);
			CircleOptions circle = new CircleOptions();
			circle.center(new LatLng(mLat, mLng));
			circle.radius(mSbRange == null ? 0 :mSbRange.getProgress());
			circle.fillColor(getResources().getColor(R.color.color_geofence_338E44AD));
			circle.strokeColor(getResources().getColor(R.color.color_geofence_888E44AD));
			circle.strokeWidth(Define.GEOFENCE_STROKE_WIDTH);
			MainActivity.map.addCircle(circle);
		}
		
		if (bMove) {
			animateMap();
		}
	}
	
	private void mapEventHandler(){
		if (MainActivity.map == null) return;
		MainActivity.map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition camera) {
				if (!mFlagEditable) return;
				handleRange();
			}
		});
	}
	
	private void animateMap() {
		animate(mLat, mLng, 17);
	}
	
	private void animate(double x, double y, int zoom) {
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(x, y)).tilt(45).zoom(zoom).build();
		MainActivity.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
}