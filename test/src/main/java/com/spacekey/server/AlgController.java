package com.spacekey.server;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.spacekey.algorithm.Methods;
import com.spacekey.algorithm.global.Point;
import com.spacekey.algorithm.Link;

@RestController
@RequestMapping("alg")
public class AlgController {

	@GetMapping("mck/{keywords}")
	HashSet<Point> mck(@PathVariable String keywords) {
		Methods M = new Methods();
		M.constructData(
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\loc",
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\doc"
		);
		HashSet<String> keyword_list = new HashSet<String>();
		for (String keyword : keywords.split("-")) {
			keyword_list.add(keyword);
		}
		HashSet<Point> results = M.mckExact(keyword_list);
		return results;
	}
	
	@GetMapping("minsk/{keywords}")
	HashSet<Point> minsk(@PathVariable String keywords) {
		Methods M = new Methods();
		M.constructData(
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\loc",
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\doc"
		);
		HashSet<String> keyword_list = new HashSet<String>();
		for (String keyword : keywords.split("-")) {
			keyword_list.add(keyword);
		}
		HashSet<Point> results = M.minskScaleLune(keyword_list);
		return results;
	}
	
	@GetMapping("coskq/{lat}/{lng}/{keywords}")
	HashSet<Point> coskq(@PathVariable double lat, @PathVariable double lng, @PathVariable String keywords) {
		Methods M = new Methods();
		M.constructData(
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\loc",
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\doc"
		);
		HashSet<String> keyword_list = new HashSet<String>();
		for (String keyword : keywords.split("-")) {
			keyword_list.add(keyword);
		}
		HashSet<Point> results = M.coskqType1Exact(lat, lng, keyword_list);
		return results;
	}
	
	@GetMapping("spm")
	String spm() {
		Methods M = new Methods();
		M.constructData(
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\loc",
			"C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\doc"
		);
		List<Link> linkList = new ArrayList<Link>();
		return "spm";
	}
}
