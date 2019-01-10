package com.spacekey.algorithm.spm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spacekey.algorithm.spm.algorithm.Box;
import com.spacekey.algorithm.spm.irtree.Point;
import com.spacekey.algorithm.spm.pattern.BoundedPattern;

/**
 * @author yxfang
 * @date 2016-11-23
 * link the tree box
 */
public class LinkBoxOld {
	
	public LinkBoxOld(){}
	
	//reverse the join results
	public Map<Point, List<Point>> reverse(Map<Point, List<Point>> candMap){
		if(candMap == null)   return null;
		Map<Integer, Point> map = new HashMap<Integer, Point>();//an auxiliary map
		Map<Point, List<Point>> revCandMap = new HashMap<Point, List<Point>>();
		for(Point point1:candMap.keySet()){
			for(Point point2:candMap.get(point1)){
				if(!map.containsKey(point2.id)){
					map.put(point2.id, point2);
					List<Point> list = new ArrayList<Point>();
					list.add(point1);
					revCandMap.put(point2, list);
				}else{
					Point oldPoint2 = map.get(point2.id);
					revCandMap.get(oldPoint2).add(point1);
				}
				
//				if(revCandMap.containsKey(point2)){
//					revCandMap.get(point2).add(point1);
//				}else{
//					List<Point> list = new ArrayList<Point>();
//					list.add(point1);
//					revCandMap.put(point2, list);
//				}
			}
		}
//		System.out.println("reverse |candMap|:" + candMap.size() + " |revCandMap|:" + revCandMap.size());
		return revCandMap;//reverse the key-value pairs in the candMap
	}
	private void findBox(Box rootBox, int startRank, int targetRank, List<Box> boxList){
		if(startRank < targetRank){
			if(rootBox.getChildArr() != null){
				for(Box box:rootBox.getChildArr()){
					findBox(box, startRank + 1, targetRank, boxList);
				}
			}
		}else{
			boxList.add(rootBox);
		}
		
		if(startRank > targetRank){
			System.out.println("LinkBox wrong");
			System.exit(0);
		}
	} 

	//build the box for the results of joining the first edge
	public void buildFirstLevel(Box rootBox, Map<Point, List<Point>> candMap){
		//test codes
//		int recNum = 0;
//		for(Map.Entry<Point, List<Point>> entry:candMap.entrySet()){
//			int firstId = entry.getKey().id;
//			for(Point secondPoint:entry.getValue())   System.out.println("buildFirstLevel [" + firstId + ", " + secondPoint.id + "]");
//		}
//		System.out.println("LinkBox buildFirstLevel:" + recNum);
		
		Box rootBoxArr[] = new Box[candMap.size()];
		int index = 0;
		for(Map.Entry<Point, List<Point>> ent:candMap.entrySet()){
			Point firstPoint = ent.getKey();
			List<Point> list = ent.getValue();
			Box boxArr[] = new Box[list.size()];
			for(int h = 0;h < boxArr.length;h ++)   boxArr[h] = new Box(list.get(h));
			rootBoxArr[index ++] = new Box(firstPoint, boxArr);
		}
		rootBox.setChildArr(rootBoxArr);
	}
	
	//handle the outer-edges. It returns false if no new level is added
	public boolean buildLeaf(Box rootBox, Map<Point, List<Point>> candMap, int leftRank, int rightRank){
		//step 1: find boxes in the leftRank-th level of the tree
		List<Box> leftBoxList = new ArrayList<Box>();
		findBox(rootBox, -1, leftRank, leftBoxList);

		//step 2: build an auxiliary map
		Map<Integer, List<Point>> candIdMap = new HashMap<Integer, List<Point>>();
		for(Map.Entry<Point, List<Point>> ent:candMap.entrySet())   candIdMap.put(ent.getKey().id, ent.getValue());
		
		//step 3: link boxes
		boolean hasNextLevel = false;
		for(Box leftBox:leftBoxList){
			Point leftObj = leftBox.getPoint();
			if(candIdMap.containsKey(leftObj.id)){
				List<Point> rightList = candIdMap.get(leftObj.id);
				
				if(leftRank + 1 == rightRank){//append records directly
					Box boxArr[] = new Box[rightList.size()];
					for(int i = 0;i < rightList.size();i ++)   boxArr[i] = new Box(rightList.get(i));
					leftBox.setChildArr(boxArr);
					hasNextLevel = true;
				}else{//skip at least one level
					List<Box> boxList = new ArrayList<Box>();
					findBox(leftBox, leftRank, rightRank - 1, boxList);
					for(Box box:boxList){
						Box boxArr[] = new Box[rightList.size()];
						for(int i = 0;i < rightList.size();i ++)   boxArr[i] = new Box(rightList.get(i));
						box.setChildArr(boxArr);
						hasNextLevel = true;
//						System.out.println("add count=" + boxArr.length);
					}
				}
			}
		}
		return hasNextLevel;
	}
	
