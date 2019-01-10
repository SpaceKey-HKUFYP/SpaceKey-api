package com.spacekey.algorithm.spm.util;

/**
 * @author yxfang
 * @date 2016-10-19
 */
public class FordTest {

	public static void main(String[] args) {
		int m = 7;
		double u[][] = new double[m][m];
		for(int i = 0;i < m;i ++){
			for(int j = 0;j < m;j ++){
				u[i][j] = Double.MAX_VALUE;
			}
		}
		
		u[0][2] = 10;
		u[0][6] = 100;
		u[2][3] = 10;
		u[3][1] = 10;
		u[1][4] = 10;
		u[4][5] = 10;
		u[5][6] = 10;
		
		for(int k = 0;k < m;k ++){
			for(int i = 0;i < m;i ++){
				for(int j = 0;j < m;j ++){
					double dist = u[i][k] + u[k][j];
					if(dist < u[i][j])   u[i][j] = dist;
				}
			}
		}
		System.out.println("u[0][1]=" + u[0][1]);
		
		for(int i = 0;i < m;i ++){
			for(int j = 0;j < m;j ++){
				if(u[i][j] > 100000){
					System.out.print("99.9" + "\t");
				}else{
					System.out.print(u[i][j] + "\t");
				}
			}
			System.out.println();
		}
	}

}
