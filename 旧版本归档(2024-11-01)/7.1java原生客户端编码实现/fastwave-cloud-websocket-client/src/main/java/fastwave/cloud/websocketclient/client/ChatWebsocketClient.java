package fastwave.cloud.websocketclient.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatWebsocketClient extends WebSocketClient {

    public ChatWebsocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        System.out.println("客户端已连接成功");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println("客户端已连接关闭");
    }

    @Override
    public void onError(Exception arg0) {
        System.out.println("客户端出错啦，消息" + arg0);
    }

    @Override
    public void onMessage(String arg0) {
        System.out.println("接收到服务端数据：" + arg0);
    }
}
