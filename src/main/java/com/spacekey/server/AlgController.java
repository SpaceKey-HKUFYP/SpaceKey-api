package com.spacekey.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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
	}

	@PostMapping("spm_simple")
	@ResponseBody
	SpmSimpleRet spmSimple(@RequestParam String type, @RequestParam String region, @RequestBody SpmSimpleReq reqBody) {

		ArrayList<WantedObject> wantedObjects = reqBody.wantedObjects;

		Methods M = new Methods();
		
		System.out.println("!alg spm_simple type=" + type + " region=" + region);
		M.constructDataWeb();
		
		ArrayList<POI> dataPOI = DataReader.readPOI(Const.path, Const.filenamePOI);
		ArrayList<Property> dataProp = DataReader.readProperty(Const.path, Const.filenameProp);

		List<Link> linkList = new ArrayList<Link>();
		
		for (WantedObject obj : wantedObjects) {
			HashSet<String> k1 = new HashSet<String>();
			HashSet<String> k2 = new HashSet<String>();
			double lower = 0, upper = 10;
			k1.add("property");
			k2.add(obj.keyword);
			System.out.println(obj.keyword);
			
			double coordinateToMeter = 111320;
			lower = obj.lower / coordinateToMeter;
			upper = obj.upper / coordinateToMeter;
			Link link = new Link(k1, k2, lower, upper, false, true, obj.dir);
			linkList.add(link);
		}

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
								if (Point.dir(poi, prop, obj.dir))
									flag_dir = true;
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
					if (!point.keywords.contains("property"))
						POIs.add(dataPOI.get(point.id));
			}

		}
		System.out.println("property size: " + props.size());
		System.out.println("POI size: " + POIs.size());
		return new SpmSimpleRet(props, POIs);
	}
}
