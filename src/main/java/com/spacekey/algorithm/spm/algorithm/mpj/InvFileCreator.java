package com.spacekey.algorithm.spm.algorithm.mpj;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;

/**
 * @author yxfang
 * @date 2017-1-16
 * create the inverted file for each keyword
 * this is supposed to be done offline, i.e., before running MPJ
 */
public class InvFileCreator {

	public static void creat(String locFilePath, String docFilePath, String invertFilePath){
		DataReader dataReader = new DataReader(locFilePath, docFilePath);
		String kws[][] = dataReader.readKws();
		double loc[][] = dataReader.readLoc();
		
		//step 1: build the inverted list
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		for(int id = 0;id < kws.length;id ++){
			String words[] = kws[id];
			for(String word:words){
				if(map.containsKey(word)){
					map.get(word).add(id);
				}else{
					List<Integer> list = new ArrayList<Integer>();
					list.add(id);
					map.put(word, list);
				}
			}
		}
		
		//step 2: output the inverted list into files
		System.out.println("total: " + map.size());
		int progress = 0;
		File rootFold = new File(invertFilePath);
		if(!rootFold.exists())   rootFold.mkdir();
		for(Map.Entry<String, List<Integer>> entry:map.entrySet()){
			String word = entry.getKey();
			List<Integer> list = entry.getValue();
			int code = word.hashCode();
			int mod = code % Config.foldNum;
			if(mod < 0)   mod += Config.foldNum;
			File subFold = new File(invertFilePath + mod);
			if(!subFold.exists())   subFold.mkdir();
			
			try{
				BufferedWriter stdout = new BufferedWriter(new FileWriter(invertFilePath + mod + "/" + word));
				for(int id:list){
					stdout.write(loc[id][0] + " " + loc[id][1] + " ");
				}
				stdout.newLine();
				stdout.flush();
				stdout.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			progress ++;
			if(progress % 10000 == 0)   System.out.println("finish: " + progress);
		}
	}
	
	public static void main(String[] args) {
//		InvFileCreator.creat(Config.locUK, Config.docUK, Config.invertUK);
//		InvFileCreator.creat(Config.locNY, Config.docNY, Config.invertNY);
//		InvFileCreator.creat(Config.locLA, Config.docLA, Config.invertLA);
//		InvFileCreator.creat(Config.locTW, Config.docTW, Config.invertTW);
	}

}
