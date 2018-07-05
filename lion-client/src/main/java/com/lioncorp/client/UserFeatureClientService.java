package com.lioncorp.client;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.lioncorp.client.thrift.PoolConfig;
import com.lioncorp.client.thrift.ServiceInfo;
import com.lioncorp.client.thrift.ShardedThriftClientPool;
import com.lioncorp.client.thrift.ThriftClientPool;
import com.lioncorp.client.thrift.ThriftMultiplexedBinaryProtocolFactory;
import com.lioncorp.common.util.IdUtil;
import com.lioncorp.common.util.MurmurHash;
import com.lioncorp.service.thrift.iface.MaterialResponse;
import com.lioncorp.service.thrift.iface.MaterialService;
import com.lioncorp.service.thrift.iface.MaterialService.Iface;
import com.lioncorp.service.thrift.iface.UserRequest;

//@Service
public class UserFeatureClientService {

	private static Splitter splitter = Splitter.on(';');
	
	private static Splitter splitter2 = Splitter.on(',');
	private static Gson gson = new Gson();
    public  static  Logger logger = LoggerFactory.getLogger(UserFeatureClientService.class);
    private static final String USER_ACTION_FEATURE_SERVER_NAME = "TUserFeatureService";

    private static ShardedThriftClientPool<String, MaterialService.Client>  parserUserFeatureDataPool = null;

    static {
        try{
          //  BasicConfigurator.configure();
            init();
        }catch (Exception e){

        }

    }
    public static List<List<ServiceInfo>> getUserFeatureServerAddress(String address){
		List<List<ServiceInfo>> serverList = Lists.newArrayList();
		List<String> shardList = splitter.splitToList(address);
		for(String shard : shardList){
			List<String> ipList = splitter2.splitToList(shard);
			List<ServiceInfo> server = Lists.newArrayList();
			for(String ad: ipList){
				String[] tmp = address.split(":");
				logger.info("topic server ip:{}, port:{}", tmp[0], tmp[1]);
				server.add(ServiceInfo.of(ad));
			}
			serverList.add(server);
		}
		return serverList;
	}
    public static void init() throws Exception {
//        String address = "10.201.88.218:13925;10.201.88.219:13925;10.201.88.220:13925;10.201.88.221:13925;" +
//                "10.201.88.222:13925;10.201.88.223:13925";
//        String address = "10.201.88.218:23926";
    	String address = "10.201.88.218:23926;10.201.88.218:23927;10.201.88.218:23928";
//      String address = "10.201.88.218:23926;10.201.88.219:23926;10.201.88.220:23926;10.201.88.221:23926;" +
//      "10.201.88.222:23926;10.201.88.223:23926;" +
//    		  
//"10.201.88.218:23927;10.201.88.219:23927;10.201.88.220:23927;10.201.88.221:23927;" +
//      "10.201.88.222:23927;10.201.88.223:23927;"
//      +
//      "10.201.88.218:23928;10.201.88.219:23928;10.201.88.220:23928;10.201.88.221:23928;" +
//"10.201.88.222:23928;10.201.88.223:23928";
      // logger.info(address);
        if(StringUtils.isBlank(address))
            //
            return;
        List<List<ServiceInfo>> servicePartitions = getUserFeatureServerAddress(address);
        PoolConfig config = new PoolConfig();
        config.setFailover(true);
        ThriftMultiplexedBinaryProtocolFactory multiFactory =
                new ThriftMultiplexedBinaryProtocolFactory("TMaterialService");
        parserUserFeatureDataPool = new ShardedThriftClientPool<>(
                servicePartitions, //
                key -> Math.abs(MurmurHash.murmurHash32(key)), //
                servers -> new ThriftClientPool<>(servers,
                        transport -> new MaterialService.Client(
                                multiFactory.makeCompactProtocol(transport)), config));
        System.out.println("init over");
}
    private static Random rd = new Random();
    public static long generateHTTPRequestId(String url) {
        return IdUtil.genID("http://cur_domain"+"/"+url+"/" + Calendar.getInstance().getTimeInMillis()+"/"+rd.nextInt(100000000));
    }

    public String getUserFeature(String param, int groups) {

        if(null == parserUserFeatureDataPool)
            return null;

        if(StringUtils.isBlank(param))
            return null;
//        String sid = "1213";
        String sid = String.valueOf(generateHTTPRequestId("rpctest"));
        if(StringUtils.isBlank(sid))
            return null;
      /*  long startTime = System.currentTimeMillis();*/
        UserRequest userRequest = new UserRequest();
//        List<String> lis = Lists.newArrayList();
//        for(int i=10; i < 1010; i++){
//        	lis.add("xyz"+i);
//        }
//		userRequest.setIds(lis);
        userRequest.setIds(Collections.singletonList(param));
		Iface iface = parserUserFeatureDataPool.getShardedPoolByHash(sid).iface();
		MaterialResponse res = null;
		try {
			res = iface.getMaterial(userRequest);
			if(res != null)	{		
//				System.out.println(JsonUtils.toJsonStr(res));
			}
		} catch (TException e) {
			e.printStackTrace();
		}
        return gson.toJson(res);
    }

    public static void main(String[] args) throws InterruptedException {
    }

}