package org.act.knowledge.element.textfield;

import java.awt.TextArea;
import java.awt.TextField;

public class WebSocketTextField {

	private TextArea txt_websocket = null; // WebSocket服务器状态
	private TextField txt_websocketnumber = null; // 处理WebSocket的数量
	
	public WebSocketTextField(TextArea txt_websocket, TextField txt_websocketnumber)
	{
		this.txt_websocket = txt_websocket;
		this.txt_websocketnumber = txt_websocketnumber;
	}

	public synchronized void setTxt_websocket(String str_websocket) {
		txt_websocket.append(str_websocket);
	}

	public synchronized void setTxt_websocketnumber(String str_websocketnumber) {
		txt_websocketnumber.setText(str_websocketnumber);
	}
	
}
