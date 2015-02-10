package com.sumeet.communication;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sumeet.callback.NetAPICallBack;
import com.sumeet.global.Define;
import com.sumeet.global.Global;
import com.sumeet.global.WebConfig;

@SuppressLint("DefaultLocale")
public class CommunicationAPIManager {
	
	Context mContext;
	public CommunicationAPIManager(Context ctx){
		mContext = ctx;
	}
	
    public void signInWithUserInfo(String user_email, String user_password, final NetAPICallBack callback) {
        final String  strURL = WebConfig.BASE_URL + WebConfig.LOGIN_URL;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", user_email);
        params.put("password", user_password);
        params.put("deviceId", Global.deviceToken);
        params.put("platform", "android");
//      NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strURL, params, callback);
        sendRequestServerByPost(strURL, params, callback);
    }
    
    private void sendRequestServerByPost(final String strURL, final HashMap<String, Object> params, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			JSONObject js1 = new JSONObject(params);
        			JsonNode js = new JsonNode(js1.toString());
        			response = Unirest.post(strURL).
        					header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.body(js).asJson();
        			JSONObject result = response.getBody().getObject();
        			callback.succeed(result);
        		} catch (UnirestException e) {
        			e.printStackTrace();
        			callback.failed((JSONObject)null);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerByGet(final String strURL, final JSONObject params, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			response = Unirest.get(strURL).
        					header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.asJson();
        			JSONObject result = response.getBody().getObject();
        			callback.succeed(result);
        		} catch (UnirestException e) {
        			e.printStackTrace();
        			callback.failed((JSONObject)null);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerByPost(final String strURL, final JSONObject jsObject, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			JSONObject js1 = jsObject;
        			JSONArray jsArray = new JSONArray();
        			jsArray.put(js1);
        			JsonNode js = new JsonNode(js1.toString());
        			response = Unirest.post(strURL).
        					header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.body(js).asJson();
        			if (response.getBody() == null){
            			callback.succeed((JSONObject)null);
        			}else{
        				JSONObject result = response.getBody().getObject();
        				callback.succeed(result);
        			}
        		} catch (UnirestException e) {
        			e.printStackTrace();
//        			JSONObject errorObj = new JSONObject();
//        			try {
//						errorObj.put("errorMessage", e.getMessage());
//					} catch (JSONException e1) {
//						e1.printStackTrace();
//					}
//        			callback.failed((JSONObject)errorObj);
        			callback.failed((JSONObject)null);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerByPUT(final String strURL, final JSONObject jsObject, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			JSONObject js1 = jsObject;
        			JSONArray jsArray = new JSONArray();
        			jsArray.put(js1);
        			JsonNode js = new JsonNode(js1.toString());
        			response = Unirest.put(strURL).
        					header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.body(js).asJson();
        			if (response.getBody() == null){
            			callback.succeed((JSONObject)null);
        			}else{
        				JSONObject result = response.getBody().getObject();
        				callback.succeed(result);
        			}
        		} catch (UnirestException e) {
        			e.printStackTrace();
        			callback.failed((JSONObject)null);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerByPUT2(final String strURL, final JSONObject jsObject, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			JSONObject js1 = jsObject;
        			JSONArray jsArray = new JSONArray();
        			jsArray.put(js1);
        			JsonNode js = new JsonNode(js1.toString());
        			response = Unirest.put(strURL).
        					header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.body(js).asJson();
        			if (response.getBody() == null){
            			callback.succeed((JSONObject)null);
        			}else{
        				JSONObject result = response.getBody().getObject();
        				callback.succeed(result);
        			}
        		} catch (UnirestException e) {
        			e.printStackTrace();
        			JSONObject error = new JSONObject();
        			try {
						error.put(Define.ERRORMESSAGE_TAG, e.getMessage());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
        			callback.failed(error);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerByPUT1(final String strURL, final JSONObject jsObject, final NetAPICallBack callback){
        new Thread(){
        	@Override
        	public void run(){
        		HttpResponse<JsonNode> response;
        		try {
        			JSONObject js1 = jsObject;
        			JSONArray jsArray = new JSONArray();
        			jsArray.put(js1);
        			JsonNode js = new JsonNode(js1.toString());
        			response = Unirest.put(strURL)
        					.header("accept", "application/json")
        					.header("Content-Type", "application/json")
        					.body(js).asJson();
        			if (response.getBody() == null){
            			callback.succeed((JSONObject)null);
        			}else{
        				JSONObject result = response.getBody().getObject();
        				callback.succeed(result);
        			}
        		} catch (UnirestException e) {
        			e.printStackTrace();
        			JSONObject obj = new JSONObject();
        			try {
						obj.put(Define.ERRORMESSAGE_TAG, e.getMessage());
					} catch (JSONException e1){
						e1.printStackTrace();
					}
        			callback.failed(obj);
        		}
        	}
        }.start();
    }
    
    private void sendRequestServerWithMedia(final String strURL, final byte [] imageData, final NetAPICallBack callback){
    	NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithContentType(
    			strURL, 
    			imageData, 
    			"multipart/form-data; boundary=---------------------------14737809831466499882746641449", 
    			callback);
    }
    
//    private void sendRequestServerByDELETE(final String strURL, final JSONObject jsObject, final NetAPICallBack callback){
//        new Thread(){
//        	@Override
//        	public void run(){
//        		HttpResponse<JsonNode> response;
//        		try {
//        			JSONObject js1 = jsObject;
//        			JSONArray jsArray = new JSONArray();
//        			jsArray.put(js1);
//        			JsonNode js = new JsonNode(js1.toString());
//        			response = Unirest.delete(strURL).
//        					header("accept", "application/json")
//        					.header("Content-Type", "application/json")
//        					.body(js).asJson();
//        			if (response.getBody() == null){
//            			callback.succeed((JSONObject)null);
//        			}else{
//        				JSONObject result = response.getBody().getObject();
//        				callback.succeed(result);
//        			}
//        		} catch (UnirestException e) {
//        			e.printStackTrace();
//        			callback.failed((JSONObject)null);
//        		}
//        	}
//        }.start();
//    }
    
    public void fetchListOfHootThereFriends(long userId, NetAPICallBack callback){
    	String strUrl = String.format("%s/friends/%d/getAll?page=0", WebConfig.BASE_URL, userId);
        NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strUrl, null, callback);
    }
    
    public void fetchListOfHootThereFriendsForEvent(long userId, long eventId, NetAPICallBack callback){
    	String strUrl = String.format("%s/friends/%d/getAll?eventId=%d", WebConfig.BASE_URL, userId, eventId);
        NetAPIClient.sharedInstance(mContext).jsonRequestByGETWithVolley(strUrl, null, callback);
    }
    
    public void getUpComingEvents(long userId, NetAPICallBack callback){
    	String strUrl = String.format("%s/user/%d/upcomingEvents", WebConfig.BASE_URL, userId);
        NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strUrl, null, callback);
    }

    public void getPastEvents(long userId, NetAPICallBack callback){
    	String strUrl = String.format("%s/user/%d/pastEvents", WebConfig.BASE_URL, userId);
        NetAPIClient.sharedInstance(mContext).jsonRequestByGETWithVolley(strUrl, null, callback);
    }

    public void getGuestList(long nEventId, String strPageIndex, String strPageSize, String strStatus, NetAPICallBack callback){
    	String strUrl = String.format("%s/event/%d/getGuests", WebConfig.BASE_URL, nEventId);
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	params.put("pageIndex", strPageIndex);
    	params.put("pageSize", strPageSize);
    	params.put("status", strStatus);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strUrl, params, callback);
    }
    
    public void getListOfNotifications(long strUserId, String offset, boolean bFlagGetAll, NetAPICallBack callback){
    	String strUrl = String.format("%s/user/%d/allNotifications?pageIndex=%s&pageSize=20", WebConfig.BASE_URL, strUserId, offset);
    	if (!bFlagGetAll){
    		strUrl = String.format("%s/user/%d/unreadNotifications?pageIndex=%s&pageSize=50",WebConfig.BASE_URL, strUserId, offset);
    	}
    	NetAPIClient.sharedInstance(mContext).jsonRequestByGETWithVolley(strUrl, null, callback);
    }
    
    public void getNumberOfNotifications(long nUserId, NetAPICallBack callback){
    	String strUrl = String.format("%s/user/%d/notificationCount", WebConfig.BASE_URL, nUserId);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByGETWithVolley(strUrl, null, callback);
    }
    
    public void sendSignupRequest(String firstName, String email, String password, String mobile, String signupType, String deviceId, final NetAPICallBack callback){
        final String  strURL = WebConfig.BASE_URL + WebConfig.SIGNUP_URL;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", firstName);
        params.put("email", email);
        params.put("password", password);
        params.put("mobile", mobile);
        params.put("signupType", signupType);
        params.put("deviceId", deviceId);
        params.put("platform", "android");
        sendRequestServerByPost(strURL, params, callback);
//        NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strURL, params, callback);
    }
    
    public void sendInviteRequest(long eventId, long userId, JSONArray friends, JSONArray contacts, JSONArray fbFriends, NetAPICallBack callback){
    	String strURL = String.format("%s/event/invite/%d/%d", WebConfig.BASE_URL, eventId, userId);
    	JSONObject json = new JSONObject();
    	try {
			json.put("hoothere", friends);
	    	json.put("contacts", contacts);
	    	json.put("facebook", fbFriends);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	sendRequestServerByPost(strURL, json, callback);
    }
    
    public void sendInviteSingleFriend(long eventId, long userId, Object friend, JSONObject friendJSON, NetAPICallBack callback){
    	String strURL = String.format("%s/event/invite/%d/%d", WebConfig.BASE_URL, eventId, userId);
    	sendRequestServerByPost(strURL, friendJSON, callback);
//    	NetAPIClient.sharedInstance(mContext).jsonRequestByPOSTWithVolley(strURL, params, callback);
    }
    
    public void sendVerifyCodeRequest(long userId, String verifyCode, String deviceId, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/verifycode", WebConfig.BASE_URL, userId);
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	params.put("verificationCode", verifyCode);
    	params.put("deviceId", deviceId);
    	sendRequestServerByPost(strURL, params, callback);
    }
    
    public void sendNotificationAsReadRequest(long userId, long notificationId, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/read/%d", WebConfig.BASE_URL, userId, notificationId);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByPUTWithVolley(strURL, null, callback);
    }
    
    public void sendAddFriendRequest(long userId, long friendId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/add/%d", WebConfig.BASE_URL, userId, friendId);
    	sendRequestServerByPUT(strURL, new JSONObject(), callback);
//    	NetAPIClient.sharedInstance(mContext).jsonRequestByPUTWithVolley(strURL, null, callback);
    }

    public void sendRemoveFriendRequest(long userId, long friendId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/remove/%d", WebConfig.BASE_URL, userId, friendId);
//    	sendRequestServerByDELETE(strURL, new JSONObject(), callback);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByDELETEWithVolley(strURL, null, callback);
    }

    public void sendRejectFriendRequest(long userId, long friendId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/reject/%d", WebConfig.BASE_URL, userId, friendId);
//    	sendRequestServerByDELETE(strURL, new JSONObject(), callback);
    	sendRequestServerByPUT(strURL, new JSONObject(), callback);
    }

    public void acceptFriendRequest(long userId, long friendId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/accept/%d", WebConfig.BASE_URL, userId, friendId);
    	sendRequestServerByPUT2(strURL, new JSONObject(), callback);
    }
    
    public void rejectFriendRequest(long userId, long friendId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/reject/%d", WebConfig.BASE_URL, userId, friendId);
    	sendRequestServerByPUT(strURL, new JSONObject(), callback);
    }
    
    public void sendHootAFriendRequest(long userId, long toId, NetAPICallBack callback){
    	String strURL = String.format("%s/friends/%d/hoot/%d", WebConfig.BASE_URL, userId, toId);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByPUTWithVolley(strURL, null, callback);
//    	sendRequestServerByPUT(strURL, new JSONObject(), callback);
    }
    
    public void sendUpdatePwdRequest(JSONObject params, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/updatePassword", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPUT(strURL, params, callback);
//    	NetAPIClient.sharedInstance(mContext).jsonRequestByPUTWithVolley(strURL, null, callback);
    }
    
    public void sendUpdateMyProfileRequest(JSONObject params, NetAPICallBack callback){
    	String strURL = String.format("%s/user/edit/%d", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPUT(strURL, params, callback);
    }
    
    public void sendUpdateAvatarRequest(byte[] avatarData, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/upload", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	NetAPIClient.sharedInstance(mContext).sendImageDataViaHttpPost(strURL, avatarData, callback);    	
    }
    
    public void sendUploadNewEventAlbum(byte[] eaData, long eventId, String caption, NetAPICallBack callback){
    	String strURL = String.format("%s/event/%d/upload?userId=%d&caption=%s", WebConfig.BASE_URL, eventId, Global.GUser == null ? 0 : Global.GUser.userid, caption);
    	NetAPIClient.sharedInstance(mContext).sendImageDataViaHttpPostWithCallBack(strURL, eaData, callback);    	
    }

    public void sendNotReceivedCodeRequest(NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/resendVerificationCode", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPUT(strURL, new JSONObject(), callback);
    }
    
    public void sendChangeNumberRequest(JSONObject obj, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/editPhoneForVerification", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPUT(strURL, obj, callback);
    }
    
    public void sendRequestWithImageData(JSONObject obj, NetAPICallBack callback){
		String strURL = String.format("%s/user/%d/upload", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
		sendRequestServerWithMedia(strURL, null, callback);
    }
    
    public void sendRequestForgotPwd(JSONObject params, NetAPICallBack callback){
    	String strURL = String.format("%s/user/forgotPassword", WebConfig.BASE_URL);
    	sendRequestServerByPost(strURL, params, callback);
    }
    
    public void sendRequestCreateEvent(JSONObject param, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/event/create", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPost(strURL, param, callback);
    }
    
    public void sendRequestCheckIn(long eventId, JSONObject param, NetAPICallBack callback){
    	String strURL = String.format("%s/event/%d/checkin", WebConfig.BASE_URL, eventId);
    	sendRequestServerByPUT(strURL, param, callback);
    }
    
    public void sendRequestCheckOut(JSONObject param, NetAPICallBack callback){
    	String strURL = String.format("%s/event/%d/checkout", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid);
    	sendRequestServerByPUT(strURL, param, callback);
    }
    
    public void sendRequestAcceptEvent(long eventId, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/event/%d/accept", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid, eventId);
    	JSONObject param = new JSONObject();
    	try {
			param.put("channel", "INVITED");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	sendRequestServerByPUT(strURL, param, callback);
    }
    
    public void sendRequestModifyEvent(long eventId, JSONObject param, NetAPICallBack callback){
    	String strURL = String.format("%s/event/edit/%d", WebConfig.BASE_URL, eventId);
    	sendRequestServerByPUT(strURL, param, callback);
    }
    
    public void sendRequestSearchEvent(long userId, String sIndex, NetAPICallBack callback){
    	String strURL = String.format("%s/event/search?name=%s&mode=EI&userId=%d&pageSize=500&pageIndex=0", WebConfig.BASE_URL, sIndex, userId);
    	sendRequestServerByGet(strURL, null, callback);
//        NetAPIClient.sharedInstance(mContext).jsonRequestByGETWithVolley(strURL, null, callback);
    }
    
    public void sendRequestJoinEvent(long eventId, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/event/%d/accept", WebConfig.BASE_URL, Global.GUser == null ? 0 : Global.GUser.userid, eventId);
    	JSONObject param = new JSONObject();
    	try {
			param.put("channel", "SELF");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	sendRequestServerByPUT(strURL, param, callback);
    }
    
    public void sendRequestSearchFriend(long userId, String sIndex, NetAPICallBack callback){
    	String strURL = String.format("%s/user/%d/search?name=%s", WebConfig.BASE_URL, userId, sIndex);
    	sendRequestServerByGet(strURL, null, callback);
    }
    
    public void sendRequestDeleteEventAlbumImage(long eventId, String name, NetAPICallBack callback){
    	String strURL = String.format("%s/event/%d/delete/%s?userId=%d", WebConfig.BASE_URL, eventId, name, Global.GUser == null ? -1 : Global.GUser.userid);
    	NetAPIClient.sharedInstance(mContext).jsonRequestByDELETEWithVolley(strURL, null, callback);
    }
    
    public void sendRequestSetAsEventAlbumImage(long eventId, String name, NetAPICallBack callback){
    	String strURL = String.format("%s/event/%d/setEventImage/%s?userId=%d", WebConfig.BASE_URL, eventId, name, Global.GUser == null ? -1 : Global.GUser.userid);
    	sendRequestServerByPUT1(strURL, new JSONObject(), callback);
   	}
    
    public void sendFBSignUpRequest(JSONObject params, NetAPICallBack callback){
    	String strURL = String.format("%s/user/register", WebConfig.BASE_URL);
    	sendRequestServerByPost(strURL, params, callback);
    }
}