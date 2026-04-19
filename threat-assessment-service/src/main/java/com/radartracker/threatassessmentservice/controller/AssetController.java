package com.radartracker.threatassessmentservice.controller;

import com.radartracker.threatassessmentservice.model.AssetPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
@CrossOrigin("*")
public class AssetController {

    @Autowired
    private AssetPosition asset;

    @GetMapping
    public AssetPosition get() {
        return asset;
    }

    @PostMapping
    public AssetPosition set(@RequestBody AssetPosition body) {
        asset.setX(body.getX());
        asset.setY(body.getY());
        return asset;
    }
}
