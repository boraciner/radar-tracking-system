package com.radartracker.threatassessmentservice.model;

public class IffZone {

    private String id;
    private String name;
    private double centerX;
    private double centerY;
    private double radius;
    private ZoneType type;

    public IffZone() {}

    public IffZone(String id, String name, double centerX, double centerY, double radius, ZoneType type) {
        this.id = id;
        this.name = name;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.type = type;
    }

    public boolean contains(double x, double y) {
        double dx = x - centerX;
        double dy = y - centerY;
        return Math.sqrt(dx * dx + dy * dy) <= radius;
    }

    public String getId()           { return id; }
    public void setId(String id)    { this.id = id; }

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }

    public double getCenterX()              { return centerX; }
    public void setCenterX(double centerX)  { this.centerX = centerX; }

    public double getCenterY()              { return centerY; }
    public void setCenterY(double centerY)  { this.centerY = centerY; }

    public double getRadius()               { return radius; }
    public void setRadius(double radius)    { this.radius = radius; }

    public ZoneType getType()               { return type; }
    public void setType(ZoneType type)      { this.type = type; }
}
