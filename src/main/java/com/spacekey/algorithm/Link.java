package com.spacekey.algorithm;

import java.util.HashSet;
/**
 * @author yxfang
 * @date 2016-11-29
 * a link of the pattern
 */
public class Link {
	public HashSet<String> keyword1 = new HashSet<String>();
	public HashSet<String> keyword2 = new HashSet<String>();
	public double lower = -1;
	public double upper = -1;
	public boolean left = false;
	public boolean right = true;
	
	public Link(HashSet<String> keyword1, HashSet<String> keyword2, double lower, double upper, boolean left, boolean right){
		this.keyword1 = keyword1;
		this.keyword2 = keyword2;
		this.lower = lower;
		this.upper = upper;
		this.left = left;
		this.right = right;
	}
	
	public String toString(){
		return keyword1 + " " + keyword2 + " " + lower + " " + upper + " " + left + " " + right;
	}
}
