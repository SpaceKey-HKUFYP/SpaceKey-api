package com.spacekey.server;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import com.spacekey.algorithm.Link;
import com.spacekey.algorithm.global.DataReader;
import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Pair;
import com.spacekey.algorithm.global.Point;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.docindex.InvertedFile;
import com.spacekey.algorithm.global.typedef.Dataset;
import com.spacekey.algorithm.global.typedef.Group;
import com.spacekey.algorithm.global.typedef.STObject;

import com.spacekey.util.Const;
import com.spacekey.util.POI;
import com.spacekey.util.Property;

import java.util.ArrayList;
import java.util.Set;

public class Methods implements MethodsInterface {
	
	private double loc[][];
	private String kws[][];
	private com.spacekey.algorithm.coskq.index.Node coskqRoot;
	private com.spacekey.algorithm.spm.irtree.Node spmRoot;
	private InvertedFile invertedFile;
	
	public void constructDataWeb(String[] keyword, double[] lat, double[] lng) {
		ArrayList<POI> dataPOI 			= com.spacekey.util.DataReader.readPOI(Const.path, Const.filenamePOI);
		ArrayList<Property> dataProp 	= com.spacekey.util.DataReader.readProperty(Const.path, Const.filenameProp);
		
		int size = dataPOI.size() + dataProp.size() + keyword.length;
		loc = new double[size][];
		kws = new String[size][];
		
		int index = 0;
		for (POI p : dataPOI) {
			loc[index] = new double[2];
			loc[index][0] = p.lat;
			loc[index][1] = p.lng;
			kws[index] = new String[1];
			kws[index][0] = p.searchKey;
			index++;
		}
		for (Property p: dataProp) {
			loc[index] = new double[2];
			loc[index][0] = p.lat;
			loc[index][1] = p.lng;
			kws[index] = new String[1];
			kws[index][0] = "property";
			index++;
		}
		if (keyword.length > 0) {
			for (int i=0 ; i<keyword.length ; i++) {
				loc[index] = new double[2];
				loc[index][0] = lat[i];
				loc[index][1] = lng[i];
				kws[index] = new String[1];
				kws[index][0] = keyword[i];
				index++;
			}
		}
		
		Env.W = new Words();
		InvertedFile iv = new InvertedFile();
		Dataset db = new Dataset();
		for (int i=0 ; i<loc.length ; i++) {
			HashSet<String> keywords = new HashSet<String>();
			for (int j=0 ; j<kws[i].length ; j++)
				keywords.add(kws[i][j]);
			STObject obj = new STObject(i+1, loc[i][0], loc[i][1], keywords);
			db.add(obj);
			Env.W.add(obj);
		}
		for (STObject o: db) iv.add(o);	// loading objects into the inverted file
		this.invertedFile = iv;
		
		String indexFile = com.spacekey.algorithm.global.Config.indexUK;
		com.spacekey.algorithm.coskq.index.BuildIRTree builder1 = new com.spacekey.algorithm.coskq.index.BuildIRTree(loc, kws, indexFile);
		coskqRoot = builder1.build();

		com.spacekey.algorithm.spm.irtree.BuildIRTree builder2 = new com.spacekey.algorithm.spm.irtree.BuildIRTree(loc, kws, indexFile);
		spmRoot = builder2.build();
	}
	
	
	public void constructData(String locPath, String docPath) {
		// read data
		DataReader dataReader = new DataReader(locPath, docPath);
		this.loc = dataReader.readLoc();
		this.kws = dataReader.readKws();
		
		// mck & minsk construct
		Env.W = new Words();
		InvertedFile iv = new InvertedFile();
		Dataset db = new Dataset();
		for (int i=0 ; i<loc.length ; i++) {
			HashSet<String> keywords = new HashSet<String>();
			for (int j=0 ; j<kws[i].length ; j++)
				keywords.add(kws[i][j]);
			STObject obj = new STObject(i+1, loc[i][0], loc[i][1], keywords);
			db.add(obj);
			Env.W.add(obj);
		}
		for (STObject o: db) iv.add(o);	// loading objects into the inverted file
		this.invertedFile = iv;
		
		// coskq construct
		String indexFile = com.spacekey.algorithm.global.Config.indexUK;
		com.spacekey.algorithm.coskq.index.BuildIRTree builder1 = new com.spacekey.algorithm.coskq.index.BuildIRTree(loc, kws, indexFile);
		coskqRoot = builder1.build();
		
		// spm construct
		indexFile = com.spacekey.algorithm.global.Config.indexUK;
		com.spacekey.algorithm.spm.irtree.BuildIRTree builder2 = new com.spacekey.algorithm.spm.irtree.BuildIRTree(loc, kws, indexFile);
		spmRoot = builder2.build();
	}
	
