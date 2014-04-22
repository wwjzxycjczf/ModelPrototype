package org.act.knowledge.element.users;

import java.net.URL;
import java.sql.* ;
import java.util.Properties;
import java.io.* ;

import javax.servlet.http.* ;
import javax.servlet.* ;
public class LoginServlet extends HttpServlet implements Servlet{
	private static String jDriver="";
	private static String dbURL="";
	private static String userName="";
	private static String userPwd="";
 public LoginServlet(){
 }
  
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
  
 }

 @SuppressWarnings("finally")
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
  response.setContentType("text/html;charset=utf-8") ;
  request.setCharacterEncoding("utf-8") ;
  //获取用户名
  String username = request.getParameter("txtUserName") ;
  String psw = request.getParameter("txtPassword") ;
  HttpSession session =  request.getSession() ;
  session.setAttribute("username", username);

  InputStream in = getClass().getResourceAsStream("/dbinfo.properties");
  Properties prop = new Properties();
  try{ 
	  prop.load(in);
  }
  catch(Exception e)
  {
   System.out.print(e.getMessage());
   response.sendRedirect("login.jsp") ;
   return ;
  }
  if(prop.isEmpty()) 
  {
	  response.sendRedirect("login.jsp") ;
   return; 
  }
  jDriver = prop.getProperty("jDriver"); 
  dbURL = prop.getProperty("dbURL");
  userName = prop.getProperty("userName");
  userPwd = prop.getProperty("userPwd");
  Connection conn=null;
	Statement stmt =null;
  try{
		
		Class.forName(jDriver);
		conn = DriverManager.getConnection(dbURL, userName, userPwd);  
		stmt=conn.createStatement();// 执行SQL语句 
		String sql = "select * from userinfo where username='" + username
			      + "' and password = '" + psw + "'";//查询语句
		  ResultSet rs = stmt.executeQuery(sql);//结果集
		  if(rs.next()) {//从第一条往后依次取结果集中的记录
		    System.out.println ( "登录成功！" );
			   response.sendRedirect("edition4.jsp") ;
			   
		  }else{
		  System.out.println ( "用户名不存在或密码错误！" );
			    	session.setAttribute("message", "用户名不存在或密码错误！");
			    	response.sendRedirect("fail.jsp") ;
			    	
		  }
		  rs.close();//释放资源
		  stmt.close();
		  conn.close();
		  return ;
		}
		catch(Exception e) {
		  System.out.print("获得数据错误!");
		  response.sendRedirect("fail.jsp") ;
	//	 e.printStackTrace();
		 return ;
		  
  }  finally{ 
	   try { 
		      if(stmt != null) 
		    	  stmt.close(); 
		      if(conn!=null && !conn.isClosed()) 
		    	  conn.close(); 
		   }catch (final SQLException e) { 
		        e.printStackTrace(); 
		   } 
	   return ;
} 
 
 }
  
 private static final long serialVersionUID = 1L;
}