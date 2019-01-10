package com.spacekey.algorithm.spm.algorithm;

import java.util.*;

import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.PriQueue;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2017-01-18
 * determine the join order
 */
public class Order {
	
	public List<CPair> determineLeafJoinOrder(Pattern pattern, Map<String, Integer> costMap){
		int m = pattern.getM();
		int graph[][] = pattern.getGraph();
		HashSet<String> label[] = pattern.getLabel();
		boolean mark[][] = pattern.getMark();
		
		//step 1: select the starting edge
		int startI = -1, startJ = -1, minCost = Integer.MAX_VALUE;
		for(Map.Entry<String, Integer> entry:costMap.entrySet()){
			if(entry.getValue() < minCost){
				minCost = entry.getValue();
				String s[] = entry.getKey().split(":");
				startI = Integer.parseInt(s[0]);
				startJ = Integer.parseInt(s[1]);
			}
		}
//		System.out.println("Order start:[" + label[startI] + ", " + label[startJ] + "]");
		
		//step 2: initialize the list
		List<CPair> orderList = new ArrayList<CPair>();//UPDATE: order of visited edges
		boolean visit[] = new boolean[m];//mark visited and selected vertices
		orderList.add(new CPair(startI, startJ, true));
		if(mark[startI][startJ] && mark[startJ][startI])   orderList.add(new CPair(startJ, startI, false));
		visit[startI] = visit[startJ] = true;
		
		//step 3: explore the order incrementally
		PriQueue queue = new PriQueue();
		for(int k:graph[startI]){
			if(!mark[startI][k] && !mark[k][startI]){
				if(startI < k)   queue.getQueue().add(new CPair(startI, k, costMap.get(startI + ":" + k)));
				else             queue.getQueue().add(new CPair(k, startI, costMap.get(k + ":" + startI)));
			}else{
				if(mark[startI][k])   queue.getQueue().add(new CPair(startI, k, costMap.get(startI + ":" + k)));
				if(mark[k][startI])   queue.getQueue().add(new CPair(k, startI, costMap.get(k + ":" + startI)));
			}
		}
		for(int k:graph[startJ]){
			if(!mark[startJ][k] && !mark[k][startJ]){
				if(startJ < k)   queue.getQueue().add(new CPair(startJ, k, costMap.get(startJ + ":" + k)));
				else             queue.getQueue().add(new CPair(k, startJ, costMap.get(k + ":" + startJ)));
			}else{
				if(mark[startJ][k])   queue.getQueue().add(new CPair(startJ, k, costMap.get(startJ + ":" + k)));
				if(mark[k][startJ])   queue.getQueue().add(new CPair(k, startJ, costMap.get(k + ":" + startJ)));
			}
		}
		while(queue.getQueue().size() > 0){
			CPair cpair = queue.getQueue().poll();
			int id1 = cpair.id1, id2 = cpair.id2;
			if(visit[id1] && visit[id2])   continue;
			
			int newId = visit[id2] ? id1 : id2;
//			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
//			System.out.println("[" + label[id1] + ", " + label[id2] + "] newId=" + label[newId]);
			orderList.add(new CPair(id1, id2, true));
			if(mark[id1][id2] && mark[id2][id1])   orderList.add(new CPair(id2, id1, false));
			
			//handle inner-edges
			for(int k:graph[newId]){
				if(visit[k] && k != id1 && k != id2){
//					System.out.println(label[newId] + "'s in-neighbor [" + label[k] + "]");
					if(!mark[k][newId] && !mark[newId][k]){
						if(k < newId)    orderList.add(new CPair(k, newId, false));
						else             orderList.add(new CPair(newId, k, false));
					}else{
						if(mark[k][newId])   orderList.add(new CPair(k, newId, false));
						if(mark[newId][k])   orderList.add(new CPair(newId, k, false));
					}
				}
			}
			
			//handle out-edges
			for(int k:graph[newId]){
				if(!visit[k]){
//					System.out.println(label[newId] + "'s out-neighbor [" + label[k] + "]");
					if(!mark[k][newId] && !mark[newId][k]){
						if(k < newId)    queue.getQueue().add(new CPair(k, newId, costMap.get(k + ":" + newId)));
						else             queue.getQueue().add(new CPair(newId, k, costMap.get(newId + ":" + k)));
					}else{
						if(mark[k][newId])   queue.getQueue().add(new CPair(k, newId, costMap.get(k + ":" + newId)));
						if(mark[newId][k])   queue.getQueue().add(new CPair(newId, k, costMap.get(newId + ":" + k)));
					}
				}
			}
			
			//mark newId as a visited vertex
			visit[newId] = true;
//			System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
		}
		
		//test codes
//		System.out.println("Order");
//		for(CPair cpair:orderList){
//			String first = label[cpair.id1];
//			String second = label[cpair.id2];
//			System.out.println(first + "-" + second + "-" + cpair.linkOut);
//		}
		
//		System.out.print("Order: ");
//		Set<String> labelSet = new HashSet<String>();
//		for(CPair cpair:orderList){
//			HashSet<String> first = label[cpair.id1];
//			HashSet<String> second = label[cpair.id2];
//			if(!labelSet.containsAll(first)){
//				System.out.print(first + " ");
//				labelSet.addAll(first);
//			}
//			if(!labelSet.containsAll(second)){
//				System.out.print(second + " ");
//				labelSet.addAll(second);
//			}
//		}
//		System.out.println();
		
		return orderList;
	}
}