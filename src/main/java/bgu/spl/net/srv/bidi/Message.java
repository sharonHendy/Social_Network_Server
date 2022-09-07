package bgu.spl.net.srv.bidi;

import java.io.Serializable;

public class Message implements Serializable {
    public short opcode;
    short messageOpcode;
    byte[] optional;

    public Message(short messageOpcode){
        this.messageOpcode = messageOpcode;
        this.optional = new byte[0];
    }

    public Message(short messageOpcode, byte[] optional){
        this.messageOpcode = messageOpcode;
        this.optional = optional;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

    public byte[] getOptional() {
        return optional;
    }
}
