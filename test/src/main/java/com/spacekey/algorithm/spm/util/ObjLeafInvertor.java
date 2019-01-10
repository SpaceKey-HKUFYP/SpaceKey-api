package com.spacekey.algorithm.spm.util;

import java.util.List;

import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.irtree.Leaf;
import com.spacekey.algorithm.spm.irtree.MBR;
import com.spacekey.algorithm.spm.irtree.Node;
import com.spacekey.algorithm.spm.irtree.Point;

/**
 * @author yxfang
 * @date 2016-8-12
 * This is a newly added inverted list: keyword -> leaf-node-MBR
 * Get an inverted list by inputting an IR-tree
 */
public class ObjLeafInvertor {
	private Node root = null;//an IR-tree
	private double loc[][] = null;//the locations of the dataset
	private MBR objMBRArr[] = null;//objId -> leaf-node

	public ObjLeafInvertor(Node root, double loc[][]){
		this.root = root;
		this.loc = loc;
	}
	
	//the public function provided by this class
	public MBR[] buildInvert(){
		this.objMBRArr = new MBR[loc.length];
		
		buildInvertIterative(root);
		
		return objMBRArr;
	}

	//traverse the IR-tree to reach the leaf nodes and build the inverted list
	private void buildInvertIterative(ANode root){
		if(root instanceof Leaf){
			Leaf leaf = (Leaf)root;
			List<Point> objList = leaf.getObjList();
			for(Point point:objList)   objMBRArr[point.id] = leaf.getMbr();
		}else{
			Node node = (Node)root;
			for(ANode anode:node.getChildList()){
				buildInvertIterative(anode);
			}
		}
	}
}