	//prune based on anchors
	public void achPrune(BoundedPattern bpattern, Box rootBox, Map<Integer, Integer> rankMap, int newId, List<Integer> achList){
		int achNum = achList.size();
		int rightRank = rankMap.get(newId);
		double lower[][] = bpattern.getLower(), upper[][] = bpattern.getUpper();
		for(int i = 0;i < achNum;i ++){
			int oldId = achList.get(i);
			int leftRank = rankMap.get(oldId);
			List<Box> leftBoxList = new ArrayList<Box>();
			findBox(rootBox, -1, leftRank, leftBoxList);//bug-2017-2-7
			
			double lbDist = lower[newId][oldId], ubDist = upper[newId][oldId];
			for(Box leftBox:leftBoxList){//System.out.println("leftBox-" + leftBox.getObjId());
				Point leftObj = leftBox.getPoint();
				List<Box> leftRightBoxList = new ArrayList<Box>();
				findBox(leftBox, leftRank, rightRank - 1, leftRightBoxList);
				
				for(Box leftRightBox:leftRightBoxList){
					if(leftRightBox.getChildArr() != null){
						List<Box> prunedBoxList = new ArrayList<Box>();
						for(Box rightBox:leftRightBox.getChildArr()){//System.out.println("rightBox-" + rightBox.getObjId());
							Point rightObj = rightBox.getPoint();
							double dist = Euclidean.dist(leftObj, rightObj);
							if(dist >= lbDist && dist <= ubDist)   prunedBoxList.add(rightBox);
						}
						if(prunedBoxList.size() < leftRightBox.getChildArr().length){
							Box boxArr[] = (Box[])prunedBoxList.toArray(new Box[prunedBoxList.size()]);
							leftRightBox.setChildArr(boxArr);//update the right-box list
						}
					}
				}
			}
		}
	}
	
	//prune based on anchors
	public void achPrune_old(BoundedPattern bpattern, Box rootBox, Map<Integer, Integer> rankMap, int newId, List<Integer> achList){
		//step 1: obtain the ranks
		int achNum = achList.size();
		List<Integer> rankList = new ArrayList<Integer>(achNum);
		for(int i = 0;i < achNum;i ++)   rankList.add(rankMap.get(achList.get(i)));

		//test codes
//		for(int rank:rankList)   System.out.print("rank:" + rank + " ");
//		System.out.println();
//		String label[] = bpattern.getPattern().getLabel();
//		System.out.println("anchor newId:" + label[newId] + " |achList|:" + achList.size());
//		for(int achId:achList)   System.out.println("anchor: " + label[achId] + " -> rank:" + rankList);
		
		//step 2: prune
		int curRank = rankList.get(0), rightRank = rankMap.get(newId);
		double lower[][] = bpattern.getLower(), upper[][] = bpattern.getUpper();
		
		for(int i = 0;i < achNum;i ++){
			int oldId = achList.get(i);
			int leftRank = rankList.get(i);
//			System.out.println("i=" + i);
//			System.out.println("curRank=" + curRank + " oldId:" + oldId);
//			System.out.println("leftRank=" + leftRank + " rightRank:" + rightRank);
			
			List<Box> boxList = new ArrayList<Box>();
			findBox(rootBox, -1, leftRank, boxList);//bug-2017-2-7
			for(Box box:boxList){
				List<Box> leftBoxList = new ArrayList<Box>();
				findBox(box, curRank, leftRank, leftBoxList);
				
				double lbDist = lower[newId][oldId], ubDist = upper[newId][oldId];
				for(Box leftBox:leftBoxList){//System.out.println("leftBox-" + leftBox.getObjId());
					Point leftObj = leftBox.getPoint();
					List<Box> leftRightBoxList = new ArrayList<Box>();
					findBox(leftBox, leftRank, rightRank - 1, leftRightBoxList);
					
					for(Box leftRightBox:leftRightBoxList){
						if(leftRightBox.getChildArr() != null){
							List<Box> prunedBoxList = new ArrayList<Box>();
							for(Box rightBox:leftRightBox.getChildArr()){//System.out.println("rightBox-" + rightBox.getObjId());
								Point rightObj = rightBox.getPoint();
								double dist = Euclidean.dist(leftObj, rightObj);
								if(dist >= lbDist && dist <= ubDist)   prunedBoxList.add(rightBox);
							}
							if(prunedBoxList.size() < leftRightBox.getChildArr().length){
								Box boxArr[] = (Box[])prunedBoxList.toArray(new Box[prunedBoxList.size()]);
								leftRightBox.setChildArr(boxArr);//update the right-box list
							}
						}
					}
				}
			}
			
			//test codes
//			BoxRecord boxRecord = new BoxRecord(bpattern.getPattern(), rootBox);
//			int count = boxRecord.countRecord(rootBox, 0, 6);
//			System.out.println("count:" + count);
//			System.out.println();
			
			curRank = leftRank;//update curRank
		}
	}
	
