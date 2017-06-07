package cn.com.zlqf.airservice.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class MessageReminderHandler extends AbstractWebSocketHandler{
	
	private Map<String,WebSocketSession> sessionMap = new HashMap<>();
	
	public void sendMessage(String msg) throws Exception {
		if(sessionMap.size()>0) {
			for(String key:sessionMap.keySet()) {
				sessionMap.get(key).sendMessage(new TextMessage(msg));
			}
		}
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("receive message : " + message.getPayload());
		session.sendMessage(new TextMessage("Hello Client!"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("session" + session.getId() + " close");
		sessionMap.remove(session.getId());
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("session" + session.getId() + " connection established!");
		sessionMap.put(session.getId(),session);
		session.sendMessage(new TextMessage("websocket链接成功"));
	}
}
