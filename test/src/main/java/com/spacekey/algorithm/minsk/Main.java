package com.spacekey.algorithm.minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import com.spacekey.algorithm.global.Env;
import com.spacekey.algorithm.global.Words;
import com.spacekey.algorithm.global.datastruct.docindex.*;
import com.spacekey.algorithm.global.datastruct.rtree.*;
import com.spacekey.algorithm.global.typedef.*;
import com.spacekey.algorithm.global.util.Debug;
import com.spacekey.algorithm.global.util.Util;
import com.spacekey.algorithm.minsk.algorithm.Algorithm;

/**
 * Main
 * @author Dong-Wan Choi
 * 2015. 8. 24.
 */
public class Main {

	public static void main(String[] args) {
		try {
			System.out.println("* max memory size: " + java.lang.Runtime.getRuntime().maxMemory() / (double)1024 / (double)1024 + "MBs");
			
			Algorithm alg = new Algorithm();
			Env.W = new Words();
			Random r = new Random();
			InvertedFile iv = new InvertedFile();
			HashSet<String> T;
			
			// Initialize dataset
			Dataset db = construct("src/UK.txt");
			
			// load queries
//			 ArrayList<HashSet<String>> queries = loadQueries(Env.HomeDir+"UK_T/UK_T.queries");
			
			// loading objects into the inverted file
			System.out.println("* Indexing Start");
			for (STObject o: db) {
				iv.add(o);
			}
			System.out.println("* Indexing End");
			
			// freqrate: ratio of frequency of each query keyword to n 
			int cnt = 50, l = 6; 
			double freqRate = 0.01; // 

			/* varying |T| */
			for (l = 4; l <= 10; l = l+2) {
//			for (int q = 0; q < queries.size()/cnt; q++) {
			
				/* varying freqRate */
//				double [] rates = new double[]{0.0025, 0.005, 0.02, 0.04};
//				for (int f = 0; f < rates.length; f++) {
//				freqRate = rates[f];
				
				
				System.out.print("M: " + RTree.M + " m: " + RTree.m);
//				System.out.print(" objects: " + db.size() + " nodes: "+rt.nodes+" heights: "+rt.height);
//				System.out.println(" keywords: " + Env.W.size() + " |T|: " + queries.get(q*cnt).size()+"\n");
				System.out.println(" keywords: " + Env.W.size() + " |T|: " + l+"\n");
//				int maxFreq = iv.maxFreq();
//				System.out.println(" Max Freq: " + maxFreq + " Max Freq Rate: " + (double)maxFreq/(double)db.size()+"\n");
//				System.out.println("keywords: " + Env.W.size() + " Freq Rate: " + freqRate+"\n");
				
				long cpuTimeElapsed;
				
				String [] a 	= new String[]{"GKG", "SKECa+", "ScaleLune(w/o PT)", "ScaleLune(PT)", "GreedyMinSK"};
//				String [] a 	= new String[]{"MinLune", "ScaleLune(w/o PT)", "ScaleLune(PT)"};
				double [] c1 	= new double[a.length]; 
				double [] rc1 	= new double[a.length];
				double [] c2 	= new double[a.length];                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
				double [] rc2 	= new double[a.length];
				double [] t 	= new double[a.length];
				Group [] result = new Group[a.length];
				
				double [] n = new double[a.length];
				double [] dia = new double[a.length];
				
				
				for (int i = 0; i < cnt; i++){
					System.out.print(i+" ");
					if (i % 20 == 0 && i > 0) System.out.println();
	
//					T = queries.get(q*cnt+i);
					T = Util.rand(l, Env.W, iv, db, r, freqRate);
//					T = new HashSet<String>(Arrays.asList(new String [] {"Car", "Link", "Crescent", "Londonderry"}));
					Debug._PrintL("T:(" + T.size() + ")" + T);
					Debug._PrintL("Freq Rate: " + freqRate + "\n");
					
	 			
					
					// MinLune without Pruning
//					cpuTimeElapsed = Util.getCpuTime();
//					result[0] = alg.MinLune(T, iv);
//					result[0].shrink(T);
//					cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[0] += cpuTimeElapsed/(double)1000000000;
//					Debug._Print(result[0]);
//					Debug._PrintL("MinLune\t-----------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
					
					// ScaleLuneCartesian algorithm
					cpuTimeElapsed = Util.getCpuTime();
					result[2] = alg.ScaleLuneCartesian(T, iv);
					result[2].shrink(T);
					cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[2] += cpuTimeElapsed / (double)1000000000;
					Debug._Print(result[2]);
					Debug._PrintL("ScaleLuneCartesian\t-----------------------------" + cpuTimeElapsed / (double)1000000000+"\n");
					
					// ScaleLunePolar algorithm
					cpuTimeElapsed = Util.getCpuTime();
					result[3] = alg.ScaleLunePolar(T, iv);
					result[3].shrink(T);
					cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[3] += cpuTimeElapsed / (double)1000000000;
					Debug._Print(result[3]);
					Debug._PrintL("ScaleLunePolar\t-----------------------------" + cpuTimeElapsed / (double)1000000000+"\n");
					
					// GreedyMinSK algorithm
					cpuTimeElapsed = Util.getCpuTime();
					result[4] = alg.GreedyMinSK(T, iv);
					result[4].shrink(T);
					cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[4] += cpuTimeElapsed / (double)1000000000;
					Debug._Print(result[4]);
					Debug._PrintL("GreedyMinSK\t-----------------------------" + cpuTimeElapsed / (double)1000000000+"\n");
					
					
					for (int j = 0; j < result.length; j++) {
						c1[j] += result[j].cost1();
						c2[j] += result[j].cost2();
						rc1[j] += result[j].rcost1();
						rc2[j] += result[j].rcost2();
						n[j] += result[j].size();
						dia[j] += result[j].dia();
						
						if (!result[j].covers(T)) {
							System.err.println("result does not cover T");
							System.err.println(result[j]);
							System.exit(0);
						}
					}
					
					if (result[2].cost1() != result[3].cost1()) {
						System.err.println("Polar returns different result from Cartesian");
						System.err.println(result[2]);
						System.err.println(result[3]);
						System.exit(0);
					}
	
					Debug._Print("\n");
				}
				System.out.println();
//				System.out.format("%-10s%-15s%-15s%-15s\n", "", a[0], a[1], a[2]);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "time avg.", t[0]/cnt, t[1]/cnt, t[2]/cnt);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "cost1 avg.", c1[0]/cnt, c1[1]/cnt, c1[2]/cnt);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "N avg.", n[0]/cnt, n[1]/cnt, n[2]/cnt);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "Dia. avg.", dia[0]/cnt, dia[1]/cnt, dia[2]/cnt);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "cost1 max", c1max[0], c1max[1], c1max[2]);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "cost2 avg.", c2[0]/cnt, c2[1]/cnt, c2[2]/cnt);
//				System.out.format("%-10s%-15f%-15f%-15f\n", "cost2 max", c2max[0], c2max[1], c2max[2]);
				System.out.format("%-12s%-15s%-15s%-15s%-15s%-15s\n", "", a[0], a[1], a[2], a[3], a[4]);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "time avg.", t[0]/cnt, t[1]/cnt, t[2]/cnt, t[3]/cnt, t[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "cost1 avg.", c1[0]/cnt, c1[1]/cnt, c1[2]/cnt, c1[3]/cnt, c1[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "N avg.", n[0]/cnt, n[1]/cnt, n[2]/cnt, n[3]/cnt, n[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "Dia. avg.", dia[0]/cnt, dia[1]/cnt, dia[2]/cnt, dia[3]/cnt, dia[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "rcost1 avg.", rc1[0]/cnt, rc1[1]/cnt, rc1[2]/cnt, rc1[3]/cnt, rc1[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "cost2 avg.", c2[0]/cnt, c2[1]/cnt, c2[2]/cnt, c2[3]/cnt, c2[4]/cnt);
				System.out.format("%-12s%-15f%-15f%-15f%-15f%-15f\n", "rcost2 avg.", rc2[0]/cnt, rc2[1]/cnt, rc2[2]/cnt, rc2[3]/cnt, rc2[4]/cnt);

			} // end of all experiments
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<HashSet<String>> loadQueries(String filename) throws IOException {
		ArrayList<HashSet<String>> queries = new ArrayList<HashSet<String>>();
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
		
		System.out.print("Query Load Start");
		
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			String [] tokens = line.split(",");
			HashSet<String> query = new HashSet<String>();
			for (String word : tokens) {
				query.add(word.trim());
			}
			queries.add(query);
		}
		System.out.print("---Query Load End\n");
		return queries;
	}

	public static Dataset construct(String filename) throws IOException{
		Dataset db = new Dataset();
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
 		int count = 1;  
		System.out.print("DB Load Start");
		
		String[] spChars = {
				"`", "-", "=", ";", "'", "/", "~", "!", "@", 
				"#", "$", "%", "^", "&", "|", ":", "<", ">", 
				"\\", "*", "+", "{", "}", "?", ".",	",", "the",
				"The", "of", "(", ")", "]", "[", "\"" 
		};
		
		final HashSet<String> symbols = new HashSet<String>();
		symbols.addAll(Arrays.asList(spChars));
		
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			String [] tokens = line.split("\t");
			double x = Double.parseDouble(tokens[0]);
			double y = Double.parseDouble(tokens[1]);
			HashSet<String> text = new HashSet<String>();
			String [] keywords = tokens[2].split(" ");
			for (String tag: keywords)
			{
				if (symbols.contains(tag) || tag.length() == 0) // remove extra symbols
					continue;

				String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
				tag = tag.replaceAll(match, "");
				if (!"".equals(tag.trim()))
					text.add(tag.trim()); 
			}
			STObject obj= new STObject(count, x, y, text);
			db.add(obj);		
			Env.W.add(obj);
			
			count ++; 
		}
		
		System.out.print("---DB Load End\n");
		in.close();
		
		return db;

	}
}