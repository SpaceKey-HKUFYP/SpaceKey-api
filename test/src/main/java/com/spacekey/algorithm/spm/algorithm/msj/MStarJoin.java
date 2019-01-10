package com.spacekey.algorithm.spm.algorithm.msj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.spacekey.algorithm.spm.algorithm.ArrowDel;
import com.spacekey.algorithm.spm.algorithm.Join;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.algorithm.msj.anchor.AnchorFinder;
import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.irtree.BuildIRTree;
import com.spacekey.algorithm.spm.irtree.Node;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.BoundedPattern;
import com.spacekey.algorithm.spm.pattern.GlobalRef;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2016-10-20
 * Perform mstar-join
 * Caution: the distance differences between pattern and bpattern
 */
public class MStarJoin {
	private Node root = null;//IR-tree of the dataset
	public HashSet<String> keywordMap[];
	
	public MStarJoin(Node root){
		this.root = root;
	}	
	
	public List<int[]> query(Pattern pattern){
		for (HashSet<String> keywords:pattern.getLabel())
			for (String keyword : keywords)
				if(!root.getInvertMap().containsKey(keyword)) return null;//stop query
		
		//step 1: compute the bounded pattern
		BoundedPattern bpattern = new BoundedPattern(pattern);
		bpattern.computeBound();

		//step 2: global-refining
		GlobalRef glbRef = new GlobalRef(pattern, bpattern);
		pattern = glbRef.refine();
		if(pattern == null)   return null;//a wrong pattern
		int m = pattern.getM();//the number of vertices in the pattern
		int graph[][] = pattern.getGraph();
		boolean mark[][] = pattern.getMark();
		
		//step 3: initialization
		boolean nghPrune[] = new boolean[m];//bug---rediscovered on Dec 18, 2016
		List<CPair> initOrderList = new ArrayList<CPair>();//order of visited edges
		List<Map<ANode, List<ANode>>> candList = new ArrayList<Map<ANode, List<ANode>>>();//internal nodes
		for(int i = 0;i < m;i ++){
			for(int j:graph[i]){
				boolean isConsidered = false;
				if(!mark[i][j] && !mark[j][i] && i < j)   isConsidered = true;
				if(mark[i][j])   isConsidered = true;
				if(mark[i][j])   nghPrune[j] = true;//----------bug 20170204: rediscover bug mark nghId for later pruning
				
				if(isConsidered){
					initOrderList.add(new CPair(i, j, Integer.MIN_VALUE));
					ArrayList<ANode> list = new ArrayList<ANode>(){{add(root);}};
					Map<ANode, List<ANode>> candMap = new HashMap<ANode, List<ANode>>();
					candMap.put(root, list);
					candList.add(candMap);
				}
			}
		}
		
		NonLeafJoin nonLeafJoin = new NonLeafJoin(pattern);
		LeafJoin leafJoin = new LeafJoin(pattern);
		
		List<int[]> rsList = null;
		int treeHeight = BuildIRTree.height;
		for(int h = 1;h <= treeHeight;h ++){
			if(h < treeHeight){
				candList = nonLeafJoin.nonLeafJoin(initOrderList, candList, nghPrune);
				if(candList == null)   return null;
			}else{
				//determine the join order
				LeafJoinOrder leafJoinOrder = new LeafJoinOrder();
				List<CPair> orderList = leafJoinOrder.determineLeafJoinOrder(pattern, initOrderList, candList);
				
				//join each edge
				Map<String, Map<Point, List<Point>>> linkMap = leafJoin.leafJoin(orderList, candList, initOrderList);
				for(Map.Entry<String, Map<Point, List<Point>>> entry:linkMap.entrySet()){
					if(entry.getValue() == null || entry.getValue().size() == 0){
						return null;
					}
				}
				
				//delete <-> edges and updated mark[][]
				ArrowDel arrowDel = new ArrowDel();
				orderList = arrowDel.remove(pattern, linkMap, orderList);
				
				//star-pruning
				StarPruner starPruner = new StarPruner();
				starPruner.prune(pattern, orderList, linkMap);
				
				//find anchors
				Map<Integer, List<Integer>> achMap = null;
				if(pattern.getM() >= 4 && orderList.size() >= 3){
					AnchorFinder finder = new AnchorFinder(pattern, bpattern);
					achMap = finder.find(orderList);
				}
				
				//test codes
//				System.out.println("|orderList|:" + orderList.size());
//				String label[] = pattern.getLabel();
//				for(CPair cpair:orderList){
//					Map<Point, List<Point>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
//					System.out.print("MStarJoin [" + label[cpair.id1] + ", " + label[cpair.id2] + ", " + cpair.linkOut + "] ");
//					System.out.println("size=" + map.size());
//				}
//				System.out.println();
				
				//link the results
				Join join = new Join();
				rsList = join.join(pattern, bpattern, orderList, linkMap, achMap, this);
			}
		}
		
		return rsList;
	}
}