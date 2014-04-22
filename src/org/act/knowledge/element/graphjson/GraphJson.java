package org.act.knowledge.element.graphjson;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.act.server.XMLJsonTraverse;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import com.realpersist.gef.layout.NodeJoiningDirectedGraphLayout;

public class GraphJson  {
	private String graphjsontxt = "D:/Program Files/apache-tomcat-7.0.32/webapps/ModelPrototype/resources/knowledge graph1/graphjson.txt";
	private String graphjson;
	private JSONObject jo;
	private List  edgeBeanList =new ArrayList();
	private List nodeBeanList =new ArrayList();
	public Map gefNodeMap = null;

	public Map graphNodeMap = null;

	public List edgeList = null;

	DirectedGraph directedGraph = null;

	JGraph graph = null;
	private int formulanum = 0;
	public  GraphJson() throws IOException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		graphInit();
		
		getgraphjson();

	}
//	public static void main(String args[]) throws Exception {
//		new GraphJson();
//	}
	public synchronized void readgraphjson(){
		 String laststr = "";
		    try   
		    {       
		        File f = new File(graphjsontxt);      
		        if(f.isFile()&&f.exists())  
		        {       
		            InputStreamReader read = new InputStreamReader(new FileInputStream(f),"gbk");       
		            BufferedReader reader=new BufferedReader(read);       
		            String line;       
		            while ((line = reader.readLine()) != null)   
		            {        
		            	 System.out.println("line "+ line);
		            	laststr += line;       
		            }         
		            read.close();      
		        }     
		    } catch (Exception e)   
		    {         
		        e.printStackTrace();     
		    }     
		    if(laststr==""){
		    	jo = null;
		    }else{
		    	graphjson = laststr;
		    	jo = JSONObject.fromObject(laststr);
		    }
//		    return fileContent;  
//		 File file = new File(graphjsontxt);
//		    BufferedReader reader = null;
//		    String laststr = "";
//		    try {
//		     //System.out.println("以行为单位读取文件内容，一次读一整行：");
//		     reader = new BufferedReader(new FileReader(file));
//		     String tempString = null;
//		     int line = 1;
//		     //一次读入一行，直到读入null为文件结束
//		     while ((tempString = reader.readLine()) != null) {
//		      //显示行号
//		      System.out.println("line "+ line+ ": " +tempString);
//		      laststr = laststr + tempString;
//		      line++ ;
//		     }
//		     reader.close();
//		    } catch (IOException e) {
//		     e.printStackTrace();
//		    } finally {
//		     if (reader != null) {
//		      try {
//		       reader.close();
//		      } catch (IOException e1) {
//		      }
//		     }
//		    }
//		    if(laststr==""){
//		    	jo = null;
//		    }else{
//		    	graphjson = laststr;
//		    	jo = JSONObject.fromObject(laststr);
//		    }
//		    return  laststr;
	}
	public synchronized  String getgraphjson() throws IOException{
		readgraphjson();
		if(jo==null){//文件为空
			XMLJsonTraverse xmljsontraverse= new XMLJsonTraverse();
			Document relationxml = (Document) xmljsontraverse.parse("D:/Program Files/apache-tomcat-7.0.32/webapps/ModelPrototype/resources/knowledge graph1/Relations.xml");
			NodeList list = relationxml.getElementsByTagName("Element");
//			List edgeBeanList =new ArrayList();
//			List nodeBeanList =new ArrayList();
			
			for(int i=0;i<list.getLength();i++){
				Element ele1 = (Element)list.item(i);
//				String nodename = ele1.getAttributes().getNamedItem("ID").getNodeValue()+":"+ele1.getAttributes().getNamedItem("Name").getNodeValue();
//				String 
				NodeBean nodename=new NodeBean( ele1.getAttributes().getNamedItem("ID").getNodeValue()+"."+ele1.getAttributes().getNamedItem("Name").getNodeValue());
				int k=0;
				for(k=0;k<nodeBeanList.size();k++){
					if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename.getName())){
						nodename = (NodeBean) nodeBeanList.get(k);
						break;
					}
				}
				if(k==nodeBeanList.size()){
					nodeBeanList.add(nodename);	
				}
				
		        if(ele1.getElementsByTagName("Variables").item(0)==null){//如果ele1中没有Variables
		        	continue;
		        }else{
		        	for(Node node1 = ele1.getFirstChild();node1!=null;node1=node1.getNextSibling()){
						 if (node1.getNodeType() == Node.ELEMENT_NODE) {  
							 if (node1.getNodeName().equals("Variables")) {
								 NodeList listv =  ((Element) node1).getElementsByTagName("VariableDes");
						         for(int j=0;j<listv.getLength();j++){
						        	Node v2 = listv.item(j);
						        	NodeList v2des = ((Element) v2).getElementsByTagName("Formula");
//						        	nodename = v2des.item(0).getAttributes().getNamedItem("ID")+":"+ v2des.item(0).getAttributes().getNamedItem("Name");
						        	NodeBean nodename1 = new NodeBean("数学公式"+formulanum+"."+v2des.item(0).getAttributes().getNamedItem("ID").getNodeValue()+"."+ v2des.item(0).getAttributes().getNamedItem("Name").getNodeValue());
						        	formulanum++;
						        	nodeBeanList.add(nodename1);
						        	EdgeBean as1=new EdgeBean(nodename1,nodename,new Long(20));
						        	edgeBeanList.add(as1);
					 				NodeList v2Var = ((Element) v2).getElementsByTagName("Conception");
					 				for(int m=0;m<v2Var.getLength();m++){
					 					Element ele = (Element)v2Var.item(m);
//					 					String nodename = ele1.getAttributes().getNamedItem("ID").getNodeValue()+"."+ele1.getAttributes().getNamedItem("Name").getNodeValue();
//					 					String 
					 					NodeBean nodename2=new NodeBean( ele.getAttributes().getNamedItem("ID").getNodeValue()+"."+ele.getAttributes().getNamedItem("Name").getNodeValue());
					 					k=0;
					 					for(k=0;k<nodeBeanList.size();k++){
					 						if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename2.getName())){
					 							nodename2 = (NodeBean) nodeBeanList.get(k);
					 							break;
					 						}
					 					}
					 					if(k==nodeBeanList.size()){
					 						nodeBeanList.add(nodename2);	
					 					}
					 					EdgeBean as=new EdgeBean(nodename2,nodename1,new Long(20));
							        	edgeBeanList.add(as);
			 						}
						         }
						        }
							}
			            }
		            }
			}
			graphjson = "{\"type\":\"graphjson\",";
			String arrjson = "\"arrows\":[";
			for(int l=0;l<edgeBeanList.size();l++){
				EdgeBean eg = (EdgeBean)edgeBeanList.get(l);
				arrjson+="{\"source\":\""+eg.getsourceNodeBean().getName()+"\",\"target\":\""+eg.gettargetNodeBean().getName()+"\"},";
			}
			arrjson = arrjson.substring(0,arrjson.length()-1);
			arrjson+="]";
			System.out.println(arrjson);
			graphjson+=arrjson;
