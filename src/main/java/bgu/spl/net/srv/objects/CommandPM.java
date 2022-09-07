package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import javax.swing.*;
import java.io.Serializable;

public class CommandPM implements Command {
    String userName;
    String content;
    String date;
    public CommandPM(String userName,String content, String date){
        this.userName = userName;
        this.content = content;
        this.date = date;
    }
    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.PM(connId,userName,content,date);
        if(isSuccessful){
            return new MessageAck((short) 6);
        }
        return new MessageError((short) 6);
    }
}
