package com.lioncorp.server.handler;

import java.lang.reflect.Proxy;


public class ServiceProxy {

	/**
	 * 生成代理类
	 * @param service
	 * @param serviceName
	 * @param serviceVersion
	 * @return
	 */
	public Object wrapper(Object service, String serviceName, String serviceVersion) {
		return Proxy.newProxyInstance(
				service.getClass().getClassLoader(), 
				service.getClass().getInterfaces(),
				new ServiceProxyHandler(service, serviceName, serviceVersion));
	}

}