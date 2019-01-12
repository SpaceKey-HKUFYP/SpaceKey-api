package com.spacekey.server;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import com.spacekey.algorithm.Link;
import com.spacekey.algorithm.global.Point;

public interface MethodsInterface {
	// construct data for all problems
	public void constructData(String locPath, String docPath);
	
	// mck cost = maxDist(obj1, obj2);
	public HashSet<Point> mckGKG(HashSet<String> keywords);
	public HashSet<Point> mckSKECaplus(HashSet<String> keywords);
	public HashSet<Point> mckExact(HashSet<String> keywords);
	
	// minsk cost = (|S|-1) * maxDist(obj1, o	bj2); (for a set S)
	public HashSet<Point> minskScaleLune(HashSet<String> keywords);
	
	// Type 1 cost = sum(dist(obj, query));
	public HashSet<Point> coskqType1Appro(double query_x, double query_y, HashSet<String> keywords);
	public HashSet<Point> coskqType1Exact(double query_x, double query_y, HashSet<String> keywords);
	
	// Type 2 cost = maxDist(obj, query)) + maxDist(obj1, obj2);
	public HashSet<Point> coskqType2Appro(double query_x, double query_y, HashSet<String> keywords);
	public HashSet<Point> coskqType2Exact(double query_x, double query_y, HashSet<String> keywords);
	
	// Type 3 cost = max(maxDist(obj, query), maxDist(obj1, obj2));
	public HashSet<Point> coskqType3Appro(double query_x, double query_y, HashSet<String> keywords);
	public HashSet<Point> coskqType3Exact(double query_x, double query_y, HashSet<String> keywords);
	
	// spatial pattern matching
	public HashSet<HashSet<Point>> spmMSJ(List<Link> linkList);
	public ArrayList<HashSet<Point>> spmTopK(List<Link> linkList, int k, double query_x, double query_y);
}