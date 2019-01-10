package com.spacekey.algorithm.spm.util.backup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.spacekey.algorithm.spm.algorithm.ArrowDel;
import com.spacekey.algorithm.spm.algorithm.Join;
import com.spacekey.algorithm.spm.algorithm.mpj.queue.CPair;
import com.spacekey.algorithm.spm.algorithm.msj.LeafJoin;
import com.spacekey.algorithm.spm.algorithm.msj.LeafJoinOrder;
import com.spacekey.algorithm.spm.algorithm.msj.NonLeafJoin;
import com.spacekey.algorithm.spm.algorithm.msj.StarPruner;
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
public class MSJTime {
	private Node root = null;//IR-tree of the dataset
	
	public MSJTime(Node root){
		this.root = root;
	}
	
	public long[] query(Pattern pattern){
		long time[] = new long[2];
		long startTime = System.nanoTime();
		
		for (HashSet<String> keywords : pattern.getLabel())
			for (String keyword : keywords)
				if(!root.getInvertMap().containsKey(keyword))   return null;//stop query
		
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
		
		//step 4: perform join
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
				long time1 = System.nanoTime();
				time[0] = time1 - startTime;
				
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
				
				//link the results
				Join join = new Join();
				// rsList = join.join(pattern, bpattern, orderList, linkMap, achMap);
				long time2 = System.nanoTime();
				time[1] = time2 - startTime;
			}
		}
		
		return time;
	}
}