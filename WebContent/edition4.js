 ConceptList = "{}";
 RelationList = "{}";
 RuleList = "{}";
 GraphJson = "{}";
 
 Conceptcontent="";
 Relationcontent="";
 knowledgewidth=200;
 knowledgeheight=50;
 ConceptGraphobj="{}";
 conceptobj = "{}";
 rulename  = "";
 rulecontent = "";
 
 username = "";
 userlist = [];
 realnodes = [];
 reallinks = [];
 dObject = null;
 initinputnum=0;
 FZObject = null;
 
 var ws;
 var SocketCreated = false;
 
 var Drawsvg;
 
 function loginClicked(){
	 if(checkUserName()){
		 if(checkUserPass()){
			 
			 Connection();
		 }
	 }
 }
 
 function checkUserName(){
	 var name = document.getElementById("txtName").value;
     var o = document.getElementById("t_name");
     o.innerHTML = "";
     //用户名
     if (name.length == 0) {
         o.innerHTML = "* 用户名不能为空";
         return false;
     }
     if (name.length <1) {
         o.innerHTML = "* 用户名长度不能小于1位";
         return false;
     }
     if (name.length > 12) {
         o.innerHTML = "* 用户名长度不能超过12位";
         return false;
     }
     if (name.indexOf("'") >= 0 || name.indexOf("‘") >= 0) {
         o.innerHTML = "* 用户名中存在非法字符";
         return false;
     }
     else {
    	 username = name;
         return true;
     }
 }
 function checkUserPass(){
	 var pass = document.getElementById("pwd").value;
     var o = document.getElementById("t_pass");
     o.innerHTML = "";
     if (pass.length == 0) {
         o.innerHTML = "* 密码不能为空";
         return false;
     }
     if (pass.length > 12) {
         ao.innerHTML = "* 密码不能超过12位";
         return false;
     }
     if (pass.indexOf("'") >= 0 || pass.indexOf("‘") >= 0) {
         o.innerHTML = "* 密码不能包含非法字符";
         return false;
     } else {
         return true;
     }
 }

function Log(Text, MessageType) {
    if (MessageType == "OK") Text = "<span style='color: green;'>" + Text + "</span>";
    else if (MessageType == "ERROR") Text = "<span style='color: red;'>" + Text + "</span>";
    document.getElementById("eventlist").innerHTML = document.getElementById("eventlist").innerHTML + Text + "<br />";
    var eventlist = document.getElementById("eventlist");
    eventlist.scrollTop = eventlist.scrollHeight;
};
function userLog(Text, MessageType) {
    if (MessageType == "OK") Text = "<span style='color: green;'>" + Text + "</span>";
    else if (MessageType == "ERROR") Text = "<span style='color: red;'>" + Text + "</span>";
    document.getElementById("userlist").innerHTML =document.getElementById("userlist").innerHTML+ Text + "<br />";
    var userlist = document.getElementById("userlist");
    userlist.scrollTop = userlist.scrollHeight;
};
function jsonToString(json) {
    return JSON.stringify(json);
};
function stringToJson(str) {
	var sss = str;
    try {
    	
//    	str = str.replace(/\'/g, "\"");
        return JSON.parse(str);
    } catch (error) {
        Log("error:"+error+sss);
    };
};
function Connection(){
	if (SocketCreated) {
        SocketCreated = false;
        ws.close();
    } else {
        try {
            if ("WebSocket" in window) {
            	ws = new WebSocket("ws://192.168.2.28:5001/websocket");
            } else if ("MozWebSocket" in window) {
            	ws = new MozWebSocket("ws://192.168.2.28:5001/websocket");
            }
            SocketCreated = true;
        } catch (ex) {
            Log(ex, "ERROR");
            return;
        }

        ws.onopen = WSonOpen;
        ws.onmessage = WSonMessage;
        ws.onclose = WSonClose;
        ws.onerror = WSonError;
        document.getElementById('fknowledge').addEventListener('change', readknowledge, false);
       	document.getElementById('files').addEventListener('change', readsvgrequirementfolder, false);
       	document.getElementById('fmathml').addEventListener('change', readknowledge, false);
    }
};
function Send(message) {
    if (!window.WebSocket) { return; }
    if (ws.readyState == ws.OPEN) {
        ws.send(message);
    } else {
        alert("The socket is not open.");
    }
};
function WSonClose() {
    Log("close", "ERROR");
};
function WSonError() {
    Log("远程连接中断", "ERROR");
};
function WSonOpen(){
	Send(username+":login");
	Log("Connection has been established", "OK");
	Send("request_concept_list");
	Send("request_relation_list");
	Send("request_rule_list");
}
function WSonMessage(event) {
	
    var data = event.data;
    if (!data) {
        return;
    }
    data = stringToJson(data);
    if (!data) {
        return;
    }
    var type = data.type;
    if (!type) {
        return;
    }
    if(type == "user"){
    	userlist = data.name.split(";");
    	for(var i=0;i<userlist.length;i++){
    		userLog(userlist[i],"OK");
    	}
    	return;
    }
    if(type=="singleuser"){
    	if(userlist==[]){
    		userlist.push(data.name);
    		userLog(data.name,"OK");
    	}else if(data.name!=username){
    		userlist.push(data.name);
    		userLog(data.name,"OK");
    	}
    	Log(data.name+" login","OK");
    	return;
    }
    if(type=="singleuserout"){
    	for(var i=0;i<userlist.length;i++){
    		if(userlist[i]==data.name){
    			userlist.splice(i, 1);
    		}
    	}
    		document.getElementById("userlist").innerHTML = "";
    		for(var i=0;i<userlist.length;i++){
        		userLog(userlist[i],"OK");
        	}
    	Log(data.name+" logout","OK");
    	return;
    }
    if(type=="chat"){
    	document.getElementById("Chatlog").innerHTML = document.getElementById("Chatlog").innerHTML + data.info + "<br />";
        var Chatlog = document.getElementById("Chatlog");
        Chatlog.scrollTop = Chatlog.scrollHeight;
    	return;
    }
    if (type == "relationlist") {
        LoadDatas(data,1);
        return;
    }
    if(type == "conceptlist"){
    	LoadDatas(data,2);
    	return;
    }
    if(type == "rulelist"){
    	LoadDatas(data,3);
    	return;
    }
    
    if(type=="addconcept"){//"{\"type\":\"addconcept\",\n" + "\"concept\":"
    	modifyConcept(data,"addconcept");
    }
    if(type=="deleteconcept"){// "{\"type\":\"deleteconcept\",\n" + "\"concept\":"conceptindicate+
    	modifyConcept(data,"deleteconcept");
    }
    if(type=="updateconcept"){//"{\"type\":\"updateconcept\",\n" + "\"concept\":" 
    	modifyConcept(data,"updateconcept");
    }
    if(type=="deleterule"){//"{\"type\":\"deleterule\",\n" + "\"rule\":" 
    	modifyRule(data,"deleterule");
    }
    if(type=="addrule"){//"{\"type\":\"addrule\",\n" + "\"rule\":"
    	modifyRule(data,"addrule");
    }
    if(type=="updaterule"){//"{\"type\":\"addrule\",\n" + "\"rule\":"
    	modifyRule(data,"updaterule");
    }
    if(type=="deleteRelation"){
    	modifyRelation(data,"deleteRelation");
    }
    if(type=="graphjson"){
    	LoadDatas(data,4);
    	return;
    }
    if(type=="log"){
    	Log(data.log,"OK");
    	return;
    }
   
};

