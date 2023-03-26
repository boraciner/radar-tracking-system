package com.radartracker.mapviewerservice;

import java.io.Serializable;
import java.time.LocalTime;

public class Track implements Serializable{
	private double x;
	private double y;
	private double vx;
	private double vy;
	private long trackId;
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
	public long getTrackId() {
		return trackId;
	}
	public void setTrackId(long trackId) {
		this.trackId = trackId;
	}
	public LocalTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Track(double x, double y, double vx, double vy, long trackId, LocalTime timeStamp) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.trackId = trackId;
		this.timeStamp = timeStamp;
	}
	public Track() {
		super();
	}
	@Override
	public String toString() {
		return "Track [x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", trackId=" + trackId + ", timeStamp="
				+ timeStamp + "]";
	}
	
	
}
