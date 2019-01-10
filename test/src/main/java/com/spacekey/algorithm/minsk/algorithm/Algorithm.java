package com.spacekey.algorithm.minsk.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.google.common.collect.TreeMultiset;
import com.google.common.math.DoubleMath;
import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.brtree.BRTree;
import com.spacekey.algorithm.global.datastruct.cbrtree.CBRTree;
import com.spacekey.algorithm.global.datastruct.cbrtree.CEntry;
import com.spacekey.algorithm.global.datastruct.docindex.InvertedFile;
import com.spacekey.algorithm.global.datastruct.polartree.RBTree;
import com.spacekey.algorithm.global.typedef.CardTab;
import com.spacekey.algorithm.global.typedef.Circle;
import com.spacekey.algorithm.global.typedef.Dataset;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.Lune;
import com.spacekey.algorithm.global.typedef.STObject;
import com.spacekey.algorithm.global.typedef.WordTab;
import com.spacekey.algorithm.global.util.Debug;
import com.spacekey.algorithm.global.util.Util;


/**
 * @author wangj
 * @version 8.18 
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
	
	/**
	 * @param S, the collection of sets (i.e., objects)
	 * @param T, the set of keywords to be covered
	 * @return the subset of S that covers T
	 */
	private Group fastSetCover(Group S, HashSet<String> T) {
		CardTab tb = new CardTab(); // partition
		HashSet<String> C = new HashSet<String>(); // covered keywords so far
		Group g = new Group();
		
		// partition the sets into sub-collections
		for (STObject o: S) tb.add(o);
		
		// start covering elements
		double k, pk;
		int diff;
		for (int l = tb.max; l >= 1; l--) {
			if (!tb.containsKey(l)) continue;
			
			k = (int) Math.floor(DoubleMath.log2(l)/DoubleMath.log2(Env.P));
			pk = Math.pow(Env.P, k); // p^k
			HashSet<STObject> cardSet = tb.get(l);
			for (STObject o: cardSet) {
				diff = o.diffCnt(C); // |o.\tau - C|
				if (diff >= pk) {
					g.add(o);
					C.addAll(o.text);
				} else {
//					cardSet.remove(o);
					if (diff > 0) tb.add(o, diff);
				}
				
				if (C.containsAll(T)) return g;
			}
		}
		return null;
	}
	
	private Group GKG4ScaleLune(HashSet<String> T, CBRTree crt, InvertedFile iv, Words w){
		Group g = null;
		
		double mind = Double.MAX_VALUE;
		
		String t_inf = null;
		int minf = Integer.MAX_VALUE;
		for (String t: T) {
			if (iv.freq(t) < minf) {
				minf = iv.freq(t);
				t_inf = t;
			}
		}
		
		TreeMultiset<STObject> postings = iv.getList(t_inf);
		
		for (STObject o: postings) {
			Group tg = new Group();
			tg.add(o);
			
			HashSet<String> ucSet = new HashSet<String>(T);
			ucSet.removeAll(o.text);
			tg.addAll(crt.textNNSearch(o.loc, ucSet, w));
			double dia = tg.dia();
			
			if (mind > dia) {
				mind = dia;
				g = tg;
			}
		}
		
		return g;
	}
	
	public Group ScaleLuneCartesian(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		CardTab htb = new CardTab();
		for (STObject o: db) {
			crt.insert(o);
			htb.add(o);
		}
		
		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;
		
		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
		rLB = gr.dia()*0.5;
		gk = fastSetCover(db, T); 
		kLB = gk.size()-1;
		gr.shrink(T);
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}
		
		// Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);
		
		// from Line 7 in Algorithm 2
		for (int l = htb.max; l >= 1; l--) {
			if (!htb.containsKey(l)) continue;
			
			for (STObject o: htb.get(l)) {
				o.checked = true;
				PriorityQueue<CEntry> pq = crt.initPQ(o);
				STObject nn = crt.nextNN(o, pq);
				while(nn != null && o.loc.distance(nn.loc) < fmin/(double)kLB) {
					if ((Math.ceil(T.size()/l)-1)*rLB >= fmin) return g;
					
					if (!nn.checked && nn.text.size() <= l) {
						Lune lune = new Lune(o.loc, nn.loc);
						gl = crt.luneRangeSearch(lune);
						if (gl.covers(T)) {
							gl = fastSetCover(gl, T);
							if (gl.cost1() < fmin) {
								fmin = gl.cost1();
								g = gl;
							}
						}
					}
					nn = crt.nextNN(o, pq);
				}
			}
		}
		
		return g;
	}
	
	public Group ScaleLunePolar(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		CardTab htb = new CardTab();
		for (STObject o: db) {
			crt.insert(o);
			htb.add(o);
		}
		
		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;
		Boolean isCovering;
		
		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
		rLB = gr.dia()*0.5;
		gk = fastSetCover(db, T); 
		kLB = gk.size()-1;
		gr.shrink(T);
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}
		
		// Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);
		
		// from Line 7 in Algorithm 2
		for (int l = htb.max; l >= 1; l--) {
			if (!htb.containsKey(l)) continue;
			
			for (STObject o: htb.get(l)) {
				o.checked = true;
				RBTree pt = new RBTree(w, o);
				PriorityQueue<CEntry> pq = crt.initPQ(o);
				ArrayList<STObject> nns = crt.nextNNs(o, pq);				
				while(!nns.isEmpty() && o.loc.distance(nns.get(0).loc) < fmin/(double)kLB) {
					isCovering = pt.insert(nns);
					if ((Math.ceil(T.size()/l)-1)*rLB >= fmin) return g;

					for (STObject nn: nns) {
						if (nn.checked || nn.text.size() > l) isCovering = false;
					
						if (isCovering == null || isCovering.equals(true)) {
							gl = pt.rangeSearch(nn); gl.add(o);
							if (isCovering == null || (isCovering == true && nns.size()>1))
								isCovering = gl.covers(T);
							if (isCovering.equals(true)) {
								gl = fastSetCover(gl, T);
								if (gl.cost1() < fmin) {
									fmin = gl.cost1();
									g = gl;
								}
							}
						}
					}
					nns = crt.nextNNs(o, pq);
				}
			}
		}
		
		return g;
	}

	
	public Group GreedyMinSK(HashSet<String> T, InvertedFile iv) {
		Group g = null, gmin = null;
		
		Words w = new Words(T);
		Dataset db = iv.filter(T);
		CBRTree crt = new CBRTree(w);
		CardTab htb = new CardTab();
		for (STObject o: db) {
			crt.insert(o);
			htb.add(o);
		}
		
		int lmax = htb.max;
		double minCost = Double.MAX_VALUE;
		for (STObject o_seed: htb.get(lmax)) {
			g = crt.textPriceNNSearch(o_seed.loc, T, w);
			if (minCost > g.cost1()) {
				minCost = g.cost1();
				gmin = g;
			}
		}
		
		return gmin;
	}
	
	public Group MinLune(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		for (STObject o: db) {
			crt.insert(o);
		}

		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;

		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
		rLB = gr.dia()*0.5;
		gk = fastSetCover(db, T); 
		kLB = gk.size();
		gr.shrink(T);
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}

		// Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);


		for (STObject o: db) {
			o.checked = true;
			PriorityQueue<CEntry> pq = crt.initPQ(o);
			STObject nn = crt.nextNN(o, pq);
			while(nn != null) {
				if (!nn.checked) {
					Lune lune = new Lune(o.loc, nn.loc);
					gl = crt.luneRangeSearch(lune);
					if (gl.covers(T)) {
						gl = fastSetCover(gl, T);
						if (gl.cost1() < fmin) {
							fmin = gl.cost1();
							g = gl;
						}
					}
				}
				nn = crt.nextNN(o, pq);
			}
		}

		return g;
	}
	
	public Group MinLuneSpatialPrune(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		for (STObject o: db) {
			crt.insert(o);
		}

		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;

		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
		rLB = gr.dia()*0.5;
		gk = fastSetCover(db, T); 
		kLB = gk.size();
		gr.shrink(T);
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}

		// Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);


		for (STObject o: db) {
			o.checked = true;
			PriorityQueue<CEntry> pq = crt.initPQ(o);
			STObject nn = crt.nextNN(o, pq);
			while(nn != null && o.loc.distance(nn.loc) < fmin/(double)kLB) {
				if (!nn.checked) {
					Lune lune = new Lune(o.loc, nn.loc);
					gl = crt.luneRangeSearch(lune);
					if (gl.covers(T)) {
						gl = fastSetCover(gl, T);
						if (gl.cost1() < fmin) {
							fmin = gl.cost1();
							g = gl;
						}
					}
				}
				nn = crt.nextNN(o, pq);
			}
		}

		return g;
	}

}
