package proCollab.projectManagement.capstoneProject.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import proCollab.projectManagement.capstoneProject.model.ChatMessage;
import proCollab.projectManagement.capstoneProject.model.MessageDto;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.service.ChatService;
import proCollab.projectManagement.capstoneProject.service.UserService;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public MessageDto sendMessage(MessageDto message) {
        ChatMessage message2 = new ChatMessage();
        User sender = userService.getUserById(Long.parseLong(message.getSender()));
        User recipient = userService.getUserById(Long.parseLong(message.getRecipient()));
        System.out.println(sender.getName());
        message2.setContent(message.getContent());
        message2.setSender(sender);
        message2.setRecipient(recipient);
        message2.setTimestamp(LocalDateTime.now());
        chatService.saveChatMessage(message2);
        return message;
    }
}
