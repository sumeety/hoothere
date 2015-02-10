package com.sumeet.model;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactMember{
	
	public String mName;
	public ArrayList<PhoneNumber> mNumber;
	public Bitmap mBitmap; 
	public int mMainNumberIndex;
	public int mSelected = 0;
	public int mType = 0;
	public int mId = 0;
	public int mGroupId;
	public String mGroupName;
	public Uri mAvatarUrl;
	public String email = "";
	
	public class PhoneNumber{
		public String mNumber;
		public String mType;
		
		public PhoneNumber(){
			mNumber = new String();
			mType = new String();
		}
		
		public PhoneNumber(String num){
			mNumber = num;
			mType = "MOBILE";
		}

		public PhoneNumber(String num, String type){
			mNumber = num;
			mType = type;
		}
	}

	public ContactMember(){
		mName = new String();
		mNumber = new ArrayList<PhoneNumber>();		
		mBitmap = null; 
		mMainNumberIndex = 0;
		mGroupName = new String();
		mGroupId = -1;		
	}

	public String getDisplayName() {
		return mName;		
	}
	
	public ArrayList<PhoneNumber> getNumberArray(){
		return mNumber;		
	}
	
	public Bitmap getIcon(){
		return mBitmap;		
	}
	
	public int getGroupId(){
		return mGroupId;
	}
}