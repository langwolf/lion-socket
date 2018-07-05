package com.lioncorp.nettythrift.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.DefaultMultiChannelInitializer;
import com.lioncorp.nettythrift.core.ThriftServerDef;

/**
 * @author bjssgong
 *
 */
public class TNettyThriftServerTransport extends TServerTransport {
	private static Logger logger = LoggerFactory.getLogger(TNettyThriftServerTransport.class);
    private int serverPort;

    private long clientTimeout;
    private ChannelFuture channelFuture;

    private ThriftServerDef[] serverDefs;
    private int bossThreads;
    private int workThreads;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    
    private static String OS = System.getProperty("os.name").toLowerCase();

    public TNettyThriftServerTransport(int port) {
        this(port, 0);
    }

    public TNettyThriftServerTransport(int port, long timeout) {
        this.serverPort = port;
        this.clientTimeout = timeout;
    }
    
    public TNettyThriftServerTransport(int port, long timeout, int bossThreads, int workThreads) {
        this.serverPort = port;
        this.clientTimeout = timeout;
        this.bossThreads = bossThreads;
        this.workThreads = workThreads;
    }
    
    @Override
    public void listen() throws TTransportException {
        
    	if(isUnix()){
    		bossGroup = new EpollEventLoopGroup(bossThreads);
    		workerGroup = new EpollEventLoopGroup(workThreads);
    	} else {
    		bossGroup = new NioEventLoopGroup(bossThreads);
    		workerGroup = new NioEventLoopGroup(workThreads);
    	}
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			if(isUnix()){
				bootstrap.channel(EpollServerSocketChannel.class);
				logger.info("netty use epoll for channel");
			} else {
				bootstrap.channel(NioServerSocketChannel.class);
			}
			bootstrap.group(bossGroup, workerGroup)
					 .option(ChannelOption.SO_BACKLOG, 128)
					 .option(ChannelOption.SO_REUSEADDR, true)
					 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
					 .childOption(ChannelOption.SO_SNDBUF, 1024*512)
					 .childOption(ChannelOption.SO_RCVBUF, 1024*512)
					 .childOption(ChannelOption.TCP_NODELAY, true)// no nagle
					 .childOption(ChannelOption.SO_KEEPALIVE, true) // default
					// Memory pooled 
					 .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// default
					 .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) 
					 .handler(new LoggingHandler(LogLevel.DEBUG))
					 .childHandler(new DefaultMultiChannelInitializer<Channel>(serverDefs));

			if (clientTimeout > 0) {
                bootstrap.childOption(ChannelOption.SO_TIMEOUT, new Long(clientTimeout).intValue());
            }
			channelFuture = bootstrap.bind(serverPort).sync();
			logger.info("Server started and listen on port:{}", serverPort);
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException ie) {
            throw new TTransportException(ie);
        } finally {
            close();
        }

    }

    @Override
    public void close() {
        try {
            channelFuture.channel().close();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public ThriftServerDef[] getServerDefs() {
		return serverDefs;
	}

	public void setServerDefs(ThriftServerDef[] serverDefs) {
		this.serverDefs = serverDefs;
	}

	@Override
    protected TTransport acceptImpl() throws TTransportException {
        return null;
    }

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );		
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
}
