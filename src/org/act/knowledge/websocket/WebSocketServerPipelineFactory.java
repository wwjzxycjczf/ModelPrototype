package org.act.knowledge.websocket;

import static org.jboss.netty.channel.Channels.pipeline;

import org.act.knowledge.process.DataPublisherFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;


public class WebSocketServerPipelineFactory implements ChannelPipelineFactory {

	private DataPublisherFactory datapublisherfactory = null; // 数据处理类

	public WebSocketServerPipelineFactory(
			DataPublisherFactory datapublisherfactory) {

		this.datapublisherfactory = datapublisherfactory;
	}

	/** 取得通道 */
	public ChannelPipeline getPipeline() throws Exception {
		// pipeline 的配置与逻辑
		ChannelPipeline pipeline = pipeline();// channelHandler的集合

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", new WebSocketServerHandler(
				datapublisherfactory));
		return pipeline;
	}

}
