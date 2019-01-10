package com.spacekey.algorithm.coskq;

import java.util.HashMap;
import java.util.HashSet;

import com.spacekey.algorithm.coskq.index.ANode;
import com.spacekey.algorithm.coskq.index.IRTreeOperator;
import com.spacekey.algorithm.coskq.pattern.Entry;
import com.spacekey.algorithm.coskq.pattern.Pattern;

import java.util.ArrayList;

public class Util {
	
	public static int hash(HashSet<String> s, Pattern query, HashMap<String, Integer> map) {
		int hashCode = 0;
		for (String keyword : query.getKeyword()) {
			if (s.contains(keyword))
				hashCode += (1 << map.get(keyword));
		}
		return hashCode;
	}
	
	public static boolean belongTo(HashSet<String> x, ArrayList<HashSet<String>> y, Pattern query, HashMap<String, Integer> map) {
		boolean belongTo = false;
		for (HashSet<String> is : y) {
			if (Util.hash(is, query, map) == Util.hash(x, query, map))
				belongTo = true;
		}
		return belongTo;
	}
	
	public static HashSet<String> intersect(HashSet<String> x, HashSet<String> y) {
		HashSet<String> intersect = new HashSet<String>();
		for (String keyword : x) {
			if (y.contains(keyword))
				intersect.add(keyword);
		}
		return intersect;
	}
	
	public static HashSet<String> intersect(HashSet<String> x, ANode node) {
		HashSet<String> intersect = new HashSet<String>();
		for (String keyword : x) {
			if (IRTreeOperator.hasKeyword(node, keyword))
				intersect.add(keyword);
		}
		return intersect;
	}
	
	public static HashSet<String> intersect(HashSet<String> x, Entry e) {
		HashSet<String> intersect = new HashSet<String>();
		for (String keyword : x) {
			if (e.isObject) {
				if (e.obj.keywords.contains(keyword))
					intersect.add(keyword);
			} else {
				if (IRTreeOperator.hasKeyword(e.irTree, keyword))
					intersect.add(keyword);
			}
		}
		return intersect;
	}
	
	public static void remove(HashSet<String> x, ArrayList<HashSet<String>> y, Pattern query, HashMap<String, Integer> map) {
		for (int i=0 ; i<y.size() ; i++) {
			if (Util.hash(x, query, map) == Util.hash(y.get(i), query, map)) {
				y.remove(i); break;
			}
		}
	}
	
	public static void insert(HashSet<String> x, ArrayList<HashSet<String>> y, Pattern query, HashMap<String, Integer> map) {
		for (int i=0 ; i<y.size() ; i++) {
			if (Util.hash(x, query, map) == Util.hash(y.get(i), query, map)) return;
		}
		y.add(x);
	}
}
