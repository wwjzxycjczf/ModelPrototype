package org.act.knowledge.element.concepts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.act.server.XMLJsonTraverse;
//import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ConceptsList {
	// private NodeList conceptsList;
	// private List<Concept> conceptsList = null;
	private String ConceptsXML = "resources/knowledge graph1/Concepts.xml";
	private XMLJsonTraverse xmljsontraverse = new XMLJsonTraverse();
	private Document conceptxml;
	private Element root;
	private String conceptstr = "";

	// private String deleteconceptstr="";
	// private String modifyconceptstr="";
	/**
	 * 读取Concepts的xml文档，设置Concept列表
	 * 
	 * @throws Exception
	 */
	public synchronized void setNetNodeList() throws Exception {
		// conceptsList = Collections.synchronizedList(new
		// ArrayList<Concept>());// 新建Oid列表, 不与get方法冲突

	}

	/**
	 * 读取Concepts的xml文档，设置Concept列表
	 * 
	 * @throws Exception
	 */
	public synchronized void readConceptsXml() {
		conceptxml = (Document) xmljsontraverse.parse(ConceptsXML);
		root = (Element) conceptxml.getDocumentElement();
		// conceptsList = conceptxml.getElementsByTagName("Conception");
	}

	// public synchronized NodeList getConceptsXml(){
	// return conceptsList;
	// }
	public synchronized String getConceptsjson() throws FileNotFoundException, Exception {
		conceptxml = (Document) xmljsontraverse.parse(ConceptsXML);
		root = (Element) conceptxml.getDocumentElement();
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.readFromFile(new File(ConceptsXML));
//		 XMLSerializer xmlSerializer = new XMLSerializer();
//	     JSON json = xmlSerializer.read(getConcepts(ConceptsXML));
//		JSON json = readFromStream(new FileInputStream(ConceptsXML));
//        return json.toString(2);
		String conceptjson = "{\"type\":\"conceptlist\",\n" + "\"data\":"+ json.toString()+"}";
//		System.out.print(conceptjson);
		return conceptjson;
		// System.out.println(json.toString());
	}
