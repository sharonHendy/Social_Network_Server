package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.srv.bidi.Command;
import bgu.spl.net.srv.bidi.Message;
import bgu.spl.net.srv.objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageEncoderDecoderImpl<Serializable> implements MessageEncoderDecoder<Serializable> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int start = 0;

    @Override
    public Serializable decodeNextByte(byte nextByte) {
        if(nextByte == ';'){
            return (Serializable) buildCommand();
        }
        pushByte(nextByte);
        return null;
    }

    public Command buildCommand(){
        Command command;
        short opcode = bytesToShort(Arrays.copyOfRange(bytes, 0 , 2));
        if(opcode == 1){
            command =  buildRegisterCommand();
        }else if(opcode == 2){
            command =  buildLoginCommand();
        }else if(opcode == 3){
            command = new CommandLogout();
        }else if(opcode == 4){
            command = buildFollowCommand();
        }else if(opcode == 5){
            command = buildPostCommand();
        }else if(opcode == 6){
            command = buildPMCommand();
        }else if(opcode == 7){
            command = new CommandLogstat();
        }else if(opcode == 8){
            command = buildStatCommand();
        }else if (opcode == 12){
            command = buildBlockCommand();
        }else{
            command = null;
        }
        len = 0;

        return command;
    }

    /**
     * splits the bytes array by '\0' to a list of strings.
     * @return the list of strings.
     */
    public ArrayList<String> splitArray(){
        ArrayList<String> list = new ArrayList<>();
        int i = 2;
        int j = i;
        while (i < len) {
            while (bytes[i] != '\0') {
                i++;
            }
            list.add(new String(bytes, j, i - j));
            i++;
            j = i;
        }
        return list;
    }
    public Command buildRegisterCommand(){
        Command command;
        ArrayList<String> splitCommand = splitArray();

        String userName = splitCommand.remove(0);
        String password = splitCommand.remove(0);
        String birthday = splitCommand.remove(0);

        command = new CommandRegister(userName,password,birthday);
        return command;
    }

    public Command buildLoginCommand(){
        ArrayList<String> splitCommand = splitArray();
        String userName = splitCommand.remove(0);
        String password = splitCommand.remove(0);
        byte captcha = bytes[len - 2];

        return new CommandLogin(userName,password,captcha);
    }

    public Command buildFollowCommand(){
        byte followUnfollow = bytes[2];
        String userName = new String(bytes, 3, len - 3);
//        ArrayList<String> splitCommand = splitArray();
//        splitCommand.remove(0);
//        String userName = splitCommand.remove(0);
        return new CommandFollow(followUnfollow,userName);
    }

    public Command buildPostCommand(){
        ArrayList<String> splitCommand = splitArray();
        String content = splitCommand.remove(0);
        return new CommandPost(content);
    }

    public Command buildPMCommand(){
        ArrayList<String> splitCommand = splitArray();
        String userName = splitCommand.remove(0);
        String content = splitCommand.remove(0);
        String date = splitCommand.remove(0);
        return new CommandPM(userName,content,date);
    }

    public  Command buildStatCommand(){
        String usersStr = new String(bytes, 2, len - 3);
        String[] usersArr;
        if(usersStr.indexOf('|') == -1){
            usersArr = new String[]{usersStr};
        }else{
            usersArr = usersStr.split("\\|");
        }
        return new CommandStat(usersArr);
    }

    public Command buildBlockCommand(){
        String userName = new String(bytes, 2, len - 3);
        return new CommandBlock(userName);
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    @Override
    public byte[] encode(Serializable message) {
        byte[] encoded;
        if(message instanceof ArrayList){ //if it's a list of acks - stat and log stat
            int len = 0;
            if(((ArrayList<?>)message).size() != 0) {
                len = encodeMsg((MessageAck)(((ArrayList<?>) message).get(0))).length;
            }
            encoded = new byte[len * ((ArrayList<?>)message).size()];
            int i = 0;
            for(Object msg : (ArrayList<?>)message){
                byte[] encodedMsg = encodeMsg((Message) msg);
                for (byte b : encodedMsg){
                    encoded[i] = b;
                    i++;
                }
            }

        }else if(message instanceof  Message){
            encoded = encodeMsg((Message) message);
        }else{ //notification
            encoded = encodeNotification((MessageNotification) message);
        }
        return encoded;
    }

    public byte[] encodeNotification(MessageNotification notification){
        byte[] opcode = shortToBytes(notification.getOpcode());
        byte PMOrPublic = notification.getPMorPublic();
        byte[] encoded = new byte[3+notification.getOptional().length + 1];
        int i = 0;
        for(byte b : opcode){
            encoded[i] =  b;
            i++;
        }
        encoded[i] = PMOrPublic;
        i++;
        for(byte b : notification.getOptional()){
            encoded[i] =  b;
            i++;
        }
        encoded[i] = ';';

        return encoded;

    }

    public byte[] encodeMsg(Message message) {
        byte[] opcode = shortToBytes(message.getOpcode());
        byte[] messageOpcode = shortToBytes(message.getMessageOpcode());
        byte[] encoded = new byte[4+message.getOptional().length + 1];
        int i = 0;
        for(byte b : opcode){
            encoded[i] =  b;
            i++;
        }
        for(byte b : messageOpcode){
            encoded[i] =  b;
            i++;
        }
        for(byte b : message.getOptional()){
            encoded[i] =  b;
            i++;
        }

        encoded[i] = ';';

        return encoded;
    }

}
