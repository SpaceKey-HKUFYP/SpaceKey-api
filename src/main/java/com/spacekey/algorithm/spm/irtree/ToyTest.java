package com.spacekey.algorithm.spm.irtree;

import java.util.Set;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;

/**
 * @author yxfang
 * @date 2016-8-8
 */
public class ToyTest {

	public static void main0(String[] args) {
		Config.Fanout = 10;
		
		DataReader dataReader = new DataReader(Config.locNY, Config.docNY);
		double loc[][] = dataReader.readLoc();
		String kws[][] = dataReader.readKws();
		
		BuildIRTree builder = new BuildIRTree(loc, kws, "./spm/info/index/");
		Node root = builder.build();
	}

	public static void main(String[] args) {
		Config.Fanout = 2;
		
		double loc[][] = {{1, 4}, {2, 1}, {5, 5}, {6, 3}};
		String kws[][] = {{"a", "b"}, {"a", "c"}, {"b", "c"}, {"a", "b", "d", "e"}};
		
//		BuildIRTree builder = new BuildIRTree(loc, kws);
		BuildIRTree builder = new BuildIRTree(loc, kws, "./info/");
		Node root = builder.build();
		
		//traverse the IR-tree
		IRTreeOperator.traverse(loc, kws, root, 0);

		//find objects having a specific keyword
//		Set<Integer> set = IRTreeOperator.findKws(root, "e");
//		for(int id:set)   System.out.print(id + ", ");
//		System.out.println();
	}
}
