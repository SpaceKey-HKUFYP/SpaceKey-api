package com.spacekey.util;

public class POI {
	public int id;
	public String name;
	public int numOfReviews;
	public String searchKey;
	public String givenKey;
	public String address;
	public double lat;
	public double lng;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POI other = (POI) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
