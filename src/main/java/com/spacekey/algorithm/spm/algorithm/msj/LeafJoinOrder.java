package com.spacekey.algorithm.spm.algorithm.msj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.algorithm.Order;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2016-11-21
 * determine the join order for leaf level
 */
public class LeafJoinOrder {

	public List<CPair> determineLeafJoinOrder(Pattern pattern, List<CPair> preOrderList, List<Map<ANode, List<ANode>>> candList){
		Map<String, Integer> costMap = new HashMap<String, Integer>();//the cost of each edge
		for(int i = 0;i < preOrderList.size();i ++){
			CPair curCPair = preOrderList.get(i);
			int id1 = curCPair.id1, id2 = curCPair.id2;
			Map<ANode, List<ANode>> candMap = candList.get(i);
			Set<ANode> keySet = candMap.keySet();
			
			int cost = 0;
			for(ANode anode:keySet)   cost += candMap.get(anode).size();
			costMap.put(id1 + ":" + id2, cost);
		}
		
		Order order = new Order();
		return order.determineLeafJoinOrder(pattern, costMap);
	}
}