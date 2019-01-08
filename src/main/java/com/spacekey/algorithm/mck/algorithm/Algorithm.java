package com.spacekey.algorithm.mck.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.google.common.collect.TreeMultiset;
import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.brtree.BRTree;
import com.spacekey.algorithm.global.datastruct.docindex.InvertedFile;
import com.spacekey.algorithm.global.typedef.Circle;
import com.spacekey.algorithm.global.typedef.Dataset;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.STObject;
import com.spacekey.algorithm.global.typedef.WordTab;
import com.spacekey.algorithm.global.util.Util;

/**
 * @author wangj
 * @version 8.15
 *
 */
public class Algorithm {
	/**
	 * Greedy Keyword Group (GKG) algorithm inside demonstration
	 * 
	 * Pipeline:
	 * 1. Find the most infrequent keyword tinf among the keywords in q;
	 * 2. Around each object o containing tinf, for each keyword t, we find the nearest object containing t;
	 * 3. These objects form a feasible group, and we denote thi group by Go;
	 * 4. Process all of them, and select the smallest diameter one to answer the query.
	 * 
	 * @param T: the queried keywords
	 * @param brt: BRTree
	 * @param iv: inverted file
	 * @param w: Words
	 * @return: queried result
	 */
	private Group GKG(HashSet<String> T, BRTree brt, InvertedFile iv, Words w){
		Group g = null;
		double mind = Double.MAX_VALUE;
		
		// find the least frequent keyword t_inf
		String t_inf = null;
		int minf = Integer.MAX_VALUE;
		for (String t: T) {
			if (iv.freq(t) < minf) {
				minf = iv.freq(t);
				t_inf = t;
			}
		}
		
		// find all the object with keyword t_inf
		TreeMultiset<STObject> postings = iv.getList(t_inf);
		
		// around each object o containing t_inf, for each keyword t, 
		// we find the nearest object containing t
		for (STObject o: postings) {
			Group tg = new Group();
			tg.add(o);
			HashSet<String> ucSet = new HashSet<String>(T);
			ucSet.removeAll(o.text);
			tg.addAll(brt.textNNSearch(o.loc, ucSet, w));
			double dia = tg.dia();
			
			if (mind > dia) {
				mind = dia;
				g = tg;
			}
		}
		return g;
	}
	
	/**
	 * Greedy Keyword Group (GKG) algorithm public API
	 * 
	 * @param T: the queried keywords
	 * @param iv: inverted file
	 * @return queried result
	 */
	public Group GKG(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		return GKG(T, brt, iv, w);
	}
	
	private class Tuple implements Comparable<Tuple>{
		double angle;
		int flag; // 1 is in, -1 is out
		STObject obj;
		public Tuple(double a, int f, STObject o) {
			angle = a; flag = f; obj = o;
		}
		@Override
		public int compareTo(Tuple o) {
			if (angle > o.angle) return 1;
			else if (angle < o.angle) return -1;
			else {
				if (flag < o.flag) return -1; // out has a higher priority
				else if (flag > o.flag) return 1; // in has a lower priority
				else return 0; // should not happen...
			}
		}
		public String toString() {
			return "<" + angle + ", " + (flag == 1? "In": "Out") + ", " + obj.id + ">";
		}
	}
	
	private double getInAngle(STObject pole, double diam, STObject o) {
		double l = pole.loc.distance(o.loc)*0.5;
		double r = diam * 0.5;
		double theta = Math.toDegrees(Math.acos(l/r));
		double between = Util.getAngle(pole.loc, o.loc);
		if (between + theta > 360) return between + theta - 360;
		else return between + theta;
	}
	
	private double getOutAngle(STObject pole, double diam, STObject o) {
		double l = pole.loc.distance(o.loc)*0.5;
		double r = diam * 0.5;
		double theta = Math.toDegrees(Math.acos(l/r));
		double between = Util.getAngle(pole.loc, o.loc);
		if (between - theta < 0) 
			return 360 + between - theta;
		else 
			return between - theta;
	}
	
