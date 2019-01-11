package com.spacekey.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;

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
		ArrayList<Property> result = new ArrayList<Property>();
        try {
			CSVReader reader = new CSVReader(new FileReader(path + filename));
			String [] items;
			int count = 0;
			while ((items = reader.readNext()) != null) {
				if (count == 0) { count++; continue; } else count++;
			    // items[] is an array of values from the line
				for (int i=0 ; i<items.length ; i++) {
					System.out.print(items[i] + " ");
				}
				Property p = new Property();
				p.id = Integer.parseInt(items[0]);
				p.type = items[1];
				p.price = Integer.parseInt(items[2]);
				p.rent = Integer.parseInt(items[3]);
				p.bedrooms = Integer.parseInt(items[4]);
				p.grossArea = Integer.parseInt(items[5]);
				p.saleableArea = Integer.parseInt(items[6]);
				p.floor = items[7];
				p.address = items[8];
				p.postDate = items[9];
				p.lat = Double.parseDouble(items[10]);
				p.lng = Double.parseDouble(items[11]);
				p.title = items[12];
				p.region = items[13];
				p.propertyName = items[14];
				p.description = items[15];
				p.contact = items[16];
				p.phoneNum = items[17];
				p.imageURL = items[18];
				p.pageURL = items[19];
				p.agentName = items[20];
				result.add(p);
				System.out.println();
			}
			reader.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
        return result;
	}
	

	public static void main(String[] args) {
		String path = "C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey-backend\\dataset\\";
		String filenamePOI = "poi.csv";
		String filenameProp = "property_cropped.csv";
		
		// ArrayList<String[]> result = readPOI("C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey-backend\\dataset\\poi.csv");
		ArrayList<Property> result = readProperty(path, filenameProp);
		
		System.out.println("Total data size: " + result.size());
		
	}
}
