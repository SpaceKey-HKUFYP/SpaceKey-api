package com.spacekey.algorithm.spm.irtree;

import java.util.ArrayList;
import java.util.List;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;

/**
 * @author yxfang
 * @date 2016-12-12
 */
public class IRTreeKwQuery {
	private Node root = null;
	
	public IRTreeKwQuery(Node root){
		this.root = root;
	}

	public List<Point> query(String keyword2){
		if(!root.getInvertMap().containsKey(keyword2) || !root.getInvertMap().containsKey(keyword2))   return null;

		List<Point> rsList = new ArrayList<Point>();
		List<ANode> candList = new ArrayList<ANode>();
		candList.add(root);
		
		boolean isStop = false;
		while(!isStop){
			List<ANode> nextCandList = new ArrayList<ANode>();
			for(ANode anode:candList){
				if(anode instanceof Node){//internal nodes
					List<ANode> childList = ((Node)anode).getInvertMap().get(keyword2);
					nextCandList.addAll(childList);
				}else{//leaf nodes
					Leaf leaf = (Leaf)anode;
					List<Point> childList = leaf.getInvertMap().get(keyword2);
					rsList.addAll(childList);
					isStop = true;
				}
			}
			
			candList = nextCandList;
			if(candList.size() == 0)   break;
		}
		
		return rsList;
	}
	
	public static void main(String[] args) {
		//step 1: read data
		String indexFile = Config.indexNY;
		DataReader dataReader = new DataReader(Config.locNY, Config.docNY);
		double loc[][] = dataReader.readLoc();
		String kws[][] = dataReader.readKws();

		//step 2: build index
		BuildIRTree builder = new BuildIRTree(loc, kws, indexFile);
		Node root = builder.build();
		
		IRTreeKwQuery query = new IRTreeKwQuery(root);
		List<Point> list = query.query("8929");
		for(Point point:list)   System.out.println(point.id);
	}

}
