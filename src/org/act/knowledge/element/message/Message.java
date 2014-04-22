package org.act.knowledge.element.message;

public class Message {
	private String name;//说话名字
	private String mess;//说话内容
	private String ip;//ip地址
	private String port;//端口
	public Message()
	{
		
	}
	public Message(String name,String mess,String ip,String port)
	{
		this.name = name;
		this.mess = mess;
		this.ip = ip;
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
