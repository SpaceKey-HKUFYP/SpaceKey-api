package com.spacekey.algorithm.spm.irtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yxfang
 * @date 2016-8-8
 * The leaf node of the IR-tree
 */
public class Leaf extends ANode {
	private int leafId = -1;
	private List<Point> objList = null;
	private Map<String, List<Point>> invertMap = null;//the inverted list
	
	public Leaf(int leafId, MBR mbr, List<Point> objList){
		this.leafId = leafId;
		this.mbr = mbr;
		this.objList = objList;
	}

	public int getLeafId() {
		return leafId;
	}

	public void setLeafId(int leafId) {
		this.leafId = leafId;
	}

	public List<Point> getObjList() {
		return objList;
	}

	public void setObjList(List<Point> objList) {
		this.objList = objList;
	}

	public Map<String, List<Point>> getInvertMap() {
		return invertMap;
	}

	public void setInvertMap(Map<String, List<Point>> invertMap) {
		this.invertMap = invertMap;
	}
}
