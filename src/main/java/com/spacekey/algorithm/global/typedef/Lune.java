package com.spacekey.algorithm.global.typedef;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class Lune
 * @date 2015-09-03
 *
 */
public class Lune extends Group{
	Point p1, p2;
	double width = -1;
	
	public Lune(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
		width = p1.distance(p2);
	}
	
	public double width() {
		return width;
	}
	
	public double[] mbr() {
		double [] mbr = new double[4];
		
		mbr[0] = Math.max(p1.x - width, p2.x - width); // xl
		mbr[1] = Math.min(p1.x + width, p2.x + width); // xh
		mbr[2] = Math.max(p1.y - width, p2.y - width); // yl
		mbr[3] = Math.min(p1.y + width, p2.y + width); // yh
		
		return mbr;
	}
	
	public boolean contains(Point p) {
		return p.distance(p1) <= width && p.distance(p2) <= width;
	}
}