function modifyConcept(data,type){
	if(type=="addconcept"){
		if(ConceptList!=null){
			ConceptList.data.push(data.concept);
		}
	}else if(type=="deleteconcept"){
		for(var i=0;i<ConceptList.data.length;i++){
			if((ConceptList.data[i]["@ID"]+":"+ConceptList.data[i]["@Name"])==data.concept){
				ConceptList.data.splice(ConceptList.data.indexOf(ConceptList.data[i]),1);
			}
		}
		
	}else if(type=="updateconcept"){
		var conceptid = data.concept["@ID"];
		var conceptname = data.concept["@Name"];
		for(var i=0;i<ConceptList.data.length;i++){
			if(ConceptList.data[i]["@ID"]==conceptid&&ConceptList.data[i]["@Name"]==conceptname){
				ConceptList.data.remove(ConceptList.data[i]);
				ConceptList.data.push(data.concept);
				break;
			};
		};
	};
}
function modifyRelation(data,type){
	if(type=="deleteRelation"){
		var idname = data.relation;
		var arr = idname.split(".");
		for(var i=0;i<RelationList.data.length;i++){
			if(RelationList.data[i]["@ID"]==arr[0]&&RelationList.data[i]["@Name"]==arr[1]){
				for(var j=0;j<RelationList.data[i]["DependentVariables"]["DependentVariable"].length;j++){
					if(RelationList.data[i]["DependentVariables"]["DependentVariable"][j]["@ID"]==arr[2]&&RelationList.data[i]["DependentVariables"]["DependentVariable"][j]["@Name"]==arr[3]){
						RelationList.data[i]["DependentVariables"]["DependentVariable"].remove(RelationList.data[i]["DependentVariables"]["DependentVariable"][j]);
						break;
					}
				}
				break;
			}
		}
	}
}
function modifyRule(data,type){
	if(type=="deleterule"){
		var rulename = data.rule.rulename;
		for(var i=0;i<RuleList.data.length;i++){
			if(RuleList.data[i].rulename==rulename){
				RuleList.data.remove(RuleList.data[i]);
				break;
			}
		}
	}else if(type=="addrule"){
		RuleList.data.push(data.rule);
	}else if(type=="updaterule"){
		var rulename = data.rule.rulename;
		for(var i=0;i<RuleList.data.length;i++){
			if(RuleList.data[i].rulename==rulename){
				RuleList.data[i].rulestr = data.rule.rulestr;
				break;
			}
		}
	}
}
function LoadDatas(data,flag){
	if(flag==1){
		if (stringToJson(RelationList) != data) {
	    	RelationList = data;
		}
	}else if(flag==2){
		if (stringToJson(ConceptList) != data) {
			ConceptList = data;
		} 
	}else if(flag==3){
		if (stringToJson(RuleList) != data) {
			RuleList = data;
		}
	
	}else if(flag==4){
			if(stringToJson(GraphJson)!=data){
				GraphJson = data;
				reallinks = [];
				var links = GraphJson.arrows;
				var flag = false;
				if(realnodes.length==0){
		 			flag = true;
		 		}
				var i=0;
				for(i=0;i<realnodes.length;i++){
					realnodes[i].add = 0;
				}
				
				links.forEach(function(link) {
					var reallink = {};
					
					var node={};
					node.name = link.source;
					var i=0;
					for(i=0;i<realnodes.length;i++){
						if(realnodes[i].name==node.name){
							reallink.source = realnodes[i];
							break;
						}
					}
					if(i==realnodes.length){
						realnodes.push(node);
						if(flag==false){
							node.add = 1;
						}
						reallink.source = node;
					}
					var node1={};
					node1.name = link.target;
					for(i=0;i<realnodes.length;i++){
						if(realnodes[i].name==node1.name){
							reallink.target = realnodes[i];
							break;
						}
					}
					if(i==realnodes.length){
						realnodes.push(node1);
						if(flag == false){
							node1.add = 1;	
						}
						reallink.target = node1;
					}
					reallink.left = false;
					reallink.right = true;
					reallink.value = 20;
					reallinks.push(reallink);
				});
				drawsvg21(realnodes,reallinks);
		}
		
	}
};

