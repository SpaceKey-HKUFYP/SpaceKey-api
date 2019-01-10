package com.spacekey.algorithm.coskq.pattern;
import com.spacekey.algorithm.coskq.index.ANode;

public class Entry {
	public ANode irTree;
	public Point obj;
	public double key;
	public boolean isObject;
	
	public Entry(ANode irTree, Point obj, double key) {
		this.key = key;
		if (irTree == null) {
			this.isObject = true;
			this.irTree = null;
			this.obj = obj;
		} else {
			this.isObject = false;
			this.irTree = irTree;
			this.obj = null;
		}
	}
}