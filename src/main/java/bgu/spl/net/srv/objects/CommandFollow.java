package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class CommandFollow implements Command {
    byte followUnfollow;
    String userName;

    public CommandFollow(byte followUnfollow,String userName){
        this.followUnfollow = followUnfollow;
        this.userName = userName;
    }
    @Override
    public Serializable execute(Manager manager, int connId) {
        boolean isSuccessful;
        if(followUnfollow == '0') {
            isSuccessful = manager.follow(connId, userName);
        }else{
            isSuccessful = manager.unfollow(connId,userName);
        }
        if(isSuccessful){
            return new MessageAck((short) 4, (userName + "\0").getBytes(StandardCharsets.UTF_8));
        }else{
            return new MessageError((short) 4);
        }
    }
}
