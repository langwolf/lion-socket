package com.lioncorp.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.lioncorp.client.BaseService;
import com.lioncorp.client.thrift.PoolConfig;
import com.lioncorp.client.thrift.ServiceInfo;
import com.lioncorp.client.thrift.ShardedThriftClientPool;
import com.lioncorp.client.thrift.ThriftClientPool;
import com.lioncorp.client.thrift.ThriftMultiplexedBinaryProtocolFactory;
import com.lioncorp.common.util.IdUtil;
import com.lioncorp.common.util.MurmurHash;
import com.lioncorp.server.TCalculator.Iface;

public class LionClientTest {
	private static ShardedThriftClientPool<String, TCalculator.Client>  parserDataPool = null;
	private static Gson gson = new Gson();
	@Before
	public void test1() {

		new LionServerTest().test1();
		
		String address = "127.0.0.1:9000";

		if (StringUtils.isBlank(address))
			//
			return;
		List<List<ServiceInfo>> servicePartitions = BaseService
				.getServerAddress(address);
		PoolConfig config = new PoolConfig();
		config.setFailover(true);
		ThriftMultiplexedBinaryProtocolFactory multiFactory = new ThriftMultiplexedBinaryProtocolFactory(
				"Ttest");
		parserDataPool = new ShardedThriftClientPool<>(servicePartitions, //
				key -> Math.abs(MurmurHash.murmurHash32(key)), //
				servers -> new ThriftClientPool<>(servers,
						transport -> new TCalculator.Client(multiFactory
								.makeCompactProtocol(transport)),
						config));
		System.out.println("init over");
	}
	
//	@Test
	public void test2(){
        if(null == parserDataPool)
            return;
        String sid = String.valueOf(IdUtil.generateHTTPRequestId("rpctest"));
        if(StringUtils.isBlank(sid))
            return;
		Iface iface = parserDataPool.getShardedPoolByHash(sid).iface();
		String res = null;
		try {
			res = iface.ping();
			if(res != null)	{		
				System.out.println(gson.toJson(res));
			}
		} catch (TException e) {
			e.printStackTrace();
		}
	}
}
