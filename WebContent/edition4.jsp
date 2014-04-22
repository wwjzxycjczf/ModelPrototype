<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8" isELIgnored="false"%>
    <%@ page language="java"   %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>  
<META HTTP-EQUIV= "pragma " CONTENT= "no-cache "> 
<META HTTP-EQUIV= "Cache-Control " CONTENT= "no-cache, must-revalidate "> 
<META HTTP-EQUIV= "expires " CONTENT= "Wed, 26 Feb 1997 08:21:57 GMT "> 
<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>   
    </title>
    
<link rel="stylesheet" type="text/css" href="style.css" />
<link rel="stylesheet" type="text/css" href="layout.css" />

<style>


#chart {
    height: 500px;
  }
 
  .node rect {
    cursor: move;
    fill-opacity: .9;
    shape-rendering: crispEdges;
  }
 .node circle {
  stroke-width: 1.5px;
  cursor: pointer;
}
.node.reflexive.circle{
stroke: #000 !important;
  stroke-width: 2.5px;
  fill:red;
}
  .node text {
    pointer-events: none;
    text-shadow: 0 1px 0 #fff;
  }
 
  .link {
    fill: none;
    stroke: #000;
   stroke-opacity: .5;
    fill-opacity: .2;
  }
 
  .link:hover {
    fill-opacity: .5;
  }
</style>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
 <script src="http://d3js.org/d3.v2.js"></script> 
   <!--<script src="d3.js"></script>
    <script src="d3.geom.js"></script> 
      <script src="d3.layout.js"></script>  --> 
      <script type="text/javascript"  src="ajaxfileupload.js"></script>
      
      <script src="sankey.js"></script>
<script type="text/javascript"  src="drawsvgnew2.js"></script> 


  
</head>  
<body onload="onLoad()">
 <div id="DocFormulaUpload" style="background: url(images/docloadbkg.png);left:400px;top:200px;position:absolute;border:1px solid black;width:512px;height:240px;display:none;">
	<img id="loading" src="images/loading.gif" style="display: none;"> </img>
	<form name="upform" action="" method="post" enctype="multipart/form-data">
<input type ="file" name="file3" id="file3"/><br/>
<input type="button" value="Submit" onclick ="docsubmit()" /><br/>
<input type='button'  value='取消' onclick="QuitDocRule()"/> 
</form>
</div>
<div id="chatbox" class="chat" style="display:none">
        <div id='Chatlog' class='Chatcontainer'></div>
        <br />
        <div id='SendDataContainer' class="Senddatacontainer">
        <input type="text" id="DataToSend" size="63" />
        <br />
       
        <button id='SendData' type="button" style="margin-top:4px;margin-left:300px;" onclick='SendDataClicked();'>发送</button>
        <button id='closebox' type="button" style="margin-top:4px;margin-left:20px;" onclick='CloseDataClicked();'>关闭</button>
        </div>
       
       
</div>
     <div id="mainframe">
     <div>
    <form id="form1">  
        <div>   
         	<input type='button' class='btn' value='Input knowledge' /> 
        	<input type="file" name="fileField" class="file1" id="fknowledge" size="28" /> 
        	<input type='button' class='btn' value='Input formula' /> 
        	<input type="file" name="fileField" class="file2" id="fmathml" size="28" />   
        	<input type='button' class='btn1' value='Input system requirements' /> 
        	<input type="file" id="files" name="files[]" class="file3" multiple /> 
        	<input type='button' class='btn2' value='Input Doc formula' onClick="DocFormulaClick()" /> 
        	
        </div>  
    </form>
