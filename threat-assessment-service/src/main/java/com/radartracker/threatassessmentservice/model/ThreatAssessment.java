package com.radartracker.threatassessmentservice.model;

public class ThreatAssessment {

    private long trackId;
    private ThreatLevel level;
    private double threatScore;
    private double closingSpeed;
    private double distanceToAsset;
    private String iffZoneId;
    private ZoneType zoneType;
    private long timestamp;

    public ThreatAssessment() {}

    public long getTrackId()                    { return trackId; }
    public void setTrackId(long trackId)        { this.trackId = trackId; }

    public ThreatLevel getLevel()               { return level; }
    public void setLevel(ThreatLevel level)     { this.level = level; }

    public double getThreatScore()                  { return threatScore; }
    public void setThreatScore(double threatScore)  { this.threatScore = threatScore; }

    public double getClosingSpeed()                     { return closingSpeed; }
    public void setClosingSpeed(double closingSpeed)    { this.closingSpeed = closingSpeed; }

    public double getDistanceToAsset()                          { return distanceToAsset; }
    public void setDistanceToAsset(double distanceToAsset)      { this.distanceToAsset = distanceToAsset; }

    public String getIffZoneId()                { return iffZoneId; }
    public void setIffZoneId(String iffZoneId)  { this.iffZoneId = iffZoneId; }

    public ZoneType getZoneType()               { return zoneType; }
    public void setZoneType(ZoneType zoneType)  { this.zoneType = zoneType; }

    public long getTimestamp()                  { return timestamp; }
    public void setTimestamp(long timestamp)    { this.timestamp = timestamp; }
}
