package com.sumeet.model;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class EventGuest {
	public int invitedBy;
	public int modifiedBy;
	public String number;
	public String userType;
	public int id;
	public long modifiedOn;
	public long invitedOn;
	public String statusOfGuest;
	public boolean smsSent;
	public String email;
	public String name;
	public long guestId;
	public boolean emailSent;
	public UserInformation user;
	
	public EventGuest(){
		
	}
	
	public EventGuest(JSONObject obj){
		if (obj == null) return;
		if (obj.has(Define.TAG_EVENTGUEST_EMAIL)){
			try {
				email = obj.getString(Define.TAG_EVENTGUEST_EMAIL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.TAG_EVENTGUEST_EMAILSENT)){
			try {
				emailSent = obj.getBoolean(Define.TAG_EVENTGUEST_EMAILSENT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_GUESTID)){
			try {
				guestId = obj.getLong(Define.TAG_EVENTGUEST_GUESTID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_ID)){
			try {
				id = obj.getInt(Define.TAG_EVENTGUEST_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_INVITEDBY)){
			try {
				invitedBy = obj.getInt(Define.TAG_EVENTGUEST_INVITEDBY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_INVITEDON)){
			try {
				invitedOn = obj.getLong(Define.TAG_EVENTGUEST_INVITEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_MODIFIEDBY)){
			try {
				modifiedBy = obj.getInt(Define.TAG_EVENTGUEST_MODIFIEDBY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_MODIFIEDON)){
			try {
				modifiedOn = obj.getLong(Define.TAG_EVENTGUEST_MODIFIEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_NAME)){
			try {
				name = obj.getString(Define.TAG_EVENTGUEST_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_NUMBER)){
			try {
				number = obj.getString(Define.TAG_EVENTGUEST_NUMBER);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.TAG_EVENTGUEST_SMSSENT)){
			try {
				smsSent = obj.getBoolean(Define.TAG_EVENTGUEST_SMSSENT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_STATUSOFGUEST)){
			try {
				statusOfGuest = obj.getString(Define.TAG_EVENTGUEST_STATUSOFGUEST);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_USER)){
			try {
				user = new UserInformation(obj.getJSONObject(Define.TAG_EVENTGUEST_USER));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_EVENTGUEST_USERTYPE)){
			try {
				userType = obj.getString(Define.TAG_EVENTGUEST_USERTYPE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public HashMap<String, Object> asHashMap(){
		HashMap<String, Object> retVal = new HashMap<String, Object>();
		return retVal;
	}
}
