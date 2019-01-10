package com.spacekey.algorithm.spm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spacekey.algorithm.Link;
import com.spacekey.algorithm.global.Config;
import com.spacekey.algorithm.spm.pattern.Pattern;


/**
 * @author yxfang
 * @date 2016-8-19
 */
public class T {
	
	public static void main(String[] args) {
		double a[] = {0.1, 0.15, 0.2, 0.25, 0.3};
		for(int i = 0;i < a.length;i ++){
			for(int j = 0;j < a.length;j ++){
				int m = (int)((-8.0 / a[i]) * Math.log(a[j]));
				System.out.println(a[i] + "\t" + a[j] + "\t" + m);
			}
		}
	}
}
