package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class HooThereFriend {
	public long createdOn;
	public int id;
	public long modifiedOn;
	public String status;
	public long approvedOn;
	public String eventGuestStatus;
	public UserInformation friend;
	public long requestedBy;
	
	public HooThereFriend(){
		
	}
	
	public HooThereFriend(JSONObject obj){
		if (obj == null) return;
		
		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_APPROVEDON)){
			try {
				approvedOn = obj.getLong(Define.HOOT_THERE_FRIEND_INFO_APPROVEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_CREATED)){
			try {
				createdOn = obj.getLong(Define.HOOT_THERE_FRIEND_INFO_CREATED);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_EVENTGUESTSTATUS)){
			try {
				eventGuestStatus = obj.getString(Define.HOOT_THERE_FRIEND_INFO_EVENTGUESTSTATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_FRIEND)){
			try {
				friend = new UserInformation(obj.getJSONObject(Define.HOOT_THERE_FRIEND_INFO_FRIEND));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_ID)){
			try {
				id = obj.getInt(Define.HOOT_THERE_FRIEND_INFO_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_MODIFIEDON)){
			try {
				modifiedOn = obj.getLong(Define.HOOT_THERE_FRIEND_INFO_MODIFIEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_REQUESTEDBY)){
			try {
				requestedBy = obj.getLong(Define.HOOT_THERE_FRIEND_INFO_REQUESTEDBY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.HOOT_THERE_FRIEND_INFO_STATUS)){
			try {
				status = obj.getString(Define.HOOT_THERE_FRIEND_INFO_STATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
