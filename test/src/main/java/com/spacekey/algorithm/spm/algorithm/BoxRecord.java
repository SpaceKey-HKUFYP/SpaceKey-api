package com.spacekey.algorithm.spm.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2017-1-19
 * collect all the records, or count the records
 */
public class BoxRecord {
	private Pattern pattern = null;
	private Box rootBox = null;
	
	public BoxRecord(Pattern pattern, Box rootBox){
		this.pattern = pattern;
		this.rootBox = rootBox;
	}
	
	//collect all the matched records
	public List<int[]> collectRecord(){
		return collectRecord(rootBox, 0, pattern.getM(), null);
	}
	private List<int[]> collectRecord(Box rootBox, int cur, int all, int[] rootRecord){
		List<int[]> rsList = new ArrayList<int[]>();
		if(cur == 0){
			for(Box box:rootBox.getChildArr()){
				int record[] = {box.getPoint().id};
				List<int[]> tmpList = collectRecord(box, cur + 1, all, record);
				rsList.addAll(tmpList);
			}
		}else{
			if(rootBox.getChildArr() != null){
				for(Box box:rootBox.getChildArr()){
					int record[] = new int[rootRecord.length + 1];
					for(int i = 0;i < rootRecord.length;i ++)   record[i] = rootRecord[i];
					record[rootRecord.length] = box.getPoint().id;
					
					if(cur < all - 1){
						List<int[]> tmpList = collectRecord(box, cur + 1, all, record);
						rsList.addAll(tmpList);
					}else{
						rsList.add(record);
					}
				}
			}
		}
		return rsList;
	}
	
	//count the number of matched records
//	public int countRecord(){
//		return countRecord(rootBox, 0, pattern.getM());
//	}
	public int countRecord(Box rootBox, int cur, int all){
		if(cur < all){
			int sum = 0;
			if(rootBox.getChildArr() != null){
				for(Box box:rootBox.getChildArr()){
					int number = countRecord(box, cur + 1, all);
					sum += number;
				}
			}
			return sum;
		}else{
			return 1;
		}
	}
}