//			graphjson+=",";
//			graphjson += arrjson+",";
		//	String nodejsonstr = Getnodexyjson();
//			graphjson += nodejsonstr;
			graphjson+="}";
			System.out.println(graphjson);
			writeFile(graphjsontxt,graphjson);
		}
			return graphjson;
}

//public synchronized String addgraph(String relation) throws ParserConfigurationException, SAXException, IOException{
//	StringReader sr = new StringReader(relation); 
//	InputSource is = new InputSource(sr); 
//	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
//	DocumentBuilder builder=factory.newDocumentBuilder(); 
//	Document xml = builder.parse(is);
////	Document xml = (Document) DocumentHelper.parseText(relation);//便得到xml文档
//	NodeList list1 = xml.getElementsByTagName("Relation");
//	if(list1!=null){
//			Element rel = (Element) list1.item(0);
//        	NodeList list2 =  ((Element) rel).getElementsByTagName("Formula");
//        	NodeBean nodename1 = new NodeBean("数学公式"+formulanum+"."+list2.item(0).getAttributes().getNamedItem("ID")+"."+ list2.item(0).getAttributes().getNamedItem("Name"));
//			formulanum++;
//			nodeBeanList.add(nodename1);
//			for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
//				 if (node.getNodeType() == Node.ELEMENT_NODE) {  
//					 if (node.getNodeName().equals("DependentVariables")) {  
//						 
//	                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
//	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
//	                			 NodeBean nodename=new NodeBean( node2.getAttributes().getNamedItem("ID").getNodeValue()+"."+node2.getAttributes().getNamedItem("Name").getNodeValue());
////	             				int k=0;
////	             				for(k=0;k<nodeBeanList.size();k++){
////	             					if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename.getName())){
////	             						nodename = (NodeBean) nodeBeanList.get(k);
////	             						break;
////	             					}
////	             				}
////	             				if(k==nodeBeanList.size()){
//	                			 nodeBeanList.add(nodename);	
//	             				EdgeBean as1=new EdgeBean(nodename1,nodename,new Long(20));
//	             	        	edgeBeanList.add(as1);
////	             				}
//	                		 }
//	                	 }
//	                 }
//	                 if (node.getNodeName().equals("Variables")) {  
//	                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
//	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
//	                			 NodeBean nodename2=new NodeBean( node2.getAttributes().getNamedItem("ID").getNodeValue()+"."+node2.getAttributes().getNamedItem("Name").getNodeValue());
//	                			 int k=0;
//	             				for(k=0;k<nodeBeanList.size();k++){
//	             					if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename2.getName())){
//	             						nodename2 = (NodeBean) nodeBeanList.get(k);
//	             						break;
//	             					}
//	             				}
//	             				if(k==nodeBeanList.size()){
//	             					nodeBeanList.add(nodename2);	
//	             				}
//				 				EdgeBean as=new EdgeBean(nodename2,nodename1,new Long(20));
//						        edgeBeanList.add(as);
////	             				}
//	                		 }
//	                	 }
//	                 }
//				 }
//			}
//	}
//	graphjson = "{\"type\":\"graphjson\",";
//	String arrjson = "\"arrows\":[";
//	for(int l=0;l<edgeBeanList.size();l++){
//		EdgeBean eg = (EdgeBean)edgeBeanList.get(l);
//		arrjson+="{\"source\":\""+eg.getsourceNodeBean().getName()+"\",\"target\":\""+eg.gettargetNodeBean().getName()+"\"},";
//	}
//	arrjson = arrjson.substring(0,arrjson.length()-1);
//	arrjson+="]";
//	System.out.println(arrjson);
//	graphjson += arrjson+",";
//	String nodejsonstr = Getnodexyjson();
//	graphjson += nodejsonstr;
//	graphjson+="}";
//	System.out.println(graphjson);
//	writeFile(graphjsontxt,graphjson);
////	readxmldocrelation(xml,"add");
//	System.out.println("addgraph成功");
//	return graphjson;
//}
//public synchronized String updategraph(String relation) throws ParserConfigurationException, SAXException, IOException{
//	StringReader sr = new StringReader(relation); 
//	InputSource is = new InputSource(sr); 
//	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
//	DocumentBuilder builder=factory.newDocumentBuilder(); 
//	Document xml = builder.parse(is);
////	Document xml = (Document) DocumentHelper.parseText(relation);//便得到xml文档
//	NodeList list1 = xml.getElementsByTagName("Relation");
//	if(list1!=null){
//			Element rel = (Element) list1.item(0);
//        	NodeList list2 =  ((Element) rel).getElementsByTagName("Formula");
//        	NodeBean nodename1=null;
//        	int k=0;
//				for(k=0;k<nodeBeanList.size();k++){//判断公式是否相同
//					if(((NodeBean)nodeBeanList.get(k)).getName().equals(list2.item(0).getAttributes().getNamedItem("ID")+"."+ list2.item(0).getAttributes().getNamedItem("Name"))){
//						 nodename1 = (NodeBean) nodeBeanList.get(k);
//						break;
//					}
//				}
//				if(k==nodeBeanList.size()){
//					nodename1 = new NodeBean("数学公式"+formulanum+"."+list2.item(0).getAttributes().getNamedItem("ID")+"."+ list2.item(0).getAttributes().getNamedItem("Name"));
//					formulanum++;
//					nodeBeanList.add(nodename1);
// 				}
////        	NodeBean nodename1 = new NodeBean("数学公式"+formulanum+"."+list2.item(0).getAttributes().getNamedItem("ID")+"."+ list2.item(0).getAttributes().getNamedItem("Name"));
//			for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
//				 if (node.getNodeType() == Node.ELEMENT_NODE) {  
//					 if (node.getNodeName().equals("DependentVariables")) {  
//						 
//	                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
//	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
//	                			 NodeBean nodename=new NodeBean( node2.getAttributes().getNamedItem("ID").getNodeValue()+"."+node2.getAttributes().getNamedItem("Name").getNodeValue());
//	             				  k=0;
//		             				for(k=0;k<nodeBeanList.size();k++){
//		             					if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename.getName())){
//		             						nodename = (NodeBean) nodeBeanList.get(k);
//		             						break;
//		             					}
//		             				}
////	                			 nodeBeanList.add(nodename);	
//		             				if(k==nodeBeanList.size()){
//		             					nodeBeanList.add(nodename);	
//		             				}
//	             				EdgeBean as1=new EdgeBean(nodename1,nodename,new Long(20));
//	             	        	edgeBeanList.add(as1);
////	             				}
//	                		 }
//	                	 }
//	                 }
//	                 if (node.getNodeName().equals("Variables")) {  
//	                	 for(Node node2 = node.getFirstChild();node2!=null;node2=node2.getNextSibling()){
//	                		 if (node2.getNodeType() == Node.ELEMENT_NODE) { 
//	                			 NodeBean nodename2=new NodeBean( node2.getAttributes().getNamedItem("ID").getNodeValue()+"."+node2.getAttributes().getNamedItem("Name").getNodeValue());
//	                			  k=0;
//	             				for(k=0;k<nodeBeanList.size();k++){
//	             					if(((NodeBean)nodeBeanList.get(k)).getName().equals(nodename2.getName())){
//	             						nodename2 = (NodeBean) nodeBeanList.get(k);
//	             						break;
//	             					}
//	             				}
//	             				if(k==nodeBeanList.size()){
//	             					nodeBeanList.add(nodename2);	
//	             				}
//				 				EdgeBean as=new EdgeBean(nodename2,nodename1,new Long(20));
//						        edgeBeanList.add(as);
////	             				}
//	                		 }
//	                	 }
//	                 }
//				 }
//			}
//	}
//	graphjson = "{\"type\":\"graphjson\",";
//	String arrjson = "\"arrows\":[";
//	for(int l=0;l<edgeBeanList.size();l++){
//		EdgeBean eg = (EdgeBean)edgeBeanList.get(l);
//		arrjson+="{\"source\":\""+eg.getsourceNodeBean().getName()+"\",\"target\":\""+eg.gettargetNodeBean().getName()+"\"},";
//	}
//	arrjson = arrjson.substring(0,arrjson.length()-1);
//	arrjson+="]";
//	System.out.println(arrjson);
//	graphjson += arrjson+",";
//	String nodejsonstr = Getnodexyjson();
//	graphjson += nodejsonstr;
//	graphjson+="}";
//	System.out.println(graphjson);
//	writeFile(graphjsontxt,graphjson);
////	readxmldocrelation(xml,"add");
//	System.out.println("updategraph成功");
//	return graphjson;
//}
//private void graphInit() {
//
//	// Construct Model and Graph
//	GraphModel model = new DefaultGraphModel();
//	graph = new JGraph(model);
//	graph.setSelectionEnabled(true);
//	
//	// 显示applet
//	JScrollPane scroll=new JScrollPane(graph);
////	this.getContentPane().add(scroll);
////	
////	this.setSize(new Dimension(800, 800));
//
//}

