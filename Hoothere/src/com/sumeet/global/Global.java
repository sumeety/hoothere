package com.sumeet.global;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumeet.model.ContactMember;
import com.sumeet.model.FBFriend;
import com.sumeet.model.HooThereFriend;
import com.sumeet.model.HoothereEvent;
import com.sumeet.model.UserInformation;

public class Global {

	public static String deviceToken = "";

	public static UserInformation GUser;

	public static ArrayList<HooThereFriend> GArrHootThereFriends;
	public static ArrayList<HoothereEvent> GArrHooThereEvents;
	public static HashMap<Object, JSONObject> GMapHoothereFriendsJSON;
	public static ArrayList<ContactMember> GArrContacts;
	
	public static ArrayList<FBFriend> GArrFBFriends;
	public static JSONArray GArrFBFriendsJSON;
	
//	public static JSONArray GArrEventGuestJSON;
//	public static JSONArray GArrEventMemberFriendJSON;
//	public static JSONArray GArrNotificationJSON;
//	
//	public static JSONArray GArrFriendsInvitedJSON;
//	public static JSONArray GArrFriendsAcceptedJSON;
//	public static JSONArray GArrFriendsHooJSON;
}
