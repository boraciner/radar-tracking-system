package com.radartracker.radarservice;

import org.springframework.stereotype.Component;

@Component
public class EcmState {

    private volatile EcmMode mode = EcmMode.NONE;

    public EcmMode getMode()            { return mode; }
    public void setMode(EcmMode mode)   { this.mode = mode; }
}
