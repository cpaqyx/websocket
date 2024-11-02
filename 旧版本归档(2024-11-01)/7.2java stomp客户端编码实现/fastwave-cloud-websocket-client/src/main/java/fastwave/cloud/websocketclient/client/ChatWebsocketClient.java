package fastwave.cloud.websocketclient.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatWebsocketClient {

    @Value("${websocket.chat.endpoint}")
    String chatEndpoint;

    // 定义全局变量，代表一个session
    private static StompSession stompSession;

    public StompSession getStompSession()
    {
        return stompSession;
    }

    String userId = "100";

    public void connect() {// 定义连接函数
        if (stompSession == null || !stompSession.isConnected()) {
            System.out.println("连接中...");
            List<Transport> transports = new ArrayList<>();
            transports.add(new WebSocketTransport(new StandardWebSocketClient()));
            SockJsClient sockJsClient = new SockJsClient(transports);
            WebSocketStompClient webSocketStompClient = new WebSocketStompClient(sockJsClient);
            webSocketStompClient.setMessageConverter(new StringMessageConverter());
            webSocketStompClient.setDefaultHeartbeat(new long[] { 20000, 0 });
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.afterPropertiesSet();
            webSocketStompClient.setTaskScheduler(taskScheduler);
            WebSocketHttpHeaders webSocketHttpHeaders = null;
            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.add("token", "token");

            StompSessionHandler chatStompSessionHandler = new ChatStompSessionHandler();
            try {
                ListenableFuture<StompSession> future = webSocketStompClient.connect(chatEndpoint, webSocketHttpHeaders,
                        stompHeaders, chatStompSessionHandler);
                stompSession = future.get();
                stompSession.setAutoReceipt(true);
                stompSession.subscribe("/queue/" + userId + "/topic", chatStompSessionHandler);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("连接成功");
        } else {
            System.out.println("已连接状态");
        }
    }

    public String sendMessage(String msg)
    {
        stompSession.send("/app/sendToUser", msg.getBytes());
        return "已发送";
    }
}
