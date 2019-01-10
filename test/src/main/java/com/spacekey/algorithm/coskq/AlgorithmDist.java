package com.spacekey.algorithm.coskq;

import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;

import com.spacekey.algorithm.coskq.index.ANode;
import com.spacekey.algorithm.coskq.index.Leaf;
import com.spacekey.algorithm.coskq.index.Node;
import com.spacekey.algorithm.coskq.pattern.Entry;
import com.spacekey.algorithm.coskq.pattern.EntryComparator;
import com.spacekey.algorithm.coskq.pattern.Pattern;
import com.spacekey.algorithm.coskq.pattern.Point;
import com.spacekey.algorithm.global.Pair;

import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author wangj
 * @date Aug 23, 2017
 */
public class AlgorithmDist {
	/**
	 * Distance Owner-Driven Approach proposed by SIGMOD'13 paper - CoSKQ - A Distance Owner-Driven Approach
	 * (1) (Query Distance Owner Finding): Select one object o in Oq to take the role of the query distance owner of a set S' to be found
	 * (2) (Pairwise Distance Owner Finding): Select two objects, o1 and o2, in Oq to take the roles of the pariwise distance owners of 
	 * 		the set S' (to be found). Note that o, o1 and o2 form a distance owner group
	 * (3) (Sub-Optimal Feasible Set Finding): Find the set S' which is (o, o1, o2)-owner consistent (if any), and update S with S' 
	 * 		if cost(S') < cost(S)
	 * (4) (Iterative Step): Repeat Step 1 and Step 2 which find another distance owner group, and continue with SDtep 3 until 
	 * 		all distance owner groups are traversed
	 * @return a pair of result, first is cost, second is the group
	 * */
	public Pair<Double, HashSet<Point>> Type2DistExact(Pattern query, Node root) {
		
		HashSet<Point> S = new HashSet<Point>();
		HashSet<Point> S_ = new HashSet<Point>();
		HashSet<Point> R = new HashSet<Point>();
		HashSet<Point> D = new HashSet<Point>();
		Map<Double, Pair<Point, Point>> P = new TreeMap<Double, Pair<Point, Point>>();
		HashSet<Point> processed = new HashSet<Point>();
		Set<Double> keys;
		ArrayList<Double> keyList = new ArrayList<Double>();
	
		// initialize S with q's nearest neighbor set
		getNearestNeighborSet(S, query, root);
		getRingCandidate(S, R, query, root, 2);
		
		double cost = Point.type2Cost(S, query.getLoc());
		double minDist; Point o;
		while (true) {
			// Step 1 (Query Distance Owner Finding)
			minDist = Double.MAX_VALUE;	o = null;
			for (Point obj : R) {
				if (!processed.contains(obj) && minDist > Point.dist(query.getLoc(), obj)) {
					minDist = Point.dist(query.getLoc(), obj);
					o 		= obj;
				}
			}
			if (o == null) break;	// no "un-processed" relevant object
			// System.out.printf("%f %f\n", o.x, o.y);
			
			// Step 2 (Pairwise Distance Owner Finding)
			// D <- the q-disk with its radius equal to d(o,q)
			D.clear();
			P.clear();
			rangeQuery(D, query, query.getLoc(), root, 0, Point.dist(o, query.getLoc()));
			for (Point obj1 : D) {
				for (Point obj2 : D) {
					// P <- a set of all pairs (o1, o2) in D, in ascending order of d(o1, o2)
					 if (Point.dist(obj1, obj2) != 0)
						 P.put(Point.dist(obj1, obj2), new Pair<Point, Point>(obj1, obj2));
				}
			}
			// Step 3 (Sub-optimal Feasible Set Finding)
			keys = P.keySet();
			Pair<Point, Point> o1o2;
			Point o1, o2;
			// ! add binary search
			keyList.clear();
			for (double key : keys) keyList.add(key);
			
			int ll = 0, rr = keyList.size() - 1, mid;
			double key;
			while (ll <= rr) {
				mid = (ll + rr) / 2;
				key = keyList.get(mid);
				o1o2 = P.get(key);
				o1 = o1o2.getFirst();
				o2 = o1o2.getSecond();
				getFeasibleSet(S_, D, o, o1, o2, query, root, 2);
				// System.out.printf("%f %d\n", key, S_.size());
				// System.out.printf("old cost = %f, new cost = %f\n", cost, Point.type2Cost(S_, query.getLoc()));
				if (S_.size() == 0) {
					ll = mid + 1;
				} else {
					if (cost > Point.type2Cost(S_, query.getLoc())) {
						S.clear();	S.addAll(S_);
						cost = Point.type2Cost(S_, query.getLoc());
						getRingCandidate(S, R, query, root, 2);
					}
					rr = mid - 1;
				}
			}
			// Step 4 (Iterative Process)
			processed.add(o);	// mark o as "processed"
		}
		return new Pair<Double, HashSet<Point>>(cost, S);
	}
	
