package com.spacekey.algorithm.coskq.pattern;

import java.util.HashSet;

import com.spacekey.algorithm.coskq.index.MBR;

/**
 * @author wangj
 * @date Aug 21, 2017
 */
public class Point {
	
	public int id = -1;
	public double x = -1;
	public double y = -1;
	public HashSet<String> keywords;
	
	public Point(int id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
		this.keywords = new HashSet<String>();
	}
	
	public static double dist(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	public static double minDist(Point a, MBR b) {
		if (b.getMinX() <= a.x && a.x <= b.getMaxX() && b.getMinY() <= a.y && a.y <= b.getMaxY()) return 0;
		double dist1 = (a.x - b.getMinX()) * (a.x - b.getMinX()) + (a.y - b.getMinY()) * (a.y - b.getMinY());
		double dist2 = (a.x - b.getMinX()) * (a.x - b.getMinX()) + (a.y - b.getMaxY()) * (a.y - b.getMaxY());
		double dist3 = (a.x - b.getMaxX()) * (a.x - b.getMaxX()) + (a.y - b.getMinY()) * (a.y - b.getMinY());
		double dist4 = (a.x - b.getMaxX()) * (a.x - b.getMaxX()) + (a.y - b.getMaxY()) * (a.y - b.getMaxY());
		return Math.sqrt(Double.min(Double.min(dist1, dist2), Double.min(dist3, dist4)));
	}
	
	public static double maxDist(Point a, MBR b) {
		double dist1 = (a.x - b.getMinX()) * (a.x - b.getMinX()) + (a.y - b.getMinY()) * (a.y - b.getMinY());
		double dist2 = (a.x - b.getMinX()) * (a.x - b.getMinX()) + (a.y - b.getMaxY()) * (a.y - b.getMaxY());
		double dist3 = (a.x - b.getMaxX()) * (a.x - b.getMaxX()) + (a.y - b.getMinY()) * (a.y - b.getMinY());
		double dist4 = (a.x - b.getMaxX()) * (a.x - b.getMaxX()) + (a.y - b.getMaxY()) * (a.y - b.getMaxY());
		return Math.sqrt(Double.max(Double.max(dist1, dist2), Double.max(dist3, dist4)));
	}
	
	public static double type1Cost(HashSet<Point> objList, Point query) {
		double sumoqDist = 0;
		for (Point o : objList)
			sumoqDist += Point.dist(o, query);
		return sumoqDist;
	}
	
	public static double type2Cost(HashSet<Point> objList, Point query) {
		double maxoqDist = 0, maxooDist = 0;
		for (Point o1 : objList) {
			if (Point.dist(o1, query) > maxoqDist)
				maxoqDist = Point.dist(o1, query);
			for (Point o2 : objList) {
				if (Point.dist(o1, o2) > maxooDist)
					maxooDist = Point.dist(o1, o2);
			}
		}
		return maxoqDist + maxooDist;
	}
	
	public static double type3Cost(HashSet<Point> objList, Point query) {
		double maxooDist = 0;
		for (Point o1 : objList) {
			for (Point o2 : objList) {
				if (Point.dist(o1, o2) > maxooDist)
					maxooDist = Point.dist(o1, o2);
			}
			if (Point.dist(o1, query) > maxooDist)
				maxooDist = Point.dist(o1, query);
		}
		return maxooDist;
	}
	
	public void clearKeywords() {
		this.keywords.clear();
	}
	
	public void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}
	
    @Override
    public int hashCode() {
    	return this.id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Point) {
        	Point otherObj = (Point) other;
            return (this.id == otherObj.id);
        }
        return false;
    }
}
