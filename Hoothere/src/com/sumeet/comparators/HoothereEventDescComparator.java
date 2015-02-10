package com.sumeet.comparators;

import java.util.Comparator;

import com.sumeet.model.HoothereEvent;

public class HoothereEventDescComparator  implements Comparator<Object>{
	@Override
	public int compare(Object lhs, Object rhs) {
		long disp1 = getDisplayName(lhs);
		long disp2 = getDisplayName(rhs);
		if (disp1 == 0) return 1;
		if (disp2 == 0) return -1;
		if (disp1 > disp2) return -1;
		if (disp1 < disp2) return 1;
		return 0;
	}
	
	private long getDisplayName(Object event) {
		if (event instanceof HoothereEvent) {
			return ((HoothereEvent) event) != null ? ((HoothereEvent) event).endDateTime : 0;
		}
		return 0;
	}
}
