package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class HoothereNotification {
	public long createdOn;
	public UserInformation sender;
	public String message;
	public int id;
	public int eventId;
	public boolean status;
	public String eventName;
	public String type;
	public UserInformation recipient;
	public boolean isRead;
	public boolean isLoadMore;
	
	public HoothereNotification(){
		isLoadMore = true;
	}
	
	public HoothereNotification(JSONObject obj){
		if (obj == null) return;
		isLoadMore = false;
		if (obj.has(Define.TAG_NOTIFICATION_CREATEDON)){
			try {
				createdOn = obj.getLong(Define.TAG_NOTIFICATION_CREATEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.TAG_NOTIFICATION_EVENTID)){
			try {
				eventId = obj.getInt(Define.TAG_NOTIFICATION_EVENTID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_EVENTNAME)){
			try {
				eventName = obj.getString(Define.TAG_NOTIFICATION_EVENTNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_ID)){
			try {
				id = obj.getInt(Define.TAG_NOTIFICATION_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_ISREAD)){
			try {
				isRead = obj.getBoolean(Define.TAG_NOTIFICATION_ISREAD);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_MESSAGE)){
			try {
				message = obj.getString(Define.TAG_NOTIFICATION_MESSAGE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_RECIPIENT)){
			try {
				recipient = new UserInformation(obj.getJSONObject(Define.TAG_NOTIFICATION_RECIPIENT));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_SENDER)){
			try {
				sender = new UserInformation(obj.getJSONObject(Define.TAG_NOTIFICATION_SENDER));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_STATUS)){
			try {
				status = obj.getBoolean(Define.TAG_NOTIFICATION_STATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_NOTIFICATION_TYPE)){
			try {
				type = obj.getString(Define.TAG_NOTIFICATION_TYPE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
