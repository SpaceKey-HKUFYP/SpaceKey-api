package com.spacekey.algorithm.coskq.index;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.coskq.pattern.Point;

/**
 * @author wangj
 * @date Aug 22, 2017
 * Define some operations for the IR-tree to do some simple test
 */
public class IRTreeOperator {
	/**
	 * test output the content of IR-Tree
	 */
	public static void traverse(double loc[][], String kws[][], ANode root, int level){
		if(root instanceof Node){
			for(int i = 0;i < 4 * level;i ++)   System.out.print("-");
			System.out.print(root.getMbr().toString());
			
			for(int i = 0;i < 4 * level;i ++)   System.out.print("-");
			Map<String, List<ANode>> map = ((Node)root).getInvertMap();
			for(String keyword:map.keySet()){
				for(int i = 0;i < 4 * (level + 1);i ++)   System.out.print("-");
				System.out.print(keyword + ":");
				List<ANode> list = map.get(keyword);
				for (int i=0 ; i<list.size() ; i++)
					System.out.print("*");
				System.out.println();
			}
			
			for(ANode anode:((Node)root).getChildList()){
				traverse(loc, kws, anode, level + 1);
			}
		}else if(root instanceof Leaf){
			for(int i = 0;i < 4 * level;i ++)   System.out.print("-");
			System.out.println(root.getMbr().toString());
			
			List<Point> objList = ((Leaf)root).getObjList();
			for(Point point:objList){
				for(int i = 0;i < 4 * (level + 1);i ++)   System.out.print("-");
				System.out.println("id:" + point.id + " coordinate:[" + loc[point.id][0] + ", " + loc[point.id][1] + "]");
			}
			Map<String, List<Point>> map = ((Leaf)root).getInvertMap();
			for(String keyword:map.keySet()){
				for(int i = 0;i < 4 * (level + 1);i ++)   System.out.print("-");
				System.out.print(keyword + ":");
				List<Point> list = map.get(keyword);
				for(Point point:list)   System.out.print(point.id + " ");
				System.out.println();
			}
		}
	}
	
	public static void traverseLevel(ANode root, int level){
		if(root instanceof Node){
			System.out.println("Level:" + level + " " + root.getMbr().toString());
			for(ANode anode:((Node)root).getChildList()){
				traverseLevel(anode, level + 1);
			}
		}else if(root instanceof Leaf){
			System.out.println("Level:" + level + " " + root.getMbr().toString());
			System.exit(0);
		}
	}
	
	/**
	 * test output the levels of IR-Tree
	 */
	public static void traverseCount(ANode root, int level, int count[]){
		if(root instanceof Node) {
			count[level + 1] += ((Node)root).getChildList().size();
			for(ANode anode:((Node)root).getChildList()){
				traverseCount(anode, level + 1, count);
			}
		}else if(root instanceof Leaf) {
			count[level + 1] += ((Leaf) root).getObjList().size();
			System.out.println("Level:" + level);
		}
	}
	
	/**
	 * the minDist value between a sub-IR-tree and a point
	 * @return the minDist value
	 */
	public static double traverseMinDist(ANode root, Point x) {
		double minDist = Double.MAX_VALUE;
		if(root instanceof Node){
			for(ANode anode : ((Node) root).getChildList()) {
				minDist = Double.min(minDist, traverseMinDist(anode, x));
			}
		} else if(root instanceof Leaf){
			for(Point obj : ((Leaf) root).getObjList()) {
				minDist = Double.min(minDist, Point.dist(x, obj));
			}
		}
		return minDist;
	}
	/**
	 * the minDist value for 2 sub-IR-tree
	 * @return the minDist value
	 */
	public static double traverseMinDist(ANode root1, ANode root2) {
		double minDist = Double.MAX_VALUE;
		if(root2 instanceof Node){
			for(ANode anode : ((Node) root2).getChildList()) {
				minDist = Double.min(minDist, traverseMinDist(root1, anode));
			}
		} else if(root2 instanceof Leaf){
			for(Point obj : ((Leaf) root2).getObjList()) {
				minDist = Double.min(minDist, traverseMinDist(root1, obj));
			}
		}
		return minDist;
	}
	
	/**
	 * defined in CoSKQ 4.3 definition 2
	 * @return the minCost value
	 */
	public static double minCost(HashSet<ANode> nodeSet, Point query) {
		if (nodeSet.size() == 1) {
			for (ANode node : nodeSet)
				return traverseMinDist(node, query);
			return -1;	// will never happen
		} else {
			double maxqeiDist = 0, maxejekDist = 0;
			for (ANode ei : nodeSet)
				maxqeiDist = Double.max(maxqeiDist, traverseMinDist(ei, query));
			for (ANode ej : nodeSet)
				for (ANode ek : nodeSet)
					maxejekDist = Double.max(maxejekDist, traverseMinDist(ej, ek));
			return maxqeiDist + maxejekDist;
		}
	}
	
	public static boolean hasKeyword(ANode anode, String keyword) {
		if (anode instanceof Leaf) {
			Leaf leaf = (Leaf) anode;
			if (leaf.getInvertMap().get(keyword) != null) return true;
		} else {
			Node node = (Node) anode;
			if (node.getInvertMap().get(keyword) != null) return true;
		}
		return false;
	}

	/**
	 * find the points with the specified keyword in the IR-tree
	 * @return a set of points with the keyword
	 */
	public static Set<Point> findKws(ANode root, String keyword){
		Set<Point> set = new HashSet<Point>();
		if(root instanceof Leaf){
			List<Point> list = ((Leaf) root).getInvertMap().get(keyword);
			if (list != null) set.addAll(list);
		} else {
			List<ANode> kwsChildList = ((Node)root).getInvertMap().get(keyword);
			if (kwsChildList != null) {
				for (ANode anode : kwsChildList){
					Set<Point> tmpSet = findKws(anode, keyword);
					set.addAll(tmpSet);
				}
			}
		}
		return set;
	}
}
