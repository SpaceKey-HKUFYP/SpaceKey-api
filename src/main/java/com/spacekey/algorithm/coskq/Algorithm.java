package com.spacekey.algorithm.coskq;

import java.util.List;
import java.util.PriorityQueue;

import com.spacekey.algorithm.coskq.index.ANode;
import com.spacekey.algorithm.coskq.index.IRTreeOperator;
import com.spacekey.algorithm.coskq.index.Leaf;
import com.spacekey.algorithm.coskq.index.Node;
import com.spacekey.algorithm.coskq.pattern.Entry;
import com.spacekey.algorithm.coskq.pattern.EntryComparator;
import com.spacekey.algorithm.coskq.pattern.Pattern;
import com.spacekey.algorithm.coskq.pattern.Point;
import com.spacekey.algorithm.global.Pair;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * @author wangj
 * @date Aug 23, 2017
 */
public class Algorithm {
	/**
	 * Greedy algorithm for CoSKQ with type 1 cost function.
	 * The algorithm uses a min-priority queue for the best-first search with the cost as the key.
	 * For each partial query, we use the best-first search to find an object that overlaps with 
	 * the query keyword mSet and has the lowest cost. The algorithm computes the cost for a non-object
	 * node and the cost for an object.
	 * @return a pair of result, first is cost, second is the group
	 */      
	public Pair<Double, HashSet<Point>> Type1Greedy(Pattern query, Node root) {
		double cost = 0;
		HashSet<Point> V = new HashSet<Point>();
		
		EntryComparator comparator = new EntryComparator();
		PriorityQueue<Entry> U = new PriorityQueue<Entry>(comparator);
		HashSet<Entry> U_ = new HashSet<Entry>();
		HashSet<String> mSet = new HashSet<String>();	// keeps the keyword set of the current partial query
		HashSet<String> pSet = new HashSet<String>();	// keeps the keyword set of the preceding partial query
		
		U.add(new Entry(root, null, 0));
		mSet.addAll(query.getKeyword());
		pSet.addAll(query.getKeyword());
		
		while (!mSet.isEmpty()) {
			while (!U.isEmpty()) {
				Entry e = U.poll();
				cost = cost + e.key;
				// if e is an object
				if (e.isObject) {
					V.add(e.obj);
					// pSet <- mSet
					pSet.clear();
					pSet.addAll(mSet);
					// mSet <- mSet \ e.kws
					mSet.removeAll(e.obj.keywords);
					
					U_.clear();
					for (Entry e_ : U) {
						if (!e_.isObject) {
							boolean intersect = false;
							for (String keyword : e.obj.keywords) {
								if (IRTreeOperator.hasKeyword(e_.irTree, keyword))
									intersect = true;
							}
							if (intersect) {
								// calculate | e' n mSet | and | e' n pSet |
								int mSetUnion = 0, pSetUnion = 0;
								for (String keyword : mSet) {
									if (IRTreeOperator.hasKeyword(e_.irTree, keyword)) mSetUnion++;
								}
								for (String keyword : pSet) {
									if (IRTreeOperator.hasKeyword(e_.irTree, keyword)) pSetUnion++;
								}
								// reorganize priority queue U using new key values
								if (mSetUnion != 0) {
									e_.key = e_.key * (double) pSetUnion / (double) mSetUnion;
									U_.add(e_);
								}
							}
						} else {
							boolean intersect = false;
							for (String keyword : e.obj.keywords) {
								if (e_.obj.keywords.contains(keyword))
									intersect = true;
							}
							if (intersect) {
								// calculate | e' n mSet | and | e' n pSet |
								int mSetUnion = 0, pSetUnion = 0;
								for (String keyword : mSet) {
									if (e_.obj.keywords.contains(keyword)) mSetUnion++;
								}
								for (String keyword : pSet) {
									if (e_.obj.keywords.contains(keyword)) pSetUnion++;
								}
								// reorganize priority queue U using new key values
								if (mSetUnion != 0) {
									e_.key = e_.key * (double) pSetUnion / (double) mSetUnion;
									U_.add(e_);
								}
							}
						}
					}
					U.clear();
					U.addAll(U_);
					break;
				} else if (e.irTree instanceof Node) {
					List<ANode> childList = ((Node) e.irTree).getChildList();
					for (ANode e_ : childList) {
						int cntIntersect = 0;
						for (String keyword : mSet) {
							if (IRTreeOperator.hasKeyword(e_, keyword))
								cntIntersect++;
						}
						if (cntIntersect > 0) {
							double minDist = IRTreeOperator.traverseMinDist(e_, query.getLoc());
							U.add(new Entry(e_, null, minDist / (double) cntIntersect));
						}
					}
				} else {
					List<Point> objList = ((Leaf) e.irTree).getObjList();
					for (Point o : objList) {
						int cntIntersect = 0;
						for (String keyword : mSet) {
							if (o.keywords.contains(keyword))
								cntIntersect++;
						}
						if (cntIntersect > 0) {
							double dist = Point.dist(o, query.getLoc());
							U.add(new Entry(null, o, dist / (double) cntIntersect));
						}
					}
				}
			}
		}
		return new Pair<Double, HashSet<Point>>(cost, V);
	}
	
