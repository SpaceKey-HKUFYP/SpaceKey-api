package com.spacekey.algorithm.global;

import java.util.HashSet;

/**
 * @author wangj
 * @date Sep 2, 2017
 */
public class Point {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int id;
	public double x = -1;
	public double y = -1;
	public HashSet<String> keywords;
	
	public Point(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.keywords = new HashSet<String>();
	}
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
		this.keywords = new HashSet<String>();
	}
	
	public static double dist(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	public static double averagePairwiseDist(HashSet<Point> points) {
		double temp = 0;
		for (Point x : points) {
			for (Point y : points) {
				if (x != y) temp += Point.dist(x, y);
			}
		}
		temp = temp / ((points.size() - 1) * points.size());
		return temp;
	}
	
	public static double maxPairwiseDist(HashSet<Point> points) {
		double temp = 0;
		for (Point x : points) {
			for (Point y : points) {
				temp = (temp < Point.dist(x, y)) ? Point.dist(x, y) : temp;
			}
		}
		return temp;
	}
	
	public static double maxDistToPoint(double x, double y, HashSet<Point> points) {
		double temp = 0;
		Point query_loc = new Point(x, y);
		for (Point point : points) {
			temp = (temp < Point.dist(query_loc, point)) ? Point.dist(query_loc, point) : temp;
		}
		return temp;
	}
	
	public void clearKeywords() {
		this.keywords.clear();
	}
	
	public void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}
	
	public void addKeywordAll(HashSet<String> keyword) {
		this.keywords.addAll(keyword);
	}
}
