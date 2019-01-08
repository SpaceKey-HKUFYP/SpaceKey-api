package com.spacekey.algorithm.spm.util;

import com.spacekey.algorithm.spm.irtree.MBR;

/**
 * @author yxfang
 * @date 2016-8-21
 */
public class MBRContain {
	
	public boolean contains(MBR mbr, double x, double y){
		if(x >= mbr.getMinX() && x <= mbr.getMaxX()
				&& y >= mbr.getMinY() && y <= mbr.getMaxY()){
			return true;
		}else{
			return false;
		}
	}

}
