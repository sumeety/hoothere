package com.sumeet.hoothere;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gcm.GCMBaseIntentService;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;

public class GCMIntentService extends GCMBaseIntentService {
	
	public static String mMessage;

	public GCMIntentService() {
		super(WebConfig.GCM_APP_ID);
	}
	
	@Override
	protected void onError(Context context, String errorId){
		
	}

	@Override
	protected void onMessage(final Context context, Intent intent) {

		Intent passIntent = new Intent(context, MainActivity.class);
		
		String strMessage = Define.DEFAULT_NOTIFICATION_MESSAGE;
		Bundle bundle = intent.getExtras();
		if (bundle != null){
			strMessage = bundle.getString(Define.GCM_MESSAGE_TAG, Define.DEFAULT_NOTIFICATION_MESSAGE);
		}
		passIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, passIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.alert_dlg_title_hoothere))
				.setContentText(strMessage)
				.setSmallIcon(R.drawable.ic_launcher)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000})
				.setAutoCancel(true);

		mNotifyBuilder.setContentIntent(contentIntent);
		mManager.notify(0, mNotifyBuilder.build());
//		if (MainActivity.instance != null){
//			MainActivity.instance.displayNotificationBadge();
//		}
//		try {
//			boolean foregroud = new ForegroundCheckTask().execute(context).get();
//
//
//			if (foregroud) {
//				if (MainActivity.instance != null){
//					passIntent = new Intent(context, MainActivity.class);
//					passIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					b = new Bundle();
//					b.putString("type", push_type);
//					passIntent.putExtras(b);
//					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, passIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//					NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//					NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
//							this)
//							.setContentTitle("Hoothere")
//							.setContentText(recieveMsg)
//							.setSmallIcon(R.drawable.ic_launcher)
//							.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//							.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
//							.setAutoCancel(true);
//
//					mNotifyBuilder.setContentIntent(contentIntent);
//
//					mManager.notify(0, mNotifyBuilder.build());					
//				}else{
//					passIntent = new Intent(context, MainActivity.class);
//
//					passIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					b = new Bundle();
//					b.putString("type", push_type);
//					passIntent.putExtras(b);
//					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, passIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//					NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//					NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
//							this)
//							.setContentTitle("Hoothere")
//							.setContentText(recieveMsg)
//							.setSmallIcon(R.drawable.ic_launcher)
//							.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//							.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
//							.setAutoCancel(true);
//
//					mNotifyBuilder.setContentIntent(contentIntent);
//
//					mManager.notify(0, mNotifyBuilder.build());					
//				}
//			} else {
//				passIntent = new Intent(context, MainActivity.class);
//
//				passIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				b = new Bundle();
//				b.putString("type", push_type);
//				passIntent.putExtras(b);
//				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, passIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//				NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//				NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
//						this)
//						.setContentTitle("Hoothere")
//						.setContentText(recieveMsg)
//						.setSmallIcon(R.drawable.ic_launcher)
//						.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//						.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
//						.setAutoCancel(true);
//
//				mNotifyBuilder.setContentIntent(contentIntent);
//
//				mManager.notify(0, mNotifyBuilder.build());
//			}
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	protected void onRegistered(final Context context, final String registrationId) {
		getSharedPreferences("hoothere", MODE_PRIVATE).edit().putString("devicetoken", registrationId).commit();
		Global.deviceToken = registrationId;
	}

	@Override
	protected void onUnregistered(android.content.Context context, java.lang.String s) {
		getSharedPreferences("hoothere", MODE_PRIVATE).edit().putString("devicetoken", "").commit();
		Global.deviceToken = "";
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		getSharedPreferences("hoothere", MODE_PRIVATE).edit().putString("devicetoken", "").commit();
		Global.deviceToken = "";
		return super.onRecoverableError(context, errorId);
	}

	/*
	 * Check app is on foreground or not
	 */
	class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			final Context context = params[0].getApplicationContext();
			return isAppOnForeground(context);
		}

		private boolean isAppOnForeground(Context context) {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
			if (appProcesses == null) {
				return false;
			}
			final String packageName = context.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
					return true;
				}
			}
			return false;
		}
	}
}
