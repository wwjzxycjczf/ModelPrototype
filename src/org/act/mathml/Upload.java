package org.act.mathml;

//import java.io.*;  
//import java.util.*;  
//
//import javax.servlet.*;  
//import javax.servlet.http.*;  
//
//import org.apache.commons.fileupload.DiskFileUpload;
//import org.apache.tomcat.util.http.fileupload.*;
//import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
//import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

//public class Upload extends HttpServlet {  
//	 
//    private String uploadPath = "C:\\upload\\"; // 用于存放上传文件的目录  
//    private String tempPath = "C:\\upload\\tmp\\"; // 用于存放临时文件的目录  
// 
//public void doPost(HttpServletRequest request, HttpServletResponse response)  
//    throws IOException, ServletException  
//{  
//    try {  
////        DiskFileUpload fu = new DiskFileUpload();  
//    	DiskFileUpload fu = new DiskFileUpload();  
//        // 设置最大文件尺寸，这里是4MB  
//        fu.setSizeMax(4194304);  
//        // 设置缓冲区大小，这里是4kb  
//        fu.setSizeThreshold(4096);  
//        // 设置临时目录：  
//        fu.setRepositoryPath(tempPath);  
// 
//        // 得到所有的文件：  
//        List fileItems = fu.parseRequest(request);  
//        Iterator i = fileItems.iterator();  
//        // 依次处理每一个文件：  
//        while(i.hasNext()) {  
//            FileItem fi = (FileItem)i.next();  
//            // 获得文件名，这个文件名包括路径：  
//            String fifileName = fi.getName();  
//            if(fifileName!=null) {  
//                // 在这里可以记录用户和文件信息  
//                // ...  
//                // 写入文件a.txt，你也可以从fileName中提取文件名：  
//                fi.write(new File(uploadPath + "a.txt"));  
//            }  
//        }  
//        // 跳转到上传成功提示页面  
//    }  
//    catch(Exception e) {  
//        // 可以跳转出错页面  
//    }  
//}  
//} 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

import fmath.conversion.ConvertFromMathMLToLatex;
import fmath.conversion.ConvertFromWordToMathML;



/**
* Servlet implementation class for Servlet: UploadServlet
*
*/
public class Upload extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
File tmpDir = null;//初始化上传文件的临时存放目录
File saveDir = null;//初始化上传文件后的保存目录
public Upload() {
super();
} 

protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
doPost(request,response); 
} 

protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try{
//	String fileName= "";  
//	String mathml = "";
		HttpSession session = request.getSession();
		
		if(ServletFileUpload.isMultipartContent(request)){
			DiskFileItemFactory dff = new DiskFileItemFactory();//创建该对象
			dff.setRepository(tmpDir);//指定上传文件的临时目录
			dff.setSizeThreshold(1024000);//指定在内存中缓存数据大小,单位为byte
			ServletFileUpload sfu = new ServletFileUpload(dff);//创建该对象
			sfu.setFileSizeMax(5000000);//指定单个上传文件的最大尺寸   
			sfu.setSizeMax(10000000);//指定一次上传多个文件的总尺寸
			FileItemIterator fii = sfu.getItemIterator(request);//解析request 请求,并返回FileItemIterator集合
//			while(fii.hasNext()){   
				FileItemStream fis = fii.next();//从集合中获得一个文件流
				if(!fis.isFormField() && fis.getName().length()>0){//过滤掉表单中非文件域
					String fileName = fis.getName().substring(fis.getName().lastIndexOf("\\")+1);//获得上传文件的文件名
					String mathml = ConvertFromWordToMathML.getMathMLFromDocStream(fis.openStream(), "ISO-8859-1");
					session.setAttribute("mathml", mathml);
					session.setAttribute("filename", fileName);
					String latex = ConvertFromMathMLToLatex.convertToLatex(mathml);
					System.out.print("latex:"+latex);
					PrintStream pstream = new PrintStream(new FileOutputStream(saveDir+"5.xml"));  
					pstream.println(mathml);// 往文件里写入字符串  
					
					
	
					PrintWriter writer = response.getWriter(); 
				
					writer.print("{");  
					writer.print("\"content\":\""+mathml+"\",\"file\":\""+fileName+"\"");  
					writer.print("}");   
					writer.close();  
	//BufferedInputStream in = new BufferedInputStream(fis.openStream());//获得文件输入流
	//BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(saveDir+fileName)));//获得文件输出流
	//Streams.copy(mathml, out, true);//开始把文件写到你指定的上传文件夹
				}
			}
	//success = "File upload successfully!!!";
	
	  
	
	//System.out.println("成功");
	//response.sendRedirect("frameset.jsp");
	//response.getWriter().println("File upload successfully!!!");//终于成功了,还不到你的上传文件中看看,你要的东西都到齐了吗
//		}
	}catch(Exception e){
		e.printStackTrace();																			
	}
} 

public void init() throws ServletException {
/* 对上传文件夹和临时文件夹进行初始化
*
*/
super.init();
String tmpPath = "d:\\tmpdir";
String savePath = "d:\\updir";
tmpDir = new File(tmpPath);
saveDir = new File(savePath);
if(!tmpDir.isDirectory())
tmpDir.mkdir();
if(!saveDir.isDirectory())
saveDir.mkdir();


} 
}