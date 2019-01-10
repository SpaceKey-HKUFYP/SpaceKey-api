package com.spacekey.algorithm.global.typedef;

import java.util.HashMap;
import java.util.HashSet;


/**
 * 	Cardinality Table that maps a cardinality value k 
 * 	to the set of objects having k keywords
 * @author Dong-Wan Choi at SFU, CA
 * @class HashTable
 * @date 2015-09-02
 *
 */
public class CardTab {
	public HashMap<Integer, HashSet<STObject>> tb;
	public int min, max;

	public CardTab() {
		tb = new HashMap<Integer, HashSet<STObject>>();
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
	}
	
	
	public void add(STObject obj) {
		add(obj, obj.text.size());
	}
	
	public void add(STObject obj, int card) {
		HashSet<STObject> set = tb.get(card);
		if (set == null) {
			set = new HashSet<STObject>();
			tb.put(card, set);
		}
		set.add(obj);
		
		if (card > max) max = card;
		if (card < min) min = card;
	}
	
	public HashSet<STObject> get(int card) {
		return tb.get(card);
	}
	
	public boolean containsKey(int card) {
		return tb.containsKey(card);
	}
}
