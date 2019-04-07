package com.spacekey.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.spacekey.util.POI;

public class DataReader {
	final static double lat_max = 24f;
	final static double lat_min = 22f;
	final static double lng_max = 115f;
	final static double lng_min = 113f;

	public static ArrayList<POI> readPOI(String path, String filename) {
		ArrayList<POI> result = new ArrayList<POI>();
        try {
			CSVReader reader = new CSVReader(new FileReader(path + filename), ';');
			String [] items;
			int count = 0, index = 0;
			while ((items = reader.readNext()) != null) {
				if (count == 0) { count++; continue; } else count++;
				POI p = new POI();
				p.name = items[0];
				p.numOfReviews = Integer.parseInt(items[1]);
				p.searchKey = items[2];
				p.givenKey = items[3];
				p.address = items[4];
				p.lat = Double.parseDouble(items[5]);
				p.lng = Double.parseDouble(items[6]);
				// p.id = Integer.parseInt(items[7]);
				p.id = index++;

				result.add(p);
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
		//System.out.println("starting reading property data");
		ArrayList<Property> result = new ArrayList<Property>();
        try {
			CSVReader reader = new CSVReader(new FileReader(path + filename),';');
			String [] items;
			int count = 0, index = 0;
			while ((items = reader.readNext()) != null) {
				if (count == 0) { count++; continue; } else count++;
				Property p = new Property();
				// p.id = Integer.parseInt(items[0]);
				//System.out.print(items.length + " ");
				//System.out.print(items[0]);
				//System.out.print(items[18]);System.out.println();
				//System.out.println("index " + index + "count " + count);
				p.id = index++;
				p.type = items[1];
				p.price = Integer.parseInt(items[2]); // 0 as null
				p.rent = Integer.parseInt(items[3]);  // 0 as null
				p.bedrooms = Integer.parseInt(items[4]);
				p.grossArea = (int)Double.parseDouble(items[5]);  // 0 as null
				p.saleableArea = (int)Double.parseDouble(items[6]);  // 0 as null
				p.floor = items[7]; 		// can be ""
				p.address = items[8];		// can be ""
				p.postDate = items[9];
				p.lat = Double.parseDouble(items[10]); // 0 as null
				p.lng = Double.parseDouble(items[11]);
				p.title = items[12];
				p.region = items[13];  // can be ""
				p.propertyName = items[14];
				p.description = items[15];
				p.contact = items[16];
				p.phoneNum = items[17];
				p.imageURL = items[18];
				p.pageURL = items[19];
				p.agentName = items[20];
				if(p.lat == 0.0f
						|| p.imageURL == "https://www.28hse.com/en/utf8/dreamimages/nophoto150.jpg"
						|| (p.price == 0.0f && p.rent == 0.0f)
						|| (p.lat > lat_max || p.lat < lat_min)
						|| (p.lng > lng_max || p.lng < lng_min)
						) continue;
				result.add(p);
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
		//System.out.println("current dir" + System.getProperty("user.dir"));
		String appPath = System.getProperty("user.dir");
		String path = appPath + "//dataset//";
		String filenamePOI = "place_sample.csv";
		String filenameProp = "property_sample_test.csv";

		ArrayList<POI> result = readPOI(path, filenamePOI);
		ArrayList<Property> propResult = readProperty(path, filenameProp);


		System.out.println("Total data size: " + result.size());

	}
}
