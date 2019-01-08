package com.spacekey.algorithm.spm.algorithm.mpj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.spacekey.algorithm.global.Config;

/**
 * @author yxfang
 * @date 2017-1-16
 * Given a keyword, read its inverted list
 */
public class InvFileReader {
	private String rootPath = null;
	
	public InvFileReader(String rootPath){
		this.rootPath = rootPath;
	}
	
	public List<double[]> readInvFileCreator(String word){
		List<double[]> list = new ArrayList<double[]>();
		
		int code = word.hashCode();
		int mod = code % Config.foldNum;
		if(mod < 0)   mod += Config.foldNum;
		try{
			File file = new File(rootPath + mod + "/" + word);
			if(!file.exists())   return list;
			
			BufferedReader stdin = new BufferedReader(new FileReader(file));
			String line = stdin.readLine();
			
			String s[] = line.split(" ");
			for(int i = 0;i + 1 < s.length;i += 2){
				double x = Double.parseDouble(s[i]);
				double y = Double.parseDouble(s[i + 1]);
				double loc[] = {x, y};
				list.add(loc);
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
}