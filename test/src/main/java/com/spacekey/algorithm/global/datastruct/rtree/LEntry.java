package com.spacekey.algorithm.global.datastruct.rtree;

import com.spacekey.algorithm.global.typedef.STObject;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class LEntry, this is the leaf entry class
 * @date 2015-08-26
 *
 */
public class LEntry extends Entry {

	public STObject obj;
	
	public LEntry(double xl, double xh, double yl, double yh, STObject o) {
		super(xl, xh, yl, yh);
		obj = o;
	}

}
