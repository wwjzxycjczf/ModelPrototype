package org.act.knowledge.element.log;

public class Log {

	private String username = "";
	private String logtext = "";
	
	public Log(){
		
	}
	public Log(String username,String logtext){
		this.username = username;
		this.logtext = logtext;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLogtext() {
		return logtext;
	}

	public void setLogtext(String logtext) {
		this.logtext = logtext;
	}
	
	
}
