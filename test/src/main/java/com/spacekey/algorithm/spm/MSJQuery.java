package com.spacekey.algorithm.spm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.spacekey.algorithm.Link;
import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.global.DataReader;
import com.spacekey.algorithm.global.Point;
import com.spacekey.algorithm.spm.algorithm.msj.MStarJoin;
import com.spacekey.algorithm.spm.irtree.BuildIRTree;
import com.spacekey.algorithm.spm.irtree.Node;
import com.spacekey.algorithm.spm.pattern.Pattern;

/**
 * @author yxfang
 * @date 2016-11-28
 */
public class MSJQuery {

	public static void main(String[] args) {
		//step 1: read data
		String indexFile = Config.indexUK;
		DataReader dataReader = new DataReader(Config.locUK, Config.docUK);
		
		double loc[][] = dataReader.readLoc();
		String kws[][] = dataReader.readKws();

		//step 2: build index
		BuildIRTree builder = new BuildIRTree(loc, kws, indexFile);
		Node root = builder.build();
		
		//step 3: generate a pattern
		List<Link> linkList = new ArrayList<Link>();
		
		HashSet<String> hotel = new HashSet<String>(); hotel.add("hotel"); hotel.add("barton");
		HashSet<String> park = new HashSet<String>(); park.add("park"); park.add("farm");  park.add("est");
		HashSet<String> metro = new HashSet<String>(); metro.add("metro"); metro.add("canterbuy");
		HashSet<String> bar = new HashSet<String>(); bar.add("bar");
		HashSet<String> sea = new HashSet<String>(); sea.add("sea");
		HashSet<String> airport = new HashSet<String>(); airport.add("airport");
		
		
		linkList.add(new Link(hotel, park, 0.0, 0.2, true, false));
		linkList.add(new Link(hotel, metro, 0.005, 0.01, true, false));
		linkList.add(new Link(hotel, bar, 0.0, 0.01, false, false));
		linkList.add(new Link(metro, bar, 0.0, 0.02, false, false));

		Pattern pattern = new Pattern(linkList);
		MStarJoin all = new MStarJoin(root);
		List<int[]> rsList = all.query(pattern);
		
		if(rsList != null && rsList.size() > 0){
			System.out.println("|rsList|:" + rsList.size());
			for(int i = 0; i < rsList.size(); i++){
				System.out.println();
				int record[] = rsList.get(i);
				for (int j = 0 ; j < record.length ; j++) {
					System.out.printf("loc: %f %f, keyword: %s\n", loc[record[j]][0], loc[record[j]][1], all.keywordMap[j]);
					System.out.printf("keywords: ");
					for (int k = 0 ; k < kws[record[j]].length ; k++)
						System.out.printf("%s ", kws[record[j]][k]);
					System.out.println();
				}
			}
		}
	}
}
/*
hotel->0
airport->2
station->3
sea->1

154202 56502 87794 88813 
  var myLatLng0 = {lat: 55.95609, lng: -3.17873};
  var myLatLng1 = {lat: 55.95803, lng: -3.18518};
  var myLatLng2 = {lat: 55.94823, lng: -3.36372};
  var myLatLng3 = {lat: 55.95178, lng: -3.19139};

hotel->0
airport->2
metro->1
bar->3
154324 155895 139305 160699 
  var myLatLng0 = {lat: 55.94979, lng: -3.17854};
  var myLatLng1 = {lat: 55.94492, lng: -3.18357};
  var myLatLng2 = {lat: 55.94307, lng: -3.36918};
  var myLatLng3 = {lat: 55.94287, lng: -3.18468};  
*/