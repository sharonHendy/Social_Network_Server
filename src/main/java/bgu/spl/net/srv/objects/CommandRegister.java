package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;

public class CommandRegister implements Command{
    String userName;
    String password;
    String birthday;

    public CommandRegister(String userName,String password, String birthday){
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
    }
    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.register(connId,userName,password,birthday);
        if(isSuccessful){
            return new MessageAck((short) 1);
        }
        return new MessageError((short) 1);
    }
}
