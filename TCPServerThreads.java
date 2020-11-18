/*
    Rosie Wang, Agatha Lam, Sanjana Sankaran
    CS 4390
	TCPServerThreads.java
		This class handles server threads. Each thread handles a client allowing the math server to handle multiple connections.
		In response to connection requests, request messages, and disconnection requests, the server will send:
			- Connection Response (Syn-Ack)
				-----------------------------
				| clientID:	ID (copy)		|
				| data:					   	|
				| answer:					|
				| syn: true					|
				| ack: true					|
				| fin: false				|
				-----------------------------
			- Disconnection Response (Fin-Ack)
				-----------------------------
				| clientID:	ID	(copy)		|
				| data:					   	|
				| answer:					|
				| syn: false				|	
				| ack: true					|
				| fin: true					|
				-----------------------------
			- Answer Response 
				-----------------------------
				| clientID:	ID	(copy)		|
				| data:	request	(copy)		|   	
				| answer: response			|
				| syn: false				|
				| ack: false				|
				| fin: false				|	
				-----------------------------

			- Error Response
				-----------------------------
				| clientID:	ID (copy)		|
				| err: true 			   	|
				-----------------------------
*/

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
import java.util.Date;

class TCPServerThreads implements Runnable {
	Socket socket;
	String clientID;

	boolean synReceived = false;
	boolean endConnection = false;

	Date connectTime;
	Date disconnectTime;

	TCPServerThreads(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		while (!endConnection) {
			try {
				DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
				InputStream inToClient = socket.getInputStream();               

				Message message = receiveMessage(inToClient); // receive message from client
				Message response = null;

				if(!synReceived) {
					if(message.isSyn()) {	// connection request
						connectTime = new Date();
						synReceived = true;
						clientID = message.getClientID();
						response = new Message(clientID, true, true, false);					// send syn-ack
						TCPServer.log(clientID + " has connected\n"); 							// log connection
					}
					else {
						TCPServer.log("Error. Invalid connection attempt\n");					// log invalid connection attempt
						endConnection = true;
					}
				}
				else {
					//initial connection was made
					if(message.isFin()) {	// disconnection request
						disconnectTime = new Date();
						endConnection = true;
						response = new Message(clientID, false, true, true);  					// send fin-ack
						TCPServer.log(clientID + " has disconnected\n"); 						// log disconnection

						long duration = (disconnectTime.getTime() - connectTime.getTime());
						TCPServer.log(clientID + " was connected for " + duration + " ms\n"); 	// log connection duration
					}
					else {					// math request
						try
						{
							response = solve(message);											// send answer
							TCPServer.log(clientID + " request: " + message.getData() + "\n"); 	// log math request
						}
						catch (Exception e)
						{
							response = new Message(clientID, true);								// send error message
							TCPServer.log("Error handling request from " + clientID + ": " + message.getData() + "\n"); // log error message
						}
					}
				}

				sendMessage(outToClient, response); // send response to client
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
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

		Message response = new Message(clientID, request.data, resultNum);
		return response;
	}

}