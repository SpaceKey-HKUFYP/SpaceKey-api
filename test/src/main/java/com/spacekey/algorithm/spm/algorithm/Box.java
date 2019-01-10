package com.spacekey.algorithm.spm.algorithm;

import com.spacekey.algorithm.spm.irtree.Point;

/**
 * @author yxfang
 * @date 2016-12-22
 * A node for organizing the join results
 * (1) Improve the efficiency (No need to perform array-copy)
 * (2) Save space
 */
public class Box {
	private Point point = null;
	private Box childArr[] = null;
	
	public Box(Point point){
		this.point = point;
	}

	public Box(Point point, Box childArr[]){
		this.point = point;
		this.childArr = childArr;
	}
	
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Box[] getChildArr() {
		return childArr;
	}

	public void setChildArr(Box[] childArr) {
		this.childArr = childArr;
	}
}
