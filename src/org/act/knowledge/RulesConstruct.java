package org.act.knowledge;

import java.io.File;
import java.io.FileWriter;

import org.act.server.XMLJsonTraverse;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
public class RulesConstruct {

	public void RulesConstruct(String rulexmlstr,String name) throws DocumentException{
	}
	/** 
	    * string2XmlFile 
	    * 将xml格式的字符串保存为本地文件，如果字符串格式不符合xml规则，则返回失败 
	    * @return true:保存成功  flase:失败 
	    * @param filename 保存的文件名 
	    * @param str 需要保存的字符串 
	    */ 
	 public static boolean string2XmlFile(String str,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	         Document doc =  DocumentHelper.parseText(str);        
	         flag = doc2XmlFile(doc,filename); 
	      }catch (Exception ex) 
	      { 
	         flag = false; 
	         ex.printStackTrace(); 
	      } 
	      return flag; 
	   }
	 /** 
	    * doc2XmlFile 
	    * 将Document对象保存为一个xml文件到本地 
	    * @return true:保存成功  flase:失败 
	    * @param filename 保存的文件名 
	    * @param document 需要保存的document对象 
	    */ 
	 public static boolean doc2XmlFile(Document document,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	            /* 将document中的内容写入文件中 */ 
	            //默认为UTF-8格式，指定为"GB2312" 
	            OutputFormat format = OutputFormat.createPrettyPrint(); 
	            format.setEncoding("GB2312"); 
	            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format); 
	            writer.write(document); 
	            writer.close();             
	        }catch(Exception ex) 
	        { 
	            flag = false; 
	            ex.printStackTrace(); 
	        } 
	        return flag;       
	   }
}
