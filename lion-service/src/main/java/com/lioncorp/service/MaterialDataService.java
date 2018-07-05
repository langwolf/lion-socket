package com.lioncorp.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.lioncorp.store.redis.RedisClusterDao;
import com.lioncorp.store.redis.RedisDao;

@Service
public class MaterialDataService implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(MaterialDataService.class);
	
	private RedisClusterDao redisClusterDao = null;
	private RedisDao redisDao = null;
	public static final int EXPTIME =(int) TimeUnit.DAYS.toSeconds(30);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		redisClusterDao = RedisClusterDao.getInstance();
		redisDao = RedisDao.getInstance();
	}	
	
	@PostConstruct
	public void initCache() {
		
	}

	public List<String> getMaterial(List<String> ids) throws Exception {
		if (null == ids || ids.isEmpty())
			return Collections.emptyList();

		String material = "";
		List<String> materials = null;
		if (ids.size() == 1) {
			material = redisClusterDao.getMaterialsByDocId(ids.get(0));
			if(StringUtils.isBlank(material)){
				material = redisDao.getMaterialsByDocId(ids.get(0));
				if (StringUtils.isBlank(material)) {
					return Collections.emptyList();
				} else {
					redisClusterDao.addMaterial(ids.get(0), material, EXPTIME);
				}
			}			
			return Collections.singletonList(material);
		}
		materials = redisClusterDao.getMaterialsByDocId(ids
				.toArray(new String[0]));
		if(ids.size() != materials.size()){
			
		}
		materials = redisDao.getMaterialsByDocId(ids
				.toArray(new String[0]));
		if (null == materials || materials.isEmpty())
			return Collections.emptyList();

		return materials;
	}
}
