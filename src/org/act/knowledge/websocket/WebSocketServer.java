package org.act.knowledge.websocket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.act.knowledge.process.DataPublisherFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;


public class WebSocketServer {

	private int port; // WebSocket端口

	private DataPublisherFactory datapublisherfactory = null; // 数据处理类

	private ServerBootstrap bootstrap = null;// Socket channel factory
	private Channel channel = null; // 通道

	public WebSocketServer(int port, DataPublisherFactory datapublisherfactory) {

		this.port = port;

		this.datapublisherfactory = datapublisherfactory;

	}

	/** 启动WebSocket服务器（netty） */
	public void serverStart() throws Exception {
		// 设置 Socket channel factory
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		// 设置 Socket pipeline factory
		bootstrap.setPipelineFactory(new WebSocketServerPipelineFactory(
				datapublisherfactory));
		// 启动服务，开始监听
		channel = bootstrap.bind(new InetSocketAddress(port));
	}

	/** 关闭WebSocket服务器（netty） */
	public void serverStop() {
		if (channel != null) {
			channel.unbind();// unbind netty创建的所有channel
			channel.close();// close netty创建的所有channel
		}
		if (bootstrap != null) {
			bootstrap.releaseExternalResources(); // shutdown netty的线程执行器
			bootstrap.shutdown();
		}
	}
}
