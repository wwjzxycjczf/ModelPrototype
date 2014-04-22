package org.act.knowledge.element.users;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.act.knowledge.element.concepts.ConceptsList;
import org.act.knowledge.element.relations.RelationsList;
import org.act.knowledge.element.rules.Rule;
import org.act.knowledge.element.rules.RuleList;
import org.act.knowledge.element.textfield.WebSocketTextField;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import fmath.conversion.d.b.e;




//import xservices.wsr.datacenter.element.netnode.NetNodeList;
//import xservices.wsr.datacenter.element.station.Station;
//import xservices.wsr.datacenter.element.textfield.WebSocketTextField;

public class UserList {

private ChannelGroup channelgroup = null;	//组播组
	
	private WebSocketTextField websockettextfield = null;	// WebSocket状态栏
	
	private int channelnum = 0;
	private int sendingtimes = 1;

	private final String userlistfilename = "userlist.txt";


	private HashMap<String,String> userChannel;
	private Object[] channelarrayorigin;
	private final String userchatlistfilename = "userchatlist.txt";
	private ArrayList<String> userchat;
//	private List<String> usernames;
//	private int usernum=0;
	public UserList(WebSocketTextField websockettextfield){
		channelgroup = new DefaultChannelGroup();
		this.websockettextfield = websockettextfield;
		userChannel = new HashMap<String,String>();
		userchat = new ArrayList<String>(50);
//		usernames = new ArrayList<String>();
//		usernames = new String[10];
	}
	/** 将用户加入WebSocket列表 */
	public synchronized void addUser(Channel channel) {
		channelgroup.add(channel);
		websockettextfield.setTxt_websocketnumber(String.valueOf(channelgroup.size()));
	}
	
