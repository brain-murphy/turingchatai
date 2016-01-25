import network.*;
import network.interfaces.*;

/**
 * Created by brian on 1/25/16.
 */
public class Demo {

    public static void main(String args) {
        new Demo().demoFirebase();
    }


    public void demoFirebase() {

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
        giveInputToAI(messageRecieved); // or whatever

        // send messages with this
        conversationManager.sendMessage(getOutputFromAI());
    }
    
    public void giveInputToAI(String str) {
        System.out.println("AI is thinking...");
    }

    public String getOutputFromAI() {
        return "I am not a robot";
    }
}
