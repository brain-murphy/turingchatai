package network;

import com.firebase.client.*;
import network.interfaces.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseConnection implements NetworkConnection {

    private static final String FIREBASE_URL = "https://turingchat.firebaseio.com/";

    private Map<MessageListener, Conversation> conversationMappings;

    private Firebase waitingListRef;

    public FirebaseConnection() {
        conversationMappings = new HashMap<>();

        waitingListRef = new Firebase(FIREBASE_URL + "seeking_partner/");
    }

    @Override
    public void sendMessage(String message, MessageListener listener) {

    }

    @Override
    public void listenForMessages(MessageListener listener) {
        if (isListenerRegistered(listener)) {
            throw new IllegalArgumentException("Listener is already registered.");
        }

        lookForPartner(listener);
    }

    private void lookForPartner(MessageListener listener) {
        String partnerUid = getPartnerFromWaitingList();

        String chatUid = pushNewChatToFirebase();
    }

    private String getPartnerFromWaitingList() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<String> partnerUidAtomicRef = new AtomicReference<>();

        GetPartnerFromWaitingListEventListener listener = new GetPartnerFromWaitingListEventListener((partnerId, toRemove) -> {
            waitingListRef.removeEventListener(toRemove);
            partnerUidAtomicRef.set(partnerId);

            countDownLatch.countDown();
        });

        waitingListRef.addChildEventListener(listener);

        awaitLatchResolution(countDownLatch);
        return partnerUidAtomicRef.get();
    }

    private String pushNewChatToFirebase() {
        return null;
    }

    private boolean isListenerRegistered(MessageListener listener) {
        return conversationMappings.containsKey(listener);
    }

    private void awaitLatchResolution(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
