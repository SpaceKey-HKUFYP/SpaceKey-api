package com.spacekey.algorithm.spm.algorithm.queue;

/**
 * @author yxfang
 * @date 2016-8-17
 * the cost of join a pair of vertices
 * Note: the direction of an edge is well kept
 */
public class CPair {
	public int id1 = -1;
	public int id2 = -1;
	public String pair = null;
	public int cost = -1;
	
	public CPair(int id1, int id2, int cost){
		this.id1 = id1;
		this.id2 = id2;
		this.cost = cost;
	}
	
	//two attributes
	public boolean linkOut = false;//type of an edge [true: outer-edge] or [false: inner-edge]
	
	public CPair(int id1, int id2, boolean linkOut){
		this.id1 = id1;
		this.id2 = id2;
		this.linkOut = linkOut;
	}
	
	public String toString(){
		return "[id1=" + id1 + ", id2=" + id2 + ", linkOut= " + linkOut + "]";
	}
}
