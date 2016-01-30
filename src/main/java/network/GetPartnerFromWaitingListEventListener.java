package network;

import com.firebase.client.*;

/**
 * Created by brian on 1/24/16.
 */
class GetPartnerFromWaitingListEventListener implements ChildEventListener {

    interface PartnerFoundOnWaitingList {
        void onPartnerFound(String parterUid, GetPartnerFromWaitingListEventListener toRemove);
    }


    private PartnerFoundOnWaitingList listener;

    public GetPartnerFromWaitingListEventListener(PartnerFoundOnWaitingList pListener) {
        listener = pListener;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
        if (dataSnapshot.getValue().equals(false)) {
            String partnerUid = dataSnapshot.getKey();
            listener.onPartnerFound(partnerUid, this);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}
