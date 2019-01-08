package com.spacekey.algorithm.spm.algorithm.msj.anchor;

import java.util.HashSet;
import java.util.Set;

import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2016-11-25
 * find the vertices in the 2-core of the pattern
 */
public class Find2Core {

	public Set<Integer> find(Pattern pattern){
		int m = pattern.getM();
		int graph[][] = pattern.getGraph();
		
		//step 1: form the graph, whose vertices are 1, 2, 3, ..., m
		int coreGraph[][] = new int[m + 1][];
		for(int i = 0;i < m;i ++){
			Set<Integer> set = new HashSet<Integer>();//if i has two edges with j, then only one j is kept
			for(int nghId:graph[i])   set.add(nghId);
			
			coreGraph[i + 1] = new int[set.size()];
			int j = 0;
			for(int nghId:set){
				coreGraph[i + 1][j] = nghId + 1;//increase their id numbers by 1
				j += 1;
			}
		}
		
		//step 2: find 2-core
		FindKCore finder = new FindKCore(coreGraph, 2);
		int core[] = finder.decompose();
		
		//step 3: obtain vertices whose core numbers are at least 2
		Set<Integer> rsSet = new HashSet<Integer>();
		for(int i = 1;i <= m;i ++)   if(core[i] >= 2)   rsSet.add(i - 1);//decrease the core numbers by 1
		
		return rsSet;
	}
}