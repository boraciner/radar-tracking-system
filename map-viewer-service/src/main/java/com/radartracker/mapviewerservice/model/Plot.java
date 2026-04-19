package com.radartracker.mapviewerservice.model;

public class Plot {

    private double x;
    private double y;
    private double trueX;
    private double trueY;
    private long targetId;
    private long timeStamp;

    public Plot() {}

    public Plot(double x, double y, double trueX, double trueY, long targetId, long timeStamp) {
        this.x = x;
        this.y = y;
        this.trueX = trueX;
        this.trueY = trueY;
        this.targetId = targetId;
        this.timeStamp = timeStamp;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getTrueX() { return trueX; }
    public void setTrueX(double trueX) { this.trueX = trueX; }

    public double getTrueY() { return trueY; }
    public void setTrueY(double trueY) { this.trueY = trueY; }

    public long getTargetId() { return targetId; }
    public void setTargetId(long targetId) { this.targetId = targetId; }

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    @Override
    public String toString() {
        return "Plot[targetId=" + targetId + ", x=" + x + ", y=" + y + ", timeStamp=" + timeStamp + "]";
    }
}
