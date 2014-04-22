package org.act.knowledge;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KnowledgeManagement {
	public  Element root;
//	public  NodeList list; 
	public  Document xmldocgraph;
	public static String outPath = "resources/knowledge graph1/graph.xml";
	public  Element getElementByID(String id){
		
		NodeList list = this.xmldocgraph.getElementsByTagName("Element");
		int i;
		for(i=0;i<list.getLength();i++){
			Element ele = (Element)list.item(i);
			if(ele.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){
				return ele;
			}
		}
		return null;
	}
	public  Element getElementByName(String name){
		NodeList list = this.xmldocgraph.getElementsByTagName("Element");
		int i;
		for(i=0;i<list.getLength();i++){
			Element ele = (Element)list.item(i);
			if(ele.getAttributes().getNamedItem("Name").getNodeValue().equals(name)){
				return ele;
			}
		}
		return null;
	}
	public  void insertKnowledge(Element ele,String id){
		NodeList list = this.xmldocgraph.getElementsByTagName("Element");
		int i;
		for(i=0;i<list.getLength();i++){
			Element ele1 = (Element)list.item(i);
			if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
				if(ele1.equals(ele)){//两条知识相等
					return;
				}else{//融合
					for(Node node = ele.getFirstChild();node!=null;node=node.getNextSibling()){
						 if (node.getNodeType() == Node.ELEMENT_NODE) {  
			                 if (node.getNodeName().equals("Variables")) {  
			                	 if(ele1.getElementsByTagName("Variables").item(0)==null){//如果ele1中没有Variables
			                		 ele1.appendChild(node);
			                	 }else{//ele1中有variables
			                		 continue;
			                	 }
			                 }
			                 if(node.getNodeName().equals("Description")) {
			                	 if(ele1.getElementsByTagName("Description").item(0)==null){//如果ele1中没有Description
			                		 ele1.appendChild(node);
			                	 }else{//ele1中有Description
			                		 continue;
			                	 }
			                 }
			                 if(node.getNodeName().equals("property")) {
			                	 if(ele1.getElementsByTagName("property").item(0)==null){//如果ele1中没有property
			                		 ele1.appendChild(node);
			                	 }else{//ele1中有property
			                		 continue;
			                	 }
			                 }
			                 if(node.getNodeName().equals("DependentVariables")) {
			                	 if(ele1.getElementsByTagName("DependentVariables").item(0)==null){//如果ele1中没有DependentVariables
			                		 ele1.appendChild(node);
			                	 }else{//ele1中有DependentVariables,要进行融合
			                		 if(ele1.getElementsByTagName("DependentVariables").item(0).equals(node)){
			                			 continue;
			                		 }else{//不一样
				                		 for(Node node1 = node.getFirstChild();node1!=null;node1=node1.getNextSibling()){
				                			 if (node1.getNodeType() == Node.ELEMENT_NODE) {
				                				 Node ele1node = ele1.getElementsByTagName("DependentVariables").item(0);
				                				 Node node2;
				                				 for(node2 = ele1node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
				                					 if(node2.equals(node1)){
				                						 break;
				                					 }
				                				 }
				                				 if(node2==null){//没有ele中的concept，则要添加
				                					 ele1node.appendChild(node1);
				                				 }
				                			 }
				                		 }
			                		 }
			                	 }
			                 }
						 }
					}
				}
			}
		}
		if(i==list.getLength()){//不存在此条知识则添加
				root.appendChild(ele);
				xmldocgraph.appendChild(ele);
		
		}
		fillgraph();
		
	}
	public  void deleteKnowledgeByID(String id){
		NodeList list = this.xmldocgraph.getElementsByTagName("Element");
		int i;
		for(i=0;i<list.getLength();i++){
			Element ele1 = (Element)list.item(i);
			if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
				ele1.getParentNode().removeChild(ele1);
				break;
			}
		}
		fillgraph();
	}
	public  void deleteKnowledgeByName(String name){
		NodeList list = this.xmldocgraph.getElementsByTagName("Element");
		int i;
		for(i=0;i<list.getLength();i++){
			Element ele1 = (Element)list.item(i);
			if(ele1.getAttributes().getNamedItem("Name").getNodeValue().equals(name)){//存在此条知识
				ele1.getParentNode().removeChild(ele1);
				break;
			}
		}
		fillgraph();
	}
	public  void setroot(Element root){
		this.root = root;
	}
	public Element getroot(){
		return this.root;
	}
//	public  void setlist(NodeList list){
//		this.list = list;
//	}
//	public NodeList getlist(){
//		return this.list;
//	}
	public  void setxmldocgraph(Document xmldocgraph){
		this.xmldocgraph = xmldocgraph;
	}
	public Document getxmldocgraph(){
		return this.xmldocgraph;
	}
	public void fillgraph(){
		 TransformerFactory transFactory = TransformerFactory.newInstance();  
		    try {  
		 Transformer transformer = transFactory.newTransformer();  
	      // 设置各种输出属性  
	      transformer.setOutputProperty("encoding", "UTF-8");  
	      transformer.setOutputProperty("indent", "yes");  
		 DOMSource source = new DOMSource();  
	      // 将待转换输出节点赋值给DOM源模型的持有者(holder)  
	      source.setNode(this.root);
		StreamResult result = new StreamResult();  
//	      if (filename == null) {  
//	        // 设置标准输出流为transformer的底层输出目标  
//	        result.setOutputStream(System.out);  
//	      } else {  
	        result.setOutputStream(new FileOutputStream(outPath));  
	        transformer.transform(source, result);
//	      }  
		    } catch (TransformerConfigurationException e) {  
		        e.printStackTrace();  
		    } catch (TransformerException e) {  
		      e.printStackTrace();  
		    } catch (FileNotFoundException e) {  
		      e.printStackTrace();  
		    } 

   }
}
