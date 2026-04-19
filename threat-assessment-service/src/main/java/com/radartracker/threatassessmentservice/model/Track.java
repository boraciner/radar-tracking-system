package com.radartracker.threatassessmentservice.model;

import java.io.Serializable;

public class Track implements Serializable {

    private double x;
    private double y;
    private double vx;
    private double vy;
    private long trackId;
    private long timeStamp;

    public Track() {}

    public double getX()            { return x; }
    public void setX(double x)      { this.x = x; }

    public double getY()            { return y; }
    public void setY(double y)      { this.y = y; }

    public double getVx()           { return vx; }
    public void setVx(double vx)    { this.vx = vx; }

    public double getVy()           { return vy; }
    public void setVy(double vy)    { this.vy = vy; }

    public long getTrackId()                { return trackId; }
    public void setTrackId(long trackId)    { this.trackId = trackId; }

    public long getTimeStamp()                  { return timeStamp; }
    public void setTimeStamp(long timeStamp)    { this.timeStamp = timeStamp; }
}
