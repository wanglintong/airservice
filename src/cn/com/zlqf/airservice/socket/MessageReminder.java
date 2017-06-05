package cn.com.zlqf.airservice.socket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import cn.com.zlqf.airservice.entity.Const;

@ServerEndpoint("/messageReminder")
public class MessageReminder {
	
	@OnOpen
	public void open(Session session) {
		System.out.println(session.getId() + " open");
		try {
			session.getBasicRemote().sendText("websocket连接成功");
			Const.session = session;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println(session.getId() + " close");
		try {
			if(session!=null) {
				session.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnMessage
	public void message(Session session,String msg,boolean last) {
		System.out.println("msg = " + msg);
	}
}
