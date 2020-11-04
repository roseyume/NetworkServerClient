import java.io.*; 
import java.net.*; 

class Message implements Serializable{ 

    public int clientNum;
    public String data;
    
    public Message(String data)
    {
        this.data = data;
    }



}