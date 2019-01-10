package com.spacekey.algorithm.spm.util;

import com.spacekey.algorithm.spm.irtree.MBR;

/**
 * @author yxfang
 * @date 2016-8-9
 */
public class EuclideanTest {

	public static void main(String[] args) {
		MBR mbr1 = new MBR(1, 1, 2, 2);

//		MBR mbr2 = new MBR(3, 3, 4, 4);//case 1
//		MBR mbr2 = new MBR(3, -1, 4, 0);//case 3
//		MBR mbr2 = new MBR(3, 0, 4, 2);//case 2
		
//		MBR mbr2 = new MBR(-1, 3, 0, 4);//case 4
//		MBR mbr2 = new MBR(-1, -1, 0, 0);//case 6
//		MBR mbr2 = new MBR(-1, 1, 0, 3);//case 5
		
//		MBR mbr2 = new MBR(1, 3, 2, 4);//case 7
//		MBR mbr2 = new MBR(1, -1, 2, 0);//case 8
//		MBR mbr2 = new MBR(1, 0, 2, 2);//case center
//		MBR mbr2 = new MBR(1, 0, 2, 3);//case center
//		MBR mbr2 = new MBR(1.4, 1.4, 1.6, 1.6);//case center
		
		MBR mbr2 = new MBR(10, 1, 100, 100);//casual case
		
		System.out.println(Euclidean.obtainMinDist(mbr1, mbr2) + " " + Euclidean.obtainMaxDist(mbr1, mbr2));
	}

}
