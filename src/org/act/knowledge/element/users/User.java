package org.act.knowledge.element.users;

import org.jboss.netty.channel.Channel;

public class User {

	private Channel channel = null;//通道
	private String URL = "";	//网络地址
	private String ip = "";	//IP地址
	private int port = 0;		//端口号 
	private String name = "";//名字
	private String pwd = "";//密码
	
	public User() {

	}
	
	public User( Channel channel, String URL, String ip, int port) {
		this.channel = channel;
		this.URL = URL;
		this.ip = ip;
		this.port = port;
	}
	/** 取得通道 */
	public synchronized Channel getChannel() {
		return channel;
	}
	/** 设置通道 */
	public synchronized void setChannel(Channel channel) {
		this.channel = channel;
	}

	/** 取得IP */
	public synchronized String getIp() {
		return ip;
	}
	/** 取得端口 */
	public synchronized int getPort() {
		return port;
	}

	/** 取得URL */
	public synchronized String getURL() {
		return URL;
	}

	/** 设置URL，同时设置IP和Port */
	public synchronized void setURL(String URL)
	{
		this.URL = URL ;
		String temp[] = URL.split(":");
		
		this.ip = temp[0].replace("/", "");
		this.port = Integer.parseInt(temp[1]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
