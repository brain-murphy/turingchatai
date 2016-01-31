package network;

import com.firebase.client.*;
import network.interfaces.*;
import network.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseDataConnection implements DataConnection {

    public static final String FIREBASE_URL = "https://turingchat.firebaseio.com/";


    private Stack<ChatStartedListener> chatStartedListeners;

    private Firebase waitingListRef;
    private FirebaseArray waitingListArray;
    private Firebase chatsRef;

    private FirebaseAuth auth;

    public FirebaseDataConnection() {
        chatStartedListeners = new Stack<>();

        setUpFirebase();

        auth = new FirebaseAuth();
    }

    @Override
    public void lookForPartner(ChatStartedListener chatStartedListener) {
        chatStartedListeners.push(chatStartedListener);

        getPartnerFromWaitingList(this::startChatAsync);
    }

    private void startChatAsync(String partnerUid) {

        Firebase newChatFirebaseRef = pushNewChatToFirebase();
        Firebase partnersRef = newChatFirebaseRef.child("partners");

        PartnerJoiningChatListener joiningChatListener = new PartnerJoiningChatListener((partnersRecordUid, toRemove) -> {
            if (!partnersRecordUid.equals(auth.getUid())) {
                partnersRef.removeEventListener(toRemove);

                chatStartedListeners.pop().onPartnerFound(new FirebaseConversationManager(newChatFirebaseRef));
            }
        });

        partnersRef.addChildEventListener(joiningChatListener);

        giveChatIdToPartner(newChatFirebaseRef.getKey(), partnerUid, (nullParam) -> {
            System.out.println("Partner joined another chat before chat uid could be written.");

            partnersRef.removeEventListener(joiningChatListener);

            restartLookingForPartner(newChatFirebaseRef);

            return null;
        });

    }

    private void setUpFirebase() {
        Firebase.setDefaultConfig(new Config());

        waitingListRef = new Firebase(FIREBASE_URL + "seeking_partner/");
        waitingListArray = new FirebaseArray(waitingListRef);
        chatsRef = new Firebase(FIREBASE_URL + "chats/");
    }

    private void getPartnerFromWaitingList(Consumer<String> callback) {
        for (DataSnapshot snapshot : waitingListArray) {
            if (!snapshot.getKey().equals(auth.getUid())) {
                callback.accept(snapshot.getKey());
                return;
            }
        }

        waitingListArray.setAddedListener((added) -> {
            if (!added.getKey().equals(auth.getUid())) {
                waitingListArray.setAddedListener(null); //remove listener
                callback.accept(added.getKey());
            }
            return null;
        });
    }

    private Firebase pushNewChatToFirebase() {
        Firebase newChatRef = chatsRef.push();

        newChatRef.setValue(initializeNewChat());

        return newChatRef;
    }

    private void waitForPartnerJoiningChat(Firebase chatRef) {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Firebase partnersRef = chatRef.child("partners");

        PartnerJoiningChatListener joiningChatListener = new PartnerJoiningChatListener((partnerUid, toRemove) -> {
            if (!partnerUid.equals(auth.getUid())) {

                partnersRef.removeEventListener(toRemove);

                countDownLatch.countDown();
            }
        });

        partnersRef.addChildEventListener(joiningChatListener);

        awaitLatchResolution(countDownLatch);
    }

    private void giveChatIdToPartner(String chatId, String partnerId, Function<Void, Void> onFail) {
        Firebase waitingListEntryRef = waitingListRef.child(partnerId);

        waitingListEntryRef.setValue(chatId, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                onFail.apply(null);
            }
        });
    }

    private void restartLookingForPartner(Firebase chatRef) {
        springPartnerListener(chatRef.child("partners"));
        chatRef.removeValue();

        getPartnerFromWaitingList(this::startChatAsync);
    }

    private void springPartnerListener(Firebase partnersRef) {
        partnersRef.push().setValue(new ChatPartner());
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

    private void waitHalfASecond() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
