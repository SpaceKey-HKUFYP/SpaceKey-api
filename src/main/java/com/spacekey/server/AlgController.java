package com.spacekey.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spacekey.algorithm.Link;
import com.spacekey.algorithm.global.Point;
import com.spacekey.util.Const;
import com.spacekey.util.DataReader;
import com.spacekey.util.POI;
import com.spacekey.util.Property;

@RestController
@RequestMapping("alg")
public class AlgController {

	static class WantedObject {
		public String keyword;
		public int lower;
		public int upper;
		public String dir;
	}
	
	static class CustomObject {
		public String keyword;
		public int lower;
		public int upper;
		public String dir;
		public double lat;
		public double lng;
	}

	static class SpmSimpleRet {
		public HashSet<Property> houseData;
		public HashSet<POI> poiData;

		public SpmSimpleRet(HashSet<Property> houseData, HashSet<POI> poiData) {
			this.houseData = houseData;
			this.poiData = poiData;
		}
	}

	static class SpmSimpleReq {
		public ArrayList<WantedObject> wantedObjects;
		public ArrayList<CustomObject> customObjects;
	}

	@PostMapping("spm_simple")
	@ResponseBody
	SpmSimpleRet spmSimple(@RequestParam String type, @RequestParam String region, @RequestBody SpmSimpleReq reqBody) {
		
		System.out.println("!alg spm_simple type=" + type + " region=" + region);
		ArrayList<WantedObject> wantedObjects = reqBody.wantedObjects;
		ArrayList<CustomObject> customObjects = reqBody.customObjects;

		ArrayList<POI> dataPOI = DataReader.readPOI(Const.path, Const.filenamePOI);
		ArrayList<Property> dataProp = DataReader.readProperty(Const.path, Const.filenameProp);

		List<Link> linkList = new ArrayList<Link>();
		for (WantedObject obj : wantedObjects) {
			
			HashSet<String> k1 = new HashSet<String>();
			HashSet<String> k2 = new HashSet<String>();
			double lower = 0, upper = 10;
			k1.add("property");
			k2.add(obj.keyword);
			System.out.println("\t!" +obj.keyword + " " + obj.dir + " " + obj.lower + " " + obj.upper);

			double coordinateToMeter = 111320;
			lower = obj.lower/ coordinateToMeter;
			if (obj.upper == -1)
				upper = 30000 / coordinateToMeter;
			else upper = obj.upper / coordinateToMeter;

			if (obj.upper == -1 && obj.upper == -1) {
				// TODO: unwanted object
			}

			Link link = new Link(k1, k2, lower, upper, false, true, obj.dir);
			linkList.add(link);
		}
		
		String keyword[] = new String[customObjects.size()];
		double lat[] = new double[customObjects.size()];
		double lng[] = new double[customObjects.size()];
		
		int i = 0;
		for (CustomObject obj: customObjects) {
			
			HashSet<String> k1 = new HashSet<String>();
			HashSet<String> k2 = new HashSet<String>();
			double lower = 0, upper = 10;
			k1.add("property");
			k2.add(obj.keyword);
			System.out.println("\t!" +obj.keyword + " " + obj.dir + " " + obj.lower + " " + obj.upper);

			double coordinateToMeter = 111320;
			lower = obj.lower/ coordinateToMeter;
			if (obj.upper == -1)
				upper = 30000 / coordinateToMeter;
			else upper = obj.upper / coordinateToMeter;

			if (obj.upper == -1 && obj.upper == -1) {
				// TODO: unwanted object
			}

			Link link = new Link(k1, k2, lower, upper, false, true, obj.dir);
			linkList.add(link);
			
			keyword[i] = obj.keyword; lat[i] = obj.lat; lng[i] = obj.lng;
			i++;
		}

		Methods M = new Methods();
		M.constructDataWeb(keyword, lat, lng);
		
		HashSet<HashSet<Point>> results = M.spmMSJ(linkList);
		HashSet<POI> POIs = new HashSet<POI>();
		HashSet<Property> props = new HashSet<Property>();

		System.out.println("result size: " + results.size());

		for (HashSet<Point> result : results) {
			boolean flag = false;

			for (Point point : result) {
				if (point.keywords.contains("property")) {

					Property p = dataProp.get(point.id - dataPOI.size());

					boolean flag_break = false;
					if (!(type.equals("any") || p.type.equals(type)))
						flag_break = true;
					if (!(region.equals("any") || p.region.equals(region)))
						flag_break = true;

					Point prop = point;
					for (WantedObject obj: wantedObjects) {
						boolean flag_dir = false;
						for (Point poi : result) {
							if (poi.keywords.contains(obj.keyword))
								if (Point.dir(poi, prop, obj.dir)) flag_dir = true;
						}
						if (!flag_dir) flag_break = true;
					}
					if (!flag_break) {
						props.add(p);
						flag = true;
					}
				}
			}
			if (flag) {
				for (Point point : result)
					if (!point.keywords.contains("property") && point.id < dataPOI.size())
						POIs.add(dataPOI.get(point.id));
			}

		}
		System.out.println("property size: " + props.size());
		System.out.println("POI size: " + POIs.size());
		return new SpmSimpleRet(props, POIs);
	}
}
