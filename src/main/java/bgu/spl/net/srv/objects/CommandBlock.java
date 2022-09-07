package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;

public class CommandBlock implements Command {
    String userName;
    public CommandBlock(String userName){
        this.userName = userName;
    }

    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.block(connId,userName);
        if(isSuccessful){
            return new MessageAck((short) 12);
        }
        return new MessageError((short) 12);
    }
}
