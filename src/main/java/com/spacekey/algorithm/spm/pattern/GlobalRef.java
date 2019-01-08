package com.spacekey.algorithm.spm.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yxfang
 * @date 2016-11-21
 * Refining the pattern based on the bounded pattern
 */
public class GlobalRef {
	private Pattern pattern = null;
	private BoundedPattern bpattern = null;
	
	public GlobalRef(Pattern pattern, BoundedPattern bpattern){
		this.pattern = pattern;
		this.bpattern = bpattern;
	}
	
	public Pattern refine(){
		int m = pattern.getM();
		boolean mark[][] = pattern.getMark();
		double lb[][] = pattern.getLb(), ub[][] = pattern.getUb();
		double lower[][] = bpattern.getLower(), upper[][] = bpattern.getUpper();
		
		boolean edgeRemoved = false;
		for(int i = 0;i < m;i ++){
			for(int j = i + 1;j < m;j ++){
				if(ub[i][j] > 0){
					boolean first = isOverlap(lb[i][j], ub[i][j], lower[i][j], upper[i][j]);
					if(first){
						if(!mark[i][j] && !mark[j][i]){//edges with inclusion-ship
							if(lower[i][j] > lb[i][j] && upper[i][j] < ub[i][j]){//delete one edge
								lb[i][j] = lb[j][i] = ub[i][j] = ub[j][i] = - 1;
								edgeRemoved = true;
//								System.out.println("An edge is removed");
							}else{
								if(lower[i][j] > lb[i][j])   lb[i][j] = lb[j][i] = lower[i][j];
								if(upper[i][j] < ub[i][j])   ub[i][j] = ub[j][i] = upper[i][j];
							}
						}else{
							if(upper[i][j] < ub[i][j])   ub[i][j] = ub[j][i] = upper[i][j];
						}
					}else{//edges with exclusion-ship
//						System.out.println("A wrong pattern detected !");
						return null;
					}
				}
			}
		}
		
		//update the graph, which contains the neighbor set
		//(1): i != j
		//(2): all the neighbors
		if(edgeRemoved){
			int graph[][] = new int[m][];
			for(int i = 0;i < m;i ++){
				List<Integer> list = new ArrayList<Integer>();
				for(int j = 0;j < m;j ++){
					if(i != j && ub[i][j] > 0){
						list.add(j);
					}
				}
				
				graph[i] = new int[list.size()];
				for(int j = 0;j < list.size();j ++)   graph[i][j] = list.get(j);
			}
			pattern.setGraph(graph);
			
			//test codes
//			String label[] = pattern.getLabel();
//			for(int i = 0;i < m;i ++){
//				System.out.print(label[i] + ": ");
//				for(int j:graph[i])   System.out.print(label[j] + " ");
//				System.out.println();
//			}
		}
		
		return pattern;
	}
	
	private boolean isOverlap(double lb1, double ub1, double lb2, double ub2){
		if((ub1 < lb2) || (ub2 < lb1))   return false;
		else                             return true;
	}
}
