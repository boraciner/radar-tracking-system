package com.radartracker.threatassessmentservice.service;

import com.radartracker.threatassessmentservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThreatAssessor {

    private static final double MAX_RANGE = Math.sqrt(200); // diagonal of 10×10 space ≈ 14.14

    @Autowired private IffZoneRegistry zoneRegistry;
    @Autowired private AssetPosition   asset;

    public ThreatAssessment assess(Track track) {
        ThreatAssessment ta = new ThreatAssessment();
        ta.setTrackId(track.getTrackId());
        ta.setTimestamp(System.currentTimeMillis());

        double dx   = track.getX() - asset.getX();
        double dy   = track.getY() - asset.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        ta.setDistanceToAsset(dist);

        // Radial closing speed — positive means track is approaching asset
        double closing = dist > 0.01
                ? -(track.getVx() * dx + track.getVy() * dy) / dist
                : 0.0;
        ta.setClosingSpeed(closing);

        // IFF zone overrides scoring
        Optional<IffZone> zone = zoneRegistry.findContaining(track.getX(), track.getY());
        if (zone.isPresent()) {
            ta.setIffZoneId(zone.get().getId());
            ta.setZoneType(zone.get().getType());
            switch (zone.get().getType()) {
                case FRIENDLY   -> { ta.setThreatScore(0.0); ta.setLevel(ThreatLevel.FRIENDLY); }
                case HOSTILE    -> { ta.setThreatScore(1.0); ta.setLevel(ThreatLevel.HOSTILE); }
                case RESTRICTED -> { ta.setThreatScore(computeScore(dist, closing)); ta.setLevel(ThreatLevel.SUSPECT); }
            }
            return ta;
        }

        double score = computeScore(dist, closing);
        ta.setThreatScore(score);
        ta.setLevel(levelFromScore(score));
        return ta;
    }

    private double computeScore(double dist, double closing) {
        double distComponent    = 1.0 - Math.min(dist / MAX_RANGE, 1.0);
        double closingComponent = closing > 0 ? Math.min(closing / 3.0, 1.0) : 0.0;
        double score = closing >= 0
                ? 0.4 * distComponent + 0.6 * closingComponent
                : 0.3 * distComponent;
        return Math.max(0.0, Math.min(score, 1.0));
    }

    private ThreatLevel levelFromScore(double score) {
        if (score < 0.20) return ThreatLevel.NEUTRAL;
        if (score < 0.40) return ThreatLevel.UNKNOWN;
        if (score < 0.65) return ThreatLevel.SUSPECT;
        return ThreatLevel.HOSTILE;
    }
}
