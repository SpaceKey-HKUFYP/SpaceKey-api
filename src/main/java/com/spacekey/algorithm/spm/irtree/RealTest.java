package com.spacekey.algorithm.spm.irtree;

import java.util.Set;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;

/**
 * @author yxfang
 * @date 2016-8-11
 */
public class RealTest {

	public static void main(String[] args) {
		Config.Fanout = 100;
		
		//step 1: read data
		String indexFile = Config.indexNY;
		DataReader dataReader = new DataReader(Config.locNY, Config.docNY);
		double loc[][] = dataReader.readLoc();
		String kws[][] = dataReader.readKws();

		//step 2: build index
		BuildIRTree builder = new BuildIRTree(loc, kws, indexFile);
		Node root = builder.build();
		
//		int count[] = new int[10];
//		count[0] = 1;
//		IRTreeOperator.traverseCount(root, 0, count);
//		System.out.println(count.length);
//		for(int i = 0;i < count.length;i ++){
//			System.out.println("level=" + i + " count=" + count[i]);
//		}
		
		Set<Point> set = IRTreeOperator.findKws(root, "73");
		for(Point point:set)   System.out.println(point.id + " ");
	}

}