	/**
	 * Exact algorithm for CoSKQ with type 1 cost function.
	 * Only supports query keywords less than 15.
	 * This algorithm develops a dynamic programming  algorithm with exponential running time in terms of the
	 * number of query keywords. 
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type1ExactNoIndex(Pattern query, HashSet<Point> objs) {
		// only supports query keywords less than 1
		int n = query.getKeyword().size();
		if (n > 15) return null;
		
		double cost[] = new double[65536];
		double dist[] = new double[65536];
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ArrayList<HashSet<Point>> group = new ArrayList<HashSet<Point>>();
		
		// pre-process the map from keyword to integer
		int id = 0;
		for (String keyword: query.getKeyword()) {
			map.put(keyword, id++);
		}
		// initialization
		for (int i = 1 ; i < (1<<n) ; i++) {
			cost[i] = Double.MAX_VALUE;
			group.add(new HashSet<Point>());
		}
		// initialize the groups with the best object
		HashSet<String> intersect = new HashSet<String>();
		for (Point o : objs) {
			intersect.clear();
			for (String keyword : o.keywords) {
				if (query.getKeyword().contains(keyword))
					intersect.add(keyword);
			}
			if (intersect.size() > 0) {
				dist[o.id] = Point.dist(query.getLoc(), o);
				for (int i=1 ; i < (1<<intersect.size()) ; i++) {
					int j = 0, hashCode = 0;
					// compute the hash code for each subset
					for (String keyword : intersect) {
						if (((i>>(j++)) & 1) == 1)
							hashCode += (1 << map.get(keyword));
					}
					if (cost[hashCode] > dist[o.id]) {
						cost[i] = dist[o.id];
						group.get(i).clear();
						group.get(i).add(o);
					}
				}
			}
		}
		// Processing
		HashSet<Point> S = new HashSet<Point>();
		for (int i = 1 ; i < (1<<n) ; i++) {
			double minValue = Double.MAX_VALUE;
			int bestSplit = 0;
			for (int j = 1 ; j <= i/2 ; j++) {
				if ((j & i) == j) {
					for (Point oi: group.get(i)) {
						if (group.get(j).contains(oi)) S.add(oi);
					}
					double oDist = 0;
					for (Point o: S)
						oDist += dist[o.id];
					double locCost = cost[j] + cost[i-j] - oDist;
					if (minValue > locCost) {
						minValue = locCost;
						bestSplit = j;
					}
				}
			}
			if (cost[i] > minValue) {
				cost[i] = minValue;
				group.get(i).clear();
				group.get(i).addAll(group.get(bestSplit));
				group.get(i).addAll(group.get(i - bestSplit));
			}
		}
		return new Pair<Double, HashSet<Point>>(cost[(1<<n) - 1], group.get((1<<n) - 1));
	}
	
	/**
	 * Exact algorithm for CoSKQ with type 1 cost function with index.
	 * Without index has two drawbacks: 
	 * (1) wastes computation when checking many unnecessary objects that do not contain any query keyword, and 
	 * (2) all the objects whose text descriptions overlap with the query keywords are scanned to obtain the lowest costs for the query  keyword subsets.
	 * To overcome the drawbacks, we utilize the IR-tree that enables us to retrieve only the objects that contain some query keywords while avoiding 
	 * checking the objects containing no query keywords. For the second drawback, we show that it is not always necessary to scan all the objects covering 
	 * part of the query keywords.
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type1ExactWIndex(Pattern query, Node root) {
		// only supports query keywords less than 15
		int n = query.getKeyword().size();
		if (n > 15) return null;
		
		ArrayList<HashSet<String>> markedSet = new ArrayList<HashSet<String>>();
		ArrayList<HashSet<String>> valuedSet = new ArrayList<HashSet<String>>();
		
		// pre-process the map from keyword to integer
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int id = 0;
		for (String keyword: query.getKeyword()) {
			map.put(keyword, id++);
		}
		
		// initialization
		double cost[] = new double[65536];
		ArrayList<HashSet<Point>> group = new ArrayList<HashSet<Point>>();
		for (int i = 0 ; i < (1<<n) ; i++) {
			cost[i] = Double.MAX_VALUE;
			group.add(new HashSet<Point>());
		}
		
		// use a min-priority queue
		PriorityQueue<Entry> U = new PriorityQueue<Entry>(new EntryComparator());
		U.add(new Entry(root, null, 0));
		
		while (!U.isEmpty()) {
			Entry e = U.poll();
			// ks <- q.keywords n e.keywords
			HashSet<String> ks = Util.intersect(query.getKeyword(), e);
			// if ks belongs to markedSet
			if (!Util.belongTo(ks, markedSet, query, map)) {
				// if e is a non-leaf node
				if (!e.isObject && e.irTree instanceof Node) {
					// for each entry e' in node e do
					for (ANode e_ : ((Node) e.irTree).getChildList()) {
						// check if q.keywords n e'.keywords belongs to markedSet
						HashSet<String> intersect = Util.intersect(query.getKeyword(), e_);
						if (!Util.belongTo(intersect, markedSet, query, map)) {
							double minDist = IRTreeOperator.traverseMinDist(e_, query.getLoc());
							U.add(new Entry(e_, null, minDist));
						}
					}
				// if e is a leaf node
				} else if (!e.isObject && e.irTree instanceof Leaf) {
					for (Point o : ((Leaf) e.irTree).getObjList()) {
						// check if q.keywords n o.keywords belongs to markedSet
						HashSet<String> intersect = Util.intersect(query.getKeyword(), o.keywords);
						if (!Util.belongTo(intersect, markedSet, query, map)) 
							U.add(new Entry(null, o, Point.dist(o, query.getLoc())));
					}
				// if e is an object
				} else {
					for (HashSet<String> s : valuedSet) {
						// compute the hash code for each subset
						int i = Util.hash(s, query, map);
						if (cost[i] < Point.dist(query.getLoc(), e.obj)) {
							if (i == (1<<n) - 1)
								return new Pair<Double, HashSet<Point>>( new Double(cost[(1<<n) - 1]), group.get((1<<n) - 1) );
							// valuedSet <- valuedSet \ S, markedSet <- markedSet U S
							Util.remove(s, valuedSet, query, map);
							Util.insert(s, markedSet, query, map);
						}
					}
					ArrayList<HashSet<String>> tempSet = new ArrayList<HashSet<String>>();
					for (int i = 1 ; i < (1<<ks.size()) ; i++) {
						int cnt = 0;
						HashSet<String> ss = new HashSet<String>();
						for (String keyword : ks) {
							if ((( i >> (cnt++) ) & 1) == 1)
								ss.add(keyword);
						}
						int j = Util.hash(ss, query, map);
						// markedSet <- markedSet U S, tempSet <- tempSet U  S
						// if ss belongs to valuedSet then valuedSet <- valuedSet \ ss
						Util.insert(ss, markedSet, query, map);
						Util.insert(ss, tempSet, query, map);
						Util.remove(ss, valuedSet, query, map);
						cost[j] = Point.dist(e.obj, query.getLoc());
						group.get(j).clear();
						group.get(j).add(e.obj);
						if (j == (1<<n) - 1)
							return new Pair<Double, HashSet<Point>>( new Double(cost[(1<<n) - 1]), group.get((1<<n) - 1) );
					}
					for (HashSet<String> ts : tempSet) {
						int j = Util.hash(ts, query, map);
						for (int i=1 ; i<(1<<n) ; i++) {
							if (cost[i] == Double.MAX_VALUE) continue;
							int unionKey = (i | j);
							if (unionKey == i && unionKey == j) continue;
							double d = cost[i] + Point.dist(e.obj, query.getLoc());
							if (cost[unionKey] > d) {
								cost[unionKey] = d;
								group.get(unionKey).clear();
								group.get(unionKey).addAll(group.get(i));
								group.get(unionKey).add(e.obj);
							}
						}
					}
				}
			}
		}
		return new Pair<Double, HashSet<Point>>(cost[(1<<n) - 1], group.get((1<<n) - 1));
	}
	
	/**
	 * Approximation Algorithm 1 for CoSKQ with type 2 cost
	 * Find the nearest object for each keyword ti in q.keywords. The set of all such nearest objects make up the 
	 * result set. Assuming that the data set is indexed using the IR-tree.
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type2Appro1(Pattern query, Node root, HashSet<Point> objs) {
		// only supports query keywords less than 15
		int n = query.getKeyword().size();
		if (n > 15) return null;
		
		// initialize variables
		HashSet<Point> V = new HashSet<Point>();			// result set
		HashSet<String> uSkiSet = new HashSet<String>();	// uncovered keywords
		uSkiSet.addAll(query.getKeyword());
		
		// initialize the priority queue
		EntryComparator comparator = new EntryComparator();
		PriorityQueue<Entry> U = new PriorityQueue<Entry>(comparator);
		U.add(new Entry(root, null, 0));
		
		while (!U.isEmpty()) {
			Entry e = U.poll();
			if (e.isObject) {
				V.add(e.obj);	// add e to result
				for (String keyword : e.obj.keywords) {
					uSkiSet.remove(keyword);
				}
				if (uSkiSet.isEmpty()) break;
			} else {
				if (e.irTree instanceof Node) {
					for (ANode e_ : ((Node) e.irTree).getChildList()) {
						boolean intersect = false;
						for (String keyword : uSkiSet) {
							if (IRTreeOperator.findKws(e_, keyword).size() > 0)
								intersect = true;
						}
						if (intersect) {
							double minDist = IRTreeOperator.traverseMinDist(e_, query.getLoc());
							U.add(new Entry(e_, null, minDist));
						}
					}
				} else {
					for (Point e_ : ((Leaf) e.irTree).getObjList()) {
						boolean intersect = false;
						for (String keyword : uSkiSet) {
							if (e_.keywords.contains(keyword))
								intersect = true;
						}
						if (intersect) {
							double minDist = Point.dist(e_, query.getLoc());
							U.add(new Entry(null, e_, minDist));
						}
					}
				}
			}
		}
		double cost = Point.type2Cost(V, query.getLoc());
		return new Pair<Double, HashSet<Point>>(cost, V);	// results
	}
	
	/**
	 * Approximation Algorithm 2 for CoSKQ with type 2 cost using algorithm 1
	 * Based on Type2Appro1, Type2Appro2 is an algorithm with a better approximation bound.
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type2Appro2(Pattern query, Node root, HashSet<Point> objs) {
		// only supports query keywords less than 15
		int n = query.getKeyword().size();
		if (n > 15) return null;
		
		// initialize results with type 1 approximation
		Pair<Double, HashSet<Point>> appro1Result = Type2Appro1(query, root, objs);
		double costV 		= appro1Result.getFirst();
		HashSet<Point> V 	= appro1Result.getSecond();
		
		// find the farthest point in Type2appro1's result
		Point of = query.getLoc();
		double maxDist = 0;
		for (Point o : V) {
			if (maxDist < Point.dist(o, query.getLoc())) {
				maxDist = Point.dist(o, query.getLoc()); of = o;
			}
		}
		
		// obtain the word only covered by the farthest object in V
		String ts = new String();
		for (String keyword : of.keywords) {
			boolean onlyCoveredByOf = true;
			for (Point o : V) {
				// if o is not the farthest point from q.loc and keyword is covered by it
				if (Point.dist(o, query.getLoc()) != maxDist && o.keywords.contains(keyword))
					onlyCoveredByOf = false;
			}
			if (onlyCoveredByOf) ts = keyword;
		}
		
		// initialize the priority queue
		EntryComparator comparator = new EntryComparator();
		PriorityQueue<Entry> U = new PriorityQueue<Entry>(comparator);
		U.add(new Entry(root, null, 0));
		
		while (!U.isEmpty()) {
			Entry e = U.poll();
			if (!e.isObject) {
				double minDist = IRTreeOperator.traverseMinDist(e.irTree, query.getLoc());
				if (minDist >= costV) break;
				if (e.irTree instanceof Node) {
					for (ANode e_ : ((Node) e.irTree).getChildList()) {
						if (IRTreeOperator.findKws(e_, ts).size() > 0)
							U.add( new Entry(e_, null, IRTreeOperator.traverseMinDist(e_, query.getLoc())) );
					}
				} else {
					for (Point e_ : ((Leaf) e.irTree).getObjList()) {
						if (e_.keywords.contains(ts))
							U.add( new Entry(null, e_, Point.dist(e_, query.getLoc())) );
					}
				}
			} else {
				if (Point.dist(query.getLoc(), e.obj) >= costV) break;
				Pattern qe = new Pattern(e.obj, query.getKeyword());
				Pair<Double, HashSet<Point>> appro1Result_ = Type2Appro1(qe, root, objs);
				double costV_ 		= appro1Result_.getFirst();
				HashSet<Point> V_ 	= appro1Result_.getSecond();
				if (costV_ < costV) {
					costV = costV_; V = V_;
				}
			}
		}
		return new Pair<Double, HashSet<Point>>(costV, V);
	}
	
	/**
	 * Exact Algorithm for type 2 spatial group keyword queries.
	 * We utilize the Type2Appro2 algorithm to first derive an upper bound cost for the best group and then use this cost to bound 
	 * the exhaustive search in the object space. Specifically, we develop several pruning strategies to prune the enumeration space. 
	 * The Algorithm's idea is to perform a best-first search on the IR-Tree to find the covering node sets, with some objects from these nodes 
	 * constituting a group satisfying the keywords requirement of of a query. We process the covering node set with the lowest cost 
	 * to find covering node sets from their child nodes. When we reach a covering node set consisting of leaf nodes, we find a group 
	 * of objects with the lowest cost by performing an exhaustive search.
	 * @return a pair of result, first is cost, second is the group
	 */
	public Pair<Double, HashSet<Point>> Type2Exact(Pattern query, Node root, HashSet<Point> objs) {
		// only supports query keywords less than 15
		int n = query.getKeyword().size();
		if (n > 15) return null;
		
		// initialize results with type 2 approximation
		Pair<Double, HashSet<Point>> appro2Result = Type2Appro2(query, root, objs);
		double costV 		= appro2Result.getFirst();
		HashSet<Point> V 	= appro2Result.getSecond();
		
		// initialize the priority queue
		NodeSetEntryComparator comparator = new NodeSetEntryComparator();
		PriorityQueue<NodeSetEntry> U = new PriorityQueue<NodeSetEntry>(comparator);
		HashSet<ANode> firstNodeSet = new HashSet<ANode>();	// add the root into the first entry
		firstNodeSet.add(root);
		U.add(new NodeSetEntry(firstNodeSet, 0));
		
		while (!U.isEmpty()) {
			NodeSetEntry N = U.poll();
			
			if (IRTreeOperator.minCost(N.nodeSet, query.getLoc()) >= costV) break;
			// check if N contains leaf nodes
			boolean containLeaf = false;
			for (ANode anode : N.nodeSet) {
				if (anode instanceof Leaf) containLeaf = true;
			}
			if (containLeaf) {
				Pair<Double, HashSet<Point>> exhauSearchResult = exhaustiveSearch(query, N.nodeSet, objs);
				double costV_ 		= exhauSearchResult.getFirst();
				HashSet<Point> V_ 	= exhauSearchResult.getSecond();
				if (costV > costV_) {
					costV = costV_; V = V_;
				}
			} else {
				HashSet<HashSet<ANode>> s = enumerateNodeSets(query, N.nodeSet, costV);
				for (HashSet<ANode> ns : s) {
					boolean qCoveredByNs = true;
					for (String keyword : query.getKeyword()) {
						for (ANode node : ns) {
							if (IRTreeOperator.findKws(node, keyword).size() == 0)
								qCoveredByNs = false;
						}
					}
					if (qCoveredByNs) {
						double minCost = IRTreeOperator.minCost(ns, query.getLoc());
						U.add(new NodeSetEntry(ns, minCost));
					}
				}
			}
		}
		
		return new Pair<Double, HashSet<Point>>(costV, V);
	}
	