private String Getnodexyjson(){
	// 解析数据，构造图
	// Construct Model and Graph

	GraphModel model = new DefaultGraphModel();
	graph = new JGraph(model);
	graph.setSelectionEnabled(true);

				gefNodeMap = new HashMap();
				graphNodeMap = new HashMap();
				edgeList = new ArrayList();
				directedGraph = new DirectedGraph();
				GraphModel model1 = new DefaultGraphModel();
				graph.setModel(model1);
				Map attributes = new Hashtable();
				// Set Arrow
				Map edgeAttrib = new Hashtable();
				GraphConstants.setLineEnd(edgeAttrib, GraphConstants.ARROW_CLASSIC);
				GraphConstants.setEndFill(edgeAttrib, true);
				graph.setJumpToDefaultPort(true);
				
				if (edgeBeanList == null || edgeBeanList.size() == 0) {
					graph.repaint();
					return "";
				}
				Iterator edgeBeanIt = edgeBeanList.iterator();
				while (edgeBeanIt.hasNext()) {
					EdgeBean edgeBean = (EdgeBean) edgeBeanIt.next();
					NodeBean sourceAction = edgeBean.getsourceNodeBean();
					NodeBean targetAction = edgeBean.gettargetNodeBean();
					Long ageLong = edgeBean.getStatCount();
					String edgeString = "(" + ageLong +  ")";
					addEdge(sourceAction, targetAction, 20, edgeString);
				}
				
				// 自动布局 首先用DirectedGraphLayout如果出现异常（图不是整体连通的）则采用NodeJoiningDirectedGraphLayout
				// 后者可以对非连通图进行布局坐标计算，但效果不如前者好，所以首选前者，当前者不可以处理时采用后者
//				try{
//					new DirectedGraphLayout().visit(directedGraph);
//				}catch(Exception e1){
//					new NodeJoiningDirectedGraphLayout().visit(directedGraph);
//				}
				new NodeJoiningDirectedGraphLayout().visit(directedGraph);
				
				int self_x=50;
				int self_y=50;
				int base_y=10;
				if(graphNodeMap!=null&&gefNodeMap!=null&&graphNodeMap.size()>gefNodeMap.size()){
					base_y=self_y+GraphProp.NODE_HEIGHT;
				}
				// 向图中添加节点node
				Collection nodeCollection = graphNodeMap.values();
				String nodejsonstr="\"Nodes\":[";
				if (nodeCollection != null) {
					Iterator nodeIterator = nodeCollection.iterator();
					if (nodeIterator != null) {
						while (nodeIterator.hasNext()) {
							DefaultGraphCell node = (DefaultGraphCell) nodeIterator.next();
							NodeBean userObject = (NodeBean) node.getUserObject();
							if (userObject == null) {
								continue;
							}
							org.eclipse.draw2d.graph.Node gefNode = (org.eclipse.draw2d.graph.Node) gefNodeMap.get(userObject);
							if(gefNode==null){
								// 这是因为当一个节点有一个指向自身的边的时候，我们在gefGraph中并没有计算这条边（gefGraph不能计算包含指向自己边的布局），
								// 所以当在所要画的图中该节点只有一条指向自身的边的时候，我们在gefNodeMap中就找不到相应节点了
								// 这个时候，我们手工给出该节点的 X,Y 坐标
								gefNode=new org.eclipse.draw2d.graph.Node();
								gefNode.x=self_x;
								gefNode.y=self_y-base_y;
								self_x+=(10+GraphProp.NODE_WIDTH);
							}
							nodejsonstr+="{\"name\":\""+userObject.getName()+"\",\"x\":"+gefNode.x+",\"y\":"+(gefNode.y+base_y)+"},";
							
							Map nodeAttrib = new Hashtable();
							GraphConstants.setBorderColor(nodeAttrib, Color.black);
							Rectangle2D Bounds = new Rectangle2D.Double(gefNode.x,gefNode.y+base_y, GraphProp.NODE_WIDTH,GraphProp.NODE_HEIGHT);
							GraphConstants.setBounds(nodeAttrib, Bounds);
							attributes.put(node, nodeAttrib);
						}// while
						nodejsonstr = nodejsonstr.substring(0,nodejsonstr.length()-1);
						nodejsonstr+="]";
						System.out.println(nodejsonstr);
					}
				}
				return nodejsonstr;
}
/**
 * @param source
 * @param target
 */
