import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

class TCPServer {

    public static void main(String argv[]) throws Exception {

        BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {

            System.out.println("begining of while loop");
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("new connection found");
            Thread serverThread = new Thread(new TCPServerThreads(connectionSocket));

            serverThread.start();
            System.out.println("finsihed running serverThread");
        }
    }

} 
 

           
