
package com.spacekey.algorithm.global.typedef;

import java.util.Comparator;
import java.util.HashSet;


/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class STObject {

	public int id;
	public Point loc; // original location
	public HashSet<String> text;
	
	public boolean checked = false;
	
	public STObject(int i, double x, double y)
	{
		id = i;
		loc = new Point(x, y);
		text = new HashSet<String>();
	}
	
	public STObject(int i, double x, double y, HashSet<String> argStr)
	{
		id = i;
		loc = new Point(x, y);
		text = new HashSet<String>();
		text.addAll(argStr);
	}
	
//	public STObject(STObject obj, double x, double y)
//	{
//		loc = new Point(obj.loc.x, obj.loc.y);
//		text = new HashSet<String>();
//		text.addAll(obj.text);
//	}
//	
//	
//	public STObject(STObject obj) 
//	{
//		this.loc = new Point(obj.loc.x, obj.loc.y);
//		this.text = new HashSet<String>();
//		this.text.addAll(obj.text);
//	}
	
	public int interCnt(HashSet<String> T) {
		HashSet<String> tmp = new HashSet<String>(T);
		tmp.retainAll(text);

		return tmp.size();
	}
	
	public int diffCnt(HashSet<String> T) {
		return text.size() - interCnt(T);
	}
	


	public String toString()
	{
//		return id+"" ;
		return id + loc.toString()+"["+text.size()+"]";
//		return id + loc.toString()+"["+text.size()+"]"+text.toString();
//		return id + "-->" + loc.toString() + " " + text.toString();
	}
	
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return (id == ((STObject)obj).id);
	}

	public int compareLoc(STObject o) {
		if (loc.x == o.loc.x && loc.y != o.loc.y) return (loc.y - o.loc.y > 0? 1 : -1);
		else if (loc.x != o.loc.x) return (loc.x - o.loc.x>0? 1: -1);
		else return 0;
	} 
	
	public static Comparator<STObject> CompareLoc = new Comparator<STObject>() {
		public int compare(STObject o1, STObject o2) {
			if (o1.loc.x == o2.loc.x && o1.loc.y != o2.loc.y) return (o1.loc.y - o2.loc.y > 0? 1 : -1);
			else if (o1.loc.x != o2.loc.x) return (o1.loc.x - o2.loc.x>0? 1: -1);
			else return (o1.id - o2.id);
		}
	};

}