function RelationShow(){
	
	var _row;  
	var _cell; 
	var table = document.getElementById("table1"); 
	for(var i=table.rows.length-1;i>=0;i--){
		 table.deleteRow(i);
	}
	if(RelationList!=null){
		for(var i=0;i<RelationList.data.length;i++){
			_row = table.insertRow(-1);
			_cell = _row.insertCell(-1);
//	        if(RelationList.data[i].Variable!=null){//没有生成此知识的
	        	_cell.innerHTML+=i+":"+RelationList.data[i]["@ID"]+":"+RelationList.data[i]["@Name"];
//	        }
	     //   _row.onclick = rowClick;
		}
	}
};

function readknowledge(evt){
    var f = evt.target.files[0];
//    var content = document.getElementById("content");
    if (f) {
      var r = new FileReader();
      r.onload = function(e) {
          var contents = e.target.result;
          if(contents.indexOf("Relation")>=0){
        	  Relationcontent = parseXML(contents);
        	  document.getElementById("math").style.display="none";
        	  document.getElementsByTagName("svg").item(0).style.display="block";
        	  displayRelation(f.name,contents);
        	 
          }else if(contents.indexOf("Performance")>=0){
        	  Conceptcontent = parseXML(contents);
        	  document.getElementById("math").style.display="none";
        	  document.getElementsByTagName("svg").item(0).style.display="block";
        	  displayConcept(f.name,contents);
//        	  
          }else if(contents.indexOf("<mrow>")>=0){
        	  document.getElementsByTagName("svg").item(0).style.display="none";
        	  document.getElementById("Attributes").style.display="none";
        	  document.getElementById("math").style.display="block";
//        	  document.getElementById("math").innerHTML=contents;
        	  diplayRule(f.name,contents);
//        	  Send("addRule:"+f.name+":"+contents);
          }else{
        	  return;
          };
//          alert(f.name);
//          content.innerText = contents;

      };
      r.readAsText(f);
    } else {
      alert("Failed to load file");
    };
	
}

function diplayRule(filename,contents){
	document.getElementById("mathcontent").innerHTML=contents;
	rulename = filename;
	rulecontent = contents;
}
function parseXML(text) 
{ 
	try //Internet Explorer 
	{ 
		xmlDoc=new ActiveXObject("Microsoft.XMLDOM"); 
		xmlDoc.async="false"; 
		xmlDoc.loadXML(text); 
		return xmlDoc;
	}catch(e){ 
		try{  //Firefox, Mozilla, Opera, etc. 
			parser=new DOMParser(); 
			xmlDoc=parser.parseFromString(text,"text/xml");
			return xmlDoc;
		}catch(e){ 
			alert(e.message); 
			return; 
		};
	};
} 
function displayRelation(filename,contents){
	var os = contents;
	var DependentVariables = xmlDoc.getElementsByTagName("DependentVariables");
//	var DependentEles = DependentVariables.getElementsByTagName("Conception");
	for(var i=0;i<DependentVariables[0].childNodes.length;i++){
		if(DependentVariables[0].childNodes[i].nodeName=="Conception"){
			var id = DependentVariables[0].childNodes[i].attributes[0].value;
			var name = DependentVariables[0].childNodes[i].attributes[1].value;
			var j;
			
			for(j=0;j<RelationList.data.length;j++){
				if(RelationList.data[j]["@Name"]==name){
					contents = os.substring(0,os.indexOf("DependentVariables"))+"DependentVariables>\n"+"<Conception ID=\""+id+"\" Name=\""+name+"\"/>"+
					 "\n"+os.substring(os.indexOf("</DependentVariables>"));
					Send(username+":"+"updateRelation:"+filename+":"+id+":"+name+":"+contents);
					break;
				};
			}
			
			if(j==RelationList.data.length){
				contents = os.substring(0,os.indexOf("DependentVariables"))+"DependentVariables>\n"+"<Conception ID=\""+id+"\" Name=\""+name+"\"/>"+
				"\n"+os.substring(os.indexOf("</DependentVariables>"));
				Send(username+":"+"addRelation:"+filename+":"+id+":"+name+":"+contents);
			};
		};
	}
	
}
function displayConcept(filename,contents){
	conceptobj = {};
	var id = Conceptcontent.childNodes[0].attributes[0].value;
	var name = Conceptcontent.childNodes[0].attributes[1].value;
	conceptobj.id = id;
	conceptobj.name=name;
	var j;
	for(j=0;j<ConceptList.data.length;j++){
		if(ConceptList.data[j]["@Name"]==name){
			Send(username+":"+"updateConcept:"+filename+":"+id+":"+name+":"+contents);
			
			alert("修改了知识"+id+":"+name);
			break;
		}
	}
	if(j==ConceptList.data.length){
		Send(username+":"+"addConception:"+filename+":"+id+":"+name+":"+contents);
		alert("添加了知识"+id+":"+name);
	}
	
}

function deleteConcept(conceptname){
	if(conceptname.indexOf("formula")>=0){
		var arr = conceptname.split(".");
		for(var i=0;i<RuleList.data.length;i++){
			if(arr[2]==RuleList.data[i]["rulename"]){
				Send(username+":"+"deleteRule:"+RuleList.data[i].rulename+":"+"..");
			}
		}
	}else{
		for(var i=0;i<ConceptList.data.length;i++){
			if(conceptname==ConceptList.data[i]["@ID"]+"."+ConceptList.data[i]["@Name"]){
				Send("deleteConception:"+ConceptList.data[i]["@ID"]+":"+ConceptList.data[i]["@Name"]);//难实现
			}
		}
	}
}

