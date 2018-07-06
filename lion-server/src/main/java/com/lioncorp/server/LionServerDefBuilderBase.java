package com.lioncorp.server;



public abstract class LionServerDefBuilderBase<T extends LionServerDefBuilderBase<T>> {

	private int serverPort = 8080;
	private int timeOut = 5000;
	private Object service;
	private boolean consulShutdownHook = false;
	private boolean useConsul = false;
	
	@SuppressWarnings("unchecked")
	public T listen(int serverPort) {
		this.serverPort = serverPort;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T timeOut(int timeOut) {
		this.timeOut = timeOut;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T consulShutdownHook(boolean consulShutdownHook) {
		this.consulShutdownHook = consulShutdownHook;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T useConsul(boolean useConsul) {
		this.useConsul = useConsul;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T register(Object service) {
		this.service = service;
		return (T)this;
	}
	public LionServer build() {
		return new LionServer(serverPort, timeOut, consulShutdownHook, useConsul, service);
	}
}
