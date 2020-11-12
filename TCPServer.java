import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class TCPServer {

    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);

        log("Server starting\n");
        
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            Thread serverThread = new Thread(new TCPServerThreads(connectionSocket));
            serverThread.start();
        }
    }

	public static void log(String message) {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println(dateFormat.format(new Date()) + " " + message);
	}

} 
 

           
