package com.sumeet.callback;

import java.util.ArrayList;

import org.json.JSONArray;

import com.sumeet.model.LocSearchResult;

public interface OnFinishLocationSearch {
	public void onSucceed(ArrayList<LocSearchResult> result);
	public void onFailure();
	public void onSucceed(JSONArray result);
}