function deleteRelation(conceptname1,conceptname2){
	Send("deleteRelation:"+conceptname1+"."+conceptname2);//难实现
}

function AddRule(){
	document.getElementById("rulesave").style.display = "block";
	document.getElementById("rulefilename").value = rulename;
	
	
}
function RealAddRule(){
	var j;
	var realrulename = document.getElementById("rulefilename").value;
	for(j=0;j<RuleList.data.length;j++){
		if(RuleList.data[j]["rulename"]==realrulename){
			Send(username+":"+"updateRule:"+realrulename+":"+rulecontent);
			break;
		}
	}
	if(j==RuleList.data.length){
		Send(username+":"+"addRule:"+realrulename+":"+rulecontent);
	}
	document.getElementById("rulesave").style.display = "none";
	document.getElementById("math").style.display="none";
	document.getElementsByTagName("svg").item(0).style.display="block";
	document.getElementById("Attributes").style.display="block";
}
function RealQuitRule(){
	document.getElementById("math").style.display="none";
	document.getElementById("rulesave").style.display = "none";
	rulename="";
	rulecontent="";
	document.getElementsByTagName("svg").item(0).style.display="block";
	document.getElementById("Attributes").style.display="block";
}
function QuitRule(){
	document.getElementById("math").style.display="none";
	document.getElementById("rulesave").style.display = "none";
	document.getElementById("mathcontent").innerHTML="";
	rulename = "";
	rulecontent = "";
	document.getElementsByTagName("svg").item(0).style.display="block";
	document.getElementById("Attributes").style.display="block";
}

function SearchKnowledge(){
	var search = document.getElementById("knowledgeid").value;
	var nodes = [];
	var links = [];
	var i;
	if(search!=""&&search.indexOf("formula")>=0){
		for(i=0;i<realnodes.length;i++){
			if(realnodes[i].name.indexOf(search)>=0){
				var node = {};
				node.name  = realnodes[i].name;
				node.reflexive = false;
				var j;
				for(j=0;j<nodes.length;j++){
					if(nodes[j].name==node.name){
						break;
					}
				}
				if(j==nodes.length){
					nodes.push(node);
				}
				
				for(var k=0;k<reallinks.length;k++){
					if(reallinks[k].source.name==realnodes[i].name){
						var node1 = {};
						var link = {};
						node1.name  = reallinks[k].target.name;
						node1.reflexive = false;
						var j;
						for(j=0;j<nodes.length;j++){
							if(nodes[j].name==node1.name){
								break;
							}
						}
						if(j==nodes.length){
							nodes.push(node1);
						}
						link.source = node;
						link.target = nodes[j];
						link.left = false;
						link.right = true;
						link.value = 20;
						links.push(link);
					}
					if(reallinks[k].target.name==realnodes[i].name){
						var node1 = {};
						var link = {};
						node1.name  = reallinks[k].source.name;
						node1.reflexive = false;
						var j;
						for(j=0;j<nodes.length;j++){
							if(nodes[j].name==node1.name){
								break;
							}
						}
						if(j==nodes.length){
							nodes.push(node1);
						}
						link.source = nodes[j];
						link.target = node;
						link.left = false;
						link.right = true;
						link.value = 20;
						links.push(link);
					}
				}
			}
		}
		
	}else{
		for(i=0;i<realnodes.length;i++){
			if(realnodes[i].name.indexOf(search)>=0){
				var node = {};
				node.name  = realnodes[i].name;
				node.reflexive = false;
				var j;
				var index1;
				for(j=0;j<nodes.length;j++){
					if(nodes[j].name==node.name){
						break;
					}
				}
				if(j==nodes.length){
					nodes.push(node);
				}
				index1 = j;
				for(var k=0;k<reallinks.length;k++){
					if(reallinks[k].target.name==realnodes[i].name){
						var node1 = {};
						var link = {};
						var index=0;
						node1.name  = reallinks[k].source.name;
						node1.reflexive = false;
						var j;
						for(j=0;j<nodes.length;j++){
							if(nodes[j].name==node1.name){
								
								break;
							}
						}
						if(j==nodes.length){
							nodes.push(node1);
						}
						index = j;
						link.source = nodes[j];
						link.target = nodes[index1];
						link.left = false;
						link.right = true;
						link.value = 20;
						links.push(link);
						for(var l=0;l<reallinks.length;l++){
							if(reallinks[l].target.name==node1.name){
								var node2 = {};
								var link1 = {};
								node2.name  = reallinks[l].source.name;
								node2.reflexive = false;
								var j;
								for(j=0;j<nodes.length;j++){
									if(nodes[j].name==node2.name){
										break;
									}
								}
								if(j==nodes.length){
									nodes.push(node2);
								}
								link1.source = nodes[j];
								link1.target = nodes[index];
								link1.left = false;
								link1.right = true;
								link1.value = 20;
								links.push(link1);
//								nodes.push(reallinks[k].source);
//								links.push(reallinks[k]);
							}
						}
					}
				}
			}
		}
	}
	drawsvg21(nodes,links);
	document.getElementById("back").style.display = "block";
	
	if(i==RelationList.data.length){
		alert("无此条知识");
	}
	
}

function BackMainKnowledge(){
	document.getElementById("back").style.display = "none";
	drawsvg21(realnodes,reallinks);
	
}

