package org.act.knowledge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class KnowledgeMapCreate {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
	public static HashMap<String,Object> graphmap = new HashMap<String,Object>(); 
	static String outPath = "resources/knowledge graph1/graph.xml";
	public static Element root;
	public static NodeList list1;
	public static Iterator iterator;
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
        KnowledgeMapCreate kc = new KnowledgeMapCreate();
        xmldocgraph = kc.parse(outPath);
    	root = xmldocgraph.getDocumentElement();
		list1 = xmldocgraph.getElementsByTagName("Element");
		
		if(list1!=null){
			for(int i=0;i<list1.getLength();i++){
				Node rel = list1.item(i);
				String id = rel.getAttributes().getNamedItem("ID").getNodeValue();
				graphmap.put(id, rel);
			}
		}
//		iterator = graphmap.entrySet().iterator();
        readFolder(filePath,"relation");  
        readFolder(filePath1,"cocept");
        fillgraph();
//        ConstructKnowledge();
    }  
      
    /** 
     * 读取文件夹 
     * @return  
     */  
	
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
	                    readFileCocepts(absolutepath,filename,i);//调用 readFile 方法读取文件夹下所有文件  
	                }
                }
                System.out.println("---------- 所有文件操作完毕 ----------");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    public static void readFileCocepts(String absolutepath,String filename,int index){
    	KnowledgeMapCreate kc = new KnowledgeMapCreate();
    	
    	Document xmldoc = kc.parse(absolutepath);
    	NodeList list3 = xmldoc.getElementsByTagName("Conception");
		if(list3!=null){
				Element rel = (Element) list3.item(0);
				String conceptid = rel.getAttributes().getNamedItem("ID").getNodeValue();
				String conceptName = rel.getAttributes().getNamedItem("Name").getNodeValue();
				NodeList list2 = xmldocgraph.getElementsByTagName("Element");
				int i;
				for(i=0;i<list2.getLength();i++){
					Element ele = (Element)list1.item(i);
					if(ele.getAttributes().getNamedItem("ID").getNodeValue().equals(conceptid)){

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
						break;
					
					
					}
				}
				if(i==list2.getLength()){
					System.out.println("没有此条知识"+conceptid+":"+conceptName);
					
					Element newChild = xmldocgraph.createElement("Element");
					newChild.setAttribute("ID", conceptid);
					newChild.setAttribute("Name", conceptName);
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
					newChild.appendChild(property);
					root.appendChild(newChild);
//					return;
				}
				
					
				}
		}
		
    
    public static void readFile(String absolutepath,String filename,int index){  
		KnowledgeMapCreate kc = new KnowledgeMapCreate();
    	Document xmldoc = kc.parse(absolutepath);
		NodeList list1 = xmldoc.getElementsByTagName("Relation");
		if(list1!=null){
				Element rel = (Element) list1.item(0);
				Node depend =  rel.getElementsByTagName("DependentVariables").item(0);
				String outid="";
				for(Node node2 = depend.getFirstChild();node2!=null;node2=node2.getNextSibling()){
					if (node2.getNodeType() == Node.ELEMENT_NODE) {  
           			 	outid = node2.getAttributes().getNamedItem("ID").getNodeValue();
               		 
					}
				}
				if(graphmap.containsKey(outid)){
					return;
				}else{
					Element newChild = xmldocgraph.createElement("Element");
					Node variables =null;
					for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
						 if (node.getNodeType() == Node.ELEMENT_NODE) {  
			                 if (node.getNodeName().equals("Variables")) {  
			                	 variables = node;
			                	 Element inputname = xmldocgraph.createElement("Variables");// 元素节点  name
			                	 for(Node node1 = node.getFirstChild();node1!=null;node1=node1.getNextSibling()){
			                		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
			                			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
			                             String in = node1.getAttributes().getNamedItem("Name").getNodeValue();
			               	    	  	Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name
			               	    	  	Conception.setAttribute("ID", id);
			               	    	  	Conception.setAttribute("Name", in);// 添加Name属性
			               	    	  	inputname.appendChild(Conception);
			                		 }
			                	 }
			                	newChild.appendChild(inputname);
			                 }
			                 if (node.getNodeName().equals("DependentVariables")) {  
			                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
			                		 if (node2.getNodeType() == Node.ELEMENT_NODE) {  
			                			 String outid1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
			                			 String out = node2.getAttributes().getNamedItem("Name").getNodeValue();
			                			 
			                			 System.out.println("idout:"+outid+":"+out);
			                			 newChild.setAttribute("ID", outid1);
			                			 newChild.setAttribute("Name", out);
			                		 }
			                	}            
			                 }
			                 if(node.getNodeName().equals("Description")){
			                	 Element description = xmldocgraph.createElement("Description");
			                	 for(Node node3 = node.getFirstChild();node3!=null;node3=node3.getNextSibling()){
			                		 if (node3.getNodeType() == Node.ELEMENT_NODE) {  
			                			 String ruleid = node3.getAttributes().getNamedItem("ID").getNodeValue();
			                			 String rulename = node3.getAttributes().getNamedItem("Name").getNodeValue();
			                			 String rulemathml = node3.getAttributes().getNamedItem("mathml").getNodeValue();
			                			 Element formula = xmldocgraph.createElement("Formula");
			                			 formula.setAttribute("ID", ruleid);
			                			 formula.setAttribute("Name", rulename);
			                			 formula.setAttribute("mathml", rulemathml);
			                			 
			                			 description.appendChild(formula);
			                		 }
			                	 }
			                	 newChild.appendChild(description);
					
			                 }
						 }
					}
					graphmap.put(newChild.getAttributes().getNamedItem("ID").getNodeValue(), newChild);
		             root.appendChild(newChild);
					 for(Node node1 = variables.getFirstChild();node1!=null;node1=node1.getNextSibling()){
                		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
                			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
                             String in = node1.getAttributes().getNamedItem("Name").getNodeValue();
                             if(graphmap.containsKey(id)){//存在此知识
                            	 Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name
//	                			 System.out.println("idout:"+outid+":"+out);
	                			 Conception.setAttribute("ID", newChild.getAttribute("ID"));
	                 	    	 Conception.setAttribute("Name", newChild.getAttribute("Name"));// 添加Name属性

                             
                            	 Element ele = (Element) graphmap.get(id);
                             
                            	 for(Node node = ele.getFirstChild();node!=null;node=node.getNextSibling()){    
                            		 
            			                 if (node.getNodeName().equals("DependentVariables")) {  
//            			                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
//            			                		 if (node2.getNodeType() == Node.ELEMENT_NODE) {  
//            			                			 String outid1 = node2.getAttributes().getNamedItem("ID").getNodeValue();
//            			                			 String out = node2.getAttributes().getNamedItem("Name").getNodeValue();
            			                			 node.appendChild(Conception);
//            			                		 }
//            			                	} 
            			                 	    	 break;
            			                 }
                            	 }
                            	 NodeList list2 = xmldocgraph.getElementsByTagName("Element");
                 				int i;
                 				for(i=0;i<list2.getLength();i++){//删除所有节点
                 					Element ele1 = (Element)list1.item(i);
                 					if(ele1.getAttributes().getNamedItem("ID").getNodeValue().equals(id)){
                 						ele1.appendChild(Conception);
                 						break;
                 					}
                 				}
//                            	 Element ele = (Element) graphmap.get(id);
//                            	 Element dependvar = (Element) ele.getElementsByTagName("DependentVariables").item(0);
//                            	 Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name
//
//                 	    	  	Conception.setAttribute("ID", newChild.getAttribute("ID"));
//                 	    	  	Conception.setAttribute("Name", newChild.getAttribute("Name"));// 添加Name属性
//                 	    	  	dependvar.appendChild(Conception);
                             }else{
                             Element newChild1 = xmldocgraph.createElement("Element");
                             newChild1.setAttribute("ID", id);
                   			 newChild1.setAttribute("Name", in);
                   			 
                   			 Element dependvar = xmldocgraph.createElement("DependentVariables");// 元素节点  name
//                             System.out.println("id"+id);
               	    	  	Element Conception = xmldocgraph.createElement("Conception");// 元素节点  name

            	    	  	Conception.setAttribute("ID", newChild.getAttribute("ID"));
            	    	  	Conception.setAttribute("Name", newChild.getAttribute("Name"));// 添加Name属性
            	    	  	dependvar.appendChild(Conception);
            	    	  	newChild1.appendChild(dependvar);
            	    	  	graphmap.put(newChild1.getAttributes().getNamedItem("ID").getNodeValue(), newChild);
            	    	  	root.appendChild(newChild1);
                             }
                		 }
					}
					 
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
