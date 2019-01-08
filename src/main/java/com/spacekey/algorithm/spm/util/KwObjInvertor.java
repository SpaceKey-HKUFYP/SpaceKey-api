package com.spacekey.algorithm.spm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yxfang
 * @date 2016-8-26
 * This is a newly added inverted list: keyword -> objects
 * This inverted list will be used for sampling
 */
public class KwObjInvertor {
	private String kws[][] = null;//the keywords in the dataset
	private Map<String, List<Integer>> kwObjInvertMap = null;//the returned inverted list
	
	public KwObjInvertor(String kws[][]){
		this.kws = kws;
	}
	
	//the public function provided by this class
	public Map<String, List<Integer>> buildInvert(){
		this.kwObjInvertMap = new HashMap<String, List<Integer>>();
		
		for(int i = 0;i < kws.length;i ++){
			for(int j = 0;j < kws[i].length;j ++){
				String word = kws[i][j];
				if(kwObjInvertMap.containsKey(word)){
					kwObjInvertMap.get(word).add(i);
				}else{
					List<Integer> list = new ArrayList<Integer>();
					list.add(i);
					kwObjInvertMap.put(word, list);
				}
			}
		}
		
		return kwObjInvertMap;
	}
}
