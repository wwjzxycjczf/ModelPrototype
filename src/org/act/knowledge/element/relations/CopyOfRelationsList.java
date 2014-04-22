package org.act.knowledge.element.relations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.act.server.XMLJsonTraverse;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CopyOfRelationsList {
	private String RelationsXML = "resources/knowledge graph1/Relations.xml";
	private XMLJsonTraverse xmljsontraverse= new XMLJsonTraverse();
	private Document relationxml;
	private Element root;
	/** 读取Concepts的xml文档，设置Concept列表 
	 * @throws Exception */
	public synchronized void setNetNodeList() throws Exception{
//		conceptsList = Collections.synchronizedList(new ArrayList<Concept>());// 新建Oid列表, 不与get方法冲突
		
	}
	/** 读取Concepts的xml文档，设置Concept列表 
	 * @throws Exception */
	public synchronized void readRelationsXml(){
		relationxml = (Document) xmljsontraverse.parse(RelationsXML);
		root = (Element) relationxml.getDocumentElement();
	}
	public synchronized String getRelationsjson() throws FileNotFoundException, Exception{
//		JSON json = readFromStream(new FileInputStream(RelationsXML));
//      return json.toString(2);
//		String relationjson = "{\"type\":\"conceptlist\",\n" + "\"data\":"+ json.toString()+"}";
//		return relationjson;
		 XMLSerializer xmlSerializer = new XMLSerializer();
		 JSON json = xmlSerializer.readFromFile(new File(RelationsXML));
		 String relationjson = "{\"type\":\"relationlist\",\n" + "\"data\":"+json.toString()+"}";
		 System.out.print(relationjson);
		 return relationjson;
	}
	public static JSON readFromStream(InputStream stream) throws Exception {
        StringBuffer xml = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
        String line = null;
        while ((line = in.readLine()) != null) {
            xml.append(line);
        }
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.read(xml.toString());
    }
	/** 添加一条concept知识 
	 * @throws Exception */
	public synchronized void addRelation(String relation) throws DocumentException{//将xml字符串转为文档
		Document xml = (Document) DocumentHelper.parseText(relation);//便得到xml文档
		readxmldocrelation(xml,"add");
//		return getRelationsjson();
	}
	public synchronized  void insertKnowledge(Element ele,String id,String name,String type){
		NodeList list = relationxml.getElementsByTagName("Element");
		System.out.println("ele::"+name);
		int i;
		if(type=="Merge"){
		for(i=0;i<list.getLength();i++){
			Element ele1 = (Element)list.item(i);
			if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
					for(Node node = ele.getFirstChild();node!=null;node=node.getNextSibling()){
						 if (node.getNodeType() == Node.ELEMENT_NODE) {  
							
			                 if (node.getNodeName().equals("Variable")) {  
			                	 if(ele1.getElementsByTagName("Variable").item(0)==null){//如果ele1中没有Variables
			                		 Node firstDocImportedNode = relationxml.importNode(node, true);
			                		 ele1.appendChild(firstDocImportedNode );
			                	 }else{
				                	 Element nodev1 = (Element) node;
				                	 Element nodev2 = null;
				                	 for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
										 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
							                 if (node1.getNodeName().equals("Variable")) {
							                	 nodev2 = (Element) node1;
							                	 break;
							                 }
										 }
				                	 }
				                	 MergeNode(nodev1,nodev2,"VariableDes");
			                	 }
			                 }else if(node.getNodeName().equals("property")) {
			                	 System.out.println("property:"+name);
			                	 if(ele1.getElementsByTagName("property").item(0)==null){//如果ele1中没有property
			                		 Node firstDocImportedNode = relationxml.importNode(node, true);
			                		 ele1.appendChild(firstDocImportedNode );
			                	 }
			                 }else
			                 if(node.getNodeName().equals("DependentVariable")) {
			                	 if(ele1.getElementsByTagName("DependentVariable").item(0)==null){//如果ele1中没有Variables
			                		 Node firstDocImportedNode = relationxml.importNode(node, true);
			                		 ele1.appendChild(firstDocImportedNode );
//			                		 ele1.appendChild(node);
			                	 }else{
				                	 Element nodev1 = (Element) node;
				                	 Element nodev2 = null;
				                	 for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
										 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
							                 if (node1.getNodeName().equals("DependentVariable")) {
							                	 nodev2 = (Element) node1;
							                	 break;
							                 }
										 }
				                	 }
			                	 MergeNode(nodev1,nodev2,"DependentVariables");
			                	 }
			                 }
			                 }
						}
					break;
			}
			
		}}
		if(type=="add"){
			root.appendChild(ele);
		}
		fillgraph();
	}
	public static void MergeNode(Element nodeV1,Element nodeV2,String ident){
		int i,j;
		NodeList listv1 = nodeV1.getElementsByTagName(ident);
		NodeList listv2 = nodeV2.getElementsByTagName(ident);
		if(ident=="VariableDes"){
			for(i=0;i<listv1.getLength();i++){
				Node v1 = listv1.item(i);
				NodeList v1Var = ((Element) v1).getElementsByTagName("Conception");
				for(j=0;j<listv2.getLength();j++){
 					Node v2 = listv2.item(j);
 					NodeList v2Var = ((Element) v2).getElementsByTagName("Conception");
 					int k,m;
 					for(k=0;k<v1Var.getLength();k++){
 						for(m=0;m<v2Var.getLength();m++){
 							if(v2Var.item(m).getAttributes().getNamedItem("ID").getNodeValue().equals(v1Var.item(k).getAttributes().getNamedItem("ID").getNodeValue())||(v2Var.item(m).getAttributes().getNamedItem("Name").getNodeValue().equals(v1Var.item(k).getAttributes().getNamedItem("Name").getNodeValue()))){
 								break;
 							}
 						}
 						if(m==v2Var.getLength()){//不在v2var中
 							break;
 						}
 					}
 					if(k==v1Var.getLength()){//v1在nodeV2中
 						break;
 					}
				}
				if(j==listv2.getLength()){//没有此结点
					nodeV2.appendChild(v1);
				}
			}
		}else{
			for(i=0;i<listv1.getLength();i++){
				Node v1 = listv1.item(i);
				
				for(j=0;j<listv2.getLength();j++){
					Node v2 = listv2.item(j);
					Node node1;
					for(node1 = v1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
	       			 if (node1.getNodeType() == Node.ELEMENT_NODE) {
	       				 Node node2;
	       				 for(node2 = v2.getFirstChild();node2!=null;node2=node2.getNextSibling()){
	       					 if (node2.getNodeType() == Node.ELEMENT_NODE) {
	       						 if((node2.getAttributes().getNamedItem("ID").getNodeValue().equals(node1.getAttributes().getNamedItem("ID").getNodeValue()))||(node2.getAttributes().getNamedItem("Name").getNodeValue().equals(node1.getAttributes().getNamedItem("Name").getNodeValue()))){
	           						 break;
	           					 }
	       					 }
	       				 }
	       				 if(node2==null){//v2不存在node1
	       					 break;
	       				 }
	 				}
					}
					if(node1==null){//v1中都在v2中
						break;
					}
				}
				if(j==listv2.getLength()){//没有这个结点
					nodeV2.appendChild(v1);}
				}
			}
	}
	public synchronized void deleteRelation(String relation) throws DocumentException{//将concept的id和name传过来ID:Name
		Document xml = (Document) DocumentHelper.parseText(relation);//便得到xml文档
		NodeList list1 = xml.getElementsByTagName("Relation");
		if(list1!=null){
			Element rel = (Element) list1.item(0);
			String conceptid = rel.getAttributes().getNamedItem("ID").getNodeValue();
			Node variables =null;
			Node DependentVariables = null;
			for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
				 if (node.getNodeType() == Node.ELEMENT_NODE) {  
					 if (node.getNodeName().equals("Variables")) {  
		              	 variables = node;
		             }
		             if (node.getNodeName().equals("DependentVariables")) {  
		              	 DependentVariables = node;
		             }
		             if(node.getNodeName().equals("Description")){
//		                	 description = node;
		             }
				}
			}
			if(variables!=null){
				for(Node node1 = variables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
					if (node1.getNodeType() == Node.ELEMENT_NODE) { 
						String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                        NodeList list = relationxml.getElementsByTagName("Element");
             			int i;
             			for(i=0;i<list.getLength();i++){
             				Element ele1 = (Element)list.item(i);
             				if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
             					for(Node node = ele1.getFirstChild();node!=null;node=node.getNextSibling()){
             						 if (node.getNodeType() == Node.ELEMENT_NODE) {  
             			                 if (node.getNodeName().equals("DependentVariable")) { 
             			                	 NodeList list2 = ((Element) node).getElementsByTagName("DependentVariables");
             			                	 for(int j=0;j<list2.getLength();j++){
             			                		 Node node2 = list2.item(j);
             			                		 for(Node node3 = node2.getFirstChild();node3!=null;node3=node3.getNextSibling()){
	             				                 	 if (node3.getNodeType() == Node.ELEMENT_NODE) { 
	             				                 		 String id1 = node3.getAttributes().getNamedItem("ID").getNodeValue();
//	             				                             String out = node1.getAttributes().getNamedItem("Name").getNodeValue();
	             				                 		 if(id1.equals(conceptid)){
	             				                           	  node3.getParentNode().getParentNode().removeChild(node3.getParentNode());  
	             				                           	  break;
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
				}
				if(DependentVariables!=null){
					 for(Node node1 = DependentVariables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
                		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
                			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                             NodeList list = relationxml.getElementsByTagName("Element");
             				int i;
             				for(i=0;i<list.getLength();i++){
             					Element ele1 = (Element)list.item(i);
             					if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
             						for(Node node = ele1.getFirstChild();node!=null;node=node.getNextSibling()){
             							 if (node.getNodeType() == Node.ELEMENT_NODE) {  
             				                 if (node.getNodeName().equals("Variable")) {  
             				                	NodeList listv1 = ((Element) node).getElementsByTagName("VariableDes");
             				                	for(i=0;i<listv1.getLength();i++){
             				   					Node v1 = listv1.item(i);
             				   					NodeList v1Var = ((Element) v1).getElementsByTagName("Conception");
             				   					for(int k=0;k<v1Var.getLength();k++){
             				   	 						if(v1Var.item(k).getAttributes().getNamedItem("ID").equals(id)){
             				   	 							v1Var.item(k).getParentNode().getParentNode().getParentNode().removeChild(v1Var.item(k).getParentNode().getParentNode());
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
				NodeList list = relationxml.getElementsByTagName("Element");
				int i;
				for(i=0;i<list.getLength();i++){
					Element ele1 = (Element)list.item(i);
					if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(conceptid)){//存在此条知识
						ele1.getParentNode().removeChild(ele1);
						break;
					}
				}	
		}
		fillgraph();
//		return getRelationsjson();
	}

	public synchronized void updateRelation(String relation) throws DocumentException{//将concept的xml字符串传过来
		Document xml = (Document) DocumentHelper.parseText(relation);//便得到xml文档
		readxmldocrelation(xml,"Merge");
//		return getRelationsjson();
	}
	public synchronized void readxmldocrelation(Document xmldoc,String type){
		NodeList list1 = xmldoc.getElementsByTagName("Relation");
		if(list1!=null){
				Element rel = (Element) list1.item(0);
				Node variables =null;
				Node DependentVariables = null;
				Node description = null;
				for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
					 if (node.getNodeType() == Node.ELEMENT_NODE) {  
		                 if (node.getNodeName().equals("Variables")) {  
		                	 variables = node;
		                 }
		                 if (node.getNodeName().equals("DependentVariables")) {  
		                	 DependentVariables = node;
		                 }
		                 if(node.getNodeName().equals("Description")){
		                	 description = node;
		                 }
					 }
				}
				if(DependentVariables!=null&&description!=null){
				for(Node node1 = DependentVariables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
           		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
           			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                        String out = node1.getAttributes().getNamedItem("Name").getNodeValue();
                        Element newChild = relationxml.createElement("Element");
                        newChild.setAttribute("ID", id);
              			 newChild.setAttribute("Name", out);
              			 
              			 Element inputname = relationxml.createElement("Variable");// 元素节点  name
              			 Element varDes = relationxml.createElement("VariableDes");// 元素节点  name
              			 Element input = relationxml.createElement("Variables");// 元素节点  name
	                	 for(Node node2 = variables.getFirstChild();node2!=null;node2=node2.getNextSibling()){
	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
	                			 String id1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
	                             String in1 = node2.getAttributes().getNamedItem("Name").getNodeValue();
	               	    	  	Element Conception = relationxml.createElement("Conception");// 元素节点  name
	               	    	  	Conception.setAttribute("ID", id1);
	               	    	  	Conception.setAttribute("Name", in1);// 添加Name属性
	               	    	 input.appendChild(Conception);
	                		 }
	                	 }
	                	 varDes.appendChild(input);
	                	
//	                	 Element dess = xmldocgraph.createElement("Descriptions");
	                	 Element des = relationxml.createElement("Description");
	                	 for(Node node3 = description.getFirstChild();node3!=null;node3=node3.getNextSibling()){
	                		 if (node3.getNodeType() == Node.ELEMENT_NODE) {  
	                			 String ruleid = node3.getAttributes().getNamedItem("ID").getNodeValue();
	                			 String rulename = node3.getAttributes().getNamedItem("Name").getNodeValue();
	                			 String rulemathml = node3.getAttributes().getNamedItem("mathml").getNodeValue();
	                			 Element formula = relationxml.createElement("Formula");
	                			 formula.setAttribute("ID", ruleid);
	                			 formula.setAttribute("Name", rulename);
	                			 formula.setAttribute("mathml", rulemathml);
	                			 
	                			 des.appendChild(formula);
	                		 }
	                	 }
//	                	 dess.appendChild(des);
	                	 varDes.appendChild(des);
	                	 inputname.appendChild(varDes);
//	                	 newChild.appendChild(dess);
	                	 newChild.appendChild(inputname);
	                	 insertKnowledge(newChild, id,out,type);
           		 	}
           		 	}
				 }
				if(variables!=null){
				 for(Node node1 = variables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
            		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
            			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                         String in = node1.getAttributes().getNamedItem("Name").getNodeValue();
                         Element newChild1 = relationxml.createElement("Element");
                         newChild1.setAttribute("ID", id);
               			 newChild1.setAttribute("Name", in);
               			 
               			 Element depend = relationxml.createElement("DependentVariable");// 元素节点  name

               			 Element dependvar = relationxml.createElement("DependentVariables");// 元素节点  name
	                	 for(Node node2 = DependentVariables.getFirstChild();node2!=null;node2=node2.getNextSibling()){
	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
	                			 String id1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
	                             String in1 = node2.getAttributes().getNamedItem("Name").getNodeValue();
	               	    	  	Element Conception = relationxml.createElement("Conception");// 元素节点  name
	               	    	  	Conception.setAttribute("ID", id1);
	               	    	  	Conception.setAttribute("Name", in1);// 添加Name属性
	               	    	  	dependvar.appendChild(Conception);
	                		 }
	                	 }
	                	 depend.appendChild(dependvar);
        	    	  	newChild1.appendChild(depend);
        	    	  	insertKnowledge(newChild1, id,in,type);
            		 }
				 }
			}
		}
	}
	
	public synchronized void fillgraph(){
		 TransformerFactory transFactory = TransformerFactory.newInstance();  
		    try {  
		 Transformer transformer = transFactory.newTransformer();  
	      // 设置各种输出属性  
	      transformer.setOutputProperty("encoding", "UTF-8");  
	      transformer.setOutputProperty("indent", "yes");  
		 DOMSource source = new DOMSource();  
	      // 将待转换输出节点赋值给DOM源模型的持有者(holder)  
	      source.setNode(root);
		StreamResult result = new StreamResult();  
//	      if (filename == null) {  
//	        // 设置标准输出流为transformer的底层输出目标  
//	        result.setOutputStream(System.out);  
//	      } else {  
	        result.setOutputStream(new FileOutputStream(RelationsXML));  
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
