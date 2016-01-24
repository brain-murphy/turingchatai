package interfaces;

/**
 * Created by brian on 1/24/16.
 */
public interface NetworkConnection {

    void sendMessage(String message, MessageListener listener);

    void listenForMessages(MessageListener listener);

}
