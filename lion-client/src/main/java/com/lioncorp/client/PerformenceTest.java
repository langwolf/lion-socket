package com.lioncorp.client;


import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformenceTest implements JavaSamplerClient {
//	private SampleResult results;
	public static Logger logger = LoggerFactory
			.getLogger(PerformenceTest.class);
	UserFeatureClientService test = new UserFeatureClientService();

	// 设置传入的参数，可以设置多个，已设置的参数会显示到Jmeter的参数列表中
	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument("key", "pikah120930101");// 设置参数，并赋予默认值pika0100000002
		return params;
	}

	// 初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行
	public void setupTest(JavaSamplerContext arg0) {
//		results = new SampleResult();
	}

	// 测试执行的循环体，根据线程数和循环次数的不同可执行多次
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		
		SampleResult results = new SampleResult();
		// long time_start = System.currentTimeMillis();
		String key = arg0.getParameter("key"); // 获取在Jmeter中设置的参数值
		results.sampleStart();// jmeter 开始统计响应时间标记
		String resString = null;
		try {
			// 通过java请求调用服务

//			 long time_start_1 = System.currentTimeMillis();
			resString = test.getUserFeature(key, 0);
//			 long time_end_1 = System.currentTimeMillis();
//			 System.out.println("runTest调用getUserFeature的运行时间："+(time_end_1-time_start_1)+"ms");

			// results.setResponseData(resString, "utf-8");
			// results.setResponseMessage(resString);

		} catch (Throwable e) {
			results.setSuccessful(false);
			e.printStackTrace();
		} finally {
			results.sampleEnd();// jmeter 结束统计响应时间标记
		}
		if (resString != null) {
//			JSONObject jsonObject = JSONObject.parseObject(resString);
			int i = 1;
				//	jsonObject.getIntValue("code");
			if (i == 1) {
				results.setSuccessful(true);
				results.setResponseMessage("1");
			} else {
				results.setSuccessful(false);
				results.setResponseMessage("0");
			}
		}
		// logger.info(resString);
		return results;
	}

	// 结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行
	public void teardownTest(JavaSamplerContext arg0) {
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Arguments params = new Arguments();
		params.addArgument("key", "xyz2000");// 设置参数，并赋予默认值0
		JavaSamplerContext arg0 = new JavaSamplerContext(params);
		PerformenceTest test = new PerformenceTest();
		test.setupTest(arg0);
		test.runTest(arg0);
		test.teardownTest(arg0);
		System.exit(0);
//		while (true) {
//
//			long time_start1 = System.currentTimeMillis();
//			client.getUserFeature("xyz2000", 0);
//
//			long time_end = System.currentTimeMillis();
//			System.out.println("总的运行时间：" + (time_end - time_start1) + "ms");
//		}
	}
}