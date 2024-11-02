package fastwave.cloud.websocket.webstocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

public class SessionPool {

	public static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

   	public static void close(String sessionId) throws IOException {
   		Session session = sessions.get(sessionId);
   		if(session != null) {
   			sessions.get(sessionId).close();
   		}
   	}

    public static void sendMessage(String sessionId , String message) {
        sessions.get(sessionId).getAsyncRemote().sendText(message);
    }

    public static void sendMessage(String message) {
    	for(String sessionId : SessionPool.sessions.keySet())
    	{
    		SessionPool.sessions.get(sessionId).getAsyncRemote().sendText(message);
    	}
    }

	public static void sendMessageByUserId(String userId, String message) {
		Session session = SessionPool.sessions.get(userId);
		if(session != null && session.isOpen())
		{
			session.getAsyncRemote().sendText(message);
		}
	}

	public static String sendMessage(Map<String, Object> params)
	{
		String msg = params.get("msg").toString();
		String fromUserId = params.get("fromUserId").toString();
		String toUserId = params.get("toUserId");

		String toUserId = params.get("toUserId").toString();
		msg = String.format("来自用户%s发送给用户%s,消息：%s",  fromUserId, toUserId, msg);
		sendMessageByUserId(toUserId, msg);

		return "发送成功";
	}
}
