package com.lioncorp.store.redis;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;

import com.google.common.base.Splitter;
import com.lioncorp.common.util.PropertyUtil;

/**
 * persistenceJedisPool
 */
public abstract class RedisClusterCache {
	
	private static Logger logger = LoggerFactory.getLogger(RedisClusterCache.class);
	private static Splitter splitter = Splitter.on(';');
	private final static String JEDIS_RANK_ADDRESS = PropertyUtil.get("jedis.cluster.address.rank");
    private final static String JEDIS_CTR_ADDRESS = PropertyUtil.get("jedis.cluster.address.ctr");
    private final static String JEDIS_FEATURE_ADDRESS = PropertyUtil.get("jedis.cluster.address.feature");
    
    static{
    	setUp();
    	setUpForCtr();
    	setUpForFeature();
    }
    
    public static RedisClusterFactory clusterFactory;    
    public static RedisClusterFactory clusterFactoryForCtr;    
    public static RedisClusterFactory clusterFactoryForFeature;

    public static void setUp() {
        clusterFactory = new RedisClusterFactory();

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();       
        for(String address : splitter.splitToList(JEDIS_RANK_ADDRESS)){
			String[] tmp = address.split(":");
			logger.info("rank redis cluster ip:{}, port:{}", tmp[0], tmp[1]);
			jedisClusterNodes.add(new HostAndPort(tmp[0], Integer.parseInt(tmp[1])));
		}
//        Set<HostAndPort> standbyJedisClusterNodes = new HashSet<>();
//        standbyJedisClusterNodes.add(new HostAndPort("10.100.1.2", 7543));
//        standbyJedisClusterNodes.add(new HostAndPort("10.100.1.2", 7544));

        clusterFactory.setJedisClusterNodes(jedisClusterNodes);
//        clusterFactory.setStandbyJedisClusterNodes(standbyJedisClusterNodes);

    }
    
    public static void setUpForCtr() {
        clusterFactoryForCtr = new RedisClusterFactory();

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        
        for(String address : splitter.splitToList(JEDIS_CTR_ADDRESS)){
			String[] tmp = address.split(":");
			logger.info("ctr redis cluster ip:{}, port:{}", tmp[0], tmp[1]);
			jedisClusterNodes.add(new HostAndPort(tmp[0], Integer.parseInt(tmp[1])));
		}

        clusterFactoryForCtr.setJedisClusterNodes(jedisClusterNodes);

    }   
    
    public static void setUpForFeature() {
    	clusterFactoryForFeature = new RedisClusterFactory();

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        
        for(String address : splitter.splitToList(JEDIS_FEATURE_ADDRESS)){
			String[] tmp = address.split(":");
			logger.info("feature redis cluster ip:{}, port:{}", tmp[0], tmp[1]);
			jedisClusterNodes.add(new HostAndPort(tmp[0], Integer.parseInt(tmp[1])));
		}

        clusterFactoryForFeature.setJedisClusterNodes(jedisClusterNodes);

    }   
}
