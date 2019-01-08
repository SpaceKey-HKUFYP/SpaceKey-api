package com.spacekey.algorithm.global.datastruct.brtree;

import java.util.ArrayList;

import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.rtree.Pair;
import com.spacekey.algorithm.global.util.Bitmap;
import com.spacekey.algorithm.global.util.Debug;

public class BNode {
	public boolean isleaf;   // leaf = true
	ArrayList<BEntry> entryList;
	BNode parent;
	Pair x;
	Pair y;
	public Bitmap bmp; // keyword bitmap
	
	public BNode(boolean flag){
		isleaf = flag;
		parent = null;
		x = new Pair();
		y = new Pair();
		initEntries();
	}
	public BNode(){
		isleaf = false;
		parent = null;
		x = new Pair();
		y = new Pair();
		initEntries();
	}
	public void initEntries(){
		entryList = new ArrayList<BEntry>();
		x.l = Env.MaxCoord;
		x.h = 0;
		y.l = Env.MaxCoord;
		y.h = 0;
		bmp = null;
	}
	public int size(){
		return entryList.size();
	}
	public BEntry get(int i){
		return entryList.get(i);
	}
	public void add(BEntry e){
		entryList.add(e);
		updateMBR(e);
		updateBitmap(e, BRTree.W);
	}
	private void updateMBR(BEntry e) {
		x.l = Math.min(x.l, e.x.l);
		x.h = Math.max(x.h, e.x.h);
		y.l = Math.min(y.l, e.y.l);
		y.h = Math.max(y.h, e.y.h);
	}
	public void updateBitmap(BEntry e, Words w) {
		Bitmap tbmp;
		if (e instanceof BLEntry) tbmp = w.getBitmap(((BLEntry)e).obj.text);
		else tbmp = e.child.bmp;
		
		if (bmp == null) bmp = new Bitmap(tbmp);
		else bmp.or(tbmp);
	}
	public void remove(BEntry e){
		entryList.remove(e);
		updateMBR(e);
	}
	public BEntry remove(int a){
		BEntry e = entryList.remove(a);
		updateMBR(e);
		return e;
	}
	public double overlap(int i, BEntry e){// overlap cost if e is inserted into i-th entry
		double xl, xh, yl, yh;
		xl = Math.min(get(i).x.l, e.x.l);
		xh = Math.max(get(i).x.h, e.x.h);
		yl = Math.min(get(i).y.l, e.y.l);
		yh = Math.max(get(i).y.h, e.y.h);
		BEntry k = new BEntry(xl, xh, yl, yh);
		
		double cost = 0;
		for (int j = 0; j < size(); j++)
		{
			if (i == j) continue;
			cost += k.overlap(get(j));
		}
		return cost;
	}
	public double diffArea(BEntry e) { // area difference if e is inserted
		double s = area();
		double xl, xh, yl, yh;
		xl = Math.min(x.l, e.x.l);
		xh = Math.max(x.h, e.x.h);
		yl = Math.min(y.l, e.y.l);
		yh = Math.max(y.h, e.y.h);
		double r = (xh-xl)*(yh-yl);
		return r-s;
	}
	public BEntry find(BNode n){
		for (int i=0; i<size(); i++)
			if (get(i).child.equals(n)) return get(i);
		return null;
	}	
	public double area(){        // return area
		return (x.h-x.l)*(y.h-y.l); 

	}
	public double margin(){
		return 2*((x.h-x.l)+(y.h-y.l));
	}
	
	public String toString() {
		return "("+ this.x.l + "," + this.x.h + ")" + "("+ this.y.l + "," + this.y.h + ")";
	}
}
