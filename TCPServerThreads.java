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
import java.text.SimpleDateFormat;

class TCPServerThreads implements Runnable {
	Socket socket;
	String clientID;

	boolean synReceived = false;
	boolean endConnection = false;

	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
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
						TCPServer.log(clientID + " has connected"); 							// log connection
					}
					else {
						TCPServer.log("Error. Invalid connection attempt");						// log invalid connection attempt
						endConnection = true;
					}
				}
				else {
					if(message.isFin()) {	// disconnection request
						disconnectTime = new Date();
						endConnection = true;
						response = new Message(clientID, false, true, true);  					// send fin-ack
						TCPServer.log(clientID + " has disconnected"); 							// log disconnection

						long duration = (disconnectTime.getTime() - connectTime.getTime());
						TCPServer.log(clientID + " was connected for " + duration + " ms\n"); 	// log connection duration
					}
					else {					// math request
						response = solve(message);												// send answer
						TCPServer.log(clientID + " request: " + message.getData()); 			// log math request
					}
				}

				sendMessage(outToClient, response); // send response to client
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (ScriptException e) {
				e.printStackTrace();
			}
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