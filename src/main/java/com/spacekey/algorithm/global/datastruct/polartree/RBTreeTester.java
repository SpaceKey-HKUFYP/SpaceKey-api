package com.spacekey.algorithm.global.datastruct.polartree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;

import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.STObject;
import com.spacekey.algorithm.global.util.Util;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class PolarTree
 * @date 2015-09-03
 *
 */

public class RBTreeTester {

	public static void main(String [] args) {
		double cpuTimeElapsed;

		int len = 1000000;
		RBItem [] items = new RBItem[len];
		TreeSet<RBItem> treeSet = new TreeSet<RBItem>(RBItem.ComparePolar);
		HashSet<RBItem> hashSet = new HashSet<RBItem>();
		Random rand = new Random(3);
		HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"1", "2", "3", "4", "5", "6"}));
		STObject obj = new STObject(1, 0.2, 0.4, T);
		Words w = new Words(T);
		RBTree pt = new RBTree(w, obj);
		
		for (int i=0; i < len; i++) {
			items[i] = new RBItem(rand.nextInt(360)+rand.nextDouble(), obj, 0);
		}
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			pt.insert(items[i]);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for rb-tree: " + cpuTimeElapsed/(double)1000000000 + "secs");
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			treeSet.add(items[i]);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for TreeSet: " + cpuTimeElapsed/(double)1000000000 + "secs");
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			hashSet.add(items[i]);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for HashSet: " + cpuTimeElapsed/(double)1000000000 + "secs");
		
		
		pt.printTree();
	}
}