private void addEdge(NodeBean source, NodeBean target, int weight,String edgeString) {

	if (source == null || target == null) {
		return;
	}
	if (gefNodeMap == null) {
		gefNodeMap = new HashMap();
	}
	if (graphNodeMap == null) {
		graphNodeMap = new HashMap();
	}
	if (edgeList == null) {
		edgeList = new ArrayList();
	}
	if (directedGraph == null) {
		directedGraph = new DirectedGraph();
	}

	// 建立GEF的 node edge将来用来计算graph node的layout
	addEdgeGef(source, target,weight,edgeString);
	
	// 建立真正要用的graph的 node edge
	DefaultGraphCell sourceNode = null;
	DefaultGraphCell targetNode = null;
	sourceNode = (DefaultGraphCell) graphNodeMap.get(source);
	if (sourceNode == null) {
		sourceNode = new DefaultGraphCell(source);
		sourceNode.addPort();
		graphNodeMap.put(source, sourceNode);
	}
	targetNode = (DefaultGraphCell) graphNodeMap.get(target);
	if (targetNode == null) {
		targetNode = new DefaultGraphCell(target);
		targetNode.addPort();
		graphNodeMap.put(target, targetNode);
	}
	DefaultEdge edge = new DefaultEdge(edgeString);
	UnionEdge unionEdge = new UnionEdge();
	unionEdge.setEdge(edge);
	unionEdge.setSourceNode(sourceNode);
	unionEdge.setTargetNode(targetNode);

	edgeList.add(unionEdge);

}

