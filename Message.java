import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.*; 

class Message implements Serializable{ 

    public int clientNum;
    public String data;
    public double answer;
    
    public Message(String data) throws ScriptException {
        this.data = data;
        //this.answer = evaluate(data);
    }

    public void setAnswer(double answer){
        this.answer = answer;
    }

    public double getAnswer(){
        return answer;
    }


}