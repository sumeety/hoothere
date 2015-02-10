package com.sumeet.global;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

import com.sumeet.model.UserInformation;

public class Configure {	

	private  SharedPreferences prefs;
	
	private static final String PREF_KEY_LOGGED_IN = "loggedin";
	private static final String PREF_KEY_LOGGED_IN_INFO = "loggedin_info";
	private static final String PREF_KEY_GCM_DEVICE_TOKEN = "devicetoken";
	private static final String PREF_KEY_FB_FRIENDS = "fbfriends";
	
	public Configure(final SharedPreferences prefs) {
		this.prefs = prefs;		
//		loadSetting();
	}
	
	public boolean checkIfLoggedIn(){
		if (prefs == null) return false;
		return prefs.getBoolean(PREF_KEY_LOGGED_IN, false);
	}
	
	public void saveLoggedIn(boolean bValue){
		if (prefs == null) return;
		prefs.edit().putBoolean(PREF_KEY_LOGGED_IN, bValue).commit();
	}
	
	public void saveDeviceToken(String strToken){
		if (prefs == null) return;
		prefs.edit().putString(PREF_KEY_GCM_DEVICE_TOKEN, strToken).commit();
	}
	
	public String getDeviceToken(){
		if (prefs == null) return "";
		return prefs.getString(PREF_KEY_GCM_DEVICE_TOKEN, "");
	}
	
	public void saveUserLoginInfo(JSONObject user){
		if (prefs == null || user == null) return;
		prefs.edit().putString(PREF_KEY_LOGGED_IN_INFO, user.toString()).commit();
	}
	
	public void saveFBFriendsList(JSONArray friends){
		if (prefs == null || friends == null) return;
		prefs.edit().putString(PREF_KEY_FB_FRIENDS, friends.toString()).commit();
	}
	
	public JSONArray getFBFriendsList(){
		JSONArray retVal = new JSONArray();
		if (prefs != null){
			String str = prefs.getString(PREF_KEY_FB_FRIENDS, "[]");
			try {
				retVal = new JSONArray(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return retVal;
	}
	
	public void removeSavedInfo(){
		prefs.edit().remove(PREF_KEY_LOGGED_IN_INFO).commit();
	}
	
	public UserInformation getUserLoggedInfo(){
		if (prefs == null) return null;
		String strInfo = prefs.getString(PREF_KEY_LOGGED_IN_INFO, null);
		if (strInfo == null || strInfo.isEmpty()) return null;
		UserInformation user = null;
		try {
			JSONObject obj = new JSONObject(strInfo);
			user = new UserInformation(obj);
		} catch (JSONException e) {
			e.printStackTrace();
			
		}
		return user;
	}
	
	/**
	 * @param settings
	 */
	public void saveSetting(HashMap<String,Boolean> settings){
		
		if (settings == null) return;
		for(String  key : settings.keySet()){
			prefs.edit().putBoolean(key, settings.get(key)).apply();
		}
	}
	
	public void saveUserInformation(JSONObject userInfo){
//		Iterator iterator = userInfo.keys();
//		String keys = "";
//		while(iterator.hasNext()){
//			String key = (String)iterator.next();
//			try {
//				String value = (String)userInfo.getString(key);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		if (userInfo.has(Define.USERINFO_ACTIVATIONSTATUS)){
			try {
				prefs.edit().putString(Define.USERINFO_ACTIVATIONSTATUS, userInfo.getString(Define.USERINFO_ACTIVATIONSTATUS)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (userInfo.has(Define.USERINFO_AVAILABILITYSTATUS)){
			try {
				prefs.edit().putString(Define.USERINFO_AVAILABILITYSTATUS, userInfo.getString(Define.USERINFO_AVAILABILITYSTATUS)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (userInfo.has(Define.USERINFO_CREATEDON)){
			try {
				prefs.edit().putString(Define.USERINFO_CREATEDON, userInfo.getString(Define.USERINFO_CREATEDON)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_DATEOFBIRTH)){
			try {
				prefs.edit().putString(Define.USERINFO_DATEOFBIRTH, userInfo.getString(Define.USERINFO_DATEOFBIRTH)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_DEVICEID)){
			try {
				prefs.edit().putString(Define.USERINFO_DEVICEID, userInfo.getString(Define.USERINFO_DEVICEID)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_EMAIL)){
			try {
				prefs.edit().putString(Define.USERINFO_EMAIL, userInfo.getString(Define.USERINFO_EMAIL)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_EVENTAVAILABILITY)){
			try {
				prefs.edit().putString(Define.USERINFO_EVENTAVAILABILITY, userInfo.getString(Define.USERINFO_EVENTAVAILABILITY)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_FACEBOOKID)){
			try {
				prefs.edit().putString(Define.USERINFO_FACEBOOKID, userInfo.getString(Define.USERINFO_FACEBOOKID)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_FIRSTNAME)){
			try {
				prefs.edit().putString(Define.USERINFO_FIRSTNAME, userInfo.getString(Define.USERINFO_FIRSTNAME)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_FULLNAME)){
			try {
				prefs.edit().putString(Define.USERINFO_FULLNAME, userInfo.getString(Define.USERINFO_FULLNAME)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_ID)){
			try {
				prefs.edit().putString(Define.USERINFO_ID, userInfo.getString(Define.USERINFO_ID)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_LASTNAME)){
			try {
				prefs.edit().putString(Define.USERINFO_LASTNAME, userInfo.getString(Define.USERINFO_LASTNAME)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_MOBILE)){
			try {
				prefs.edit().putString(Define.USERINFO_MOBILE, userInfo.getString(Define.USERINFO_MOBILE)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_MODIFIEDON)){
			try {
				prefs.edit().putString(Define.USERINFO_MODIFIEDON, userInfo.getString(Define.USERINFO_MODIFIEDON)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_PASSWORD)){
			try {
				prefs.edit().putString(Define.USERINFO_PASSWORD, userInfo.getString(Define.USERINFO_PASSWORD)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_PROFILE_PICTURE)){
			try {
				prefs.edit().putString(Define.USERINFO_PROFILE_PICTURE, userInfo.getString(Define.USERINFO_PROFILE_PICTURE)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (userInfo.has(Define.USERINFO_SIGNUPTYPE)){
			try {
				prefs.edit().putString(Define.USERINFO_SIGNUPTYPE, userInfo.getString(Define.USERINFO_SIGNUPTYPE)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_VERIFICATIONCODE)){
			try {
				prefs.edit().putString(Define.USERINFO_VERIFICATIONCODE, userInfo.getString(Define.USERINFO_VERIFICATIONCODE)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (userInfo.has(Define.USERINFO_PLATFORM)){
			try {
				prefs.edit().putString(Define.USERINFO_PLATFORM, userInfo.getString(Define.USERINFO_PLATFORM)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (userInfo.has(Define.USERINFO_ISMOBILEVERIFIED)){
			try {
				prefs.edit().putBoolean(Define.USERINFO_ISMOBILEVERIFIED, userInfo.getBoolean(Define.USERINFO_ISMOBILEVERIFIED)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (userInfo.has(Define.USERINFO_TOVERIFY)){
			try {
				prefs.edit().putBoolean(Define.USERINFO_TOVERIFY, userInfo.getBoolean(Define.USERINFO_TOVERIFY)).apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
