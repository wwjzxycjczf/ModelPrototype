package org.act.knowledge.element.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.act.knowledge.element.textfield.WebSocketTextField;

public class LogList {
	private WebSocketTextField websockettextfield = null;	// WebSocket状态栏
	
	private final String logfilename="log.txt";
	public LogList(WebSocketTextField websockettextfield){
		this.websockettextfield = websockettextfield;
		
	}
	/**将用户消息加入log.txt中*/
	private void updateLogListFile(Log log){
		// 文件
		FileWriter filewriter = null;
		PrintWriter out = null;
				
		try {
			filewriter = new FileWriter(logfilename);// 创建输出流
			out = new PrintWriter(filewriter);
			StringBuffer buffer = new StringBuffer("");
			buffer = buffer.append(log.getUsername());
			buffer = buffer.append(log.getLogtext());
			buffer = buffer.append("\r\n");
			out.print(buffer.toString());
					
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					JOptionPane.showMessageDialog(null, "Userlist文件写入错误");
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
	/** 更新WebSocket用户列表输出显示*/
	private void updateLogListOutput(Log log){
		websockettextfield.setTxt_websocket("\r\n有消息：\r\n");
		websockettextfield.setTxt_websocket(log.getUsername()+":"+log.getLogtext());
	}
}
