package com.spacekey.algorithm.spm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.irtree.ANode;
import com.spacekey.algorithm.spm.irtree.Leaf;
import com.spacekey.algorithm.spm.irtree.Node;

/**
 * @author yxfang
 * @date 2016-8-12
 * This is a newly added inverted list: keyword -> leaf nodes
 * Get an inverted list by inputting an IR-tree
 */
public class KwLeafInvertor {
	private Node root = null;//an IR-tree
	private Map<String, List<Leaf>> invertLeafMap = null;//the returned inverted list
	private Map<String, Integer> kwFreqMap = null;//the frequency of keywords

	public KwLeafInvertor(Node root){
		this.root = root;
	}
	
	//the public function provided by this class
	public Map<String, List<Leaf>> buildInvert(){
		this.invertLeafMap = new HashMap<String, List<Leaf>>();
		this.kwFreqMap = new HashMap<String, Integer>();
		
		buildInvertIterative(root);
		
		return invertLeafMap;
	}
	
	public Map<String, Integer> buildFreq(){
		return kwFreqMap;
	}
	
	//traverse the IR-tree to reach the leaf nodes and build the inverted list
	private void buildInvertIterative(ANode root){
		if(root instanceof Leaf){
			Leaf leaf = (Leaf)root;
			Set<String> kwsSet = leaf.getInvertMap().keySet();
			for(String keyword:kwsSet){
				if(invertLeafMap.containsKey(keyword)){
					invertLeafMap.get(keyword).add(leaf);
				}else{
					List<Leaf> list = new ArrayList<Leaf>();
					list.add(leaf);
					invertLeafMap.put(keyword, list);
				}
				
				if(kwFreqMap.containsKey(keyword)){
					int old = kwFreqMap.get(keyword);
					kwFreqMap.put(keyword, old + leaf.getInvertMap().get(keyword).size());
				}else{
					kwFreqMap.put(keyword, leaf.getInvertMap().get(keyword).size());
				}
			}
		}else{
			Node node = (Node)root;
			for(ANode anode:node.getChildList()){
				buildInvertIterative(anode);
			}
		}
	}
}
