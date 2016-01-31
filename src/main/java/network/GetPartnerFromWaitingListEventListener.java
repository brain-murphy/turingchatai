package network;

import com.firebase.client.*;

/**
 * Created by brian on 1/24/16.
 */
class GetPartnerFromWaitingListEventListener implements ChildEventListener {

    interface PartnerFoundOnWaitingList {
        void onPartnerFound(String parterUid, GetPartnerFromWaitingListEventListener toRemove);
    }

    private static int nextId = 0;
    private int id = nextId++;

    private PartnerFoundOnWaitingList listener;

    public GetPartnerFromWaitingListEventListener(PartnerFoundOnWaitingList pListener) {
        listener = pListener;
        System.out.println("newInstance");
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
//        if (dataSnapshot.getValue().equals(false)) {
        System.out.println("onChildAdded");
            String partnerUid = dataSnapshot.getKey();
            listener.onPartnerFound(partnerUid, this);
//        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        System.out.println("onChildChanged");
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {System.out.println("onChildRemoved");}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {System.out.println("onChildMoved");}

    @Override
    public void onCancelled(FirebaseError firebaseError) {System.out.println("onCancelled");}

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GetPartnerFromWaitingListEventListener && ((GetPartnerFromWaitingListEventListener)obj).id == id;
    }
}
