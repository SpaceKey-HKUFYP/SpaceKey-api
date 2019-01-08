package com.spacekey.algorithm.global.typedef;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class WordTab
 * @date 2015-09-01
 *
 */
public class WordTab {
	HashMap<String, Integer> table;
	public WordTab(){ table = new HashMap<String, Integer>(); }
	
	public void add(STObject o) {
		for (String t: o.text) {
			if (table.containsKey(t)) {
				int f = table.get(t);
				table.put(t, f+1);
			}
			else table.put(t, 1);
		}
	}
	
	public void remove(STObject o) {
		for (String t: o.text) {
			if (table.containsKey(t)) {
				int f = table.get(t);
				if (f > 1) table.put(t, f-1);
				else table.remove(t);
			}
		}
	}
	
	public boolean containsAll(HashSet<String> T) { 
		return table.keySet().containsAll(T); 
	}
	
	public String toString() {
		return table.toString();
	}
}