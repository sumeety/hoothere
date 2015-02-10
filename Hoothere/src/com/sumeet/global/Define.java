package com.sumeet.global;

public class Define {
	public static final int MAIN_FOOTER_TAB_INDEX_EVENTS = 1;
	public static final int MAIN_FOOTER_TAB_INDEX_FRIENDS = 2;
	public static final int MAIN_FOOTER_TAB_INDEX_NOTIFICATIONS = 3;
	public static final int MAIN_FOOTER_TAB_INDEX_ME = 4;
	public static final int MAIN_FOOTER_TAB_INDEX_NONE = -1;
	
	public static final int STATUS_AVAILABLE = 0;
	public static final int STATUS_WANT_PLAN = 1;
	public static final int STATUS_GOING_OUT = 2;
	public static final int STATUS_BUSY = 3;
	
	public static final int MEMBER_TYPE_INVITED = 0;
	public static final int MEMBER_TYPE_GOING_THERE = 1;
	public static final int MEMBER_TYPE_HOO_THERE = 2;
	public static final int MEMBER_TYPE_HOO_CAME = 3;
	
	public static final int RANGE_MIN_VAL = 10;
	public static final int RANGE_MAX_VAL = 1000;
	
	public static final String EVENT_TYPE_PUBLIC = "PUB";
	public static final String EVENT_TYPE_PRIVATE = "PVT";
	
	public static final String ERRORMESSAGE_TAG = "errorMessage";
	public static final String ERROR_TAG = "error";
	public static final String JSONEXCEPTION_SUCCESS = "org.json.JSONException: End of input at character 0 of ";
	
	public static final String SIGNUP_TYPE_HOOTHERE = "hoothere";
	public static final String SIGNUP_TYPE_FACEBOOK = "F";

	public static final String USERINFO_EVENTAVAILABILITY = "eventAvailability";
	public static final String USERINFO_DATEOFBIRTH = "dateOfBirth";
	public static final String USERINFO_LASTNAME = "lastName";
	public static final String USERINFO_PROFILE_PICTURE = "profile_picture";
	public static final String USERINFO_AVAILABILITYSTATUS = "availabilityStatus";
	public static final String USERINFO_ACTIVATIONSTATUS = "activationStatus";
	public static final String USERINFO_FACEBOOKID = "facebookId";
	public static final String USERINFO_SIGNUPTYPE = "signupType";
	public static final String USERINFO_PASSWORD = "password";
	public static final String USERINFO_CREATEDON = "createdOn";
	public static final String USERINFO_ID = "id";
	public static final String USERINFO_MODIFIEDON= "modifiedOn";
	public static final String USERINFO_EMAIL = "email";
	public static final String USERINFO_VERIFICATIONCODE = "verificationCode";
	public static final String USERINFO_FULLNAME = "fullName";
	public static final String USERINFO_FIRSTNAME = "firstName";
	public static final String USERINFO_DEVICEID = "deviceId";
	public static final String USERINFO_MOBILE = "mobile";
	public static final String USERINFO_MIDDLENAME = "middleName";
	public static final String USERINFO_PLATFORM = "platform";
	public static final String USERINFO_ISMOBILEVERIFIED = "isMobileVerified";
	public static final String USERINFO_TOVERIFY = "toVerify";
	public static final String USERINFO_DEVICETYPE = "deviceType";

	public static final String HOOT_THERE_FRIEND_INFO_CREATED = "createdOn";
	public static final String HOOT_THERE_FRIEND_INFO_ID = "id";
	public static final String HOOT_THERE_FRIEND_INFO_MODIFIEDON = "modifiedOn";
	public static final String HOOT_THERE_FRIEND_INFO_STATUS = "status";
	public static final String HOOT_THERE_FRIEND_INFO_APPROVEDON = "approvedOn";
	public static final String HOOT_THERE_FRIEND_INFO_EVENTGUESTSTATUS = "eventGuestStatus";
	public static final String HOOT_THERE_FRIEND_INFO_FRIEND = "friend";
	public static final String HOOT_THERE_FRIEND_INFO_REQUESTEDBY = "requestedBy";
	
	public static final String HOOT_THERE_FRIEND_ARRAY_TAG = "Friends";
	public static final String HOO_THERE_EVENTS_ARRAY_TAG = "Events";
	public static final String HOO_THERE_NOTIFICATION_ARRAY_TAG = "notifications";
	
	public static final String HOOT_THERE_FRIEND_STATUS_A = "A";
	public static final String HOOT_THERE_FRIEND_STATUS_P = "P";
	
	public static final String TAG_EVENTGUEST_INVITEDBY = "invitedBy";
	public static final String TAG_EVENTGUEST_MODIFIEDBY = "modifiedBy";
	public static final String TAG_EVENTGUEST_NUMBER = "number";
	public static final String TAG_EVENTGUEST_USERTYPE = "userType";
	public static final String TAG_EVENTGUEST_ID = "id";
	public static final String TAG_EVENTGUEST_MODIFIEDON = "modifiedOn";
	public static final String TAG_EVENTGUEST_INVITEDON = "invitedOn";
	public static final String TAG_EVENTGUEST_STATUSOFGUEST = "statusOfGuest";
	public static final String TAG_EVENTGUEST_SMSSENT = "smsSent";
	public static final String TAG_EVENTGUEST_EMAIL = "email";
	public static final String TAG_EVENTGUEST_NAME = "name";
	public static final String TAG_EVENTGUEST_GUESTID = "guestId";
	public static final String TAG_EVENTGUEST_EMAILSENT = "emailSent";
	public static final String TAG_EVENTGUEST_USER = "user";