/**
 * @param source
 * @param target
 * @param weight
 * @param edgeString
 */

private void addEdgeGef(NodeBean source, NodeBean target, int weight, String edgeString) {

	if(source.equals(target)){
		return;
	}
	// 建立GEF的 node edge将来用来计算graph node的layout
	org.eclipse.draw2d.graph.Node gefSourceNode = null;
	org.eclipse.draw2d.graph.Node gefTargetNode = null;
	gefSourceNode = (org.eclipse.draw2d.graph.Node) gefNodeMap.get(source);
	if (gefSourceNode == null) {
		gefSourceNode = new org.eclipse.draw2d.graph.Node();
		gefSourceNode.width = GraphProp.NODE_WIDTH;
		gefSourceNode.height = GraphProp.NODE_WIDTH;
		//gefSourceNode.setPadding(new Insets(GraphProp.NODE_TOP_PAD,GraphProp.NODE_LEFT_PAD, GraphProp.NODE_BOTTOM_PAD,GraphProp.NODE_RIGHT_PAD));
		directedGraph.nodes.add(gefSourceNode);
		gefNodeMap.put(source, gefSourceNode);
	}
	
	gefTargetNode = (org.eclipse.draw2d.graph.Node) gefNodeMap.get(target);
	if (gefTargetNode == null) {
		gefTargetNode = new org.eclipse.draw2d.graph.Node();
		gefTargetNode.width = GraphProp.NODE_WIDTH;
		gefTargetNode.height = GraphProp.NODE_WIDTH;
		//gefTargetNode.setPadding(new Insets(GraphProp.NODE_TOP_PAD,GraphProp.NODE_LEFT_PAD, GraphProp.NODE_BOTTOM_PAD,GraphProp.NODE_RIGHT_PAD));
		directedGraph.nodes.add(gefTargetNode);
		gefNodeMap.put(target, gefTargetNode);
	}
	
	Edge gefEdge1=null;
	try{
		gefEdge1 = new Edge(gefSourceNode, gefTargetNode);
		gefEdge1.weight = weight;
		directedGraph.edges.add(gefEdge1);
	}catch(Exception e){
		e.printStackTrace();
	}
}

public static void writeFile(String filePathAndName, String fileContent) { 
	  try { 
	   File f = new File(filePathAndName); 
	   if (!f.exists()) { 
	    f.createNewFile(); 
	   } 
	   OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"gbk"); 
	   BufferedWriter writer=new BufferedWriter(write);   
	   //PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathAndName))); 
	   //PrintWriter writer = new PrintWriter(new FileWriter(filePathAndName)); 
	   writer.write(fileContent); 
	   writer.close(); 
	  } catch (Exception e) { 
	   System.out.println("写文件内容操作出错"); 
	   e.printStackTrace(); 
	  } 
	} 
}