function drawsearch(){}
function docsubmit(){
	   $("#loading").ajaxStart(function() {  
        $(this).show();  
    }).ajaxComplete(function() {  
        $(this).hide();  
    });
	   $.ajaxFileUpload({  
        url : 'FileUpLoad',// servlet请求路径  
        secureuri : false,  
        fileElementId : 'file3',// 上传控件的id  
       dataType : 'docx',  
  //     data : {username : username}, // 其它请求参数
        success : function(data, status) {  
            if(data) {  
                alert(data);  
            }
            content = data.substring(data.indexOf("content")+10,data.indexOf("file")-3);
            rulename = data.substring(data.indexOf("file")+7,data.length-2);
           data = data.substring(data.indexOf("content")+10,data.indexOf("file")-3);
        //    data = data.substring(data.indexOf("mrow")-4,data.lastIndexOf("/mrow")+9);
      //      data = data.substring(data.indexOf("{"),data.indexOf("</pre>"));
       //    data = stringToJson(data);
            document.getElementById("DocFormulaUpload").style.display = "none";
          //document.getElementById("login").style.display = "none";
          document.getElementsByTagName("svg").item(0).style.display="none";
          document.getElementById("Attributes").style.display="none";
          document.getElementById("math").style.display="block";
          document.getElementById("mathcontent").innerHTML = content;
          rulecontent = content;
          
        },  
        error : function(data, status, e) {  
            alert('上传出错');  
        }  
    });
}
function DocFormulaClick(){
	document.getElementById("DocFormulaUpload").style.zIndex = 9999; 
	document.getElementById("DocFormulaUpload").style.display = "block";
	
}

function QuitDocRule(){
	document.getElementById("DocFormulaUpload").style.display = "none";
}