	/**
	 * scan the circle around the object within specified diameter
	 * @param obj: object
	 * @param diam: diameter
	 * @param T: queried keywords
	 * @param brt: BRTree
	 * @return a available group around object o 
	 */
	private Group circleScan(STObject obj, double diam, HashSet<String> T, BRTree brt) {
		Group sg = brt.cirRangeSearch(new Circle(obj.loc, diam)); // objects in the sweeping area
		if (!sg.covers(T)) return null;
		
		ArrayList<Tuple> list = new ArrayList<Tuple>();		
		WordTab tab = new WordTab();
		HashSet<STObject> g = new HashSet<STObject>();
		
		double thetaIn, thetaOut;
		for (STObject o: sg) {
			thetaOut = getOutAngle(obj, diam, o);			
			thetaIn = getInAngle(obj, diam, o);
			list.add(new Tuple(thetaOut, -1, o));
			list.add(new Tuple(thetaIn, 1, o));
		}
		Collections.sort(list, Collections.reverseOrder());
		int pos = 0;
		while (!list.isEmpty()) {
			pos = pos % list.size();
			Tuple tuple = list.get(pos);
			if (tuple.flag == 1) { // in
				list.remove(pos); // delete always if it is 'in'-type
				g.add(tuple.obj);
				tab.add(tuple.obj);
				if (tab.containsAll(T)) 
					return new Group(g);
			} else { // out
				if (g.contains(tuple.obj)) { // only if its 'in'-type came before
					list.remove(pos);
					g.remove(tuple.obj);
					tab.remove(tuple.obj);
				} else { // if its 'in'-type never came
					pos++; // continue to the next tuple
				}
			}
		}
		
		return null;
	}
	
	/**
	 * find the SKEC around object o with tolerance using binary search
	 * @param o: the object
	 * @param gkg: the group found by GKG
	 * @param c: circle
	 * @param alpha: tolerance
	 * @param T: queried keywords
	 * @param brt: BRTree
	 * @return the SKEC around object o
	 */
	private Group findAppOSKEC(STObject o, Group gkg, Circle c, double alpha, HashSet<String> T, BRTree brt) {
		double searchUB = c.dia();
		double searchLB, diam;
		Group g = null, tg = null;
		
		tg = circleScan(o, searchUB, T, brt);
		if (tg == null) return null;
		searchLB = gkg.dia() * 0.5;
		
		while (searchUB - searchLB > alpha) {
			diam = (searchUB + searchLB) * 0.5;
			tg = circleScan(o, diam, T, brt);
			if (tg != null) {
				searchUB = diam; 
				g = tg;
			} else searchLB = diam;
		}	
		return g;
	}
	
	/**
	 * Implementation of Algorithm SKECa
	 * 
	 * Pipeline:
	 * 1. Use the GKG algorithm to obtain a group to provide the initial upper bound of SKECa
	 * 2. find SKEC with tolerance using binary search (findAppOSKEC) around each object o
	 * 
	 * @param T: queried keywords
	 * @param iv: inverted file
	 * @return the SKEC around object o
	 */
	public Group SKECa(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		Group g = GKG(T, brt, iv, w);
		Circle c = Util.findSec(g);
		double alpha = g.dia()*0.5*Env.Ep;
		Group tg;
		for (STObject o : db) {
			if (o.text.containsAll(T)) return new Group(o);
			tg = findAppOSKEC(o, g, c, alpha, T, brt);
			if (tg != null) {
				g = tg;
				c = Util.findSec(g);
			}
		}
		return g;
	}
	
	/**
	 * Implementation of Algorithm SKECa+
	 * 
	 * Pipeline:
	 * 1. Use the GKG algorithm to obtain a group to provide the initial upper bound of SKECa
	 * 2. Perform binary search on all objects in O together and find any circle around every object o
	 * 
	 * @param T: queried keywords
	 * @param iv: inverted file
	 * @return the SKEC around object o
	 */
	public Group SKECaplus(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		boolean foundResult;
		Group g, tg; 
		Circle c;
		double searchUB, searchLB, alpha, diam;
		double [] maxInvalidRange = new double[db.size()];
		
		g = GKG(T, brt, iv, w);
		c = Util.findSec(g);
		alpha = g.dia()*0.5*Env.Ep;		
		searchUB = c.dia();
		searchLB = g.dia() * 0.5;
		
		for (int i=0; i < db.size(); i++) {
			STObject o = db.get(i);
			if (o.text.containsAll(T)) return new Group(o);
			maxInvalidRange[i] = 0;
		}
		
		while (searchUB - searchLB > alpha) {
			diam = (searchUB + searchLB) * 0.5;
			foundResult = false;
			
			for (int i=0; i < db.size(); i++) {
				STObject o = db.get(i);
				if (diam < maxInvalidRange[i]) continue;
				
				tg = circleScan(o, diam, T, brt);
				if (tg != null) {
					searchUB = diam;
					g = tg;
					foundResult = true;
					break;
				} else {
					if (diam > maxInvalidRange[i])
						maxInvalidRange[i] = diam;
				}
			}
			if (!foundResult) searchLB = diam;
		}
		return g;
	}
	
