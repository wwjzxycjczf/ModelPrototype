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
 
 var ws;
 var SocketCreated = false;
 
 var Drawsvg;
 var outputs=[];
 var inputs=[];
 var stackList=[];
 var inputnum="{}";
 var requirements="[]";
 var over = false;
 var over1 = false;
 var existstr = [];
 var mapVariable = "{}";
 var mapdependVariable = "{}";
 var readnum = 0;
 var filenum = 0;
 var inferflag = false;

 var formulanum = 0;
 var nodes = [];
 var links = [];

 var inputsecurity = {};
 var outputsafety = {};
 var highlevelConceptList = {};
 
		
function onLoad(){
	

	ConceptList = stringToJson(ConceptList);
	RelationList = stringToJson(RelationList);
	RuleList = stringToJson(RuleList);
	Connection();
   	document.getElementById('files').addEventListener('change', readsvgrequirementfolder, false);
}

function Log(Text, MessageType) {
    if (MessageType == "OK") Text = "<span style='color: green;'>" + Text + "</span>";
    else if (MessageType == "ERROR") Text = "<span style='color: red;'>" + Text + "</span>";
    document.getElementById("eventlist").innerHTML = document.getElementById("eventlist").innerHTML + Text + "<br />";
    var eventlist = document.getElementById("eventlist");
    eventlist.scrollTop = eventlist.scrollHeight;
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
    
    if(type=="graphjson"){
    	LoadDatas(data,4);
    	return;
    }
   
};

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
				drawsvg22(realnodes,reallinks);
		}
		
	}
};
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
							}
						}
					}
				}
			}
		}
					
		
	}
	drawsvg22(nodes,links);
	document.getElementById("back").style.display = "block";
	
	if(i==RelationList.data.length){
		alert("无此条知识");
	}
	
}

