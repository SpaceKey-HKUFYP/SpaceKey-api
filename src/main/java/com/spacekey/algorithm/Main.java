package com.spacekey.algorithm;

import java.util.HashSet;

import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.Point;
import com.spacekey.server.Methods;

public class Main {
	
	public static String root = "C:\\Users\\WagJK\\Desktop\\FYP\\SpaceKey\\dataset\\UK\\";
	
	public static void main(String args[]) {
		Methods methods = new Methods();
		
		String loc = Main.root + "loc";
		String doc = Main.root + "doc";
		
		methods.constructData(loc, doc);
		
		HashSet<String> keywords = new HashSet<String>();
		keywords.add("house");
		keywords.add("school") ;
		
		HashSet<Point> result_exact = methods.mckExact(keywords);
		HashSet<Point> result_SKECaplus = methods.mckSKECaplus(keywords);
		HashSet<Point> result_GKG = methods.mckGKG(keywords);
		
		System.out.println("result_exact:");
		for (Point point : result_exact) {
			for (String keyword : point.keywords) {
				System.out.print(keyword);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("result_SKECaplus:");
		for (Point point : result_SKECaplus) {
			for (String keyword : point.keywords) {
				System.out.print(keyword);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("result_GKG:");
		for (Point point : result_GKG) {
			for (String keyword : point.keywords) {
				System.out.print(keyword);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
}
