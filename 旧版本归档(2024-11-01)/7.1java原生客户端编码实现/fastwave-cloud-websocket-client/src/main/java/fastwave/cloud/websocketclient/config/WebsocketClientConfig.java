package fastwave.cloud.websocketclient.config;

import fastwave.cloud.websocketclient.client.ChatWebsocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class WebsocketClientConfig {

    @Value("${websocket.chat.endpoint}")
    String chatEndpoint;

    @Bean
    public ChatWebsocketClient webSocketClient() {
        try {
            String userId = "100";
            ChatWebsocketClient webSocketClient = new ChatWebsocketClient(new URI(chatEndpoint + userId));
            webSocketClient.connect();
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
