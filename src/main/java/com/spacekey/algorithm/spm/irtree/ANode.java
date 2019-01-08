package com.spacekey.algorithm.spm.irtree;

/**
 * @author yxfang
 * @date 2016-8-8
 * The abstract node for internal nodes and leaf nodes
 */
public abstract class ANode {
	protected MBR mbr = null;//the MBR of this internal node
	
	public MBR getMbr() {
		return mbr;
	}

	public void setMbr(MBR mbr) {
		this.mbr = mbr;
	}
	
}
