package com.spacekey.algorithm.spm.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.algorithm.msj.MStarJoin;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.BoundedPattern;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2017-1-18
 */
public class Join {
	
	public List<int[]> join(Pattern pattern, List<CPair> orderList, Map<String, Map<Point, List<Point>>> linkMap){
		boolean mark[][] = pattern.getMark();
		double lb[][] = pattern.getLb(), ub[][] = pattern.getUb();
		
		//test codes
		HashSet<String> label[] = pattern.getLabel();
		for(CPair cpair:orderList){
			Map<Point, List<Point>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
			System.out.println("[" + label[cpair.id1] + ", " + label[cpair.id2] + "]"+ " |map|:" + map.size());
			for(Map.Entry<Point, List<Point>> ent: map.entrySet()){
				int firstId = ent.getKey().id;
				for(Point p:ent.getValue()){
					int secondId = p.id;
					System.out.println("[" + firstId + ", " + secondId + "]");
				}
			}
			System.out.println();
		}
		
		//step 0: stop early
		for(CPair cpair:orderList){
			Map<Point, List<Point>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
			if(cpair.linkOut){
				if(map == null || map.size() == 0)   return null;
			}else{
				if(mark[cpair.id1][cpair.id2] || mark[cpair.id1][cpair.id2]){
					if(map == null || map.size() == 0)   return null;
				}
			}
		}
		
		//step 1: determine the column order
		int rankIndex = 0;
		Map<Integer, Integer> rankMap = new HashMap<Integer, Integer>();//vertexId -> i
		for(CPair cpair:orderList){
			if(!rankMap.containsKey(cpair.id1))   rankMap.put(cpair.id1, rankIndex ++);
			if(!rankMap.containsKey(cpair.id2))   rankMap.put(cpair.id2, rankIndex ++);
		}
		
		//step 2: link edges
		LinkBox linkBox = new LinkBox();
		Box rootBox = new Box(new Point(-1, -1, -1));//the root of the result tree
		for(int i = 0;i < orderList.size();i ++){
			CPair cpair = orderList.get(i);
			int id1 = cpair.id1, id2 = cpair.id2;
			int rank1 = rankMap.get(id1), rank2 = rankMap.get(id2);
			Map<Point, List<Point>> candMap = linkMap.get(id1 + ":" + id2);
			if(rank1 > rank2)   candMap = linkBox.reverse(candMap);//reverse the key-value pairs in the candMap
			
			int leftRank = rank1, rightRank = rank2;
			if(rank1 > rank2){
				leftRank = rank2;
				rightRank = rank1;
			}

			if(cpair.linkOut){//[true: outer-edge]
				if(i == 0)   linkBox.buildFirstLevel(rootBox, candMap);
				else{
					boolean hasNextLevel = linkBox.buildLeaf(rootBox, candMap, leftRank, rightRank);
					if(!hasNextLevel)   return null;
				}
			}else{//[false: inner-edge]
				double lbDist = lb[id1][id2], ubDist = ub[id1][id2];
				if(!mark[id1][id2])   linkBox.backInclude(rootBox, leftRank, rightRank, lbDist, ubDist);
				else{
					if(candMap == null || candMap.size() == 0)   return null;
					linkBox.backExclude(rootBox, candMap, leftRank, rightRank);
				}
			}
		}
		
		//step 3: collect the records
		BoxRecord boxRecord = new BoxRecord(pattern, rootBox);
		List<int[]> rsList = boxRecord.collectRecord();
		
		return rsList;
	}

