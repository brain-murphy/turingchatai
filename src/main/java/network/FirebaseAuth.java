package network;

import com.firebase.client.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Created by brian on 1/24/16.
 */
public class FirebaseAuth {

    private String email;
    private String password;
    private AtomicReference<String> uid;

    private Firebase firebaseRef;

    public FirebaseAuth() {
        email = getRandomEmail();
        password = getRandomString();

        firebaseRef = new Firebase(FirebaseDataConnection.FIREBASE_URL);

        assureAuthenticated();
    }

    public String getUid() {
        return uid.get();
    }

    private void assureAuthenticated() {
        if (firebaseRef.getAuth() == null) {
            createAccount();
            logIn();
        }
    }

    private void logIn() {
        CountDownLatch latch = new CountDownLatch(1);

        firebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                latch.countDown();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                System.out.println("could not log in");
                throw firebaseError.toException();
            }
        });

        awaitLatchResolution(latch);
    }

    private void createAccount() {
        final CountDownLatch latch = new CountDownLatch(1);
        uid = new AtomicReference<>();

        firebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> authMap) {
                uid.set((String) authMap.get("uid"));
                latch.countDown();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                System.out.println("could not create account");
                throw firebaseError.toException();
            }
        });

        awaitLatchResolution(latch);
    }

    private String getRandomEmail() {
        return getRandomString() + "@fake.turingchat.com";
    }

    private String getRandomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append(getRandomChar());
        }

        return builder.toString();
    }

    private char getRandomChar() {
        return (char) (((int)(Math.random() * 26)) + 97);
    }

    private void awaitLatchResolution(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
