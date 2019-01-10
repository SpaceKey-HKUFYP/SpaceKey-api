package com.spacekey.algorithm.spm.irtree;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yxfang
 * @date 2016-8-8
 * Define some operations for the IR-tree to do some simple test
 */
public class IRTreeOperator {
	
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
				for(ANode anode:list)   System.out.print("*");
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
	
	public static void traverseCount(ANode root, int level, int count[]){
		if(root instanceof Node){
			count[level + 1] += ((Node)root).getChildList().size();
			for(ANode anode:((Node)root).getChildList()){
				traverseCount(anode, level + 1, count);
			}
		}else if(root instanceof Leaf){
			count[level + 1] += ((Leaf)root).getObjList().size();
			System.out.println("Level:" + level);
		}
	}

	public static Set<Point> findKws(ANode root, String keyword){
		Set<Point> set = new HashSet<Point>();
		if(root instanceof Leaf){
			List<Point> list = ((Leaf) root).getInvertMap().get(keyword);
			set.addAll(list);
		}else{
			List<ANode> kwsChildList = ((Node)root).getInvertMap().get(keyword);
			for(ANode anode:kwsChildList){
				Set<Point> tmpSet = findKws(anode, keyword);
				set.addAll(tmpSet);
			}
		}
		return set;
	}
}
