package com.spacekey.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.spacekey.util.POI;

public class DataReader {
	
	public static ArrayList<POI> readPOI(String path, String filename) {
		ArrayList<POI> result = new ArrayList<POI>();
        try {
			CSVReader reader = new CSVReader(new FileReader(path + filename), ';');
			String [] items;
			int count = 0;
			while ((items = reader.readNext()) != null) {
				if (count == 0) { count++; continue; } else count++;
			    // items[] is an array of values from the line
//				for (int i=0 ; i<items.length ; i++) {
//					System.out.print(items[i] + " ");
//				}
				POI p = new POI();
				p.name = items[0];
				p.numOfReviews = Integer.parseInt(items[1]);
				p.searchKey = items[2];
				p.givenKey = items[3];
				p.address = items[4];
				p.lat = Double.parseDouble(items[5]);
				p.lng = Double.parseDouble(items[6]);
				p.id = Integer.parseInt(items[7]);
				
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
	
	public static ArrayList<Property> readProperty(String path, String filename) {
		ArrayList<Property> result = new ArrayList<Property>();
        try {
			CSVReader reader = new CSVReader(new FileReader(path + filename));
			String [] items;
			int count = 0;
			while ((items = reader.readNext()) != null) {
				if (count == 0) { count++; continue; } else count++;
			    // items[] is an array of values from the line
//				for (int i=0 ; i<items.length ; i++) {
//					System.out.print(items[i] + " ");
//				}
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
		String filenamePOI = "place_sample.csv";
		String filenameProp = "property_cropped.csv";
		
		ArrayList<POI> result = readPOI(path, filenamePOI);
		// ArrayList<Property> result = readProperty(path, filenameProp);
		
		System.out.println("Total data size: " + result.size());
		
	}
}
