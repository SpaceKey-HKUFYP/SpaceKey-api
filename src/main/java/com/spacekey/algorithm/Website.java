/**
 * 
 */
package com.spacekey.algorithm;

import java.util.HashSet;

import com.spacekey.algorithm.global.DataReader;
import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.docindex.InvertedFile;
import com.spacekey.algorithm.global.typedef.Dataset;
import com.spacekey.algorithm.global.typedef.STObject;

/**
 * @author Budiman
 *
 */
public class Website {
	public static double loc[][] = null;
	public static String kws[][] = null;
	public static com.spacekey.algorithm.coskq.index.Node coskqRoot;
	public static InvertedFile invertedFile;
	public static com.spacekey.algorithm.spm.irtree.Node spmRoot = null;
	
	static{
		System.out.println("Loading the data and build the index ...");
		
		final String locPath="C:\\Users\\Budiman\\Documents\\workspace\\trial\\WebContent\\file\\loc";
		final String docPath="C:\\Users\\Budiman\\Documents\\workspace\\trial\\WebContent\\file\\doc";
		
		DataReader dataReader = new DataReader(locPath, docPath);
		loc = dataReader.readLoc();
		kws = dataReader.readKws();


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
		invertedFile = iv;
		
		// coskq construct
		String indexFile = com.spacekey.algorithm.global.Config.indexUK;
		com.spacekey.algorithm.coskq.index.BuildIRTree builder1 = new com.spacekey.algorithm.coskq.index.BuildIRTree(loc, kws, indexFile);
		coskqRoot = builder1.build();
		
		// spm construct
		com.spacekey.algorithm.spm.irtree.BuildIRTree builder2 = new com.spacekey.algorithm.spm.irtree.BuildIRTree(loc, kws, indexFile);
		spmRoot = builder2.build();
	}
}
