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
var performance = {};
var highlevelConceptList = {};
var maxSafety = 0;
var maxSecurity = 0;
var maxRealtime = 0;

var stacks = [];
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
	  	        for(var j=0;j<RelationList.data.length;j++){
	  	    	  if(name == RelationList.data[j]["@Name"]||(name==(RelationList.data[j]["@Name"]+" Input"))||(name==(RelationList.data[j]["@Name"]+" Data Input"))){
	  	    		  id = RelationList.data[j]["@ID"];
	  	    		  break;
	  	    	  }
	  	      }
	  	        var obj="{}";
	  	        obj = stringToJson(obj);
	  	        obj.id = id; obj.name = name;obj.content = contents;
	  	        requirements.push(obj);
	  	        var perform = requirement.getElementsByTagName("Performance");
	  	      var perf={};
    			for(var k=0;k<perform[0].childNodes.length;k++){
    				if(perform[0].childNodes[k].nodeName=="Accuracy"){
    					var accuracy = perform[0].childNodes[k].childNodes[0].data;
    					perf.accuracy = accuracy;
    				}
    				if(perform[0].childNodes[k].nodeName=="RefreshRate"){
    					var refreshrate = perform[0].childNodes[k].childNodes[0].data;
    					perf.refreshrate = refreshrate;
    				}
    				if(perform[0].childNodes[k].nodeName=="Range"){
    					var range = perform[0].childNodes[k].childNodes[0].data;
    					perf.range = range;
    				}
    				if(perform[0].childNodes[k].nodeName=="SigBits"){
    					var sigbits = perform[0].childNodes[k].childNodes[0].data;
    					perf.sigbits = sigbits;
    				}
    				if(perform[0].childNodes[k].nodeName=="MaxTransDelay"){
    					var maxtransdelay = perform[0].childNodes[k].childNodes[0].data;
    					perf.maxtransdelay = maxtransdelay;
    				}
    			}
	  	      performance[id+"."+name] = perf;
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
	  	        			inputsecurity[id+"."+name] = safety;
	  	        			inputs.push(id+"."+name);
	  	        			inputnum[id+"."+name] = 0;
	  	        			
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
	  	        			outputsafety[id+"."+name] = safety;
	  	        			outputs.push(id+"."+name);
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
							var id = varobj["@ID"];
							var name = varobj["@Name"];
							var str = id+"."+name;
							arr.push(str);
						}
					}else{
						var varobj = obj1["Conception"];
						var id = varobj["@ID"];var name = varobj["@Name"];
						var str = id+"."+name;
						arr.push(str);
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
						var id = varobj["@ID"];
						var name = varobj["@Name"];
						var str = id+"."+name;
						arr.push(str);
					}
				}else{
					var varobj = obj1["Conception"];
					var id = varobj["@ID"];
					var name = varobj["@Name"];
					var str = id+"."+name;
					arr.push(str);
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
				var id = objj["DependentVariables"]["DependentVariable"][i]["@ID"];
				var name = objj["DependentVariables"]["DependentVariable"][i]["@Name"];
				var str = id+"."+name;
				arr.push(str);
			}
			dconceptarr.push(arr);
		}else{
			var arr=[];
			var varobj = objj["DependentVariables"]["DependentVariable"]["Conception"];
			var id = varobj["@ID"];
			var name = varobj["@Name"];
			var str = id+"."+name;
			arr.push(str);
			dconceptarr.push(arr);
		}
		}
		mapVariable[RelationList.data[j]["@ID"]+"."+RelationList.data[j]["@Name"]] = vconceptarr;
		mapdependVariable[RelationList.data[j]["@ID"]+"."+RelationList.data[j]["@Name"]] = dconceptarr;
	}
}
function infer(){
	existstr.splice(0,existstr.length); 
	getfromoutput();
	getfrominput();
	document.getElementById("math").style.display="none";
	document.getElementsByTagName("svg").item(0).style.display="block";
	Log(jsonToString(nodes));
	Log(jsonToString(links));
	drawsvg21(nodes,links);
	
	filterStrList(stacks);
	var highlevelreqStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n<formulas>";
	for(var i=0;i<stacks.length;i++){
		var stackss = stacks[i].substring(1,stacks[i].length-1);
		var stacksarr = stackss.split(",");
		var formulastr = "<formula id=\""+stacksarr[stacksarr.length-2]+"\">\n";
		for(var j=0;j<stacksarr.length-2;j++){
			formulastr+="<input>"+stacksarr[j]+"</input>\n";
		}
		formulastr+="<output>"+stacksarr[stacksarr.length-1]+"</output>\n</formula>\n";
		highlevelreqStr+= formulastr;
	}
	highlevelreqStr+="</formulas>";
	Send("highlevelreqStr:"+highlevelreqStr);
//	alert("highlevelreqStr:"+highlevelreqStr);
}