	/**向userchat中添加用户交谈信息，如果交谈信息>=50条则存到文件userchatlist.txt中
	 * 
	 */
	public synchronized void saveChatinfo(String chatinfo) throws FileNotFoundException, Exception {
		System.out.println("savechatinfo:");
		String chatinfomation = "{\"type\":\"chat\",\n"+"\"info\":\""+chatinfo+"\"}";
		channelgroup.write(new TextWebSocketFrame(chatinfomation));
		if(userchat.size()>=50){
			FileWriter filewriter = null;
			PrintWriter out = null;
			
			try {
				filewriter = new FileWriter(userchatlistfilename,true);// 创建输出流
				out = new PrintWriter(filewriter);
				StringBuffer buffer = new StringBuffer("");
				
				for (int i = 0; i < userchat.size(); ++i) {
					buffer = buffer.append(userchat.get(i));
					buffer = buffer.append("\r\n");
				}
				out.print(buffer.toString());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				JOptionPane.showMessageDialog(null, "Userlist文件写入错误");
				System.err.println("userchatlist文件写入错误");
			} finally
			{
				if (out != null) {
					out.close();
					out = null;
				}
				if (filewriter != null) {
					try {
						filewriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					filewriter = null;
				}
			}
		
			userchat = new ArrayList<String>(50);
			
			
		}
		userchat.add(chatinfo);	
	}
	/** 向列表中的用户转发Json格式的Concept信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public synchronized void transforConceptsDataToUsers(ConceptsList conceptlist) throws FileNotFoundException, Exception {

		// 更新WebSocket用户列表
		updateUserList();
				
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String conceptsjsonbuffer = conceptlist.getConceptsjson();
		// 转发
		channelgroup.write(new TextWebSocketFrame(conceptsjsonbuffer));

	}
	/**向usernames中添加用户
	 * 
	 */
	public synchronized void addUsername(String username) throws FileNotFoundException, Exception {
		String key = updateuserChannelMap(channelgroup,username);
		if(key==""){
//			Object[] channelarray = channelgroup.toArray();
//			key =((Channel)channelarray[0]).getRemoteAddress().toString();
			return;
		}
//		Object[] channelarray = channelgroup.toArray();
//		String key =((Channel)channelarray[channelgroup.size()-1]).getRemoteAddress().toString(); 
		userChannel.put(key, username);
//		userChannel[key] = username;
//		int i=0;
//		for(i=0;i<usernum;i++){
//			if(usernames.get(i).equals(username)){
////				return;
//				break;
//			}
//		}
//		if(i==usernum){
//			usernames.add(username);
//			usernum++;
////			usernames[usernum++] = username;
//		}
//		String users="";
//		for(int j=0;j<usernum;j++){
//			users+=usernames[j]+";";
//		}
//		String userstr ="{\"type\":\"user\",\n"+"\"name\":\""+users+"\"}";
//		// 转发
//		channelgroup.write(new TextWebSocketFrame(userstr));
	}
	public synchronized String updateuserChannelMap(ChannelGroup channelgroup,String username){
		String key ="";
		Object[] channelarray = channelgroup.toArray();
		
		for(int i=0;i<channelarray.length;i++){
			System.out.println("channelid:"+((Channel)channelarray[i]).getId());
			System.out.println("channel:"+((Channel)channelarray[i]).getRemoteAddress().toString());
		}
		if(channelarrayorigin==null){
			channelarrayorigin = channelarray;
			return ((Channel)channelarrayorigin[0]).getRemoteAddress().toString();
		}else{
			if(channelarrayorigin.length<channelarray.length){//添加了channel
				
				for(int i=0;i<channelarray.length;i++){
					int j=0;
					for(j=0;j<channelarrayorigin.length;j++){
						if(((Channel)channelarrayorigin[j]).getRemoteAddress().toString().equals(((Channel)channelarray[i]).getRemoteAddress().toString())){
							break;
						}
					}
					if(j==channelarrayorigin.length){
						key = ((Channel)channelarray[i]).getRemoteAddress().toString();
						
						break;
					}
				}
				channelarrayorigin = channelarray;
				return key;
			}else{//删除了channel，刷新后是新的channel
				int i=0;
				for(i=0;i<channelarrayorigin.length;i++){
					int j=0;
					for(j=0;j<channelarray.length;j++){
						if(((Channel)channelarrayorigin[i]).getRemoteAddress().toString().equals(((Channel)channelarray[j]).getRemoteAddress().toString())){
							break;
						}
					}
					if(j==channelarray.length){
						String key1 = ((Channel)channelarrayorigin[i]).getRemoteAddress().toString();
						String username1 = userChannel.get(key1);
						if(!username1.equals(username)){
							String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username1+"\"}";
							channelgroup.write(new TextWebSocketFrame(log));
							websockettextfield.setTxt_websocket(username1+"退出了\r\n\r\n");
							userChannel.remove(key1);
						}else{
							userChannel.remove(key1);
						}
						
//						for(i=0;i<channelarray.length;i++){
//							for(j=0;j<channelarrayorigin.length;j++){
//								if(((Channel)channelarrayorigin[j]).getRemoteAddress().toString().equals(((Channel)channelarray[i]).getRemoteAddress().toString())){
//									break;
//								}
//							}
//							if(j==channelarrayorigin.length){
//								key = ((Channel)channelarray[i]).getRemoteAddress().toString();
//								break;
//							}
//						}
						
//						break;
					}
				}
				for(i=0;i<channelarray.length;i++){
					int j=0;
					for(j=0;j<channelarrayorigin.length;j++){
						if(((Channel)channelarray[i]).getRemoteAddress().toString().equals(((Channel)channelarrayorigin[j]).getRemoteAddress().toString())){
							break;
						}
					}
					if(j==channelarrayorigin.length){
						key =  ((Channel)channelarray[i]).getRemoteAddress().toString();
						break;
					}
				}
				channelarrayorigin = channelarray;
				
			}
		}
		return key;
	}
		
		
//		if(userChannel.size()+1>channelarray.length){//删除用户并提醒
////			String username = userChannel.get(((Channel)channelarray[channelarray.length-1]).getRemoteAddress().toString());
////			String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username+"\"}";
////			System.out.println(log+"loginout");
////			channelgroup.write(new TextWebSocketFrame(log));
////			websockettextfield.setTxt_websocket(username+"退出了\r\n\r\n");
////		}
//						Iterator<String> it =userChannel.keySet().iterator();
//////			Set<Entry<String, String>> set = userChannel.entrySet();
//////			Iterator<Entry<String, String>> it = set.iterator();
////						int i=0;
//						String key = it.next();
//						if(key!=((Channel)channelarray[0]).getRemoteAddress().toString()){
//							String username = userChannel.get(key);
//							String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username+"\"}";
//							channelgroup.write(new TextWebSocketFrame(log));
//							websockettextfield.setTxt_websocket(username+"退出了\r\n\r\n");
//							userChannel.remove(key);
//							return ((Channel)channelarray[0]).getRemoteAddress().toString();
////							
//						}
////			while(it.hasNext()){
////				String key1  = it.next();
////				if(((Channel)channelarray[channelarray.length-1-i]).getRemoteAddress().toString()!=key1){
////					String username = userChannel.get(key1);
////					String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username+"\"}";
////					channelgroup.write(new TextWebSocketFrame(log));
////					websockettextfield.setTxt_websocket(username+"退出了\r\n\r\n");
////					return ((Channel)channelarray[i]).getRemoteAddress().toString();
//////					userChannel.remove(key1);
////				}
////				i++;
//////				int j=0;
//////				for(j=0;j<channelarray.length; ++j) {
//////					String key =((Channel)channelarray[j]).getRemoteAddress().toString();
//////					if(key.equals(key1)){
//////						break;
//////					}
//////				}
//////				if(j==channelarray.length){//没有me.getKey();
//////					String username = userChannel.get(key1);
//////					String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username+"\"}";
//////					channelgroup.write(new TextWebSocketFrame(log));
//////					websockettextfield.setTxt_websocket(username+"退出了\r\n\r\n");
//////					userChannel.remove(key1);
//////				}
////			}
//		}
//		return "";
//			for(int i=0;i<userChannel.size();i++){
//				String key1 = userChannel.
//				for(int j=0;j<channelarray.length; ++j) {
//					String key =((Channel)channelarray[i]).getRemoteAddress().toString();
//					if(userChannel.containsKey(key)){
//						break;
//					}
//				}
//				String key =((Channel)channelarray[channelnum]).getRemoteAddress().toString();
//				if(userChannel.)
//			}
//		}

	
	public synchronized void deleteUsername(String username) throws FileNotFoundException, Exception {
		Iterator iter = userChannel.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if(val.equals(username)){
//				String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username1+"\"}";
//				channelgroup.write(new TextWebSocketFrame(log));
//				websockettextfield.setTxt_websocket(username1+"退出了\r\n\r\n");
				userChannel.remove(key);
				for(int i=0;i<channelgroup.toArray().length;i++){
					if(((Channel)channelgroup.toArray()[i]).getRemoteAddress().toString().equals(key)){
						channelgroup.remove((Channel)channelgroup.toArray()[i]);
						channelarrayorigin =channelgroup.toArray();
						break;
					}
//					System.out.println("channelid:"+((Channel)channelarrayorigin[i]).getId());
//					System.out.println("channel:"+((Channel)channelarrayorigin[i]).getRemoteAddress().toString());
				}
				break;
			}
		}
//		String username1 = userChannel.get(key1);
//		if(username1.equals(username)){
////			String log = "{\"type\":\"singleuserout\",\n"+"\"name\":\""+username1+"\"}";
////			channelgroup.write(new TextWebSocketFrame(log));
////			websockettextfield.setTxt_websocket(username1+"退出了\r\n\r\n");
//			userChannel.remove(key1);
//		}
//		if(i==usernum){
//			usernames[usernum++] = username;
//		}
//		String users="";
//		for(int j=0;j<usernum;j++){
//			users+=usernames[j]+";";
//		}
//		String userstr ="{\"type\":\"user\",\n"+"\"name\":\""+users+"\"}";
//		// 转发
//		channelgroup.write(new TextWebSocketFrame(userstr));
	}
	public synchronized String sendUsername(){
		String users="";
		Iterator<String> it =userChannel.keySet().iterator();
		while(it.hasNext())
		{
		    String key = (String)it.next(); // key
		    users+=userChannel.get(key)+";"; // value
		}
//		for(int i=0;i<userChannel.size();i++){
//			users+=userChannel.get(i)+";";
//		}
//		if(usernum==0)
//			return "";
//		
//		for(int i=0;i<usernum;i++){
//			users+=usernames.get(i)+";";
//		}
		users = users.substring(0,users.length()-1);
		System.out.println(users+"ddddd");
		String userstr ="{\"type\":\"user\",\n"+"\"name\":\""+users+"\"}";
//		channelgroup.write(new TextWebSocketFrame(userstr));
		return userstr;
		// 转发
//		channelgroup.write(new TextWebSocketFrame(userstr));
	}
	/** 向列表中的用户转发Json格式的Relation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public synchronized void transforRelationsDataToUsers(RelationsList relationslist) throws FileNotFoundException, Exception {
		
		// 更新WebSocket用户列表
		updateUserList();
		
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String relationsjsonbuffer = relationslist.getRelationsjson();
		// 转发
		channelgroup.write(new TextWebSocketFrame(relationsjsonbuffer));
		
	}
	/** 向列表中的用户转发Json格式的Rule信息 */
	public synchronized void transforRulesDataToUsers(RuleList rulelist) {
		
		// 更新WebSocket用户列表
		updateUserList();
		
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String rulesjsonbuffer = rulelist.sendRuleListstr();
		// 转发
		channelgroup.write(new TextWebSocketFrame(rulesjsonbuffer));
		
	}
	/** 向列表中的用户转发Json格式的增加的单个concept信息 */
	public synchronized void transforaddConceptDataToUsers(ConceptsList conceptlist) {

		// 更新WebSocket用户列表
		updateUserList();
				
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String addconceptbuffer = conceptlist.getconceptstr();
		// 转发
		channelgroup.write(new TextWebSocketFrame(addconceptbuffer));

	}
	/** 向列表中的用户转发Json格式的修改的单个concept信息 */
	public synchronized void transforupdateConceptDataToUsers(ConceptsList conceptlist) {

		// 更新WebSocket用户列表
		updateUserList();
				
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String updateconceptbuffer = conceptlist.getconceptstr();
		// 转发
		channelgroup.write(new TextWebSocketFrame(updateconceptbuffer));

	}
	
