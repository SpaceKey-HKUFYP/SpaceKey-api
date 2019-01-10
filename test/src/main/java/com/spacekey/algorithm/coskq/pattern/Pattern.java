package com.spacekey.algorithm.coskq.pattern;


import java.util.HashSet;

/**
 * 
 * @author wangj
 * @date Aug 19, 2017
 */
public class Pattern {
	private Point loc;							// the location
	private HashSet<String> keyword = null; 	// the set of keywords
	
	public Pattern(){
		this.keyword = new HashSet<String>();
	}
	
	public Pattern(Point loc, HashSet<String> keyword) {
		this.loc = loc;
		this.keyword = keyword;
	}
	
	public void setLoc(Point loc) {
		this.loc = loc;
	}

	public Point getLoc() {
		return this.loc;
	}
	
	public void setKeyword(HashSet<String> keyword) {
		this.keyword = keyword;
	}
	
	public void addKeyword(String keyword) {
		if (this.keyword == null)
			this.keyword = new HashSet<String>();
		this.keyword.add(keyword);
	}
	
	public HashSet<String> getKeyword() {
		return this.keyword;
	}
}
