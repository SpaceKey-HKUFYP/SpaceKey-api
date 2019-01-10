/**
 * 
 */
package com.spacekey.algorithm.global.datastruct.cbrtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.Circle;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.Lune;
import com.spacekey.algorithm.global.typedef.Point;
import com.spacekey.algorithm.global.typedef.STObject;


/**
 * CCBRTree
 * @author Dong-Wan Choi
 * 2015. 9. 2.
 */
public class CBRTree {
	public static final int M = (int) Math.floor(Env.B/CEntry.Size);
	public static final int m = (int) Math.floor(M * 0.5);
	public static Words W;
	public CNode R=null;
	public int nodeCount = 0;
	public int leafCount = 0;
	public int height = 0;
	public int nodes = 0;
	
	public CBRTree(Words words) {
		if (R==null){
			CNode n = new CNode(true);
			nodes++;
			R = n;
			height++;
		}
		CBRTree.W = words; // This static value is somewhat not good, but I had no time....
	}

	/* Search */
	private void _search(CNode T, double xl, double xh, double yl, double yh, ArrayList<STObject> result){
		CEntry e;

		for (int a=0; a<T.size(); a++){
			e = T.get(a);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				if (T.isleaf && e instanceof CLEntry){ // leaf
					result.add(((CLEntry)e).obj);
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
	
	private void _cirSearch(CNode T, double xl, double xh, double yl, double yh, Circle c, Group result){
		CEntry e;

		for (int i=0; i<T.size(); i++){
			e = T.get(i);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				if (T.isleaf && e instanceof CLEntry){ // leaf
					STObject o = ((CLEntry)e).obj;
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
	
	private void _luneSearch(CNode T, double xl, double xh, double yl, double yh, Lune l, Group result){
		CEntry e;

		for (int i=0; i<T.size(); i++){
			e = T.get(i);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				if (T.isleaf && e instanceof CLEntry){ // leaf
					STObject o = ((CLEntry)e).obj;
					if (l.contains(o.loc)) result.add(o);
				}
				else _luneSearch(e.child, xl, xh, yl, yh, l, result);
			} 
		}
	}	
	public Group luneRangeSearch(Lune l) {
		Group result = new Group();
		double mbr[] = l.mbr();
		_luneSearch(R, mbr[0], mbr[1], mbr[2], mbr[3], l, result);
		return result;
	}

	
	public STObject nextNN(Point q, String t, Words w, PriorityQueue<CEntry> pq) {
		if (pq == null) {
			pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				CEntry e = R.get(i);
				if (e.contains(t, w)) {
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		CEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof CLEntry) {// point
				if (next.contains(t, w))
					break;
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
					if (e.contains(t, w)) {
						e.dist = e.distTo(q);
						pq.add(e);
					}
				}
			}
		}
		return ((CLEntry)next).obj;
	}
	public ArrayList<STObject> kNNSearch(Point q, String t, Words w, int k) {
		ArrayList<STObject> knns = new ArrayList<STObject>();
		PriorityQueue<CEntry> pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			CEntry e = R.get(i);
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
	
	private STObject nextNN(Point q, HashSet<String> T, Words w, PriorityQueue<CEntry> pq) {
		if (pq == null) {
			pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				CEntry e = R.get(i);
				if (e.intersect(T, w)) {
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		CEntry next;
		while ((next=pq.poll()) != null && !T.isEmpty())
		{
			if (next instanceof CLEntry) { // point
				if (next.intersect(T, w)) {
					T.removeAll(((CLEntry)next).obj.text);
					break; // return this point
				}
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
					if (e.intersect(T, w)) {
						e.dist = e.distTo(q);
						pq.add(e);
					}
				}
			}
		}
		return ((CLEntry)next).obj;
	}
	public ArrayList<STObject> textNNSearch(Point q, HashSet<String> T, Words w) {
		HashSet<String> Tmp = new HashSet<String>(T);
		ArrayList<STObject> nns = new ArrayList<STObject>();
		PriorityQueue<CEntry> pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			CEntry e = R.get(i);
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
	
	private double incurredDia(CEntry e, Group g) {
		double dist;
		double maxDist = Double.MIN_VALUE;
		for (STObject o: g) {
			dist = e.distTo(o.loc);
			if (dist > maxDist)
				maxDist = dist;
		}
		return maxDist;
	}
	private STObject nextPriceNN(Point q, HashSet<String> T, Words w, PriorityQueue<CEntry> pq, Group g) {
		double dia = g.dia();
		if (pq == null) {
			pq = new PriorityQueue<CEntry>(11, CEntry.ComparePrice);
			for(int i = 0; i < R.size(); i++) {
				CEntry e = R.get(i);
				if (e.intersect(T, w)) {
					e.dist = e.distTo(q) - dia;
//					e.dist = incurredDia(e, g) - dia; // incurred distance
					e.icnt = e.intersectCnt(T, w);
					pq.add(e);
				}
			}
		}
		CEntry next;
		while ((next=pq.poll()) != null && !T.isEmpty())
		{
			if (next instanceof CLEntry) { // point
				if (next.intersect(T, w)) {
					T.removeAll(((CLEntry)next).obj.text);
					
					// update pq
					Object [] entryArr = pq.toArray();
					pq.clear();
					for (Object o: entryArr) {
						CEntry e = (CEntry)o;
						e.dist = e.distTo(q) - dia;
//						e.dist = incurredDia(e, g) - dia; // incurred distance
						e.icnt = e.intersectCnt(T, w);
						pq.add(e);
					}
					
					break; // return this point
				}
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
					if (e.intersect(T, w)) {
						e.dist = e.distTo(q) - dia;
//						e.dist = incurredDia(e, g) - dia; // incurred distance
						e.icnt = e.intersectCnt(T, w);
						pq.add(e);
					}
				}
			}
		}
		return ((CLEntry)next).obj;
	}
	public Group textPriceNNSearch(Point q, HashSet<String> T, Words w) {
		HashSet<String> Tmp = new HashSet<String>(T);
		Group g = new Group();
		PriorityQueue<CEntry> pq = new PriorityQueue<CEntry>(11, CEntry.ComparePrice);
		for(int i = 0; i < R.size(); i++) {
			CEntry e = R.get(i);
			if (e.intersect(Tmp, w)) {
				e.dist = e.distTo(q);
				e.icnt = e.intersectCnt(Tmp, w);
				pq.add(e);
			}
		}
		while (!Tmp.isEmpty()) {
			STObject next = nextPriceNN(q, Tmp, w, pq, g); 
			g.add(next);
			g.updateDia();
		}
		return g;
	}
	
	public STObject nextNN(STObject o, PriorityQueue<CEntry> pq) {
		if (pq == null ) pq = initPQ(o);
		Point q = o.loc;
		CEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof CLEntry) { // point
				if (((CLEntry)next).obj.equals(o)) continue;
				else break;
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
						e.dist = e.distTo(q);
						pq.add(e);
				}
			}
		}
		if (next != null) return ((CLEntry)next).obj;
		else return null;
	}
	
	private CLEntry nextNNEntry(STObject o, PriorityQueue<CEntry> pq) {
		if (pq == null ) pq = initPQ(o);
		Point q = o.loc;
		CEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof CLEntry) { // point
				if (((CLEntry)next).obj.equals(o)) continue;
				else break;
			}
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
						e.dist = e.distTo(q);
						pq.add(e);
				}
			}
		}
		return (CLEntry) next;
	}
	public ArrayList<STObject> nextNNs(STObject o, PriorityQueue<CEntry> pq) {
		ArrayList<STObject> nns = new ArrayList<STObject>();
		STObject nn1 = nextNN(o, pq);
		nns.add(nn1);
		STObject next;
		CLEntry nextEntry;
		double dist = nn1.loc.distance(o.loc);
		
		while ((nextEntry = nextNNEntry(o, pq)) != null) {
			next = nextEntry.obj;
			if (next.loc.distance(o.loc) == dist) {
				nns.add(next);
			} else {
				pq.add(nextEntry);
				break;
			}
		}
		return nns;		
	}

	public PriorityQueue<CEntry> initPQ(STObject o) {
		Point q = o.loc;
		int len = o.text.size();
		PriorityQueue<CEntry> pq;
		pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			CEntry e = R.get(i);
			if (e.minCard() <= len) {
				e.dist = e.distTo(q);
				pq.add(e);
			}
		}
		return pq;
	}

	public STObject nextNN(Point q, PriorityQueue<CEntry> pq) {
		if (pq == null) {
			pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
			for(int i = 0; i < R.size(); i++) {
				CEntry e = R.get(i);
				e.dist = e.distTo(q);
				pq.add(e);
			}
		}
		CEntry next;
		while ((next=pq.poll()) != null)
		{
			if (next instanceof CLEntry) // point
				break;
			else {
				for(int i = 0; i < next.child.size(); i++) { // node
					CEntry e = next.child.get(i);
					e.dist = e.distTo(q);
					pq.add(e);
				}
			}
		}
		return ((CLEntry)next).obj;
	}
	public ArrayList<STObject> kNNSearch(Point q, int k) {
		ArrayList<STObject> knns = new ArrayList<STObject>();

		PriorityQueue<CEntry> pq = new PriorityQueue<CEntry>(11, CEntry.CompareDist);
		for(int i = 0; i < R.size(); i++) {
			CEntry e = R.get(i);
			e.dist = e.distTo(q);
			pq.add(e);
		}
		for (int i=0; i < k; i++){
			knns.add(nextNN(q, pq));
		}
		return knns;
	}

	/* Insert */
	private CNode chooseLeaf(CEntry e){
		CNode n = R;
		while (!n.isleaf){
			n.updateBitmap((CLEntry)e, CBRTree.W);
			n.updateMinMaxC((CLEntry)e);
			double mincost = Double.MAX_VALUE;
			double s = -1, d;
			CEntry se = null;
			if (n.get(0).child.isleaf) { // the childpointers in N point to leaves
				for (int i=0; i < n.size(); i++)
				{
					CEntry k = n.get(i);
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
					CEntry k = n.get(i);
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
		n.updateBitmap((CLEntry)e, CBRTree.W);
		n.updateMinMaxC((CLEntry)e);
		return n;
	}

	private CNode splitNode(CNode n, CEntry e){  // node n's split and inserted entry is e
		CNode nn = new CNode(n.isleaf);
		nodes++;
		nn.parent = n.parent;
		n.add(e);
		ArrayList<CEntry> temp = n.entryList;
		n.initEntries();
		nn.initEntries();

		chooseSplitAxis(temp);
		int k = chooseSplitIndex(temp);

		CEntry t;
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
	private int chooseSplitIndex(ArrayList<CEntry> entries) {
		int index = -1;
		double min = Double.MAX_VALUE, cost;
		CNode first = new CNode(); CNode second = new CNode();
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
	private double overlap(CNode n1, CNode n2) {
		double xl, xh, yl, yh;

		xl = Math.max(n1.x.l, n2.x.l);
		xh = Math.min(n1.x.h, n2.x.h);
		yl = Math.max(n1.y.l, n2.y.l);
		yh = Math.min(n1.y.h, n2.y.h);

		if (xl > xh || yl > yh) return 0;
		else return (xh-xl)*(yh-yl);
	}
	private void chooseSplitAxis(ArrayList<CEntry> entries) { // sort entries by the best axis
		double xmargin = 0;
		Collections.sort(entries, CEntry.CompareX);

		CNode first = new CNode(); CNode second = new CNode();
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
		Collections.sort(entries, CEntry.CompareY);

		first = new CNode(); second = new CNode();
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
			Collections.sort(entries, CEntry.CompareX);		
	}

	private void adjustTree(CNode l, CNode ll){
		CNode n = l;
		CNode nn = ll;
		while(!n.equals(R)){
			CNode p = n.parent;
			CEntry en = p.find(n);
			en.adjust();
			if (nn!=null){
				CEntry enn = new CEntry();
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
			CNode r = new CNode(false);
			nodes++;
			R = r;
			n.parent = R;
			nn.parent = R;
			CEntry e1 = new CEntry();
			CEntry e2 = new CEntry();
			e1.child = n;
			e2.child = nn;
			e1.adjust();
			e2.adjust();
			r.add(e1);
			r.add(e2);	
			height++;
		}
	}
	public void insert(CEntry e){
		CNode l = chooseLeaf(e);
		CNode ll = null;
		if (l.size() < M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l, ll);
	}
	public void insert(STObject o){
		Point p = o.loc;
		CLEntry e = new CLEntry(p.x, p.x, p.y, p.y, o);
		insert(e);
	}

	/* Delete */
	private CNode chooseNode(CEntry e){   /// e is not leaf, 
		CNode n = R;    // at least has root
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
			CNode sn=null;
			for (int a=0; a<n.size(); a++){
				CEntry k = n.get(a);
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
	private void hinsert(CEntry e){ // e has a appropriate subtree.   Insert entry 
		CNode l = chooseNode(e);
		CNode ll = null;
		if (l.size()<M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l,ll);
	}
	private void _findLeaf(CNode t, int xl, int xh, int yl, int yh, CEntry k){  // assume there exists 1 entry
		for (int a = 0; a<t.size(); a++){
			CEntry b = t.get(a);
			if (t.isleaf){
				if (xl==b.x.l&&xh==b.x.h&&yl==b.y.l&&yh==b.y.h)
					k.child = t;
			}
			else if (xl>=b.x.l&&xh<=b.x.h&&yl>=b.y.l&&yh<=b.y.h) 
				_findLeaf(b.child, xl, xh, yl, yh, k);
		}
	}
	private CNode findLeaf(int xl, int xh, int yl, int yh){
		CEntry result = new CEntry();
		result.child = null;
		_findLeaf(R, xl, xh, yl, yh ,result);
		return result.child;
	}
	private void condenseTree(CNode n){
		CNode p = null;
		CEntry e = null;
		ArrayList<CNode> q = new ArrayList<CNode>();
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
			n = (CNode) q.remove(0);
			for (int a=0; a<n.size(); a++){
				if (n.isleaf) insert(n.get(a));
				else hinsert(n.get(a));
			}
		}
	}
	public void delete(int xl, int xh, int yl, int yh){
		CNode l = findLeaf(xl, xh, yl, yh);
		if (l==null) return;
		for (int a=0; a<l.size(); a++){
			CEntry b = l.get(a);
			if (b.x.l==xl&&b.x.h==xh&&b.y.l==yl&&b.y.h==yh)
				l.remove(a);
		}
		condenseTree(l);
		if (R.size()==1) R = R.get(0).child;
	}
}
