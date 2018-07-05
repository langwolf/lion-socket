package com.lioncorp.store.redis;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisDao extends RedisCache {
    private static final Logger logger = LoggerFactory
            .getLogger(RedisDao.class);

    private static RedisDao instance;

    private RedisDao() {
        super();
    }

    public synchronized static RedisDao getInstance() {
        if (instance == null) {
            instance = new RedisDao();
        }
        return instance;
    }
    
    public String getMaterialsByDocId(String key) {
    	if(StringUtils.isBlank(key))
    		return "";
    	
    	List<String> list = this.get(new String[]{key});
    	if(null != list && !list.isEmpty())
    		return list.get(0);
    	return "";     	
    }
    
    public List<String> getMaterialsByDocId(String[] key) {
    	if(null == key || key.length == 0)
    		return Collections.emptyList();
    	
    	List<String> list = this.get(key);
    	if(null != list && !list.isEmpty())
    		return list;
    	return Collections.emptyList();   	
    }
    
    public static void main(String[] args) {
    	
    }
}
