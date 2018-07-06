# lion-socket
[![Build Status](https://travis-ci.org/langwolf/lion-socket.svg?branch=master)](https://travis-ci.org/langwolf/lion-socket)
[![Coverage Status](https://coveralls.io/repos/github/langwolf/lion-socket/badge.svg?branch=master)](https://coveralls.io/github/langwolf/lion-socket?branch=master)

lion-socket provides a rpc solution for using thrift. 

The server-side is built upon [netty](http://netty.io/) which supports asynchronous and non-blocking io functionality


## Quick Start

### 1. Make a thrift generated message

Use `thrift` command to compile an **IDL thrift file** and generate a java source file. 

a simple sample:


```
namespace java com.lioncorp.service.thrift.iface

struct Request {
	1:optional string id
}
struct Sample {
	1:optional string id
	2:optional string data
}
struct Response {
    1:optional string id
    2:optional Sample data
}
service TService {
    Response getData(1:Request request)
    i32 getHealthStatus()
}
```

### 2. Develop server-side service

Develop a server-side service implementation. Below is an example based on the IDL generated java code from the previous step. set the **service name** as "Ttest",The logic here is pretty simple.

```
@LionImpl(ApiName = "Ttest", 
ApiIfaceClazz = TCalculator.Iface.class, 
ApiProcessorClazz = TCalculator.Processor.class)
public class CalcIfaceImpl implements TCalculator.Iface {
	@Override
	public String ping() throws TException {
		System.out.println("...");
		return "PONG";
	}
}
```

### 3. Expose service and start server

Then start the server on port 9000.

```
LionServer lionServer = LionServer.newBuilder().listen(9000)
		.register(new CalcIfaceImpl()).build();
lionServer.start();

```

