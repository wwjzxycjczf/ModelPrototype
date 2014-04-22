package org.act.knowledge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.w3c.dom.Element;  

public class KnowledgeInfer {
	public static ArrayList<String> inputs;
	public static ArrayList<String> outputs;
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
	public static ArrayList<ArrayList<String>> stackList;
	public static HashMap<String,Object> map = new HashMap<String,Object>();
	public static boolean over = false;
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
        inputs = new ArrayList<String>();
        outputs = new ArrayList<String>();
        stackList = new ArrayList<ArrayList<String>>();
		String filePath = "resources/system requirements/0.01/"; //给我你要读取的文件夹路径  
		String graphpath = "resources/knowledge graph1/graph.xml";

        readFolder(filePath);  
        readgraph(graphpath);
        graphinfer();
    }  
	//读取图
	public static void readgraph(String path){
		KnowledgeConstruct kc = new KnowledgeConstruct();
		Document doc2 = kc.parse(path);
		 NodeList list1 = doc2.getElementsByTagName("object");  
		 String dependent="";
		 ArrayList<String> input;
		if(list1!=null){
			  for(int i = 0; i< list1.getLength() ; i ++){  
				  Node obj = list1.item(i);
				  if (obj.getNodeType() == Node.ELEMENT_NODE) { 
//					  if (obj.getNodeName().equals("object")) {
					  dependent = obj.getAttributes().getNamedItem("name").getNodeValue(); 
					  System.out.println("dependent:"+dependent);
					 }
//				  }
				  input = new ArrayList<String>();
				  for(Node node = obj.getFirstChild();node!=null;node=node.getNextSibling()){
						 
//			                 if (node.getNodeName().equals("Variables")) {  
			                	 for(Node node1 = node.getFirstChild();node1!=null;node1=node1.getNextSibling()){
			                		 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
			                             String in = node1.getAttributes().getNamedItem("name").getNodeValue();
			                             System.out.println("in:"+in);
			                             input.add(in);
			     				}
			                 }
//			                 }
			                 
			               
						
					}
				  	      map.put(dependent, input);
			      
			  }
		}
	}
	public static void graphinfer(){
//		ArrayList<String> stack = new ArrayList<String>();
//		System.out.println("Output:Pitch Servo Command Output");
//		jugde("Pitch Servo Command Output",(ArrayList)map.get("Pitch Servo Command Output"),stack);
//		if(over==true){
//		for(int j=0;j<stack.size();j++){
//			System.out.println("stack:"+stack.get(j));
//		}
////		}
		for(int i=0;i<outputs.size();i++){
			ArrayList<String> stack = new ArrayList<String>();
			over = false;
			System.out.println("Output:"+outputs.get(i));
			if((ArrayList)map.get(outputs.get(i))!=null){
				for(int h=0;h<((ArrayList) map.get(outputs.get(i))).size();h++){
					System.out.println("outputs:"+((ArrayList)map.get(outputs.get(i))).get(h));
				}
				jugde(outputs.get(i),(ArrayList)map.get(outputs.get(i)),stack);
				if(over==true){
					filterStrList(stack);
					for(int j=stack.size()-1;j>=0;j--){
						System.out.println("stack:"+stack.get(j));
					}
//					for(int j=0;j<stack.size();j++){
//						System.out.println("stack:"+stack.get(j));
//					}
				}
//				for(int j=0;j<stackList.size();j++){
//					ArrayList<String> arr =(ArrayList)stackList.get(j); 
//					for(int k=0;k<arr.size();k++){
//						System.out.println("stack:"+arr.get(k));
//					}
//				}
			}else{
				System.out.println("不可达到");
			}
		}
//		for(int j=0;j<stackList.size();j++){
//			ArrayList<String> arr =(ArrayList)stackList.get(j); 
//			for(int k=0;k<arr.size();k++){
//				System.out.println(arr.get(k));
//			}
//		}
	}
      
	public static void filterStrList(ArrayList<String> list){//过滤掉重复的偶对
		ArrayList<String> ls = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			for(int j=i+1;j<list.size();){
				if(list.get(j).equals(list.get(i))){
					list.remove(j);
					
				}else{
					j++;
				}
			}
		}
	}
	public static void infer(ArrayList<String> arr){
		
	}
	public static void jugde(String dep,ArrayList<String> arr,ArrayList<String> stack){
		int i=0;
		boolean bool=false;
		boolean bool1 = false;
		String stackstr = "<";
		ArrayList<String> newarr = new ArrayList<String>();
		for(i=0;i<arr.size();i++){
			if(map.get(arr.get(i))==null){
				bool = true;
//				break;		
			}else{
				bool1 = true;
			}
		}
		if(bool&&!bool1){//没有依赖，均为最初输入
			for(i=0;i<arr.size();i++){
				int j;
				for(j=0;j<inputs.size();){
					if(inputs.get(j).equals(arr.get(i))||inputs.get(j).equals(arr.get(i)+" Input")||inputs.get(j).equals(arr.get(i)+" Data Input")){
						break;
					}else{
						j++;
					}

				}
				if(j==inputs.size()){//给定的输入中没有最初输入,此条路径断了。
					System.out.println("输入中缺少"+arr.get(i));
					return ;
				}else{
					stackstr+=arr.get(i)+",";
				}
			}
			if(i==arr.size()){//给定的输入中均有最初输入，此条路径正确。
				stackstr+=dep+">";
				stack.add(stackstr);
				stackList.add(stack);
				over = true;
//				return;
			}
//			return 1;
			
		}else 	if(bool&&bool1){//有最初输入和中间
			for(i=0;i<arr.size();i++){
				if(map.get(arr.get(i))==null){
					int j;
					for(j=0;j<inputs.size();){
						if(inputs.get(j).equals(arr.get(i))||inputs.get(j).equals(arr.get(i)+" Input")||inputs.get(j).equals(arr.get(i)+" Data Input")){
							break;
						}else{
							j++;
						}
	
					}
					if(j==inputs.size()){//给定的输入中没有最初输入,此条路径断了。
						System.out.println("输入中缺少"+arr.get(i));
						return ;
					}else{
						stackstr+=arr.get(i)+",";
					}
				}else{
					newarr.add(arr.get(i));
					stackstr+=arr.get(i)+",";
				}
			}
			if(i==arr.size()){//给定的输入中均有最初输入，此条路径正确但未完全。
				stackstr+=dep+">";
				stack.add(stackstr);
			}
			for(i=0;i<newarr.size();i++){
				System.out.println("newarr:"+newarr.get(i));
				jugde(newarr.get(i),(ArrayList)map.get(newarr.get(i)),stack);	
			}
			
			
		}else{//均为中间
			newarr = arr;
			for(i=0;i<arr.size();i++){
				stackstr+=arr.get(i)+",";
			}
			stackstr+=dep+">";
			stack.add(stackstr);
			for(i=0;i<newarr.size();i++){
				jugde(newarr.get(i),(ArrayList)map.get(newarr.get(i)),stack);	
			}
//			jugde(dep,newarr,stack);
//			return -1;
		}
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
    	String Dependent = "";
    	ArrayList<String> input= new ArrayList<String>();
    	Document xmldoc = kc.parse(absolutepath);
		NodeList list1 = xmldoc.getElementsByTagName("SystemRequirement");
		
		if(list1!=null){
//        		for(int i = 0; i< list1.getLength() ; i ++){  
			Element element = (Element)list1.item(0);
//			Node node1 = list1.item(0);
			Node node = (Element) element.getElementsByTagName("Device").item(0);
			Node node1;
			if(node.getAttributes().getNamedItem("IO").getNodeValue().equals("Input")){
//				node1 =(Node)element.getElementsByTagName("Conception").item(0);
				String inName = element.getAttributes().getNamedItem("Name").getNodeValue();
//				String inName = node1.getAttributes().getNamedItem("Name").getNodeValue();
				System.out.println("inName:"+inName);
				inputs.add(inName);
			}
			if(node.getAttributes().getNamedItem("IO").getNodeValue().equals("Output")){
				
				String outName = element.getAttributes().getNamedItem("Name").getNodeValue();
//				node1 =(Node)element.getElementsByTagName("Conception").item(0);
//				
//				String outName = node1.getAttributes().getNamedItem("Name").getNodeValue();
				System.out.println("outName:"+outName);
				outputs.add(outName);
			} 
		}
    }

    }