//	public static String xml2json(File xmlFile) throws Exception {
//	    
//        JSON json = readFromStream(new FileInputStream(xmlFile));
//        return json.toString(2);
//    }
	public synchronized String getConcepts(String filepath) {
		File file = new File(filepath);
		BufferedReader reader = null;
		String Concepts = "";
		try {
//			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
//			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				Concepts += tempString;
//				System.out.println("line " + line + ": " + tempString);
//				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return Concepts;
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
	public synchronized String getconceptjson(String xml) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xml);
		return json.toString();
	}

	/**
	 * 添加一条concept知识
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * 
	 * @throws Exception
	 */
	public synchronized void addConcept(String concept) throws ParserConfigurationException, SAXException, IOException {// 将xml字符串转为文档
		StringReader sr = new StringReader(concept); 
		InputSource is = new InputSource(sr); 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder=factory.newDocumentBuilder(); 
		Document xml = builder.parse(is);
		NodeList list1 = xml.getElementsByTagName("Conception");
		if(list1!=null){
			Element rel = (Element) list1.item(0);
			String conceptid = rel.getAttributes().getNamedItem("ID").getNodeValue();
			String conceptName = rel.getAttributes().getNamedItem("Name").getNodeValue();
			
			Element ele = conceptxml.createElement("Conception");
			ele.setAttribute("ID", conceptid);
			ele.setAttribute("Name", conceptName);
			Element property = conceptxml.createElement("property");
			for(Node node = rel.getFirstChild();node!=null;node=node.getNextSibling()){
				if (node.getNodeType() == Node.ELEMENT_NODE) {  
					if (node.getNodeName().equals("Performance")) {  
			           	 Node firstDocImportedNode = conceptxml.importNode(node, true);
			           	 property.appendChild(firstDocImportedNode );
//			                	 property.appendChild(node);
			        }
			        if (node.getNodeName().equals("Safety")) {  
			           	 Node firstDocImportedNode = conceptxml.importNode(node, true);
			           	 property.appendChild(firstDocImportedNode );
//			                	 property.appendChild(node);
			        }
			        if(node.getNodeName().equals("Interface")){
			           	 Node firstDocImportedNode = conceptxml.importNode(node, true);
			           	 property.appendChild(firstDocImportedNode );
//			                	 property.appendChild(node);
					
			        }
				}
			}
			ele.appendChild(property);
			root.appendChild(ele);
		}
		
////		Document xml = (Document) DocumentHelper.parseText(concept);// 便得到xml文档
//		Element xmlele = (Element) xml.getDocumentElement();
//		Node firstDocImportedNode = xml.importNode((Node)xmlele, true);
//		root.appendChild(firstDocImportedNode );
//		Element conception = conceptxml.createElement("C")
//		root.appendChild(xmlele);
		fillgraph();

		conceptstr = "{\"type\":\"addconcept\",\n" + "\"concept\":" + getconceptjson(concept)+"}";
		// return concept;
		System.out.println("addConception成功");
	}

	public synchronized String getconceptstr() {
		return this.conceptstr;
	}
	
	
	
	public synchronized void deleteConcept(String conceptindicate) {// 将concept的id和name传过来ID:Name
		String conceptid = conceptindicate.substring(0,
				conceptindicate.indexOf(":"));
		String conceptname = conceptindicate.substring(conceptindicate
				.indexOf(":") + 1);
		NodeList list = conceptxml.getElementsByTagName("Conception");
		int i;
		for (i = 0; i < list.getLength(); i++) {
			Element ele1 = (Element) list.item(i);
			if (ele1.getAttributes().getNamedItem("ID").getNodeValue()
					.equals(conceptid)
					&& ele1.getAttributes().getNamedItem("Name").getNodeValue()
							.equals(conceptname)) {// 存在此条知识
				ele1.getParentNode().removeChild(ele1);
				break;
			}
		}

		fillgraph();
		conceptstr = "{\"type\":\"deleteconcept\",\n" + "\"concept\":"  + conceptindicate+"}";
		// return conceptindicate;
		System.out.println("deleteConception成功");
	}

	public synchronized void updateConcept(String concept) throws ParserConfigurationException, SAXException, IOException {// 将concept的xml字符串传过来
//		Document xml = (Document) DocumentHelper.parseText(concept);// 便得到xml文档
		StringReader sr = new StringReader(concept); 
		InputSource is = new InputSource(sr); 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder=factory.newDocumentBuilder(); 
		Document xml = builder.parse(is);
		Element xmlele = (Element) xml.getDocumentElement();
		NodeList list1 = xml.getElementsByTagName("Conception");
		if (list1 != null) {
			Element rel = (Element) list1.item(0);
			String conceptid = rel.getAttributes().getNamedItem("ID")
					.getNodeValue();
			// String conceptName =
			// rel.getAttributes().getNamedItem("Name").getNodeValue();
			NodeList list = conceptxml.getElementsByTagName("Conception");
			int i;
			for (i = 0; i < list.getLength(); i++) {
				Element ele1 = (Element) list.item(i);
				if (ele1.getAttributes().getNamedItem("ID").getNodeValue()
						.equals(conceptid)) {// 存在此条知识
					ele1.getParentNode().removeChild(ele1);
					break;
				}
			}
			addConcept(concept);
//			root.appendChild(xmlele);
		}
		fillgraph();
		conceptstr = "{\"type\":\"updateconcept\",\n" + "\"concept\":" + getconceptjson(concept)+"}";
		// return concept;
		System.out.println("updateConception成功");
	}

	public synchronized void fillgraph() {
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
			// if (filename == null) {
			// // 设置标准输出流为transformer的底层输出目标
			// result.setOutputStream(System.out);
			// } else {
			result.setOutputStream(new FileOutputStream(ConceptsXML));
			transformer.transform(source, result);
			// }
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
