package chatbot;

import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Random;

import network.FirebaseDataConnection;

public class ChatBotManager
{

	FirebaseDataConnection connection;

	public static void main(String[] args)
	{
		new ChatBotManager(1);
	}

	public ChatBotManager(int numberBots)
	{
		connection=new FirebaseDataConnection();
		for(int created=0; created<numberBots; created++)
		{
			ChatBot newChatBot=new ChatBot(connection);
			new Thread(newChatBot).run();
			/*
			while(!newChatBot.convFound)
			{
				try
				{
					Thread.sleep(50);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			*/
		}
	}

}
