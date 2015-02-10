package com.sumeet.hoothere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.util.AQUtility;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.GoogleMap;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.comparators.FBFriendComparator;
import com.sumeet.global.Configure;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;
import com.sumeet.hoothere.fragments.FragEvents;
import com.sumeet.hoothere.fragments.FragFriends;
import com.sumeet.hoothere.fragments.FragLogo;
import com.sumeet.hoothere.fragments.FragMyProfile;
import com.sumeet.hoothere.fragments.FragNotifications;
import com.sumeet.hoothere.fragments.FragVerifyNumber;
import com.sumeet.model.ContactMember;
import com.sumeet.model.FBFriend;
import com.sumeet.util.ConnectionDetector;
import com.sumeet.util.GPSTracker;
import com.sumeet.util.GeofenceUtil;
import com.sumeet.util.GetAllContactsInPhone;

public class MainActivity extends FragmentActivity {

	public static MainActivity instance;
	public static Configure config;
	public Fragment m_curFrag;
	public GeofenceUtil mGeofenceUtil;
	public GPSTracker myTracker;
	public static GoogleMap map;
	private int mCurTabIndex = Define.MAIN_FOOTER_TAB_INDEX_NONE;
	
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_main);
		initUI();
		eventHandler();
		loadContacts();
		config = new Configure(getSharedPreferences("hoothere", Context.MODE_PRIVATE));
		if (Global.GArrFBFriends != null){
			Global.GArrFBFriends.clear();
		}
		Global.GArrFBFriends = new ArrayList<FBFriend>();
		JSONArray dataArray = config.getFBFriendsList();
		if (dataArray != null){
			for (int i = 0; i < dataArray.length(); i++){
				try{
					FBFriend fbFriend = new FBFriend(dataArray.getJSONObject(i));
					Global.GArrFBFriends.add(fbFriend);
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
			Collections.sort(Global.GArrFBFriends, new FBFriendComparator());
		}
		myTracker = new GPSTracker(this);
		mGeofenceUtil = new GeofenceUtil(instance);
		ConnectionDetector con = new ConnectionDetector(this);
		if (con.isConnectingToInternet()) {
			if (config.getDeviceToken() == "") {
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);

				final String regId = GCMRegistrar.getRegistrationId(this);

				if (regId.equals("")) {
					GCMRegistrar.register(this, WebConfig.GCM_APP_ID);
				} else {
					Global.deviceToken = regId;
					config.saveDeviceToken(regId);
				}
			} else {
				Global.deviceToken = config.getDeviceToken();
			}
		} else {
			Builder alertDialogBuilder = new Builder(this);
			alertDialogBuilder
					.setCancelable(false)
					.setMessage(getString(R.string.alert_dlg_no_internet_connection))
					.setPositiveButton(getString(R.string.alert_dlg_btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 10);
						}
					});

			alertDialogBuilder.create().show();
		}
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if (config.checkIfLoggedIn()){
							Global.GUser = config.getUserLoggedInfo();
							if (Global.GUser == null){
								getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragLogo()).commit();
							}else{
								if (Global.GUser.activationStatus == null){
									getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragLogo()).commit();
								}else if (Global.GUser.activationStatus.equals(Define.EVENTGUESTSTATUS_ACCEPTED)){
									getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragEvents()).commit();
								}else if (Global.GUser.activationStatus.equals(Define.EVENTGUESTSTATUS_PENDING)){
									if (Global.GUser.toVerify){
										getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragVerifyNumber()).commit();
									}else{
										getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragEvents()).commit();
									}
								}
							}
						}else{
							getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragLogo()).commit();
						}
						findViewById(R.id.rl_main_logo).setVisibility(View.GONE);
					}
				});
			}
		}, 2500);
	}
	
	@Override
	public void onDestroy(){
		AQUtility.cleanCacheAsync(this);
		super.onDestroy();
		instance = null;
	}
	
	private void loadContacts(){
		GetAllContactsInPhone getAllContacts = new GetAllContactsInPhone(MainActivity.instance);
		Global.GArrContacts = new ArrayList<ContactMember>();
		getAllContacts.execute((Void[])null);
	}
	
	public void startNewFramgent(Fragment fragment, boolean bAdd){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
		ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_left, R.anim.exit_to_right);
		ft.replace(R.id.container, fragment);     
		if (bAdd) {
			ft.addToBackStack(null);	
		}
		ft.commit();
	}
	
    public void pushFragment(Fragment fragment, boolean bAdd) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
		ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
		ft.replace(R.id.container, fragment);     
		if (bAdd) {
			ft.addToBackStack(null);	
		}
		ft.commit();
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (null != m_curFrag)
            m_curFrag.onActivityResult(requestCode, resultCode, data);
    }
    
    public void locationChanged(Location location){
    	if (mGeofenceUtil == null){
    		mGeofenceUtil = new GeofenceUtil(instance);
    	}
    	mGeofenceUtil.setCurrentLocation(location);
    	mGeofenceUtil.checkEvents();
    }
    
    public static void setBadge(Context context, int count){
    	if (context == null) return;
    	String launcherClassName = getLauncherClassName(context);
    	if (launcherClassName == null) return;
    	Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
    	intent.putExtra("badge_count", count);
    	intent.putExtra("badge_count_package_name", context.getPackageName());
    	intent.putExtra("badge_count_class_name", launcherClassName);
    	context.sendBroadcast(intent);
    }
    
    public static String getLauncherClassName(Context context){
    	PackageManager pm = context.getPackageManager();
    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
    	for (ResolveInfo resolveInfo : resolveInfos){
    		String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
    		if (pkgName.equalsIgnoreCase(context.getPackageName()))
    			return resolveInfo.activityInfo.name;
    	}
    	return null;
    }
    
    private void initUI(){
    	
    }
    
    private void eventHandler(){
    	findViewById(R.id.ll_main_tabbar_footer_events).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_EVENTS);
			}
		});
    	
    	findViewById(R.id.ll_main_tabbar_footer_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_FRIENDS);
			}
		});    	

    	findViewById(R.id.ll_main_tabbar_footer_notifications).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_NOTIFICATIONS);
			}
		});    	

    	findViewById(R.id.ll_main_tabbar_footer_me).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFooterTabs(Define.MAIN_FOOTER_TAB_INDEX_ME);
			}
		});    	
    }
    
    public void handleFooterTabs(int nIndex){
    	switch(nIndex){
    	case Define.MAIN_FOOTER_TAB_INDEX_EVENTS:
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_events_icon)).setImageResource(R.drawable.events_selected);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_events_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_purple));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_friends_icon)).setImageResource(R.drawable.friends);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_friends_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_notifications_icon)).setImageResource(R.drawable.notifications);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_notifications_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_me_icon)).setImageResource(R.drawable.me);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_me_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		if (mCurTabIndex == nIndex){
    			return;
    		}
    		mCurTabIndex = nIndex;
    		startNewFramgent(new FragEvents(), false);
    		break;
    		
    	case Define.MAIN_FOOTER_TAB_INDEX_FRIENDS:
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_events_icon)).setImageResource(R.drawable.events);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_events_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_friends_icon)).setImageResource(R.drawable.friends_selected);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_friends_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_purple));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_notifications_icon)).setImageResource(R.drawable.notifications);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_notifications_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_me_icon)).setImageResource(R.drawable.me);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_me_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		if (mCurTabIndex == nIndex){
    			return;
    		}
    		mCurTabIndex = nIndex;
    		startNewFramgent(new FragFriends(), false);
    		break;

    	case Define.MAIN_FOOTER_TAB_INDEX_NOTIFICATIONS:
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_events_icon)).setImageResource(R.drawable.events);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_events_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_friends_icon)).setImageResource(R.drawable.friends);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_friends_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_notifications_icon)).setImageResource(R.drawable.notifications_selected);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_notifications_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_purple));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_me_icon)).setImageResource(R.drawable.me);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_me_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		if (mCurTabIndex == nIndex){
    			return;
    		}
    		mCurTabIndex = nIndex;
    		startNewFramgent(new FragNotifications(), false);
    		break;
    		
    	case Define.MAIN_FOOTER_TAB_INDEX_ME:
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_events_icon)).setImageResource(R.drawable.events);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_events_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_friends_icon)).setImageResource(R.drawable.friends);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_friends_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_notifications_icon)).setImageResource(R.drawable.notifications);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_notifications_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_text_dark_gray_666666));
    		((ImageView)findViewById(R.id.iv_main_tabbar_footer_me_icon)).setImageResource(R.drawable.me_selected);
    		((TextView)findViewById(R.id.txt_main_tabbar_footer_me_title)).setTextColor(MainActivity.instance.getResources().getColor(R.color.color_purple));
    		if (mCurTabIndex == nIndex){
    			return;
    		}
    		mCurTabIndex = nIndex;
    		FragMyProfile fragMyProfile = new FragMyProfile();
    		fragMyProfile.setFlagFromMainMenu(true);
    		startNewFramgent(fragMyProfile, false);
    		break;
    	}
    }
    
    public void setFooterVisibility(int nVisibility){
    	findViewById(R.id.ll_main_tabbar_footer).setVisibility(nVisibility);
    	findViewById(R.id.rl_main_tabbar_divider).setVisibility(nVisibility);
    }
    
    public void hideBadge(){
    	findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
    }
    
	public void displayNotificationBadge(){
		if (Global.GUser == null){
			findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
			return;
		}
		new CommunicationAPIManager(this).getNumberOfNotifications(Global.GUser.userid, new NetAPICallBack(){
			@Override
			public void succeed(final JSONObject responseObj) {
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							int nCount = 0;
							if (responseObj != null && responseObj.has(Define.NOTIFICATION_COUNT)){
								try {
									String strCount = responseObj.getString(Define.NOTIFICATION_COUNT);
									if (strCount != null && !strCount.equals("") && !strCount.equals("null") && Integer.parseInt(strCount) > 0){
										nCount = Integer.parseInt(strCount);
										findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.VISIBLE);
										((TextView)findViewById(R.id.txt_main_tabbar_footer_notification_badge)).setText(strCount);
									}else{
										findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
									}
								} catch (JSONException e) {
									e.printStackTrace();								
								}catch(NumberFormatException e1){
									e1.printStackTrace();
								}
							}else{
								findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
							}
							setBadge(MainActivity.instance, nCount);
						}catch(Exception e){
							e.printStackTrace();
							findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
							setBadge(MainActivity.instance, 0);
						}
					}
				});
			}

			@Override
			public void failed(JSONObject errorObj) {
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
							setBadge(MainActivity.instance, 0);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void failed(VolleyError error) {
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						try{
							findViewById(R.id.rl_main_tabbar_footer_notification_badge).setVisibility(View.GONE);	
							setBadge(MainActivity.instance, 0);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}
