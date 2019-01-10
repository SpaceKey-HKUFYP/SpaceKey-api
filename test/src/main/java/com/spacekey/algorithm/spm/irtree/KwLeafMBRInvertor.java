package com.spacekey.algorithm.spm.irtree;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yxfang
 * @date 2016-12-20
 * This is a newly added inverted list: keyword -> leaf nodes
 * Get an inverted list by inputting an IR-tree
 */
public class KwLeafMBRInvertor {
	private Node root = null;//an IR-tree
	private Map<String, List<MBR>> invertLeafMBRMap = null;//the returned inverted list

	public KwLeafMBRInvertor(Node root){
		this.root = root;
	}
	
	//the public function provided by this class
	public Map<String, List<MBR>> buildInvert(){
		this.invertLeafMBRMap = new HashMap<String, List<MBR>>();
		
		buildInvertIterative(root);
		
		return invertLeafMBRMap;
	}
	
	//traverse the IR-tree to reach the leaf nodes and build the inverted list
	private void buildInvertIterative(ANode root){
		if(root instanceof Leaf){
			Leaf leaf = (Leaf)root;
			for(Map.Entry<String, List<Point>> entry:leaf.getInvertMap().entrySet()){
				String word = entry.getKey();
				int num = entry.getValue().size();

				List<MBR> mbrList = new ArrayList<MBR>();
				for(int k = 0;k < num;k ++)   mbrList.add(leaf.getMbr());
				if(invertLeafMBRMap.containsKey(word))   invertLeafMBRMap.get(word).addAll(mbrList);
				else                                     invertLeafMBRMap.put(word, mbrList);
			}
		}else{
			Node node = (Node)root;
			for(ANode anode:node.getChildList()){
				buildInvertIterative(anode);
			}
		}
	}
}
