import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class TCPServerThreads implements Runnable {
    private Socket socket;
    Message response;
    boolean synReceived = false;
    boolean endConnection = false;
    String client;

    TCPServerThreads( Socket socket) {
        this.socket = socket;
        System.out.println("Creating ");
    }

    public void run() {
        System.out.println("Running ");
        while (!endConnection) {

            DataOutputStream outToClient = null;
            byte[] buffer = null;
            try {
                outToClient = new DataOutputStream(socket.getOutputStream());
                InputStream inToClient = null;                
                inToClient = socket.getInputStream();               
                byte[] bytes = new byte[1024];           
                inToClient.read(bytes);   

                // read client request
                Message message = null;
                message = new Message(client, new String(bytes, StandardCharsets.UTF_8));

                if(!synReceived)
                {
                    if(message.getSyn() == true)
                    {
                        //send syn-ack message to establish connection
                        response = new Message(client, true, true, false);
                        buffer = Double.toString(response.answer).getBytes();
                    }
                    else
                    {
                        System.out.println("Error. Invalid connection attempt");
                        endConnection = true;
                    }
                }
                else
                {
                    if(message.getFin() == true)
                    {
                        response = new Message(client, false, true, true);
                        endConnection = true;
                    }
                    else
                    {
                        try{
                            System.out.println("Message request: " + message.data + "\n");
                            response = solve(message);
                            buffer = Double.toString(response.answer).getBytes();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                outToClient.write(buffer, 0, buffer.length);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            System.out.println("Thread ended with client "+client);
        }
    }

    public Message solve(Message request) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        double resultNum = Double.parseDouble(engine.eval(request.data.trim()).toString());
        Message response = new Message(client, request.data, resultNum);
        return response;
    }

}