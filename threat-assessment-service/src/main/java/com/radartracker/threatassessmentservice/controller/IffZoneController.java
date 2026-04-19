package com.radartracker.threatassessmentservice.controller;

import com.radartracker.threatassessmentservice.model.IffZone;
import com.radartracker.threatassessmentservice.service.IffZoneRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/iff-zones")
@CrossOrigin("*")
public class IffZoneController {

    @Autowired
    private IffZoneRegistry registry;

    @GetMapping
    public Collection<IffZone> getAll() {
        return registry.getAll();
    }

    @PostMapping
    public IffZone create(@RequestBody IffZone zone) {
        return registry.add(zone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return registry.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
