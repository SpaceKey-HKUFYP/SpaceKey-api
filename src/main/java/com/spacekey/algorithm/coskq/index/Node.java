package com.spacekey.algorithm.coskq.index;

import java.util.*;

/**
 * @author yxfang
 * @date 2016-8-8
 * The internal node of the IR-tree
 */
public class Node extends ANode {
	private List<ANode> childList = null;//the child list of this node
	private Map<String, List<ANode>> invertMap = null;//the keyword inverted list
	
	public Node(){
		
	}
	
	public Node(MBR mbr){
		this.mbr = mbr;
		this.childList = new ArrayList<ANode>();
	}
	
	public Node(MBR mbr, List<ANode> childList){
		this.mbr = mbr;
		this.childList = childList;
	}
	
	public MBR getMbr() {
		return mbr;
	}

	public void setMbr(MBR mbr) {
		this.mbr = mbr;
	}

	public List<ANode> getChildList() {
		return childList;
	}

	public void setChildList(List<ANode> childList) {
		this.childList = childList;
	}

	public Map<String, List<ANode>> getInvertMap() {
		return invertMap;
	}

	public void setInvertMap(Map<String, List<ANode>> invertMap) {
		this.invertMap = invertMap;
	}
}