package fastwave.cloud.websocket.chat.config;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;

//ws://localhost:8071/websocket/A
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
		// 把会话存入到连接池中
		SessionPool.sessions.put(userId, session);
	}

	/**
	 * 关闭连接
	 */
	@OnClose
	public void onClose(Session session) throws IOException {
		SessionPool.close(session.getId());
		session.close();
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// 如果是心跳检测的消息，则返回pong作为心跳回应
		if (message.equalsIgnoreCase("ping")) {
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("type", "pong");
				session.getBasicRemote().sendText(JSON.toJSONString(params));
				System.out.println("应答客户端的消息:" + JSON.toJSONString(params));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else
		{
			SessionPool.sendMessage(message);
		}
	}

}
