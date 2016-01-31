import network.*;
import network.interfaces.*;

import java.io.*;
import java.net.*;

/**
 * Created by brian on 1/25/16.
 */
public class Demo {

    public static void main(String[] args) {
        new Demo().demoFirebase();
    }

    private Socket willieSocket;

    public void demoFirebase() {
        try {
            willieSocket = new Socket(InetAddress.getByName("143.215.62.198"), 28250);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // only need to create one FirebaseDataConnection in the program
        DataConnection firebaseConnection = new FirebaseDataConnection();

        // use this to start individual conversations
        firebaseConnection.lookForPartner(conversationManager -> {
            // this callback is invoked when a conversation starts

            // set a callback that will listen for messages
            conversationManager.listenForMessages(this::reply);

        });
    }

    public void reply(String messageRecieved, ConversationManager conversationManager) {

        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(willieSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter out = null;

        try {
            out = new PrintWriter(willieSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println(messageRecieved);

        // send messages with this
        try {
            String response =  in.readLine();
            conversationManager.sendMessage(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void giveInputToAI(String str) {
        System.out.println("AI is thinking...");
    }

    public String getOutputFromAI() {
        return "I am not a robot";
    }
}
