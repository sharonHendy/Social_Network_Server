package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.bidi.Message;

import java.util.ArrayList;

public class MessageAck extends Message {

    public MessageAck(short messageOpcode){
        super(messageOpcode);
        opcode = 10;
    }
    public MessageAck(short messageOpcode, byte[] optional) {
        super(messageOpcode, optional);
        opcode = 10;
    }

//    public byte[] getBytes(){
//        byte[] encoded;
//        if(OPCODE == (short) 7){ //if it's a list of acks
//            int len = 0;
//            if(((ArrayList<MessageAck>)message).size() != 0) {
//                len = encodeMsg(((ArrayList<MessageAck>) message).get(0)).length;
//            }
//            encoded = new byte[len * ((ArrayList<MessageAck>)message).size()];
//            for(Message msg : (ArrayList<MessageAck>)message){
//                byte[] encodedMsg = encodeMsg(msg);
//                int i = 0;
//                for (byte b : encodedMsg){
//                    encoded[i] = b;
//                    i++;
//                }
//            }
//
//        }else {
//            encoded = encodeMsg((Message) message);
//        }
//        return encoded;
//    }
//
//    public byte[] shortToBytes(short num) {
//        byte[] bytesArr = new byte[2];
//        bytesArr[0] = (byte)((num >> 8) & 0xFF);
//        bytesArr[1] = (byte)(num & 0xFF);
//        return bytesArr;
//    }
}
