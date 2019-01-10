package com.spacekey.algorithm.spm.algorithm.msj.anchor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.pattern.BoundedPattern;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2016-11-21
 * find anchor vertices that have chances used for pruning
 * Notice that, in the orderList, the inner-edges are appearing before the newId
 * Bug: anchors should be associated with edges
 * Anchors happen only for link-out edges
 */
public class AnchorFinder {
	private int m = -1;
	private Pattern pattern = null;
	private double lb[][] = null, ub[][] = null, lower[][] = null, upper[][] = null;

	public AnchorFinder(Pattern pattern, BoundedPattern bpattern){
		this.pattern = pattern;
		this.m = pattern.getM();
		this.lb = pattern.getLb();   this.ub = pattern.getUb();
		this.lower = bpattern.getLower();   this.upper = bpattern.getUpper();
	}
	
	public Map<Integer, List<Integer>> find(List<CPair> orderList){
		//step 1: find candidate vertices, which are in 2-core
		Find2Core find2Core = new Find2Core();
		Set<Integer> coreSet = find2Core.find(pattern);
		if(coreSet.size() == 0)   return null;//no anchor vertex can be found
		
		//step 2: initialization
		double lowerInc[][] = new double[m][m];
		double upperInc[][] = new double[m][m];
		for(int i = 0;i < m;i ++){
			for(int j = i + 1;j < m;j ++){
				upperInc[i][j] = upperInc[j][i] = Integer.MAX_VALUE;
			}
		}
		
		//add on Feb 7, 2017
		List<Integer> coreList = new ArrayList<Integer>();
		for(int index = 0;index < orderList.size() - 1;index ++){
			CPair cpair = orderList.get(index);
			int id1 = cpair.id1, id2 = cpair.id2;
			if(coreSet.contains(id1) && !coreList.contains(id1))   coreList.add(id1);
			if(coreSet.contains(id2) && !coreList.contains(id2))   coreList.add(id2);
		}
		
		//step 3: find the anchors
		boolean visit[] = new boolean[m];//record the visited vertices
		Map<Integer, List<Integer>> anchorMap = new HashMap<Integer, List<Integer>>();
		for(int index = 0;index < orderList.size() - 1;index ++){
			CPair cpair = orderList.get(index);
			int id1 = cpair.id1, id2 = cpair.id2;
//			System.out.println("AnchorFinder-order [" + id1 + "->" + id2 + "]");
			
			//update the inc-graph incrementally
			upperInc[id1][id2] = upperInc[id2][id1] = ub[id1][id2];
			lowerInc[id1][id2] = lowerInc[id2][id1] = Math.max(lowerInc[id1][id2], lb[id1][id2]);//consider the exclusion-ship
			
			if(!cpair.linkOut)   continue;//for inner-edges, no chance to have anchors
			int newId = visit[id2] ? id1 : id2;
			if(index >= 1 && coreSet.contains(newId)){
				//step a: compute the upper-bound
				for(int k = 0;k < m;k ++){
					for(int i = 0;i < m;i ++){
						for(int j = 0;j < m;j ++){
							double dist = upperInc[i][k] + upperInc[k][j];
							if(dist < upperInc[i][j])   upperInc[i][j] = dist;
						}
					}
				}
				
				//step b: compute the lower-bound
				for(int k = 0;k < m;k ++){
					for(int i = 0;i < m;i ++){
						for(int j = 0;j < m;j ++){
							if(i != j){
								double dist = 0;
								if(upperInc[i][k] < lowerInc[k][j]){//updated
									dist = lowerInc[k][j] - upperInc[i][k];
								}else if(lowerInc[i][k] > upperInc[k][j]){
									dist = lowerInc[i][k] - upperInc[k][j];
								}else{
									dist = 0;
								}
								if(dist > lowerInc[i][j])   lowerInc[i][j] = dist;
							}
						}
					}
				}
				
				//step c: find the anchor vertices by checking back
				List<Integer> list = new ArrayList<Integer>();//record the anchor edges
//				for(int backId:coreSet){
				for(int backId:coreList){
					if(visit[backId]){//only consider visited vertices
						if(lb[newId][backId] < 0 && lb[backId][newId] < 0){//only consider "dashed" edges
							if(upper[newId][backId] * Config.alpha < upperInc[newId][backId] 
									|| lower[newId][backId] > lowerInc[newId][backId] * Config.alpha){
								list.add(backId);
							}
						}
					}
				}
				
				if(list.size() > 0){
					anchorMap.put(newId, list);
					
					//test codes
//					System.out.print("AnchorFinder [edge:" + id1 + "->" + id2 + "]    [newId:" + newId + "]    ");
//					for(int id:list)   System.out.print(id + "   ");
//					System.out.println();
				}
			}
			
			visit[id1] = visit[id2] = true;//mark these two vertices as visited vertices
		}
		return anchorMap;
	}
}