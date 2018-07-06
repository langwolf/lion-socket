package com.lioncorp.server;

import org.apache.thrift.TException;

@LionImpl(ApiName = "Ttest", 
ApiIfaceClazz = TCalculator.Iface.class, 
ApiProcessorClazz = TCalculator.Processor.class)
public class CalcIfaceImpl implements TCalculator.Iface {

	@Override
	public String ping() throws TException {
		System.out.println("***ping()...");
		return "PONG";
	}

	@Override
	public int add(int num1, int num2) throws TException {
		System.out.printf("***add:(%d, %d)\n", num1, num2);
		return num1 + num2;
	}

	@Override
	public void zip(String str, int type) throws TException {
		System.out.printf("***zip:(%s, %d)\n", str, type);
	}

	@Override
	public void uploadAction(String str) throws TException {
		System.out.printf("***uploadAction:(%s)\n", str);
	}
}
