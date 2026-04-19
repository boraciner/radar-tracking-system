package com.radartracker.trackerservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrackManager {

    private static final Logger log = LoggerFactory.getLogger(TrackManager.class);

    private static final double GATE_DISTANCE    = 2.0;
    private static final int    CONFIRM_HITS     = 3;
    private static final int    DELETE_MISSES    = 5;

    private final Map<Long, TrackedObject> tracks = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Track> processMeasurements(List<Plot> plots, long timeStamp) {
        // 1. Predict all active tracks forward
        tracks.values().forEach(obj -> obj.getFilter().predict());

        Set<Long> matched = new HashSet<>();
        List<Plot>  unmatched = new ArrayList<>();

        // 2. Nearest-neighbour association within gate
        for (Plot plot : plots) {
            double bestDist = Double.MAX_VALUE;
            TrackedObject bestTrack = null;

            for (TrackedObject obj : tracks.values()) {
                if (obj.getState() == TrackedObject.TrackState.DELETED) continue;
                if (matched.contains(obj.getTrackId())) continue;

                double dx = plot.getX() - obj.getFilter().getX();
                double dy = plot.getY() - obj.getFilter().getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < bestDist && dist < GATE_DISTANCE) {
                    bestDist  = dist;
                    bestTrack = obj;
                }
            }

            if (bestTrack != null) {
                bestTrack.getFilter().update(plot.getX(), plot.getY());
                bestTrack.incrementHitCount();
                bestTrack.resetMissCount();
                if (bestTrack.getHitCount() >= CONFIRM_HITS) {
                    bestTrack.setState(TrackedObject.TrackState.CONFIRMED);
                }
                matched.add(bestTrack.getTrackId());
            } else {
                unmatched.add(plot);
            }
        }

        // 3. Unmatched tracks accumulate misses
        for (TrackedObject obj : tracks.values()) {
            if (!matched.contains(obj.getTrackId()) &&
                    obj.getState() != TrackedObject.TrackState.DELETED) {
                obj.incrementMissCount();
                if (obj.getMissCount() >= DELETE_MISSES) {
                    obj.setState(TrackedObject.TrackState.DELETED);
                    log.info("Track {} deleted after {} misses", obj.getTrackId(), DELETE_MISSES);
                }
            }
        }

        // 4. Initiate new tentative tracks for unmatched plots
        for (Plot plot : unmatched) {
            long id = nextId.getAndIncrement();
            tracks.put(id, new TrackedObject(id, plot.getX(), plot.getY()));
            log.debug("New tentative track {} at ({}, {})", id, plot.getX(), plot.getY());
        }

        // 5. Clean up deleted tracks
        tracks.entrySet().removeIf(e -> e.getValue().getState() == TrackedObject.TrackState.DELETED);

        // 6. Publish only confirmed tracks
        List<Track> output = new ArrayList<>();
        for (TrackedObject obj : tracks.values()) {
            if (obj.getState() == TrackedObject.TrackState.CONFIRMED) {
                KalmanFilter kf = obj.getFilter();
                output.add(new Track(kf.getX(), kf.getY(), kf.getVx(), kf.getVy(),
                        obj.getTrackId(), timeStamp));
            }
        }
        return output;
    }

    public int activeTrackCount() {
        return tracks.size();
    }
}