	public HashSet<Point> mckGKG(HashSet<String> keywords) {
		if (invertedFile == null) {
			System.out.println("Error: mck not yet constructed"); return null;
		}
		com.spacekey.algorithm.mck.algorithm.Algorithm alg = new com.spacekey.algorithm.mck.algorithm.Algorithm();
		Group ans = alg.GKG(keywords, invertedFile);
		
		HashSet<Point> result = new HashSet<Point>();
		for (STObject obj : ans) {
			Point newPoint = new Point(obj.loc.x, obj.loc.y);
			for (String keyword : obj.text) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> mckSKECaplus(HashSet<String> keywords) {
		if (invertedFile == null) {
			System.out.println("Error: mck not yet constructed"); return null;
		}
		com.spacekey.algorithm.mck.algorithm.Algorithm alg = new com.spacekey.algorithm.mck.algorithm.Algorithm();
		Group ans = alg.SKECaplus(keywords, invertedFile);
		
		HashSet<Point> result = new HashSet<Point>();
		for (STObject obj : ans) {
			Point newPoint = new Point(obj.loc.x, obj.loc.y);
			for (String keyword : obj.text) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}

	public HashSet<Point> mckExact(HashSet<String> keywords) {
		if (invertedFile == null) {
			System.out.println("Error: mck not yet constructed"); return null;
		}
		com.spacekey.algorithm.mck.algorithm.Algorithm alg = new com.spacekey.algorithm.mck.algorithm.Algorithm();
		Group ans = alg.Exact(keywords, invertedFile);
		
		HashSet<Point> result = new HashSet<Point>();
		for (STObject obj : ans) {
			Point newPoint = new Point(obj.loc.x, obj.loc.y);
			for (String keyword : obj.text) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}

	public HashSet<Point> minskScaleLune(HashSet<String> keywords) {
		if (invertedFile == null) {
			System.out.println("Error: minsk not yet constructed"); return null;
		}
		com.spacekey.algorithm.minsk.algorithm.Algorithm alg = new com.spacekey.algorithm.minsk.algorithm.Algorithm();
		Group ans = alg.ScaleLunePolar(keywords, invertedFile);
		
		HashSet<Point> result = new HashSet<Point>();
		for (STObject obj : ans) {
			Point newPoint = new Point(obj.loc.x, obj.loc.y);
			for (String keyword : obj.text) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType1Appro(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.Algorithm alg = new com.spacekey.algorithm.coskq.Algorithm();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type1Greedy(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType1Exact(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.Algorithm alg = new com.spacekey.algorithm.coskq.Algorithm();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type1ExactWIndex(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType2Appro(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.AlgorithmDist alg = new com.spacekey.algorithm.coskq.AlgorithmDist();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type2DistAppro(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType2Exact(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.AlgorithmDist alg = new com.spacekey.algorithm.coskq.AlgorithmDist();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type2DistExact(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType3Appro(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.AlgorithmDist alg = new com.spacekey.algorithm.coskq.AlgorithmDist();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type3DistAppro(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<Point> coskqType3Exact(double query_x, double query_y, HashSet<String> keywords) {
		if (coskqRoot == null) {
			System.out.println("Error: coskq not yet constructed"); return null;
		}
		com.spacekey.algorithm.coskq.AlgorithmDist alg = new com.spacekey.algorithm.coskq.AlgorithmDist();
		com.spacekey.algorithm.coskq.pattern.Point		point = new com.spacekey.algorithm.coskq.pattern.Point(0, query_x, query_y);
		com.spacekey.algorithm.coskq.pattern.Pattern 	query = new com.spacekey.algorithm.coskq.pattern.Pattern(point, keywords);
		Pair<Double, HashSet<com.spacekey.algorithm.coskq.pattern.Point>> ans = alg.Type3DistExact(query, coskqRoot);
		
		HashSet<Point> result = new HashSet<Point>();
		for (com.spacekey.algorithm.coskq.pattern.Point obj : ans.getSecond()) {
			Point newPoint = new Point(obj.x, obj.y);
			for (String keyword : obj.keywords) {
				if (keywords.contains(keyword))
					newPoint.addKeyword(keyword);
			}
			result.add(newPoint);
		}
		return result;
	}
	
	public HashSet<HashSet<Point>> spmMSJ(List<Link> linkList) {
		com.spacekey.algorithm.spm.pattern.Pattern pattern = new com.spacekey.algorithm.spm.pattern.Pattern(linkList);
		com.spacekey.algorithm.spm.algorithm.msj.MStarJoin all = new com.spacekey.algorithm.spm.algorithm.msj.MStarJoin(spmRoot);
		List<int[]> rsList = all.query(pattern);
		
		if (rsList != null && rsList.size() > 0){
			HashSet<HashSet<Point>> result = new HashSet<HashSet<Point>>();
			for (int i = 0; i < rsList.size(); i++){
				int record[] = rsList.get(i);
				HashSet<Point> oneResult = new HashSet<Point>();
				for (int j = 0 ; j < record.length ; j++) {
					Point point = new Point(record[j], loc[record[j]][0], loc[record[j]][1]);
					point.addKeywordAll(all.keywordMap[j]);
					oneResult.add(point);
				}
				result.add(oneResult);
			}
			return result;
		} else return new HashSet<HashSet<Point>>();
	}
	
	public ArrayList<HashSet<Point>> spmTopK(List<Link> linkList, int k, double query_x, double query_y) {
		com.spacekey.algorithm.spm.pattern.Pattern pattern = new com.spacekey.algorithm.spm.pattern.Pattern(linkList);
		com.spacekey.algorithm.spm.algorithm.msj.MStarJoin all = new com.spacekey.algorithm.spm.algorithm.msj.MStarJoin(spmRoot);
		List<int[]> rsList = all.query(pattern);
		
		if (rsList != null && rsList.size() > 0) {
			ArrayList<HashSet<Point>> ans = new ArrayList<HashSet<Point>>();
			TreeMap<Double, HashSet<Point>> result = new TreeMap<Double, HashSet<Point>>();
			for (int i = 0 ; i < rsList.size(); i++) {
				int record[] = rsList.get(i);
				HashSet<Point> oneResult = new HashSet<Point>();
				for (int j = 0 ; j < record.length ; j++) {
					Point point = new Point(loc[record[j]][0], loc[record[j]][1]);
					point.addKeywordAll(all.keywordMap[j]);
					oneResult.add(point);
				}
				double temp = Point.maxDistToPoint(query_x, query_y, oneResult);
				result.put(temp, oneResult);
			}
			int count = 0;
			Set<Double> keyset = result.keySet();
			for (double key : keyset) {
				if (count++ > k) break;
				ans.add(result.get(key));
			}
			return ans;
		} else return null;
	}
}