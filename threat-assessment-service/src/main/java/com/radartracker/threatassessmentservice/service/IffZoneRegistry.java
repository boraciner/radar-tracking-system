package com.radartracker.threatassessmentservice.service;

import com.radartracker.threatassessmentservice.model.IffZone;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IffZoneRegistry {

    private final ConcurrentHashMap<String, IffZone> zones = new ConcurrentHashMap<>();

    public IffZone add(IffZone zone) {
        zone.setId(UUID.randomUUID().toString());
        zones.put(zone.getId(), zone);
        return zone;
    }

    public Collection<IffZone> getAll() {
        return zones.values();
    }

    public Optional<IffZone> getById(String id) {
        return Optional.ofNullable(zones.get(id));
    }

    public boolean delete(String id) {
        return zones.remove(id) != null;
    }

    public Optional<IffZone> findContaining(double x, double y) {
        return zones.values().stream()
                .filter(z -> z.contains(x, y))
                .findFirst();
    }
}
