package com.sumeet.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class HoothereStatistics {
	public int id;
	public int acceptedCount;
	public int invitedCount;
	public int hooCameCount;
	public int hoothereCount;
	
	public HoothereStatistics(){
		
	}
	
	public HoothereStatistics(JSONObject obj){
		if (obj == null) return;
		if (obj.has(Define.TAG_STATISTICS_ID)){
			try {
				id = obj.getInt(Define.TAG_STATISTICS_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.TAG_STATISTICS_ACCEPTEDCOUNT)){
			try {
				acceptedCount = obj.getInt(Define.TAG_STATISTICS_ACCEPTEDCOUNT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.TAG_STATISTICS_HOOTHERECOUNT)){
			try {
				hoothereCount = obj.getInt(Define.TAG_STATISTICS_HOOTHERECOUNT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_STATISTICS_INVITEDCOUNT)){
			try {
				invitedCount = obj.getInt(Define.TAG_STATISTICS_INVITEDCOUNT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.TAG_STATISTICS_HOOCAMECOUNT)){
			try {
				hooCameCount = obj.getInt(Define.TAG_STATISTICS_HOOCAMECOUNT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
