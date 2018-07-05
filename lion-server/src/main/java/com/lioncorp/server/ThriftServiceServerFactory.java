package com.lioncorp.server;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lioncorp.nettythrift.core.ThriftServerDefBuilderBase;
import com.lioncorp.nettythrift.server.TNettyThriftServer;
import com.lioncorp.service.consul.ConsulService;



public class ThriftServiceServerFactory implements  Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(ThriftServiceServerFactory.class);
	
	private Integer port = 8299;
	private Integer timeOut = 120000;	
	private Set<Object> services;
	private String version;
//
	private ConsulService consulService;
    private ExecutorService servingExecutor;
    private boolean consulShutdownHook = false;
    private TServer server = null;

	public void init() throws TTransportException, Exception{
		this.addProcessor();
        TNettyThriftServer.Args nettyArg = new TNettyThriftServer.Args(port, timeOut);
        nettyArg.setMaxReadBuffer(ThriftServerDefBuilderBase.MAX_FRAME_SIZE);
        nettyArg.setMap(LionServicesMapping.SERVICES_PROCESSOR_MAP2);
        server = new TNettyThriftServer(nettyArg);
        
        server.setServerEventHandler(new LionServerEventHandler());
        logger.info("Start server on port:{}", port+"...");
		servingExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactoryBuilder()
						.setNameFormat("server I/O Boss").build());
		/**
		 * Start serving.
		 */
		servingExecutor.submit(new Runnable() {
			@Override
			public void run() {
				server.serve();
			}
		});
        // 注册服务
		if (consulService != null) {
//			consulService.register();		
		}

		if (consulShutdownHook) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						if (consulService != null) {
							consulService.deregister();
						}
						Thread.sleep(1000 * 5);
						close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
    }
	
	@Override
	public void close() {
		if (server != null) {
			server.stop();
		}
	}
	
	public void addProcessor(String apiName,org.apache.thrift.TBaseProcessor<?> processor){
	    LionServicesMapping.SERVICES_PROCESSOR_MAP2.put(apiName, processor);
	}
	
    private void addProcessor(){
        if(CollectionUtils.isEmpty(services)){
        	logger.info("no config service");
            return ;
        }       
        for (Object service : services) {
        	Class<?> clas = service.getClass();
        	LionImpl rid = clas.getAnnotation(LionImpl.class);
        	if(null == rid) continue;
			try {
				Constructor<?> constructor = rid.ApiProcessorClazz()
						.getConstructor(new Class[] { rid.ApiIfaceClazz() });
				String apiName = rid.ApiName();
				addProcessor(apiName,
						(org.apache.thrift.TBaseProcessor<?>) constructor.newInstance(
								new ServiceProxy().wrapper(service, apiName, version))
								);
				logger.info("load service success");
				
			} catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
	
	// set
	public void setPort(Integer port) {
		this.port = port;
	}

	public void setConsulShutdownHook(boolean consulShutdownHook) {
		this.consulShutdownHook = consulShutdownHook;
	}

	public void setServices(Set<Object> services) {
		this.services = services;
	}

	public void setConsulService(ConsulService consulService) {
		this.consulService = consulService;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
}
