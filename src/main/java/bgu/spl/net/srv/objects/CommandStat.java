package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommandStat implements Command {
    String[] users;
    public CommandStat(String[] users){
        this.users = users;
    }
    @Override
    public Serializable execute(Manager manager, int connId) {
        List<byte[]> response = manager.stat(connId, users);
        if(response != null){
            ArrayList<MessageAck> acks = new ArrayList();
            for (byte[] s : response){
                acks.add(new MessageAck((short) 8, s));
            }
            return acks;
        }
        return new MessageError((short) 8);
    }
}
