package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.Command;
import bgu.spl.net.srv.objects.MessageAck;


public class BidiMessagingProtocolImpl<Serializable> implements BidiMessagingProtocol<Serializable> {
    Connections<Serializable> connections;
    int connectionId;
    boolean shouldTerminate = false;
    Manager<Serializable> manager;

//    //public BidiMessagingProtocolImpl(Manager<Serializable> manager){
//        this.manager = manager;
//    }

    public void setManager(Manager<Serializable> manager){ //TODO???
        this.manager = manager;
    }

    @Override
    public void start(int connectionId, Connections<Serializable> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
    }

    @Override
    public void process(Object message) {
        Serializable response= (Serializable) ((Command)message).execute(manager, connectionId);
        connections.send(connectionId, response);
        if(response instanceof MessageAck){ //check if it's an ack for logout
            if (((MessageAck) response).getMessageOpcode() == 3){
                shouldTerminate = true;
                connections.disconnect(connectionId);
            }
        }
        //response is sent by connection.send
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
