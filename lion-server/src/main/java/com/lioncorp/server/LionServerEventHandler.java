package com.lioncorp.server;

import java.net.Socket;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LionServerEventHandler implements TServerEventHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LionServerEventHandler.class);

    @Override
    public void preServe() {

    }

    @Override
    public ServerContext createContext(TProtocol input, TProtocol output) {
    	if (input != null && input.getTransport() != null) {
    		/**
    		TSocket tsocket = (TSocket)((TFramedTransport)input.getTransport()).transport_; 
    		Socket socket = tsocket.getSocket();
            LOGGER.info("[Monitor] ThriftServer Socket Info : server地址: " + socket.getLocalAddress() + " , server端口: "
                + socket.getLocalPort() + " , client地址: " + socket.getInetAddress() + " , client端口: "
                + socket.getPort());
           **/
        }
    	return null;
    }

    @Override
    public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {

    }

    @Override
    public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
//        TSocket socket = (TSocket)inputTransport;
//        LoggerUtility.beforeInvoke();
//        LoggerUtility.noticeLog("request from ip is %s ", socket.getSocket().getRemoteSocketAddress());
   }
    
}
