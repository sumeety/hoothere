package com.sumeet.comparators;

import java.util.Comparator;

import com.sumeet.model.ContactMember;

public class PhoneContactsComparator implements Comparator<Object> {
	@Override
	public synchronized int compare(Object lhs, Object rhs) {
		String name1 = getDisplayName(lhs);
		String name2 = getDisplayName(rhs);
		return String.CASE_INSENSITIVE_ORDER.compare(name1, name2);
	}
	
	private String getDisplayName(Object user) {
		if (user instanceof ContactMember) {
			return ((ContactMember) user).mName;
		}
		return String.valueOf(user);
	}
}
