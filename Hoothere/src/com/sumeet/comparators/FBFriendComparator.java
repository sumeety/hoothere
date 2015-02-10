package com.sumeet.comparators;

import java.util.Comparator;

import com.sumeet.model.FBFriend;

public class FBFriendComparator implements Comparator<Object> {

	@Override
	public int compare(Object lhs, Object rhs) {
		String name1 = getDisplayName(lhs);
		String name2 = getDisplayName(rhs);
		return String.CASE_INSENSITIVE_ORDER.compare(name1, name2);
	}
	
	private String getDisplayName(Object friend) {
		if (friend instanceof FBFriend) {
			return ((FBFriend) friend) != null ? ((FBFriend) friend).name : "";
		}
		return "";
	}
}