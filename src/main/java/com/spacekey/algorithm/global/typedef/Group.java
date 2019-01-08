package com.spacekey.algorithm.global.typedef;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Group
 * @author Dong-Wan Choi
 * @date 2015. 8. 28.
 */
public class Group implements Iterable<STObject>{

	protected ArrayList<STObject> g;
	private WordTab tab;
	private double d = -1;
	
	public Group() {
		g = new ArrayList<STObject>();
	}
	
	public Group(STObject o) {
		g = new ArrayList<STObject>();
		g.add(o);
	}
	
	public Group(Collection<? extends STObject> c) {
		g = new ArrayList<STObject>(c);
	}
	
	public int size() {
		return g.size();
	}

	public double dia() {
		if (d >= 0) return d;
		else {
			double max = Double.MIN_VALUE;
			double dist;
			for (int i=0; i < g.size(); i++) {
				for (int j=i+1; j < g.size(); j++) {
					dist = g.get(i).loc.distance(g.get(j).loc);
					if ( max < dist ) max = dist;
				}
			}
			d = max;
			return d;
		}
	}
	
	
	public void updateDia() {
		double max = Double.MIN_VALUE;
		double dist;
		for (int i=0; i < g.size(); i++) {
			for (int j=i+1; j < g.size(); j++) {
				dist = g.get(i).loc.distance(g.get(j).loc);
				if ( max < dist ) max = dist;
			}
		}
		d = max;
	}

	public double cost1() {
		return (size()-1)*dia();
	}
	public double cost2() {
		long n = (long) size();
		long comb = n*(n-1)/2;
		return comb * dia();
	}
	
	public double rcost1() {
		sort();
//		STObject obj = g.get(0);
//		double rcost1 = 0;
//		
//		for (STObject o: g) {
//			rcost1 += obj.loc.distance(o.loc);
//		}
//		return rcost1;
		
//		double cost, minCost = Double.MAX_VALUE;
//		STObject o1, o2;
//		
//		for (int i=0; i < g.size(); i++) {
//			o1 = g.get(i);
//			cost = 0;
//			for (int j=0; j < g.size(); j++) {
//				o2 = g.get(j);
//				cost += o1.loc.distance(o2.loc);
//			}
//			if (cost < minCost) {
//				minCost = cost;
//			}
//		}
//		return minCost;
		
		double cost, maxCost = Double.MIN_VALUE;
		STObject o1, o2;
		
		for (int i=0; i < g.size(); i++) {
			o1 = g.get(i);
			cost = 0;
			for (int j=0; j < g.size(); j++) {
				o2 = g.get(j);
				cost += o1.loc.distance(o2.loc);
			}
			if (cost > maxCost) {
				maxCost = cost;
			}
		}
		return maxCost;
	}
	
	
//	public double rcost1() {
//		return permuteCost(0);
//	}
	
	public double permuteCost(int k) {
		double cost = 0, maxCost = Double.MIN_VALUE;
		STObject o1, o2;
		for (int i = k; i < g.size(); i++) {
			Collections.swap(g, i, k);
			maxCost = Math.max(permuteCost(k+1), maxCost);
			Collections.swap(g, k, i);
		}
		if (k == g.size() -1) {
			for (int j=0; j < g.size()-1; j++) {
				o1 = g.get(j);
				o2 = g.get(j+1);
				cost += o1.loc.distance(o2.loc);
			}
			
			o1 = g.get(g.size()-1);
			o2 = g.get(0);
			cost += o1.loc.distance(o2.loc);

//			System.out.println(g);
//			System.out.println("cost = " + cost);
		}
		
		maxCost = Math.max(maxCost, cost);
		
		return maxCost;
	}
	
	
	public double rcost2() {
		STObject o1, o2;
		double rcost2 = 0;
		for (int i=0; i < g.size(); i++) {
			o1 = g.get(i);
			for (int j=i; j < g.size(); j++) {
				o2 = g.get(j);
				rcost2 += o1.loc.distance(o2.loc);
			}
		}
		return rcost2;
	}
	
	public void add(STObject o) {
		g.add(o);
	}
	
	public void addAll(Collection<? extends STObject> c) {
		g.addAll(c);
	}

	public boolean covers(HashSet<String> T) {
		if (tab == null) {
			tab = new WordTab();
			for (STObject o: g) tab.add(o);
		}
		return tab.containsAll(T);
	}
	
	public void shrink(HashSet<String> T) {
		// perform greedy set cover
		HashSet<String> U = new HashSet<String>(T);
		ArrayList<STObject> tg = new ArrayList<STObject>();
		
		while (!U.isEmpty()) {
			int cnt, maxC = Integer.MIN_VALUE;
			STObject next=null;
			for (STObject o: g) {
				cnt = o.interCnt(U);
				if (cnt > maxC) {
					maxC = cnt;
					next = o;
				}
			}
			if (next != null) {
				U.removeAll(next.text);
				tg.add(next);
			}
		}
		g = tg;
		tab = null;
	}
	
	@Override
	public Iterator<STObject> iterator() {
		return g.iterator();
	}
	
	public void sort() {
		Collections.sort(g, STObject.CompareLoc);
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append("dia: " + dia() + " n: " + size() + "\n");
		s.append("cost1: " + cost1() + " cost2: " + cost2() + "\n");
		s.append("rcost1: " + rcost1() + " rcost2: " + rcost2() + "\n");
		s.append(g.toString() + "\n");
//		for (STObject o: g) s += o.toString() + "\n";
		
		return s.toString();
	}
	
}
