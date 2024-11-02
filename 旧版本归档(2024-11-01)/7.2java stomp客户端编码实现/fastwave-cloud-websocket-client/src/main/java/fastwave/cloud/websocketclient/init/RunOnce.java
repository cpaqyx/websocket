package fastwave.cloud.websocketclient.init;

import fastwave.cloud.websocketclient.client.ChatWebsocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RunOnce implements ApplicationRunner {

    @Autowired
    ChatWebsocketClient chatWebsocketClient;

	@Override
    public void run(ApplicationArguments args) throws Exception {
        while (chatWebsocketClient.getStompSession() == null || !chatWebsocketClient.getStompSession().isConnected()) {
            chatWebsocketClient.connect();// 连接服务器
            try {
                Thread.sleep(3000);// 连接服务器失败的处理 3秒后重新连接
            } catch (Exception e1) {
                System.out.println("启动失败，将继续尝试");
            }
        }
    }
}