</div>
<div class="bodyerTab1">
	<div class="bodyerTab1_1"  id = "listpanel">
	
    	<div class="news mb10">    	
            <h3 class="title mb10">Domain Knowledge </h3>
            
            <div id="knowledgelist" style="width:275px;background:#F4F1F0;height:800px;font-family:å¾®è½¯éé»; font-size:14px;margin:0 0;overflow:auto;">
			<!-- 	<ul id="s">
				</ul> -->
			<!--	 <div id="list1" style="width:260px; border: 1px solid #eee;height:800px;overflow: auto;"><!-- å­æ¾ç¥è¯åè¡¨ 
					<table cellpadding="0" cellspacing="0" class="tablemenu" id="table1"></table>
				</div>--> 
			</div>
		</div>
    </div>
    <div>
	    <div style="width:1100px;height:50px;position:absolute;border: 1px solid #eee;margin-left:0px;left:280px;top:0px;overflow:auto;border:1px">
	    <label class="Search" id="nametip">Knowledge keywords:</label>
	        <input type="text" id="knowledgeid" value="" class="Searchknowledge"  />
	    <input type="submit" name="Submit" value="Search"  style="position:absolute;top:0px;left:300px;" onclick="SearchKnowledge();"  />
	 <input type="submit" name="Submit" id="back" value="Back"  style="position:absolute;top:0px;left:400px;display:none;" onclick="BackMainKnowledge();"  />
	 
	<div id="loginstate" style="font-family:微软雅黑; font-size:14px;top:0px;left:600px;display:none;position:absolute;width:100px;"></div>
	
	 <input type="submit" name="Submit" value="login out"  style="position:absolute;top:0px;left:800px;" onclick="loginout();"  />
	 <input type="submit" name="Submit" value="发起聊天"  style="position:absolute;top:0px;left:1000px;" onclick="chat();"  />
	    </div>
	   	    
	  <!--   <div id="math" style=" width:600px;height:600px;position:absolute;border: 1px solid #eee;margin-left:310px;top:40px;overflow:auto;display:none;"></div>
	    -->
	     <div id="mathmldiv"  style="width:1050px;height:800px; position:absolute;border: 1px solid #eee;margin-left:0px;left:280px;top:40px;overflow:auto;">
	     	<div id="math" style="width:1048px;height:630px;position:absolute;border: 1px solid #eee;margin-left:0px;top:0px;overflow:auto;display:none;">
	     		<div id="mathcontent" style=" width:1046px;height:500px;position:absolute;border: 1px solid #eee;margin-left:0px;top:0px;overflow:auto;"></div>
	     		<input type='button' style="background-color:#FFF; filter:alpha(opacity:1);border:1px solid #CDCDCD;height:24px;margin-left:750px;margin-top:530px; width:70px;" value='保存' onclick="AddRule()"/> 
	     		<input type='button' style="background-color:#FFF; filter:alpha(opacity:1);border:1px solid #CDCDCD;height:24px;margin-left:50px;margin-top:0px;width:70px;" value='取消'  onclick="QuitRule()"/> 
	     	</div>
	     	<div id="rulesave" style="width:450px;height:100px;position:absolute;border: 1px solid #eee;margin-left:200px;top:200px;overflow:auto;display:none;">
	     		 <label style="position:absolute;top:15px;left:40px;font-family:微软雅黑; font-size:12px;color:#f00;" id="filelabel">文件名</label>
	        	<input type="text" id="rulefilename" value="" style="width:200px; height:24px;border: 1px solid #eee; position:absolute; top:15px; left:100px;  color:#666;"  />
	        	<input type='button' style="background-color:#FFF; filter:alpha(opacity:1);border:1px solid #CDCDCD;height:24px;margin-left:100px;margin-top:70px; width:50px;" value='保存' onclick="RealAddRule()"/> 
	     		<input type='button' style="background-color:#FFF; filter:alpha(opacity:1);border:1px solid #CDCDCD;height:24px;margin-left:50px;margin-top:0px;width:50px;" value='取消' onclick="RealQuitRule()"/> 
	     	</div>
	     		
	     <!-- 	 <div id="svgg" style="width:900px;height:500px,border-style:solid;border-width:2px;margin-left:0px;top:0px;overflow:auto;display:block;"/>
	     		-->
	     	<!-- 	   <svg xmlns="http://www.w3.org/2000/svg" version="1.1"  id="svg1" width="900px" height="500px"  style="border-style:solid;border-width:2px;margin-left:0px;top:0px;overflow:auto;display:block;" ></svg>
	     		 -->
	     	<div id="Attributes"   style="width:1045px;height:147px;position:absolute;border: 1px solid #eee;margin-left:0px;top:650px;overflow:auto;">
		    <div id="conceptproperty" style="display:block">
		    <img src="images/property.png" style="left:250px;top:0px;position:absolute;"/>
			 
			  	
			  	<input type="text" name="id" id ="id" value=""   class="p1" />
			  	<input type="text" name="name" id="name" value=""   class="p2" />
			  	<input type="text" name="accuracy" id="Accuracy" value=""  class="p3" />
				<input type="text" name="refreshrate" id="RefreshRate" value=""  class="p4" />		
				<input type="text" name="Range" id="Range" value=""  class="p5" />		
				<input type="text" name="MaxTransDelay" id="MaxTransDelay" value=""  class="p6" />		
				<input type="text" name="SigBits" id="SigBits" value=""  class="p7" />		
				<input type="text" name="Redundancy" id="Redundancy" value=""  class="p8" />		
				<input type="text" name="Interface" id="Interface" value=""  class="p9" />		
				<input type="text" name="Description" id="Description" value=""  class="p10" />				
			</div>	
			<div id="mathproperty" style="display:none">
	
    		</div>
    		
		
		    </div>
		 </div>
    </div>
</div>
<div class="bodyerTab2">
  	<div class="bodyerTab1_1">
	   	<div class="news mb10">
	   	<h3 class="title mb10">User List </h3>
        		<div id="userlist" style="width:240px;background:#F4F1F0;height:200px;font-family:微软雅黑; font-size:14px;margin:0 0;overflow:auto;">
				</div>
    	    
		</div>
		<div class="xz">
        	<h3 class="title mb10">Latest News </h3>
        	<div id="eventlist" style="width:275px;background:#F4F1F0;height:510px;font-family:微软雅黑; font-size:14px;margin:0 0;overflow:auto;">
			</div>
            	
         </div>
	</div>    
    </div>

</div> 

<!-- <div id="content" style="width:600px;height:200px;position:absolute;border: 1px solid #eee;left:310px;margin-left:0px;top:600px;overflow:auto;"></div>
-->
<script type="text/javascript"  src="edition4requirement.js"></script> 
  <script type="text/javascript"  src="edition4.js"></script> 
   <script type="text/javascript">
   function onLoad(){
	   <%
	   String userName = (String)session.getAttribute("username") ;
	   if(userName==null){
	   	response.sendRedirect("login.jsp");
//	   	response.setHeader("Refresh","0;URL=/MapGIS/login.jsp");  
	   }else{
	   %>
	   username = "<%=userName %>";
	   var loginstate = document.getElementById("loginstate");
	   loginstate.style.display = "block";
	//   loginstate.value = "<%=userName %>"+"登陆成功！";
	   loginstate.innerHTML = "<%=userName %>"+"登陆成功！";
	   <%
	   }
	   %>
	   <%
	   String mathml = (String) session.getAttribute("mathml");
	   	String filename = (String) session.getAttribute("filename");
	   if(mathml!=null&&filename!=null){
	   %>
	   //document.getElementById("mainframe").style.display="block";

	   <%
	   }
	   %>
	   //document.getElementById("math").innerHTML = "ok";
	   ConceptList = stringToJson(ConceptList);
	   RelationList = stringToJson(RelationList);
	   RuleList = stringToJson(RuleList);
	   Connection();
	      }
	     
    </script>
</body>  
</html>  