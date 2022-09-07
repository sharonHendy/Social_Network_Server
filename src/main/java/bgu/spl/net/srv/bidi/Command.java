package bgu.spl.net.srv.bidi;

import bgu.spl.net.srv.Manager;

import java.io.Serializable;

public interface Command extends Serializable{

    Serializable execute(Manager manager, int connId); //returns the ack/error response in string
}
