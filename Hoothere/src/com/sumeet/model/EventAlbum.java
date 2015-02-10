package com.sumeet.model;

import org.json.JSONObject;

import com.sumeet.global.Define;

public class EventAlbum {

	public int id;
	public String imageUrl;
	public String thumbnailUrl;
	public String album;
	public String name;
	public String privacy;
	public String caption;
	public UserInformation user;
	public long uploadedOn;
	
	public EventAlbum(){
		
	}
	
	public EventAlbum(JSONObject obj){
		if (obj == null) return;
		id = obj.optInt(Define.EVENTALBUM_ID);
		imageUrl = obj.optString(Define.EVENTALBUM_IMAGEURL);
		thumbnailUrl = obj.optString(Define.EVENTALBUM_THUMBNAILURL);
		album = obj.optString(Define.EVENTALBUM_ALBUM);
		name = obj.optString(Define.EVENTALBUM_NAME);
		privacy = obj.optString(Define.EVENTALBUM_PRIVACY);
		caption = obj.optString(Define.EVENTALBUM_CAPTION);
		user = new UserInformation(obj.optJSONObject(Define.EVENTALBUM_USER));
		uploadedOn = obj.optLong(Define.EVENTALBUM_UPLOADEDON);
	}
}