	/**
	 * Enumerating Node Sets used in Top-Down Search Demonstration
	 */
	private HashSet<HashSet<ANode>> enumerateNodeSets(Pattern query, HashSet<ANode> nodeSet, double cost) {
		
		HashSet<HashSet<ANode>> setList 	= new HashSet<HashSet<ANode>>();
		HashSet<HashSet<ANode>> [] cList 	= new HashSet[nodeSet.size() + 1];
		HashSet<HashSet<ANode>> [][] L 		= new HashSet[nodeSet.size() + 1][query.getKeyword().size() + 1];
		
		int i = 0;
		for (ANode ni : nodeSet) { i++;
			cList[i] 	= new HashSet<HashSet<ANode>>();
			L[i][1] 	= new HashSet<HashSet<ANode>>();	
			for (ANode ci : ((Node) ni).getChildList()) {	// ni is a Node
				if (IRTreeOperator.traverseMinDist(ci, query.getLoc()) >= cost) {
					HashSet<ANode> initSet = new HashSet<ANode>();
					initSet.add(ci);
					L[i][1].add(initSet);
				}
			}
			for (int m = 2 ; m <= query.getKeyword().size() - nodeSet.size() + 1 ; m++) {
				L[i][m] = new HashSet<HashSet<ANode>>();
				for (HashSet<ANode> ns1 : L[i][m-1]) {
					for (HashSet<ANode> ns2 : L[i][m-1]) {
						int cntSame = 0;
						for (ANode node : ns1)
							if (ns2.contains(node)) cntSame++;
						if (cntSame >= m-1) { 
							HashSet<ANode> ns = new HashSet<ANode>();
							ns.addAll(ns1);
							ns.addAll(ns2);
							if (IRTreeOperator.minCost(ns, query.getLoc()) < cost)
								L[i][m].add(ns);
						}
					}
				}
				cList[i].addAll(L[i][m]);
			}
		}
		for (i = 1 ; i <= nodeSet.size() ; i++) {
			for (HashSet<ANode> ns : cList[i]) {
				if (IRTreeOperator.minCost(ns, query.getLoc()) < cost)
					setList.add(ns);
			}
		}
		return setList;
	}
	
