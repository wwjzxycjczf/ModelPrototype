package org.act.knowledge.websocket;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.FileNotFoundException;

import org.act.knowledge.element.concepts.ConceptsList;
import org.act.knowledge.element.relations.RelationsList;
import org.act.knowledge.element.rules.RuleList;
import org.act.knowledge.process.DataPublisherFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;


public class WebSocketServerHandler extends SimpleChannelUpstreamHandler {

	private static final String WEBSOCKET_PATH = "/websocket";

	private WebSocketServerHandshaker handshaker = null;

	private DataPublisherFactory datapublisherfactory = null; // 数据处理类
	
//	private ConceptsList conceptslist = null;
//	
//	private RelationsList relationslist = null;
//
//	private RuleList rulelist = null;
	
	public WebSocketServerHandler(DataPublisherFactory datapublisherfactory) {

		this.datapublisherfactory = datapublisherfactory;

	}

	/** 处理接受的消息 */
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object msg = e.getMessage();
		// 消息分类
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);// HTTP报文
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);// WebSocket报文
		}
	}

	/** 处理异常情况 */
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	/** 处理HTTP请求 */
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req)
			throws Exception {
		// 接受 HTTP GET 请求
		if (req.getMethod() != GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1,
					FORBIDDEN));// 非get请求，返回 HTTP 错误页面
			return;
		}

		// Websocket 握手开始
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(req), null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			handshaker.handshake(ctx.getChannel(), req).addListener(
					WebSocketServerHandshaker.HANDSHAKE_LISTENER);
		}
		// 增加WebSocket连接
		datapublisherfactory.addWebSocketUser(ctx.getChannel());
	}

	/** 处理WebSocket报文 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) throws FileNotFoundException, Exception {
		// 处理WebSocket信息
		if (frame instanceof CloseWebSocketFrame)// Websocket 握手结束信息
		{
			handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);// 关闭连接
			return;
		} else if (frame instanceof PingWebSocketFrame) // Websocket ping信息
		{
			ctx.getChannel().write(
					new PongWebSocketFrame(frame.getBinaryData()));
			return;
		} else if (frame instanceof TextWebSocketFrame) // WebSocket文本信息
		{
			// 接收信息
			// 处理接受到的数据
			String request = ((TextWebSocketFrame) frame).getText();
			System.out.println("request:"+request);
			if (request.indexOf("login")>=0) {
				System.out.println("loginrequest"+request);
				// 发送站点列表
				((DataPublisherFactory) datapublisherfactory).sendUserlist(ctx,request.substring(0,request.indexOf(":")),request.substring(request.indexOf(":")+1));
			}
			
			if (request.indexOf("logout")>=0) {
				// 发送站点列表
				((DataPublisherFactory) datapublisherfactory).sendUserlist(ctx,request.substring(0,request.indexOf(":")),request.substring(request.indexOf(":")+1));
			}
			if(request.indexOf("说:")>=0){
				((DataPublisherFactory) datapublisherfactory).saveChatinfo(ctx,request);
			}
			if(request.indexOf("highlevelreqStr")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).AddHighlevelreq(ctx,strarr[1]);
			}
			if (request.equalsIgnoreCase("request_concept_list")) {
				// 发送站点列表
				datapublisherfactory.sendConceptList(ctx);
			} else if (request.equalsIgnoreCase("request_relation_list")) {
				// 发送网络设备列表
				datapublisherfactory.sendRelationList(ctx);
			} else if (request.equalsIgnoreCase("request_rule_list")) {
				// 发送网络设备列表
				datapublisherfactory.sendRuleList(ctx);
				datapublisherfactory.sendGraphjson(ctx);
//			}else if(request.indexOf("request_graphjson")>=0){
//				//发送图的json文件
//				datapublisherfactory.sendGraphjson(ctx);
			}else if(request.indexOf("addRelation:")>=0){//人名:addRelation：文件名：id:name:文件内容
				String[] strarr = request.split(":");//0是人名，1是addRelation，2是文件名，3是id，4是name，5是文件内容
				((DataPublisherFactory) datapublisherfactory).sendAddRelation(ctx,strarr[0],strarr[3],strarr[4],strarr[5]);
//				datapublisherfactory.sendGraphjson(ctx);
//			}else if(request.indexOf("addGraphjson:")>=0){
//				String[] strarr = request.split(":");
//				((DataPublisherFactory) datapublisherfactory).sendAddGraphjson(ctx,strarr[2]);
			}else if(request.indexOf("addConception:")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendAddConception(ctx,strarr[0],strarr[3],strarr[4],strarr[5]);
			}else if(request.indexOf("addRule:")>=0){//人名：addRule:文件名:文件内容
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendAddRule(ctx,strarr[0],strarr[2],strarr[3]);
			}else if(request.indexOf("updateRule:")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendUpdateRule(ctx,strarr[0],strarr[2],strarr[3]);
			}else if(request.indexOf("updateConcept:")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendUpdateConception(ctx,strarr[0],strarr[3],strarr[4],strarr[5]);

			}else if(request.indexOf("updateRelation:")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendUpdateRelation(ctx,strarr[0],strarr[3],strarr[4],strarr[5]);
//				datapublisherfactory.sendGraphjson(ctx);
//			}else if(request.indexOf("updateGraphjson:")>=0){
//				String[] strarr = request.split(":");
//				((DataPublisherFactory) datapublisherfactory).sendUpdateGraphjson(ctx,strarr[2]);
			}else if(request.indexOf("deleteRule:")>=0){
				String[] strarr = request.split(":");
				((DataPublisherFactory) datapublisherfactory).sendDeleteRule(ctx,strarr[0],strarr[2],strarr[3]);
			}else if(request.indexOf("deleteConception:")>=0){//目前不能实现，还需设计
				String indicateconception = request.substring(request.indexOf(":"));
				((DataPublisherFactory) datapublisherfactory).sendDeleteConception(ctx,indicateconception);
			}else if(request.indexOf("deleteRelation:")>=0){//目前不能实现，还需设计
				String deleterelation = request.substring(request.indexOf(":"));
				((DataPublisherFactory) datapublisherfactory).sendDeleteRelation(ctx,deleterelation);
//				datapublisherfactory.sendGraphjson(ctx);
			}
		} else {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}

	}

	/** 返回 HTTP 错误页面 */
	private static void sendHttpResponse(ChannelHandlerContext ctx,
			HttpRequest req, HttpResponse res) {

		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus()
					.toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// 发送返回信息并关闭连接
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}

}
