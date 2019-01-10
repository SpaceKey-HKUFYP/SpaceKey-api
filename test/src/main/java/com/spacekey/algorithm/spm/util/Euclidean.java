package com.spacekey.algorithm.spm.util;

import com.spacekey.algorithm.spm.irtree.MBR;
import com.spacekey.algorithm.spm.irtree.Point;

/**
 * @author yxfang
 * @date 2016-8-9
 * Compute the Euclidean distance
 */
public class Euclidean {
	//the distance between two points
	public static double dist(double x1, double y1, double x2, double y2){
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	//the distance of two objects
	public static double dist(double loc[][], int id1, int id2){
		return Math.sqrt((loc[id1][0] - loc[id2][0]) * (loc[id1][0] - loc[id2][0]) 
				+ (loc[id1][1] - loc[id2][1]) * (loc[id1][1] - loc[id2][1]));
	}

	public static double dist(Point p1, Point p2){
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}
	
	public static double dist(double p1[], double p2[]){
		return Math.sqrt((p1[0] - p2[0]) * (p1[0] - p2[0]) + (p1[1] - p2[1]) * (p1[1] - p2[1]));
	}
	
	//the minimum distance between two MBRs
	public static double obtainMinDist(MBR mbr1, MBR mbr2){
		// 4 * 7 * 1
		// 5 * O * 2
		// 6 * 8 * 3
		double minX1 = mbr1.getMinX(), minY1 = mbr1.getMinY(), maxX1 = mbr1.getMaxX(), maxY1 = mbr1.getMaxY(),
				   minX2 = mbr2.getMinX(), minY2 = mbr2.getMinY(), maxX2 = mbr2.getMaxX(), maxY2 = mbr2.getMaxY();
		
		if(maxX1 < minX2){
			if(maxY1 < minY2){//case 1
				return dist(maxX1, maxY1, minX2, minY2);
			}else if(minY1 > maxY2){//case 3
				return dist(maxX1, minY1, minX2, maxY2);
			}else{
				return minX2 - maxX1;//case 2
			}
		}else if(minX1 > maxX2){
			if(maxY1 < minY2){//case 4
				return dist(minX1, maxY1, maxX2, minY2);
			}else if(minY1 > maxY2){//case 6
				return dist(minX1, minY1, maxX2, maxY2);
			}else{
				return minX1 - maxX2;//case 5
			}
		}else{
			if(maxY1 < minY2){
				return minY2 - maxY1;//case 7
			}else if(minY1 > maxY2){
				return minY1 - maxY2;//case 8
			}else{
				return 0;
			}
		}
	}
	
	//the maximum distance between two MBRs
	public static double obtainMaxDist(MBR mbr1, MBR mbr2){
		// 4 * 7 * 1
		// 5 * O * 2
		// 6 * 8 * 3
		long s = System.nanoTime();
		double minX1 = mbr1.getMinX(), minY1 = mbr1.getMinY(), maxX1 = mbr1.getMaxX(), maxY1 = mbr1.getMaxY(),
			   minX2 = mbr2.getMinX(), minY2 = mbr2.getMinY(), maxX2 = mbr2.getMaxX(), maxY2 = mbr2.getMaxY();
		
		if(maxX1 < minX2){
			if(maxY1 < minY2){//case 1
//				long e = System.nanoTime();   if((e - s) / 1000000 > 500)   System.out.println("fuck-MaxDist1!");
				return dist(minX1, minY1, maxX2, maxY2);
			}else if(minY1 > maxY2){//case 3
//				long e = System.nanoTime();   if((e - s) / 1000000 > 500)   System.out.println("fuck-MaxDist3!");
				return dist(minX1, maxY1, maxX2, minY2);
			}else{
				long bb = System.nanoTime();
				double mpA[][] = {{minX1, minY1}, {minX1, maxY1}};
				double mpB[][] = {{maxX2, minY2}, {maxX2, maxY2}};
				long m = System.nanoTime();
				
				double max = -1;
				for(int i = 0;i < mpA.length;i ++){
					for(int j = 0;j < mpB.length;j ++){
						double distance = dist(mpA[i][0], mpA[i][1], mpB[j][0], mpB[j][1]);
						if(distance > max)   max = distance;
					}
				}
//				long e = System.nanoTime();
//				if((e - s) / 1000000 > 500){
//					System.out.println("fuck-MaxDist2! middle=" + (m - s) / 1000000 + " " + (m - bb) / 1000000);
//				}
				return max;//case 2
			}
		}else if(minX1 > maxX2){
			if(maxY1 < minY2){//case 4
//				long e = System.nanoTime();   if((e - s) / 1000000 > 500)   System.out.println("fuck-MaxDist4!");
				return dist(maxX1, minY1, minX2, maxY2);
			}else if(minY1 > maxY2){//case 6 ---bug---20161212
//				long e = System.nanoTime();   if((e - s) / 1000000 > 500)   System.out.println("fuck-MaxDist6!");
				return dist(maxX1, maxY1, minX2, minY2);
			}else{
				long bb = System.nanoTime();
				double mpA[][] = {{maxX1, minY1}, {maxX1, maxY1}};//point2 of MBR1;
				double mpB[][] = {{minX2, minY2}, {minX2, maxY2}};//point2 of MBR2;
				long m = System.nanoTime();
				
				double max = -1;
				for(int i = 0;i < mpA.length;i ++){
					for(int j = 0;j < mpB.length;j ++){
						double distance = dist(mpA[i][0], mpA[i][1], mpB[j][0], mpB[j][1]);
						if(distance > max)   max = distance;
					}
				}
//				long e = System.nanoTime();
//				if((e - s) / 1000000 > 500){
//					System.out.println("fuck-MaxDist5! middle=" + (m - s) / 1000000 + " " + (m - bb) / 1000000);
//				}
				return max;//case 5
			}
		}else{
			if(maxY1 < minY2){
				long bb = System.nanoTime();
				double mpA[][] = {{minX1, minY1}, {maxX1, minY1}};//point2 of MBR1;
				double mpB[][] = {{minX2, maxY2}, {maxX2, maxY2}};//point2 of MBR2;
				long m = System.nanoTime();
				
				double max = -1;
				for(int i = 0;i < mpA.length;i ++){
					for(int j = 0;j < mpB.length;j ++){
						double distance = dist(mpA[i][0], mpA[i][1], mpB[j][0], mpB[j][1]);
						if(distance > max)   max = distance;
					}
				}
//				long e = System.nanoTime();
//				if((e - s) / 1000000 > 500){
//					System.out.println("fuck-MaxDist7! middle=" + (m - s) / 1000000 + " " + (m - bb) / 1000000);
//				}
				return max;//case 7
			}else if(minY1 > maxY2){
				long bb = System.nanoTime();
				double mpA[][] = {{minX1, maxY1}, {maxX1, maxY1}};//point2 of MBR1;
				double mpB[][] = {{minX2, minY2}, {maxX2, minY2}};//point2 of MBR2;
				long m = System.nanoTime();
				
				double max = -1;
				for(int i = 0;i < mpA.length;i ++){
					for(int j = 0;j < mpB.length;j ++){
						double distance = dist(mpA[i][0], mpA[i][1], mpB[j][0], mpB[j][1]);
						if(distance > max)   max = distance;
					}
				}
//				long e = System.nanoTime();
//				if((e - s) / 1000000 > 500){
//					System.out.println("fuck-MaxDist8! middle=" + (m - s) / 1000000 + " " + (m - bb) / 1000000);
//				}
				return max;//case 8
			}else{
				long bb = System.nanoTime();
				double mpA[][] = {{minX1, minY1}, {minX1, maxY1}, {maxX1, minY1}, {maxX1, maxY1}};
				double mpB[][] = {{minX2, minY2}, {minX2, maxY2}, {maxX2, minY2}, {maxX2, maxY2}};//point4 of MBR2;
				long m = System.nanoTime();
				
				double max = -1;
				for(int i = 0;i < mpA.length;i ++){
					for(int j = 0;j < mpB.length;j ++){
						double distance = dist(mpA[i][0], mpA[i][1], mpB[j][0], mpB[j][1]);
						if(distance > max)   max = distance;
					}
				}
//				long e = System.nanoTime();
//				if((e - s) / 1000000 > 500){
//					System.out.println("fuck-MaxDist9! middle=" + (m - s) / 1000000 + " " + (m - bb) / 1000000);
//				}
				return max;//case center
			}
		}
	}

	//the minimum distance between a point and an MBR
	public static double obtainPMinDist(double x, double y, MBR mbr){
		if(x >= mbr.getMinX() && x <= mbr.getMaxX() && y >= mbr.getMinY() && y <= mbr.getMaxY())   return 0;
		
		// 4 * 7 * 1
		// 5 * O * 2
		// 6 * 8 * 3
		if(x > mbr.getMaxX()){
			if(y > mbr.getMaxY()){//case 1
				return Math.sqrt((x - mbr.getMaxX()) * (x - mbr.getMaxX()) + (y - mbr.getMaxY()) * (y - mbr.getMaxY()));
			}else if(y < mbr.getMinY()){//case 3
				return Math.sqrt((x - mbr.getMaxX()) * (x - mbr.getMaxX()) + (y - mbr.getMinY()) * (y - mbr.getMinY()));
			}else{//case 2
				return Math.abs(x - mbr.getMaxX());
			}
		}else if(x < mbr.getMinX()){
			if(y > mbr.getMaxY()){//case 4
				return Math.sqrt((x - mbr.getMinX()) * (x - mbr.getMinX()) + (y - mbr.getMaxY()) * (y - mbr.getMaxY()));
			}else if(y < mbr.getMinY()){//case 6
				return Math.sqrt((x - mbr.getMinX()) * (x - mbr.getMinX()) + (y - mbr.getMinY()) * (y - mbr.getMinY()));
			}else{//case 5
				return Math.abs(x - mbr.getMinX());
			}
		}else{
			if(y > mbr.getMaxY()){//case 7
				return Math.abs(y - mbr.getMaxY());
			}else{//case 8
				return Math.abs(y - mbr.getMinY());
			}
		}
		
	}
	
	//the maximum distance between a point and an MBR
	public static double obtainPMaxDist(double x, double y, MBR mbr){
		double max = 0, dist = 0;
		dist = Math.sqrt((x - mbr.getMinX()) * (x - mbr.getMinX()) + (y - mbr.getMinY()) * (y - mbr.getMinY()));
		if(dist > max)   max = dist;
		dist = Math.sqrt((x - mbr.getMinX()) * (x - mbr.getMinX()) + (y - mbr.getMaxY()) * (y - mbr.getMaxY()));
		if(dist > max)   max = dist;
		dist = Math.sqrt((x - mbr.getMaxX()) * (x - mbr.getMaxX()) + (y - mbr.getMinY()) * (y - mbr.getMinY()));
		if(dist > max)   max = dist;
		dist = Math.sqrt((x - mbr.getMaxX()) * (x - mbr.getMaxX()) + (y - mbr.getMaxY()) * (y - mbr.getMaxY()));
		if(dist > max)   max = dist;
		return max;
	}
}
