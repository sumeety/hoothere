package com.sumeet.model;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumeet.global.Define;

public class UserInformation {
	public String  availabilityStatus;
	public Date  createdOn;
	public String  dateOfBirth;
	public String  deviceId;
	public String  email;
	public String  facebookId;
	public String  firstName;
	public String  lastName;
	public String  middleName;
	public String  mobile;
	public Date  modifiedOn;
	public String  profile_picture;
	public byte[]  profileImage;
	public long  userid;
	public String  fullName;
	public String  password;
	public String activationStatus;
	public String platform;
	public boolean isMobileVerified;
	public String deviceType;
	public boolean toVerify;
	public String signupType;
	
	public HashMap<String, Object> asHashMap(){
		HashMap<String, Object> retVal = new HashMap<String, Object>();
		retVal.put(Define.USERINFO_AVAILABILITYSTATUS, modifiedOn);
		retVal.put(Define.USERINFO_CREATEDON, createdOn);
		retVal.put(Define.USERINFO_DATEOFBIRTH, dateOfBirth);
		retVal.put(Define.USERINFO_DEVICEID, deviceId);
		retVal.put(Define.USERINFO_EMAIL, email);
		retVal.put(Define.USERINFO_FACEBOOKID, facebookId);
		retVal.put(Define.USERINFO_FIRSTNAME, firstName);
		retVal.put(Define.USERINFO_FULLNAME, fullName);
		retVal.put(Define.USERINFO_ID, userid);
		retVal.put(Define.USERINFO_LASTNAME, lastName);
		retVal.put(Define.USERINFO_MIDDLENAME, middleName);
		retVal.put(Define.USERINFO_MOBILE, mobile);
		retVal.put(Define.USERINFO_MODIFIEDON, modifiedOn);
		retVal.put(Define.USERINFO_PASSWORD, password);
		retVal.put(Define.USERINFO_PROFILE_PICTURE, profile_picture);
		retVal.put(Define.USERINFO_ACTIVATIONSTATUS, activationStatus);
		retVal.put(Define.USERINFO_PLATFORM, platform);
		retVal.put(Define.USERINFO_ISMOBILEVERIFIED, isMobileVerified);
		retVal.put(Define.USERINFO_TOVERIFY, toVerify);
		retVal.put(Define.USERINFO_DEVICETYPE, deviceType);
		retVal.put(Define.USERINFO_SIGNUPTYPE, signupType);
		return retVal;
	}
	
