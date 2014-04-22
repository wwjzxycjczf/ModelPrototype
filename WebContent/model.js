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
 loginflag = false;
// links ={};
//	
//nodes = {};
//force={};

//path={};
//circle = {};
//text = {};
 //websocket模块
 
 var ws;
 var SocketCreated = false;
 
 var Drawsvg;
 
 function loginClicked(){
	 if(checkUserName()){
		 if(checkUserPass()){
			 loginflag = true;
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
		
function onLoad(){
	

	ConceptList = stringToJson(ConceptList);
	RelationList = stringToJson(RelationList);
	RuleList = stringToJson(RuleList);
	Connection();
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
        document.getElementById("login").style.display = "none";
        document.getElementById("mainframe").style.display="block";
        document.getElementById('fknowledge').addEventListener('change', readknowledge, false);
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
	send(username+":logout");
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
//				RuleList.data.remove(RuleList.data[i]);
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
				var links = GraphJson.arrows;
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
						reallink.source = node;
					}
					var node1={};
					node1.name = link.target;
					var i=0;
					for(i=0;i<realnodes.length;i++){
						if(realnodes[i].name==node1.name){
							reallink.target = realnodes[i];
							break;
						}
					}
					if(i==realnodes.length){
						realnodes.push(node1);
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
	
//	
}

function deleteConcept(conceptname){
	if(conceptname.indexOf("formula")>=0){
		var arr = conceptname.split(".");
		for(var i=0;i<RuleList.data.length;i++){
			if(arr[2]==RuleList.data[i]["rulename"]){
//				ConceptList.data.splice(ConceptList.data.indexOf(ConceptList.data[i]),1);
				Send(username+":"+"deleteRule:"+RuleList.data[i].rulename+":"+"..");
			}
		}
	}else{
		for(var i=0;i<ConceptList.data.length;i++){
			if(conceptname==ConceptList.data[i]["@ID"]+"."+ConceptList.data[i]["@Name"]){
//				ConceptList.data.splice(ConceptList.data.indexOf(ConceptList.data[i]),1);
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
//	var formulanumber = 0;
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
				
//				nodes.push(realnodes[i]);
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
//						nodes.push(reallinks[j].target);
//						links.push(reallinks[j]);
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
//						nodes.push(reallinks[j].source);
//						links.push(reallinks[j]);
					}
				}
//				break;
			}
		}
//		if(i==realnodes.length){
//			alert("无此条知识");
//			return;
//		}
		
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
//				nodes.push(realnodes[i]);
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
//						nodes.push(reallinks[j].source);
//						links.push(reallinks[j]);
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

function DocFormulaClick(){
	document.getElementById("DocFormulaUpload").style.zIndex = 9999; 
	document.getElementById("DocFormulaUpload").style.display = "block";
	
}