	public List<int[]> join(Pattern pattern, BoundedPattern bpattern, List<CPair> orderList, Map<String, Map<Point, List<Point>>> linkMap, Map<Integer, List<Integer>> achMap, MStarJoin caller){
		boolean mark[][] = pattern.getMark();
		HashSet<String> label[] = pattern.getLabel();
		double lb[][] = pattern.getLb(), ub[][] = pattern.getUb();
		
		//step 0: stop early
		for(CPair cpair:orderList){
			Map<Point, List<Point>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
			if(cpair.linkOut){
				if(map == null || map.size() == 0)   return null;
			}else{
				if(mark[cpair.id1][cpair.id2] || mark[cpair.id1][cpair.id2]){
					if(map == null || map.size() == 0)   return null;
				}
			}
		}
		
		//step 1: determine the column order
		int rankIndex = 0;
		HashMap<Integer, Integer> rankMap = new HashMap<Integer, Integer>(); //vertexId -> i
		for (CPair cpair:orderList){
			if (!rankMap.containsKey(cpair.id1))
				rankMap.put(cpair.id1, rankIndex++);
			if (!rankMap.containsKey(cpair.id2))
				rankMap.put(cpair.id2, rankIndex++);
		}
		
		//step 2: link edges
		LinkBox linkBox = new LinkBox();
		Box rootBox = new Box(new Point(-1, -1, -1));//the root of the result tree
		BoxRecord boxRecord = new BoxRecord(pattern, rootBox);
		Set<Integer> visitSet = new HashSet<Integer>();
		for(int i = 0;i < orderList.size();i ++){
			CPair cpair = orderList.get(i);
			int id1 = cpair.id1, id2 = cpair.id2;
			int rank1 = rankMap.get(id1), rank2 = rankMap.get(id2);
			Map<Point, List<Point>> candMap = linkMap.get(id1 + ":" + id2);
			
//			String tmp = "[" + label[cpair.id1] + ", " + label[cpair.id2] + "," + cpair.linkOut + "]";
//			int tmptmp = 0;
//			for(Map.Entry<Point, List<Point>> ent:candMap.entrySet())   tmptmp += ent.getValue().size();
//			System.out.println("processing: " + tmp + " |candMap|:" + candMap.size() + " pair:" + tmptmp);
			if(rank1 > rank2)   candMap = linkBox.reverse(candMap);//reverse the key-value pairs in the candMap
			
			int leftRank = rank1, rightRank = rank2, newId = id2;
			if(rank1 > rank2){
				leftRank = rank2;
				rightRank = rank1;
				newId = id1;
			}
			
			if(cpair.linkOut){//[true: outer-edge]
				if(i == 0)   linkBox.buildFirstLevel(rootBox, candMap);
				else{
//					System.out.println("leftRank:" + leftRank + " rightRank:" + rightRank);
					boolean hasNextLevel = linkBox.buildLeaf(rootBox, candMap, leftRank, rightRank);
					if(!hasNextLevel)   return null;
					
					//anchor pruning
					if(achMap != null && achMap.containsKey(newId)){
						linkBox.achPrune(bpattern, rootBox, rankMap, newId, achMap.get(newId));
					}
				}
			}else{//[false: inner-edge]
				double lbDist = lb[id1][id2], ubDist = ub[id1][id2];
				if(!mark[id1][id2])   linkBox.backInclude(rootBox, leftRank, rightRank, lbDist, ubDist);
				else{
					if(candMap == null || candMap.size() == 0)   return null;
//					System.out.println("leftRank:" + leftRank + " rightRank:" + rightRank);
					linkBox.backExclude(rootBox, candMap, leftRank, rightRank);
				}
			}
			
//		for experiments
//			visitSet.add(id1);
//			visitSet.add(id2);
//			int count = boxRecord.countRecord(rootBox, 0, visitSet.size());
//			System.out.println("Join count:" + count + "\n");
//			if(count >= 100000)   return null;
		}
		
		//step 3: collect the records
		List<int[]> rsList = boxRecord.collectRecord();
//		if(rsList.size() >= 1000000)   return null;
		
		int maxRank = 0;
		for(Map.Entry<Integer, Integer> entry : rankMap.entrySet()){
			int rank = entry.getValue();
			if (rank > maxRank) maxRank = rank;
		}
		caller.keywordMap = new HashSet[maxRank + 1];
		for(Map.Entry<Integer, Integer> entry : rankMap.entrySet()){
			int rank = entry.getValue();
			HashSet<String> word = label[entry.getKey()];
			caller.keywordMap[rank] = word;
		}
		return rsList;
	}
}