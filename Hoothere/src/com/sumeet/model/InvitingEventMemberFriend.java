package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InvitingEventMemberFriend {
	public String mobile;
	public String firstName;
	public long guestId;
	public String eventGuestStatus;
	public String middleName;
	public String fullName;
	public String email;
	public String profile_picture;
	public String status;
	public String lastName;
	public String dateOfBirth;
	
	public InvitingEventMemberFriend(){
		
	}
	
	public InvitingEventMemberFriend(HooThereFriend friend){
		if (friend == null) return;
		mobile = friend.friend == null ? "" : friend.friend.mobile;
		firstName = friend.friend == null || friend.friend.firstName == null? "" : friend.friend.firstName;
		eventGuestStatus = friend.eventGuestStatus;
		guestId = friend.friend == null ? 0 : friend.friend.userid;
		middleName = friend.friend == null ? "" : friend.friend.middleName;
		fullName = friend.friend == null ? "" : friend.friend.fullName;
		email = friend.friend == null ? "" : friend.friend.email;
		profile_picture = friend.friend == null ? "" : friend.friend.profile_picture;
		status = friend.status;
		lastName = friend.friend == null ? "" : friend.friend.lastName;
		dateOfBirth = friend.friend == null ? "" : friend.friend.dateOfBirth;
	}
	
	public JSONObject asJSON(){
		JSONObject retVal = new JSONObject();
		try {
			retVal.put("mobile", mobile == null || mobile.equals("null") ? "" : mobile);
			retVal.put("status", status == null || status.equals("null") ? "" : status);
			retVal.put("firstName", firstName == null || firstName.equals("null") ? "" : firstName);
			retVal.put("guestId", guestId);
			retVal.put("eventGuestStatus", eventGuestStatus == null || eventGuestStatus.equals("null") ? "" : eventGuestStatus);
			retVal.put("dateOfBirth", dateOfBirth == null || dateOfBirth.equals("null") ? "" : dateOfBirth);
			retVal.put("fullName", fullName == null || fullName.equals("null") ? "" : fullName);
			retVal.put("email", email == null || email.equals("null") ? "" : email);
			retVal.put("middleName", middleName == null || middleName.equals("null") ? "" : middleName);
			retVal.put("profile_picture", profile_picture == null || profile_picture.equals("null") ? "" : profile_picture);
			retVal.put("lastName", lastName == null || lastName.equals("null") ? "" : lastName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
