package com.example.webchat.websocket;

import com.example.webchat.service.OnlineUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.util.Map;

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OnlineUsersService onlineUsersService;

    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate,
                               OnlineUsersService onlineUsersService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.onlineUsersService = onlineUsersService;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Sending message: " + message);
        simpMessagingTemplate.convertAndSend("/topic/all/messages", message);

        if (Action.JOINED.equals(message.action())) {
            String userDestination = String.format("/topic/%s/messages", message.user().id());
            onlineUsersService.getOnlineUsers().forEach(onlineUser -> {
                Message newMessage = new Message(onlineUser, null, Action.JOINED, null);
                simpMessagingTemplate.convertAndSend(userDestination, newMessage);
            });

            headerAccessor.getSessionAttributes().put("user", message.user());
            onlineUsersService.addOnlineUser(message.user());
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            log.error("Unable to get the user as headerAccessor.getSessionAttributes() is null");
            return;
        }

        User user = (User) sessionAttributes.get("user");
        if (user == null) {
            return;
        }
        onlineUsersService.removeOnlineUser(user);

        Message message = new Message(user, "", Action.LEFT, Instant.now());
        simpMessagingTemplate.convertAndSend("/topic/all/messages", message);
    }
}