	public static final String TAG_STATISTICS_ID = "id";
	public static final String TAG_STATISTICS_ACCEPTEDCOUNT = "acceptedCount";
	public static final String TAG_STATISTICS_INVITEDCOUNT = "invitedCount";
	public static final String TAG_STATISTICS_HOOCAMECOUNT = "hooCameCount";
	public static final String TAG_STATISTICS_HOOTHERECOUNT = "hoothereCount";

	public static final String EVENT_STARTDATETIME = "startDateTime";
	public static final String EVENT_MODIFIEDBY = "modifiedBy";
	public static final String EVENT_EVENTTYPE = "eventType";
	public static final String EVENT_STATE = "State";
	public static final String EVENT_TIMEZONE = "timeZone";
	public static final String EVENT_STATISTICS = "statistics";
	public static final String EVENT_CITY = "city";
	public static final String EVENT_ID = "id";
	public static final String EVENT_DESCRIPTION = "description";
	public static final String EVENT_EVENTGUEST = "eventGuest";
	public static final String EVENT_EVENTGUESTS = "eventGuests";
	public static final String EVENT_LONGITUDE = "longitude";
	public static final String EVENT_VENUENAME = "venueName";
	public static final String EVENT_GUESTSTATUS = "guestStatus";
	public static final String EVENT_ZIPCODE = "zipcode";
	public static final String EVENT_STREETNAME = "streetName";
	public static final String EVENT_ENDDATETIME = "endDateTime";
	public static final String EVENT_COUNTRY = "country";
	public static final String EVENT_CREATEDON = "createdOn";
	public static final String EVENT_MODIFIEDON = "modifiedOn";
	public static final String EVENT_EVENTDESCRIPTION = "eventDescription";
	public static final String EVENT_SOURCE = "source";
	public static final String EVENT_ADDRESS = "address";
	public static final String EVENT_EVENTIMAGE = "eventImage";
	public static final String EVENT_RADIUS = "radius";
	public static final String EVENT_LATITUDE = "latitude";
	public static final String EVENT_USER = "user";
	public static final String EVENT_NAME = "name";
	public static final String EVENT_ALBUM = "eventAlbum";
	public static final String EVENT_COVERIMAGE = "coverImage";
	
	public static final String EVENT_GUEST_INVITED = "Invited";
	public static final String EVENT_GUEST_ACCEPTED = "Accepted";
	public static final String EVENT_GUEST_HOO = "Hoothere";
	public static final String EVENT_GUEST_HOO_CAME = "HooCame";

	public static final String TAG_NOTIFICATION_CREATEDON = "createdOn";
	public static final String TAG_NOTIFICATION_SENDER = "sender";
	public static final String TAG_NOTIFICATION_MESSAGE = "message";
	public static final String TAG_NOTIFICATION_ID = "id";
	public static final String TAG_NOTIFICATION_EVENTID = "eventId";
	public static final String TAG_NOTIFICATION_STATUS = "status";
	public static final String TAG_NOTIFICATION_EVENTNAME = "eventName";
	public static final String TAG_NOTIFICATION_TYPE = "type";
	public static final String TAG_NOTIFICATION_RECIPIENT = "recipient";
	public static final String TAG_NOTIFICATION_ISREAD = "isRead";
	
    //---------------- image process ------------------------//
    public static final int ACTION_REQUEST_GALLERY  = 1;
    public static final int ACTION_REQUEST_CAMERA   = 2;
    public static final int ACTION_REQUEST_PIC_CROP = 3;
    public static final int ACTION_SHARE = 4;
    public static final String TEMP_AVATAR_FILE_NAME = "temp_avatar.jpg";
    
	public static final String INVITING_FRIEND_USERID = "userId";
	public static final String INVITING_FRIEND_FIRSTNAME = "firstName";
	public static final String INVITING_FRIEND_MIDDLENAME = "middleName";
	public static final String INVITING_FRIEND_LASTNAME = "lastName";
	public static final String INVITING_FRIEND_PROFILE_PICTURE = "profile_picture";
	public static final String INVITING_FRIEND_FRIENDSHIPSTATUS = "friendshipStatus";
	public static final String INVITING_FRIEND_AVAILABILITYSTATUS = "availabilityStatus";

	public static final String EVENTALBUM_ID = "id";
	public static final String EVENTALBUM_IMAGEURL = "imageUrl";
	public static final String EVENTALBUM_THUMBNAILURL = "thumbnailUrl";
	public static final String EVENTALBUM_ALBUM =  "album";
	public static final String EVENTALBUM_NAME = "name";
	public static final String EVENTALBUM_PRIVACY = "privacy";
	public static final String EVENTALBUM_CAPTION = "caption";
	public static final String EVENTALBUM_USER = "user";
	public static final String EVENTALBUM_UPLOADEDON = "uploadedOn";
	
