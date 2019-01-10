package com.spacekey.algorithm.coskq;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import com.spacekey.algorithm.coskq.index.BuildIRTree;
import com.spacekey.algorithm.coskq.index.Node;
import com.spacekey.algorithm.coskq.pattern.Pattern;
import com.spacekey.algorithm.coskq.pattern.Point;
import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;
import com.spacekey.algorithm.global.Pair;

public class Test {

	public static void main(String[] args) {

		// read data
		String indexFile = Config.indexUK;
		DataReader dataReader = new DataReader(Config.locUK, Config.docUK);
		
		double loc[][] = dataReader.readLoc();
		String kws[][] = dataReader.readKws();
		
		for (int i=0 ; i<20 ; i++) {
			System.out.printf("%d %f %f ", i, loc[i][0], loc[i][1]);
			for (int j=0 ; j<kws[i].length ; j++)
				System.out.printf("%s ", kws[i][j]);
			System.out.println();
		}
		
		// construct data
		HashSet<Point> objs = new HashSet<Point>();
		HashSet<String> keywordSet = new HashSet<String>();
		HashMap<String, Integer> keywordMap = new HashMap<String, Integer>();
		
		for (int i=0 ; i<loc.length ; i++) {
			Point obj = new Point(i, loc[i][0], loc[i][1]);
			for (int j=0 ; j<kws[i].length ; j++) {
				obj.addKeyword(kws[i][j]);
				keywordSet.add(kws[i][j]);
				if (keywordMap.containsKey(kws[i][j])) {
					keywordMap.put(kws[i][j], keywordMap.get(kws[i][j]) + 1);
				} else {
					keywordMap.put(kws[i][j], 0);
				}
			}
			objs.add(obj);
		}
		
		
		TreeMap<Integer, String> freqMap = new TreeMap<Integer, String>();
		Set<String> keySet = keywordMap.keySet();
		for (String keyword : keySet) {
			freqMap.put(keywordMap.get(keyword), keyword);
		}
		Set<Integer> freqKeySet = freqMap.keySet();
		for (int freq : freqKeySet) {
			System.out.printf("%d %s\n", freq, freqMap.get(freq));
		}
		
		
		System.out.println();
		System.out.printf("No. of objects: %d\n", objs.size());
		System.out.printf("No. of distinct keywords: %d\n", keywordSet.size());
		
		// build IRTree
		BuildIRTree builder = new BuildIRTree(loc, kws, indexFile);
		Node root = builder.build();
		
		// start the tests
		Algorithm alg = new Algorithm();
		int querySize = 10;
		int thresholdNum = 1000;
		
		// initialize a query
		System.out.println();
		Pattern query = new Pattern();
		query.setLoc(new Point(-1, 2, 51));
		System.out.printf("query:");
		for (String keyword : keywordSet) {
			if (query.getKeyword().size() >= querySize) break;
			if (keywordMap.get(keyword) < thresholdNum) continue;
			query.addKeyword(keyword);
			System.out.printf(" %d*%s", keywordMap.get(keyword), keyword);
		}
		System.out.println();
		
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		System.out.println();
		Pair<Double, HashSet<Point>> ans;
		ans = alg.Type1Greedy(query, root);
		System.out.printf("Type1Greedy(Appro) answer: cost = %f - %d objects\n", 
				Point.type1Cost(ans.getSecond(), query.getLoc()), 
				ans.getSecond().size());
		for (Point obj : ans.getSecond()) {
			System.out.printf("%f %f ", obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (query.getKeyword().contains(keyword))
					System.out.printf("*%s ", keyword);
			}
			System.out.println();
		}
		endTime = System.currentTimeMillis();
		System.out.printf("time = %d ms\n", endTime - startTime);	
		
		startTime = System.currentTimeMillis();
		System.out.println();
		ans = alg.Type1ExactWIndex(query, root);
		System.out.printf("Type1ExactWIndex answer: cost = %f - %d objects\n", 
				Point.type1Cost(ans.getSecond(), query.getLoc()), 
				ans.getSecond().size());
		for (Point obj : ans.getSecond()) {
			System.out.printf("%f %f ", obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (query.getKeyword().contains(keyword))
					System.out.printf("*%s ", keyword);
			}
			System.out.println();
		}
		endTime = System.currentTimeMillis();
		System.out.printf("time = %d ms\n", endTime - startTime);
	}
}
