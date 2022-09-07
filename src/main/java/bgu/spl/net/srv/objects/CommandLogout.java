package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;

public class CommandLogout implements Command {
    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.logout(connId);
        if(isSuccessful){
            return new MessageAck((short) 3);
        }
        return new MessageError((short) 3);
    }
}
