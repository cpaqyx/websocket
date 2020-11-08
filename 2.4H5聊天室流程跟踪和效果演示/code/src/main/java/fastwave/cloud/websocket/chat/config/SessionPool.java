package fastwave.cloud.websocket.chat.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

public class SessionPool {

	public static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public static void close(String sessionId) throws IOException {
		for(String userId : SessionPool.sessions.keySet())
		{
			Session session = SessionPool.sessions.get(userId);
			if(session.getId().equals(sessionId))
			{
				sessions.remove(userId);
				break;
			}
		}
	}

    public static void sendMessage(String message) {
    	for(String sessionId : SessionPool.sessions.keySet())
    	{
    		SessionPool.sessions.get(sessionId).getAsyncRemote().sendText(message);
    	}
    }
}
