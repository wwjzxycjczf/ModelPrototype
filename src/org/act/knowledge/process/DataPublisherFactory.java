package org.act.knowledge.process;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.act.knowledge.element.concepts.ConceptsList;
import org.act.knowledge.element.highlevelreq.HighLevelReq;
import org.act.knowledge.element.log.LogList;
//import org.act.knowledge.element.graphjson.GraphJson;
import org.act.knowledge.element.relations.RelationsList;
import org.act.knowledge.element.rules.RuleList;
import org.act.knowledge.element.textfield.WebSocketTextField;
import org.act.knowledge.element.users.UserList;
import org.dom4j.DocumentException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.xml.sax.SAXException;


public class DataPublisherFactory {


	private UserList userlist = null;			// WebSocket用户列表
	private ConceptsList conceptlist = null;// 知识概念列表
	private RelationsList relationlist = null;// 知识关系列表
	private RuleList rulelist = null;// 规则列表
	private HighLevelReq highlevelreq = new HighLevelReq();// 高层需求
//	private GraphJson graphjson = null;
	private WebSocketTextField websockettextfield= null;	// WebSocket状态栏
	
	private LogList loglist = null;//消息列表
	public DataPublisherFactory(UserList userlist,				// WebSocket用户列表
			ConceptsList conceptlist,		// 知识概念列表
			RelationsList relationlist,// 知识关系列表
			RuleList rulelist,	// 规则列表
			LogList loglist,//消息列表
			
//			GraphJson graphjson,
			WebSocketTextField websockettextfield// WebSocket状态栏
			) 
	{
		
		this.userlist = userlist;				// WebSocket用户列表
		this.setConceptlist(conceptlist);			// 站点列表
		this.setRelationlist(relationlist);			// 网络设备列表
		this.setRulelist(rulelist);	// 站点信息日志
		this.setLoglist(loglist);//消息记录日志
//		this.setGraphjson(graphjson);
		this.websockettextfield = websockettextfield;	// WebSocket状态栏
	}
	
	
	/**保存高层需求
	 * @throws FileNotFoundException */
	public void AddHighlevelreq(ChannelHandlerContext ctx,String highlevelreqstr) throws FileNotFoundException{
		if(ctx != null)
		{
			highlevelreq.AddHighlevelreq(highlevelreqstr);
//			String netnodelistbuffer = relationlist.getRelationsjson();
		}
	}
	/** 自动转发Concept信息 */
	public void autosendConceptData(ConceptsList conceptlist) {
		if (conceptlist != null)
		{
			userlist.transforaddConceptDataToUsers(conceptlist);
		}
	}
	
