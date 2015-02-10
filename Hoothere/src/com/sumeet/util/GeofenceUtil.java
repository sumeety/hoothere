package com.sumeet.util;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.communication.CommunicationAPIManager;
import com.sumeet.global.Global;
import com.sumeet.hoothere.MainActivity;
import com.sumeet.hoothere.R;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.HoothereStatistics;

public class GeofenceUtil {
	private Context mContext;
	private Location mCurrentLocation;
	
	public static final double M_PI = 3.14159265358979323846264338327950288;
	private static int mNotificationId = 1;
	
	public GeofenceUtil(Context ctx){
		mContext = ctx;
	}
	
	public void setCurrentLocation(Location location){
		mCurrentLocation = location;
	}
	
	private double getDistanceBetween2Points(double lat1, double lng1, double lat2, double lng2){
	    int nRadius = 6371;
	    double latDiff = (lat1 - lat2) * (M_PI / 180);
	    double lonDiff = (lng1 - lng2) * (M_PI / 180);
	    double lat1InRadians = lat1 * (M_PI / 180);
	    double lat2InRadians = lat2 * (M_PI / 180);
	    double nA = Math.pow(Math.sin(latDiff / 2), 2) + Math.cos(lat1InRadians) * Math.cos(lat2InRadians) * Math.pow(Math.sin(lonDiff / 2), 2 );
	    double nC = 2 * Math.atan2(Math.sqrt(nA), Math.sqrt(1 - nA));
	    double nD = nRadius * nC;
	    return nD * 1000;
	}

	private boolean checkIfEventIsValid(long time1, long time2){
		Calendar cal = Calendar.getInstance();
		if (time1 <= cal.getTimeInMillis() && time2 >= cal.getTimeInMillis()) return true;
		return false;
	}
	
