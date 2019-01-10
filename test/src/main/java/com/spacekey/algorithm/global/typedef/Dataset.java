/**
 * Dataset.java, 2014. 9. 17.
 */
package com.spacekey.algorithm.global.typedef;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class Dataset extends Group{
	public Dataset()
	{
		super(); 
	}
	
	public Dataset(Collection<? extends STObject> c) {
		super(c);
	}

	public Dataset(STObject [] objs)
	{
		super();
		super.addAll(Arrays.asList(objs));
	}
	
	public STObject get(int i) {
		return super.g.get(i);
	}
}
