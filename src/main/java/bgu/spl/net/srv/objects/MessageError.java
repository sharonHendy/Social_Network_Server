package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.bidi.Message;

public class MessageError extends Message {

    public MessageError(short messageOpcode){
        super(messageOpcode);
        opcode = 11;
    }
    public MessageError(short messageOpcode, byte[] optional) {
        super(messageOpcode, optional);
        opcode = 11;
    }
}
