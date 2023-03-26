package com.radartracker.mapviewerservice;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class SocketTextHandler extends TextWebSocketHandler {
	int counter = 0;
	Logger logger = LoggerFactory.getLogger(SocketTextHandler.class);
	static ArrayList<WebSocketSession> activeSessions = new ArrayList<WebSocketSession>();
	
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		session.sendMessage(new TextMessage("Hi  how may we help you?"));	
	}

	@EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
		logger.info("handleSessionConnected called");
		
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		activeSessions.add(session);
		
		logger.info("afterConnectionEstablished called activeSessions.size:"+activeSessions.size());
		session.sendMessage(new TextMessage("afterConnectionEstablished called"));
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		activeSessions.remove(session);
		logger.info("afterConnectionClosed called activeSessions.size:"+activeSessions.size());
		
	}
}
