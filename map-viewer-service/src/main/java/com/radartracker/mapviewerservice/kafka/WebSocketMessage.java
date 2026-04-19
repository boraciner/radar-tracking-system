package com.radartracker.mapviewerservice.kafka;

public class WebSocketMessage {

    private String type;
    private Object payload;

    public WebSocketMessage(String type, Object payload) {
        this.type    = type;
        this.payload = payload;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
