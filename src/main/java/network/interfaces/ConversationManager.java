package network.interfaces;

/**
 * Created by brian on 1/24/16.
 */
public interface ConversationManager {

    void sendMessage(String message);

    void listenForMessages(MessageListener listener);
}
