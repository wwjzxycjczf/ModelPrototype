package org.act.realtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OpenTask1 extends HttpServlet implements Servlet{
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
	public OpenTask1(){
		
	}
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	  
	 }
	// protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	//  System.out.println("-------------------------") ;
	//  doPost(request, response) ;
	// }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	  response.setContentType("text/html;charset=utf-8") ;
	  request.setCharacterEncoding("utf-8") ;
	  //获取用户名
	  String username = request.getParameter("filename") ;
	  HttpSession session =  request.getSession() ;
	 
	  String input ="";
		 String output = "";
		 String inputArr[]=null;
		 
		 OpenTask1 task = new OpenTask1();
		 Document doc1 = task.parse(username);		 
// Document doc1 = task.parse("resources\\tasks\\task1.xml");
 NodeList list1 = doc1.getElementsByTagName("Task");  
   
 //遍历该集合，显示结合中的元素及其子元素的名字  
 for(int i = 0; i< list1.getLength() ; i ++){  
     Element element = (Element)list1.item(i);
     input = element.getElementsByTagName("input").item(0).getFirstChild().getNodeValue();
     output = element.getElementsByTagName("output").item(0).getFirstChild().getNodeValue();
     System.out.println("input:"+input);
     System.out.println("output:"+output);
     inputArr = input.split(",");
     
 } 
 String inputArrName[] = new String[inputArr.length];
	 String inputArrGS[] = new String[inputArr.length];
 for(int j =0;j<inputArr.length;j++){
 	inputArrName[j] = inputArr[j].substring(0, inputArr[j].indexOf(':'));
 	inputArrGS[j] = inputArr[j].substring(inputArr[j].indexOf(':')+1);
 }
 Document doc2 = task.parse("resources\\knowledge graph\\graph.xml");
 NodeList list2 = doc2.getElementsByTagName("object");  
