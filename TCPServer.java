import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.*;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class TCPServer {

    public static void main(String argv[]) throws Exception {

        BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));
        ServerSocket welcomeSocket = new ServerSocket(6789);

        ArrayList<Thread> serverSockets = new ArrayList<>();
        ArrayList<Socket> sockets = new ArrayList<>();

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            Thread serverThread = new Thread(new TCPServerThreads(connectionSocket));

            sockets.add(connectionSocket);
            serverSockets.add(serverThread);

            serverThread.run();
/*
            while(true) {

                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                InputStream inToClient = connectionSocket.getInputStream();
                byte[] bytes = new byte[1024];
                inToClient.read(bytes);

                // read client request
                Message message = new Message(new String(bytes, StandardCharsets.UTF_8));
                System.out.println("Message request: " + message.data + "\n");

                response = solve(message);

                final byte[] buffer = Double.toString(response.answer).getBytes();
                System.out.print("SERVER RESPONSE: " + buffer);
                outToClient.write(buffer, 0, buffer.length);
            }*/
        }
    }

    public static Message solve(Message request) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        double resultNum = Double.parseDouble(engine.eval(request.data.trim()).toString());
        Message response = new Message(request.data);
        response.setAnswer(resultNum);
        return response;
    }
} 
 

           