	public Pair<Double, HashSet<Point>> Type2DistAppro(Pattern query, Node root) {
		
		HashSet<Point> S = new HashSet<Point>();
		HashSet<Point> R = new HashSet<Point>();
		HashSet<Point> feasibleSet = new HashSet<Point>();
		HashSet<Point> processed = new HashSet<Point>();
	
		// initialize S with q's nearest neighbor set
		getNearestNeighborSet(S, query, root);
		getRingCandidate(S, R, query, root, 2);
		
		double cost = Point.type2Cost(S, query.getLoc());
		double minDist; Point o;
		while (true) {
			// Step 1 (Query Distance Owner Finding)
			minDist = Double.MAX_VALUE;	o = null;
			for (Point obj : R) {
				if (!processed.contains(obj) && minDist > Point.dist(query.getLoc(), obj)) {
					minDist = Point.dist(query.getLoc(), obj);
					o 		= obj;
				}
			}
			if (o == null) break;	// no "un-processed" relevant object
			processed.add(o);		// mark o as "processed"
			// Step 2 (o-Neighborhood Feasible Set Finding)
			boolean cover = true;
			Point minDistObj;
			feasibleSet.clear();
			for (String keyword : query.getKeyword()) {
				minDistObj	= getNearestNeighbor(o, root, keyword);
				if (minDistObj == null) {
					cover = false; break;
				} else feasibleSet.add(minDistObj);
			}
			if (!cover) continue;
			if (Point.type2Cost(feasibleSet, query.getLoc()) < cost) {
				S.clear();	S.addAll(feasibleSet);
				cost = Point.type2Cost(feasibleSet, query.getLoc());
				getRingCandidate(S, R, query, root, 2);
			}
		}
		return new Pair<Double, HashSet<Point>>(cost, S);
	}
	
