package com.spacekey.algorithm.global;

import java.io.*;

import com.spacekey.algorithm.spm.algorithm.mpj.InvFileCreator;

/**
 * @author yxfang
 * @date 2016-8-3
 * read datasets into memory from disk
 */
public class DataReader {
	private String locFile = null;
	private String kwsFile = null;
	private int userNum = -1;
	
	public DataReader(String locFile, String kwsFile){
		this.locFile = locFile;
		this.kwsFile = kwsFile;
		
		try{
			File test= new File(locFile);
			long fileLength = test.length(); 
			LineNumberReader rf = new LineNumberReader(new FileReader(test));
			if (rf != null) {
				rf.skip(fileLength);
				userNum = rf.getLineNumber();//obtain the number of nodes
			}
			rf.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println(locFile + " userNum=" + userNum);
	}
	
	//read locations
	public double[][] readLoc(){
		double loc[][] = new double[userNum][];
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(locFile));

			String line = null;
			while((line = stdin.readLine()) != null){
				String s[] = line.split(",");
//				System.out.println(line);
				
				int id = Integer.parseInt(s[0]);
				double x = Double.parseDouble(s[1]);
				double y = Double.parseDouble(s[2]);
				
				loc[id] = new double[2];
				loc[id][0] = x;
				loc[id][1] = y;
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("finish reading " + locFile);
		
		return loc;
	}
	
	//read keywords
	public String[][] readKws(){
		String kws[][] = new String[userNum][];
		try{
			BufferedReader stdin = new BufferedReader(new FileReader(kwsFile));

			String line = null;
			while((line = stdin.readLine()) != null){
				String s[] = line.split(",");
				
				int id = Integer.parseInt(s[0]);
				kws[id] = new String[s.length - 1];
				for(int i = 1;i < s.length;i ++)   kws[id][i - 1] = s[i];
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("finish reading " + kwsFile);
		
		return kws;
	}
	
	public static void main(String[] args) {
//		DataReader dataReader = new DataReader(Config.locNY, Config.docNY);
		DataReader dataReader = new DataReader(Config.locTW, Config.docTW);
		double loc[][] = dataReader.readLoc();
//		String kws[][] = dataReader.readKws();
	}
}