package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;

public class CommandPost implements Command {
    String content;

    public CommandPost(String content){
        this.content = content;
    }

    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.post(connId, content);
        if(isSuccessful){
            return new MessageAck((short) 5);
        }
        return new MessageError((short) 5);
    }
}
