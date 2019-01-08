package com.spacekey.algorithm.spm.irtree;

/**
 * @author yxfang
 * @date 2016-8-8
 * The MBR of internal nodes of the IR-tree
 */
public class MBR {
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	
	public MBR(double minX, double minY, double maxX, double maxY){
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}
	
	public String toString(){
		return "[" + minX + ", " + minY + ", " + maxX + ", " + maxY + "]";
	}
	
	public double obtainCenterX(){
		return (this.maxX + this.minX) * 0.5;
	}
	
	public double obtainCenterY(){
		return (this.maxY + this.minY) * 0.5;
	}
	
	public double obtainXExtent(){
		return this.maxX - this.minX;
	}
	
	public double obtainYExtent(){
		return this.maxY - this.minY;
	}
}
