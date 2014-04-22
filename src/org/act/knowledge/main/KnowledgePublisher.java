package org.act.knowledge.main;

import java.awt.TextArea;
import java.awt.TextField;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.act.knowledge.element.concepts.ConceptsList;
import org.act.knowledge.element.log.LogList;
//import org.act.knowledge.element.graphjson.GraphJson;
import org.act.knowledge.element.relations.RelationsList;
import org.act.knowledge.element.rules.RuleList;
import org.act.knowledge.element.textfield.WebSocketTextField;
import org.act.knowledge.element.users.UserList;
import org.act.knowledge.process.DataPublisherFactory;
import org.act.knowledge.websocket.WebSocketServer;




public class KnowledgePublisher {
	private TextArea txt_websocket = null; // WebSocket服务器状态
	private TextField txt_websocketnumber = null; // 当前WebSocket用户连接数量

	private UserList userlist = null;// WebSocket用户列表
	private ConceptsList conceptlist = null;// 知识概念列表
	private RelationsList relationlist = null;// 知识关系列表
	private RuleList rulelist = null;// 规则列表
	
//	private UserList userlist = null;//websocket用户列表
	private LogList loglist = null;//用户消息记录
	
	
//	private GraphJson graphjson = null;
	
	
	private WebSocketServer websockerserver = null; // WebServer服务器


	private DataPublisherFactory datapublisherfactory = null; // 数据处理类
	private WebSocketTextField websockettextfield= null;	// WebSocket状态栏

	public KnowledgePublisher(TextArea txt_websocket,
			 TextField txt_websocketnumber) {

		this.txt_websocket = txt_websocket;
		this.txt_websocketnumber = txt_websocketnumber;
	}
	/** 启动连接 
	 * @throws IOException */
	public void processStart(String ip, int websocketport) throws IOException {
		
		websockettextfield = new WebSocketTextField(txt_websocket, txt_websocketnumber);// WebSocket状态栏
		
		userlist = new UserList(websockettextfield);
		conceptlist = new ConceptsList();
		relationlist = new RelationsList();
		rulelist = new RuleList();
		loglist = new LogList(websockettextfield);
//		graphjson = new GraphJson();
		
		datapublisherfactory = new DataPublisherFactory(
				userlist, 			// WebSocket用户列表
				conceptlist, 		// 站点列表
				relationlist,	 	// 网络设备列表
				rulelist,	// 站点信息日志
				loglist,	//消息列表
//				graphjson,
				websockettextfield	// WebSocket状态栏
		);


		// 启动WebServer服务器，监听浏览器端的请求
		websockerserver = new WebSocketServer(websocketport,
				datapublisherfactory);
		try {
			websockerserver.serverStart();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "WebSocket服务器错误" + e);
			processStop();
			e.printStackTrace();
			throw new RuntimeException(e);  
		}

	}

	/** 关闭连接 */
	public void processStop() {
		// 关闭WebSockerServer
		if (websockerserver != null)
			websockerserver.serverStop();


		
	}
}
