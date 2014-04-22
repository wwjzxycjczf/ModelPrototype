package org.act.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.json.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
public class XMLJsonTraverse {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 

	 public  static Document JsonToXML(String jsonString) throws ParserConfigurationException, SAXException, IOException {
		  JSONObject jsonObj = JSONObject.fromObject(jsonString);
		  XMLSerializer xmlSerializer = new XMLSerializer();
		  String xmlObject = xmlSerializer.write(jsonObj);
		 
			  StringReader sr = new StringReader(xmlObject); 
				InputSource is = new InputSource(sr); 
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder builder=factory.newDocumentBuilder(); 
				Document document = builder.parse(is);
				System.out.println(xmlObject);
//		   Document document = DocumentHelper.parseText(xmlObject);//便得到xml文档
		   return document;
//		   XMLWriter writer = new XMLWriter(new FileWriter(
//		     "../json/src/demo2.xml"));
//		   writer.write(document);
//		   writer.close();
		 
		  
//		return null;
		 }
	 public static Document ToXML(String xml) throws ParserConfigurationException, SAXException, IOException {//将xml字符串转为文档
		 StringReader sr = new StringReader(xml); 
			InputSource is = new InputSource(sr); 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder=factory.newDocumentBuilder(); 
			Document document = builder.parse(is);
//		 Document document = DocumentHelper.parseText(xml);//便得到xml文档
		   return document;
	 }
	 public void XMLToJson(String filename) {
		  XMLSerializer xmlSerializer = new XMLSerializer();
		  JSON json = xmlSerializer.readFromFile(new File(filename));
		  System.out.println(json.toString());
		 }
	
	 public Document parse(String filePath) { 
	      Document document = null; 
	      try { 
	         //DOM parser instance 
	         DocumentBuilder builder = builderFactory.newDocumentBuilder(); 
	         //parse an XML file into a DOM tree 
	         document = (Document) builder.parse(new File(filePath)); 
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
		 XMLJsonTraverse xjt = new XMLJsonTraverse();
		 xjt.XMLToJson("resources/knowledge graph1/concepts/PitchServoCommand.xml");
	 }
}
