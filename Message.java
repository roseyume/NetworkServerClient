import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.*; 

class Message implements Serializable
{ 

    String client;
    String data;
    double answer;

    boolean syn;
    boolean ack;
    boolean fin;
    
    //Syn or Fin Message
    public Message(String client, boolean syn, boolean ack, boolean fin) 
    {

        this.client = client;
        this.syn = syn;
        this.ack = ack;
        this.fin = fin;
    }

    //Request Message
    public Message(String client, String data) 
    {
        this.client = client;
        this.data = data;
    }

    //Server Response Message
    public Message(String client, String data, double answer) 
    {
        this.client = client;
        this.data = data;
        this.answer = answer;
    }

    //Message
    public Message(String data) 
    {
        this.data = data;
    }

    public String getClient()
    {
        return client;
    }

    public String getData()
    {
        return data;
    }

    public double getAnswer()
    {
        return answer;
    }

    public boolean isSyn()
    {
        return syn;
    }

    public boolean isAck()
    {
        return ack;
    }

    public boolean isFin()
    {
        return fin;
    }

}
