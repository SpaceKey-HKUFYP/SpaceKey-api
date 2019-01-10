package com.spacekey.algorithm.global.datastruct.brtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.Circle;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.Point;
import com.spacekey.algorithm.global.typedef.STObject;

/**
 * BRTree
 * @author Dong-Wan Choi
 * 2015. 8. 27.
 */
public class BRTree {
	public static final int M = (int) Math.floor(Env.B/BEntry.Size);
	public static final int m = (int) Math.floor(M * 0.5);
	public static Words W;
	public BNode R=null;
	public int nodeCount = 0;
	public int leafCount = 0;
	public int height = 0;
	public int nodes = 0;
	
	public BRTree(Words words) {
		if (R==null){
			BNode n = new BNode(true);
			nodes++;
			R = n;
			height++;
		}
		BRTree.W = words; // This static value is somewhat not good, but I had no time....
	}

	/* Search */
	private void _search(BNode T, double xl, double xh, double yl, double yh, ArrayList<STObject> result){
		BEntry e;

		for (int a=0; a<T.size(); a++){
			e = T.get(a);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				if (T.isleaf && e instanceof BLEntry){ // leaf
					result.add(((BLEntry)e).obj);
				}
				else _search(e.child, xl, xh, yl, yh, result);
			} 
		}
	}
	public ArrayList<STObject> rangeSearch(double xl, double xh, double yl, double yh){
		ArrayList<STObject> result = new ArrayList<STObject>(); 
		_search(R, xl, xh, yl, yh, result);

		return result;
	}
	
	private void _cirSearch(BNode T, double xl, double xh, double yl, double yh, Circle c, Group result){
		BEntry e;

		for (int i=0; i<T.size(); i++){
			e = T.get(i);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				if (T.isleaf && e instanceof BLEntry){ // leaf
					STObject o = ((BLEntry)e).obj;
					if (c.contain(o.loc) <= 0) result.add(o);
				}
				else _cirSearch(e.child, xl, xh, yl, yh, c, result);
			} 
		}
	}
	public Group cirRangeSearch(Circle c) {
		Group result = new Group();
		double xl = c.center().x - c.radius();
		double yl = c.center().y - c.radius();
		double xh = c.center().x + c.radius();
		double yh = c.center().y + c.radius();
		
		_cirSearch(R, xl, xh, yl, yh, c, result);
		return result;		
	}

	
	public STObject nextNN(Point q, String t, Words w, PriorityQueue<BEntry> pq) {
		if (pq == null) {
			pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				BEntry e = R.get(i);
				if (e.contains(t, w)) {
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		BEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof BLEntry) {// point
				if (next.contains(t, w))
					break;
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					BEntry e = next.child.get(i);
					if (e.contains(t, w)) {
						e.dist = e.distTo(q);
						pq.add(e);
					}
				}
			}
		}
		return ((BLEntry)next).obj;
	}
	public ArrayList<STObject> kNNSearch(Point q, String t, Words w, int k) {
		ArrayList<STObject> knns = new ArrayList<STObject>();
		PriorityQueue<BEntry> pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			BEntry e = R.get(i);
			if (e.contains(t, w)) {
				e.dist = e.distTo(q);
				pq.add(e);
			}
		}
		for (int i=0; i < k; i++){
			knns.add(nextNN(q, t, w, pq));
		}
		return knns;
	}
	
	public STObject nextNN(Point q, HashSet<String> T, Words w, PriorityQueue<BEntry> pq) {
		if (pq == null) {
			pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				BEntry e = R.get(i);
				if (e.intersect(T, w)) {
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		BEntry next;
		while ((next=pq.poll()) != null && !T.isEmpty())
		{
			if (next instanceof BLEntry) { // point
				if (next.intersect(T, w)) {
					T.removeAll(((BLEntry)next).obj.text);
					break; // return this point
				}
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					BEntry e = next.child.get(i);
					if (e.intersect(T, w)) {
						e.dist = e.distTo(q);
						pq.add(e);
					}
				}
			}
		}
		return ((BLEntry)next).obj;
	}
	public ArrayList<STObject> textNNSearch(Point q, HashSet<String> T, Words w) {
		HashSet<String> Tmp = new HashSet<String>(T);
		ArrayList<STObject> nns = new ArrayList<STObject>();
		PriorityQueue<BEntry> pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			BEntry e = R.get(i);
			if (e.intersect(Tmp, w)) {
				e.dist = e.distTo(q);
				pq.add(e);
			}
		}
		while (!Tmp.isEmpty()) {
			nns.add(nextNN(q, Tmp, w, pq));
		}
		return nns;
	}

	public STObject nextNN(Point q, PriorityQueue<BEntry> pq) {
		if (pq == null) {

			pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				BEntry e = R.get(i);
				e.dist = e.distTo(q);
				pq.add(e);
			}
		}
		BEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof BLEntry) // point
				break;
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					BEntry e = next.child.get(i);
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		return ((BLEntry)next).obj;
	}
	public ArrayList<STObject> kNNSearch(Point q, int k) {
		ArrayList<STObject> knns = new ArrayList<STObject>();

		PriorityQueue<BEntry> pq = new PriorityQueue<BEntry>(11, BEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			BEntry e = R.get(i);
			e.dist = e.distTo(q);
			pq.add(e);
		}
		for (int i=0; i < k; i++){
			knns.add(nextNN(q, pq));
		}
		return knns;
	}

	/* Insert */
	private BNode chooseLeaf(BEntry e){
		BNode n = R;
		while (!n.isleaf){
			n.updateBitmap((BLEntry)e, BRTree.W);
			double mincost = Double.MAX_VALUE;
			double s = -1, d;
			BEntry se = null;
			if (n.get(0).child.isleaf) { // the childpointers in N point to leaves
				for (int i=0; i < n.size(); i++)
				{
					BEntry k = n.get(i);
					double cost = k.overlap(e);
					if (mincost > cost) {
						mincost = cost;
						se = k;
					} else if (mincost == cost) {
						d = k.diffArea(e);
						if (s < 0 || s > d) {
							s = d;
							se = k;
						}
						else if (s == d) {
							if (se.area() > k.area())
								se = k;
						}
					}
				}
			} else {
				for (int i=0; i < n.size(); i++)
				{
					BEntry k = n.get(i);
					d = k.diffArea(e);
					if (s < 0 || s > d) {
						s = d;
						se = k;
					}
					else if (s == d) {
						if (se.area() > k.area())
							se = k;
					}
				}
			}
			n = se.child;
		}
		n.updateBitmap((BLEntry)e, BRTree.W);
		return n;
	}

	private BNode splitNode(BNode n, BEntry e){  // node n's split and inserted entry is e
		BNode nn = new BNode(n.isleaf);
		nodes++;
		nn.parent = n.parent;
		n.add(e);
		ArrayList<BEntry> temp = n.entryList;
		n.initEntries();
		nn.initEntries();

		chooseSplitAxis(temp);
		int k = chooseSplitIndex(temp);

		BEntry t;
		for (int i=0; i < m-1+k; i++) {
			t = temp.get(i);
			n.add(t);
			if (!n.isleaf) t.child.parent = n;
		}
		for (int i=m-1+k; i < M+1; i++) {
			t = temp.get(i);
			nn.add(t);
			if (!n.isleaf) t.child.parent = nn;
		}

		return nn;
	}
	private int chooseSplitIndex(ArrayList<BEntry> entries) {
		int index = -1;
		double min = Double.MAX_VALUE, cost;
		BNode first = new BNode(); BNode second = new BNode();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}

		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			cost = overlap(first, second);
			if (min > cost) {
				index = k;
				min = cost;
			}
		}
		return index;
	}
	private double overlap(BNode n1, BNode n2) {
		double xl, xh, yl, yh;

		xl = Math.max(n1.x.l, n2.x.l);
		xh = Math.min(n1.x.h, n2.x.h);
		yl = Math.max(n1.y.l, n2.y.l);
		yh = Math.min(n1.y.h, n2.y.h);

		if (xl > xh || yl > yh) return 0;
		else return (xh-xl)*(yh-yl);
	}
	private void chooseSplitAxis(ArrayList<BEntry> entries) { // sort entries by the best axis
		double xmargin = 0;
		Collections.sort(entries, BEntry.CompareX);

		BNode first = new BNode(); BNode second = new BNode();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}

		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			xmargin += first.margin() + second.margin();
		}

		double ymargin = 0;
		Collections.sort(entries, BEntry.CompareY);

		first = new BNode(); second = new BNode();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}

		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			ymargin += first.margin() + second.margin();
		}

		if (xmargin < ymargin)
			Collections.sort(entries, BEntry.CompareX);		
	}

	private void adjustTree(BNode l, BNode ll){
		BNode n = l;
		BNode nn = ll;
		while(!n.equals(R)){
			BNode p = n.parent;
			BEntry en = p.find(n);
			en.adjust();
			if (nn!=null){
				BEntry enn = new BEntry();
				enn.child = nn;
				enn.adjust();
				if (p.size() < M) {
					p.add(enn);
					nn = null;
				}
				else nn = splitNode(p, enn);
			}
			n = p;
		}
		if (nn!=null){      // root is split!
			BNode r = new BNode(false);
			nodes++;
			R = r;
			n.parent = R;
			nn.parent = R;
			BEntry e1 = new BEntry();
			BEntry e2 = new BEntry();
			e1.child = n;
			e2.child = nn;
			e1.adjust();
			e2.adjust();
			r.add(e1);
			r.add(e2);	
			height++;
		}
	}
	public void insert(BEntry e){
		BNode l = chooseLeaf(e);
		BNode ll = null;
		if (l.size() < M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l, ll);
	}
	public void insert(STObject o){
		Point p = o.loc;
		BLEntry e = new BLEntry(p.x, p.x, p.y, p.y, o);
		insert(e);
	}

	/* Delete */
	private BNode chooseNode(BEntry e){   /// e is not leaf, 
		BNode n = R;    // at least has root
		int th = 1;
		int h = 1;
		while (!n.isleaf){
			n = n.get(0).child;
			th++;
		}
		n = e.child;
		while (!n.isleaf){
			n = n.get(0).child;
			h++;
		}
		h = th-h;
		n = R;
		while (h!=0){
			double s = -1;
			BNode sn=null;
			for (int a=0; a<n.size(); a++){
				BEntry k = n.get(a);
				if (e.x.h<=k.x.h&&e.x.l>=k.x.l&&e.y.h<=k.y.h&&e.y.l>=k.y.l){
					if ((s==-1)||s>k.area()) {
						s = k.area();
						sn = k.child;
					}
				}
			}
			n = sn;
			h--;
		}
		return n;
	}
	private void hinsert(BEntry e){ // e has a appropriate subtree.   Insert entry
		BNode l = chooseNode(e);
		BNode ll = null;
		if (l.size()<M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l,ll);
	}
	private void _findLeaf(BNode t, int xl, int xh, int yl, int yh, BEntry k){  // assume there exists 1 entry
		for (int a = 0; a<t.size(); a++){
			BEntry b = t.get(a);
			if (t.isleaf){
				if (xl==b.x.l&&xh==b.x.h&&yl==b.y.l&&yh==b.y.h)
					k.child = t;
			}
			else if (xl>=b.x.l&&xh<=b.x.h&&yl>=b.y.l&&yh<=b.y.h) 
				_findLeaf(b.child, xl, xh, yl, yh, k);
		}
	}
	private BNode findLeaf(int xl, int xh, int yl, int yh){
		BEntry result = new BEntry();
		result.child = null;
		_findLeaf(R, xl, xh, yl, yh ,result);
		return result.child;
	}
	private void condenseTree(BNode n){
		BNode p = null;
		BEntry e = null;
		ArrayList<BNode> q = new ArrayList<BNode>();
		while(n!=R){
			p = n.parent;
			e = p.find(n);
			if (n.size()<m){
				p.remove(e);
				q.add(n);
			}else e.adjust();
			n = p;
		}
		while (!q.isEmpty()){
			n = (BNode) q.remove(0);
			for (int a=0; a<n.size(); a++){
				if (n.isleaf) insert(n.get(a));
				else hinsert(n.get(a));
			}
		}
	}
	public void delete(int xl, int xh, int yl, int yh){
		BNode l = findLeaf(xl, xh, yl, yh);
		if (l==null) return;
		for (int a=0; a<l.size(); a++){
			BEntry b = l.get(a);
			if (b.x.l==xl&&b.x.h==xh&&b.y.l==yl&&b.y.h==yh)
				l.remove(a);
		}
		condenseTree(l);
		if (R.size()==1) R = R.get(0).child;
	}
}
