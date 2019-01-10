package com.spacekey.algorithm.global.typedef;

import java.util.ArrayList;

import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.brtree.BEntry;
import com.spacekey.algorithm.global.datastruct.brtree.BLEntry;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class LinList
 * @date 2015-08-28
 *
 */
public class LinList {
	ArrayList<BEntry> l;
	
	public LinList () {
		this.l = new ArrayList<BEntry>();
	}

	public LinList (ArrayList<BEntry> l) {
		this.l = l;
	}

	public int size() {
		return l.size();
	}
	
	public void add(STObject o) {
		Point p = o.loc;
		BLEntry e = new BLEntry(p.x, p.x, p.y, p.y, o);
		l.add(e);
	}

	public ArrayList<BEntry> rangeSearch(double xl, double xh, double yl, double yh) {
		ArrayList<BEntry> result = new ArrayList<BEntry>(); 
		for (BEntry e : l){
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){ 
				result.add(e);
			}
		}
		return result;
	}
	
	public BEntry nextNN(Point q, String t, Words w) {
		BEntry nn = null;
		double dist, mindist = Double.MAX_VALUE;
		for (BEntry e: l) {
			dist = e.distTo(q);
			if (mindist > dist && e.contains(t, w)) {
				nn = e;
				mindist = dist;
			}
		}
		nn.dist = mindist;
		return nn;
	}
	
	public ArrayList<BEntry> kNNSearch(Point q, String t, Words w, int k) {
		ArrayList<BEntry> knns = new ArrayList<BEntry>();
		for (int i=0; i < k; i++){
			BEntry next = nextNN(q, t, w);
			knns.add(next);
			l.remove(next);
		}
		l.addAll(knns);
		return knns;
	}

	public BEntry nextNN(Point q) {
		BEntry nn = null;
		double dist, mindist = Double.MAX_VALUE;
		for (BEntry e: l) {
			dist = e.distTo(q);
			if (mindist > dist) {
				nn = e;
				mindist = dist;
			}
		}
		nn.dist = mindist;
		return nn;
	}
	
	public ArrayList<BEntry> kNNSearch(Point q, int k) {
		ArrayList<BEntry> knns = new ArrayList<BEntry>();
		for (int i=0; i < k; i++){
			BEntry next = nextNN(q);
			knns.add(next);
			l.remove(next);
		}
		l.addAll(knns);
		return knns;
	}
}
