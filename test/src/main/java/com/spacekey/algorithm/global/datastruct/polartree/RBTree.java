package com.spacekey.algorithm.global.datastruct.polartree;

import java.util.ArrayList;

import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.Range;
import com.spacekey.algorithm.global.typedef.STObject;
import com.spacekey.algorithm.global.util.Bitmap;
import com.spacekey.algorithm.global.util.PairObject;
import com.spacekey.algorithm.global.util.Util;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class RBTree
 * @date 2015-09-03
 *
 */
public class RBTree {
	public static final int RED = 0;
	public static final int BLACK = 1;
	
	public static Words W;
	public static Bitmap polebmp;
	private RBNode root;
	private int height = -1;
	private STObject pole; // origin of the polar-tree
	private ArrayList<STObject> objsAtSameLoc;
	
	public static StringBuffer status = new StringBuffer("");
	
	public RBTree(Words words, STObject o) {
		root = new RBNode(null, BLACK);
		pole = o;
		W = words;
		polebmp = W.getBitmap(pole.text);
		objsAtSameLoc = new ArrayList<STObject>();
	}
	
	public Boolean insert(STObject obj) {
		double dist = obj.loc.distance(pole.loc);
		if (dist == 0) objsAtSameLoc.add(obj); 
		
		RBItem item = new RBItem(Util.getAngle(pole.loc, obj.loc), obj, dist);
		return insert(item);
	}
	
	public Boolean insert(ArrayList<STObject> objs) {
		Boolean isFinCovering = null;
		for (STObject obj: objs) {
			double dist = obj.loc.distance(pole.loc);
			if (dist == 0) objsAtSameLoc.add(obj);
			
			RBItem item = new RBItem(Util.getAngle(pole.loc, obj.loc), obj, dist);
			Boolean isCovering = insert(item);
			if (isCovering != null) {
				if (isFinCovering == null) isFinCovering = isCovering;
				else isFinCovering |= isCovering;
			}
		}
		return isFinCovering;
	}

