package org.act.knowledge.element.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Rule {
	private String rulename;//一个规则的mathml名字
	private String Rulestr;//一个规则的xml字符串表示
	public Rule(){
		
	}
	
	public Rule(String rulename,String rulestr){
		
		this.rulename = rulename;
		this.Rulestr = rulestr;
	}
	/** 取得conceptstr */
	public synchronized String getRulestr(){
		return Rulestr;
	}
	
	/** 设置conceptstr */
	public synchronized void setRulestr(String rulestr) {
		this.Rulestr = rulestr;
	}
	
	public String getRulename() {
		return rulename;
	}

	public void setRulename(String rulename) {
		this.rulename = rulename;
	}
	
}
