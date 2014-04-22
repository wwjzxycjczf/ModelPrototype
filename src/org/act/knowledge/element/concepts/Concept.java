package org.act.knowledge.element.concepts;

public class Concept {
	private String conceptstr;//一个概念的xml字符串表示
	
	public Concept(){
		
	}
	
	public Concept(String conceptstr){
		this.conceptstr = conceptstr;
	}
	/** 取得conceptstr */
	public synchronized String getConceptstr(){
		return conceptstr;
	}
	
	/** 设置conceptstr */
	public synchronized void setConceptstr(String conceptstr) {
		this.conceptstr = conceptstr;
	}
}
