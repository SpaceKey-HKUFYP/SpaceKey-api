package com.spacekey.util;

public class Property {
	public int id;
	public String type;
	public int price;
	public int rent;
	public int bedrooms;
	public int grossArea;
	public int saleableArea;
	public String floor;
	public String address;
	public String postDate;
	public double lat;
	public double lng;
	public String title;
	public String region;
	public String propertyName;
	public String description;
	public String contact;
	public String phoneNum;
	public String imageURL;
	public String pageURL;
	public String agentName;
	
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
		Property other = (Property) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
