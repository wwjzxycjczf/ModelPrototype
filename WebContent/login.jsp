
<%@ page language="java" contentType="text/html;charset=utf-8"%>
<html>
 <head>
 <META HTTP-EQUIV= "pragma " CONTENT= "no-cache "> 
<META HTTP-EQUIV= "Cache-Control " CONTENT= "no-cache, must-revalidate "> 
<META HTTP-EQUIV= "expires " CONTENT= "Wed, 26 Feb 1997 08:21:57 GMT "> 
  <title></title>

<link rel="stylesheet" type="text/css" href="css/style.css" />
 </head>
 <body class="login">
 <div class="login1">
	<div class="login1_1">
  	<form method="POST" name="frmLogin" action="LoginServlet">
  	<input type="text" name="txtUserName" value=""  onblur="checkUsername()" class="loginT1" />
	<label class="logintip1" id="nametip"></label>
        <input type="password" name="txtPassword" value="" onblur="checkUserpwd()" class="loginT2" />
    	<label class="logintip2" id="pwdtip"></label>	
        <input type="submit" name="Submit" value="" class="loginS1" onClick="validateLogin();"  />

        <input type="reset" name="Reset" value="" class="loginS2" onClick = "resetlogin()"/>
   
</form>
</div>
</div>
 <script type="text/javascript">
 document.frmLogin.txtUserName.focus();
 function checkUsername(){
	 var sUserName = document.frmLogin.txtUserName.value;
	 if( sUserName=="" )
     {
		 document.getElementById("nametip").innerHTML="<span style='position:absolute;top:112px;left:348px;font-family:\"微软雅黑\"; font-size:12px;color:#f00;'>" + "用户名不能为空!" + "</span>";
 // 	   document.getElementById("nametip").innerHTML = "用户名不能为空"; 
//      alert("用户名不能为空");
    //  return false;
     }else{
    	 document.getElementById("nametip").innerHTML = "";
     }
 };
 function checkUserpwd(){
	 var sPassword = document.frmLogin.txtPassword.value;
	 if( sPassword=="" )
     {
		 document.getElementById("pwdtip").innerHTML="<span style='position:absolute;top:150px;left:348px;font-family:\"微软雅黑\"; font-size:12px;color:#f00;'>" + "密码不能为空" + "</span>";
  	//   document.getElementById("pwdtip").innerHTML = "密码不能为空";
   //   alert("密码不能为空");
     }else{
    	 document.getElementById("pwdtip").innerHTML = "";
     }
 }
      function validateLogin()
      {
       var sUserName = new String(document.frmLogin.txtUserName.value);
       var sPassword = new String(document.frmLogin.txtPassword.value);
       if( sUserName=="" )
       {
    	   document.getElementById("nametip").innerHTML="<span style='position:absolute;top:112px;left:348px;font-family:\"微软雅黑\"; font-size:12px;color:#f00;'>" + "用户名不能为空!" + "</span>";

//        alert("用户名不能为空");
        return false;
       }
       if( sPassword=="" )
       {
    	   document.getElementById("pwdtip").innerHTML="<span style='position:absolute;top:150px;left:348px;font-family:\"微软雅黑\"; font-size:12px;color:#f00;'>" + "密码不能为空" + "</span>";

     //   alert("密码不能为空");
        return false;
       }else if(sPassword.length<6){
    	   document.getElementById("pwdtip").innerHTML="<span style='position:absolute;top:150px;left:348px;font-family:\"微软雅黑\"; font-size:12px;color:#f00;'>" + "用户名或密码错误" + "</span>";

       }
      }
      function resetlogin(){
    	 document.frmLogin.txtUserName.value = "";
    	 document.frmLogin.txtPassword.value = "";
    	 document.getElementById("nametip").innerHTML = "";
    	 document.getElementById("pwdtip").innerHTML = "";
      }
     
     </script>
     
    
   </body>
</html>