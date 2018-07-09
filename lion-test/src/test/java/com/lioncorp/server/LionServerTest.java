package com.lioncorp.server;

import org.junit.Before;
import org.junit.Test;



public class LionServerTest {

	public static void main(String[] args) throws Exception {
		LionServer lionServer = LionServer.newBuilder()
				.listen(9000)
				.register(new CalcIfaceImpl())
				.build();
		lionServer.start();
	}
	
	@Test
	public void test1(){
		LionServer lionServer = LionServer.newBuilder()
				.listen(9000)
				.register(new CalcIfaceImpl())
				.build();
		lionServer.start();
		
	}

}
