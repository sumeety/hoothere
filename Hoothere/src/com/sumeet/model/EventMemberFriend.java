package com.sumeet.model;

public class EventMemberFriend {
	public String availabilityStatus;
	public String mobile;
	public String firstName;
	public long guestId;
	public String eventGuestStatus;
	public String middleName;
	public String fullName;
	public String email;
	public String profile_picture;
	public String status;
	public String lastName;
	public UserInformation user;
	
	public EventMemberFriend(){
		
	}
	
	public EventMemberFriend(HooThereFriend friend, EventGuest guest){
		availabilityStatus = (guest == null || guest.user == null) ? "" : guest.user.availabilityStatus;
		mobile = (guest == null || guest.user == null) ? "" : guest.user.mobile;
		firstName = (guest == null || guest.user == null) ? "" : guest.user.firstName;
		guestId = (guest == null || guest.user == null) ? 0 : guest.guestId;
		eventGuestStatus = (friend == null) ? "" : friend.eventGuestStatus;
		middleName = (guest == null || guest.user == null) ? "" : guest.user.middleName;
		fullName = (guest == null || guest.user == null) ? "" : guest.user.fullName;
		email = (guest == null || guest.user == null) ? "" : guest.email;
		profile_picture = (guest == null || guest.user == null) ? "" : guest.user.profile_picture;
		status = friend == null ? "" : friend.status;
		lastName = (guest == null || guest.user == null) ? "" : guest.user.lastName;
		user = guest.user;
	}
}
