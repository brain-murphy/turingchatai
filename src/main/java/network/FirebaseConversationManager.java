package network;

import com.firebase.client.*;
import network.interfaces.*;
import network.model.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseConversationManager implements ConversationManager {

    public static final int MAX_MESSAGES = 12;
    private String myUid;
    private Firebase chatRef;
    private Firebase messagesRef;
    private Firebase partnerLeftRef;
    private MessageListener messageListener;
    private ChatEndedListener chatEndedListener;
    private int messageCount;

    public FirebaseConversationManager(Firebase pChatRef) {
        chatRef = pChatRef;
        messagesRef = chatRef.child("messages");
        partnerLeftRef = chatRef.child("someone_left");

        myUid = messagesRef.getAuth().getUid();

        messageCount = 0;
    }

    @Override
    public void sendMessage(String messageText) {
        Message messageObject = new Message(messageText, myUid);

        messagesRef.push().setValue(messageObject);
    }

    @Override
    public void listenForMessages(MessageListener listener) {
        messageListener = listener;
        setFirebaseMessageListener();
    }

    @Override
    public void addOnChatEndedListener(ChatEndedListener listener) {
        chatEndedListener = listener;
        setPartnerLeftListener();
    }

    private void forwardPartnerMessageToListener(Message message) {
        if (!message.user.equals(myUid)) {
            messageListener.onMessageReceived(message.text, this);
        }
    }

    private void setFirebaseMessageListener() {
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String pastChildKey) {
                forwardPartnerMessageToListener(dataSnapshot.getValue(Message.class));

                messageCount++;
                checkMessageLimit();
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

    private void checkMessageLimit() {
        if (messageCount >= MAX_MESSAGES) {
            chatEndedListener.onChatEnded();
        }
    }

    private void setPartnerLeftListener() {
        partnerLeftRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue(Boolean.class)) {
                    chatEndedListener.onChatEnded();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
