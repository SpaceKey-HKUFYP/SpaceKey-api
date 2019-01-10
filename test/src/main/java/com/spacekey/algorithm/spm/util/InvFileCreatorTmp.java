package com.spacekey.algorithm.spm.util;

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
 */
public class InvFileCreatorTmp {

	public static void InvFileCreator(String locFilePath, String docFilePath, String rootPath){
		File rootFold = new File(rootPath + "invert");
		File subFold[] = rootFold.listFiles();
		for(File file:subFold){
			File tmp[] = file.listFiles();
			for(File tmpFile:tmp)   tmpFile.delete();
			file.delete();
		}
	}
	
	public static void main(String[] args) {
		InvFileCreatorTmp.InvFileCreator(Config.locNY, Config.docNY, Config.rootNY);
//		InvFileCreator.InvFileCreator(Config.locLA, Config.docLA, Config.rootLA);
//		InvFileCreator.InvFileCreator(Config.locTW, Config.docTW, Config.rootTW);
	}

}
