/**
 * 
 */
package com.spacekey.algorithm.spm.util;

/**
 * @author yxfang
 * longitude -jingdu
 * latitude - weidu
 */
public class LatLgt {

	private static final double PI = 3.14159265358979323;
    private static final double R = 6371229;             
     
    public static double getDistance(double longt1, double lat1, double longt2, double lat2){
        double x,y, distance;
        x=(longt2-longt1)*PI*R*Math.cos( ((lat1+lat2)/2)*PI/180)/180;
        y=(lat2-lat1)*PI*R/180;
        distance=Math.hypot(x,y);
        return distance;
    }
    
    public static double getLongt(double longt1, double lat1, double distance){
        double a = (180*distance)/(PI*R*Math.cos(lat1*PI/180));
        return a;
    }
    
    public static double getLat(double longt1, double lat1, double distance){
        double a = (180*distance)/(PI*R*Math.cos(lat1*PI/180));
        return a;
    }
    
    public static void main(String[] args){
    	LatLgt m = new LatLgt();
    	
    	//NY dataset
//    	double delta = 0.05;
//        double s1 = m.getDistance(-74.01153588667857, 40.7823214234406, -74.01153588667857 + delta, 40.7823214234406);
//        System.out.println(s1);
//        double s2 = m.getDistance(-74.01153588667857, 40.7823214234406, -74.01153588667857, 40.7823214234406 + delta);
//        System.out.println(s2);
    	
    	//UK dataset
//    	double delta = 0.02;
    	double delta = 0.01;
        double s1 = m.getDistance(0.5, 51.3, 0.5 + delta, 51.3);
        System.out.println(s1);
        double s2 = m.getDistance(0.5, 51.3, 0.5, 51.3 + delta);
        System.out.println(s2);
    }
}
