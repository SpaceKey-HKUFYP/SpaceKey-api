package com.spacekey.algorithm.global.datastruct.polartree;

import java.util.Comparator;

import com.spacekey.algorithm.global.typedef.Range;
import com.spacekey.algorithm.global.typedef.STObject;
import com.spacekey.algorithm.global.util.Bitmap;


/**
 * @author Dong-Wan Choi at SFU, CA
 * @class PItem
 * @date 2015-09-03
 *
 */
public class RBItem {

	public double ang; // angle
	public Range rng;
	public double dist;
	public Bitmap treebmp; // keyword bitmap for its subtree
	public Bitmap objbmp; // keyword bitmap for its own object (never change)
	public STObject obj;
	
	public RBItem(double theta, STObject o, double d) {
		ang = theta;
		rng = new Range(ang, ang);
		obj = o;
		dist = d;
		objbmp = new Bitmap(RBTree.W.getBitmap(obj.text));
		treebmp = new Bitmap(objbmp);
	}
	
	public double getKey() {
		return ang;
	}
	
	public static Comparator<RBItem> ComparePolar = new Comparator<RBItem>() {
		public int compare(RBItem item1, RBItem item2) {
			if (item1.ang > item2.ang) return 1;
			else if (item1.ang < item2.ang) return -1;
			else return 0;
		}
	};
	
	public boolean coversWith(RBItem item) {
		Bitmap tmp = new Bitmap(treebmp);
		tmp.or(item.objbmp);
		tmp.or(RBTree.polebmp);
		return tmp.isAllSet();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(obj.id);
		sb.append("("+ang+")");
		sb.append(rng);
		sb.append(objbmp+ " | ");
		sb.append(treebmp);
		return sb.toString();
	}
	
	public void update(RBItem item) {
		rng.l = Math.min(rng.l, item.ang);
		rng.h = Math.max(rng.h, item.ang);
		
		if (treebmp == null) treebmp = new Bitmap(objbmp);
		treebmp.or(item.objbmp);
	}
	
	public void update(RBItem litem, RBItem ritem) {
		treebmp = new Bitmap(objbmp);
		if (litem == null && ritem == null) {
			rng.l = ang;
			rng.h = ang;
		} else if (litem == null) {
			rng.l = ritem.rng.l;
			rng.h = ritem.rng.h;
			treebmp.or(ritem.treebmp);
		} else if (ritem == null) {
			rng.l = litem.rng.l;
			rng.h = litem.rng.h;
			treebmp.or(litem.treebmp);
		} else {
			rng.l = Math.min(litem.rng.l, ritem.rng.l);
			rng.h = Math.max(litem.rng.h, ritem.rng.h);
			treebmp.or(litem.treebmp); treebmp.or(ritem.treebmp);
		}
	}
}
