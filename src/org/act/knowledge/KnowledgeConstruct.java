package org.act.knowledge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.w3c.dom.Node;
public class KnowledgeConstruct {//读取文件夹形成xml（表示知识图）
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
	public static HashMap<String,Object> map = new HashMap<String,Object>(); 
	static String outPath = "resources/knowledge graph1/graph.xml";
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
        
        readFolder(filePath);  
        ConstructKnowledge();
    }  
      
    /** 
     * 读取文件夹 
     * @return  
     */  
	
    public static void readFolder(String filePath){  
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
                    readFile(absolutepath,filename,i);//调用 readFile 方法读取文件夹下所有文件  
                }  
                System.out.println("---------- 所有文件操作完毕 ----------");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 读取文件夹下的文件 
     * @return  
     */  
    public static void readFile(String absolutepath,String filename,int index){  
    	KnowledgeConstruct kc = new KnowledgeConstruct();
    	
    	ArrayList<String> Dependent = new ArrayList<String>();
    	ArrayList<String> input= new ArrayList<String>();
    	Document xmldoc = kc.parse(absolutepath);
		NodeList list1 = xmldoc.getElementsByTagName("Relation");
		if(list1!=null){
//        		for(int i = 0; i< list1.getLength() ; i ++){  
				Node rel = list1.item(0);
				for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
					 if (node.getNodeType() == Node.ELEMENT_NODE) {  
		                 if (node.getNodeName().equals("Variables")) {  
		                	 for(Node node1 = node.getFirstChild();node1!=null;node1=node1.getNextSibling()){
		                		 if (node1.getNodeType() == Node.ELEMENT_NODE) { 
		                			 String id = node1.getAttributes().getNamedItem("ID").getNodeValue();
		                             String in = node1.getAttributes().getNamedItem("Name").getNodeValue();
//		                             System.out.println("id"+id);
		                             System.out.println("idin:"+id+":"+in);
		                             input.add(id+":"+in);
//		                             input.add(in);
		     				}
		                 }
		                 }
		                 if (node.getNodeName().equals("DependentVariables")) {  
		                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
		                		 if (node2.getNodeType() == Node.ELEMENT_NODE) {  
		                			 String outid = node2.getAttributes().getNamedItem("ID").getNodeValue();
		                			 String out = node2.getAttributes().getNamedItem("Name").getNodeValue();
		                			 System.out.println("idout:"+outid+":"+out);
		                			 Dependent.add(outid+":"+out);
			                		 
		     				}
		                	}
//		                	 Node node2 = node.getFirstChild();
//		                	 if (node2.getNodeType() == Node.ELEMENT_NODE) {  
//		                		 Dependent = node2.getAttributes().getNamedItem("Name").getNodeValue();
//		                		 System.out.println("Dependent:"+Dependent);
////                                     input.add(in);
//		                 }
		               
					
				}
				
			}
				}
		}
		for(int i=0;i<Dependent.size();i++){
			map.put(Dependent.get(i), input);
		}
		
				  
    }
    
    public static void ConstructKnowledge() throws FileNotFoundException{
    	KnowledgeConstruct kc = new KnowledgeConstruct();
    	Document xmldoc = kc.parse(outPath);
    	Element root = xmldoc.getDocumentElement();
		NodeList list1 = xmldoc.getElementsByTagName("object");
		for(int i=0;i<list1.getLength();i++){//删除所有节点
			Element ele = (Element)list1.item(i);
			ele.getParentNode().removeChild(ele);
		}
		Iterator iterator = map.entrySet().iterator();
		int num=0;
		while(iterator.hasNext()){
			java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
			Element newChild = xmldoc.createElement("object");  
			
		      System.out.println((String) entry.getKey()+"_key");
		      String outidname = (String) entry.getKey();
		      newChild.setAttribute("ID", outidname.substring(0,outidname.indexOf(":")));// 添加id属性
		      newChild.setAttribute("Name",outidname.substring(outidname.indexOf(":")));// 添加id属性
		 
//		      Element dependentname = xmldoc.createElement("name");// 元素节点  name
		      
//		      dependentname.setTextContent((String) entry.getKey());  
//		      newChild.appendChild(dependentname);  
		      Element inputname = xmldoc.createElement("Variables");// 元素节点  name
		      ArrayList ls = (ArrayList)entry.getValue();
		      for(int j=0;j<ls.size();j++){
		    	  
		    	  Element Conception = xmldoc.createElement("Conception");// 元素节点  name
		    	  System.out.println((String) ls.get(j));
		    	  String inputidname = (String) ls.get(j);
		    	  Conception.setAttribute("ID", inputidname.substring(0,inputidname.indexOf(":")));
		    	  Conception.setAttribute("Name", inputidname.substring(outidname.indexOf(":")));// 添加Name属性
		    	  inputname.appendChild(Conception);
		      }
		      newChild.appendChild(inputname);
		      
		      System.out.println(num++);
		      root.appendChild(newChild);  
		      for(int k=0;k<ls.size();k++){
		    	  Element newChild1 = xmldoc.createElement("object");  
		    	  String inputidname1 = (String) ls.get(k);
			      newChild.setAttribute("ID", outidname.substring(0,outidname.indexOf(":")));// 添加id属性
			      newChild.setAttribute("Name",outidname.substring(outidname.indexOf(":")));// 添加id属性
			     			 
			      Element Dependentname = xmldoc.createElement("DependentVariables");
			      Element Conception = xmldoc.createElement("Conception");// 元素节点  name
		    	  Conception.setAttribute("ID", outidname.substring(0,outidname.indexOf(":")));
		    	  Conception.setAttribute("Name", outidname.substring(outidname.indexOf(":")));// 添加Name属性
		    	  Dependentname.appendChild(Conception);
		    	  newChild1.appendChild(Dependentname);
			      
		      }
		      
			
		}
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
    public static String filterchar(String str){
    	return str.replaceAll(" ", "");
    }
}


    
