/**
 * 
 */
package com.spacekey.algorithm.global.datastruct.cbrtree;

import java.util.HashSet;

import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.STObject;


/**
 * CLEntry
 * @author Dong-Wan Choi
 * 2015. 9. 2.
 */
public class CLEntry extends CEntry{

	public STObject obj;
	
	public CLEntry(double xl, double xh, double yl, double yh, STObject o) {
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
	
	public int intersectCnt(HashSet<String> T, Words w) {
		HashSet<String> tmp = new HashSet<String>(T);
		tmp.retainAll(obj.text);
		return tmp.size();
	}
	
	public int maxCard() {
		return obj.text.size();
	}
	public int minCard() {
		return obj.text.size();
	}

}
