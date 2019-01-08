package com.vividsolutions;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.strtree.AbstractNode;
import com.vividsolutions.jts.index.strtree.Boundable;
import com.vividsolutions.jts.index.strtree.ItemBoundable;
import com.vividsolutions.jts.index.strtree.STRtree;

public class STRtreeFangTest {

	public void test(){
		STRtree tree = new STRtree(2);
		
		double loc[][] = {{1, 4}, {2, 1}, {5, 5}, {6, 3}};
		
		for(int i = 0;i < loc.length;i ++){
			Coordinate coordinate = new Coordinate(loc[i][0], loc[i][1], i);
			tree.insert(new Envelope(coordinate), coordinate);
		}
		System.out.println("depth:" + tree.depth());
		
		//queries
		Coordinate c0 = new Coordinate(50, 50);
		List<Coordinate> list = tree.query(new Envelope(c0));
		System.out.println("list.size=" + list.size());
		for(int i = 0;i < list.size();i ++){
			Coordinate rs1 = list.get(i);
			System.out.println("id:" + rs1.id + " coordinate:[" + rs1.x + ", " + rs1.y + "]");
		}
		
		
		//traverse
		AbstractNode root = tree.findRoot();
		traverse(root, 0);
	}
	
	public void traverse(Object root, int level){
		for(int i = 0;i < level;i ++)   System.out.print("-");
		
		
		if(root instanceof AbstractNode){
			AbstractNode rootNode = (AbstractNode)root;
			Boundable bound = (Boundable)rootNode;
			Envelope e = (Envelope)bound.getBounds();
			
			String size = "[(" + e.getMinX() + "," + e.getMinY() + "), (" + e.getMaxX() + "," + e.getMaxY() + ")]";
			System.out.println(size);
			
			for (Iterator i = rootNode.getChildBoundables().iterator(); i.hasNext(); ) {
				Object object = i.next();
				traverse(object, level + 1);
			}
		}else if(root instanceof ItemBoundable){
			ItemBoundable item = (ItemBoundable)root;
			Boundable bound = item;
			Coordinate rs = (Coordinate)item.getItem();
			
			System.out.println("id:" + rs.id + " coordinate:[" + rs.x + ", " + rs.y + "]");
			
//			String size = "[id=" + "(" + e.getMinX() + "," + e.getMinY() + "), (" + e.getMaxX() + "," + e.getMaxY() + ")]";
//			System.out.println(size);
		}
	}
	
	public static void main(String[] args) {
		STRtreeFangTest test = new STRtreeFangTest();
		test.test();
	}

}
