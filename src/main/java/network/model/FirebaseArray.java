package network.model;

import com.firebase.client.*;

import javax.xml.crypto.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Created by brian on 1/30/16.
 */
public class FirebaseArray implements Iterable<DataSnapshot> {
    private Firebase firebaseRef;
    private volatile Map<String, DataSnapshot> dataMap;

    private Function<DataSnapshot, Void> listener;

    public FirebaseArray(Firebase ref) {
        firebaseRef = ref;

        dataMap = new HashMap<>();

        getInitialValues();

        firebaseRef.addChildEventListener(new FirebaseArrayListener());
    }

    public DataSnapshot get(String key) {
        return dataMap.get(key);
    }

    @Override
    public Iterator<DataSnapshot> iterator() {
        return new FirebaseArrayIterator(dataMap);
    }

    public DataSnapshot removeAsync(String key) {
        DataSnapshot removed = dataMap.get(key);
        firebaseRef.child(key).removeValue();
        return removed;
    }

    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    public void setAddedListener(Function<DataSnapshot, Void> callback) {
        listener = callback;
    }

    private void getInitialValues() {
        CountDownLatch latch = new CountDownLatch(1);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataMap.put(child.getKey(), child);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class FirebaseArrayListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
            dataMap.put(dataSnapshot.getKey(), dataSnapshot);
            if (listener != null) {
                listener.apply(dataSnapshot);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            dataMap.put(dataSnapshot.getKey(), dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            dataMap.remove(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //ignore for now
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    private class FirebaseArrayIterator implements Iterator<DataSnapshot> {

        private DataSnapshot[] snapshots;
        private int index;

        private FirebaseArrayIterator(Map<String, DataSnapshot> snapshotMap) {
            snapshots = new DataSnapshot[snapshotMap.keySet().size()];

            Iterator<String> keyIterator = snapshotMap.keySet().iterator();

            for (int i = 0; keyIterator.hasNext(); i++) {
                snapshots[i] = snapshotMap.get(keyIterator.next());
            }

            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < snapshots.length;
        }

        @Override
        public DataSnapshot next() {
            return snapshots[index++];
        }
    }
}
