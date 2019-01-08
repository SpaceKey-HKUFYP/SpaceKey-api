package com.spacekey.algorithm.coskq.pattern;

import java.util.Comparator;

public class EntryComparator implements Comparator<Entry> {
	@Override
	public int compare(Entry x, Entry y) {
		if (x.key < y.key) return -1;
		if (x.key > y.key) return 1;
		return 0;
	}
}