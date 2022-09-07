package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;

public class CommandLogin implements Command {

    String userName;
    String password;
    byte captcha;

    public CommandLogin(String userName, String password, byte captcha){
        this.userName = userName;
        this.password = password;
        this.captcha = captcha;
    }
    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful = manager.login(connId,userName,password,captcha);
        if(isSuccessful){
            return new MessageAck((short) 2);
        }
        return new MessageError((short) 2);
    }
}
