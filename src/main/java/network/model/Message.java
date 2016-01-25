package network.model;

/**
 * Created by brian on 1/24/16.
 */
public class Message {

    public Message() {

    }

    public Message(String pText, String userUid) {
        text = pText;
        user = userUid;
    }
    public String text;
    public String user;
}
