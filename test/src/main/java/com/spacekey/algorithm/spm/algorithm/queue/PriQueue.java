package com.spacekey.algorithm.spm.algorithm.queue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author yxfang
 * @date 2016-8-17
 * a priority query for cost pairs (id1, id2, cost)
 */
public class PriQueue {
	private Queue<CPair> queue = null;
	
	public PriQueue(){
		Comparator<CPair> orderIsdn =  new Comparator<CPair>(){
			public int compare(CPair o1, CPair o2) {
				double cost1 = o1.cost;
				double cost2 = o2.cost;
				if(cost1 > cost2){
					return 1;
				}else if(cost1 < cost2){
					return -1;
				}else{
					return 0;
				}
			}
		};
		
		queue =  new PriorityQueue<CPair>(5, orderIsdn);
	}

	public Queue<CPair> getQueue() {
		return queue;
	}

	public static void main(String args[]){
		PriQueue queue = new PriQueue();
		queue.getQueue().add(new CPair(1, 2, 10));
		queue.getQueue().add(new CPair(2, 3, 5));
		queue.getQueue().add(new CPair(3, 4, 20));
		queue.getQueue().add(new CPair(3, 1, 2));
		
		while(queue.getQueue().size() > 0){
			CPair pair = queue.getQueue().poll();
			System.out.println(pair.id1 + "\t" + pair.id2 + "\t" + pair.cost);
		}
	}
}
