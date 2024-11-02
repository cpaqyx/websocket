package fastwave.cloud.websocket.webstocket;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import javax.websocket.Session;

@ServerEndpoint(value = "/websocket/{userId}")
@Component
public class WebSocketEndpoint {

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId) {
		// 分解获取的参数,把参数信息，放入到session key中, 以方便后续使用
//		String queryString = session.getQueryString();
//		HashMap<String,String> maps = HttpContextUtils.parseQueryString(queryString);
//		String userId = maps.get("userId");
		
		// 把会话存入到连接池中
		this.session = session;
		SessionPool.sessions.put(userId, session);
	}

	/**
	 * 关闭连接
	 */
	@OnClose
	public void onClose(Session session) {
		SessionPool.sessions.remove(session.getId());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端的消息:" + message);
		SessionPool.sendMessage(message);
	}
}
