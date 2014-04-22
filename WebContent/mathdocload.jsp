<%@ page contentType="application/msword;charset=gb2312" %>
<%@ page language="java"   %>
<%@ page language="java" import="javax.servlet.*,java.io.File,java.io.FileInputStream,java.io.OutputStream, fmath.conversion.*;" pageEncoding="UTF-8"%>
<%
String mathml = ConvertFromWordToMathML.getMathMLFromDocFile("ddd");
%>
<head>  
<META HTTP-EQUIV= "pragma " CONTENT= "no-cache "> 
<META HTTP-EQUIV= "Cache-Control " CONTENT= "no-cache, must-revalidate "> 
<META HTTP-EQUIV= "expires " CONTENT= "Wed, 26 Feb 1997 08:21:57 GMT "> 
<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>   
    </title>
    
<link rel="stylesheet" type="text/css" href="style.css" />
<link rel="stylesheet" type="text/css" href="layout.css" />
<script type="text/javascript"  src="websocket.js"></script>
<script type="text/javascript"  src="websocketrequirement.js"></script>

<script type="text/javascript"  src="websocketwebpage.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>

</head>  
<body onload="onLoad()">
<form action="" method="post" enctype="multipart/form-data">
 <input type='text' name='textfield' id='textfield' class='txt' />  
 <input type='button' class='btn' value='浏览...' />
    <input type="file" name="fileField" class="file" id="fileField" size="28" onchange="document.getElementById('textfield').value=this.value" />
 <input type="submit" name="submit" class="btn" value="上传" />
  </form>
<div>

   
</div>
<script>
function onLoad(){
	document.getElementById('fknowledge').addEventListener('change', readknowledge, false);

}
function readknowledge(evt){
    var f = evt.target.files[0];
    <% 
String path = "";
    path = request.getParameter("textfield");
    //String mathml = ConvertFromWordToMathML.getMathMLFromDocFile(path);
    File file=new File(path);
    FileInputStream fin=new FileInputStream(file);
    OutputStream output=response.getOutputStream();
    byte[] buf=new byte[1024];
    int r=0;
    response.setContentType("application/msword;charset=GB2312");
    while((r=fin.read(buf,0,buf.length))!=-1)
    {
    output.write(buf,0,r);//response.getOutputStream()
    }
    fin.close();
    output.close();
    %>
//    var content = document.getElementById("content");
    if (f) {
      var r = new FileReader();
      r.onload = function(e) {
          var contents = e.target.result;
          alert(contents);
      }
    }
    r.readAsText(f);
}
</script> 
</body>  
</html>  