	public Group Exact(HashSet<String> T, InvertedFile iv) {
		// --------------------------SKECa+----------------------------
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		boolean foundResult;
		Group g = GKG(T, brt, iv, w), tg;
		Circle c = Util.findSec(g);
		double diam;
		double alpha = g.dia()*0.5*Env.Ep;
		double searchUB = c.dia();
		double searchLB = g.dia() * 0.5;
		double [] maxInvalidRange = new double[db.size()];
		
		for (int i=0; i < db.size(); i++) {
			STObject o = db.get(i);
			if (o.text.containsAll(T)) return new Group(o);
			maxInvalidRange[i] = 0;
		}
		
		while (searchUB - searchLB > alpha) {
			diam = (searchUB + searchLB) * 0.5;
			foundResult = false;
			for (int i=0; i < db.size(); i++) {
				STObject o = db.get(i);
				if (diam < maxInvalidRange[i]) continue;
				tg = circleScan(o, diam, T, brt);
				if (tg != null) {
					searchUB = diam;
					g = tg;
					foundResult = true;
					break;
				} else {
					if (diam > maxInvalidRange[i])
						maxInvalidRange[i] = diam;
				}
			}
			if (!foundResult) searchLB = diam;
		}
		// --------------------------SKECa+----------------------------
		
		Group Gskeca = g;
		diam = Util.findSec(Gskeca).dia() * 2 / Math.sqrt(3);
		Group bestGroup = Gskeca;
		
		for (int i=0; i < db.size(); i++) {
			STObject o = db.get(i);
			if (maxInvalidRange[i] < diam) {
				circleScanSearch(o, diam, T, brt, bestGroup);
			}
		}
		return bestGroup;
	}
	
	private void circleScanSearch(STObject obj, double diam, HashSet<String> T, BRTree brt, Group bestGroup) {
		Group sg = brt.cirRangeSearch(new Circle(obj.loc, diam)); // objects in the sweeping area
		if (!sg.covers(T)) return;
		
		ArrayList<Tuple> list = new ArrayList<Tuple>();		
		WordTab tab = new WordTab();
		HashSet<STObject> g = new HashSet<STObject>();
		
		double thetaIn, thetaOut;
		for (STObject o: sg) {
			thetaOut = getOutAngle(obj, diam, o);			
			thetaIn = getInAngle(obj, diam, o);
			list.add(new Tuple(thetaOut, -1, o));
			list.add(new Tuple(thetaIn, 1, o));
		}
		Collections.sort(list, Collections.reverseOrder());
		int pos = 0;
		while (!list.isEmpty()) {
			pos = pos % list.size();
			Tuple tuple = list.get(pos);
			if (tuple.flag == 1) { // in
				list.remove(pos); // delete always if it is 'in'-type
				g.add(tuple.obj);
				tab.add(tuple.obj);
				if (tab.containsAll(T)) {
					HashSet<STObject> selectedSet 	= new HashSet<STObject>();
					HashSet<STObject> candidateSet 	= new HashSet<STObject>();
					candidateSet.addAll(g);
					search(T, selectedSet, candidateSet, 0, bestGroup);
				}
			} else { // out
				if (g.contains(tuple.obj)) { // only if its 'in'-type came before
					list.remove(pos);
					g.remove(tuple.obj);
					tab.remove(tuple.obj);
				} else { // if its 'in'-type never came
					pos++; // continue to the next tuple
				}
			}
		}
	}
	
	private void search(HashSet<String> T, HashSet<STObject> selectedSet, HashSet<STObject> candidateSet, double maxId, Group bestGroup) {
		HashSet<String> selectedKeywords = new HashSet<String>();
		for (STObject obj : selectedSet)
			selectedKeywords.addAll(obj.text);
		boolean cover = true;
		for (String keyword : T) {
			if (!selectedKeywords.contains(keyword)) {
				cover = false; break;
			}
		}
		Group selected = new Group(selectedSet);
		if (cover) {
			if (selected.dia() <= bestGroup.dia()) {
				bestGroup = selected;
			} return;
		}
		if (selected.dia() > bestGroup.dia()) return;
		
		HashSet<STObject> nextSet = new HashSet<STObject>();
		HashSet<STObject> newSet = new HashSet<STObject>();
		HashSet<String> leftKeywords = new HashSet<String>();
		
		for (STObject oc : candidateSet) {
			newSet.clear();
			newSet.add(oc);
			newSet.addAll(selectedSet);
			Group newGroup = new Group(newSet);
			if (newGroup.dia() > bestGroup.dia()) continue;
			boolean intersect = false;
			for (String keyword : T) {
				if (!selectedKeywords.contains(keyword) && oc.text.contains(keyword))
					intersect = true;
			}
			if (!intersect) continue;
			if (oc.id < maxId) continue;
			nextSet.add(oc);
			leftKeywords.addAll(oc.text);
		}
		for (String keyword : T) {
			if (!leftKeywords.contains(keyword) && 
				!selectedKeywords.contains(keyword)) return;
		}
		for (STObject on : nextSet) {
			selectedSet.add(on);
			candidateSet.remove(on);
			search(T, selectedSet, candidateSet, on.id, bestGroup);
			selectedSet.remove(on);
			candidateSet.add(on);
		}
	}
}
