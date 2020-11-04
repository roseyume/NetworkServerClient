import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.ObjectInputStream;

class TCPClient {

    public static void main(String argv[]) throws Exception {
        boolean exit = false;
        String input = "";
        Message message;

        Socket clientSocket = new Socket("127.0.0.1", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        InputStream inFromServer = clientSocket.getInputStream();
        ObjectInputStream in;
        ByteArrayInputStream byteInStream;
        Scanner userInput = new Scanner(System.in);

        System.out.println("Hi! You've connected to a math server\n\n\n");


        do {

            System.out.println("Send a math request to the server? (Y/N)");
            while (!((input = userInput.nextLine()).equals("Y") || input.equals("N"))) {
                System.out.println("Input error. Try again.");
                System.out.println("Send a math request to the server? (Y/N)");
            }

            if (input.equals("N")) {
                exit = true;
                break;
            } else {
                System.out.println("Type your math request");
                input = userInput.nextLine();
            }

            //generate and send message
            message = new Message(input);
            System.out.println("Input " + input);
            outToServer.write(message.data.getBytes());
            outToServer.flush();

            //wait for and print server reply
            byte[] bytes = new byte[1024];
            inFromServer.read(bytes);
            String msg = new String(bytes);
            System.out.println("Message raw " + msg);
            Message reply = new Message(new String(bytes));
            System.out.println("FROM SERVER: " + reply.data);

        }
        while (!exit);

        System.out.println("Client closing");
        clientSocket.close();

    }
}

        