	/**
	 * @param item, to be inserted
	 */
	public Boolean insert(RBItem item) {
		status.setLength(0);
		PairObject<RBNode, Boolean> res = searchPosition( item ); // find the position to be inserted in the tree
		RBNode K = res.p1;
		Boolean covers = res.p2;
		
		K.item = item;
		K.leftChild = new RBNode( K, BLACK );
		K.rightChild = new RBNode( K, BLACK );
		
		K = fixRBTree( K );
		if ( K.parent == null )	root = K; // new root
		
		return covers;
	}

	
	/**
	 * @param K, the node retrieved
	 * @return the node where the fixing process is completed
	 */
	public RBNode fixRBTree( RBNode K )
	{
		try {
			if ( K.parent == null ) // if it is root
			{
				K.setColor( BLACK );			
				return K;
			}
			else if ( K.parent.color == BLACK )
			{
				K.setColor( RED );
				return K;
			}
			else 
			{
				K.setColor( RED );
				if ( K.parent.getSibling().color == BLACK )
					return reStructure(K);
				else if ( K.parent.getSibling().color == RED )
					return fixRBTree( reColor(K) );
				else
					return K;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( " key value when exception  : " + K.item.getKey());
			System.out.println( " status  : " + status);
			return null;
		}
	}
	
	/**
	 * reassign colors to 3 relevant nodes (current node, parent node, parent of parent node)
	 * @return the highest level node among 3 nodes
	 */
	public RBNode reColor(RBNode node)
	{
		node.parent.setColor( BLACK );
		node.parent.getSibling().setColor( BLACK );
		node.parent.parent.setColor( RED );
		
		//System.out.println( " --> reColor() return position : " + this.parent.parent.toString() );
		status.setLength( status.length()-2 );
		return node.parent.parent;
	}
	
	/**
	 * restructure 3 relevant nodes (current node, parent node, parent of parent node)
	 * @return the highest level node among 3 nodes after restructuring
	 */
	public RBNode reStructure(RBNode node)
	{
		RBNode L, M, R;
		
		String curState = status.substring(status.length()-2, status.length());
		
		if ( curState.equals( "RR" ))
		{
			L = node.parent.parent;
			M = node.parent;
			R = node;
		}
		else if ( curState.equals( "RL" ))
		{
			L = node.parent.parent;
			M = node;
			R = node.parent;
		}
		else if ( curState.equals( "LL" ))
		{
			L = node;
			M = node.parent;
			R = node.parent.parent;
		}
		else if ( curState.equals( "LR" ))
		{
			L = node.parent;
			M = node;
			R = node.parent.parent;
		}
		else
			L = M = R = null;
		
		if ( !curState.equals( "LL" ))
		{
			L.rightChild = M.leftChild;
			M.leftChild.parent = L;
		}
		if ( !curState.equals( "RR" ))
		{
			R.leftChild = M.rightChild;
			M.rightChild.parent = R;
		}
		
		M.leftChild = L;
		M.rightChild = R;
		
		if ( curState.equals( "RL" ) || curState.equals( "RR" ) )
			M.parent = L.parent;
		else
			M.parent = R.parent;
			
		if ( M.parent != null )
		{
			String preState = status.substring(status.length()-3, status.length()-2);
			if ( preState.equals("R"))
				M.parent.rightChild = M;
			else
				M.parent.leftChild = M;
		}
		L.parent = M;
		R.parent = M;
		
		L.setColor( RED );
		M.setColor( BLACK );
		R.setColor( RED );
		
		// must be performed in this order L -> R -> M
		L.updateItem();
		R.updateItem();
		M.updateItem();
		
		return M;		
	}
	
	/**
	 * find the position (external node) for a new item being inserted
	 * @param key, key value to be searched for
	 * @return
	 */
	public PairObject<RBNode, Boolean> searchPosition( RBItem newItem )
	{
		RBNode next = root;
		Boolean covers = null;
		
		Range superR = new Range(newItem.ang - 90, newItem.ang + 90);
		Range subR = new Range(newItem.ang - 60, newItem.ang + 60);
		if (root.item == null) {
			Bitmap tmp = new Bitmap(polebmp);
			tmp.or(newItem.objbmp);
			covers = tmp.isAllSet();
		}
		if (root.item != null) {
			if (!root.item.rng.intersect(superR) || !root.item.coversWith(newItem))
				covers = false;
		}
		
		while (next != null) {
			if (next.item == null)
				break;
			else {
				if (subR.covers(next.item.rng) && next.item.coversWith(newItem))
					covers = true;
				else if (next.item.rng.covers(superR) && !next.item.coversWith(newItem))
					covers = false;
					
				next.item.update(newItem); // update after covering-check

				if (next.rightChild != null && newItem.getKey() >= next.item.getKey()) {
					status.append("R");
					next = next.rightChild;
				} else if (next.leftChild != null && newItem.getKey() < next.item.getKey()) {
					status.append("L");
					next = next.leftChild;
				}
			}
		}
		
		return new PairObject<RBNode, Boolean>(next, covers);
		
	}
	
	public void _search(RBNode node, double theta, Range rng, double dist, Group g) {
//		System.out.println("Range Search with " + rng + " at " + node);
		RBItem item = node.item;
		if (item == null) return;
		if (!item.rng.intersect(rng)) return;
		
		if (rng.l <= rng.h) {
			if (rng.l <= item.getKey()) _search(node.leftChild, theta, rng, dist, g);
			if (rng.covers(item.getKey())) {
				double between = Util.rotateCCW(theta, 90);
				between = Util.angleDiff(between, item.ang);
				between = Math.toRadians(between);
				if (2*dist*Math.sin(between) >= item.dist)
					g.add(item.obj);
			}
			if (rng.h >= item.getKey()) _search(node.rightChild, theta, rng, dist, g);
		} 
	}
	public Group rangeSearch(STObject o) {
		Group g = new Group();
		double theta = Util.getAngle(pole.loc, o.loc);
		Range rng = new Range(theta - 90, theta + 90);
		double dist = pole.loc.distance(o.loc);
		if (rng.l > rng.h) {
			Range rng1 = new Range(rng.l, 360);
			Range rng2 = new Range(0, rng.h);
			_search(root, theta, rng1, dist, g);
			_search(root, theta, rng2, dist, g);
		} else {
			_search(root, theta, rng, dist, g);
		}
		
		if (!rng.covers(0.0)) g.addAll(objsAtSameLoc);
		
		return g;
	}

	public RBItem get(int i) {
		RBNode node = root.getRankNode(i);
		if (node != null) return node.item;
		else return null;		
	}
	
	public void printLevel(int level) {
		_printLevel(root, level);
	}
	public void printTree() {
		for (int i=1; i < height(); i++) {
			System.out.println(i+"-th level");
			printLevel(i);
			System.out.println();System.out.println();
		}
	}
	
	private int _height(RBNode node) {
		if (node == null) return 0;
		else {
			int lheight = _height(node.leftChild);
			int rheight = _height(node.rightChild);
			
			int height = Math.max(lheight, rheight);
			return height + 1;
		}
	}
	public int height() {
		if (height < 0) {
			height = _height(root);
		}
		return height;
	}
	private void _printLevel(RBNode node, int level) {
		if (node == null) return;
		if (level == 1) System.out.println(node);
		else if (level > 1) {
			_printLevel(node.leftChild, level-1);
			_printLevel(node.rightChild, level-1);
		}
	}
}
