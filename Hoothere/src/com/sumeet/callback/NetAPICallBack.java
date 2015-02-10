package com.sumeet.callback;

import org.json.JSONObject;

import com.android.volley.VolleyError;

public interface NetAPICallBack {
	public void succeed(JSONObject responseObj);
	public void failed(JSONObject errorObj);
	public void failed(VolleyError error);
}
