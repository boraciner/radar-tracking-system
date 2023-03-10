package com.radartracker.trackerservice;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Plot {

	private double x;
	private double y;
	private double vx;
	private double vy;
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
	public double getVx() {
		return vx;
	}
	public void setVx(double vx) {
		this.vx = vx;
	}
	public double getVy() {
		return vy;
	}
	public void setVy(double vy) {
		this.vy = vy;
	}
	public Plot() {
		super();
	}
	public Plot(@JsonProperty double x, @JsonProperty double y, @JsonProperty double vx, @JsonProperty double vy,@JsonProperty LocalTime timeStamp) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.timeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return "Plot [ x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", timeStamp=" + timeStamp
				+ "]";
	}
}
