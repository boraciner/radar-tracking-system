package com.radartracker.mapviewerservice;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SocketTextHandler extends TextWebSocketHandler {
	int counter = 0;
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {

		String payload = message.getPayload();
		//JSONObject jsonObject = new JSONObject(payload);
		
		
		 
	}

	
	@EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		
		event.
		service.scheduleAtFixedRate(()->{ try {
			session.sendMessage(new TextMessage("Hi " + ++counter + " how may we help you?"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		},0,1,TimeUnit.SECONDS);
		
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
    }
}
