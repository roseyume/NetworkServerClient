/*
    Rosie Wang, Agatha Lam, Sanjana Sankaran
    CS 4390
	Message.java
		This class file handles message formats for communication protocols between the math client and server.

*/
import java.io.*;

class Message implements Serializable {
    String clientID;
    String data;
    double answer;

    boolean syn;
    boolean ack;
    boolean fin;
    
    // Syn or Fin Message
    public Message(String clientID, boolean syn, boolean ack, boolean fin) {
        this.clientID = clientID;
        this.syn = syn;
        this.ack = ack;
        this.fin = fin;
    }

    // Request Message
    public Message(String clientID, String data) {
        this.clientID = clientID;
        this.data = data;
    }

    // Server Response Message
    public Message(String clientID, String data, double answer) {
        this.clientID = clientID;
        this.data = data;
        this.answer = answer;
    }

    // Message
    public Message(String data) {
        this.data = data;
    }

    public String getClientID() {
        return clientID;
    }

    public String getData() {
        return data;
    }

    public double getAnswer() {
        return answer;
    }

    public boolean isSyn() {
        return syn;
    }

    public boolean isAck() {
        return ack;
    }

    public boolean isFin() {
        return fin;
    }

}