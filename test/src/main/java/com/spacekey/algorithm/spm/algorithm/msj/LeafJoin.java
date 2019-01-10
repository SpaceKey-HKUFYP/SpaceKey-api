package com.spacekey.algorithm.spm.algorithm.msj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.algorithm.ArrowDel;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.irtree.Leaf;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.Pattern;
import com.spacekey.algorithm.spm.util.Euclidean;

/**
 * @author yxfang
 * @date 2016-11-21
 * perform leaf join
 */
public class LeafJoin {
	private int m = -1;
	private HashSet<String> label[] = null;
	private boolean mark[][] = null;
	private double lb[][] = null, ub[][] = null;
	
	public LeafJoin(Pattern pattern){
		this.m = pattern.getM();
		this.mark = pattern.getMark();
		this.label = pattern.getLabel();
		this.lb = pattern.getLb();   this.ub = pattern.getUb();
	}

	public Map<String, Map<Point, List<Point>>> leafJoin(List<CPair> orderList, List<Map<ANode, List<ANode>>> candList, List<CPair> iniOrderList){
		//step 1: map the cpairs into integers
		Map<String, Integer> iMap = new HashMap<String, Integer>();
		for(int i = 0;i < iniOrderList.size();i ++){
			CPair cpair = iniOrderList.get(i);
			iMap.put(cpair.id1 + ":" + cpair.id2, i);
		}
		
		//step 2: join each edge
		Map<String, Map<Point, List<Point>>> linkMap = new HashMap<String, Map<Point, List<Point>>>();
		for(CPair cpair:orderList){
			if(cpair.linkOut || mark[cpair.id1][cpair.id2]){
				String edge = cpair.id1 + ":" + cpair.id2;
				int index = iMap.get(edge);
				Map<ANode, List<ANode>> candMap = candList.get(index);//the orders are different
				Map<Point, List<Point>> map = join(candMap, cpair);
				linkMap.put(edge, map);
			}
		}
		
		return linkMap;
	}
	
	private Map<Point, List<Point>> join(Map<ANode, List<ANode>> candMap, CPair cpair){
		int id1 = cpair.id1, id2 = cpair.id2;
		Map<Point, List<Point>> nextCandMap = new HashMap<Point, List<Point>>();//leaf nodes
		for(ANode node:candMap.keySet()){//enumerate each particular node
			Leaf leaf = (Leaf) node;
			// get all possible points with keyword set 1
			int cnt1 = 0;
			List<Point> temp = new ArrayList<Point>();
			List<Point> points1 = new ArrayList<Point>();
			for (String keyword : label[id1]) {
				if (cnt1++ == 0) {
					points1 = leaf.getInvertMap().get(keyword);
				} else {
					temp = leaf.getInvertMap().get(keyword);
					Iterator<Point> iter = points1.iterator();
					while (iter.hasNext()) {
					    Point point = iter.next();
					    if (!temp.contains(point)) iter.remove();
					}
				}
			}
			// continue
			for(Point point1: points1){
				List<Point> tmpCandList = new ArrayList<Point>();
				boolean isPruned = false;
				for(ANode anode:candMap.get(node)){//candidate nodes
					Leaf candLeaf = (Leaf)anode;
					// get all possible points with keyword set 2
					int cnt2 = 0;
					List<Point> points2 = new ArrayList<Point>();
					for (String keyword : label[id2]) {
						if (cnt2++ == 0) {
							points2 = candLeaf.getInvertMap().get(keyword);
						} else {
							temp = candLeaf.getInvertMap().get(keyword);
							Iterator<Point> iter = points2.iterator();
							while (iter.hasNext()) {
							    Point point = iter.next();
							    if (!temp.contains(point)) iter.remove();
							}
						}
					}
					// continue
					for(Point point2 : points2){
						double dist = Euclidean.dist(point1, point2);
						if(dist < lb[id1][id2]){
							if(mark[id1][id2]){
								isPruned = true;//prune tmpNode1
								break;
							}
						}else{//maxDist >= lb
							if(dist >= lb[id1][id2] && dist <= ub[id1][id2])   tmpCandList.add(point2);
						}
					}
					if(isPruned)   break;
				}
				if(!isPruned && tmpCandList.size() > 0)   nextCandMap.put(point1, tmpCandList);
			}
		}
		
		return nextCandMap;
	}
}