	/** 自动转发Relation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void autosendRelationData(RelationsList relationlist) throws FileNotFoundException, Exception
	{
		if(relationlist != null)
		{
			userlist.transformodifyRelationDataToUsers(relationlist);
		}
	}
	/** 自动转发Rule信息 */
	public void autosendRuleData(RuleList rulelist)
	{
		if(rulelist != null)
		{
			userlist.transformodifyRuleToUsers(rulelist);
		}
	}
	
	
	/** 收到请求时转发Concept列表信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendConceptList(ChannelHandlerContext ctx) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			String stationlistbuffer = conceptlist.getConceptsjson();
			
			ctx.getChannel().write(new TextWebSocketFrame(stationlistbuffer));
			websockettextfield.setTxt_websocket("依照请求发送Concepts列表\r\n\r\n");
		}
	}

	/** 收到请求时转发Relation列表信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendRelationList(ChannelHandlerContext ctx) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			String netnodelistbuffer = relationlist.getRelationsjson();
			ctx.getChannel().write(new TextWebSocketFrame(netnodelistbuffer));
			websockettextfield.setTxt_websocket("依照请求发送Relations列表\r\n\r\n");
		}
	}
	/** 收到请求时转发Graphjson信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendGraphjson(ChannelHandlerContext ctx) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			String netnodelistbuffer = relationlist.getgraphjson();
			ctx.getChannel().write(new TextWebSocketFrame(netnodelistbuffer));
			websockettextfield.setTxt_websocket("依照请求发送graphjson\r\n\r\n");
		}
	}
	/** 收到请求时转发Rule列表信息 */
	public void sendRuleList(ChannelHandlerContext ctx)
	{
		if(ctx != null)
		{
			String netnodelistbuffer = rulelist.sendRuleListstr();
			ctx.getChannel().write(new TextWebSocketFrame(netnodelistbuffer));
			websockettextfield.setTxt_websocket("依照请求发送Rules列表\r\n\r\n");
		}
	}
	/** 收到请求时addConcept信息 
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException */
	public void sendAddConception(ChannelHandlerContext ctx,String username,String id,String name,String content) throws DocumentException, ParserConfigurationException, SAXException, IOException
	{
		if(ctx != null)
		{
			
			conceptlist.addConcept(content);
			String addconceptstr = conceptlist.getconceptstr();
			ctx.getChannel().write(new TextWebSocketFrame(addconceptstr));
			websockettextfield.setTxt_websocket(username+"添加了"+id+":"+name+"知识");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"添加了"+id+":"+name+"知识"+"\"}";
			userlist.transforlog(log);
			userlist.transforaddConceptDataToUsers(conceptlist);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			websockettextfield.setTxt_websocket("依照请求发送添加的concept\r\n\r\n");
		}
	}
	/** 收到请求时addRelation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendAddRelation(ChannelHandlerContext ctx,String username,String id,String name,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			
			relationlist.addRelation(content);
			String netnodelistbuffer = relationlist.getRelationsjson();
			ctx.getChannel().write(new TextWebSocketFrame(netnodelistbuffer));
			websockettextfield.setTxt_websocket(username+"添加了"+id+":"+name+"关系");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"添加了"+id+":"+name+"关系"+"\"}";
			userlist.transforlog(log);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			userlist.transformodifyRelationDataToUsers(relationlist);
			websockettextfield.setTxt_websocket("依照请求发送修改后的Relations列表\r\n\r\n");
			
		}
	}
	/** 收到请求时addRule信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendAddRule(ChannelHandlerContext ctx,String username,String rulename,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			rulelist.addRuleList(rulename, content);
			String rule = rulelist.getRulestr();
			ctx.getChannel().write(new TextWebSocketFrame(rule));
			websockettextfield.setTxt_websocket(username+"添加了"+rulename+"规则");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"添加了"+rulename+"规则"+"\"}";
			userlist.transforlog(log);
			userlist.transformodifyRuleToUsers(rulelist);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			websockettextfield.setTxt_websocket("依照请求发送添加后的rule\r\n\r\n");
			
		}
	}
//	/** 收到请求时addGraphjson信息 
//	 * @throws Exception 
//	 * @throws FileNotFoundException */
	
