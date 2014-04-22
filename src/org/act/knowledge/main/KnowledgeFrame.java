package org.act.knowledge.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


public class KnowledgeFrame extends JFrame {
	private static final long serialVersionUID = 208223787166682391L;

	private JPanel panel_websocket;// WebSocket区
	private JPanel panel_port; // port输入区
	private JPanel panel_websocket_number;// WebSocket区计数行

	private JPanel panel_port_lbl; // port输入区Label列
	private JPanel panel_port_txt; // port输入区TextField列
	// 输入框
	private TextField txt_ip;// IP地址
	private TextField txt_websocketport; // WebSocket port
	private TextArea txt_websocket; // WebSocket服务器状态
	private TextField txt_websocketnumber; // 处理WebSocket的数量
	// 按钮
	private JButton btn_connect; // 连接
	private JButton btn_disconnect; // 断开连接
	// 标签
	private Label lbl_ip;// IP地址
	private Label lbl_websocketport;// WebSocket Port
	private Label lbl_websocket;// WebSocket
	private Label lbl_websocketnumber;// WebSocket Numbe

	private KnowledgePublisher knowledgepublisher;

	public KnowledgeFrame() {
		// 获取本机ip
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "本机IP地址获取失败");
		}
		panel_websocket = new JPanel(); // WebSocket区
		panel_port = new JPanel(); // port输入区
		panel_port_lbl = new JPanel();// port输入区Label列
		panel_port_txt = new JPanel(); // port输入区TextField列
		panel_websocket_number = new JPanel();// WebSocket区计数行
		// 输入框定义
		txt_ip = new TextField(ip, 50);// IP
		txt_websocketport = new TextField("5001", 50); // WebSocket port
		// 显示框定义
		txt_websocket = new TextArea(); // WebSocket服务器状态
		txt_websocketnumber = new TextField("0", 10);// 处理WebSocket的数量
		// 标签定义
		lbl_ip = new Label("IP:");
		lbl_websocketport = new Label("WebSocket Port：");
		lbl_websocket = new Label("WebSocket:");
		lbl_websocketnumber = new Label("当前WebSocket连接数量:");
		// 按钮定义
		btn_connect = new JButton("连接");
		btn_disconnect = new JButton("断开连接");

		InitComponent();
		knowledgepublisher = new KnowledgePublisher(txt_websocket,
				txt_websocketnumber);

	}
	private void InitComponent() {
		// 输入框配置
				txt_ip.setMaximumSize(new Dimension(300, 30));// 设置显示大小
				txt_websocketport.setMaximumSize(new Dimension(300, 30));
				// 显示框配置
				txt_websocket.setEditable(false);
				txt_websocketnumber.setEditable(false);
				// 标签配置
				lbl_ip.setMaximumSize(new Dimension(200, 30));// 设置显示大小
				lbl_websocketport.setMaximumSize(new Dimension(200, 30));
				lbl_websocket.setMaximumSize(new Dimension(200, 30));
				lbl_websocketnumber.setMaximumSize(new Dimension(200, 30));
				// 按钮配置
				btn_disconnect.setEnabled(false);
				btn_disconnect.setVisible(false);

				// 布局
				componentSet_Main();

				// 设置按钮操作
				addAction();
	}
	// 整体布局
		private void componentSet_Main() {

			// 中间的可移动分隔符
			JPanel jpanel_manager = new JPanel();
//			JSplitPane jspane_manager = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//					false, panel_udp, panel_websocket);
//			jspane_manager.setDividerLocation(0.3);
//
//			jspane_manager.setResizeWeight(0.3);
//			jspane_manager.setDividerSize(10);// 设置分隔线宽度的大小，以pixel为计算单位。

			jpanel_manager.add(panel_websocket);
			jpanel_manager.setLayout(new GridLayout());

			componentSet_Port(); // Port输入区布局
//			componentSet_UDP(); // UDP区布局
			componentSet_WebSocket(); // WebSocket区布局

			this.add(panel_port);
			this.add(jpanel_manager);

			this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

			this.setSize(1200, 800);
			this.setVisible(true);

			// 设置右上角关闭按钮
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {

					knowledgepublisher.processStop();
					System.exit(0);// 退出
				}
			});
		}
		// Port输入区布局
		private void componentSet_Port() {
			// port输入区Label列
			// 配置
			panel_port_lbl
					.setLayout(new BoxLayout(panel_port_lbl, BoxLayout.Y_AXIS));
			panel_port_lbl.setMaximumSize(new Dimension(200, 60));
			// 添加元素
			panel_port_lbl.add(lbl_ip);
			panel_port_lbl.add(lbl_websocketport);
			// port输入区TextField列
			// 配置
			panel_port_txt
					.setLayout(new BoxLayout(panel_port_txt, BoxLayout.Y_AXIS));
			panel_port_txt.setMaximumSize(new Dimension(200, 60));
			// 添加元素
			panel_port_txt.add(txt_ip);
			panel_port_txt.add(txt_websocketport);

			componentRepaint_Port();// Port输入区重画
		}
		// Port输入区重画
		private void componentRepaint_Port() {
			// 移除元素
			panel_port.removeAll();
			// 添加元素
			panel_port.add(panel_port_lbl);
			panel_port.add(panel_port_txt);
			panel_port.add(btn_connect);
			panel_port.add(btn_disconnect);
			// 设置显示大小
			panel_port.setMaximumSize(new Dimension(800, 100));
		}
		
		// WebSocket区布局
		private void componentSet_WebSocket() {
			// 计数区配置
			panel_websocket_number.setLayout(new BoxLayout(panel_websocket_number,
					BoxLayout.X_AXIS));
			panel_websocket_number.setMaximumSize(new Dimension(300, 60));
			// 计数区添加元素
			panel_websocket_number.add(lbl_websocketnumber);
			panel_websocket_number.add(txt_websocketnumber);

			// 添加元素
			panel_websocket.add(lbl_websocket);
			panel_websocket.add(txt_websocket);
			panel_websocket.add(panel_websocket_number);
			// 设置布局方式为BoxLayout，纵向
			panel_websocket.setLayout(new BoxLayout(panel_websocket,
					BoxLayout.Y_AXIS));
		}

		// 动作
		private void addAction() {
			// 连接
			btn_connect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {

					String websocketport_str = txt_websocketport.getText().trim();
					String ip_str = txt_ip.getText().trim();

//					if (!isNumber(udpport_str)) {
//						JOptionPane.showMessageDialog(null, "UDP Port输入错误");
//					} else
					if (!isNumber(websocketport_str)) {
						JOptionPane.showMessageDialog(null, "WebSocket Port输入错误");
					} else if (!isIP(ip_str)) {
						JOptionPane.showMessageDialog(null, "IP地址输入错误");
					} else {

//						int udpport = Integer.valueOf(udpport_str);
						int websocketport = Integer.valueOf(websocketport_str);

						try {

							knowledgepublisher.processStart(ip_str,
									websocketport);
							// 若出错则不执行下面的指令
							txt_ip.setEditable(false);
//							txt_udpport.setEditable(false);
							txt_websocketport.setEditable(false);
							btn_connect.setEnabled(false);
							btn_connect.setVisible(false);
							btn_disconnect.setEnabled(true);
							btn_disconnect.setVisible(true);
							componentRepaint_Port();// Port输入区重画
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			// 断开连接
			btn_disconnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {

					knowledgepublisher.processStop();

					txt_ip.setEditable(true);
					txt_websocketport.setEditable(true);
					btn_connect.setEnabled(true);
					btn_connect.setVisible(true);
					btn_disconnect.setEnabled(false);
					btn_disconnect.setVisible(false);

					txt_websocket.setText(""); // WebSocket服务器状态
					txt_websocketnumber.setText("0"); // 处理WebSocket的数量

					componentRepaint_Port();// Port输入区重画

				}
			});
		}
		
		// 判断输入为数字
		private boolean isNumber(String str) {
			boolean isnumber = true;
			if (str.length() == 0)
				isnumber = false;// 空
			for (int i = 0; i < str.length(); ++i) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;// 非数字
				}
			}
			return isnumber;
		}

		public boolean isIP(String str_ip) {
			// 空串
			if (str_ip.length() == 0)
				return false;
			// 非数字或'.'
			for (int i = 0; i < str_ip.length(); ++i) {
				if (!Character.isDigit(str_ip.charAt(i)) && str_ip.charAt(i) != '.') {
					return false;
				}
			}
			// 位数过多，头尾含'.'
			if (str_ip.charAt(0) == '.'
					|| str_ip.charAt(str_ip.length() - 1) == '.'
					|| str_ip.length() > 15) {
				return false;
			}
			String[] number = str_ip.split("\\.");
			// '.'的个数不正确
			if (number.length != 4)
				return false;
			// 地址位大于255
			for (int i = 0; i < number.length; ++i) {
				if (Integer.valueOf(number[i]) > 255)
					return false;
			}

			return true;

		}

		public static void main(String args[]) throws Exception {
			new KnowledgeFrame();
		}


}
