package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> clientsHandlers = new ConcurrentHashMap<>();
    int counterId = 0;

    public int addToClientHandlers(ConnectionHandler<T> connectionHandler){
        clientsHandlers.put(counterId,connectionHandler);
        counterId++;
        return  counterId - 1;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> handler = clientsHandlers.get(connectionId);
        if(handler == null){ //returns null if the connectionId doesn't exist
            return false;
        }else{
            handler.send(msg);
        }
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for (Integer clientId : clientsHandlers.keySet()){
            clientsHandlers.get(clientId).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        clientsHandlers.remove(connectionId);
    }

    public ConnectionHandler<T> getHandler(int connId){
        return clientsHandlers.get(connId);
    }
}