function BackMainKnowledge(){
	document.getElementById("back").style.display = "none";
	drawsvg21(realnodes,reallinks);
	
}
function readsvgrequirementfolder(evt){//获取需求的文件夹
	requirements = "[]";
	mapdependentAndVariables = "[]";
//	mapdependVariable = "[]";
	requirements = stringToJson(requirements);
	mapVariable = stringToJson(mapVariable);
	mapdependVariable = stringToJson(mapdependVariable);
	inputnum = stringToJson(inputnum);
//	 document.getElementById("canvas").style.display="block";
//	  document.getElementById("myCanvas").style.display="none";
//	  document.getElementById("math").style.display="none";
//	  var canvas = document.getElementById("canvas");
//	    var context = canvas.getContext("2d");
//	    context.clearRect(0, 0, canvas.width, canvas.height);
	    readRelations();
	var files = evt.target.files;
	filenum = files.length;
//	 var output = [];
	for (var i = 0, f; f = files[i]; i++) {
	   	if (f) {
	   		var r = new FileReader();
	  	    r.onload = function(e) {
	  	    	readnum++;
	  	    	var contents = e.target.result;
	  	    	
	  	        var requirement = parseXML(contents);
	  	        var systemrequirement = requirement.getElementsByTagName("SystemRequirement");
	  	        if(systemrequirement==null){
	  	        	return;
	  	        }
	  	        var id = systemrequirement[0].attributes[0].value;
	  	        var name = systemrequirement[0].attributes[1].value;
	  	        var obj="{}";
	  	        obj = stringToJson(obj);
	  	        obj.id = id; obj.name = name;obj.content = contents;
	  	        requirements.push(obj);
	  	        var Interface = requirement.getElementsByTagName("Interface");
	  	        var safetyrelated = requirement.getElementsByTagName("Safety-related");
	  	        for(var j=0;j<Interface[0].childNodes.length;j++){
	  	        	if(Interface[0].childNodes[j].nodeName=="Device"){
	  	        		if(Interface[0].childNodes[j].attributes[2].value=="Input"){
//	  	        			var obj1="{}";
//	  	        			obj1 = stringToJson(obj);
////	  	        			obj1.id = id;
//	  	        			obj1.name = name;
//	  	        			obj1.num = 0;
//	  	        			inputs.push(id+"."+name);
//	  	        			inputnum[id+"."+name] = 0;
	  	        			var safety={};
	  	        			for(var k=0;k<safetyrelated[0].childNodes.length;k++){
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Redundancy"){
	  	        					var redundancy = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.redundancy = redundancy;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Partitioning"){
	  	        					var partitioning = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.partitioning = partitioning;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Dissimilarity"){
	  	        					var dissimilarity = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.dissimilarity = dissimilarity;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="SafetyMonitoring"){
	  	        					var safetyMonitoring = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.safetyMonitoring = safetyMonitoring;
	  	        				}
	  	        			}
	  	        			inputsecurity[name] = safety;
	  	        			inputs.push(name);
	  	        			inputnum[name] = 0;
	  	        			
//	  	        			knowledgedrawRect(0,30*(readnum-1),300,30,id,name);
	  	        		}
	  	        		if(Interface[0].childNodes[j].attributes[2].value=="Output"){
//	  	        			var obj1="{}";
//	  	        			obj1 = stringToJson(obj);
//	  	        			obj1.id = id;
//	  	        			obj1.name = name;
//	  	        			knowledgedrawRect(300,30*j,300,30,id,name);
//	  	        			outputs.push(id+"."+name);
	  	        			var safety={};
	  	        			for(var k=0;k<safetyrelated[0].childNodes.length;k++){
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Redundancy"){
	  	        					var redundancy = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.redundancy = redundancy;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Partitioning"){
	  	        					var partitioning = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.partitioning = partitioning;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="Dissimilarity"){
	  	        					var dissimilarity = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.dissimilarity = dissimilarity;
	  	        				}
	  	        				if(safetyrelated[0].childNodes[k].nodeName=="SafetyMonitoring"){
	  	        					var safetyMonitoring = safetyrelated[0].childNodes[k].childNodes[0].data;
	  	        					safety.safetyMonitoring = safetyMonitoring;
	  	        				}
	  	        			}
	  	        			outputsafety[name] = safety;
	  	        			outputs.push(name);
	  	        		}
	  	        	}
	  	        }
	  	      setTimeout("inferreq()", 1000);
	  	     
	  	      };
	  	    r.readAsText(f);
	    	}
	    }
}
function inferreq(){
	if(readnum==filenum&&inferflag==false){
		inferflag = true;
		infer();
		
	}
}
function readRelations(){
	var j;
	for(j=0;j<RelationList.data.length;j++){
		var vconceptarr = [];
		var dconceptarr = [];
		var objj = RelationList.data[j];
		if(objj["Variables"]!=null){
			if(objj["Variables"] instanceof Array){
				for(var i=0;i<objj["Variables"].length;i++){
					var arr=[];
					var obj1 = objj["Variables"][i]["Variable"];
					if(obj1 instanceof Array){
						for(var k=0;k<obj1.length;k++){
							var varobj = obj1[k];
							var name = varobj["@Name"];
							arr.push(name);
						}
					}else{
						var varobj = obj1["Conception"];
						var name = varobj["@Name"];
						arr.push(name);
					}
					var obj2 = objj["Variables"][i]["Description"];
					var varfor = obj2["Formula"];
					var namefor = "formula"+formulanum+"."+varfor["@ID"]+"."+varfor["@Name"];
					formulanum++;
					arr.push(namefor);
					vconceptarr.push(arr);
				}
				
			}else{
				var obj1 = objj["Variables"]["VariableDes"]["Variable"];
				var arr=[];
				if(obj1 instanceof Array){
					for(var k=0;k<obj1.length;k++){
						var varobj = obj1[k];
						var name = varobj["@Name"];
						arr.push(name);
					}
				}else{
					var varobj = obj1["Conception"];
					var name = varobj["@Name"];
					arr.push(name);
				}
				var obj2 = objj["Variables"]["VariableDes"]["Description"];
				var varfor = obj2["Formula"];
				var namefor = "formula"+formulanum+"."+varfor["@ID"]+"."+varfor["@Name"];
				formulanum++;
				arr.push(namefor);
				vconceptarr.push(arr);
			}
		}
		if(objj["DependentVariables"]!=null){
		if(objj["DependentVariables"]["DependentVariable"] instanceof Array){
			var arr=[];
			for(var i=0;i<objj["DependentVariables"]["DependentVariable"].length;i++){
				var name = objj["DependentVariables"]["DependentVariable"][i]["@Name"];
				arr.push(name);
			}
			dconceptarr.push(arr);
		}else{
			var arr=[];
			var varobj = objj["DependentVariables"]["DependentVariable"]["Conception"];
			var name = varobj["@Name"];
			arr.push(name);
			dconceptarr.push(arr);
		}
		}
		mapVariable[RelationList.data[j]["@Name"]] = vconceptarr;
		mapdependVariable[RelationList.data[j]["@Name"]] = dconceptarr;
	}
}
function infer(){
	existstr.splice(0,existstr.length); 
	getfromoutput();
	getfrominput();
	document.getElementsByTagName("svg").item(0).style.display="block";
	drawsvg22(nodes,links);
}

