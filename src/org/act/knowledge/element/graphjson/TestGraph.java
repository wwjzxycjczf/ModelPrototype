package org.act.knowledge.element.graphjson;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
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

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;


import com.realpersist.gef.layout.NodeJoiningDirectedGraphLayout;

/**
 * @author walnut
 * @version ����ʱ�䣺2007-7-18
 */
public class TestGraph extends JApplet {


	public Map gefNodeMap = null;

	public Map graphNodeMap = null;

	public List edgeList = null;

	DirectedGraph directedGraph = null;

	JGraph graph = null;
	
	public TestGraph() {

	}

	public void init() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		graphInit();
		
		paintGraph();

	}

	private void paintGraph() {
		try {
			
			// �������
			NodeBean a1=new NodeBean("a1");
			NodeBean a11=new NodeBean("a11");
			NodeBean a12=new NodeBean("a12");
			NodeBean a13=new NodeBean("a13");
			NodeBean a121=new NodeBean("a121");
			NodeBean a122=new NodeBean("a122");
			NodeBean a123=new NodeBean("a123");
			NodeBean a1231=new NodeBean("a1231");
			NodeBean a1232=new NodeBean("a1232");
			NodeBean a1233=new NodeBean("a1233");
			NodeBean a1234=new NodeBean("a1234");
			
			NodeBean b1=new NodeBean("b1");
			NodeBean b2=new NodeBean("b2");
			NodeBean c=new NodeBean("c");
			
			EdgeBean as1=new EdgeBean(a1,a11,new Long(20));
			EdgeBean as2=new EdgeBean(a1,a12,new Long(20));
			EdgeBean as3=new EdgeBean(a1,a13,new Long(20));
			EdgeBean as4=new EdgeBean(a12,a121,new Long(20));
			EdgeBean as5=new EdgeBean(a12,a122,new Long(20));
			EdgeBean as6=new EdgeBean(a12,a123,new Long(20));
			EdgeBean as7=new EdgeBean(a123,a1231,new Long(20));
			EdgeBean as8=new EdgeBean(a123,a1232,new Long(20));
			EdgeBean as9=new EdgeBean(a123,a1233,new Long(20));
			EdgeBean as10=new EdgeBean(a123,a1234,new Long(20));
			EdgeBean as11=new EdgeBean(a1232,a11,new Long(20));
			EdgeBean as12=new EdgeBean(a1233,a122,new Long(20));
			EdgeBean as13=new EdgeBean(a123,a1,new Long(20));
			// ��һ���ߺ������û����ͨ
			EdgeBean as14=new EdgeBean(b1,b2,new Long(20));
			// ��һ���ߺ������û����ͨ��ָ������
			EdgeBean as15=new EdgeBean(c,c,new Long(20));
			
			List edgeBeanList =new ArrayList();
			
			edgeBeanList.add(as1);
			edgeBeanList.add(as2);
			edgeBeanList.add(as3);
			edgeBeanList.add(as4);
			edgeBeanList.add(as5);
			edgeBeanList.add(as6);
			edgeBeanList.add(as7);
			edgeBeanList.add(as8);
			edgeBeanList.add(as9);
			edgeBeanList.add(as10);
			edgeBeanList.add(as11);
			edgeBeanList.add(as12);
			edgeBeanList.add(as13);
			edgeBeanList.add(as14);
			edgeBeanList.add(as15);
			
			// ������ݣ�����ͼ
			gefNodeMap = new HashMap();
			graphNodeMap = new HashMap();
			edgeList = new ArrayList();
			directedGraph = new DirectedGraph();
			GraphModel model = new DefaultGraphModel();
			graph.setModel(model);
			Map attributes = new Hashtable();
			// Set Arrow
			Map edgeAttrib = new Hashtable();
			GraphConstants.setLineEnd(edgeAttrib, GraphConstants.ARROW_CLASSIC);
			GraphConstants.setEndFill(edgeAttrib, true);
			graph.setJumpToDefaultPort(true);
			
			if (edgeBeanList == null || edgeBeanList.size() == 0) {
				graph.repaint();
				return;
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
			
			// �Զ����� ������DirectedGraphLayout�������쳣��ͼ����������ͨ�ģ������NodeJoiningDirectedGraphLayout
			// ���߿��ԶԷ���ͨͼ���в��������㣬��Ч����ǰ�ߺã�������ѡǰ�ߣ���ǰ�߲����Դ���ʱ���ú���
			try{
				new DirectedGraphLayout().visit(directedGraph);
			}catch(Exception e1){
				new NodeJoiningDirectedGraphLayout().visit(directedGraph);
			}
			
			int self_x=50;
			int self_y=50;
			int base_y=10;
			if(graphNodeMap!=null&&gefNodeMap!=null&&graphNodeMap.size()>gefNodeMap.size()){
				base_y=self_y+GraphProp.NODE_HEIGHT;
			}
			
			// ��ͼ����ӽڵ�node
			Collection nodeCollection = graphNodeMap.values();
			if (nodeCollection != null) {
				Iterator nodeIterator = nodeCollection.iterator();
				if (nodeIterator != null) {
					while (nodeIterator.hasNext()) {
						DefaultGraphCell node = (DefaultGraphCell) nodeIterator.next();
						NodeBean userObject = (NodeBean) node.getUserObject();
						if (userObject == null) {
							continue;
						}
						Node gefNode = (Node) gefNodeMap.get(userObject);
						if(gefNode==null){
							// ������Ϊ��һ���ڵ���һ��ָ������ıߵ�ʱ��������gefGraph�в�û�м��������ߣ�gefGraph���ܼ����ָ���Լ��ߵĲ��֣���
							// ���Ե�����Ҫ����ͼ�иýڵ�ֻ��һ��ָ������ıߵ�ʱ��������gefNodeMap�о��Ҳ�����Ӧ�ڵ���
							// ���ʱ�������ֹ�����ýڵ�� X,Y ���
							gefNode=new Node();
							gefNode.x=self_x;
							gefNode.y=self_y-base_y;
							self_x+=(10+GraphProp.NODE_WIDTH);
						}
						Map nodeAttrib = new Hashtable();
						GraphConstants.setBorderColor(nodeAttrib, Color.black);
						Rectangle2D Bounds = new Rectangle2D.Double(gefNode.x,gefNode.y+base_y, GraphProp.NODE_WIDTH,GraphProp.NODE_HEIGHT);
						GraphConstants.setBounds(nodeAttrib, Bounds);
						attributes.put(node, nodeAttrib);
					}// while
				}
			}
			
			// ��ͼ����ӱ�
			if (edgeList == null) {
				//logger.error("edge list is null");
				return;
			}
			for (int i = 0; i < edgeList.size(); i++) {
				UnionEdge unionEdge = (UnionEdge) edgeList.get(i);
				if (unionEdge == null) {
					//logger.error("union edge is null");
					continue;
				}
				ConnectionSet cs = new ConnectionSet(unionEdge.getEdge(),unionEdge.getSourceNode().getChildAt(0), unionEdge.getTargetNode().getChildAt(0));
				Object[] cells = new Object[] { unionEdge.getEdge(),unionEdge.getSourceNode(), unionEdge.getTargetNode() };
				attributes.put(unionEdge.getEdge(), edgeAttrib);
				model.insert(cells, attributes, cs, null, null);
			}
			
			graph.repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void graphInit() {

		// Construct Model and Graph
		GraphModel model = new DefaultGraphModel();
		graph = new JGraph(model);
		graph.setSelectionEnabled(true);
		
		// ��ʾapplet
		JScrollPane scroll=new JScrollPane(graph);
		this.getContentPane().add(scroll);
		
		this.setSize(new Dimension(800, 800));

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

		// ����GEF�� node edge������������graph node��layout
		addEdgeGef(source, target,weight,edgeString);
		
		// ��������Ҫ�õ�graph�� node edge
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
		// ����GEF�� node edge������������graph node��layout
		Node gefSourceNode = null;
		Node gefTargetNode = null;
		gefSourceNode = (Node) gefNodeMap.get(source);
		if (gefSourceNode == null) {
			gefSourceNode = new Node();
			gefSourceNode.width = GraphProp.NODE_WIDTH;
			gefSourceNode.height = GraphProp.NODE_WIDTH;
			//gefSourceNode.setPadding(new Insets(GraphProp.NODE_TOP_PAD,GraphProp.NODE_LEFT_PAD, GraphProp.NODE_BOTTOM_PAD,GraphProp.NODE_RIGHT_PAD));
			directedGraph.nodes.add(gefSourceNode);
			gefNodeMap.put(source, gefSourceNode);
		}
		
		gefTargetNode = (Node) gefNodeMap.get(target);
		if (gefTargetNode == null) {
			gefTargetNode = new Node();
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

}