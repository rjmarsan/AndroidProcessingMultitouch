package com.rj.processing.mt;

public class Point {
	public final float x; 
	public final float y; 
	public long time;

	public Point(final float x, final float y) {
		this.x = x;
		this.y = y;
		time = System.currentTimeMillis();
	}
	
	
	public static float distanceSquared(final Point p1, final Point p2) {
		return p1.x * p2.x + p1.y * p2.y;
	}
	
	public static float distance(final Point p1, final Point p2) {
		return (float)Math.sqrt((p1.x * p2.x + p1.y * p2.y));
	}
}