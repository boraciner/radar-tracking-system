package com.radartracker.trackerservice;

public class TrackedObject {

    public enum TrackState { TENTATIVE, CONFIRMED, DELETED }

    private final long trackId;
    private final KalmanFilter filter;
    private TrackState state = TrackState.TENTATIVE;
    private int hitCount  = 1;
    private int missCount = 0;

    public TrackedObject(long trackId, double initX, double initY) {
        this.trackId = trackId;
        this.filter  = new KalmanFilter(initX, initY);
    }

    public long getTrackId() { return trackId; }

    public KalmanFilter getFilter() { return filter; }

    public TrackState getState() { return state; }
    public void setState(TrackState state) { this.state = state; }

    public int getHitCount() { return hitCount; }
    public void incrementHitCount() { hitCount++; }
    public void resetMissCount() { missCount = 0; }

    public int getMissCount() { return missCount; }
    public void incrementMissCount() { missCount++; }
}
