package com.lioncorp;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LionMain {

	private static final Logger logger = LoggerFactory
			.getLogger(LionMain.class);
	
	public static void main(String[] args) {
		try {
			if(ArrayUtils.isNotEmpty(args)
					&& args.length > 0){
//				new ClassPathXmlApplicationContext("classpath*:lion-server"+args[0]+".xml");
				new ClassPathXmlApplicationContext("classpath*:lion-server1.xml");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