	/**
	 * Exaustive Search used in Top-Down Search Demonstration
	 */
	private Pair<Double, HashSet<Point>> exhaustiveSearch(Pattern query, HashSet<ANode> nodeSet, HashSet<Point> objs) {
		double cost = Double.MAX_VALUE;
		HashSet<Point> V 			= new HashSet<Point>();	// result
		HashSet<Point> objList 		= new HashSet<Point>();
		HashSet<Point> searchList 	= new HashSet<Point>();
		
		for (ANode node : nodeSet)
			objList.addAll(((Leaf) node).getObjList());
		for (int i=1 ; i<(1<<objList.size()) ; i++) {
			int j = 0;
			searchList.clear();
			for (Point obj : objList) {
				if (((i>>(j++)) & 1) == 1)
					searchList.add(obj);
			}
			double newCost = Point.type2Cost(searchList, query.getLoc());
			if (cost > newCost) {
				cost = newCost; V = searchList;
			}
		}
		return new Pair<Double, HashSet<Point>>(cost, V);
	}
	
	private class NodeSetEntry {
		public HashSet<ANode> nodeSet;
		public double key;
		
		public NodeSetEntry(HashSet<ANode> node, double key) {
			this.key = key;
			this.nodeSet = node;
		}
	}
	
	private class NodeSetEntryComparator implements Comparator<NodeSetEntry> {
		@Override
		public int compare(NodeSetEntry x, NodeSetEntry y) {
			if (x.key < y.key) return -1;
			if (x.key > y.key) return 1;
			return 0;
		}
	}
}
