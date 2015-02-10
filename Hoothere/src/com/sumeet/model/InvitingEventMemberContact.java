package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InvitingEventMemberContact {
	public String email;
	public String number;
	public String guestId;
	public String name;
	
	public InvitingEventMemberContact(){
		
	}
	
	public InvitingEventMemberContact(ContactMember contact){
		email = "";
		number = contact.mNumber.get(contact.mMainNumberIndex).mNumber;
		guestId = String.format("%d", contact.mId);
		name = contact.mName;
	}
	
	public JSONObject asJSON(){
		JSONObject retVal = new JSONObject();
		if (email != null && !email.equals("null")){
			try {
				retVal.put("email", email);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (number != null && !number.equals("null")){
			try {
				retVal.put("number", number);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (guestId != null && !guestId.equals("null")){
			try {
				retVal.put("guestId", guestId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (name != null && !name.equals("null")){
			try {
				retVal.put("name", name);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return retVal;
	}
}
