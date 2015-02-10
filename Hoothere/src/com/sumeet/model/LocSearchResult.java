package com.sumeet.model;

public class LocSearchResult {
	public String mStrFeature;
	public String mStrCountry;
	public double mLat;
	public double mLng;
	public boolean mIfFavorite; 
	public String mZipCode;
	public String mVenueName;
	
	public LocSearchResult(){
		mStrFeature = null;
		mStrCountry = null;
		mZipCode = null;
		mLat = 0.0d;
		mLng = 0.0d;
	}
	
	public LocSearchResult(String feature, String country, double lat, double lng, String zipcode){
		mStrFeature = feature;
		mStrCountry = country;
		mLat = lat;
		mLng = lng;
		mZipCode = zipcode;
	}
}
