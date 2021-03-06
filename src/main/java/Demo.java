import network.*;
import network.interfaces.*;

import java.io.*;
import java.net.*;

/**
 * Created by brian on 1/25/16.
 */
public class Demo {

    private DataConnection connection;

    public static void main(String[] args) {
        new Demo().demoEnding();
    }

    private Socket willieSocket;

    public void demoEnding() {
        connection = new FirebaseDataConnection();

        connection.lookForPartner(getChatStartedListener());
    }

    ChatStartedListener getChatStartedListener() {
        return (conversation -> {

            conversation.listenForMessages(this::simpleReply);

            conversation.addOnChatEndedListener(() -> {
                connection.lookForPartner(getChatStartedListener());
            });
        });
    }

    private void simpleReply(String message, ConversationManager conversationManager) {
        conversationManager.sendMessage("I am not a robot");
    }


    public void socketDemo() {
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
            conversationManager.listenForMessages(this::socketReply);

        });
    }

    public void socketReply(String messageRecieved, ConversationManager conversationManager) {

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
