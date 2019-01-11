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

@RestController
@RequestMapping("data")
public class DataController {

	class PropertyRet {
		public ArrayList<Property> houseData;
		public PropertyRet(ArrayList<Property> houseData) {
			this.houseData = houseData;
		}
	}
	
	@GetMapping("property/get")
	PropertyRet test(@RequestParam String type, @RequestParam String region) {
		ArrayList<Property> data = DataReader.readProperty(Const.path, Const.filenameProp);
		ArrayList<Property> result = new ArrayList<Property>();
		System.out.println(type + " " + region);
		for (Property p: data) {
			// System.out.println(type + " " + p.type + " " + region + " " + p.region);
			if (p.type.equals(type) && p.region.equals(region)) 
				result.add(p);
		}
		return new PropertyRet(result);
	}
	
	@GetMapping("property/all")
	ArrayList<Property> getPropAll() {
		return DataReader.readProperty(Const.path, Const.filenameProp);
	}
}