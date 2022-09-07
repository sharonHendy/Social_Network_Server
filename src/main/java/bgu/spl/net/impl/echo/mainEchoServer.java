package bgu.spl.net.impl.echo;

import bgu.spl.net.srv.Server;

public class mainEchoServer {
    public static void main(String[] args){
//        try(Server<String> server = Server.threadPerClient(7777,
//                ()->new EchoProtocol(),
//                ()->new LineMessageEncoderDecoder());){
//            server.serve();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        try(Server<String> server = Server.reactor(2,7777,
                ()->new EchoProtocol(),
                ()->new LineMessageEncoderDecoder());){
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
