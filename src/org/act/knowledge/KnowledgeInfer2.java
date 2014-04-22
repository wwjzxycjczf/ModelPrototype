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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KnowledgeInfer2 {
	public static ArrayList<String> inputs;
	public static HashMap<String,Integer> inputnum;
	public static ArrayList<String> outputs;
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
	public static ArrayList<ArrayList<String>> stackList;
	public static HashMap<String,Object> mapVariable = new HashMap<String,Object>();
	public static HashMap<String,Object> mapdependVariable = new HashMap<String,Object>();	
	public static ArrayList<String> existstr;//存储已生成的中间节点;
//	public static HashMap<String,Object> mapOut = new HashMap<String,Object>();
	
	public static boolean over = false;
	public static boolean over1 = false;
	
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
        inputnum = new HashMap<String,Integer>();
        stackList = new ArrayList<ArrayList<String>>();
		String filePath = "resources/system requirements/0.01/"; //给我你要读取的文件夹路径  
		String graphpath = "resources/knowledge graph1/Relations.xml";

        readFolder(filePath);  
        readgraph(graphpath);
        graphinfer();
    }  
	//读取图
	public static void readgraph(String path){
		KnowledgeConstruct kc = new KnowledgeConstruct();
		Document doc2 = kc.parse(path);
		 NodeList list1 = doc2.getElementsByTagName("Element");  
		 String dependent="";
		if(list1!=null){
			  for(int i = 0; i< list1.getLength() ; i ++){  
				  Node obj = list1.item(i);
				  dependent = obj.getAttributes().getNamedItem("Name").getNodeValue();
				  for(Node node = obj.getFirstChild();node!=null;node=node.getNextSibling()){
					  if (node.getNodeName().equals("Variables")) {  
						  NodeList listv1 = ((Element) node).getElementsByTagName("VariableDes");
						  ArrayList<ArrayList<String>> arrlistv1 = new ArrayList<ArrayList<String>>();
						  int j;
						  for(j=0;j<listv1.getLength();j++){
							  Node v1 = listv1.item(j);
							  NodeList v1Var = ((Element) v1).getElementsByTagName("Conception");
							  int k;
							  ArrayList<String> v1arr = new ArrayList<String>();
							  for(k=0;k<v1Var.getLength();k++){
								  v1arr.add(v1Var.item(k).getAttributes().getNamedItem("Name").getNodeValue());
//								  v1arr.add("ID:"+v1Var.item(k).getAttributes().getNamedItem("ID").getNodeValue()+";Name"+v1Var.item(k).getAttributes().getNamedItem("Name").getNodeValue());
							  }
							  arrlistv1.add(v1arr);
						  }
						  mapVariable.put(dependent, arrlistv1);
					  }
					  if(node.getNodeName().equals("DependentVariables")) {
						  NodeList listv1 = ((Element) node).getElementsByTagName("DependentVariable");
						  ArrayList<ArrayList<String>> arrlistv1 = new ArrayList<ArrayList<String>>();
						  int j;
						  for(j=0;j<listv1.getLength();j++){
							  Node v1 = listv1.item(j);
							  NodeList v1Var = ((Element) v1).getElementsByTagName("Conception");
							  int k;
							  ArrayList<String> v1arr = new ArrayList<String>();
							  for(k=0;k<v1Var.getLength();k++){
								  v1arr.add(v1Var.item(k).getAttributes().getNamedItem("Name").getNodeValue());
//								  v1arr.add("ID:"+v1Var.item(k).getAttributes().getNamedItem("ID").getNodeValue()+";Name"+v1Var.item(k).getAttributes().getNamedItem("Name").getNodeValue());
							  }
							  arrlistv1.add(v1arr);
						  }
						  mapdependVariable.put(dependent, arrlistv1);
					  }
				  }
			  }
		}
	}
	public static void graphinfer(){
		existstr = new ArrayList<String>();
		for(int i=0;i<outputs.size();i++){
			ArrayList<String> stack = new ArrayList<String>();
//			
			over = false;
			if(mapVariable.get(outputs.get(i))!=null){
				
				ArrayList<ArrayList<String>> arrlist = (ArrayList<ArrayList<String>>) mapVariable.get(outputs.get(i));
					judge(outputs.get(i),arrlist,stack);
					if(over==true){
						filterStrList(stack);
						for(int j=0;j<stack.size();j++){
							System.out.println("stack:"+stack.get(j));
						}
//						for(int j=stack.size()-1;j>=0;j--){
//							System.out.println("stack:"+stack.get(j));
//						}
					}
				}else{
					System.out.println("不可达到");
				}
//				
//			}else{
//				System.out.println("不可达到");
//			}
		}
		for(int i=0;i<inputs.size();i++){
			ArrayList<String> stack1 = new ArrayList<String>();
			if(inputnum.get(inputs.get(i))==0){//有剩余输入
				System.out.println("剩余输入："+inputs.get(i));
				over1 = false;
				if(mapdependVariable.get(inputs.get(i))!=null){
					ArrayList<ArrayList<String>> arrlist = (ArrayList<ArrayList<String>>) mapdependVariable.get(inputs.get(i));
					judge1(inputs.get(i),arrlist,stack1);
					if(over1==true){
						filterStrList(stack1);
						for(int j=0;j<stack1.size();j++){
							System.out.println("stack1:"+stack1.get(j));
						}
//						for(int j=stack.size()-1;j>=0;j--){
//							System.out.println("stack:"+stack.get(j));
//						}
					}
				}else{
					System.out.println("没有"+inputs.get(i));
				}
			}
		}
	}
      
	public static void filterStrList(ArrayList<String> list){//过滤掉重复的偶对
//		ArrayList<String> ls = new ArrayList<String>();
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
	
	public static void judge(String dep,ArrayList<ArrayList<String>> arr,ArrayList<String> stack){
		int i=0;
		boolean bool=false;
		boolean bool1 = false;
		for(i=0;i<arr.size();i++){
			String stackstr = "<";
			ArrayList<String> arr1 = arr.get(i);
			int j;
			ArrayList<String> newarr = new ArrayList<String>();
			for(j=0;j<arr1.size();j++){
				Object o = mapVariable.get(arr1.get(j));
				if(o==null||((ArrayList<ArrayList<String>>) o).size()==0){
					bool = true;
				}else{
					bool1 = true;
				}
			}
			if(bool&&!bool1){//没有依赖，均为最初输入
				for(j=0;j<arr1.size();j++){
					int k;
					for(k=0;k<inputs.size();){
						if(inputs.get(k).equals(arr1.get(j))||inputs.get(k).equals(arr1.get(j)+" Input")||inputs.get(k).equals(arr1.get(j)+" Data Input")){
							inputnum.put(inputs.get(k), 1);
							break;
						}else{
							k++;
						}
					}
					if(k==inputs.size()){//给定的输入中没有最初输入,此条路径断了。
						System.out.println("输入中缺少"+arr1.get(j));
						continue;
					}else{
						stackstr+= arr1.get(j)+",";
					}
				}
				if(j==arr1.size()){//给定输入中均有最初输入，此路径正确
					stackstr+=dep+">";
					System.out.println(stackstr);
					stack.add(stackstr);
					over = true;
					continue;
				}
			}else if(bool&&bool1){//有最初输入和中间
				for(j=0;j<arr1.size();j++){
					if(mapVariable.get(arr1.get(j))==null){//为最初输入
						int k;
						for(k=0;k<inputs.size();){
							if(inputs.get(k).equals(arr1.get(j))||inputs.get(k).equals(arr1.get(j)+" Input")||inputs.get(k).equals(arr1.get(j)+" Data Input")){
								inputnum.put(inputs.get(k), 1);
								break;
							}else{
								k++;
							}
						}
						if(k==inputs.size()){//给定的输入中没有最初输入,此条路径断了。
							System.out.println("输入中缺少"+arr1.get(j));
							continue;
						}else{
							stackstr+=arr1.get(j)+",";
						}
					}else{//为中间节点
						existstr.add(arr1.get(j));
						newarr.add(arr1.get(j));
						stackstr+=arr1.get(j)+",";
					}
				}
				if(j==arr1.size()){//给定的输入中均有最初输入，此条路径正确但未完全。
					stackstr+=dep+">";
					System.out.println(stackstr);
					stack.add(stackstr);
				}
				for(j=0;j<newarr.size();j++){
					judge(newarr.get(j),(ArrayList<ArrayList<String>>) mapVariable.get(newarr.get(j)),stack);	
				}
			}else{//均为中间
				
				newarr = arr1;
				for(j=0;j<arr1.size();j++){
					existstr.add(arr1.get(j));
					stackstr+=arr1.get(j)+",";
				}
				stackstr+=dep+">";
				System.out.println(stackstr);
				stack.add(stackstr);
				for(j=0;j<newarr.size();j++){
					judge(newarr.get(j),(ArrayList<ArrayList<String>>)mapVariable.get(newarr.get(j)),stack);	
				}
			}
		}
	}
	public static void judge1(String dep,ArrayList<ArrayList<String>> arr,ArrayList<String> stack){//正向推理
		int i=0;
		for(i=0;i<arr.size();i++){//arr表示dep的所有的dependentVariable
			
			ArrayList<String> arr1 = arr.get(i);//arr1表示dep的dependentVariables
			int j;
			for(j=0;j<arr1.size();j++){
				ArrayList<ArrayList<String>> inputarr = (ArrayList<ArrayList<String>>) mapVariable.get(arr1.get(j));//表示每个dependentVariable对应的Variable
				int k;
				for(k=0;k<inputarr.size();k++){
					String stackstr = "<";
					ArrayList<String> arr2 = inputarr.get(k);
					int l;
					for(l=0;l<arr2.size();l++){//arr2表示Variables
						ArrayList<ArrayList<String>> o = (ArrayList<ArrayList<String>>) mapVariable.get(arr2.get(l));
						if((o!=null)
								&&!(exist(arr2.get(l),existstr)||existinputs(arr2.get(l),inputs))){//依赖知识是中间节点且不在给定输入或已生成的中间节点中
							over = false;
							ArrayList<String> stack1 = new ArrayList<String>();
							judge(arr2.get(l),o,stack1);
							if(over==false){
								System.out.println(arr2.get(l)+"不能生成");
								break;
							}else{
								existstr.add(arr2.get(l));
							}
							
						}else if((o==null)&&!(exist(arr2.get(l),existstr)||existinputs(arr2.get(l),inputs))){//依赖知识初始输入且不在给定输入或已生成的中间节点中
							System.out.println(arr2.get(l)+"不在给定输入或已生成的节点中");
//							continue;
							break;
						}
					}
					if(l==arr2.size()){//要么是在给定输入要么是在已生成节点中;
						for(l=0;l<arr2.size();l++){
							stackstr+=arr2.get(l)+",";
						}
						stackstr+=arr1.get(j)+">";
						System.out.println("stackstr"+stackstr);
						Object obj = mapdependVariable.get(arr1.get(j));
						if(obj==null||((ArrayList<ArrayList<String>>) obj).size()==0){//为最终输出
							System.out.println("成功");
							over1 = true;
						}else{//不是最终输入
							judge1(arr1.get(j),(ArrayList<ArrayList<String>>)obj,stack);
						}
					}
				}
			}
		}
		
	}
	public static boolean exist(String s,ArrayList<String> list){//判断s是否在list中
		for(int i=0;i<list.size();i++){
			if(s.equals(list.get(i))){
				return true;
			}
		}
		return false;
	}
	public static boolean existinputs(String s,ArrayList<String> list){
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals(s)||list.get(i).equals(s+" Input")||list.get(i).equals(s+" Data Input")){
				return true;
			}
		}
		return false;
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
			Element element = (Element)list1.item(0);
			Node node = (Element) element.getElementsByTagName("Device").item(0);
			Node node1;
			if(node.getAttributes().getNamedItem("IO").getNodeValue().equals("Input")){
				String inName = element.getAttributes().getNamedItem("Name").getNodeValue();
				System.out.println("inName:"+inName);
				inputs.add(inName);
				inputnum.put(inName, 0);
			}
			if(node.getAttributes().getNamedItem("IO").getNodeValue().equals("Output")){
				
				String outName = element.getAttributes().getNamedItem("Name").getNodeValue();
				System.out.println("outName:"+outName);
				outputs.add(outName);
			} 
		}
    }

    
}
