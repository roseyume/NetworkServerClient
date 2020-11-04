import java.io.*; 
import java.net.*; 
import java.io.ObjectInputStream;

class TCPServer { 

  public static void main(String argv[]) throws Exception 
  { 
    Object request; 
    String capitalizedSentence; 
    ByteArrayInputStream byteInStream;
    ObjectInputStream in;
    Message response;

    BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));
    ServerSocket welcomeSocket = new ServerSocket(6789); 
    ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOutStream);

    while(true) { 

        Socket connectionSocket = welcomeSocket.accept(); 

        DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream()); 
       
        InputStream inToClient = connectionSocket.getInputStream();
        byte[] bytes = new byte[1024];
        inToClient.read(bytes);
        byteInStream = new ByteArrayInputStream(bytes);
        in = new ObjectInputStream(byteInStream);
       
        //read client request
        Object object = in.readObject();
        Message message = (Message) object;
        System.out.println("Message request: "+message.data+"\n"); 


        response = solve(message);

        out.writeObject(response);
        out.flush();
        byte[] byteMessage = byteOutStream.toByteArray();
        outToClient.write(byteMessage,0,byteMessage.length); 
        
    } 
  } 

  public static Message solve (Message request)
  {
    Message response = new Message("5");
    return response;
  } 
} 
 

           
