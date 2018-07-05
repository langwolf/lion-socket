package com.lioncorp.server;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.lioncorp.service.MaterialDataService;
import com.lioncorp.service.thrift.iface.Material;
import com.lioncorp.service.thrift.iface.MaterialResponse;
import com.lioncorp.service.thrift.iface.MaterialService;
import com.lioncorp.service.thrift.iface.UserRequest;


@LionImpl(ApiName = "TMaterialService", 
ApiIfaceClazz = MaterialService.Iface.class, 
ApiProcessorClazz = MaterialService.Processor.class)
public class TMaterialServiceImpl implements  MaterialService.Iface {

	private static final Logger logger = LoggerFactory
			.getLogger(TMaterialServiceImpl.class);

	private static Gson gson = new Gson();

	@Resource
	private MaterialDataService materialDataService;
	
	@Override
	public MaterialResponse getMaterial(UserRequest userRequest)
			throws TException {
		MaterialResponse response = new MaterialResponse();
		if(null == userRequest)
			return response;
		List<String> docids = userRequest.getIds();
		List<String> materials = null;
		List<Material> resultList = Lists.newArrayList();
		
		response.setCode(1);
		try {
			materials = materialDataService.getMaterial(docids);
			if(null == materials || materials.isEmpty())
				return response;
			for(int i = 0; i < docids.size(); i++){				
				Material tmp = new Material();
				tmp.setId(docids.get(i));
				tmp.setMaterial(materials.get(i));
				resultList.add(tmp);
			}
			response.setMaterials(resultList);
			logger.info("=11==");
		} catch (Exception e) {
			response.setCode(0);
			resultList = Collections.emptyList();
			e.printStackTrace();			
		}
		return response;
	}

	@Override
	public MaterialResponse getMaterialWithCache(UserRequest userRequest)
			throws TException {
		MaterialResponse response = new MaterialResponse();
		if(null == userRequest)
			return response;
		List<String> docids = userRequest.getIds();
		List<String> materials = null;
		List<Material> resultList = Lists.newArrayList();
		
		response.setCode(1);
		try {
			materials = materialDataService.getMaterial(docids);
			if(null == materials || materials.isEmpty())
				return response;
			for(int i = 0; i < docids.size(); i++){				
				Material tmp = new Material();
				tmp.setId(docids.get(i));
				tmp.setMaterial(materials.get(i));
				resultList.add(tmp);
			}
			response.setMaterials(resultList);
		} catch (Exception e) {
			response.setCode(0);
			resultList = Collections.emptyList();
			e.printStackTrace();			
		}
		return response;
	}

	@Override
	public int getHealthStatus() throws TException {
		return 0;
	}


}
