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

    TCPServerThreads( Socket socket) {
        this.socket = socket;
        System.out.println("Creating ");
    }

    public void run() {
        System.out.println("Running ");
        while (true) {

                DataOutputStream outToClient = null;
                try {
                    outToClient = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream inToClient = null;
                try {
                    inToClient = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bytes = new byte[1024];
            try {
                inToClient.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // read client request
                Message message = null;
                try {
                    message = new Message(new String(bytes, StandardCharsets.UTF_8));
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
                System.out.println("Message request: " + message.data + "\n");

            try {
                response = solve(message);
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            final byte[] buffer = Double.toString(response.answer).getBytes();
                System.out.print("SERVER RESPONSE: " + buffer);
                try {
                    outToClient.write(buffer, 0, buffer.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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