package fastwave.cloud.websocketclient.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ChatStompSessionHandler extends StompSessionHandlerAdapter{

    @Autowired
    ChatWebsocketClient chatWebsocketClient;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("接收订阅消息=" + (String) payload);
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable exception) {
        System.out.println(exception.getMessage());
        try {
            Thread.sleep(3000);
            chatWebsocketClient.connect();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
