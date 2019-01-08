package com.spacekey.algorithm.global.datastruct.polartree;

/**
 * Object RBTree : RedBlack Tree Class
 * @author  Dong-Wan Choi
 * from my undergraduate homework
 */
public class RBNode 
{
	public RBItem item;
	public RBNode leftChild;
	public RBNode rightChild;
	public RBNode parent;
	public int color;
	private static int cnt = 0;
	private static int height = 0;
	
	public RBNode( RBNode parent, int color )
	{
		this.item = null;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = parent;
		this.color = color;
	}


	/**
	 * This method updates item information due to restructuring the tree.
	 */
	public void updateItem()
	{
		if (leftChild == null && rightChild == null) {
			item.update(null, null);
		} else if (leftChild == null) {
			item.update(null, rightChild.item);
		} else if (rightChild == null) {
			item.update(leftChild.item, null);
		} else {
			item.update(leftChild.item, rightChild.item);
		}
	}
	
	/**
	 * print out all the items having the same key
	 * @param key, key value to be searched for
	 */
	public void printEqualItem( double key )
	{
		if ( this.item == null ) 
			return; // return if this is external node
		
		if ( key == this.item.getKey() )
			System.out.println( this.item.toString() + " (" + Integer.toString(height) + ") " );
		
		if ( this.rightChild != null )
		{
			height++; 
			rightChild.printEqualItem(key);
			height--; 
		}
		if ( this.leftChild != null )
		{
			height++;
			leftChild.printEqualItem(key);
			height--;
		}
	}
	
	/**
	 * @return the sibling node if exists. Otherwise, return null
	 */
	public RBNode getSibling()
	{
		if ( this.parent == null )
			return null;
		if ( this.parent.leftChild == this )
			return this.parent.rightChild;
		else if ( this.parent.rightChild == this )
			return this.parent.leftChild;
		return null;		
	}
	
	/**
	 * @param seq, rank value
	 * @return the node whose rank equals seq
	 */
	public RBNode getRankNode( int rank )
	{
		RBNode tmp = null;
		
		if ( this.item != null )
		{
			tmp = this.leftChild.getRankNode( rank );
			if ( tmp != null )
				return tmp;
			if ( cnt == rank )
			{
				cnt = 0;
				return this;
			}
			cnt++;
			tmp = this.rightChild.getRankNode( rank );
			if ( tmp != null )
				return tmp;
		}
		return null;
	}
	public int getColor() 
	{
		return color;
	}
	public void setColor(int color) 
	{
		this.color = color;
	}
	
	public String toString()
	{
		String [] colorStr = new String[]{ "Red", "Black" };
		
		if ( item == null )
			return "External Node" + "(" + colorStr[this.color] + ")";
		else 
		{
			String nodeInfo = item.toString() + "(" + colorStr[this.color] + ")";
			
			return nodeInfo;
		}
	}
}
