package com.radartracker.mapviewerservice.model;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Plot {

	private double x;
	private double y;
	private long timeStamp;
	
	
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public Plot() {
		super();
	}
	public Plot( double x,  double y, long timeStamp) {
		super();
		this.x = x;
		this.y = y;
		this.timeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return "Plot [ x=" + x + ", y=" + y + ", timeStamp=" + timeStamp
				+ "]";
	}
}
