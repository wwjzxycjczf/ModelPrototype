package org.act.knowledge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KnowledgeMapCreate1 {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
	static String outPath = "resources/knowledge graph1/Relations.xml";
	static String outPath1 = "resources/knowledge graph1/Concepts.xml";
	public static Element root;
	public static Document xmldocgraph;
	public Document parse(String filePath) { 
	      Document document = null; 
	      try { 
	         //DOM parser instance 
	         DocumentBuilder builder = builderFactory.newDocumentBuilder(); 
	         //parse an XML file into a DOM tree 
	         document = builder.parse(new File(filePath)); 
	      } catch (ParserConfigurationException e) { 
	         e.printStackTrace();  
	      } catch (SAXException e) { 
	         e.printStackTrace(); 
	      } catch (IOException e) { 
	         e.printStackTrace(); 
	      } 
	      return document; 
	   } 
	public static void main(String[] args) throws FileNotFoundException {  
        String filePath = "resources/knowledge graph1/relations/"; //给我你要读取的文件夹路径  
        String filePath1 = "resources/knowledge graph1/concepts/";
        KnowledgeMapCreate1 kc = new KnowledgeMapCreate1();
        
        xmldocgraph = kc.parse(outPath);
    	root = xmldocgraph.getDocumentElement();
		
        readFolder(filePath,"relation");  
        
        xmldocgraph = kc.parse(outPath1);
        root = xmldocgraph.getDocumentElement();
        readFolder(filePath1,"concept");
	}
	 public static void readFolder(String filePath,String judge){  
	        try {  
	            //读取指定文件夹下的所有文件  
	            File file = new File(filePath);  
	            if (!file.isDirectory()) {  
	                System.out.println("---------- 该文件不是一个目录文件 ----------");  
	            } else if (file.isDirectory()) {  
	                System.out.println("---------- 这是一个目录文件夹 ----------");  
	                String[] filelist = file.list();  
	                if(judge=="relation"){
		                for (int i = 0; i < filelist.length; i++) {  
		                    File readfile = new File(filePath + "\\" + filelist[i]);  
		                    //String path = readfile.getPath();//文件路径  
		                    String absolutepath = readfile.getAbsolutePath();//文件的绝对路径  
		                    String filename = readfile.getName();//读到的文件名  
		                    readFile(absolutepath,filename,i);//调用 readFile 方法读取文件夹下所有文件  
		                }  
	                }else{
	                	for (int i = 0; i < filelist.length; i++) {  
		                    File readfile = new File(filePath + "\\" + filelist[i]);  
		                    //String path = readfile.getPath();//文件路径  
		                    String absolutepath = readfile.getAbsolutePath();//文件的绝对路径  
		                    String filename = readfile.getName();//读到的文件名  
		                    readFileConcepts(absolutepath,filename,i);//调用 readFile 方法读取文件夹下所有文件  
		                }
	                }
	                System.out.println("---------- 所有文件操作完毕 ----------");  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }
	 public static void readFileConcepts(String absolutepath,String filename,int index){
//		 KnowledgeMapCreate1 kc = new KnowledgeMapCreate1();
//	        
//	        xmldocgraph = kc.parse(outPath);
//	    	root = xmldocgraph.getDocumentElement();
		 KnowledgeMapCreate kc1 = new KnowledgeMapCreate();
	    	
	    	Document xmldoc = kc1.parse(absolutepath);
	    	NodeList list1 = xmldoc.getElementsByTagName("Conception");
			if(list1!=null){
				Element rel = (Element) list1.item(0);
				String conceptid = rel.getAttributes().getNamedItem("ID").getNodeValue();
				String conceptName = rel.getAttributes().getNamedItem("Name").getNodeValue();
				if(getElementByID(conceptid)==null){
					System.out.println("没有此条知识"+conceptid+":"+conceptName);
				}
				Element ele = xmldocgraph.createElement("Element");
				ele.setAttribute("ID", conceptid);
				ele.setAttribute("Name", conceptName);
						Element property = xmldocgraph.createElement("property");
						for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
							 if (node.getNodeType() == Node.ELEMENT_NODE) {  
				                 if (node.getNodeName().equals("Performance")) {  
				                	 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                	 property.appendChild(firstDocImportedNode );
	//			                	 property.appendChild(node);
				                 }
				                 if (node.getNodeName().equals("Safety")) {  
				                	 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                	 property.appendChild(firstDocImportedNode );
	//			                	 property.appendChild(node);
				                 }
				                 if(node.getNodeName().equals("Interface")){
				                	 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                	 property.appendChild(firstDocImportedNode );
	//			                	 property.appendChild(node);
						
				                 }
							 }
						}
						ele.appendChild(property);
						insertKnowledge(ele, conceptid,conceptName);
//						break;
			
					
					
			}
		
	 }
	 public static void readFile(String absolutepath,String filename,int index){  //可能有多个Variables 和多个DependentVariables
		
			KnowledgeMapCreate kc1 = new KnowledgeMapCreate();
	    	Document xmldoc = kc1.parse(absolutepath);
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
                            Element newChild = xmldocgraph.createElement("Element");
                            newChild.setAttribute("ID", id);
                  			 newChild.setAttribute("Name", out);
                  			 
                  			 Element inputname = xmldocgraph.createElement("Variables");// 元素节点  name
                  			 Element varDes = xmldocgraph.createElement("VariableDes");// 元素节点  name
                  			 Element input = xmldocgraph.createElement("Variable");// 元素节点  name
		                	 for(Node node2 = variables.getFirstChild();node2!=null;node2=node2.getNextSibling()){
		                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
		                			 String id1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
		                             String in1 = node2.getAttributes().getNamedItem("Name").getNodeValue();
		               	    	  	Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name
		               	    	  	Conception.setAttribute("ID", id1);
		               	    	  	Conception.setAttribute("Name", in1);// 添加Name属性
		               	    	 input.appendChild(Conception);
		                		 }
		                	 }
		                	 varDes.appendChild(input);
		                	
//		                	 Element dess = xmldocgraph.createElement("Descriptions");
		                	 Element des = xmldocgraph.createElement("Description");
		                	 for(Node node3 = description.getFirstChild();node3!=null;node3=node3.getNextSibling()){
		                		 if (node3.getNodeType() == Node.ELEMENT_NODE) {  
		                			 String ruleid = node3.getAttributes().getNamedItem("ID").getNodeValue();
		                			 String rulename = node3.getAttributes().getNamedItem("Name").getNodeValue();
		                			 String rulemathml = node3.getAttributes().getNamedItem("mathml").getNodeValue();
		                			 Element formula = xmldocgraph.createElement("Formula");
		                			 formula.setAttribute("ID", ruleid);
		                			 formula.setAttribute("Name", rulename);
		                			 formula.setAttribute("mathml", rulemathml);
		                			 
		                			 des.appendChild(formula);
		                		 }
		                	 }
//		                	 dess.appendChild(des);
		                	 varDes.appendChild(des);
		                	 inputname.appendChild(varDes);
//		                	 newChild.appendChild(dess);
		                	 newChild.appendChild(inputname);
		                	 insertKnowledge(newChild, id,out);
               		 	}
               		 	}
					 }
					if(variables!=null){
					 for(Node node1 = variables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
                		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
                			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                             String in = node1.getAttributes().getNamedItem("Name").getNodeValue();
                             Element newChild1 = xmldocgraph.createElement("Element");
                             newChild1.setAttribute("ID", id);
                   			 newChild1.setAttribute("Name", in);
                   			 
                   			 Element depend = xmldocgraph.createElement("DependentVariables");// 元素节点  name

                   			 Element dependvar = xmldocgraph.createElement("DependentVariable");// 元素节点  name
		                	 for(Node node2 = DependentVariables.getFirstChild();node2!=null;node2=node2.getNextSibling()){
		                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
		                			 String id1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
		                             String in1 = node2.getAttributes().getNamedItem("Name").getNodeValue();
		               	    	  	Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name
		               	    	  	Conception.setAttribute("ID", id1);
		               	    	  	Conception.setAttribute("Name", in1);// 添加Name属性
		               	    	  	dependvar.appendChild(Conception);
		                		 }
		                	 }
		                	 depend.appendChild(dependvar);
            	    	  	newChild1.appendChild(depend);
            	    	  	insertKnowledge(newChild1, id,in);
                		 }
					 }
					 }
			}

	 }
	 public static  Element getElementByID(String id){
			
			NodeList list = xmldocgraph.getElementsByTagName("Element");
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
			NodeList list = xmldocgraph.getElementsByTagName("Element");
			int i;
			for(i=0;i<list.getLength();i++){
				Element ele = (Element)list.item(i);
				if(ele.getAttributes().getNamedItem("Name").getNodeValue().equals(name)){
					return ele;
				}
			}
			return null;
		}
		public static  void insertKnowledge(Element ele,String id,String name){
			NodeList list = xmldocgraph.getElementsByTagName("Element");
			System.out.println("ele::"+name);
			int i;
			for(i=0;i<list.getLength();i++){
				Element ele1 = (Element)list.item(i);
				if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){//存在此条知识
						for(Node node = ele.getFirstChild();node!=null;node=node.getNextSibling()){
							 if (node.getNodeType() == Node.ELEMENT_NODE) {  
								
				                 if (node.getNodeName().equals("Variables")) {  
				                	 if(ele1.getElementsByTagName("Variables").item(0)==null){//如果ele1中没有Variables
				                		 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                		 ele1.appendChild(firstDocImportedNode );
//				                		 ele1.appendChild(node);
				                		 
				                	 }else{
					                	 Element nodev1 = (Element) node;
					                	 Element nodev2 = null;
					                	 for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
											 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
								                 if (node1.getNodeName().equals("Variables")) {
								                	 nodev2 = (Element) node1;
								                	 break;
								                 }
											 }
					                	 }
//				                	 MergeNode(nodev1,nodev2,"Variables");
					                	 MergeNode(nodev1,nodev2,"VariableDes");
				                	 }
				                 }else
//				                 if(node.getNodeName().equals("Descriptions")) {
//				                	 if(ele1.getElementsByTagName("Descriptions").item(0)==null){//如果ele1中没有Variables
////				                		 ele1.appendChild(node);
//				                		 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
//				                		 ele1.appendChild(firstDocImportedNode );
//				                	 }else{
//					                	 Element nodev1 = (Element) node;
//					                	 Element nodev2 = null;
//					                	 for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
//											 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
//								                 if (node1.getNodeName().equals("Descriptions")) {
//								                	 nodev2 = (Element) node1;
//								                	 break;
//								                 }
//											 }
//					                	 }
//				                	 MergeNode(nodev1,nodev2,"Description");
//				                	 }
//				                	 
//				                 }else
				                 if(node.getNodeName().equals("property")) {
				                	 System.out.println("property:"+name);
				                	 if(ele1.getElementsByTagName("property").item(0)==null){//如果ele1中没有property
				                		 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                		 ele1.appendChild(firstDocImportedNode );
				                	 }
				                 }else
				                 if(node.getNodeName().equals("DependentVariables")) {
				                	 if(ele1.getElementsByTagName("DependentVariables").item(0)==null){//如果ele1中没有Variables
				                		 Node firstDocImportedNode = xmldocgraph.importNode(node, true);
				                		 ele1.appendChild(firstDocImportedNode );
//				                		 ele1.appendChild(node);
				                	 }else{
					                	 Element nodev1 = (Element) node;
					                	 Element nodev2 = null;
					                	 for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
											 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
								                 if (node1.getNodeName().equals("DependentVariables")) {
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
				
			}
						
			if(i==list.getLength()){//不存在此条知识则添加
					root.appendChild(ele);
			
			}
			fillgraph();
			
		}
		public  void deleteKnowledgeByID(String id){
			NodeList list = xmldocgraph.getElementsByTagName("Element");
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
//			fillgraph();
		}
		public static void MergeNode(Element nodeV1,Element nodeV2,String ident){
//			if(nodeV2!=null){
			int i,j;
			NodeList listv1 = nodeV1.getElementsByTagName(ident);
			NodeList listv2 = nodeV2.getElementsByTagName(ident);
			if(ident=="VariableDes"){
				for(i=0;i<listv1.getLength();i++){
					Node v1 = listv1.item(i);
					NodeList v1Var = ((Element) v1).getElementsByTagName("Conception");
//					int k,m;
//					for(k=0;k<v1Var.getLength();k++){
//						
//					}
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
// 					if(ident=="VariableDes"){
// 					nodeV2.getParentNode().appendChild(v1.getParentNode());
// 					}else{
 					nodeV2.appendChild(v1);}
 				}
 			
 			}
			
		}
		public static void fillgraph(){
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
//		      if (filename == null) {  
//		        // 设置标准输出流为transformer的底层输出目标  
//		        result.setOutputStream(System.out);  
//		      } else {  
		        result.setOutputStream(new FileOutputStream(outPath));  
		        transformer.transform(source, result);
//		      }  
			    } catch (TransformerConfigurationException e) {  
			        e.printStackTrace();  
			    } catch (TransformerException e) {  
			      e.printStackTrace();  
			    } catch (FileNotFoundException e) {  
			      e.printStackTrace();  
			    } 

	   }
}
