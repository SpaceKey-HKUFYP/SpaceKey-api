package com.spacekey.algorithm.spm.algorithm.msj;

import java.util.*;

import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.irtree.Node;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.Pattern;
import com.spacekey.algorithm.spm.util.Euclidean;

/**
 * @author yxfang
 * @date 2016-11-21
 * perform internal join
 */
public class NonLeafJoin {
	private int m = -1;
	private HashSet<String> label[] = null;
	private boolean mark[][] = null;
	private int graph[][] = null;
	private double lb[][] = null, ub[][] = null;
	
	public NonLeafJoin(Pattern pattern){
		this.m = pattern.getM();
		this.mark = pattern.getMark();
		this.label = pattern.getLabel();
		this.graph = pattern.getGraph();
		this.lb = pattern.getLb();   this.ub = pattern.getUb();
	}

	public List<Map<ANode, List<ANode>>> nonLeafJoin(List<CPair> orderList, List<Map<ANode, List<ANode>>> candList, boolean nghPrune[]){
		long fang1 = System.nanoTime();
		
		//step 1: perform join in this specific level
		List<Set<ANode>> pruneList = new ArrayList<Set<ANode>>();//keep the candidate node
		for(int i = 0;i < m;i ++)   pruneList.add(null);//hold the space
		List<Map<ANode, List<ANode>>> nextCandList = new ArrayList<Map<ANode, List<ANode>>>();//the join results
		for(int i = 0;i < orderList.size();i ++){
			CPair cpair = orderList.get(i);
			int id1 = cpair.id1, id2 = cpair.id2;
			
			Map<ANode, List<ANode>> candMap = candList.get(i);
			Map<ANode, List<ANode>> nextCandMap = new HashMap<ANode, List<ANode>>();//internal nodes
			for(ANode node:candMap.keySet()){//enumerate each particular node
				// get all possible anodes with keyword set 1
				int cnt1 = 0;
				List<ANode> temp = new ArrayList<ANode>();
				List<ANode> anodes1 = new ArrayList<ANode>();
				for (String keyword : label[id1]) {
					if (cnt1++ == 0) {
						anodes1 = ((Node) node).getInvertMap().get(keyword);
					} else {
						temp = ((Node) node).getInvertMap().get(keyword);
						Iterator<ANode> iter = anodes1.iterator();
						while (iter.hasNext()) {
						    ANode anode = iter.next();
						    if (!temp.contains(anode)) iter.remove();
						}
					}
				}
				// continue
				for(ANode tmpNode1 : anodes1){
					List<ANode> tmpCandList = new ArrayList<ANode>();
					boolean isPruned = false;
					for(ANode candANode:candMap.get(node)){//candidate nodes
						// get all possible anodes with keyword set 1
						int cnt2 = 0;
						List<ANode> anodes2 = new ArrayList<ANode>();
						for (String keyword : label[id2]) {
							if (cnt2++ == 0) {
								anodes2 = ((Node) candANode).getInvertMap().get(keyword);
							} else {
								temp = ((Node) candANode).getInvertMap().get(keyword);
								Iterator<ANode> iter = anodes2.iterator();
								while (iter.hasNext()) {
								    ANode anode = iter.next();
								    if (!temp.contains(anode)) iter.remove();
								}
							}
						}
						// continue
						for(ANode tmpNode2 : anodes2){
							double maxDist = Euclidean.obtainMaxDist(tmpNode1.getMbr(), tmpNode2.getMbr());
							if(maxDist < lb[id1][id2]){
								if(mark[id1][id2]){
									isPruned = true;//prune tmpNode1
									break;
								}
							} else {//maxDist >= lb
								double minDist = Euclidean.obtainMinDist(tmpNode1.getMbr(), tmpNode2.getMbr());
								if(minDist <= ub[id1][id2])  
									tmpCandList.add(tmpNode2);
							}
						}
						if(isPruned)   break;
					}
					if(!isPruned && tmpCandList.size() > 0)   nextCandMap.put(tmpNode1, tmpCandList);
				}
			}
			
			nextCandList.add(nextCandMap);//record the join results
		}
//		long fang2 = System.nanoTime();System.out.println("fang2: " + (fang2 - fang1));
		
		
		//step 2: count the number of neighbors
		List<Map<ANode, Integer>> countList = new ArrayList<Map<ANode, Integer>>();
		for(int i = 0;i < m;i ++)   countList.add(new HashMap<ANode, Integer>());
		for(int i = 0;i < orderList.size();i ++){
			CPair cpair = orderList.get(i);
			int id1 = cpair.id1, id2 = cpair.id2;
			Map<ANode, Integer> countMap1 = countList.get(id1), countMap2 = countList.get(id2);
			Map<ANode, List<ANode>> nextCandMap = nextCandList.get(i);
			
			Set<ANode> nodeSet1 = nextCandMap.keySet();
			for(ANode anode:nodeSet1){
				if(countMap1.containsKey(anode))   countMap1.put(anode, countMap1.get(anode) + 1);
				else                               countMap1.put(anode, 1);
			}
			Set<ANode> nodeSet2 = new HashSet<ANode>();
			for(ANode node1:nodeSet1)   nodeSet2.addAll(nextCandMap.get(node1));
			for(ANode candNode:nodeSet2){
				if(countMap2.containsKey(candNode))   countMap2.put(candNode, countMap2.get(candNode) + 1);
				else                                  countMap2.put(candNode, 1);
			}
		}
//		long fang3 = System.nanoTime();System.out.println("fang3: " + (fang3 - fang2));
		
		//step 3: perform neighbor pruning
		for(int i = 0;i < orderList.size();i ++){
			CPair cpair = orderList.get(i);
			int id1 = cpair.id1, id2 = cpair.id2;
			Map<ANode, Integer> countMap1 = countList.get(id1), countMap2 = countList.get(id2);
			Map<ANode, List<ANode>> nextCandMap = nextCandList.get(i);
			
			Iterator<Map.Entry<ANode, List<ANode>>> nextCandMapIt = nextCandMap.entrySet().iterator();
			while(nextCandMapIt.hasNext()){
				Map.Entry<ANode, List<ANode>> entry = nextCandMapIt.next(); 
				ANode node = entry.getKey();
				if(countMap1.get(node) < graph[id1].length)   nextCandMapIt.remove();//prune
				else{
					if(!nghPrune[id2]){//bug-2018-12-18, try to avoid over-pruning: after pruning, each candidate has at least one neighbor
						List<ANode> cdList = entry.getValue();
						List<ANode> list = new ArrayList<ANode>();
						for(ANode candNode:cdList)   if(countMap2.get(candNode) >= graph[id2].length)   list.add(candNode);//prune
						if(list.size() > 0)   entry.setValue(list);
						else                  nextCandMapIt.remove();//prune
					}
				}
			}
		}
//		long fang4 = System.nanoTime();System.out.println("fang4: " + (fang4 - fang3));
		
		return nextCandList;
	}
}