package network;

import com.firebase.client.*;
import network.model.*;

/**
 * This class is just to clean up the code. We only need the onChildAdded event, so
 * this class implements the ChildEventListner interface and then only passes along
 * the child added events to the PartnerJoinedChatListener.
 */
public class PartnerJoiningChatListener implements ChildEventListener {

    private PartnerJoinedChatListener listener;

    public PartnerJoiningChatListener(PartnerJoinedChatListener pListener) {
        listener = pListener;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        listener.onPartnerJoinedChat(dataSnapshot.getKey(), this);
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

    @FunctionalInterface
    public interface PartnerJoinedChatListener {
        void onPartnerJoinedChat(String partnerUid, PartnerJoiningChatListener toRemove);
    }
}
