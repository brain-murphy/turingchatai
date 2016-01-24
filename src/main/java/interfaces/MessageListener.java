package interfaces;

/**
 * Created by brian on 1/24/16.
 */
public interface MessageListener {
    void onMessageReceived(String message);
    boolean equals(Object obj);
    int hashCode();
}
