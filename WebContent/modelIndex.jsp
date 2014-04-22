<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8" isELIgnored="false"%>
    <%@ page language="java"   %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>系统需求建模</title>
<link rel="stylesheet" type="text/css" href="css/normal.css"/>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/layout.css" />
<script type="text/javascript"  src="websocket.js"></script>

</head>
<body>
<!--<script language="JavaScript">
<!--

function ok(){
var s = document.getElementById("file");
s.click();
var str = s.value;//获取文件路径
alert(str);//在这里你可以进行自己的工作
}
//
</script>
</html>-->
 
<div class="header" id="banner" >
	<div class="headerFont1"><img src="images/font_01.png" alt="国家国际科技合作专项资助2012DFB10200"/></div>
	<div class="logo"><img src="images/logo.png" alt="雷达组网资源管理平台"/></div>
	<div id="loginstate" style="filter:alpha(opacity=100);color:#FFF;font-family:微软雅黑; font-size:14px;z-index:9999;top:80px;left:800px;display:block;position:absolute;width:400px;"></div>
	<div class="nav">
		<div class="headerFont2"><img src="images/font_02.png" alt="上海航天电子通讯设备研究所&北京航空航天大学联合研制"/></div>
		
		
 	   <form method = "POST" name="openin" action="OpenTask1"  >
 	    <input type="hide"  name="filename" value="" />
 	   </form>
	  <input type="file" id="f">

	  
	  
	  
	     
<!-- 	    <ul class="navUl">
<input type="button" value="button" onclick="btnClick()">
<input type="button" id="file"　 value="click"    onClick='Openfile()'/>
<input type="file" id="file" onchange="handleFiles(this.files)"/>
 <input type="button" id="file"　value="click"    onClick='Openfile()'/>
	    	<li><a href="#" name="tt" class="nav1" onclick="getFileName()">打开文件</a></li>
	    </ul> -->
	</div>
</div>
<script type="text/javascript">
<%
String userName = (String)session.getAttribute("filename") ;
if(userName==null){
	
//	response.sendRedirect("login.jsp");
//	response.setHeader("Refresh","0;URL=/MapGIS/login.jsp");  
}else{
%>
var loginstate = document.getElementById("loginstate");
loginstate.innerHTML = "<%=userName %>"+"登陆成功！";
<%
}
%>
function handleFiles(files) {
	var loginstate = document.getElementById("loginstate");
	
    if (files.length) {
        var file = files[0];
        var reader = new FileReader();
   //     if (/text/w+/.test(file.type)) {
          reader.onload = function() {
     //       $('<pre>' + this.result + '</pre>').appendTo('body');
     //  }
        	  loginstate.innerHTML = this.result;
        }
        	  reader.readAsText(file);
     //}
    }
}
function btnClick()
{
	var s = document.getElementById("f");
	s.click();
	var str = s.value;//获取文件路径
	document.openin.filename.value = s.value;
}
</script>
</body>
</html> 