function getfromoutput(){
	highlevelConceptList = {};
//	var stacks = [];
	for(var i=0;i<outputs.length;i++){
		var stack = [];
		over = false;
		maxRealtime = performance[outputs[i]].refreshrate;
		if(mapVariable[outputs[i]]!=null){
			var arrlist=mapVariable[outputs[i]];
			judge(outputs[i],arrlist,stack);
			if(over==true){
				
				maxSafety = outputsafety[outputs[i]].redundancy;
				filterStrList(stack);
				for(var j=0;j<stack.length;j++){
					stacks.push(stack[j]);
					Log("stack:"+stack[j]);
					var stackss = stack[j].substring(1,stack[j].length-1);
					var stackarr = stackss.split(",");
//					var maxSafety = 0;
//					if(outputsafety[stackarr[stackarr.length-1]]!=null){
//						maxSafety = outputsafety[stackarr[stackarr.length-1]].redundancy;	
//					}
					
					var ll=0;
					for(ll=0;ll<ConceptList.data.length;ll++){
						if((ConceptList.data[ll]["@ID"]+"."+ConceptList.data[ll]["@Name"])==stackarr[stackarr.length-1]){
							if(highlevelConceptList[stackarr[stackarr.length-1]]==null){
								highlevelConceptList[stackarr[stackarr.length-1]] = ConceptList.data[ll];
								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSafety;
								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSecurity;
								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Performance"]["RefreshRate"] = maxRealtime;
							}
						}
					}
					if(ll==ConceptList.data.length){
						highlevelConceptList[stackarr[stackarr.length-1]]==null;
					}
					for(var k=0;k<stackarr.length-2;k++){
//						if(outputsafety[stackarr[k]].redundancy>maxSafety){
//							maxSafety = outputsafety[stackarr[k]].redundancy;
//						}
						var ll=0;
						for(ll=0;ll<ConceptList.data.length;ll++){
							if((ConceptList.data[ll]["@ID"]+"."+ConceptList.data[ll]["@Name"])==stackarr[k]){
								if(highlevelConceptList[stackarr[k]]==null){
									highlevelConceptList[stackarr[k]] = ConceptList.data[ll];
									highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSafety;
									highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSecurity;
									highlevelConceptList[stackarr[k]]["property"]["Performance"]["RefreshRate"] = maxRealtime;
								}
//								highlevelConceptList.stackarr[k] = ConceptList.data[ll]; 
//								if(highlevelConceptList[stackarr[k]]["property"]["Safety"][0]<maxSafety){
//									highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSafety;
//									break;
//								}		
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
					
//					for(var ll=0;ll<ConceptList.data.length;ll++){
//						if(ConceptList.data[ll]["@Name"]==stackarr[stackarr.length-1]){
//							if(ConceptList.data[ll]["Safety"]["Redundancy"]<maxSafety){
//								ConceptList.data[ll]["Safety"]["Redundancy"] = maxSafety;
//								break;
//							}		
//						}
//					}
					
//					System.out.println("stack:"+stack.get(j));
				}
//				for(int j=stack.size()-1;j>=0;j--){
//					System.out.println("stack:"+stack.get(j));
//				}
			}
			
		}else{
			Log("不可达到");
		}
		maxSafety = 0;
		maxSecurity = 0;
		maxRealtime = 0;
	}
	
}
function getfrominput(){
//	var stacks1 = [];
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
						stacks.push(stack1[j]);
						Log("stack1:"+stack1[j]);
						var stackss = stack1[j].substring(1,stack1[j].length-1);
						var stackarr = stackss.split(",");
						
						var ll=0;
						for(ll=0;ll<ConceptList.data.length;ll++){
							if((ConceptList.data[ll]["@ID"]+"."+ConceptList.data[ll]["@Name"])==stackarr[stackarr.length-1]){
								if(highlevelConceptList[stackarr[stackarr.length-1]]==null){
									highlevelConceptList[stackarr[stackarr.length-1]] = ConceptList.data[ll];
									highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSafety;
									highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSecurity;
									highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Performance"]["RefreshRate"] = maxRealtime;
								}
							}
						}
						if(ll==ConceptList.data.length){
							highlevelConceptList[stackarr[stackarr.length-1]]==null;
						}
//						var maxSecurity = 0;
						for(var k=0;k<stackarr.length-2;k++){
//							if(inputsecurity[stackarr[k]].redundancy>maxSecurity){
//								maxSecurity = inputsecurity[stackarr[k]].redundancy;
//							}
//							var ll=0;
//							for(ll=0;ll<ConceptList.data.length;ll++){
//								if(ConceptList.data[ll]["@Name"]==stackarr[k]){
//									if(highlevelConceptList[stackarr[k]]==null){
//										highlevelConceptList[stackarr[k]] = ConceptList.data[ll];
//									}
//									if(highlevelConceptList[stackarr[k]]["property"]["Safety"][0]<maxSecurity){
//										highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSecurity;
//										break;
//									}		
//								}
//								
//							}
//							if(ll==ConceptList.data.length){
//								highlevelConceptList[stackarr[k]]==null;
//							}
							var ll=0;
							for(ll=0;ll<ConceptList.data.length;ll++){
								if((ConceptList.data[ll]["@ID"]+"."+ConceptList.data[ll]["@Name"])==stackarr[k]){
									if(highlevelConceptList[stackarr[k]]==null){
										highlevelConceptList[stackarr[k]] = ConceptList.data[ll];
										highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSafety;
										highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSecurity;
										highlevelConceptList[stackarr[k]]["property"]["Performance"]["RefreshRate"] = maxRealtime;
									}
//									highlevelConceptList.stackarr[k] = ConceptList.data[ll]; 
//									if(highlevelConceptList[stackarr[k]]["property"]["Safety"][0]<maxSafety){
//										highlevelConceptList[stackarr[k]]["property"]["Safety"][0] = maxSafety;
//										break;
//									}		
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
//					var ll=0;
//					for(ll=0;ll<ConceptList.data.length;ll++){
//						if(ConceptList.data[ll]["@Name"]==stackarr[stackarr.length-1]){
//							if(highlevelConceptList[stackarr[stackarr.length-1]]==null){
//								highlevelConceptList[stackarr[stackarr.length-1]] = ConceptList.data[ll];
//							}
//							if(highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0]<maxSecurity){
//								highlevelConceptList[stackarr[stackarr.length-1]]["property"]["Safety"][0] = maxSecurity;
//								break;
//							}		
//						}
//					}
//					if(ll==ConceptList.data.length){
//						highlevelConceptList[stackarr[stackarr.length-1]]==null;
//					}
//						Log("stack1:"+stack1[j]);
//						System.out.println("stack:"+stack1.get(j));
					}
//					filterStrList(stack1);
//					for(int j=0;j<stack.size();j++){
//						System.out.println("stack:"+stack.get(j));
//					}
//					for(int j=stack.size()-1;j>=0;j--){
//						System.out.println("stack:"+stack.get(j));
//					}
				}
			}else{
				Log("没有"+inputs[i]);
			}
		}
		maxSafety = 0;
		maxSecurity = 0;
		maxRealtime = 0;
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
						if(inputsecurity[inputs[k]].redundancy>maxSecurity){
							maxSecurity = inputsecurity[inputs[k]].redundancy;
						}
						if(performance[inputs[k]].refreshrate>maxRealtime){
							maxRealtime = performance[inputs[k]].refreshrate;
						}
						break;
					}else{
						k++;
					}
				}
				if(k==inputs.length){//给定的输入中没有最初输入,此条路径断了。
					Log("输入中缺少"+arr1[j]);
					maxSecurity = 0;
					continue;
				}else{
					
					stackstr+= arr1[j]+",";
				}
			}
			if(j==arr1.length-1){//给定输入中均有最初输入，此路径正确
				stackstr+=arr1[arr1.length-1]+",";
				stackstr+=dep+"}";
//				Log("stackstr:"+stackstr);
				stack.push(stackstr);
				over = true;
				
				continue;
			}
		}else if(bool&&bool1){//有最初输入和中间
			for(j=0;j<arr1.length-1;j++){
				if(mapVariable[arr1[j]]==null){//为最初输入
					var k;
					for(k=0;k<inputs.length;){
						if(inputs[k]==arr1[j]||inputs[k]==(arr1[j]+" Input")||inputs[k]==(arr1[j]+" Data Input")){
							inputnum[inputs[k]] = 1;
							if(inputsecurity[inputs[k]].redundancy>maxSecurity){
								maxSecurity = inputsecurity[inputs[k]].redundancy;
							}
							if(performance[inputs[k]].refreshrate>maxRealtime){
								maxRealtime = performance[inputs[k]].refreshrate;
							}
							break;
						}else{
							k++;
						}
					}
					if(k==inputs.length){//给定的输入中没有最初输入,此条路径断了。
						Log("输入中缺少"+arr1[j]);
						maxSecurity = 0;
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
//				Log("stackstr:"+stackstr);
				stack.push(stackstr);
			}
			for(j=0;j<newarr.length;j++){
				judge(newarr[j],mapVariable[newarr[j]],stack);	
			}
		}else{//均为中间
			for(var ll=0;ll<arr1.length-1;ll++){
				newarr[ll] = arr1[ll];
			}
//			newarr = arr1;
			for(j=0;j<arr1.length-1;j++){
				existstr.push(arr1[j]);
				stackstr+=arr1[j]+",";
			}
			stackstr+=arr1[arr1.length-1]+",";
			stackstr+=dep+"}";
//			Log("stackstr"+stackstr);
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
						maxSafety = outputsafety[arr1[j]].redundancy;
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
		if(list[i]==s||list[i]==(s+" Input")||list[i]==(s+" Data Input")){
			if(inputsecurity[list[i]].redundancy>maxSecurity){
				maxSecurity = inputsecurity[list[i]].redundancy;
			}
			if(performance[list[i]].refreshrate>maxRealtime){
				maxRealtime = performance[list[i]].refreshrate;
			}
			return true;
		}
	}
	return false;
}


function filterStrList(list){//过滤掉重复的偶对
//	ArrayList<String> ls = new ArrayList<String>();
	for(var i=0;i<list.length;i++){
		for(var j=i+1;j<list.length;){
			if(list[j]==list[i]){
				list.splice(list.indexOf(list[j]), 1);
//				list.remove(list[j]);
				
			}else{
				j++;
			}
		}
	}
}
