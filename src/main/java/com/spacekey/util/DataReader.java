package com.spacekey.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {
	
	public static ArrayList<String[]> readCSV(String csvFile) {
		BufferedReader br = null;
		String line = "";
		String splitChar = ";";
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		try {
	        br = new BufferedReader(new FileReader(csvFile));
	        while ((line = br.readLine()) != null) {
		        String[] items = line.split(splitChar);
		        result.add(items);
		    }
	
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    if (br != null) {
		        try {
		            br.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
        return result;
	}
	
	public static void main(String[] args) {
		ArrayList<String[]> result = readCSV("C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey-backend\\dataset\\properties.csv");
		for (String[] item : result) {
			System.out.println(item.length);
//			if (item.length < 6) {
//				for (String item2: item) {
//					System.out.println(item2);
//				}
//			}
		}
	}
}