	public static final String NOTIFICATION_COUNT = "count";
	
	public static final String TEMP_EVENTALBUM_FILENAME = "new_event_album.jpg";
	public static final String TEMP_EVENTALBUM_CROPFILE = "crop_event_album.jpg";
	public static final String TEMP_SHARE_EVENTALBUM_FILENAME = "temp_share.jpg";
	
	public static final String DT_FORMAT_DATE = "dd MMM yyyy";
	public static final String DT_FORMAT_TIME = "h:mm aa";
	public static final String DT_FORMAT_ALL = "%s at %s";
	public static final String DT_FORMAT_MERGED = "%s till %s";
	
	public static final String ALERT_DLG_CONFIRM_NUMBER = "We are going to send text message to %s.\nThat's your number,right?";
	public static final String ALERT_DLG_CHECKIN_SUCCESS = "You have successfully checked into %s.";
	public static final String ALERT_DLG_VERIFICATION_SUCCESS = "New Verification Code has been sent to your mobile number %s";
	public static final String GEOCODER_STATUS = "status";
	public static final String GEOCODER_STATUS_OK = "OK";
	public static final String GEOCODER_RESULTS = "results";
	public static final String GEOCODER_GEOMETRY = "geometry";
	public static final String GEOCODER_LOCATION = "location";
	public static final String GEOCODER_LAT = "lat";
	public static final String GEOCODER_LNG = "lng";
	
	public static final String FORMAT_HOOT = "You have hooted %s.";
	
	public static final String SEARCH_FRIENDS_RESULT = "Result";
	public static final String SEARCH_FRIENDS_NAME = "Name";
	
	public static final String USER_AVATAR_IMAGE_URL = "%s/user/%d/image";
	public static final String USER_AVATAR_THUMBNAIL_URL = "%s/user/%d/thumbnail";
	public static final String EVENTMEMBER_COUNT_TITLE = "%d %s";
	public static final String USERNAME_TITLE = "%s %s %s";
	
	public static final String PROGRESS_DLG_TITLE = "";
	
	public static final String CHANGE_PWD_CUR = "currentPassword";
	public static final String CHANGE_PWD_NEW = "newPassword";
	
	public static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyDlFXsZsC7XoYCZ1v6sEL2autBMyJehwUE";
	public static final String REVERSEGEOCODE_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=AIzaSyDlFXsZsC7XoYCZ1v6sEL2autBMyJehwUE";
	
	public static final String TAG_CHECKIN_TYPE = "checkInType";
	public static final String CHECKINTYPE_MANUAL = "MANUAL";
	
	public static final int GEOFENCE_STROKE_WIDTH = 20;
	
	public static final String RELATIONSHIP_FRIEND = "F";
	public static final String EVENTGUESTSTATUS_INVITED = "I";
	public static final String EVENTGUESTSTATUS_WENTTHERE = "HC";
	public static final String EVENTGUESTSTATUS_ACCEPTED = "A";
	public static final String EVENTGUESTSTATUS_HT = "HT";
	public static final String EVENTGUESTSTATUS_PENDING = "P";
	public static final String INVITING_FRIEND_STATUS_PENDING = "P";
	public static final String INVITING_FRIEND_STATUS_ACCEPTED = "A";
	public static final String INVITING_FRIEND_STATUS_NONE = "N";
	
	public static final String TAG_FILE = "file";
	
	public static final String NOTIFICATIONTYPE_FRA = "FRA";
	public static final String NOTIFICATIONTYPE_FRF = "FRF";
	public static final String NOTIFICATIONTYPE_EHT = "EHT";
	public static final String NOTIFICATIONTYPE_HFR = "HFR";
	public static final String NOTIFICATIONTYPE_EI = "EI";
	
	public static final String TITLE_SELECTPICTURE = "Select picture from...";
	public static final String TITLE_CAMERA = "Camera";
	public static final String TITLE_GALLERY = "Image Gallery";
	
	public static final String USER_ACTIVATION_STATUS_A = "A";
	public static final String USER_ACTIVATION_STATUS_P = "P";
	
	public static final String FB_AVATAR_URL_LARGE = "https://graph.facebook.com/%s/picture?type=large";
	public static final String FB_AVATAR_URL_NORMAL = "https://graph.facebook.com/%s/picture?type=normal";
	public static final String FB_AVATAR_URL_TINY = "https://graph.facebook.com/%s/picture?type=tiny";
	public static final String FB_GRAPHUSER_EMAIL_TAG = "email";
	
	public static final String FB_FRIEND_ID = "id";
	public static final String FB_FRIEND_NAME = "name";
	
	public static final String SERVERURL_REGISTER = "%s/user/register";
	public static final String SERVERURL_REJECTFRIENDREQUEST = "%s/friends/%d/reject/%d";
	
	public static final String DEFAULT_NOTIFICATION_MESSAGE = "You received one message.";
	public static final String GCM_MESSAGE_TAG = "message";
}