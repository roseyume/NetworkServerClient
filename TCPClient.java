/*
    Rosie Wang, Agatha Lam, Sanjana Sankaran
    CS 4390
	TCPClient.java
		This program simulates a client connecting to a math server. Each client is identifiable by a generated client id.
		The client establishes a connection, sends 3-6 math requests before properly terminating its connection and ending 
		the simulation. The client is able to send three kinds of messages:
			- 	Connection Request (syn)
				-----------------------------
				| clientID:	ID				|
				| data:					   	|
				| answer:					|
				| syn: true					|
				| ack: false				|
				| fin: false				|
				-----------------------------
			-	Request Message
				-----------------------------
				| clientID:	ID				|
				| data:	request				|   	
				| answer: 					|
				| syn: false				|
				| ack: false				|
				| fin: false				|	
				-----------------------------
			- 	Disconnection Request (fin)
				-----------------------------
				| clientID:	ID				|
				| data:					   	|
				| answer:					|
				| syn: false				|	
				| ack: false				|
				| fin: true					|
				-----------------------------

		To simulate multiple client connections more easily, the client program takes a waitTime arg (ms) at runtime. 
*/

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.UUID;

class TCPClient {
	static Random rand = new Random();

	public static void main(String argv[]) throws Exception {

		Socket clientSocket = new Socket("127.0.0.1", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		InputStream inFromServer = clientSocket.getInputStream();
		Message reply;

		System.out.println("Welcome to the Math Calculation Program!");
		String ID = "client"+UUID.randomUUID().toString(); // randomly generated client ID
		System.out.println("Your Client ID is: " + ID);

		// send connection request
		int attempts = 0;
		do
		{
			System.out.println("\nConnecting you to the server...");
			sendMessage(outToServer, new Message(ID, true, false, false)); 
			attempts++;
			reply = receiveMessage(inFromServer);
		} while ((!reply.isSyn() || !reply.isAck()) && attempts <= 3); // error sending syn message

		if(attempts >= 3)
		{
			System.out.println("Error: Cannot connect to math server.");
			System.exit(1);
		}

		System.out.println("You're connected.\n");

		// # of requests is random between 3 and 6
		for(int i = rand.nextInt(4) + 3; i > 0; i--) {
			// generate a random equation with 1-4 operators
			String request = generateRandomEquation(rand.nextInt(4) + 1, true); 
			System.out.println("Request: " + request);

			sendMessage(outToServer, new Message(ID, request));
			reply = receiveMessage(inFromServer);
			System.out.println("Math Server Reply: " + 
					(reply.isErr() ? "Server Error - Request Could Not Be Handled" : reply.getAnswer()) + "\n");
			
			Thread.sleep(rand.nextInt(3001)); // delay next request by a random amount of time 0-3000 ms
		}

		// send disconnection request
		System.out.println("Disconnecting you from the server...");
		
		do
		{
			sendMessage(outToServer, new Message(ID, false, false, true));
			reply = receiveMessage(inFromServer);
		}
		while(!reply.isFin() || !reply.isAck());

		System.out.println("You've been disconnected.");
		clientSocket.close();
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

	public static String generateRandomEquation(int numOps, boolean topLevel) {
		if(numOps == 0) // return a random number 0-30
			return rand.nextInt(31) + "";
			
		// determine number of operators on left (first) part
		int numLeftOps = rand.nextInt(numOps);
		String left = generateRandomEquation(numLeftOps, false);
		String right = generateRandomEquation(numOps - numLeftOps - 1, false);

		// combine left and right
		String equation = left + " " + generateRandomOp() + " " + right;

		// add optional parenthesis
		if(!topLevel && rand.nextBoolean())
			equation = "(" + equation +  ")";
		
		return equation;
	}

	// generate a random operator +,-,*,/
	public static String generateRandomOp() {
		switch (rand.nextInt(4)) {
		case 0:
			return "+";
		case 1:
			return "-";
		case 2:
			return "*";
		case 3:
			return "/";
		default:
			return null;
		}
	}
}