function drawsvg21(nodes,links){
	var margin = {top: 10, right: 1, bottom: 6, left: 5},
	    width = 1035 - margin.left - margin.right,
	    height = 630 - margin.top - margin.bottom;

	var formatNumber = d3.format(",.0f"),
	    format = function(d) { return formatNumber(d) + " TWh"; },
	    color = d3.scale.category20();
	    d3.select('#mathmldiv').selectAll("svg").remove();
	var colors = d3.scale.category10();

	var mousedown_node = null,
		selected_node = null;
	var svg = d3.select("#mathmldiv").append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	  .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	var sankey = d3.sankey()
	    .nodeWidth(10)
	    .nodePadding(0)
	    .size([width, height]);

	svg.append('svg:defs').append('svg:marker')
	.attr('id', 'end-arrow')
	.attr('viewBox', '0 -5 10 10')
	.attr('refX', 6)
	.attr('markerWidth', 10)
	.attr('markerHeight', 10)
	.attr('orient', 'auto')
	.append('svg:path')
	.attr('d', 'M0,-5L10,0L0,5')
	.attr('fill', '#000');

	svg.append('svg:defs').append('svg:marker')
	.attr('id', 'start-arrow')
	.attr('viewBox', '0 -5 10 10')
	.attr('refX', 4)
	.attr('markerWidth', 3)
	.attr('markerHeight', 3)
	.attr('orient', 'auto')
	.append('svg:path')
	.attr('d', 'M10,-5L0,0L10,5')
	.attr('fill', '#000');

	var path = sankey.link();

	//d3.json("energy.json", function(energy) {

	  sankey.nodes(nodes)
	      .links(links)
	      .layout(32);

	  var link = svg.append("g").selectAll(".link")
	      .data(links)
	    .enter().append("path")
	      .attr("class", "link")
	      .attr("d", path)
	      .style("stroke-width", 1)
	       .style('marker-start', function(d) { return d.left ? 'url(#start-arrow)' : ''; })
		    .style('marker-end', function(d) { return d.right ? 'url(#end-arrow)' : ''; })

//	      .style("stroke-width", function(d) { return Math.max(1, d.dy); })
	      .sort(function(a, b) { return b.dy - a.dy; });

	  link.append("title")
	      .text(function(d) { return d.source.name + " → " + d.target.name + "\n" + format(d.value); });

	  var drag =d3.behavior.drag()
	.origin(function(d) { return d; })
	.on("drag", dragmove);
	  var node = svg.append("g").selectAll(".node")
	      .data(nodes)
	    .enter().append("g")
	      .attr("class", "node")
	      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
	      .on("click",click)
	      .call(drag);
	  var fillstyle = function(d) {if(d.add == 1){return "#f00";} else  if(d==selected_node){ return  "#000";}
	  	else if(d.name.indexOf("formula")>=0){ return "#00f";}
	  	else{return "#ff0";}};
	  node.append("circle")
	  	.style("stroke-width",1)
	  	.attr('r', sankey.nodeWidth())
	  	.style('fill',fillstyle)
	  .style("stroke", function(d) { return d3.rgb(d.color).darker(2); })
	  .classed("reflexive",function(d){return d.reflexive;})
	.append("title")
	  .text(function(d) { return d.name + "\n" + format(d.value); });

	  node.append("text")
	      .attr("x", -6)
	      .attr("y",0)
	      .attr("dy", ".35em")
	      .attr("text-anchor", "end")
	      .attr("transform", null)
	      .text(function(d) { return d.name; })
	    .filter(function(d) { return d.x < width / 2; })
	      .attr("x", 6 + sankey.nodeWidth())
	      .attr("text-anchor", "start");

	  function dragmove(d) {
	    d3.select(this).attr("transform", "translate(" + d.x + "," + (d.y = Math.max(0, Math.min(height - d.dy, d3.event.y))) + ")");
	    sankey.relayout();
	    link.attr("d", path);
	  }
	  function click(d){
		  if(d3.event.ctrlKey) return;

	      // select link
	      mousedown_node = d;
	      if(mousedown_node === selected_node) {selected_node.reflexive = false;selected_node = null;}
	      else {selected_node = mousedown_node;
	      selected_node.reflexive = true;}
	      if(d3.event.defaultPrevented) return;
	      node.selectAll('circle').style("fill", function(d) { if(d.add == 1){return "#f00";} else if(d==selected_node){ return "#000";}
	  	else if(d.name.indexOf("formula")>=0){ return  "#00f";}
	  	else{return   "#ff0";}});
	 	 if(d.name.indexOf("formula")>=0){
			dObject = {};
			
			dObject.content = d;
	 		if(RuleList.data instanceof Array){
				var i;
				var len = RuleList.data.length;
//				var createDiv=document.createElement("div");  
//				createDiv.id = "att";
				for(i=0;i<len;i++){
					if(d.name.substring(7,d.name.indexOf("."))==RuleList.data[i].rulename.substring(0,RuleList.data[i].rulename.indexOf("."))){
//					if(d.name.indexOf(RuleList.data[i].rulename.substring(0,RuleList.data[i].rulename.indexOf(".")))>=0){
						
//						createDiv.innerHTML = RuleList.data[i].rulestr;
						document.getElementById("conceptproperty").style.display = "none";
						document.getElementById("mathproperty").style.display = "block";
//						document.getElementById("mathproperty").innerHTML = RuleList.data[i].rulestr;
						document.getElementById("mathproperty1").innerHTML = RuleList.data[i].rulestr;
						document.getElementById("mathproperty2").innerHTML = "";
						dObject.index = i;
						if(RuleList.data[i].targetLinks!=null){
							for(var j=0;j<RuleList.data[i].targetLinks.length;j++){
								var newinput = document.createElement("input");
								newinput.id = "input"+j;
								newinput.value = RuleList.data[i].targetLinks[j].key;
								document.getElementById("mathproperty2").appendChild(newinput);
								createSelect("select"+j);
								addOption("select"+j,d.targetLinks,0);
								var ops = document.getElementById("select"+j).options;    
							    for(var k=0;k<ops.length; k++){    
							        var tempValue = ops[k].value;    
							        if(tempValue == RuleList.data[i].targetLinks[j].value)    
							        {    
							            ops[k].selected = true;    
							        }    
							    }
//								document.getElementById("selecttarget0").options[0].selected=true;
							}
	//						for(var k=0;k<d.sourceLinks.length;k++){
								var newinput = document.createElement("div");
								newinput.id = "inputtarget0";
								document.getElementById("mathproperty2").appendChild(newinput);
								newinput.innerHTML = gettarget(RuleList.data[i].rulestr);
								createSelect("selecttarget0");
								addOption("selecttarget0",d.sourceLinks,1);
	//							document.getElementById("selecttarget0").options[0].selected=true;
								
	//						}
						}else{
							
//							dObject = d;
							for(var j=0;j<d.targetLinks.length;j++){
								var newinput = document.createElement("input");
								newinput.id = "input"+j;
								document.getElementById("mathproperty2").appendChild(newinput);
								createSelect("select"+j);
								addOption("select"+j,d.targetLinks,0);
								document.getElementById("select"+j).options[j].selected = true;
							}
	//						for(var k=0;k<d.sourceLinks.length;k++){
								var newinput = document.createElement("div");
								newinput.id = "inputtarget0";
								document.getElementById("mathproperty2").appendChild(newinput);
								newinput.innerHTML = gettarget(RuleList.data[i].rulestr);
								createSelect("selecttarget0");
								addOption("selecttarget0",d.sourceLinks,1);
	//							document.getElementById("selecttarget0").options[0].selected=true;
						}
						break;
					}
				}
				if(i==RuleList.data.length){
//					createDiv.innerHTML = "none";
					alert("没有"+d.name+"formula");
					document.getElementById("conceptproperty").style.display = "none";
					document.getElementById("mathproperty").style.display = "block";
					document.getElementById("mathproperty1").innerHTML = "";
					document.getElementById("mathproperty2").innerHTML ="none";
//					document.getElementById("Attributes").appendChild(createDiv);
				}
			}
	 	 }else{
//				var createDiv=document.createElement("div");  
//				createDiv.id = "att";
	 		 var i=0;
	 		 for(i=0;i<ConceptList.data.length;i++){
	 			 if((ConceptList.data[i]["@ID"]+"."+ConceptList.data[i]["@Name"]).indexOf(d.name)>=0){
	 				document.getElementById("conceptproperty").style.display = "block";
					document.getElementById("mathproperty").style.display = "none";
	 				document.getElementById("id").value = ConceptList.data[i]["@ID"];
	 	 			document.getElementById("name").value = ConceptList.data[i]["@Name"];
	 	 			document.getElementById("Accuracy").value = ConceptList.data[i]["Performance"]["Accuracy"];
	 	 			document.getElementById("RefreshRate").value = ConceptList.data[i]["Performance"]["RefreshRate"];
	 	 			document.getElementById("Range").value = ConceptList.data[i]["Performance"]["Range"];
	 	 			document.getElementById("MaxTransDelay").value = ConceptList.data[i]["Performance"]["MaxTransDelay"];
	 	 			document.getElementById("SigBits").value = ConceptList.data[i]["Performance"]["SigBits"];
	 	 			document.getElementById("Redundancy").value = ConceptList.data[i]["Safety"][0];
	 	 			document.getElementById("Interface").value = ConceptList.data[i]["Interface"][0]["@BUS"];
	 	 			document.getElementById("Description").value = "";
	 	 			break;
	 			 }
	 		 }
	 		 if(i==ConceptList.data.length){
	 			alert("没有"+d.name+"知识");
	 			document.getElementById("conceptproperty").style.display = "block";
				document.getElementById("mathproperty").style.display = "none";
	 			document.getElementById("id").value = "";
	 			document.getElementById("name").value = "";
	 			document.getElementById("Accuracy").value = "";
	 			document.getElementById("RefreshRate").value = "";
	 			document.getElementById("Range").value = "";
	 			document.getElementById("MaxTransDelay").value = "";
	 			document.getElementById("SigBits").value = "";
	 			document.getElementById("Redundancy").value = "";
	 			document.getElementById("Interface").value = "";
	 			document.getElementById("Description").value = "";
	 		 }
	 	 }
		  
	  }
function createSelect(id){

		  var createSelect = document.createElement("select");//定义一个下拉框
	      createSelect.id = id;//设置下拉框的ID
	      document.getElementById("mathproperty2").appendChild(createSelect);//把定义的下拉框添加到页面中
	  }

	//添加选项option

	 function addOption(id,optionarr,flag){
	       var obj=document.getElementById(id);//根据id查找对象，
	       if(flag==0){
	       for(var i=0;i<optionarr.length;i++){
//	    	   var addOption=null;
	    	   
	    		   var addOption=new Option(optionarr[i].source.name,optionarr[i].source.name);//生成一个选项
	    		   obj.add(addOption);    //这个只能在IE中有效
		    	     obj.options.add(addOption); //这个兼容IE与firefox
	       }
	     }else if(flag==1){
	    		   addOption=new Option(optionarr[0].target.name,optionarr[0].target.name);//生成一个选项
	    		   obj.add(addOption);    //这个只能在IE中有效
		    	     obj.options.add(addOption); //这个兼容IE与firefox
	    	   }
//	    	     obj.add(addOption);    //这个只能在IE中有效
//	    	     obj.options.add(addOption); //这个兼容IE与firefox
//	       }
	    
	 }

	//删除所有选项option

	 function removeAll(id){
	       var obj=document.getElementById(id);
	      obj.options.length=0;

	 }

	//删除一个选项option

	function removeOne(id){
	var obj=document.getElementById(id);

	var index =obj.selectedIndex;

	obj.options.remove(index);
	}
	//获得选项option的文本和值
	function getvalue(id){
	var obj=document.getElementById(id);

	var index=obj.selectedIndex; //序号，取当前选中选项的序号

	var val=obj.options[index].value;//获取值
//	var val = obj.options[index].text;;//获取文本
	return val;
	}

	//修改选项option
	function modifyoption(id){
	var obj=document.getElementById(id);

	var index=obj.selectedIndex; //序号，取当前选中选项的序号

	obj.options[index]=new option("新文本","新值");
	}
	//删除select

	  function removeSelect(id){
	        var mySelect = document.getElementById(id);
	mySelect.parentNode.removeChild(mySelect);
	 }
	  //从mathml中获取目标变量
	  function gettarget(mathml){
		  var index1 = mathml.indexOf(">=<");
		  var index2 = mathml.indexOf("<mrow><mrow>");
		  var string="";
		  if(index2==0){
			  var str = "<mrow><mrow>";
			  var strtemp = mathml.substring(index2+str.length,index1);
			  var strtemp1 = strtemp.substring(0,strtemp.lastIndexOf("/"));
			  var strtemp2 = strtemp.substring(strtemp.lastIndexOf("/"));
			  
			  var index3 = strtemp2.indexOf(">");
			  strtemp2 = strtemp2.substring(0,index3+1);
			  string = strtemp1+strtemp2;
//			  var index4 = strtemp.lastIndexOf("/");
//			  if(strtemp.indexOf("<mrow>")>=0){
//				  string = strtemp.substring(0,strtemp.indexOf("<mrow>")-1);
//			  }else{
//				  string = strtemp.substring(0,strtemp.lastIndexOf("<")-1);
//			  }
		  }else{
			  index2 = mathml.indexOf("<mrow>");
			  var strtemp = mathml.substring(index2+6,index1);
			  var strtemp1 = strtemp.substring(0,strtemp.lastIndexOf("/"));
			  var strtemp2 = strtemp.substring(strtemp.lastIndexOf("/"));
			  
			  var index3 = strtemp2.indexOf(">");
			  strtemp2 = strtemp2.substring(0,index3+1);
			  string = strtemp1+strtemp2;
//			  if(strtemp.indexOf("<mrow>")>=0){
//				  string = strtemp.substirng(0,strtemp.indexOf("<mrow>")-1);
//			  }else{
//				  string = strtemp.substring(0,strtemp.lastIndexOf("<")-1);
//			  }
		  }
		  return string;
//		  if(mathml.substring(index2+6,index1).indexOf("<mrow>")>=0){
//			  string = mathml.substring(mathml.substring(index2+6,index1).indexOf("<mrow>")+6);
//		  }
	  }
	//})
	}
	;

