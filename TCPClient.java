import java.io.*; 
import java.net.*; 
import java.util.Scanner;
import java.io.ObjectInputStream;

class TCPClient { 

    public static void main(String argv[]) throws Exception 
    { 
        boolean exit = false;
        String input = "";
        Message message;

        Socket clientSocket = new Socket("127.0.0.1", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
        InputStream inFromServer = clientSocket.getInputStream();
        ObjectInputStream in;
        ByteArrayInputStream byteInStream;
        Scanner userInput = new Scanner(System.in);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOutStream);

        System.out.println("Hi! You've connected to a math server\n\n\n");


        do{

          System.out.println("Send a math request to the server? (Y/N)");
          while(!((input = userInput.nextLine()).equals("Y")||input.equals("N")))
          {
            System.out.println("Input error. Try again.");
            System.out.println("Send a math request to the server? (Y/N)");
          }

          if(input.equals("N"))
          {
            exit = true;
            break;
          }
          else
          {
            System.out.println("Type your math request");
            input = userInput.nextLine();
          }

          //generate and send message
          message = new Message(input); 
          out.writeObject(message);
          out.flush();
          byte[] byteMessage = byteOutStream.toByteArray();
          outToServer.write(byteMessage,0,byteMessage.length); 
          
          //wait for and print server reply
          byte[] bytes = new byte[1024];
          inFromServer.read(bytes);
          byteInStream = new ByteArrayInputStream(bytes);
          in = new ObjectInputStream(byteInStream);
          Object object = in.readObject();
          Message reply = (Message) object;
          System.out.println("FROM SERVER: " + reply.data); 
        }
        while(!exit);

        System.out.println("Client closing");
        clientSocket.close(); 
                         
          } 
      } 

        
