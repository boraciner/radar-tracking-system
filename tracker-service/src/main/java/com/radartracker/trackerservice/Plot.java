package com.radartracker.trackerservice;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Plot {

	private double x;
	private double y;
	private LocalTime timeStamp;
	
	
	
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
	public LocalTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public Plot() {
		super();
	}
	public Plot( double x,  double y, LocalTime timeStamp) {
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