	/**	 
	 * Similar algorithm to MaxSumExact for type3 cost - Diameter
	 * (1) (Pairwise Distance Owner Finding): Select two objects, o' and o'', in Oq U {oq} to take the roles of the 
	 * 		pair wise distance owners of the set S' U {oq} where S' is to be found. Note that o' and o'' form a distance 
	 * 		owner group.
	 * (2) (Sub-Optimal Feasible Set Finding): Find a set S' of objects in Oq such that the pair wise distance owners 
	 * 		of S' U {oq} are o' and o'' (if any), and update S with S' if cost(S') < cost(S)
	 * (3) (Iterative Step): Repeat Step 1 which finds another distance group, and continue with Step 2 until all 
	 * 		distance owner groups have been traversed.
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type3DistExact(Pattern query, Node root) {
		
		HashSet<Point> S = new HashSet<Point>();
		HashSet<Point> S_ = new HashSet<Point>();
		HashSet<Point> R = new HashSet<Point>();
		HashSet<Point> D = new HashSet<Point>();
		Map<Double, Pair<Point, Point>> P = new TreeMap<Double, Pair<Point, Point>>();
		HashSet<Point> processed = new HashSet<Point>();
		Set<Double> keys;
		ArrayList<Double> keyList = new ArrayList<Double>();
		
		// initialize S with q's nearest neighbor set
		getNearestNeighborSet(S, query, root);
		getRingCandidate(S, R, query, root, 3);
		
		double cost = Point.type3Cost(S, query.getLoc());
		double minDist; Point o;
		while (true) {
			// Step 1(a) (Query Distance Owner Finding)
			minDist = Double.MAX_VALUE;	o = null;
			for (Point obj : R) {
				if (!processed.contains(obj) && minDist > Point.dist(query.getLoc(), obj)) {
					minDist = Point.dist(query.getLoc(), obj);
					o 		= obj;
				}
			}
			if (o == null) break;	// no "un-processed" relevant object
			// Step 1(b) (Pairwise Distance Owner Finding)
			// D <- the q-disk with its radius equal to d(o,q)
			rangeQuery(D, query, query.getLoc(), root, 0, Point.dist(o, query.getLoc()));
			for (Point obj1 : D) {
				for (Point obj2 : D)
					// P <- a set of all pairs (o1, o2) in D, in ascending order of d(o1, o2)
					P.put(Point.dist(obj1, obj2), new Pair<Point, Point>(obj1, obj2));
			}
			
			keys = P.keySet();
			Pair<Point, Point> o1o2;
			Point o1, o2, o_1, o_2;
			// ! add binary search
			keyList.clear();
			for (double key : keys) keyList.add(key);
			
			int ll = 0, rr = keyList.size() - 1, mid;
			double key;
			while (ll <= rr) {
				mid = (ll + rr) / 2;
				key = keyList.get(mid);
				o1o2 = P.get(key);
				o1 = o1o2.getFirst();
				o2 = o1o2.getSecond();
				// Step 1(c) (Pairwise Distance Owner Determination)
				if (Point.dist(o, query.getLoc()) >= Point.dist(o1o2.getFirst(), o1o2.getSecond())) {
					o_1 = o; o_2 = query.getLoc();
				} else {
					o_1 = o1; o_2 = o2;
				}
				// Step 2 (Sub-optimal Feasible Set Finding)
				getFeasibleSet(S_, D, o, o_1, o_2, query, root, 3);
				if (S_.size() == 0) {
					ll = mid + 1;
				} else {
					if (cost > Point.type3Cost(S_, query.getLoc())) {
						S.clear();	S.addAll(S_);
						cost = Point.type3Cost(S_, query.getLoc());
						getRingCandidate(S, R, query, root, 3);
					}
					rr = mid - 1;
				}
			}
			// Step 4 (Iterative Process)
			processed.add(o);	// mark o as "processed"
		}
		return new Pair<Double, HashSet<Point>>(cost, S);
	}
	
public Pair<Double, HashSet<Point>> Type3DistAppro(Pattern query, Node root) {
		
		HashSet<Point> S = new HashSet<Point>();
		HashSet<Point> R = new HashSet<Point>();
		HashSet<Point> feasibleSet = new HashSet<Point>();
		HashSet<Point> processed = new HashSet<Point>();
	
		// initialize S with q's nearest neighbor set
		getNearestNeighborSet(S, query, root);
		getRingCandidate(S, R, query, root, 3);
		
		double cost = Point.type3Cost(S, query.getLoc());
		double minDist; Point o;
		while (true) {
			// Step 1 (Query Distance Owner Finding)
			minDist = Double.MAX_VALUE;	o = null;
			for (Point obj : R) {
				if (!processed.contains(obj) && minDist > Point.dist(query.getLoc(), obj)) {
					minDist = Point.dist(query.getLoc(), obj);
					o 		= obj;
				}
			}
			if (o == null) break;	// no "un-processed" relevant object
			processed.add(o);		// mark o as "processed"
			// Step 2 (o-Neighborhood Feasible Set Finding)
			boolean cover = true;
			Point minDistObj;
			feasibleSet.clear();
			for (String keyword : query.getKeyword()) {
				minDistObj	= getNearestNeighbor(o, root, keyword);
				if (minDistObj == null) {
					cover = false; break;
				} else feasibleSet.add(minDistObj);
			}
			if (!cover) continue;
			if (Point.type3Cost(feasibleSet, query.getLoc()) < cost) {
				S.clear();	S.addAll(feasibleSet);
				cost = Point.type3Cost(feasibleSet, query.getLoc());
				getRingCandidate(S, R, query, root, 3);
			}
		}
		return new Pair<Double, HashSet<Point>>(cost, S);
	}
	
	private void getRingCandidate(HashSet<Point> S, HashSet<Point> R, Pattern query, Node root, int type) {
		double rmin = 0, rmax;
		if (type == 2)
			rmax = Point.type2Cost(S, query.getLoc());
		else
			rmax = Point.type3Cost(S, query.getLoc());
		for (Point obj : S) {
			if (rmin < Point.dist(query.getLoc(), obj))
				rmin = Point.dist(query.getLoc(), obj);
		}
		R.clear();
		rangeQuery(R, query, query.getLoc(), root, rmin, rmax);
		// System.out.printf("rmin = %f, rmax = %f, R.size = %d\n", rmin, rmax, R.size());
	}
	
	private void getFeasibleSet(HashSet<Point> S, HashSet<Point> D, Point o, Point o1, Point o2, Pattern query, Node root, int type) {
		if (S == null) S = new HashSet<Point>();
		else S.clear();
		if (Point.dist(o1, o2) < Double.max(Point.dist(o1, o), Point.dist(o2, o))) return;
		// initialize
		HashSet<Point> R = new HashSet<Point>();
		
		S.add(o); S.add(o1); S.add(o2);
		HashSet<String> keywords = new HashSet<String>();
		keywords.addAll(query.getKeyword());
		for (String kwd : o.keywords) 	keywords.remove(kwd);
		for (String kwd : o1.keywords) 	keywords.remove(kwd);
		for (String kwd : o2.keywords) 	keywords.remove(kwd);
		if (keywords.isEmpty()) return;
		
		// R <- D(q, d(o, q)) n D(o1, d(o1, o2)) n D(o2, d(o1, o2))
		double doq 		= Point.dist(o, query.getLoc());
		double do1o2 	= Point.dist(o1, o2);
		HashSet<Point> Dq = new HashSet<Point>();
		HashSet<Point> Do1 = new HashSet<Point>();
		HashSet<Point> Do2 = new HashSet<Point>();
		rangeQuery(Dq, 	query, query.getLoc(), root, 0, doq);
		rangeQuery(Do1, query, o1, root, 0, do1o2);
		rangeQuery(Do2, query, o2, root, 0, do1o2);
		for (Point oDq : Dq) {
			if (Do1.contains(oDq) && Do2.contains(oDq)) R.add(oDq);
		}
		// System.out.printf("R size in getFeasible List = %d\n", R.size());
		
		// Construct inverted list for R
		HashMap<String, HashSet<Point>> invertedList = new HashMap<String, HashSet<Point>>();
		boolean cover;
		for (String keyword : keywords) {
			cover = false;
			invertedList.put(keyword, new HashSet<Point>());
			for (Point obj : R) {
				if (obj.keywords.contains(keyword)) {
					invertedList.get(keyword).add(obj);
					cover = true;
				}
			}
			if (!cover) {
				S.clear(); return;
			}
		}
		// depth-first search to go through each subset
		ArrayList<String> orderedKeywords = new ArrayList<String>();
		orderedKeywords.addAll(keywords);
		
		HashSet<Point> S_ = new HashSet<Point>();
		S_.addAll(S);
		
		S.clear();
		dfsKeywords(0, S_, orderedKeywords, invertedList, doq, do1o2, query.getLoc(), S);
		return;
	}
	
	private void dfsKeywords(int now, HashSet<Point> tmpList, ArrayList<String> keys, HashMap<String, HashSet<Point>> invertedList,
							double doq, double do1o2, Point query, HashSet<Point> S) {
		if (!S.isEmpty()) return;
		if (now == keys.size()) {
			boolean isOwnerConsistent = true;
			for (Point obj1 : tmpList) {
				if (Point.dist(query, obj1) > doq) {
					isOwnerConsistent = false; break;
				}
				for (Point obj2 : tmpList) {
					if (Point.dist(obj1, obj2) > do1o2) {
						isOwnerConsistent = false; break;
					}
				} if (!isOwnerConsistent) break;
			}
			if (isOwnerConsistent) {
				S.clear(); S.addAll(tmpList); return;
			} else return;
		} else {
			for (Point obj : invertedList.get(keys.get(now))) {
				if (!tmpList.contains(obj)) {
					tmpList.add(obj);
					dfsKeywords(now+1, tmpList, keys, invertedList, doq, do1o2, query, S);
					tmpList.remove(obj);
				} else dfsKeywords(now+1, tmpList, keys, invertedList, doq, do1o2, query, S);
			}
		}
	}
	
	/**
	 * For the NN query, we adopt the best first search method. 
	 * Store Nearest Neighbor Set to S for all keywords in query.
	 */
	private void getNearestNeighborSet(HashSet<Point> S, Pattern query, Node root) {
		PriorityQueue<Entry> queue = new PriorityQueue<Entry>(new EntryComparator());
		Point nearestNeighbor = null;
		for (String keyword : query.getKeyword()) {
			queue.clear();
			queue.add(new Entry(root, null, 0));
			nearestNeighbor = null;
			while (!queue.isEmpty()) {
				Entry e = queue.poll();
				if (e.isObject) {
					nearestNeighbor = e.obj; break;
				} else if (e.irTree instanceof Leaf) {
					if (((Leaf) e.irTree).getInvertMap().get(keyword) != null) {
						for (Point obj : (ArrayList<Point>)((Leaf) e.irTree).getInvertMap().get(keyword)) 
							queue.add(new Entry(null, obj, Point.dist(obj, query.getLoc())));
					}
				} else {
					if (((Node) e.irTree).getInvertMap().get(keyword) != null) {
						for (ANode node : (ArrayList<ANode>)((Node) e.irTree).getInvertMap().get(keyword))
							queue.add(new Entry(node, null, Point.minDist(query.getLoc(), node.getMbr())));
					}
				}
			}
			if (nearestNeighbor == null) {
				S = null; return;
			} else S.add(nearestNeighbor);
		}
	}
	
