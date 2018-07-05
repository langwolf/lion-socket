package com.lioncorp.service.consul;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;

public class AbstractConsulService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConsulService.class);

    protected final static String CONSUL_NAME="consul_service";
    protected final static String CONSUL_ID = UUID.randomUUID().toString();
    protected final static String CONSUL_TAGS="v3";
    protected final static String CONSUL_HEALTH_INTERVAL="1s";
    
    protected final static int PORT = 28456;

    protected final static String REGISTRY_HOST = "127.0.0.1"; 
    protected final static String REGISTRY_PORT = "8500";  
    
    protected Consul buildConsul(){
        return Consul.builder().withHostAndPort(HostAndPort.fromString(REGISTRY_HOST+":"+REGISTRY_PORT)).build();
    }
}
