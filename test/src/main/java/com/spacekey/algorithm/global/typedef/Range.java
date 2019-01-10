package com.spacekey.algorithm.global.typedef;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class Range, for representing angle-ranges
 * @date 2015-09-04
 *
 */
public class Range {
	public double l, h;
	
	public Range(double minAng, double maxAng) {
		l = (minAng < 0? 360+minAng:minAng);
		h = (maxAng > 360? maxAng - 360: maxAng);
	}
	
	public boolean intersect(Range r) {
		return !(r.l > h || r.h < l);
	}
	
	public boolean covers(Range r) {
		if (l <= h && r.l <= r.h) return (r.l >= l && r.h <= h);
		else if (l > h && r.l <= r.h) return (r.l >= l || r.h <= h);
		else if (l <= h && r.l > r.h) return false;
		else return (r.l >= l && r.h <= h);
	}
	
	public boolean covers(double x) {
		if (l <= h) return (l <= x && x <= h);
		else return !(h < x && x < l);
	}
	
	public String toString() {
		return "["+l+", "+h+"]";
	}
}