	private Point getNearestNeighbor(Point center, Node root, String keyword) {
		PriorityQueue<Entry> queue = new PriorityQueue<Entry>(new EntryComparator());
		queue.add(new Entry(root, null, 0));
		while (!queue.isEmpty()) {
			Entry e = queue.poll();
			if (e.isObject) {
				return e.obj;
			} else if (e.irTree instanceof Leaf) {
				if (((Leaf) e.irTree).getInvertMap().get(keyword) != null) {
					for (Point obj : (ArrayList<Point>)((Leaf) e.irTree).getInvertMap().get(keyword)) 
						queue.add(new Entry(null, obj, Point.dist(obj, center)));
				}
			} else {
				if (((Node) e.irTree).getInvertMap().get(keyword) != null) {
					for (ANode node : (ArrayList<ANode>)((Node) e.irTree).getInvertMap().get(keyword))
						queue.add(new Entry(node, null, Point.minDist(center, node.getMbr())));
				}
			}
		}
		return null;
	}
	
	/**
	 * For range queries, we adopt the breath first search method. 
	 * Neighbors with dist between lb and ub will be stored to S for all keywords in query.
	 */
	private void rangeQuery(HashSet<Point> S, Pattern query, Point center, Node root, double lb, double ub) {
		S.clear();
		LinkedList<Entry> queue = new LinkedList<Entry>();
		double minDist, maxDist, dist;
		for (String keyword : query.getKeyword()) {
			queue.clear();
			queue.add(new Entry(root, null, 0));
			while (!queue.isEmpty()) {
				Entry e = queue.poll();
				if (e.isObject) {
					S.add(e.obj);
				} else if (e.irTree instanceof Leaf) {
					if (((Leaf) e.irTree).getInvertMap().get(keyword) != null) {
						for (Point obj : (ArrayList<Point>)((Leaf) e.irTree).getInvertMap().get(keyword)) {
							dist = Point.dist(obj, center);
							minDist = Point.minDist(center, e.irTree.getMbr());
							maxDist = Point.maxDist(center, e.irTree.getMbr());
							if (lb <= dist && dist <= ub) {
								queue.add(new Entry(null, obj, 0));
								// System.out.printf("new Object %f %f %f\n", dist, minDist, maxDist);
							} else {
								// System.out.printf("invalid Object %f %f %f\n", dist, minDist, maxDist);
							}
						}
					}
				} else {
					if (((Node) e.irTree).getInvertMap().get(keyword) != null) {
						for (ANode node : (ArrayList<ANode>)((Node) e.irTree).getInvertMap().get(keyword)) {
							minDist = Point.minDist(center, node.getMbr());
							maxDist = Point.maxDist(center, node.getMbr());
							if (maxDist < lb || minDist > ub) continue;
							queue.add(new Entry(node, null, 0));
							// System.out.printf("new Node %f %f\n", minDist, maxDist);
						}
					}
				}
			}
		}
	}
}
