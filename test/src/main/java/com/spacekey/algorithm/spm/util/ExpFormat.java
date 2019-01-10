package com.spacekey.algorithm.spm.util;

/**
 * @author yxfang
 * @date 2016-12-19
 * Format the experimental results
 */
public class ExpFormat {
	
	public static String format(double value){
		java.text.DecimalFormat df =new java.text.DecimalFormat("#.000"); 
		return df.format(value);
	}
}
