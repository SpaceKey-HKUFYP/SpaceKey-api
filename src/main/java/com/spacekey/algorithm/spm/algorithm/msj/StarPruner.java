package com.spacekey.algorithm.spm.algorithm.msj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2017-1-18
 * perform star-pruning
 */
public class StarPruner {

	public void prune(Pattern pattern, List<CPair> orderList, Map<String, Map<Point, List<Point>>> linkMap){
		int m = pattern.getM();
		boolean mark[][] = pattern.getMark();
		
		//step 1: count the number of neighbors for each vertex in the pattern
		int nghNum[] = new int[m];
		for(CPair cpair:orderList){
			boolean isConsidered = false;//---------------fux bug 2017-2-4-----------
//			int i = cpair.id1, j = cpair.id2;
//			if(!mark[i][j] && !mark[j][i] && i < j)   isConsidered = true;
//			if(mark[i][j])   isConsidered = true;
			
			if(cpair.linkOut)   isConsidered = true;
			if(!cpair.linkOut && mark[cpair.id1][cpair.id2])   isConsidered = true;
			
			if(isConsidered){
				nghNum[cpair.id1] += 1;
				nghNum[cpair.id2] += 1;
			}
		}
		
		//step 2: count the number of neighbors for each object
		List<Map<Integer, Integer>> countList = new ArrayList<Map<Integer, Integer>>();
		for(int i = 0;i < m;i ++)   countList.add(new HashMap<Integer, Integer>());
		for(CPair cpair:orderList){
			int id1 = cpair.id1, id2 = cpair.id2;
			Map<Integer, Integer> countMap1 = countList.get(id1), countMap2 = countList.get(id2);
			Map<Point, List<Point>> nextCandMap = linkMap.get(id1 + ":" + id2);
			
			if(nextCandMap != null){
				Set<Integer> objSet1 = new HashSet<Integer>();
				for(Point point:nextCandMap.keySet())   objSet1.add(point.id);
				for(int objId:objSet1){
					if(countMap1.containsKey(objId))   countMap1.put(objId, countMap1.get(objId) + 1);
					else                               countMap1.put(objId, 1);
				}
				Set<Integer> objSet2 = new HashSet<Integer>();
				for(Map.Entry<Point, List<Point>> ent:nextCandMap.entrySet()){
					for(Point point:ent.getValue())   objSet2.add(point.id);
				}
				for(int candObjId:objSet2){
					if(countMap2.containsKey(candObjId))   countMap2.put(candObjId, countMap2.get(candObjId) + 1);
					else                                   countMap2.put(candObjId, 1);
				}
			}
		}
		
		//step 3: perform neighbor pruning
		for(CPair cpair:orderList){
			Map<Point, List<Point>> nextCandMap = linkMap.get(cpair.id1 + ":" + cpair.id2);
			if(nextCandMap != null){
				int id1 = cpair.id1, id2 = cpair.id2;
				Map<Integer, Integer> countMap1 = countList.get(id1), countMap2 = countList.get(id2);
				
				Iterator<Map.Entry<Point, List<Point>>> nextCandMapIt = nextCandMap.entrySet().iterator();
				while(nextCandMapIt.hasNext()){
					Map.Entry<Point, List<Point>> entry = nextCandMapIt.next(); 
					int objId = entry.getKey().id;
					if(countMap1.get(objId) < nghNum[id1])   nextCandMapIt.remove();//prune
					else{
						List<Point> cdList = entry.getValue();
						List<Point> list = new ArrayList<Point>();
						for(Point candPoint:cdList)   if(countMap2.get(candPoint.id) >= nghNum[id2])   list.add(candPoint);
						if(list.size() > 0)   entry.setValue(list);
						else                  nextCandMapIt.remove();//prune
					}
				}
			}
		}
	}
}
