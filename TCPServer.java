/*
    Rosie Wang, Agatha Lam, Sanjana Sankaran
    CS 4390
	TCPServer.java
        This program simulates a math server. 
        The math server handles clients using TCPServerThreads which handles connections, disconnections,
        math requests and calculations. 
        Activities handled by the server are logged to log.txt. 
        Log settings are handled in logging.properties and can be modified to log to the command line.
*/

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.*;
import java.io.InputStream;

class TCPServer {

    private static Logger logger = null;

    static{
        try{
            FileHandler file = new FileHandler("log.txt");
            InputStream stream = TCPServer.class.getClassLoader().getResourceAsStream("logging.properties.txt");
            LogManager.getLogManager().readConfiguration(stream);
            file.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger(TCPServer.class.getName());
    
            logger.addHandler(file);
        }
        catch(Exception e)
        {
            System.out.println("Log File error");
        }
    }

    public static void main(String argv[]) throws Exception {
        @SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(6789);
        log("Server starting\n");
        
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            Thread serverThread = new Thread(new TCPServerThreads(connectionSocket));
            serverThread.start();
        }
    }

	public static void log(String message) {
        try 
        {
            logger.log(Level.INFO,message);
        } 
        catch (Exception e) {	}
    }
} 