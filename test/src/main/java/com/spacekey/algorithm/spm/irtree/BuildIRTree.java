package com.spacekey.algorithm.spm.irtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.spacekey.algorithm.global.Config;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.AbstractNode;
import com.vividsolutions.jts.index.strtree.Boundable;
import com.vividsolutions.jts.index.strtree.ItemBoundable;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * @author yxfang
 * @date 2016-8-8
 * Build an IR-tree. Steps:
 * (1) Build an R-tree using API[JTS Topology Suite version 1.12]
 * (2) Build the structure of the IR-tree by traversing the R-tree
 * (3) Build the keyword inverted list of the IR-tree
 */
public class BuildIRTree {
	private double loc[][] = null;
	private String kws[][] = null;
	public static String leafFold = null;
	private int leafId = Integer.MIN_VALUE;
	public static int height = -1;//a static variable
	
	//in-memory version
	public BuildIRTree(double loc[][], String kws[][]){
		this.loc = loc;
		this.kws = kws;
	}
	
	//disk version
	public BuildIRTree(double loc[][], String kws[][], String leafFold){
		this.loc = loc;
		this.kws = kws;
		BuildIRTree.leafFold = leafFold;
		this.leafId = 0;//the leaf id is started from 0
	}
	
	public Node build(){
		//step 1: build an R-tree using API
		AbstractNode rootRtree = buildRtree();
		
		//step 2: build the structure of the IR-tree by traversing the R-tree
		ANode root = buildIRTreeStruct(rootRtree);
		
		//step 3: build the keyword inverted list of the IR-tree
		buildIRTreeInvert(root);
		
		//warming message
		if(root instanceof Leaf){
			System.out.println("BuildIRTree: the root node is a leaf node");
			System.out.println("We ignore the case if the height of the IR-tree is 1");
			System.exit(0);
		}
		System.out.println("IR-tree has been well built");
		
		//step 4: compute the height
		BuildIRTree.height = obtainHeight(root);
		
		return (Node)root;
	}
	
	//step 1: build an R-tree using API
	private AbstractNode buildRtree(){
		STRtree tree = new STRtree(Config.Fanout);
		for(int i = 0;i < loc.length;i ++){
			Coordinate coordinate = new Coordinate(loc[i][0], loc[i][1], i);
			tree.insert(new Envelope(coordinate), coordinate);
		}
		return tree.findRoot();
	}
	
	//step 2: build the structure of the IR-tree by traversing the R-tree
	private ANode buildIRTreeStruct(Object rootRtree){
		AbstractNode rootNode = (AbstractNode) rootRtree;
		Boundable bound = (Boundable) rootNode;
		Envelope e = (Envelope) bound.getBounds();
		MBR mbr = new MBR(e.getMinX(), e.getMinY(), e.getMaxX(), e.getMaxY());
		
		//consider its first child node
		Object firstChild = rootNode.getChildBoundables().get(0);
		if(firstChild instanceof AbstractNode){//handling internal nodes
			List<ANode> childNodeList = new ArrayList<ANode>();
			for (Iterator i = rootNode.getChildBoundables().iterator(); i.hasNext();){//find its child nodes, which are empty nodes
				Object childObject = i.next();
				ANode childNode = buildIRTreeStruct(childObject);
				childNodeList.add(childNode);
			}
			
			Node root = new Node(mbr, childNodeList);
			return root;
		}else if(firstChild instanceof ItemBoundable){//handling leaf nodes
			List<Point> objList = new ArrayList<Point>();
			for (Iterator i = rootNode.getChildBoundables().iterator(); i.hasNext();){//find its child entries, which are spatial objects
				Object object = i.next();
				ItemBoundable item = (ItemBoundable)object;
				Coordinate rs = (Coordinate)item.getItem();
				int id = rs.id;
				double loc[] = {rs.x, rs.y};
				objList.add(new Point(id, loc[0], loc[1]));
			}
			
			Leaf leaf = new Leaf(leafId ++, mbr, objList);
			return leaf;
		}else{
			System.out.println("BuildIRTree.buildIRTreeStruct: something wrong");
			return null;
		}
	}

	//step 3: build the keyword inverted list of the IR-tree
	private void buildIRTreeInvert(ANode rootIRTree){
		if(rootIRTree instanceof Leaf){
			//initialize the inverted list of leaf nodes
			Map<String, List<Point>> invertMap = new HashMap<String, List<Point>>();
			for(Point point : ((Leaf) rootIRTree).getObjList()){
				int id = point.id;
				for(String keyword:kws[id]){
					if(!invertMap.containsKey(keyword))
						invertMap.put(keyword, new ArrayList<Point>());
					invertMap.get(keyword).add(point);
				}
			}
			((Leaf) rootIRTree).setInvertMap(invertMap);
		}else{//initialize the inverted list of internal nodes
			List<ANode> childList = ((Node)rootIRTree).getChildList();
			ANode firstChild = childList.get(0);
			
			Map<String, List<ANode>> invertMap = new HashMap<String, List<ANode>>();
			if(firstChild instanceof Leaf){
				if (((Leaf)firstChild).getInvertMap() == null){
					for (ANode anode:childList)
						buildIRTreeInvert(anode);
				}
				
				for(ANode anode:childList){
					Leaf leaf = (Leaf)anode;
					Map<String, List<Point>> childInvertMap = leaf.getInvertMap();
					for(String keyword:childInvertMap.keySet()){
						if(!invertMap.containsKey(keyword))
							invertMap.put(keyword, new ArrayList<ANode>());
						invertMap.get(keyword).add(anode);
					}
				}
			}else{
				if (((Node)firstChild).getInvertMap() == null){
					for (ANode anode:childList)
						buildIRTreeInvert(anode);
				}
				for(ANode anode:childList){
					Map<String, List<ANode>> childInvertMap = ((Node)anode).getInvertMap();
					for(String keyword:childInvertMap.keySet()){
						if(!invertMap.containsKey(keyword))
							invertMap.put(keyword, new ArrayList<ANode>());
						invertMap.get(keyword).add(anode);
					}
				}
			}
			((Node)rootIRTree).setInvertMap(invertMap);//fit with inverted list
		}
	}

	//step 4: compute the height
	private int obtainHeight(ANode root){
		if(root instanceof Node){
			ANode firstChild = ((Node) root).getChildList().get(0);
			return 1 + obtainHeight(firstChild);
		}else{
			return 1;
		}
	}
}
