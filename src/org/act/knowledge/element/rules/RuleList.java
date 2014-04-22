package org.act.knowledge.element.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RuleList {
	private String ruleoutpath = "resources/knowledge graph1/rules/";

	private List<Rule> rulelist = null;
	private String rulestr ="";
	/**
	 * 更新规则列表
	 * 
	 * @throws IOException
	 */
	public RuleList(){
		rulelist  = Collections.synchronizedList(new ArrayList<Rule>());
	}
	public synchronized void updateRuleList(String rulename,String rulestring)
			throws IOException {
//		String[] strarr = rulestring.split(":");
//		if (strarr[0].equalsIgnoreCase("addRule")) {
//			Rule rule = new Rule(strarr[1], strarr[2]);
//			rulelist.add(rule);
//			saveRulestr(strarr[1], strarr[2]);
//			rulestr =  "{\"type\":\"addrule\",\n" + "\"rule\":{"+"\"rulename\":" + strarr[1] + ",\"rulestr\":" + strarr[2]+"}}";
//			System.out.println("addRule成功");
//			//			return "add:" + strarr[1] + ":" + strarr[2];
//		} else if (strarr[0] .equalsIgnoreCase("deleteRule")) {
//			for (int i = 0; i < rulelist.size(); i++) {
//				if (((Rule) rulelist.get(i)).getRulename() == strarr[1]) {
//					rulelist.remove(i);
//					deleteRule(strarr[1]);
//					break;
//				}
//			}
//			rulestr = "{\"type\":\"deleterule\",\n" + "\"rule\":{"+"\"rulename\":" + strarr[1] + ",\"rulestr\":" + strarr[2]+"}}";
//			System.out.println("deleteRule成功");
//			//			return "delete:" + strarr[1] + ":" + strarr[2];
//		} else if(strarr[0] .equalsIgnoreCase("updateRule")){
//			Rule rule = new Rule(strarr[1], strarr[2]);
//			for (int i = 0; i < rulelist.size(); i++) {
//				if (((Rule) rulelist.get(i)).getRulename() == strarr[1]) {
//					rulelist.remove(i);
//					deleteRule(strarr[1]);
//					break;
//				}
//			}
//			rulelist.add(rule);
//			saveRulestr(strarr[1], strarr[2]);
//			rulestr =  "{\"type\":\"updaterule\",\n" + "\"rule\":{"+"\"rulename\":" + strarr[1] + ",\"rulestr\":" + strarr[2]+"}}";
//			System.out.println("updateRule成功");
//		}
//			return ;
		Rule rule = new Rule(rulename, rulestring);
		for (int i = 0; i < rulelist.size(); i++) {
			if (((Rule) rulelist.get(i)).getRulename() == rulename) {
				rulelist.remove(i);
				deleteRule(rulename);
				break;
			}
		}
		rulelist.add(rule);
		saveRulestr(rulename, rulestring);
		rulestr =  "{\"type\":\"updaterule\",\n" + "\"rule\":{"+"\"rulename\":" + rulename + ",\"rulestr\":" + rulestring+"}}";
		System.out.println("updateRule成功");
	}

	public synchronized void addRuleList(String rulename,String rulestring) throws IOException{
		Rule rule = new Rule(rulename, rulestring);
		rulelist.add(rule);
		saveRulestr(rulename, rulestring);
		rulestr =  "{\"type\":\"addrule\",\n" + "\"rule\":{"+"\"rulename\":" + rulename + ",\"rulestr\":" + rulestring+"}}";
		System.out.println("addRule成功");
	}
	public synchronized void deleteRuleList(String rulename,String rulestring) throws IOException{
		for (int i = 0; i < rulelist.size(); i++) {
			if (((Rule) rulelist.get(i)).getRulename() == rulename) {
				rulelist.remove(i);
				deleteRule(rulename);
				break;
			}
		}
		rulestr = "{\"type\":\"deleterule\",\n" + "\"rule\":{"+"\"rulename\":" + rulename + ",\"rulestr\":" +rulestring+"}}";
		System.out.println("deleteRule成功");
	}
	
	public synchronized void readFolder(String filePath){
//		rulelist = new List<Rule>();
		try {  
            //读取指定文件夹下的所有文件  
            File file = new File(filePath);  
            if (!file.isDirectory()) {  
                System.out.println("---------- 该文件不是一个目录文件 ----------");  
            } else if (file.isDirectory()) {  
                System.out.println("---------- 这是一个目录文件夹 ----------");  
                String[] filelist = file.list();  
               
	                for (int i = 0; i < filelist.length; i++) {  
	                	
	                    File readfile = new File(filePath + "\\" + filelist[i]);  
	                    //String path = readfile.getPath();//文件路径  
	                    String absolutepath = readfile.getAbsolutePath();//文件的绝对路径  
	                    String filename = readfile.getName();//读到的文件名  
	                    String content = getRule(absolutepath);//调用 readFile 方法读取文件夹下所有文件  
	                    Rule rule = new Rule();
	                    rule.setRulename(filename);
	                    rule.setRulestr(content);
//	                    Rule rule = new Rule(filename,content);
	                    rulelist.add(rule);
	                 
                }
                System.out.println("---------- 所有文件操作完毕 ----------");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
	/** 创建规则信息发给客户端 */
	public synchronized String sendRuleListstr() {
		while(rulelist.size()!=0)
		{
			rulelist.remove(0);
		}
		readFolder(ruleoutpath);
		StringBuffer buffer = new StringBuffer("{\"type\":\"rulelist\",\"data\":[");
		if (rulelist != null) {
			for (int i = 0; i < rulelist.size(); i++) {
				buffer = buffer.append( "{");
				buffer = buffer.append( "\"rulename\":\"");
				buffer = buffer.append( rulelist.get(i).getRulename());
				buffer = buffer.append( "\",");
				buffer = buffer.append( "\"rulestr\":\"");// 相控阵、低空监视、FMCW
				buffer = buffer.append( rulelist.get(i).getRulestr());
				buffer = buffer.append( "\"");

//				buffer = buffer.append("\"" + rulelist.get(i).getRulename()	+ "\":\"");
//				buffer = buffer.append("\"");
//				buffer = buffer.append(rulelist.get(i).getRulestr());
				if (i == rulelist.size() - 1) {
					buffer = buffer.append("}");
					break;
				} else
					buffer = buffer.append("},");
			}
		}
		buffer = buffer.append("]}");
//		System.out.println(buffer);
		return buffer.toString();
	}

	public synchronized void saveRulestr(String name, String rulestr)
			throws IOException {// rulestr格式为name:rule
	// String name = rulestr.substring(0,rulestr.indexOf(":"));
	// String rulestring = rulestr.substring(rulestr.indexOf(":")+1);
	// Rule rule = new Rule(rulestring,name);
		String path = ruleoutpath + name;
		// ruleoutpath = ruleoutpath+name;
		FileWriter fw = new FileWriter(path);
		fw.write(rulestr);
		fw.flush();
		fw.close();
		// return rule;
		// rulestr = "add:"+rulestr;
		// return rulestr;
	}

	public synchronized String getRule(String filepath) {
		File file = new File(filepath);
		BufferedReader reader = null;
		String rule = "";
		try {
//			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
//			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				rule += tempString;
//				System.out.println("line " + line + ": " + tempString);
//				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return rule;
	}

	public synchronized void deleteRule(String name) {
		// String id = rule.substring(0,rule.indexOf(":"));
		// String name = rule.substring(rule.indexOf(":")+1);
		String path = ruleoutpath + name;

		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("del " + path);
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public String getRulestr() {
		return rulestr;
	}

	public void setRulestr(String rulestr) {
		this.rulestr = rulestr;
	}

}