	/** 收到请求时更新Rule信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendUpdateRule(ChannelHandlerContext ctx,String username,String rulename,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			rulelist.updateRuleList(rulename,content);
			String rule = rulelist.getRulestr();
			ctx.getChannel().write(new TextWebSocketFrame(rule));
			websockettextfield.setTxt_websocket(username+"更新了"+rulename+"规则");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"更新了"+rulename+"规则"+"\"}";
			userlist.transforlog(log);
			userlist.transformodifyRuleToUsers(rulelist);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			websockettextfield.setTxt_websocket("依照请求发送更改后的rule\r\n\r\n");
		}
	}
	/** 收到请求时更新Conception信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendUpdateConception(ChannelHandlerContext ctx,String username,String id,String name,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			conceptlist.updateConcept(content);
			String updateconceptstr = conceptlist.getconceptstr();
			ctx.getChannel().write(new TextWebSocketFrame(updateconceptstr));
			websockettextfield.setTxt_websocket(username+"更改了"+id+":"+name+"知识");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"更改了"+id+":"+name+"知识"+"\"}";
			userlist.transforlog(log);
			userlist.transforupdateConceptDataToUsers(conceptlist);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			websockettextfield.setTxt_websocket("依照请求发送更改后的concept\r\n\r\n");
		}
	}
	/** 收到请求时updateGraphjson信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	
	/** 收到请求时updateRelation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendUpdateRelation(ChannelHandlerContext ctx,String username,String id,String name,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			
			relationlist.updateRelation(content);
			String netnodelistbuffer = relationlist.getRelationsjson();
			ctx.getChannel().write(new TextWebSocketFrame(netnodelistbuffer));
			websockettextfield.setTxt_websocket(username+"更改了"+id+":"+name+"关系");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"更改了"+id+":"+name+"关系"+"\"}";
			userlist.transforlog(log);
			userlist.transformodifyRelationDataToUsers(relationlist);
//			ctx.getChannel().write(new TextWebSocketFrame(log));

			websockettextfield.setTxt_websocket("依照请求发送修改后的Relations列表\r\n\r\n");
		}
	}
	
	
	/** 收到请求时addRule信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendDeleteRule(ChannelHandlerContext ctx,String username,String rulename,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			rulelist.addRuleList(rulename, content);
			String rule = rulelist.getRulestr();
			ctx.getChannel().write(new TextWebSocketFrame(rule));
			websockettextfield.setTxt_websocket(username+"删除了"+rulename+"规则");
			String log =  "{\"type\":\"log\",\n" + "\"log\":\""+username+"删除了"+rulename+"规则"+"\"}";
			userlist.transforlog(log);
//			ctx.getChannel().write(new TextWebSocketFrame(log));
			websockettextfield.setTxt_websocket("依照请求发送删除后的rule\r\n\r\n");
			
		}
	}
	
	/** 收到请求时删除Conception信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendDeleteConception(ChannelHandlerContext ctx,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			conceptlist.deleteConcept(content);
			String deleteconceptstr = conceptlist.getconceptstr();
			userlist.transforlog(deleteconceptstr);
//			ctx.getChannel().write(new TextWebSocketFrame(deleteconceptstr));
			websockettextfield.setTxt_websocket("依照请求发送删除后的concept\r\n\r\n");
		}
	}
	/** 收到请求时删除Relation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public void sendDeleteRelation(ChannelHandlerContext ctx,String content) throws FileNotFoundException, Exception
	{
		if(ctx != null)
		{
			String deleterelationstr = relationlist.deleteRelation1(content);
//			String deleterelationstr = relationlist.getRelationsjson();
			userlist.transforlog(deleterelationstr);
//			ctx.getChannel().write(new TextWebSocketFrame(deleterelationstr));
			websockettextfield.setTxt_websocket("依照请求发送删除后的relation\r\n\r\n");
		}
	}
	public void sendUserlist(ChannelHandlerContext ctx,String username,String type) throws FileNotFoundException, Exception{
		if(type.equals("login")){
			userlist.addUsername(username);
	//		userlist.sendUsername();
			String userstr = userlist.sendUsername();
			ctx.getChannel().write(new TextWebSocketFrame(userstr));
			String log = "{\"type\":\"singleuser\",\n"+"\"name\":\""+username+"\"}";
			userlist.transforlog(log);
			websockettextfield.setTxt_websocket(username+"登录了\r\n\r\n");
		}
		else if(type.equals("logout")){
			userlist.deleteUsername(username);
			String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username+"\"}";
			userlist.transforlog(log);
			websockettextfield.setTxt_websocket(username+"退出了\r\n\r\n");
		}
	}
	public void saveChatinfo(ChannelHandlerContext ctx,String info) throws FileNotFoundException, Exception{
		userlist.saveChatinfo(info);
		websockettextfield.setTxt_websocket(info+"\r\n\r\n");
	}
	/** 增加WebSocket用户 */
	public void addWebSocketUser(Channel channel)
	{
		// 增加WebSocket连接
		userlist.addUser(channel);
		
	}
	
	public ConceptsList getConceptlist() {
		return conceptlist;
	}

	public void setConceptlist(ConceptsList conceptlist) {
		this.conceptlist = conceptlist;
	}

	public RuleList getRulelist() {
		return rulelist;
	}

	public void setRulelist(RuleList rulelist) {
		this.rulelist = rulelist;
	}

	public RelationsList getRelationlist() {
		return relationlist;
	}

	public void setRelationlist(RelationsList relationlist) {
		this.relationlist = relationlist;
	}



	public LogList getLoglist() {
		return loglist;
	}



	public void setLoglist(LogList loglist) {
		this.loglist = loglist;
	}



//	public GraphJson getGraphjson() {
//		return graphjson;
//	}
//
//
//
//	public void setGraphjson(GraphJson graphjson) {
//		this.graphjson = graphjson;
//	}
	
}
