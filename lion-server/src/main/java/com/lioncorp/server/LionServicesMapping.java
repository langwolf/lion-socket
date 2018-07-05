package com.lioncorp.server;

import java.util.Map;

import org.apache.thrift.TProcessor;

import com.google.common.collect.Maps;

public class LionServicesMapping {

	public static final Map<String, String> SERVICES_MAPPING = Maps.newHashMap();
	
	public static final Map<String,TProcessor> SERVICES_PROCESSOR_MAP = Maps.newHashMap();
	
	public static final Map<String,org.apache.thrift.TBaseProcessor<?>> SERVICES_PROCESSOR_MAP2 = Maps.newHashMap();

}