	public JSONObject asJSONForUpdate(){
		JSONObject retVal = new JSONObject();
		try{
			retVal.put(Define.USERINFO_ACTIVATIONSTATUS, activationStatus);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.USERINFO_PLATFORM, platform);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try {
			retVal.put("availabilityStatus", availabilityStatus);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("mobile", mobile);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("firstName", firstName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("profile_picture", profile_picture);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("middleName", middleName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("fullName", fullName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			retVal.put("email", email);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (createdOn != null){
			try {
				retVal.put("createdOn", createdOn.getTime());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			try {
				retVal.put("createdOn", 0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (modifiedOn != null){
			try {
				retVal.put("modifiedOn", modifiedOn.getTime());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			try {
				retVal.put("modifiedOn", 0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			retVal.put("lastName", lastName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.USERINFO_ISMOBILEVERIFIED, isMobileVerified);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.USERINFO_TOVERIFY, toVerify);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.USERINFO_DEVICETYPE, deviceType);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.USERINFO_SIGNUPTYPE, signupType);
		}catch(JSONException e){
			e.printStackTrace();
		}

		return retVal;
	}
	
	public void setUserInfo(JSONObject obj){
		if (obj == null) return;
		
		if (obj.has(Define.USERINFO_ACTIVATIONSTATUS)){
			try{
				activationStatus = obj.getString(Define.USERINFO_ACTIVATIONSTATUS);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		platform = "android";
		
		if (obj.has(Define.USERINFO_AVAILABILITYSTATUS)){
			try {
				availabilityStatus = obj.getString(Define.USERINFO_AVAILABILITYSTATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (obj.has(Define.USERINFO_CREATEDON)){
			try {
				createdOn = new Date(Long.parseLong(obj.getString(Define.USERINFO_CREATEDON)));
			} catch (JSONException e) {
				e.printStackTrace();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		if (obj.has(Define.USERINFO_DATEOFBIRTH)){
			try {
				dateOfBirth = obj.getString(Define.USERINFO_DATEOFBIRTH);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_DEVICEID)){
			try {
				deviceId = obj.getString(Define.USERINFO_DEVICEID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_EMAIL)){
			try {
				email = obj.getString(Define.USERINFO_EMAIL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FACEBOOKID)){
			try {
				facebookId = obj.getString(Define.USERINFO_FACEBOOKID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FIRSTNAME)){
			try {
				firstName = obj.getString(Define.USERINFO_FIRSTNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FULLNAME)){
			try {
				fullName = obj.getString(Define.USERINFO_FULLNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_ID)){
			try {
				userid = obj.getLong(Define.USERINFO_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_LASTNAME)){
			try {
				lastName = obj.getString(Define.USERINFO_LASTNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_MOBILE)){
			try {
				mobile = obj.getString(Define.USERINFO_MOBILE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_MODIFIEDON)){
			try {
				modifiedOn = new Date(Long.parseLong(obj.getString(Define.USERINFO_MODIFIEDON)));
			} catch (JSONException e) {
				e.printStackTrace();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_PASSWORD)){
			try {
				password = obj.getString(Define.USERINFO_PASSWORD);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_PROFILE_PICTURE)){
			try {
				profile_picture = obj.getString(Define.USERINFO_PROFILE_PICTURE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_ISMOBILEVERIFIED)){
			try{
				isMobileVerified = obj.getBoolean(Define.USERINFO_ISMOBILEVERIFIED);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_TOVERIFY)){
			try{
				toVerify = obj.getBoolean(Define.USERINFO_TOVERIFY);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_DEVICETYPE)){
			try{
				deviceType = obj.getString(Define.USERINFO_DEVICETYPE);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_SIGNUPTYPE)){
			try{
				signupType = obj.getString(Define.USERINFO_SIGNUPTYPE);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
	}
	
	public UserInformation(JSONObject obj){
		if (obj == null) return;
		
		if (obj.has(Define.USERINFO_ACTIVATIONSTATUS)){
			try{
				activationStatus = obj.getString(Define.USERINFO_ACTIVATIONSTATUS);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		platform = "android";
		
		if (obj.has(Define.USERINFO_AVAILABILITYSTATUS)){
			try {
				availabilityStatus = obj.getString(Define.USERINFO_AVAILABILITYSTATUS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (obj.has(Define.USERINFO_CREATEDON)){
			try {
				createdOn = new Date(Long.parseLong(obj.getString(Define.USERINFO_CREATEDON)));
			} catch (JSONException e) {
				e.printStackTrace();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		if (obj.has(Define.USERINFO_DATEOFBIRTH)){
			try {
				dateOfBirth = obj.getString(Define.USERINFO_DATEOFBIRTH);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_DEVICEID)){
			try {
				deviceId = obj.getString(Define.USERINFO_DEVICEID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_EMAIL)){
			try {
				email = obj.getString(Define.USERINFO_EMAIL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FACEBOOKID)){
			try {
				facebookId = obj.getString(Define.USERINFO_FACEBOOKID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FIRSTNAME)){
			try {
				firstName = obj.getString(Define.USERINFO_FIRSTNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (obj.has(Define.USERINFO_FULLNAME)){
			try {
				fullName = obj.getString(Define.USERINFO_FULLNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_ID)){
			try {
				userid = obj.getLong(Define.USERINFO_ID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_LASTNAME)){
			try {
				lastName = obj.getString(Define.USERINFO_LASTNAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_MOBILE)){
			try {
				mobile = obj.getString(Define.USERINFO_MOBILE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_MODIFIEDON)){
			try {
				modifiedOn = new Date(Long.parseLong(obj.getString(Define.USERINFO_MODIFIEDON)));
			} catch (JSONException e) {
				e.printStackTrace();
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_PASSWORD)){
			try {
				password = obj.getString(Define.USERINFO_PASSWORD);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_PROFILE_PICTURE)){
			try {
				profile_picture = obj.getString(Define.USERINFO_PROFILE_PICTURE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_ISMOBILEVERIFIED)){
			try{
				isMobileVerified = obj.getBoolean(Define.USERINFO_ISMOBILEVERIFIED);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_TOVERIFY)){
			try{
				toVerify = obj.getBoolean(Define.USERINFO_TOVERIFY);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_DEVICETYPE)){
			try{
				deviceType = obj.getString(Define.USERINFO_DEVICETYPE);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		if (obj.has(Define.USERINFO_SIGNUPTYPE)){
			try{
				signupType = obj.getString(Define.USERINFO_SIGNUPTYPE);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
	}
	
	public UserInformation(){
		
	}
}