function getfromoutput(){
	highlevelConceptList = {};
	for(var i=0;i<outputs.length;i++){
		var stack = [];
		over = false;
		if(mapVariable[outputs[i]]!=null){
			var arrlist=mapVariable[outputs[i]];
			judge(outputs[i],arrlist,stack);
			if(over==true){
				filterStrList(stack);
				for(var j=0;j<stack.length;j++){
					var stackss = stack[j].substring(1,stack[j].length-1);
					var stackarr = stackss.split(",");
					var maxSafety = 0;
					if(outputsafety[stackarr[stackarr.length-1]]!=null){
						maxSafety = outputsafety[stackarr[stackarr.length-1]].redundancy;	
					}
					
					var ll=0;
					for(ll=0;ll<ConceptList.data.length;ll++){
						if(ConceptList.data[ll]["@Name"]==stackarr[stackarr.length-1]){
							if(highlevelConceptList[stackarr[stackarr.length-1]]==null){
								highlevelConceptList[stackarr[stackarr.length-1]] = ConceptList.data[ll];
							}
							if(highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0]<maxSafety){
								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSafety;
								break;
							}		
						}
					}
					if(ll==ConceptList.data.length){
						highlevelConceptList[stackarr[stackarr.length-1]]==null;
					}
					for(var k=0;k<stackarr.length-2;k++){
						var ll=0;
						for(ll=0;ll<ConceptList.data.length;ll++){
							if(ConceptList.data[ll]["@Name"]==stackarr[k]){
								if(highlevelConceptList[stackarr[k]]==null){
									highlevelConceptList[stackarr[k]] = ConceptList.data[ll];
								}
								if(highlevelConceptList[stackarr[k]]["property"]["Safety"][0]<maxSafety){
									highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSafety;
									break;
								}		
							}
						}
						if(ll==ConceptList.data.length){
							highlevelConceptList[stackarr[k]]==null;
						}
						
						var link = {};
						var link1 = {};
						var node = {};
						node.name = stackarr[k];
						node.reflexive = false;
						var l=0;
						for(l=0;l<nodes.length;l++){
							if(nodes[l].name==node.name){
								link.source = nodes[l];
								break;
							}
						}
						if(l==nodes.length){
							nodes.push(node);
							link.source = node;
						}
						
						var node1 = {};
						node1.name = stackarr[stackarr.length-2];
						node1.reflexive = false;
						for(l=0;l<nodes.length;l++){
							if(nodes[l].name==node1.name){
								link.target = nodes[l];
								link1.source = nodes[l];
								break;
							}
						}
						if(l==nodes.length){
							nodes.push(node1);
							link.target = node1;
							link1.source = node1;
						}
						link.left = false;
						link.right = true;
						link.value = 20;
						links.push(link);
						
						
						var node2 = {};
						node2.name = stackarr[stackarr.length-1];
						node2.reflexive = false;
						for(l=0;l<nodes.length;l++){
							if(nodes[l].name==node2.name){
								link1.target = nodes[l];
								break;
							}
						}
						if(l==nodes.length){
							nodes.push(node2);
							link1.target = node2;
						}
						link1.left = false;
						link1.right = true;
						link1.value = 20;
						links.push(link1);
						
					}
				}
			}
		}else{
			Log("不可达到");
		}
	}
}
function getfrominput(){
	for(var i=0;i<inputs.length;i++){
		var stack1 = [];
		if(inputnum[inputs[i]]==0){//有剩余输入
			Log("剩余输入："+inputs[i]);
			over1 = false;
			if(mapdependVariable[inputs[i]]!=null){
				var arrlist = mapdependVariable[inputs[i]];
				judge1(inputs[i],arrlist,stack1);
				if(over1==true){
					filterStrList(stack1);
					for(var j=0;j<stack1.length;j++){
						var stackss = stack1[j].substring(1,stack1[j].length-1);
						var stackarr = stackss.split(",");
						var maxSecurity = 0;
						for(var k=0;k<stackarr.length-2;k++){
							if(inputsecurity[stackarr[k]].redundancy>maxSecurity){
								maxSecurity = inputsecurity[stackarr[k]].redundancy;
							}
							var ll=0;
							for(ll=0;ll<ConceptList.data.length;ll++){
								if(ConceptList.data[ll]["@Name"]==stackarr[k]){
									if(highlevelConceptList[stackarr[k]]==null){
										highlevelConceptList[stackarr[k]] = ConceptList.data[ll];
									}
									if(highlevelConceptList[stackarr[k]]["property"]["Safety"][0]<maxSecurity){
										highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSecurity;
										break;
									}		
								}
								
							}
							if(ll==ConceptList.data.length){
								highlevelConceptList[stackarr[k]]==null;
							}
							
							var link = {};
							var link1 = {};
							var node = {};
							node.name = stackarr[k];
							node.reflexive = false;
							var l=0;
							for(l=0;l<nodes.length;l++){
								if(nodes[l].name==node.name){
									link.source = nodes[l];
									break;
								}
							}
							if(l==nodes.length){
								nodes.push(node);
								link.source = node;
							}
							
							var node1 = {};
							node1.name = stackarr[stackarr.length-2];
							node1.reflexive = false;
							for(l=0;l<nodes.length;l++){
								if(nodes[l].name==node1.name){
									link.target = nodes[l];
									link1.source = nodes[l];
									break;
								}
							}
							if(l==nodes.length){
								nodes.push(node1);
								link.target = node1;
								link1.source = node1;
							}
							link.left = false;
							link.right = true;
							link.value = 20;
							links.push(link);
							
							
							var node2 = {};
							node2.name = stackarr[stackarr.length-1];
							node2.reflexive = false;
							for(l=0;l<nodes.length;l++){
								if(nodes[l].name==node2.name){
									link1.target = nodes[l];
									break;
								}
							}
							if(l==nodes.length){
								nodes.push(node2);
								link1.target = node2;
							}
							link1.left = false;
							link1.right = true;
							link1.value = 20;
							links.push(link1);
						
					}
					var ll=0;
					for(ll=0;ll<ConceptList.data.length;ll++){
						if(ConceptList.data[ll]["@Name"]==stackarr[stackarr.length-1]){
							if(highlevelConceptList[stackarr[stackarr.length-1]]==null){
								highlevelConceptList[stackarr[stackarr.length-1]] = ConceptList.data[ll];
							}
							if(highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0]<maxSecurity){
								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSecurity;
								break;
							}		
						}
					}
					if(ll==ConceptList.data.length){
						highlevelConceptList[stackarr[stackarr.length-1]]==null;
					}
						System.out.println("stack:"+stack1.get(j));
					}
				}
			}else{
				Log("没有"+inputs[i]);
			}
		}
	}
}
function judge(dep,arr,stack){
	var i=0;
	var bool=false;
	var bool1 = false;
	for(i=0;i<arr.length;i++){
		var stackstr = "{";
		var arr1 = arr[i];
		var j;
		var newarr=[];
		for(j=0;j<arr1.length-1;j++){//arr1.length-1是公式name
			var o = mapVariable[arr1[j]];
			if(o==null||o.length==0){
				bool = true;
			}else{
				bool1 = true;
			}
		}
		if(bool&&!bool1){//没有依赖，均为最初输入
			for(j=0;j<arr1.length-1;j++){
				var k;
				for(k=0;k<inputs.length;){
					if(inputs[k]==arr1[j]||inputs[k]==(arr1[j]+" Input")||inputs[k]==(arr1[j]+" Data Input")){
						inputnum[inputs[k]] = 1;
						break;
					}else{
						k++;
					}
				}
				if(k==inputs.length){//给定的输入中没有最初输入,此条路径断了。
					Log("输入中缺少"+arr1[j]);
					continue;
				}else{
					
					stackstr+= arr1[j]+",";
				}
			}
			if(j==arr1.length-1){//给定输入中均有最初输入，此路径正确
				stackstr+=arr1[arr1.length-1]+",";
				stackstr+=dep+"}";
				Log("stackstr:"+stackstr);
				stack.push(stackstr);
				over = true;
				
				continue;
			}
		}else if(bool&&bool1){//有最初输入和中间
			for(j=0;j<arr1.length-1;j++){
				if(mapVariable[arr1[j]]==null){//为最初输入
					var k;
					for(k=0;k<inputs.length;){
						if(inputs[k]==(arr1[j])||inputs[k]==(arr1[j]+" Input")||inputs[k]==(arr1[j]+" Data Input")){
							inputnum[inputs[k]] = 1;
							break;
						}else{
							k++;
						}
					}
					if(k==inputs.length){//给定的输入中没有最初输入,此条路径断了。
						Log("输入中缺少"+arr1[j]);
						continue;
					}else{
						stackstr+=arr1[j]+",";
					}
				}else{//为中间节点
					existstr.push(arr1[j]);
					newarr.push(arr1[j]);
					stackstr+=arr1[j]+",";
				}
			}
			if(j==arr1.length-1){//给定的输入中均有最初输入，此条路径正确但未完全。
				stackstr+=arr1[arr1.length-1]+",";
				stackstr+=dep+"}";
				Log("stackstr:"+stackstr);
				stack.push(stackstr);
			}
			for(j=0;j<newarr.length;j++){
				judge(newarr[j],mapVariable[newarr[j]],stack);	
			}
		}else{//均为中间
			for(var ll=0;ll<arr1.length-1;ll++){
				newarr[ll] = arr1[ll];
			}
			for(j=0;j<arr1.length-1;j++){
				existstr.push(arr1[j]);
				stackstr+=arr1[j]+",";
			}
			stackstr+=arr1[arr1.length-1]+",";
			stackstr+=dep+"}";
			Log("stackstr"+stackstr);
			stack.push(stackstr);
			for(j=0;j<newarr.length;j++){
				judge(newarr[j],mapVariable[newarr[j]],stack);	
			}
		}
	}
}
function judge1(dep,arr,stack){
	var i=0;
	for(i=0;i<arr.length;i++){//arr表示dep的所有的dependentVariable
		
		var arr1 = arr[i];//arr1表示dep的dependentVariables
		var j;
		for(j=0;j<arr1.length;j++){
			var inputarr = mapVariable[arr1[j]];//表示每个dependentVariable对应的Variable
			var k;
			for(k=0;k<inputarr.length;k++){
				var stackstr = "{";
				var arr2 = inputarr[k];
				var l;
				for(l=0;l<arr2.length;l++){//arr2表示Variables
					var o = mapVariable[arr2[l]];
					if((o!=null)
							&&!(exist(arr2[l],existstr)||existinputs(arr2[l],inputs))){//依赖知识是中间节点且不在给定输入或已生成的中间节点中
						over = false;
						var stack1 = [];
						judge(arr2[l],o,stack1);
						if(over==false){
							Log(arr2[l]+"不能生成");
							break;
						}else{
							existstr.push(arr2[l]);
						}
						
					}else if((o==null)&&!(exist(arr2[l],existstr)||existinputs(arr2[l],inputs))){//依赖知识初始输入且不在给定输入或已生成的中间节点中
						Log(arr2[l]+"不在给定输入或已生成的节点中");
						break;
					}
				}
				if(l==arr2.length){//要么是在给定输入要么是在已生成节点中;
					for(l=0;l<arr2.length;l++){
						stackstr+=arr2[l]+",";
					}
					stackstr+=arr1[j]+"}";
					Log("stackstr"+stackstr);
					var obj = mapdependVariable[arr1[j]];
					if(obj==null||obj.length==0){//为最终输出
						Log("成功");
						over1 = true;
					}else{//不是最终输入
						judge1(arr1[j],obj,stack);
					}
				}
			}
		}
	}
}
function exist(s,list){//判断s是否在list中
	for(var i=0;i<list.length;i++){
		if(s==(list[i])){
			return true;
		}
	}
	return false;
}
function existinputs(s,list){
	for(var i=0;i<list.length;i++){
		if(list[i]==(s)||list[i]==(s+" Input")||list[i]==(s+" Data Input")){
			return true;
		}
	}
	return false;
}


function filterStrList(list){//过滤掉重复的偶对
	for(var i=0;i<list.length;i++){
		for(var j=i+1;j<list.length;){
			if(list[j]==list[i]){
				list.splice(list.indexOf(list[j]), 1);
				
			}else{
				j++;
			}
		}
	}
}
function Verify(){
	
}