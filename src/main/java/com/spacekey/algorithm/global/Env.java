package com.spacekey.algorithm.global;

/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class Env {

	public static final String HomeDir = System.getProperty("user.home") + "/workspace/mCK-minSK";

	public static double MaxCoord = 1.0;

	public static final int B = 1024; // block size in bytes

	public static Words W = new Words(); // words class having all the keywords

	public static double Ep = 0.01; // epsilon parameter for SKECa

	public static final double P = 1.5; // p parameter for fast Set Cover
										// algorithm
}
