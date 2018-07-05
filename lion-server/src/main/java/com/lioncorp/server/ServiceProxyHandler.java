package com.lioncorp.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;



public class ServiceProxyHandler implements InvocationHandler{
	
	private Object service;
	private String serviceName;
	private String serviceVersion;
	
	
    protected ServiceProxyHandler(Object service, String serviceName, String serviceVersion){
    	this.service=service;
    	this.serviceName = serviceName;
    	this.serviceVersion = serviceVersion;
    }
    
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		long startTime=System.currentTimeMillis();

		
		Object result=null;  
		try{
	        result=method.invoke(this.service, args);  
		}catch(Exception e){
			throw e;
		} finally {
		}
		return result;
	}
    
}

