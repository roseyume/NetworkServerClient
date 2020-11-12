import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TCPClient {

	public static void main(String argv[]) throws Exception {
		Socket clientSocket = new Socket("127.0.0.1", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		InputStream inFromServer = clientSocket.getInputStream();
		Random rand = new Random();

		System.out.println("Welcome to the Math Calculation Program!");
		String ID = UUID.randomUUID().toString(); // randomly generated client ID
		System.out.println("Your Client ID is: " + ID);

		// send connection request
		int attempts = 0;
		do
		{
			System.out.println("\nConnecting you to the server...");
			sendMessage(outToServer, new Message(ID, true, false, false)); 
			attempts++;
		} while (!receiveMessage(inFromServer).isSyn() && attempts <= 3); // error sending syn message

		System.out.println("You're connected.\n");

		// # of requests is random between 3 and 6
		for(int i = rand.nextInt(4) + 3; i > 0; i--) {
			String request = generateRandomEquation();
			System.out.println("Request: " + request);

			sendMessage(outToServer, new Message(ID, request));
			Message reply = receiveMessage(inFromServer);
			System.out.println("Math Server Reply: " + reply.getAnswer() + "\n");
		}

		// send disconnection request
		System.out.println("Disconnecting you from the server...");
		sendMessage(outToServer, new Message(ID, false, false, true));

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

	public static String generateRandomEquation() {
		Random rand = new Random();

		// add first random number 0-30
		String equation = rand.nextInt(30) + "";

		// add 1-4 more sets of a random number 0-30 and a random operator +,-,*,/
		int numOps = rand.nextInt(4) + 1;
		for(int i = 0; i < numOps; i++)
			equation += " " + generateRandomOp(rand.nextInt(4)) + " " + rand.nextInt(30);

		// insert sets of parentheses randomly based on # of operators (max 3 sets)
		for(int i = rand.nextInt(Math.min(numOps, 4)); i > 0; i--) {
			Matcher matcher = Pattern.compile("\\d+").matcher(equation);

			int openParen = rand.nextInt(numOps); // choose nth number to open parentheses at randomly
			int closeParen = rand.nextInt(numOps - openParen) + 1 + openParen; // choose nth number to close parentheses at randomly

			// iterate over each number and add open/close parentheses
			for (int n = 0; !matcher.hitEnd(); n++) {
				matcher.find(n == 0 ? 0 : matcher.end());

				if(n == openParen)
					equation = insert(equation, '(', matcher.start());
				if(n == closeParen)
					equation = insert(equation, ')', matcher.end()+1);
			}
		}

		// check for extraneous parentheses (does not eliminate all cases)
		Matcher matcher = Pattern.compile("\\(\\d+\\)").matcher(equation); 			// (num) -> num
		for(int j = 0; !matcher.hitEnd() ; j++) {
			if(matcher.find(j == 0 ? 0 : matcher.end())) {
				int start = matcher.start();
				int end = matcher.end();
				equation = equation.substring(0, start) + equation.substring(start+1, end-1) + equation.substring(end);
			}
		}

		String middle = equation.substring(1, equation.length()-1);					// (num op num) -> num op num
		if(equation.startsWith("(") && equation.endsWith(")") && !middle.contains("("))
			equation = middle;

		// check for divide by zero (does not eliminate all cases)
		matcher = Pattern.compile("/ 0").matcher(equation); 
		if(matcher.find())
			equation = generateRandomEquation();

		return equation;
	}

	public static String generateRandomOp(int num) {
		switch (num) {
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

	public static String insert(String str, char c, int pos) {
		StringBuilder sb = new StringBuilder(str);
		sb.insert(pos, c);
		return sb.toString();
	}
}
