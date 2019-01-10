package com.spacekey.algorithm.global.datastruct.docindex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.TreeMultiset;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.Dataset;
import com.spacekey.algorithm.global.typedef.STObject;

public class InvertedFile {
	HashMap<String, TreeMultiset<STObject>> map;
	
	public InvertedFile() {
		map = new HashMap<String, TreeMultiset<STObject>>();
	}
	
	public TreeMultiset<STObject> getList(String t) {
		return map.get(t);
	}
	
	public void add(STObject o) {
		TreeMultiset<STObject> postings;
		for (String t: o.text) {
			postings = map.get(t);
			if (postings == null) {
				postings = TreeMultiset.create(STObject.CompareLoc);
				map.put(t, postings);
			}
			postings.add(o);
		}
	}
	
	public int freq(String t) {
		return getList(t).size();
	}
		
	public Set<String> keywords() {
		return map.keySet();
	}
	
	public int size() {
		return map.size();
	}

	
	/**
	 * This method only gets the relevant objects to T 
	 * without removing irrelevant keywords
	 */
	public Dataset getRelevantDB(HashSet<String> T, Words w) {
		HashMap<Integer, STObject> tmp = new HashMap<Integer, STObject>();
		
		for (String t: T) {
			TreeMultiset<STObject> list = getList(t);
			for (STObject o: list) {
				if (!tmp.containsKey(o.id)) {
					w.add(o);
					tmp.put(o.id, o);
				} 
			}
		}
		return new Dataset(tmp.values());
	}


	/**
	 * This method does the followings:
	 * (1) filter the objects having at least one keyword in T
	 * (2) remove all irrelevant keywords from objects
	 * @param T, the set of query keywords
	 * @return the set of all relevant objects to T
	 */
	public Dataset filter(HashSet<String> T) {
		HashMap<Integer, STObject> tmp = new HashMap<Integer, STObject>();
		STObject a;
		
		for (String t: T) {
			TreeMultiset<STObject> list = getList(t);
			for (STObject o: list) {
				if (tmp.containsKey(o.id)) {
					a = tmp.get(o.id);
				} else {
					a = new STObject(o.id, o.loc.x, o.loc.y);
					tmp.put(a.id, a);
				}
				a.text.add(t);
			}
		}
		return new Dataset(tmp.values());
	}
	
	public int maxFreq() {
		int freq, maxFreq = Integer.MIN_VALUE;
		for (String t: map.keySet()) {
			freq = freq(t);
			if (maxFreq < freq) {
				maxFreq = freq;
			}
		}
		return maxFreq;
	}
}
