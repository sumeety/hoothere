package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class FBFriend {
	public String id;
	public String name;
	
	public FBFriend(){
		id = "";
		name = "";
	}
	
	public FBFriend(JSONObject obj){
		if (obj == null) return;
		
		if (obj.has(Define.FB_FRIEND_ID)){
			try{
				id = obj.getString(Define.FB_FRIEND_ID);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.FB_FRIEND_NAME)){
			try{
				name = obj.getString(Define.FB_FRIEND_NAME);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
	}
	
	public JSONObject asJSON(){
		
		JSONObject retVal = new JSONObject();
		
		try{
			retVal.put(Define.FB_FRIEND_ID, id);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.FB_FRIEND_NAME, name);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return retVal;
	}
}
