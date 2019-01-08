package com.spacekey.algorithm.spm.irtree;

/**
 * @author yxfang
 * @date 2016-11-1
 * obtain the height of the IR-tree
 */
public class IRtreeHeight {

	public static int obtainHeight(ANode root){
		if(root instanceof Node){
			ANode firstChild = ((Node) root).getChildList().get(0);
			return 1 + obtainHeight(firstChild);
		}else{
			return 1;
		}
	}
}