ArrayList<Element> list3 = new ArrayList<Element>();
Element outele = null;
HashMap<String,Object> map = new HashMap<String,Object>(); 
if(list2!=null){
	  for(int i = 0; i< list2.getLength() ; i ++){  
		   Element element1 = (Element)list2.item(i);
	      String name = element1.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
	      map.put(name, element1);
	      
	  }
}
//int k = 0;
String finalHL = "<";
for(int ll=0;ll<inputArrName.length;ll++){
	  list3.add( (Element)map.get(inputArrName[ll]));
//	  finalHL += inputArrName[ll];
////	   System.out.println(finalHL);
//	   finalHL += ",";	
}
Element elee = (Element)map.get(inputArrName[0]);
System.out.println(elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
while(!elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue().equals(output.substring(0, output.indexOf(':')))){
//	  ele =(Element)map.get(inputArrName[k]);
	  elee = (Element)map.get(elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
	  String arr[]=elee.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().split(",");
//	  String arr[] = elee.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().split(",");
	  if(arr.length>inputArrName.length){
		  System.out.println("没有输出");//输入的不够多
		  return;
	  }
	  for(int l=0;l<arr.length;l++){
		  if(!input.contains(arr[l])){
//			  
//		  }
//		  for(int ll=0;ll<inputArrName.length;ll++){
//			  if(!arr[l].equals(inputArrName[ll])){
				  System.out.println("图中没有此条路");//需要用户选择是否添加
				  return;
//			  }
		  }
	  }
//	  k++;
	  list3.add(elee);
//	  elee = (Element) map.get((String)elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
}
list3.add((Element)map.get(output.substring(0, output.indexOf(':'))));
//	  for(int l=0;l<inputArr.length;l++){
//		  if(ele.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().indexOf(inputArr[l])>=0){
//			  
//		  }
//	  }
//}

for(int kk =0;kk<list3.size();kk++){
	   Element ele = (Element)list3.get(kk);
	   finalHL += ele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
//		   System.out.println(finalHL);
		   finalHL += ",";	   
}
//finalHL +=outele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
finalHL += ">"; 
session.setAttribute("filename", finalHL);
//}
//finalHL.concat(outele.getAttribute("name"));
//finalHL.concat(">");
System.out.println(finalHL);
	 }
//	 public static void main(String[] args)throws Exception {  
//		 String input ="";
//		 String output = "";
//		 String inputArr[]=null;
//		 
//		 OpenTask1 task = new OpenTask1();
//    Document doc1 = task.parse("resources\\tasks\\task1.xml");
//    NodeList list1 = doc1.getElementsByTagName("Task");  
//      
//    //遍历该集合，显示结合中的元素及其子元素的名字  
//    for(int i = 0; i< list1.getLength() ; i ++){  
//        Element element = (Element)list1.item(i);
//        input = element.getElementsByTagName("input").item(0).getFirstChild().getNodeValue();
//        output = element.getElementsByTagName("output").item(0).getFirstChild().getNodeValue();
//        System.out.println("input:"+input);
//        System.out.println("output:"+output);
//        inputArr = input.split(",");
//        
//    } 
//    String inputArrName[] = new String[inputArr.length];
//	 String inputArrGS[] = new String[inputArr.length];
//    for(int j =0;j<inputArr.length;j++){
//    	inputArrName[j] = inputArr[j].substring(0, inputArr[j].indexOf(':'));
//    	inputArrGS[j] = inputArr[j].substring(inputArr[j].indexOf(':')+1);
//    }
//    Document doc2 = task.parse("resources\\knowledge graph\\graph.xml");
//    NodeList list2 = doc2.getElementsByTagName("object");  
//  ArrayList<Element> list3 = new ArrayList<Element>();
//  Element outele = null;
//  HashMap<String,Object> map = new HashMap<String,Object>(); 
//  if(list2!=null){
//	  for(int i = 0; i< list2.getLength() ; i ++){  
//		   Element element1 = (Element)list2.item(i);
//	      String name = element1.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
//	      map.put(name, element1);
//	      
//	  }
//  }
////  int k = 0;
//  String finalHL = "<";
//  for(int ll=0;ll<inputArrName.length;ll++){
//	  list3.add( (Element)map.get(inputArrName[ll]));
////	  finalHL += inputArrName[ll];
//////	   System.out.println(finalHL);
////	   finalHL += ",";	
//  }
//  Element elee = (Element)map.get(inputArrName[0]);
//  System.out.println(elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
//  while(!elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue().equals(output.substring(0, output.indexOf(':')))){
////	  ele =(Element)map.get(inputArrName[k]);
//	  elee = (Element)map.get(elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
//	  String arr[]=elee.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().split(",");
////	  String arr[] = elee.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().split(",");
//	  if(arr.length>inputArrName.length){
//		  System.out.println("没有输出");//输入的不够多
//		  return;
//	  }
//	  for(int l=0;l<arr.length;l++){
//		  if(!input.contains(arr[l])){
////			  
////		  }
////		  for(int ll=0;ll<inputArrName.length;ll++){
////			  if(!arr[l].equals(inputArrName[ll])){
//				  System.out.println("图中没有此条路");//需要用户选择是否添加
//				  return;
////			  }
//		  }
//	  }
////	  k++;
//	  list3.add(elee);
////	  elee = (Element) map.get((String)elee.getElementsByTagName("output").item(0).getFirstChild().getNodeValue());
//  }
//  list3.add((Element)map.get(output.substring(0, output.indexOf(':'))));
////	  for(int l=0;l<inputArr.length;l++){
////		  if(ele.getElementsByTagName("input").item(0).getFirstChild().getNodeValue().indexOf(inputArr[l])>=0){
////			  
////		  }
////	  }
////  }
//  
//  for(int kk =0;kk<list3.size();kk++){
//	   Element ele = (Element)list3.get(kk);
//	   finalHL += ele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
////		   System.out.println(finalHL);
//		   finalHL += ",";	   
//  }
////  finalHL +=outele.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
//  finalHL += ">"; 
////  }
////  finalHL.concat(outele.getAttribute("name"));
////  finalHL.concat(">");
//  System.out.println(finalHL);
//  
//  
//}  
  
}  