	private ArrayList<HoothereEvent> getValidEvents(){
		ArrayList<HoothereEvent> result = new ArrayList<HoothereEvent>();
		if (Global.GArrHooThereEvents == null) return result;
		for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
			if (checkIfEventIsValid(Global.GArrHooThereEvents.get(i).startDateTime, Global.GArrHooThereEvents.get(i).endDateTime)){
				result.add(Global.GArrHooThereEvents.get(i));
			}
		}
		return result;
	}
	
	public void checkEvents(){
		ArrayList<HoothereEvent> availableEvents = getValidEvents();
		for (int i = 0; i < availableEvents.size(); i++){
			if (checkInEvent(availableEvents.get(i))) continue;
			checkOutEvent(availableEvents.get(i));
		}
	}
	
	private void buildNotification(String message){
		Intent passIntent;
		passIntent = new Intent(MainActivity.instance, MainActivity.class);
		passIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.instance, 0, passIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager mManager = (NotificationManager) MainActivity.instance.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(MainActivity.instance)
				.setContentTitle("Hoothere")
				.setContentText(message)
				.setSmallIcon(R.drawable.ic_launcher)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
				.setAutoCancel(true);
		mNotifyBuilder.setContentIntent(contentIntent);
		mManager.notify(mNotificationId++, mNotifyBuilder.build());		
	}
	
	public boolean checkInEvent(final HoothereEvent event){
		if (event == null) return false;
		String rad = event.radius == null || event.radius.equals("null") || event.radius.equals("") ? "0" : event.radius;
		double radius = Double.parseDouble(rad);
		double lat1 = event.latitude == null || event.latitude.equals("null") || event.latitude.isEmpty() ? 0.0f : Double.parseDouble(event.latitude);
		double lng1 = event.longitude == null || event.longitude.equals("null") || event.longitude.isEmpty() ? 0.0f : Double.parseDouble(event.longitude);
		double lat2 = mCurrentLocation == null ? 0.0f : mCurrentLocation.getLatitude();
		double lng2 = mCurrentLocation == null ? 0.0f : mCurrentLocation.getLongitude();
		double distance = getDistanceBetween2Points(lat1, lng1, lat2, lng2);
		if (distance < radius){
			if (event.guestStatus == null || !event.guestStatus.equals("HT")){
				JSONObject param = new JSONObject();
				try {
					param.put("id", event.id);
					param.put("checkInType", "AUTO");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				new CommunicationAPIManager(mContext).sendRequestCheckIn(event.id, param, new NetAPICallBack(){
					@Override
					public void succeed(final JSONObject responseObj) {
						if (MainActivity.instance != null){
							MainActivity.instance.runOnUiThread(new Runnable(){
								@Override
								public void run(){
									if (responseObj == null){
										String strMessage = String.format("You have reached %s.", event.name);
										buildNotification(strMessage);
										for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
											if (event.id == Global.GArrHooThereEvents.get(i).id){
												Global.GArrHooThereEvents.get(i).guestStatus = "HT";
												if (Global.GArrHooThereEvents.get(i).statistics == null){
													Global.GArrHooThereEvents.get(i).statistics = new HoothereStatistics();
												}
												Global.GArrHooThereEvents.get(i).statistics.hoothereCount++;
											}
										}
									}else{
										if (responseObj.has("errorMessage")){
											String strErrorMessage = responseObj.optString("errorMessage");
											Toast.makeText(MainActivity.instance == null ? mContext.getApplicationContext() : MainActivity.instance, strErrorMessage, Toast.LENGTH_LONG).show();
										}
									}
								}
							});
						}
					}

					@Override
					public void failed(JSONObject errorObj) {
					}

					@Override
					public void failed(VolleyError error) {
					}
				});
				return true;
			}
		}
		return false;
	}
	
	public boolean checkOutEvent(final HoothereEvent event){
		if (event == null) return false;
		String rad = event.radius == null || event.radius.equals("null") || event.radius.equals("") ? "0" : event.radius;
		double radius = Double.parseDouble(rad);
		double lat1 = event.latitude == null || event.latitude.equals("null") || event.latitude.isEmpty() ? 0.0f : Double.parseDouble(event.latitude);
		double lng1 = event.longitude == null || event.longitude.equals("null") || event.longitude.isEmpty() ? 0.0f : Double.parseDouble(event.longitude);
		double lat2 = mCurrentLocation == null ? 0.0f : mCurrentLocation.getLatitude();
		double lng2 = mCurrentLocation == null ? 0.0f : mCurrentLocation.getLongitude();
		double distance = getDistanceBetween2Points(lat1, lng1, lat2, lng2);
		if (distance > radius){
			if (event.guestStatus != null && event.guestStatus.equals("HT")){
				JSONObject param = new JSONObject();
				try {
					param.put("id", event.id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				new CommunicationAPIManager(mContext).sendRequestCheckOut(param, new NetAPICallBack(){
					@Override
					public void succeed(final JSONObject responseObj) {
						if (MainActivity.instance != null){
							MainActivity.instance.runOnUiThread(new Runnable(){
								@Override
								public void run(){
									if (responseObj == null){
										String strMessage = String.format("You have left %s.", event.name);
										buildNotification(strMessage);
										for (int i = 0; i < Global.GArrHooThereEvents.size(); i++){
											if (event.id == Global.GArrHooThereEvents.get(i).id){
												Global.GArrHooThereEvents.get(i).guestStatus = "WT";
												if (Global.GArrHooThereEvents.get(i).statistics == null){
													Global.GArrHooThereEvents.get(i).statistics = new HoothereStatistics();
												}
												Global.GArrHooThereEvents.get(i).statistics.hoothereCount--;
											}
										}
									}else{
										if (responseObj.has("errorMessage")){
											String strErrorMessage = responseObj.optString("errorMessage");
											Toast.makeText(MainActivity.instance == null ? mContext.getApplicationContext() : MainActivity.instance, strErrorMessage, Toast.LENGTH_LONG).show();
										}
									}
								}
							});
						}
					}

					@Override
					public void failed(JSONObject errorObj) {
					}

					@Override
					public void failed(VolleyError error) {
					}
				});
				return true;
			}
		}
		return false;
	}
}