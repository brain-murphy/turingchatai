package network;

import com.firebase.client.*;
import network.interfaces.*;
import network.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseDataConnection implements DataConnectionManager {

    public static final String FIREBASE_URL = "https://turingchat.firebaseio.com/";


    private Stack<ChatStartedListener> chatStartedListeners;

    private Firebase waitingListRef;
    private Firebase chatsRef;

    private FirebaseAuth auth;

    public FirebaseDataConnection() {
        chatStartedListeners = new Stack<>();

        setUpFirebase();

        auth = new FirebaseAuth();
    }

    @Override
    public void lookForPartner(ChatStartedListener listener) {
        chatStartedListeners.push(listener);

        String partnerUid = getPartnerFromWaitingList();

        Firebase newChatFirebaseRef = pushNewChatToFirebase();

        listenForPartnerJoiningChat(newChatFirebaseRef);

        giveChatIdToPartner(newChatFirebaseRef.getKey(), partnerUid);
    }

    private void setUpFirebase() {
        Firebase.setDefaultConfig(new Config());

        waitingListRef = new Firebase(FIREBASE_URL + "seeking_partner/");
        chatsRef = new Firebase(FIREBASE_URL + "chats/");
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

    private Firebase pushNewChatToFirebase() {
        Firebase newChatRef = chatsRef.push();

        newChatRef.setValue(initializeNewChat());

        return newChatRef;
    }

    private void listenForPartnerJoiningChat(Firebase chatRef) {
        Firebase partnersRef = chatRef.child("partners");

        partnersRef.addChildEventListener(new PartnerJoiningChatListener((partnerUid, toRemove) -> {
            if (!partnerUid.equals(auth.getUid())) {

                chatStartedListeners.pop().onPartnerFound(new FirebaseConversationManager(chatRef));

                partnersRef.removeEventListener(toRemove);
            }
        }));
    }

    private void giveChatIdToPartner(String chatId, String partnerId) {
        Firebase waitingListEntryRef = waitingListRef.child(partnerId);

        waitingListEntryRef.setValue(chatId);
    }

    private Chat initializeNewChat() {
        Chat chat = new Chat();
        chat.partners.put(auth.getUid(), new ChatPartner());
        return chat;
    }

    private void awaitLatchResolution(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
