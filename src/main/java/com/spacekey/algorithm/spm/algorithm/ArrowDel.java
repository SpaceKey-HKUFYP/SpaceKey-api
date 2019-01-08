package com.spacekey.algorithm.spm.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2017-1-18
 * if an edge has two arrows, then we remove one of them
 * Notice if i <-> j and i < j, then remove the arrow j -> i and update the linkMap
 */
public class ArrowDel {
	
	//this function will be called by GPM and MPJ
	public void remove(Pattern pattern, Map<String, Map<Point, List<Point>>> linkMap){
		int m = pattern.getM();
		boolean mark[][] = pattern.getMark();
		
		for(int i = 0;i < m;i ++){
			for(int j = i + 1;j < m;j ++){
				if(mark[i][j] && mark[j][i]){
					Map<Integer, Set<Integer>> tmpMap = new HashMap<Integer, Set<Integer>>();
					for(Map.Entry<Point, List<Point>> entry:linkMap.get(j + ":" + i).entrySet()){//results for edge "j->i"
						Set<Integer> set = new HashSet<Integer>();
						for(Point p:entry.getValue())   set.add(p.id);
						tmpMap.put(entry.getKey().id, set);
					}
					
					Map<Point, List<Point>> leftMap = linkMap.get(i + ":" + j);//results for edge "i->j"
					Map<Point, List<Point>> rsMap = new HashMap<Point, List<Point>>();
					for(Map.Entry<Point, List<Point>> entry:leftMap.entrySet()){
						Point point = entry.getKey();
						List<Point> rsPointList = new ArrayList<Point>();
						for(Point p:entry.getValue()){
							Set<Integer> set = tmpMap.get(p.id);
							if(set != null && set.contains(point.id))   rsPointList.add(p);
						}
						if(rsPointList.size() > 0)   rsMap.put(point, rsPointList);
					}
					
					mark[j][i] = false;//update the pattern
					linkMap.remove(j + ":" + i);//remove the results for j->i
					linkMap.put(i + ":" + j, rsMap);//update the results for i->j
				}
			}
		}
	}
	
	//this function will be called by MSJ
	public List<CPair> remove(Pattern pattern, Map<String, Map<Point, List<Point>>> linkMap, List<CPair> orderList){
		boolean mark[][] = pattern.getMark();
		CPair prePair = orderList.get(0);
		List<CPair> updatedOrderList = new ArrayList<CPair>();
		updatedOrderList.add(prePair);
		
		for(int k = 1;k < orderList.size();k ++){
			CPair curPair = orderList.get(k);
			if(prePair.id1 == curPair.id2 && prePair.id2 == curPair.id1){//delete 
				int i = prePair.id1, j = prePair.id2;//delete j -> i
				Map<Integer, Set<Integer>> tmpMap = new HashMap<Integer, Set<Integer>>();
				for(Map.Entry<Point, List<Point>> entry:linkMap.get(j + ":" + i).entrySet()){//results for edge "j->i"
					Set<Integer> set = new HashSet<Integer>();
					for(Point p:entry.getValue())   set.add(p.id);
					tmpMap.put(entry.getKey().id, set);
				}
				
				Map<Point, List<Point>> leftMap = linkMap.get(i + ":" + j);//results for edge "i->j"
				Map<Point, List<Point>> rsMap = new HashMap<Point, List<Point>>();
				for(Map.Entry<Point, List<Point>> entry:leftMap.entrySet()){
					Point point = entry.getKey();
					List<Point> rsPointList = new ArrayList<Point>();
					for(Point p:entry.getValue()){
						Set<Integer> set = tmpMap.get(p.id);
						if(set != null && set.contains(point.id))   rsPointList.add(p);
					}
					if(rsPointList.size() > 0)   rsMap.put(point, rsPointList);
				}
				
				mark[j][i] = false;//update the pattern
				linkMap.remove(j + ":" + i);//remove the results for j->i
				linkMap.put(i + ":" + j, rsMap);//update the results for i->j
			}else{
				updatedOrderList.add(curPair);//delete j -> i from the orderList
				prePair = curPair;
			}
		}
			
		return updatedOrderList;
	}
}
