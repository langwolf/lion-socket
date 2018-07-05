package com.lioncorp.store.redis;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.common.util.Constants;
import com.lioncorp.store.redis.pipeline.RedisPipelineAccessor;

public class RedisClusterDao extends RedisClusterCache {
    private static final Logger logger = LoggerFactory
            .getLogger(RedisClusterDao.class);

    private static RedisClusterDao instance;

    private RedisClusterDao() {
        super();
    }

    public synchronized static RedisClusterDao getInstance() {
        if (instance == null) {
            instance = new RedisClusterDao();
        }
        return instance;
    }

    public void addMaterial(String key, String value, int exptime){
    	if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
    		return;
    	}
    	RedisAccessor redisAccessor = clusterFactory.createRedisAccessor(Constants.REDIS.STREAMS_MATERIALS);
    	redisAccessor.setex(key, value, exptime);
    }

    public String getMaterialsByDocId(String key) {
    	if(StringUtils.isEmpty(key)){
    		return "";
    	}
    	RedisAccessor redisAccessor = clusterFactory.createRedisAccessor(Constants.REDIS.STREAMS_MATERIALS);
    	return redisAccessor.get(key, "");
    }
    
    public List<String> getMaterialsByDocId(String[] keys) {
    	if(ArrayUtils.isEmpty(keys)){
    		return Collections.emptyList();
    	}
    	RedisPipelineAccessor pipelineAccessor = clusterFactory.createRedisPipelineAccessor();
    	return pipelineAccessor.readByPip(Constants.REDIS.STREAMS_MATERIALS, keys);
    }
}