	/** 向列表中的用户转发Json格式的修改对的relation信息 
	 * @throws Exception 
	 * @throws FileNotFoundException */
	public synchronized void transformodifyRelationDataToUsers(RelationsList relationslist) throws FileNotFoundException, Exception {

		// 更新WebSocket用户列表
		updateUserList();
				
		if(channelgroup.isEmpty())
			return;
		
		// 创建发送信息
		String modifyrelationbuffer = relationslist.getRelationsjson();
		// 转发
		channelgroup.write(new TextWebSocketFrame(modifyrelationbuffer));
		String graphjson = relationslist.getgraphjson();
		channelgroup.write(new TextWebSocketFrame(graphjson));

	}
	public synchronized void transformodifyRuleToUsers(RuleList rulelist){
		
		// 更新WebSocket用户列表
		updateUserList();
				
		if(channelgroup.isEmpty())
			return;
				
		// 创建发送信息
		String rulebuffer = rulelist.getRulestr();
		// 转发
		channelgroup.write(new TextWebSocketFrame(rulebuffer));
	}
	public synchronized void transforlog(String log){
		// 更新WebSocket用户列表
				updateUserList();
						
				if(channelgroup.isEmpty())
					return;
				// 转发
				channelgroup.write(new TextWebSocketFrame(log));
	}
	/** 更新WebSocket用户列表 */
	private void updateUserList() {
		// 更新显示
		updateUserListOutput(channelgroup);
		
		// 更新用户列表记录文件
		// 通道数目有变化时 或者 每处理100条数据后 更新
		if (channelnum != channelgroup.size()) {	// 通道数目有变化
			
			channelnum = channelgroup.size();
			updateUserListFile(channelgroup);
			
//			updateuserChannelMap(channelgroup);
			websockettextfield.setTxt_websocket("\r\n用户数量变化：更新用户列表\r\n");
		}
		else if(sendingtimes%100 == 0)	// 每处理100条数据
		{
			sendingtimes = 0;
			updateUserListFile(channelgroup);
			
			websockettextfield.setTxt_websocket("\r\n自动更新用户列表\r\n");
		}
		websockettextfield.setTxt_websocketnumber(String.valueOf(channelgroup.size()));
		
		sendingtimes++;
	}
	/** 更新WebSocket用户列表记录文件 */
	private void updateUserListFile(ChannelGroup channelgroup) {

		Object[] channelarray = channelgroup.toArray();
		
		// 文件
		FileWriter filewriter = null;
		PrintWriter out = null;
		
		try {
			filewriter = new FileWriter(userlistfilename);// 创建输出流
			out = new PrintWriter(filewriter);
			StringBuffer buffer = new StringBuffer("");
			
			for (int i = 0; i < channelarray.length; ++i) {
				buffer = buffer.append(((Channel)channelarray[i]).getRemoteAddress().toString());
				buffer = buffer.append("\r\n");
			}
			out.print(buffer.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "Userlist文件写入错误");
			System.err.println("Userlist文件写入错误");
		} finally
		{
			if (out != null) {
				out.close();
				out = null;
			}
			if (filewriter != null) {
				try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				filewriter = null;
			}
		}
	}

	/** 更新WebSocket用户列表输出显示 */
	private void updateUserListOutput(ChannelGroup channelgroup) {
		
		if(channelgroup.isEmpty())
			return;

		Object[] channelarray = channelgroup.toArray();
		
		websockettextfield.setTxt_websocket("\r\n向下列用户转发数据：\r\n");
		
		for (int i = 0; i < channelarray.length; ++i) {
			websockettextfield.setTxt_websocket(((Channel)channelarray[i]).getRemoteAddress().toString() + "\r\n");
		}
		
	}

	
}
