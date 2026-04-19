package com.radartracker.threatassessmentservice.model;

import org.springframework.stereotype.Component;

@Component
public class AssetPosition {

    private volatile double x = 5.0;
    private volatile double y = 5.0;

    public double getX()        { return x; }
    public void setX(double x)  { this.x = x; }

    public double getY()        { return y; }
    public void setY(double y)  { this.y = y; }
}
