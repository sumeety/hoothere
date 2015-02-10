package com.sumeet.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class HoothereEvent {
	public long startDateTime;
	public int modifiedBy;
	public String eventType;
	public String state;
	public Object timeZone;
	public HoothereStatistics statistics;
	public String city;
	public long id;
	public String description;
	public ArrayList<EventGuest> eventGuest;
	public String longitude;
	public String venueName;
	public String guestStatus;
	public String zipcode;
	public String streetName;
	public long endDateTime;
	public String country;
	public long createdOn;
	public long modifiedOn;
	public String eventDescription;
	public Object source;
	public String address;
	public String eventImage;
	public String radius;
	public String latitude;
	public UserInformation user;
	public String name;
	public ArrayList<EventAlbum> eventAlbum;
	public EventAlbum coverImage;
	public int nEAIndex = 0;
	
	public HoothereEvent(){
		
	}
	
	public HoothereEvent(JSONObject obj){
		if (obj == null) return;
		if (obj.has(Define.EVENT_NAME)){
			try {
				name = obj.getString(Define.EVENT_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_ADDRESS)){
			try {
				address = obj.getString(Define.EVENT_ADDRESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_CITY)){
			try {
				city = obj.getString(Define.EVENT_CITY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_COUNTRY)){
			try {
				country = obj.getString(Define.EVENT_COUNTRY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_CREATEDON)){
			try {
				createdOn = obj.getLong(Define.EVENT_CREATEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_DESCRIPTION)){
			try {
				description = obj.getString(Define.EVENT_DESCRIPTION);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_ENDDATETIME)){
			try {
				endDateTime = obj.getLong(Define.EVENT_ENDDATETIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_EVENTDESCRIPTION)){
			try {
				eventDescription = obj.getString(Define.EVENT_EVENTDESCRIPTION);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_EVENTIMAGE)){
			try {
				eventImage = obj.getString(Define.EVENT_EVENTIMAGE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_EVENTTYPE)){
			try {
				eventType = obj.getString(Define.EVENT_EVENTTYPE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_GUESTSTATUS)){
			try {
				guestStatus = obj.getString(Define.EVENT_GUESTSTATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_ID)){
			try {
				id = obj.getInt(Define.EVENT_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_LATITUDE)){
			try {
				latitude = obj.getString(Define.EVENT_LATITUDE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_LONGITUDE)){
			try {
				longitude = obj.getString(Define.EVENT_LONGITUDE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_MODIFIEDBY)){
			try {
				modifiedBy = obj.getInt(Define.EVENT_MODIFIEDBY);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_MODIFIEDON)){
			try {
				modifiedOn = obj.getLong(Define.EVENT_MODIFIEDON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_RADIUS)){
			try {
				radius = obj.getString(Define.EVENT_RADIUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_SOURCE)){
			try {
				source = obj.get(Define.EVENT_SOURCE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_STARTDATETIME)){
			try {
				startDateTime = obj.getLong(Define.EVENT_STARTDATETIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_STATE)){
			try {
				state = obj.getString(Define.EVENT_STATE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_STATISTICS)){
			try {
				statistics = new HoothereStatistics(obj.getJSONObject(Define.EVENT_STATISTICS));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_STREETNAME)){
			try {
				streetName = obj.getString(Define.EVENT_STREETNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_TIMEZONE)){
			try {
				timeZone = obj.get(Define.EVENT_TIMEZONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_USER)){
			try {
				user = new UserInformation(obj.getJSONObject(Define.EVENT_USER));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.EVENT_VENUENAME)){
			try {
				venueName = obj.getString(Define.EVENT_VENUENAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_ZIPCODE)){
			try {
				zipcode = obj.getString(Define.EVENT_ZIPCODE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_EVENTGUEST)){
			eventGuest = new ArrayList<EventGuest>();					
			try {
				JSONArray eventGuests = obj.getJSONArray(Define.EVENT_EVENTGUEST);
				if (eventGuests != null){
					for (int i = 0; i < eventGuests.length(); i++){
						EventGuest eg = new EventGuest(eventGuests.getJSONObject(i));
						eventGuest.add(eg);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if (obj.has(Define.EVENT_EVENTGUESTS)){
			eventGuest = new ArrayList<EventGuest>();					
			try {
				JSONArray eventGuests = obj.getJSONArray(Define.EVENT_EVENTGUESTS);
				if (eventGuests != null){
					for (int i = 0; i < eventGuests.length(); i++){
						EventGuest eg = new EventGuest(eventGuests.getJSONObject(i));
						eventGuest.add(eg);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_ALBUM)){
			eventAlbum = new ArrayList<EventAlbum>();
			try {
				JSONArray eventAlbums = obj.getJSONArray(Define.EVENT_ALBUM);
				if (eventAlbums != null){
					for (int i = eventAlbums.length() - 1; i >= 0; i--){
						EventAlbum ea = new EventAlbum(eventAlbums.getJSONObject(i));
						eventAlbum.add(ea);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.EVENT_COVERIMAGE)){
			try {
				coverImage = new EventAlbum(obj.getJSONObject(Define.EVENT_COVERIMAGE));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public JSONObject asJSON(){
		JSONObject result = new JSONObject();
		
		try {
			result.put(Define.EVENT_ADDRESS, address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put(Define.EVENT_CITY, city);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_COUNTRY, country);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put(Define.EVENT_CREATEDON, createdOn);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put("eventDescription", description);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_ENDDATETIME, endDateTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_LONGITUDE, longitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_LATITUDE, latitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_NAME, name);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_RADIUS, radius);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_STARTDATETIME, startDateTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			result.put(Define.EVENT_STREETNAME, streetName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put(Define.EVENT_VENUENAME, venueName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try{
			result.put(Define.EVENT_EVENTTYPE, eventType);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return result;
	}
}
