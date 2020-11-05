import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class TCPServerThreads implements Runnable {
	private Socket socket;
	Message response;
	boolean synReceived = false;
	boolean endConnection = false;
	String client;

	TCPServerThreads( Socket socket) {
		this.socket = socket;
		System.out.println("Creating ");
	}

	public void run() {
		System.out.println("Running ");
		while (!endConnection) {
			try {
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				InputStream inToClient = socket.getInputStream();               
				
				Message message = receiveMessage(inToClient);

				if(!synReceived) {
					if(message.isSyn()) {
						synReceived = true;
						
						//send syn-ack message to establish connection
						response = new Message(client, true, true, false);
					}
					else {
						System.out.println("Error. Invalid connection attempt");
						endConnection = true;
					}
				}
				else {
					if(message.isFin()) {
						response = new Message(client, false, true, true);
						endConnection = true;
					}
					else {
						try {
							System.out.println("Message request: " + message.data + "\n");
							response = solve(message);
						} catch (ScriptException e) {
							e.printStackTrace();
						}
					}
				}
				sendMessage(outToClient, response);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Thread ended with client "+client);
		}
	}

	public static void sendMessage(OutputStream outputStream, Message message)
	{
		try
		{
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOutStream);
			
			out.writeObject(message);
			out.flush();
			byte[] byteMessage = byteOutStream.toByteArray();
			outputStream.write(byteMessage, 0, byteMessage.length); 
			outputStream.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static Message receiveMessage(InputStream inputStream)
	{
		try
		{
			ByteArrayInputStream byteInStream;
			ObjectInputStream in;
			
			byte[] bytes = new byte[1024];
			inputStream.read(bytes);
			byteInStream = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(byteInStream);
			return (Message) in.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public Message solve(Message request) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		double resultNum = Double.parseDouble(engine.eval(request.data.trim()).toString());
		System.out.println("Result: " + resultNum);
		
		Message response = new Message(client, request.data, resultNum);
		return response;
	}

}
