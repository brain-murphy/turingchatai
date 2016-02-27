package chatbot;

import network.*;
import network.interfaces.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBot implements Runnable
{

	public volatile boolean convFound=false;
	int portNumber;
	BufferedWriter processOutput=null;
	BufferedReader stdInput=null;
	Process chatBotProcess=null;

	FirebaseDataConnection connection;

	private static final double minimumResponseTime=4000;
	private static final double responseTimeSDev=3000;
	private static final double meanResponseTime=7000;
	/*
	public static void main(String[] args) throws IOException, InterruptedException
	{
		ChatBot cb=new ChatBot(1000);
		cb.getResponse("How are you?");
		cb.getResponse("Have you been to the zoo?");
	}
	*/
	public ChatBot(FirebaseDataConnection connection)
	{
		this.portNumber=portNumber;
		this.connection=connection;

		try
		{
			chatBotProcess=Runtime.getRuntime().exec("cmd /k cd C:\\Users\\C\\Desktop\\charliebot\\");
			processOutput=new BufferedWriter(new OutputStreamWriter(chatBotProcess.getOutputStream()));

			processOutput.write("cmd /k run.bat"); //Start chatbot "Charlie"
			processOutput.newLine();
			processOutput.flush();

			stdInput=new BufferedReader(new InputStreamReader(chatBotProcess.getInputStream()));
			//Thread.sleep(10000);
			while (stdInput.ready())
			{
				stdInput.read();
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void reset()
	{
		try
		{
			processOutput.write(""+(char)((byte)3)); //Start chatbot "Charlie"
			processOutput.newLine();
			processOutput.flush();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			processOutput.write("Y"); //Start chatbot "Charlie"
			processOutput.newLine();
			processOutput.flush();

			processOutput.write("Y"); //Start chatbot "Charlie"
			processOutput.newLine();
			processOutput.flush();

			processOutput.write("cmd /k run.bat"); //Start chatbot "Charlie"
			processOutput.newLine();
			processOutput.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ChatStartedListener getChatStartedListener() {
		return (conversation -> {

			conversation.listenForMessages((message, pConversation) -> {
				try {
					pConversation.sendMessage(getResponse(message));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

			conversation.addOnChatEndedListener(() -> {
				connection.lookForPartner(getChatStartedListener());
			});
		});
	}

	@Override
	public void run()
	{
		connection.lookForPartner(getChatStartedListener());
	}

	private String getResponse(String query) throws IOException, InterruptedException
	{
		convFound=true;
		processOutput.write(query);
		processOutput.newLine();
		processOutput.flush();

		responseTime();

		String response = "";
		while (stdInput.ready())
		{
			response+=(char)stdInput.read();
		}

		int startIndex=response.indexOf("] Charlie> ")+"] Charlie> ".length();
		int endIndex=response.indexOf("[", startIndex)-2;

		response=response.substring(startIndex, endIndex);

		response=textingGrammar(response);

		System.out.println(response);
		return response;
	}

	private void responseTime() throws InterruptedException
	{
		Thread.sleep((long)Math.round(Math.max(new Random().nextGaussian()*responseTimeSDev+meanResponseTime, minimumResponseTime)));
	}

	private String textingGrammar(String proper)
	{
		double decapitalizeChance=0.6;
		double removePunctuationChance=0.75;
		double endWithlolChance=0.1;
		double endWithSmileyFaceChance=0.05;
		double spellingMistakeChance=0.01;

		for(int index=0; index<proper.length(); index++)
		{
			//decapitalize
			if(proper.charAt(index)>=65 && proper.charAt(index)<=90
					   && Math.random()<decapitalizeChance)
			{
				proper=setChar(proper, proper.charAt(index)+32, index);
			}

			//removePunctuation
			if((proper.charAt(index)==54 || proper.charAt(index)==56)
					   && Math.random()<removePunctuationChance)
			{
				proper=removeChar(proper, index);
				index--;
			}

			//spelling mistakes
			if(Math.random()<spellingMistakeChance)
			{
				proper=setChar(proper, 97+(int)(26*Math.random()), index);
			}

		}

		//end with lol
		if(Math.random()<endWithlolChance)
		{
			proper+=" lol";
		}

		//end with :)
		if(Math.random()<endWithSmileyFaceChance)
		{
			proper+=" :)";
		}

		return proper;
	}

	private String setChar(String string, int charToSet, int index)
	{
		string=string.substring(0, index)+((char)charToSet)+string.substring(index+1);
		return string;
	}

	private String removeChar(String string, int index)
	{
		string=string.substring(0, index)+string.substring(index+1);
		return string;
	}

}
