package com.lioncorp.client;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.lioncorp.service.thrift.iface.MaterialResponse;
import com.lioncorp.service.thrift.iface.MaterialService;
import com.lioncorp.service.thrift.iface.UserRequest;

public class NonBlockClientTest2 {
	public static void main(String[] args) {
		try {
			ExecutorService servingExecutor = Executors.newCachedThreadPool();
			for (int i = 0; i < 10; i++) {
				servingExecutor.submit(new Runnable() {
					@Override
					public void run() {
						TTransport transport = new TFramedTransport(
								new TSocket("10.200.130.93", 23926), 64*1024*1024);
						TProtocol protocol = new TCompactProtocol(transport);
//						TProtocol protocol = new TBinaryProtocol(transport);
						
						TMultiplexedProtocol mpRetrieve = new TMultiplexedProtocol(
								protocol, "TMaterialService");
						MaterialService.Client clientRetrieve = new MaterialService.Client(
								mpRetrieve);

						try {
							transport.open();
						} catch (TTransportException e1) {
							e1.printStackTrace();
						}
						System.out.println("Client calls search()");
						try {		
							System.out.println(clientRetrieve.getHealthStatus());
						} catch (TException e) {
							e.printStackTrace();
						}
						UserRequest userRequest = new UserRequest();
//						userRequest.setIds(Collections.singletonList("testest"));
						userRequest.setIds(Collections.singletonList("xyz2000"));
						MaterialResponse res = null;
						long test1 = System.currentTimeMillis();
						try {
							res = clientRetrieve.getMaterial(userRequest);
							System.out.println(System.currentTimeMillis()-test1);
							if(res != null){
//								System.out.println(JsonUtils.toJsonStr(res));
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
