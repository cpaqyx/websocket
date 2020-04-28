package fastwave.cloud.stomp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebsocketController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/sendToAll")
    public String sendToAll(String msg) {
        return msg;
    }

    @MessageMapping("/send")
    @SendTo("/topic")
    public String say(String msg) {
        return msg;
    }

    @GetMapping("/sendToAllByTemplate")
    @MessageMapping("/sendToAllByTemplate")
    public void sendToAllByTemplate(@RequestParam String msg) {
        template.convertAndSend("/topic", msg);
    }

    @GetMapping("/send")
    public String msgReply(@RequestParam String msg) {
        template.convertAndSend("/topic", msg);
        return msg;
    }
}
