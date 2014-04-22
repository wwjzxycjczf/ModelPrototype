package org.act.knowledge.element.message;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageLog {

	private List<Message> messagelist = null;// 消息列表
	private final String messagelogfilename = "messagelog.txt";
	private String messagestr = "";// 单条消息

	public MessageLog() {
		messagelist = Collections.synchronizedList(new ArrayList<Message>());
	}

	public synchronized void updateMessageList(String mess) {
		// Message message = null;
		String[] strarr = mess.split(":");
		// String name = mess.substring(0,mess.indexOf(":"));
		Message message = new Message(strarr[0], strarr[1], strarr[2],
				strarr[3]);
		messagelist.add(message);
		messagestr = mess;
	}

	public synchronized String getmessagestr() {
		return this.messagestr;
	}

	/** 更新消息列表信息记录文件 */
	public synchronized void updateMessageListFile() {
		FileWriter filewriter = null;
		PrintWriter out = null;
		try {
			filewriter = new FileWriter(messagelogfilename);// 创建输出流
			out = new PrintWriter(filewriter);

			StringBuffer buffer = new StringBuffer("");
			// 写入站点信息
			for (int i = 0; i < messagelist.size(); ++i) {

				buffer = buffer.append("Name:" + messagelist.get(i).getName()
						+ '\t');// 发消息人的名字
				buffer = buffer.append("IP:" + messagelist.get(i).getIp()
						+ '\t');// 发消息的ip地址
				buffer = buffer.append("Port:" + messagelist.get(i).getPort()
						+ '\t');// 发消息的端口
				buffer = buffer.append("message:"
						+ messagelist.get(i).getMess() + '\t');// 消息

				buffer = buffer.append("\r\n");
			}
			out.print(buffer.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// JOptionPane.showMessageDialog(null, "站点文件写入错误");
			System.err.println("消息文件写入错误");
			e.printStackTrace();

		} finally {
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
}
