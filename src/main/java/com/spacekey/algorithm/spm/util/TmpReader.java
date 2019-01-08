package com.spacekey.algorithm.spm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import com.spacekey.algorithm.global.Config;

/**
 * @author yxfang
 * @date 2016-8-3
 * read datasets into memory from disk
 */
public class TmpReader {
	
	public static void main(String[] args) {
		try{
			String file = "C:\\Users\\Admin\\Desktop\\SPM\\datasets\\Flickr\\LondonFlickr.gz.good";
			BufferedReader stdin = new BufferedReader(new FileReader(file));

			String line = null;
			while((line = stdin.readLine()) != null){
				System.out.println(line);
			}
			stdin.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}