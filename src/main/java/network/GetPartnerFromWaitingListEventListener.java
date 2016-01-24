package network;

import com.firebase.client.*;

/**
 * Created by brian on 1/24/16.
 */
class GetPartnerFromWaitingListEventListener implements ChildEventListener {

    interface PartnerFoundListener {
        void onPartnerFound(String parterUid, GetPartnerFromWaitingListEventListener toRemove);
    }


    private PartnerFoundListener listener;

    public GetPartnerFromWaitingListEventListener(PartnerFoundListener pListener) {
        listener = pListener;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
        String partnerUid = dataSnapshot.getKey();
        listener.onPartnerFound(partnerUid, this);
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
