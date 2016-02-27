package network;

import com.firebase.client.*;
import network.interfaces.*;
import network.model.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseConversationManager implements ConversationManager {

    private String myUid;
    private Firebase chatRef;
    private Firebase messagesRef;
    private MessageListener messageListener;

    int messageCount;

    public FirebaseConversationManager(Firebase pChatRef) {
        chatRef = pChatRef;
        messagesRef = chatRef.child("messages");

        myUid = messagesRef.getAuth().getUid();

    }

    @Override
    public void sendMessage(String messageText) {
        Message messageObject = new Message(messageText, myUid);

        messagesRef.push().setValue(messageObject);
    }

    @Override
    public void listenForMessages(MessageListener listener) {
        messageListener = listener;
        setFirebaseListener();
    }

    @Override
    public void addOnChatEndedListener(ChatEndedListener listener) {
        throw new UnsupportedOperationException();
    }

    private void forwardPartnerMessageToListener(Message message) {
        if (!message.user.equals(myUid)) {
            messageListener.onMessageReceived(message.text, this);
        }
    }

    private void setFirebaseListener() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String pastChildKey) {
                forwardPartnerMessageToListener(dataSnapshot.getValue(Message.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