function ModifyRule(){
	if(dObject!=null){
		var targetlinks=[];
		for(var i=0;i<dObject.content.targetLinks.length;i++){
			var key = document.getElementById("input"+i).value;
			if(key!=""){
				var obj = {};
				obj.key = key;
				var value = document.getElementById("select"+i).options[document.getElementById("select"+i).selectedIndex].value;
				obj.value = value;
				targetlinks.push(obj);
			}else{
				alert("input"+i+"不能为空");
//				dObject = null;
				return;
			}
		}
		RuleList.data[dObject.index].targetLinks = targetlinks;
		var sourcelinks={};
		sourcelinks.key = document.getElementById("inputtarget0").innerText;
		sourcelinks.value = document.getElementById("selecttarget0").options[document.getElementById("selecttarget0").selectedIndex].value;
		RuleList.data[dObject.index].sourceLinks = sourcelinks;
		RuleList.data[dObject.index].flag = 1;
//		dObject = null;
		alert("保存成功");
	}
}
function SimulateRule(){
	if(dObject!=null){
//		var formulaindexstr="";//记录所有从后到前面的公式以6;4;8表示
//		var conceptindexstr="";//记录所有初始输入以8(C01...);9(C02...)表示
//		var finalstr="";
		var indexstr =simulatejudge(dObject.content.targetLinks,0,dObject.index,0);
		if(indexstr!=""){
			FZObject ={};
			var dobjectstr = "{$"+dObject.index+":["+indexstr;
			
			FZObject.objectstr=dobjectstr;
			
			var strarr = dobjectstr.split("$");
			var rulestr={};
			for(var i=1;i<strarr.length;i++){
				var index = strarr[i].substring(0,strarr[i].indexOf(":"));
				rulestr[index] = RuleList.data[index];
			}
			FZObject.ruleobject=rulestr;
			Log(dobjectstr+jsonToString(rulestr));
//			Log("{$"+dObject.index+":["+indexstr);
//			alert("$"+dObject.index+";"+indexstr);//$7;formula6..(C26...);$9;formula8..(C24....);$并填写相应的变量初始采样频率
//			alert("仿真成功");
		}else{
			alert("仿真失败");
		}
//		alert("仿真成功");
	}
//	dObject =null;
}
//判断是否可以仿真，即公式的变量是否都已经对应成知识
function simulatejudge(targetLinks,level,index,num){
//	while(targetLinks.length!=0){
		var finalstr="";
		var i=0;
		for(i=0;i<targetLinks.length;i++){
			var targetlk = targetLinks[i].source.targetLinks;
			
			if(targetlk.length==0){
				if(document.getElementById("InputBox").style.display =="none"){
					document.getElementById("InputBox").style.zIndex = 9999; 
					document.getElementById("InputBox").style.display ="block";
				}
				
				for(var l=0;l<RuleList.data[index].targetLinks.length;l++){
					if(RuleList.data[index].targetLinks[l].value==targetLinks[i].source.name){
						
//						var m=0;
//						for(m=0;m<=num;m++){
//							if(document.getElementById("parameter"+m).value==(RuleList.data[index].targetLinks[l].key+":"+RuleList.data[index].targetLinks[l].value)){
//								break;
//							}
//						}
//						if(m>num){
							var newinput = document.createElement("input");
							newinput.id = "parameter"+num;
							
							newinput.value = RuleList.data[index].targetLinks[l].key+":"+RuleList.data[index].targetLinks[l].value;
							
							document.getElementById("parameterBox").appendChild(newinput);
							newinput = document.createElement("input");
							newinput.id = "parameterinput"+num;
							initinputnum=num;
							num++;
							
	//						newinput.value = RuleList.data[index].targetLinks[l].key+":"+RuleList.data[index].targetLinks[l].value;
							document.getElementById("parameterBox").appendChild(newinput);
//						}

					}
				}
			}
//				conceptindexstr = conceptindexstr+targetLinks[i].target.name+"("+targetLinks[i].source.name+")"+";";
//				finalstr=finalstr+conceptindexstr+"$";
			finalstr = finalstr+"{"+targetLinks[i].source.name;
//				finalstr=finalstr+"{level"+level+":"+targetLinks[i].target.name+":"+targetLinks[i].source.name;
//				+"};";
//			}else{
				for(var k=0;k<targetlk.length;k++){
					if(targetlk[k].source.name.indexOf("formula")>=0){
						var len = RuleList.data.length;
	//					var createDiv=document.createElement("div");  
	//					createDiv.id = "att";
						var j=0;
						for(j=0;j<len;j++){
							if(targetlk[k].source.name.substring(7,targetlk[k].source.name.indexOf("."))==RuleList.data[j].rulename.substring(0,RuleList.data[j].rulename.indexOf("."))){
	//						if(targetlk[k].source.name.indexOf(RuleList.data[j].rulename.substring(0,RuleList.data[j].rulename.indexOf(".")))>=0){
								if(RuleList.data[j].flag!=1){
									alert("公式"+targetlk[k].source.name+"变量没有对应相应知识");
									return "";
								}
								
//								formulaindexstr = formulaindexstr+j+";";
//								finalstr=finalstr+formulaindexstr;
//								finalstr=finalstr+"level"+level+":{$"+j+":[";
								finalstr=finalstr+":{$"+j+":[";
								break;
							}
						}
						if(j==len){
							alert("没有"+targetlk[k].source.name+"公式");
							return "";
						}
						level++;
						finalstr=finalstr+simulatejudge(targetlk[k].source.targetLinks,level,j,num);
						level--;
					}
				}
				if(i==targetLinks.length-1){
					finalstr = finalstr+"}";
				}else{
					finalstr = finalstr+"},";
				}
			}
//	}
		if(i==targetLinks.length){
			finalstr =finalstr+"]}";
		}
//		level--;
		return finalstr;
//		return formulaindexstr+"$"+conceptindexstr;
}
function SendFZ(){
	var parameterstr = "";
	for(var i=0;i<=initinputnum;i++){
		if(document.getElementById("parameterinput"+i).value==""){
			alert("不能为空");
			return;
		}
		parameterstr+=document.getElementById("parameter"+i).value+":"+document.getElementById("parameterinput"+i).value+";";
	}
	FZObject.parameterstr=parameterstr;
	Send("FZ:"+jsonToString(FZObject));
}
function CloseFZ(){
	document.getElementById("InputBox").style.display="none";
	document.getElementById("parameterBox").innerHTML = "";
}
	//})
	
function loginout(){
	Send(username+":logout");
	ws.close();
	window.location.href = "login.jsp";
}
function chat(){
	document.getElementById("chatbox").style.zIndex = 9999; 
	document.getElementById("chatbox").style.display = "block";
	
}
function SendDataClicked(){
	if(document.getElementById("DataToSend").value.trim()==""){
		alert("输入不能为空");
	}else{
		Send(username+"说:"+document.getElementById("DataToSend").value);
		document.getElementById("DataToSend").value ="";
	}
}
function CloseDataClicked(){
	document.getElementById("DataToSend").value ="";
	document.getElementById("chatbox").style.display ="none";
}
$(document).ready(  
        function() {  
            $("#chatbox").keydown(function(event) {  
            	if (event.keyCode == 13) {
            		SendDataClicked();
            	}
            });
        }  
    );