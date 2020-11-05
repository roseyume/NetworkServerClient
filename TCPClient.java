import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.ObjectInputStream;

class TCPClient {

    public static void main(String argv[]) throws Exception {
        String input = "";
        boolean exit = false;
        String username = "";
        Message message;
        Socket clientSocket = new Socket("127.0.0.1", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        InputStream inFromServer = clientSocket.getInputStream();
        //ObjectInputStream in;
        //ByteArrayInputStream byteInStream;
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
        } while (readServerReply(inFromServer).getSyn() != true && attempt != 3);

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
                Message reply = readServerReply(inFromServer);
                System.out.println("Math Server Calculation Reply: " + reply.data);
            }

        }
        while (!exit);

        System.out.println("Client closing");
        clientSocket.close();

    }

    public static void sendMessage(DataOutputStream outToServer, Message message)
    {
      try
      {
        outToServer.write(message.data.getBytes()); //Error! convert entire message to bytes. not only data
        outToServer.flush();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    public static Message readServerReply(InputStream inFromServer)
    {
      try
      {
        byte[] bytes = new byte[1024];
        inFromServer.read(bytes);
        String msg = new String(bytes);
        return new Message(new String(bytes)); //Error! Will not deserialize field descriptors in the Message properly
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
    }
}

        
