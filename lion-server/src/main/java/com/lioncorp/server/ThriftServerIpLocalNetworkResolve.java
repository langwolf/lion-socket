package com.lioncorp.server;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftServerIpLocalNetworkResolve implements IpResolve {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private String serverIp;
	
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	@Override
	public String getServerIp() {
		if (serverIp != null) {
			return serverIp;
		}
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = netInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if(address instanceof Inet6Address){
						continue;
					}
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						serverIp = address.getHostAddress();
						logger.info("resolve server ip :"+ serverIp);
						continue;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return serverIp;
	}

	@Override
	public void reset() {
		serverIp = null;
	}
}
