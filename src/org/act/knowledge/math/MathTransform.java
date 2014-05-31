package org.act.knowledge.math;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.act.util.ExecuteCMD;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

import fmath.conversion.ConvertFromMathMLToLatex;

import net.sf.json.JSONObject;

public class MathTransform {
//	private static String mathfile = "D:\\Program Files\\apache-tomcat-7.0.32\\webapps\\ModelPrototype\\resources\\knowledge graph1\\rules\\6.xml";

	private static KernelLink ml = null;
//	 ExecuteCMD excmd = new ExecuteCMD();
//	 MathLink ml1 = null;
	 private static String jLinkDir = "D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\SystemFiles\\Links\\JLink";
	 
	public MathTransform() throws MathLinkException{
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
		
//		rulelist  = Collections.synchronizedList(new ArrayList<Rule>());
	}
//	private static JSONObject jsonobj;
	public void JsonstringToJson(String jsonString){
	 
		JSONObject jsonobj = JSONObject.fromObject(jsonString);
		ArrayList<String> mathnumarr = new ArrayList<String>();
		HashMap<String,Object> mathmap = new HashMap<String,Object>();
		ArrayList<String> codearr = new ArrayList<String>();
		String[] parameterarr;
		if(jsonobj.has("objectstr")){
			String objectstr = jsonobj.getString("objectstr");
			while(objectstr.indexOf('$')!=-1){
				String mathnum = objectstr.substring(objectstr.indexOf('$')+1,objectstr.indexOf(":["));
				mathnumarr.add(mathnum);
				objectstr = objectstr.substring(objectstr.indexOf(":[")+2);
			}
//			JSONObject objectstr = jsonobj.getJSONObject("objectstr");
			
		}
		if(jsonobj.has("ruleobject")){
			JSONObject ruleobject = jsonobj.getJSONObject("ruleobject");
			for(int i=0;i<mathnumarr.size();i++){  
				if(ruleobject.has(mathnumarr.get(i))){
					mathmap.put(mathnumarr.get(i), ruleobject.getJSONObject(mathnumarr.get(i)));
										
				}
			}
			
		}
		if(jsonobj.has("parameterstr")){
			String parameterstr = jsonobj.getString("parameterstr");
			parameterarr = parameterstr.split(";");//格式如matrix:3
			
		}

		 try { 
			 if(ml == null){
				 ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\MathKernel.exe'");
				 ml.discardAnswer();
			 }
		  Iterator iter = mathmap.keySet().iterator(); 
		  while (iter.hasNext()) { 
		      Object key = iter.next(); 
		      JSONObject val = (JSONObject) mathmap.get(key);
		      String mathstr="";
		      if(val.has("rulestr")){
		    	  mathstr = val.getString("rulestr");
		    	  System.out.println("mathstr:"+mathstr);
		    	  String latex = ConvertFromMathMLToLatex.convertToLatex(mathstr);
					System.out.println("latex:"+latex);
					latex.replaceAll(",", "");
					ml.putFunction("EnterTextPacket", 1);
					ml.put("CForm[ToExpression[\""+latex+"\", TeXForm]]");
					ml.waitForAnswer();
					String output = ml.getString();
					System.out.println(output);
					ml.discardAnswer();
		      }
		      
		  } 
		 } catch (MathLinkException e) { 
			  System.out.println("Fatal error opening link: " + 
			  e.getMessage()); 
			  return; 
			 }
			 
		
	}
	 
}
