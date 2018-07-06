package com.lioncorp.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ClientTest {
	public static void main(String[] args) {
		try {
			ExecutorService servingExecutor = Executors.newCachedThreadPool();
			for (int i = 0; i < 10; i++) {
				servingExecutor.submit(new Runnable() {
					@Override
					public void run() {
						TTransport transport = new TFramedTransport(
								new TSocket("127.0.0.1", 9000), 64*1024*1024);
						TProtocol protocol = new TCompactProtocol(transport);
//						TProtocol protocol = new TBinaryProtocol(transport);
						
						TMultiplexedProtocol mpRetrieve = new TMultiplexedProtocol(
								protocol, "Ttest");
						TCalculator.Client clientRetrieve = new TCalculator.Client(
								mpRetrieve);

						try {
							transport.open();
						} catch (TTransportException e1) {
							e1.printStackTrace();
						}
						String res = null;
						long test1 = System.currentTimeMillis();
						try {
							res = clientRetrieve.ping();
							System.out.println(System.currentTimeMillis()-test1);
							if(res != null){
								System.out.println(res);
							}
							
						} catch (TException e) {
							e.printStackTrace();
						}
						transport.close();
					}

				});
			}
			servingExecutor.shutdown();
		} catch (Exception e) {
		}
	}
}
