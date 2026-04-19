package com.radartracker.radarservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecm")
@CrossOrigin("*")
public class EcmController {

    @Autowired
    private EcmState ecmState;

    @GetMapping
    public EcmState get() {
        return ecmState;
    }

    @PostMapping
    public EcmState set(@RequestBody EcmState body) {
        if (body.getMode() != null) {
            ecmState.setMode(body.getMode());
        }
        return ecmState;
    }
}
