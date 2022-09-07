package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.Manager;
import bgu.spl.net.srv.bidi.Command;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommandLogstat implements Command {
    @Override
    public Serializable execute(Manager manager, int connId) {
        List<byte[]> response = manager.logStat(connId);
        if(response != null){
            ArrayList<MessageAck> acks = new ArrayList<MessageAck>();
            for (byte[] s : response){ //todo change encode accordingly..
                acks.add(new MessageAck((short) 7, s));
            }
            return acks;
        }
        return new MessageError((short) 7);
    }
}
