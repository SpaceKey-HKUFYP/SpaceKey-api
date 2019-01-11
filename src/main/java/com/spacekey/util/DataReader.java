package com.spacekey.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {
	
	public static ArrayList<String[]> readPOI(String csvFile) {
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
	
	public static ArrayList<Property> readProperty(String path, String filename) {
		String splitChar = ";";
		String text = "";
		ArrayList<Property> result = new ArrayList<Property>();
		
		try {
			File file = new File(path, filename);
			if (!file.exists()) {
				return null;
			}
			FileInputStream inputStream = new FileInputStream(file);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);
			text = new String(b);
			String[] items = text.split(splitChar);
			
			int i = 20;
			while (i < items.length) {
				Property p = new Property();
				System.out.print(items[i]);
				p.id = Integer.parseInt(items[i++]);
				p.type = items[i++];
				p.price = Integer.parseInt(items[i++]);
				p.rent = Integer.parseInt(items[i++]);
				p.roomNum = items[i++];
				p.grossArea = Integer.parseInt(items[i++]);
				p.netFloorArea = Integer.parseInt(items[i++]);
				p.floor = items[i++];
				p.address = items[i++];
				p.postDate = items[i++];
				p.lat = Double.parseDouble(items[i++]);
				p.lng = Double.parseDouble(items[i++]);
				p.title = items[i++];
				p.region = items[i++];
				p.propertyName = items[i++];
				p.description = items[i++];
				p.contact = items[i++];
				p.phoneNum = items[i++];
				p.imageURL = items[i++];
				p.pageURL = items[i++];
				p.agentName = items[i++];
				result.add(p);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	public static void main(String[] args) {
		//ArrayList<String[]> result = readPOI("C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey-backend\\dataset\\poi.csv");
		ArrayList<Property> result = readProperty("C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey-backend\\dataset", "properties.csv");
		for (Property p : result) {
			System.out.println(p.title);
		}
	}
}
