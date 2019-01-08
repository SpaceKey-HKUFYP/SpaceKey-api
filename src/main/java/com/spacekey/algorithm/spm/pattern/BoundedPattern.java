package com.spacekey.algorithm.spm.pattern;


/**
 * @author yxfang
 * @date 2016-10-18
 * Given a pattern, we compute the upper and lower bound of each pair of vertices
 */
public class BoundedPattern {
	private Pattern pattern = null;
	private double lower[][] = null;
	private double upper[][] = null;
	
	public BoundedPattern(Pattern pattern){
		this.pattern = pattern;
	}
	
	public void computeBound(){
		int m = pattern.getM();
		double lb[][] = pattern.getLb();
		double ub[][] = pattern.getUb();
		lower = new double[m][m];
		upper = new double[m][m];
		for(int i = 0;i < m;i ++){
			for(int j = 0;j < m;j ++){
				lower[i][j] = 0;
				if(j != i)   upper[i][j] = Integer.MAX_VALUE;
				else         upper[i][j] = 0;
			}
		}
		
		//step 1: build the undirected graph
		for(int i = 0;i < m;i ++){
			for(int j = 0;j < m;j ++){
				if(lb[i][j] >= 0.0 && lb[j][i] >= 0.0){
					lower[i][j] = lower[j][i] = Math.max(lb[i][j], lb[j][i]);
					upper[i][j] = upper[j][i] = Math.min(ub[i][j], ub[j][i]);
				}else{
					if(lb[i][j] >= 0)   lower[i][j] = lower[j][i] = lb[i][j];
					if(ub[i][j] >= 0)   upper[i][j] = upper[j][i] = ub[i][j];
				}
			}
		}
		
		//step 2: compute the upper-bound
		for(int k = 0;k < m;k ++){
			for(int i = 0;i < m;i ++){
				for(int j = 0;j < m;j ++){
					double dist = upper[i][k] + upper[k][j];
					if(dist < upper[i][j])   upper[i][j] = dist;
				}
			}
		}
		
		//step 3: compute the lower-bound
		for(int k = 0;k < m;k ++){
			for(int i = 0;i < m;i ++){
				for(int j = 0;j < m;j ++){
					if(i != j){
						double dist = 0;
						if(upper[i][k] < lower[k][j]){//updated
							dist = lower[k][j] - upper[i][k];
						}else if(lower[i][k] > upper[k][j]){
							dist = lower[i][k] - upper[k][j];
						}else{
							dist = 0;
						}
						if(dist > lower[i][j])   lower[i][j] = dist;
					}
				}
			}
			
			//test: print the results
//			for(int i = 0;i < m;i ++){
//				for(int j = 0;j < m;j ++){
//					System.out.print("[" + lower[i][j] + ", " + upper[i][j] + "]\t");
//				}
//				System.out.println();
//			}
//			System.out.println();
		}
	}
	
	public double[][] getLower() {
		return lower;
	}

	public double[][] getUpper() {
		return upper;
	}
}
