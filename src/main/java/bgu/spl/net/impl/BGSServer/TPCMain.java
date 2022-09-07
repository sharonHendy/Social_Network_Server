package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImpl<>(), //protocol factory
                () -> new MessageEncoderDecoderImpl<>()//message encoder decoder factory
        ).serve();

    }
}
