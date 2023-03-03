package com.radartracker.plotlistenerservice;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Plot {

	@Id
	private BigDecimal id;
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
	public Plot(double x, double y, LocalTime timeStamp) {
		super();
		this.x = x;
		this.y = y;
		this.timeStamp = timeStamp;
	}
	public Plot() {
		super();
	}
	
	
	
	
}