	//handle the inner-edges with inclusion-ship
	public void backInclude(Box rootBox, int leftRank, int rightRank, double lbDist, double ubDist){
		//step 1: find boxes in the leftRank-th level of the tree
		List<Box> leftBoxList = new ArrayList<Box>();
		findBox(rootBox, -1, leftRank, leftBoxList);
		
		//step 2: prune some boxes in the rightRank-th level
		for(Box leftBox:leftBoxList){
			Point leftObj = leftBox.getPoint();
			List<Box> leftRightBoxList = new ArrayList<Box>();
			findBox(leftBox, leftRank, rightRank - 1, leftRightBoxList);
			
			for(Box leftRightBox:leftRightBoxList){
				List<Box> prunedBoxList = new ArrayList<Box>();
				if(leftRightBox.getChildArr() != null){
					for(Box rightBox:leftRightBox.getChildArr()){
						Point rightObj = rightBox.getPoint();
						double dist = Euclidean.dist(leftObj, rightObj);
						if(dist >= lbDist && dist <= ubDist)   prunedBoxList.add(rightBox);
					}
					if(prunedBoxList.size() < leftRightBox.getChildArr().length){
						Box boxArr[] = (Box[])prunedBoxList.toArray(new Box[prunedBoxList.size()]);
						leftRightBox.setChildArr(boxArr);//update the right-box list
					}
				}
			}
		}
	}
	
	//handle the inner-edges with exclusion-ship
	public void backExclude(Box rootBox, Map<Point, List<Point>> candMap, int leftRank, int rightRank){
		//step 1: find boxes in the leftRank-th level of the tree
		List<Box> leftLeftBoxList = new ArrayList<Box>();
		findBox(rootBox, -1, leftRank - 1, leftLeftBoxList);
		
		//step 2: build an auxiliary map
		Map<Integer, Set<Integer>> candIdMap = new HashMap<Integer, Set<Integer>>();
		for(Map.Entry<Point, List<Point>> ent:candMap.entrySet()){
			Set<Integer> set = new HashSet<Integer>();
			for(Point point:ent.getValue())   set.add(point.id);
			candIdMap.put(ent.getKey().id, set);
		}
		
		//step 3: prune some boxes in both leftRank-th and rightRank-th level
		for(Box leftLeftBox:leftLeftBoxList){//System.out.println("leftLeftBox。obj:" + leftLeftBox.getObjId());
			if(leftLeftBox.getChildArr() != null){
				List<Box> leftPrunedList = new ArrayList<Box>();
				for(Box leftBox:leftLeftBox.getChildArr()){
//					int leftObjId = leftBox.getObjId();//System.out.println("leftBox.obj:" + leftBox.getObjId());
					Point leftObj = leftBox.getPoint();
					if(candIdMap.containsKey(leftObj.id)){
						leftPrunedList.add(leftBox);
						List<Box> leftRightBoxList = new ArrayList<Box>();
						findBox(leftBox, leftRank, rightRank - 1, leftRightBoxList);
						Set<Integer> set = candIdMap.get(leftObj.id);
						for(Box leftRightBox:leftRightBoxList){//System.out.println("leftRightBox.obj:" + leftRightBox.getObjId());
							List<Box> rightPrunedList = new ArrayList<Box>();
							if(leftRightBox.getChildArr() != null){//-------bug2017-2-4 it is possible
								for(Box rightBox:leftRightBox.getChildArr()){//System.out.println("rightBox.obj:" + rightBox.getObjId());
//									int rightObjId = rightBox.getObjId();
									Point rightObj = rightBox.getPoint();
									if(set.contains(rightObj.id))   rightPrunedList.add(rightBox);
								}
								if(rightPrunedList.size() < leftRightBox.getChildArr().length){
									Box boxArr[] = (Box[])rightPrunedList.toArray(new Box[rightPrunedList.size()]);
									leftRightBox.setChildArr(boxArr);//update the right-box list
								}
							}
						}
					}
				}
				
				if(leftPrunedList.size() < leftLeftBox.getChildArr().length){
					Box boxArr[] = (Box[])leftPrunedList.toArray(new Box[leftPrunedList.size()]);
					leftLeftBox.setChildArr(boxArr);
				}
			}
		}
	}
}