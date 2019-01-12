package com.spacekey.server;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spacekey.util.DataReader;
import com.spacekey.util.Const;
import com.spacekey.util.Property;
import com.spacekey.util.POI;

@RestController
@RequestMapping("data")
public class DataController {

	class PropertyRet {
		public ArrayList<Property> houseData;
		public PropertyRet(ArrayList<Property> houseData) {
			this.houseData = houseData;
		}
	}

	class POIRet {
		public ArrayList<POI> POIResult;
		public POIRet(ArrayList<POI> POIData) {
			this.POIResult = POIData;
		}
	}

	@GetMapping("property/get")
	PropertyRet getProp(@RequestParam String type, @RequestParam String region) {
		System.out.println("!req property/get type=" + type + " region=" + region);
		ArrayList<Property> data = DataReader.readProperty(Const.path, Const.filenameProp);
		ArrayList<Property> result = new ArrayList<Property>();
		for (Property p: data) {
			if (type == "any" || p.type.equals(type))
				if (region == "any" || p.region.equals(region))
					result.add(p);
		}
		return new PropertyRet(result);
	}

	@GetMapping("property/all")
	PropertyRet getPropAll() {
		System.out.println("!req property/all");
		return new PropertyRet(
			DataReader.readProperty(Const.path, Const.filenameProp)
		);
	}

	@GetMapping("poi/get")
	POIRet getPOI(@RequestParam String keyword) {
		System.out.println("!req poi/get keyword" + keyword);
		ArrayList<POI> data = DataReader.readPOI(Const.path, Const.filenamePOI);
		ArrayList<POI> result = new ArrayList<POI>();
		for (POI p: data) {
			if (keyword == "any" || p.searchKey.equals(keyword))
				result.add(p);
		}
		return new POIRet(result);
	}
	
	@GetMapping("poi/all")
	POIRet getPOIall() {
		System.out.println("!req poi/all");
		return new POIRet(
			DataReader.readPOI(Const.path, Const.filenamePOI)
		);
	}
}
