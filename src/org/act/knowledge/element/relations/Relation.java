package org.act.knowledge.element.relations;

public class Relation {
	private String relationstr;//一个概念的xml字符串表示
	
	public Relation(){
		
	}
	
	public Relation(String relationstr){
		this.relationstr = relationstr;
	}
	/** 取得conceptstr */
	public synchronized String getRelationstr(){
		return relationstr;
	}
	
	/** 设置conceptstr */
	public synchronized void setRelationstr(String relationstr) {
		this.relationstr = relationstr;
	}
}
