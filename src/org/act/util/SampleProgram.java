package org.act.util;
import com.wolfram.jlink.*;

public class SampleProgram {

	public static void main(String[] args) throws InterruptedException {

		 KernelLink ml = null;
		 ExecuteCMD excmd = new ExecuteCMD();
		 MathLink ml1 = null;
		 String jLinkDir = "D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\SystemFiles\\Links\\JLink";
		 System.setProperty("com.wolfram.jlink.libdir", jLinkDir);

		 try { 
		  ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\MathKernel.exe'");
		  ml.discardAnswer();
		  ml.putFunction("EnterTextPacket", 1);
			ml.put("CForm[ToExpression[Import[\"d:\\1.mml\",\"MathML\"]]]");
			  ml.waitForAnswer();
			  String output = ml.getString();
			  System.out.println(output);
//		  excmd.setLink(ml);
//		  excmd.setCmd("CForm[ToExpression[Import[\"d:\\1.mml\",\"MathML\"]]]");
//		  excmd.excute();
		  
		//  String expr = "Sum[k^2,{k,1,11}]";
//		  String mathml = "<math xmlns='http://www.w3.org/1998/Math/MathML'><msup>	<mi>X</mi>	<mn>2</mn></msup></math>";
//		 String cmd =  "CForm[ToExpression[Import[\"d:\\1.mml\",\"MathML\"]]]";
////		  ml.evaluateToInputForm(,1);
////		  ml.
//		  String output1 = ml.evaluateToOutputForm("Integrate[5 x^n a^x, x]", 0);
//		  String str = "M= √(2&2 ((1+Q_c/P_s )^((K-1)/K)-1)/(K-1))  ,0.7<Q_c/P_s ≤0.89,K=1.4";
////		  String latex = "\\left.\\frac{x^3}{3}\\right|_0^1";
////		  ml1.putData(mathml.getBytes());
//////		  ml1.wait();
////		  String xx = ml1.getString();
////String x1 =   ml.evaluateToInputForm(mathml, 1);
//		  ml.evaluate(cmd);
//		  ml.waitForAnswer();
//		  String output = ml.getString();
//		  System.out.println("Result = " + output);
//		  String expr = "UsingFrontEnd[nb=NotebookPut[Notebook[{Cell[\"Graphics3D[Cuboid[]]\", \"Input\"]}]]]";
//          System.out.println("Result = " + ml.evaluateToOutputForm(expr, 40) );
//          expr = "UsingFrontEnd[NotebookSave[nb,\"TERRANOVA1\"]]";
//          System.out.println("Result = " + ml.evaluateToOutputForm(expr, 40) );
//
//
		 } catch (MathLinkException e) { 
		  System.out.println("Fatal error opening link: " + 
		  e.getMessage()); 
		  return; 
		 }
		 
//		 
//		 KernelLink ml = null;
//		 String jLinkDir = "D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\SystemFiles\\Links\\JLink";
//		 System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
//
//		 try { 
//		  ml = MathLinkFactory.createKernelLink("-linkmode launch -linkname 'D:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\MathKernel.exe'");
//		  ml.discardAnswer();
//		  ml.putFunction("EnterTextPacket", 1);
//			ml.put("CForm[ToExpression[Import[\""+saveDir+"5.xml\",\"MathML\"]]]");
//			  ml.waitForAnswer();
//			  String output = ml.getString();
//			  System.out.println(output);
//		 } catch (MathLinkException e) { 
//			  System.out.println("Fatal error opening link: " + 
//			  e.getMessage()); 
//			  return; 
//		 }
		}
		}