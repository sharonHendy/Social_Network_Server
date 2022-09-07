package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.bidi.Message;

import java.io.Serializable;

public class MessageNotification implements Serializable {

    public short opcode;
    byte[] optional;
    byte PMorPublic;


    public MessageNotification(byte PMorPublic, byte[] optional){
        opcode = 9;
        this.optional = optional;
        this.PMorPublic = PMorPublic;
    }

    public short getOpcode() {
        return opcode;
    }

    public byte getPMorPublic() {
        return PMorPublic;
    }

    public byte[] getOptional() {
        return optional;
    }
}
