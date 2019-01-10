package com.spacekey.algorithm.global.datastruct.brtree;

import java.util.HashSet;

import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.STObject;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class LEntry, this is the leaf entry class
 * @date 2015-08-26
 *
 */
public class BLEntry extends BEntry {

	public STObject obj;
	
	public BLEntry(double xl, double xh, double yl, double yh, STObject o) {
		super(xl, xh, yl, yh);
		obj = o;
	}
	
	public boolean contains(String t, Words w) {
		return obj.text.contains(t);
	}
	
	public boolean intersect(HashSet<String> T, Words w) {
		HashSet<String> tmp = new HashSet<String>(T);
		tmp.retainAll(obj.text); // intersection T and Object's text
		return (!tmp.isEmpty());
	}
}
