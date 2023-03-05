package com.radartracker.plotlistenerservice;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Plot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public Plot(double x, double y, double vx, double vy, LocalTime timeStamp) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.timeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return "Plot [id=" + id + ", x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", timeStamp=" + timeStamp
				+ "]";
	}
}
