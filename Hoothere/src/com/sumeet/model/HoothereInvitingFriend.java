package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class HoothereInvitingFriend {
	public long userId;
	public String firstName;
	public String middleName;
	public String lastName;
	public String profile_picture;
	public String friendshipStatus;
	public String availabilityStatus;
	
	public HoothereInvitingFriend(){
		
	}
	
	public HoothereInvitingFriend(JSONObject obj){
		if (obj == null) return;
		if (obj.has(Define.INVITING_FRIEND_USERID)){
			try {
				userId = obj.getLong(Define.INVITING_FRIEND_USERID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		firstName = obj.optString(Define.INVITING_FRIEND_FIRSTNAME);
		middleName = obj.optString(Define.INVITING_FRIEND_MIDDLENAME);
		lastName = obj.optString(Define.INVITING_FRIEND_LASTNAME);
		profile_picture = obj.optString(Define.INVITING_FRIEND_PROFILE_PICTURE);
		friendshipStatus = obj.optString(Define.INVITING_FRIEND_FRIENDSHIPSTATUS);
		availabilityStatus = obj.optString(Define.INVITING_FRIEND_AVAILABILITYSTATUS);
	}
}
