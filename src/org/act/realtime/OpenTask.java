package org.act.realtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;  
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;  
import org.xml.sax.SAXException;
  
import org.dom4j.DocumentException;  
import org.dom4j.io.SAXReader;  
import org.dom4j.tree.DefaultAttribute; 
public class OpenTask {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
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
	 // private static Map xmlmap = new HashMap();   
    //存储xml元素信息的容器   
	 public static void main(String[] args)throws Exception {  
//	 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();  
//     //从DOM工厂中获得DOM解析器  
//     DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();  
//     //声明为File为了识别中文名 
//     Document doc = null;
		 String input ="";
		 String output = "";
		 String inputArr[];
		 OpenTask task = new OpenTask();
     Document doc1 = task.parse("resources\\tasks\\task1.xml");
//     doc = dbBuilder.parse("resources\\tasks\\task1.xml");
     //得到文档名称为Student的元素的节点列表  
     NodeList list1 = doc1.getElementsByTagName("Task");  
       
     //遍历该集合，显示结合中的元素及其子元素的名字  
     for(int i = 0; i< list1.getLength() ; i ++){  
         Element element = (Element)list1.item(i);
         input = element.getElementsByTagName("input").item(0).getFirstChild().getNodeValue();
         output = element.getElementsByTagName("output").item(0).getFirstChild().getNodeValue();
         System.out.println("input:"+input);
         System.out.println("output:"+output);
         inputArr = input.split(",");
//         String name=element.getAttribute("name");  
//         String capacity=element.getElementsByTagName("capacity").item(0).getFirstChild().getNodeValue();  
//         String directories=element.getElementsByTagName("directories").item(0).getFirstChild().getNodeValue();  
//         String files=element.getElementsByTagName("files").item(0).getFirstChild().getNodeValue();  
//         System.out.println("磁盘信息:");   
//         System.out.println("分区盘符:"+name);   
//         System.out.println("分区容量:"+capacity);   
//         System.out.println("目录数:"+directories);   
//         System.out.println("文件数:"+files);   
//         System.out.println("-----------------------------------");   
     } 
     Document doc2 = task.parse("resources\\knowledge graph\\graph.xml");
//   doc = dbBuilder.parse("resources\\tasks\\task1.xml");
   //得到文档名称为Student的元素的节点列表  
   NodeList list2 = doc2.getElementsByTagName("object");  
   ArrayList<Element> list3 = new ArrayList<Element>();
   Element outele = null;
   HashMap<Integer,Object> map = new HashMap<Integer,Object>(); 
//  ArrayList inOut = new ArrayList();
   //遍历该集合，显示结合中的元素及其子元素的名字  
   if(list2!=null){
   for(int i = 0; i< list2.getLength() ; i ++){  
	   Element element1 = (Element)list2.item(i);
//	   if (element1.getNodeType() == Node.ELEMENT_NODE) {
//           String sexString = element1.getAttributes().getNamedItem("性别").getNodeValue();
//           System.out.println(sexString);
//       }
//	   String name = "";
//       String in = "";
//       String out="";
//       for (Node node = element1.getFirstChild(); node != null; node = node.getNextSibling()) {
//           
//    	   if (node.getNodeType() == Node.ELEMENT_NODE) {
//               if (node.getNodeName().equals("name")) {
//                   name = node.getFirstChild().getNodeValue();
//                   System.out.println(name);
//               }
//               if (node.getNodeName().equals("input")) {
//                   in = node.getFirstChild().getNodeValue();
//                   System.out.println(in);
//               }
//               if (node.getNodeName().equals("output")) {
//                   out = node.getFirstChild().getNodeValue();
//                   System.out.println(out);
//               }
//           }
//       }
//       }
//       Element element1 = (Element)list2.item(i);
//       String name = element1.getAttribute("name");
//       String in = element1.getAttribute("input");
//       String out = element1.getAttribute("output");
       String name = element1.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
       String in = element1.getElementsByTagName("input").item(0).getFirstChild().getNodeValue();
       String out = element1.getElementsByTagName("output").item(0).getFirstChild().getNodeValue();
       System.out.println("name:"+name);
       System.out.println("input:"+in);
       System.out.println("output:"+out);
       String arr[] = new String[3];
       arr[0] = name;
       arr[1] = in;
       arr[2] = out;
       if(input.indexOf(name)>=0){
    	   list3.add(element1);
    	   
       }
//       System.out.print(output.substring(0, output.indexOf(':')));
       if(output.substring(0, output.indexOf(':')).equals(name)){
    	   outele = element1;
       }
//       inputArr[0]
       map.put(i, arr);
       
   }
   }
   String finalHL = "<";
   for(int k =0;k<list3.size();k++){
	   Element ele = (Element)list3.get(k);
//	   for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
//	   if (node.getNodeName().equals("name")) {
//	   System.out.println(element.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
//	   finalHL.
//	   System.out.println(ele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
	   finalHL += ele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
		   System.out.println(finalHL);
		   finalHL += ",";
//	   }
//           name = node.getFirstChild().getNodeValue();
//           System.out.println(name);
//       }
	   
	   
	  
	   
   }
//   if (outele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue().equals("name")) {
   finalHL +=outele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
   finalHL += ">"; 
//   }
//   finalHL.concat(outele.getAttribute("name"));
//   finalHL.concat(">");
   System.out.println(finalHL);
   
   
 }  
   
}  
	   
