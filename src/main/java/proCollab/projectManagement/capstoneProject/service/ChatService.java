package proCollab.projectManagement.capstoneProject.service;

import java.util.List;

import proCollab.projectManagement.capstoneProject.model.ChatMessage;
import proCollab.projectManagement.capstoneProject.model.User;

public interface ChatService {
    List<ChatMessage> getChatMessagesBySender(User sender);

    List<ChatMessage> getChatMessagesByRecipient(User recipient);

    void saveChatMessage(ChatMessage message);

    public List<ChatMessage> getChatMessagesBySenderOrRecipient(User user);

    List<ChatMessage> findMessagesBetweenUsers(User user, User recipient);

    List<ChatMessage> getMessagesOfSenderToRecipient(User sender, User recipient);
}
