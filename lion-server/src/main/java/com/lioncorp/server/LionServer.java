package com.lioncorp.server;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lioncorp.nettythrift.core.ThriftServerDefBuilderBase;
import com.lioncorp.nettythrift.server.TNettyThriftServer;
import com.lioncorp.server.handler.LionServerEventHandler;
import com.lioncorp.server.handler.ServiceProxy;
import com.lioncorp.service.consul.ConsulService;
import com.lioncorp.service.consul.impl.ConsulServiceImpl;

public class LionServer implements  Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(LionServer.class);
	
	public final int port;
	public final int timeOut;	
	public final boolean consulShutdownHook;
	public final boolean useConsul;
	
	private ConsulService consulService;
    private ExecutorService servingExecutor;  
    private TServer server;

    public LionServer(
    		int port, 
    		int timeOut, 
    		boolean consulShutdownHook,
    		boolean useConsul,
    		Object service) {
    	super();
    	this.port = port;
    	this.timeOut = timeOut;
    	this.consulShutdownHook = consulShutdownHook;
    	this.useConsul = useConsul;
    	if (null == service) {
			logger.info("no config service");
			return;
		}
		Class<?> clas = service.getClass();
		LionImpl rid = clas.getAnnotation(LionImpl.class);
		if (null == rid)
			return;
		try {
			Constructor<?> constructor = rid.ApiProcessorClazz()
					.getConstructor(new Class[] { rid.ApiIfaceClazz() });
			String apiName = rid.ApiName();
			addProcessor(apiName,
					(org.apache.thrift.TBaseProcessor<?>) constructor
							.newInstance(new ServiceProxy().wrapper(service,
									apiName, "")));
		} catch (Exception e) {
			e.printStackTrace();
		}
        TNettyThriftServer.Args nettyArg = new TNettyThriftServer.Args(port, timeOut);
        nettyArg.setMaxReadBuffer(ThriftServerDefBuilderBase.MAX_FRAME_SIZE);
        nettyArg.setMap(LionServicesMapping.SERVICES_PROCESSOR_MAP);
        server = new TNettyThriftServer(nettyArg);       
        server.setServerEventHandler(new LionServerEventHandler());
    }
	
	public void start() {
		logger.info("Start server on port:{}", port+"...");
		servingExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactoryBuilder()
						.setNameFormat("server I/O Boss").build());

		servingExecutor.submit(new Runnable() {
			@Override
			public void run() {
				server.serve();
			}
		});
        // 注册服务
		if (useConsul) {
			consulService = new ConsulServiceImpl();
			consulService.register();		
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
		logger.info("Server started");
	}
	
	private void addProcessor(String apiName,org.apache.thrift.TBaseProcessor<?> processor){
	    LionServicesMapping.SERVICES_PROCESSOR_MAP.put(apiName, processor);
	}
	
	public static LionServerDefBuilder newBuilder() {
		return new LionServerDefBuilder();
	}
	
	@Override
	public void close() {
		if (server != null) {
			server.stop();
		}
	}
	
}
