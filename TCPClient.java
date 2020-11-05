import java.io.*;
import java.net.*;
import java.util.Scanner;

class TCPClient {

	public static void main(String argv[]) throws Exception {
		String input = "";
		boolean exit = false;
		String username = "";
		Message message;
		Socket clientSocket = new Socket("127.0.0.1", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		InputStream inFromServer = clientSocket.getInputStream();
		
		Scanner userInput = new Scanner(System.in);

		System.out.println("Math Client Program");
		System.out.println("Enter your username..."); //might want to randomly generate this instead
		username = userInput.nextLine();
		System.out.println("Hi "+username+"!");

		int attempt = 0;
		do
		{
			System.out.println("Connecting you to a math server...\n");
			sendMessage(outToServer, new Message(username, true, false, false)); //Error sending syn message
			attempt++;
		} while (!receiveMessage(inFromServer).isSyn() && attempt <= 3);

		System.out.println("You're connected.\n");


		do {

			System.out.println("Send a math request to the server? (Y/N)");
			while (!((input = userInput.nextLine()).equals("Y") || input.equals("N"))) {
				System.out.println("Input error. Try again.");
				System.out.println("Send a math request to the server? (Y/N)");
			}

			if (input.equals("N")) {
				//send fin message
				message = new Message(username, false, false, true);
				sendMessage(outToServer, message);
				exit = true;
				break;
			} else {
				System.out.println("Type your math request");
				input = userInput.nextLine();
				message = new Message(username, input);
				sendMessage(outToServer, message);
				Message reply = receiveMessage(inFromServer);
				System.out.println("Math Server Calculation Reply: " + reply.answer);
			}

		}
		while (!exit);

		System.out.println("Client closing");
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
}


