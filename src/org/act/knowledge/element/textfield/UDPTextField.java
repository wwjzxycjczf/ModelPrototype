package org.act.knowledge.element.textfield;

import java.awt.TextArea;
import java.awt.TextField;

public class UDPTextField {
	
	private TextArea txt_udp = null; 			// UDP服务器状态
	private TextField txt_udpnumber = null; 	// 处理的UDP包数量
	
	public UDPTextField(TextArea txt_udp, TextField txt_udpnumber)
	{
		this.txt_udp = txt_udp;
		this.txt_udpnumber = txt_udpnumber;
	}

	public synchronized void setTxt_udp(String str_udp) {
		
		txt_udp.append(str_udp);
		updateUDPPackageNumber();
	}
	
	/** 更新显示的处理UDP包总数 */
	private void updateUDPPackageNumber()
	{
		String str_udpnumber = txt_udpnumber.getText().trim();
		int udpnumber = 0;
		if (str_udpnumber.length() == 0)
			udpnumber = 0;
		else
			udpnumber = Integer.valueOf(str_udpnumber);
		++udpnumber;
		txt_udpnumber.setText(String.valueOf(udpnumber));
	}
}
