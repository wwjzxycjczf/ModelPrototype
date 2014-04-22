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
//	document.getElementById('fknowledge').addEventListener('change', readknowledge, false);
//   	document.getElementById('files').addEventListener('change', readsvgrequirementfolder, false);
//   	document.getElementById('fmathml').addEventListener('change', readknowledge, false);
//	Input();
//	createSVGVML(document.getElementById("svg"));  

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
	send(username+":logout");
    Log("close", "ERROR");
//    deleteAllMarkers();
//    radarlist=null;
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
	
//	Send("request_graphjson");
}
function WSonMessage(event) {
	
    var data = event.data;
//    document.getElementById("content").innerText =  document.getElementById("content").innerText+data+"</br>";
//    document.write(data);
//    Log(data,"OK");
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
//    	if(data.name!=username){
//    		userLog(data.name,"OK");
//    	}
//    	Log(data.name+" login","OK");
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
//    		userLog(userlist[i],"OK");
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
//		var conceptid = data.concept.substring(0,data.concept.indexOf(":"));
//		var conceptname = data.concept.substring(data.concept.indexOf(":") + 1);
//		for(var i=0;i<ConceptList.data.length;i++){
//			if(ConceptList.data[i]["@ID"]==conceptid&&ConceptList.data[i]["@Name"]==conceptname){
//				ConceptList.data.remove(ConceptList.data[i]);
//				break;
//			};
//		};
		
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
//		var rulestr = data.rulestr;
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
//	    	 RelationShow();
		}
	}else if(flag==2){
		if (stringToJson(ConceptList) != data) {
			ConceptList = data;
//	        RShow();
		} 
	}else if(flag==3){
		if (stringToJson(RuleList) != data) {
			RuleList = data;
//	        RShow();
		}
	
	}else if(flag==4){
//		if(GraphJson=="{}"){
//			GraphJson = data;
//			
//			var links = GraphJson.arrows;
//
//			var nodes = [];
//			var reallinks=[];
//			links.forEach(function(link) {
////			link.type = "licensing";  
////				link.left = false;
////				link.right = true;
//				var reallink = {};
//				
//				var node={};
//				node.name = link.source;
//				node.reflexive = false;
//				var i=0;
//				for(i=0;i<nodes.length;i++){
//					if(nodes[i].name==node.name){
//						reallink.source = nodes[i];
//						break;
//					}
//				}
//				if(i==nodes.length){
//					nodes.push(node);
//					reallink.source = node;
//				}
//				var node1={};
//				node1.name = link.target;
//				node1.reflexive = false;
////				var i=0;
//				for(i=0;i<nodes.length;i++){
//					if(nodes[i].name==node1.name){
//						reallink.target = nodes[i];
//						break;
//					}
//				}
//				if(i==nodes.length){
//					nodes.push(node1);
//					reallink.target = node1;
//				}
//				reallink.left = false;
//				reallink.right = true;
//				reallinks.push(reallink);
////			link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
////			  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
////			nodes[link.source.reflexive]=false;
////			nodes[link.target.reflexive]=false;
//			
//			});
//			drawsvg2(nodes,reallinks);
////			GraphJson={"name":"root","children":[{"name":"ext","children":[{"name":"twig","children":[{"name":"twig.c","size":763,"language":"C"},{"name":"php_twig.h","size":11,"language":"C/C++ Header"},{"name":"config.m4","size":5,"language":"m4"}],"size":779}],"size":779},{"name":"lib","children":[{"name":"Twig","children":[{"name":"Extension","children":[{"name":"Core.php","size":705,"language":"PHP"},{"name":"Sandbox.php","size":73,"language":"PHP"},{"name":"Staging.php","size":62,"language":"PHP"},{"name":"Escaper.php","size":45,"language":"PHP"},{"name":"Debug.php","size":40,"language":"PHP"},{"name":"StringLoader.php","size":31,"language":"PHP"},{"name":"Optimizer.php","size":17,"language":"PHP"}],"size":973},{"name":"Environment.php","size":640,"language":"PHP"},{"name":"ExpressionParser.php","size":462,"language":"PHP"},{"name":"Lexer.php","size":310,"language":"PHP"},{"name":"Node","children":[{"name":"Module.php","size":287,"language":"PHP"},{"name":"Expression","children":[{"name":"Call.php","size":138,"language":"PHP"},{"name":"Name.php","size":66,"language":"PHP"},{"name":"Array.php","size":59,"language":"PHP"},{"name":"GetAttr.php","size":34,"language":"PHP"},{"name":"MethodCall.php","size":29,"language":"PHP"},{"name":"BlockReference.php","size":28,"language":"PHP"},{"name":"Test","children":[{"name":"Defined.php","size":27,"language":"PHP"},{"name":"Sameas.php","size":14,"language":"PHP"},{"name":"Divisibleby.php","size":14,"language":"PHP"},{"name":"Constant.php","size":14,"language":"PHP"},{"name":"Even.php","size":13,"language":"PHP"},{"name":"Odd.php","size":13,"language":"PHP"},{"name":"Null.php","size":12,"language":"PHP"}],"size":107},{"name":"Parent.php","size":25,"language":"PHP"},{"name":"Function.php","size":23,"language":"PHP"},{"name":"Filter.php","size":23,"language":"PHP"},{"name":"Binary.php","size":23,"language":"PHP"},{"name":"Conditional.php","size":20,"language":"PHP"},{"name":"Test.php","size":20,"language":"PHP"},{"name":"Filter","children":[{"name":"Default.php","size":20,"language":"PHP"}],"size":20},{"name":"Binary","children":[{"name":"Power.php","size":18,"language":"PHP"},{"name":"In.php","size":18,"language":"PHP"},{"name":"NotIn.php","size":18,"language":"PHP"},{"name":"Range.php","size":18,"language":"PHP"},{"name":"FloorDiv.php","size":14,"language":"PHP"},{"name":"Or.php","size":8,"language":"PHP"},{"name":"GreaterEqual.php","size":8,"language":"PHP"},{"name":"BitwiseXor.php","size":8,"language":"PHP"},{"name":"Mul.php","size":8,"language":"PHP"},{"name":"Greater.php","size":8,"language":"PHP"},{"name":"Sub.php","size":8,"language":"PHP"},{"name":"Add.php","size":8,"language":"PHP"},{"name":"Equal.php","size":8,"language":"PHP"},{"name":"And.php","size":8,"language":"PHP"},{"name":"BitwiseAnd.php","size":8,"language":"PHP"},{"name":"Div.php","size":8,"language":"PHP"},{"name":"Mod.php","size":8,"language":"PHP"},{"name":"Concat.php","size":8,"language":"PHP"},{"name":"BitwiseOr.php","size":8,"language":"PHP"},{"name":"LessEqual.php","size":8,"language":"PHP"},{"name":"NotEqual.php","size":8,"language":"PHP"},{"name":"Less.php","size":8,"language":"PHP"}],"size":222},{"name":"Unary.php","size":18,"language":"PHP"},{"name":"TempName.php","size":16,"language":"PHP"},{"name":"Constant.php","size":12,"language":"PHP"},{"name":"ExtensionReference.php","size":12,"language":"PHP"},{"name":"AssignName.php","size":12,"language":"PHP"},{"name":"Unary","children":[{"name":"Not.php","size":8,"language":"PHP"},{"name":"Pos.php","size":8,"language":"PHP"},{"name":"Neg.php","size":8,"language":"PHP"}],"size":24}],"size":931},{"name":"For.php","size":75,"language":"PHP"},{"name":"Include.php","size":69,"language":"PHP"},{"name":"Macro.php","size":68,"language":"PHP"},{"name":"Set.php","size":65,"language":"PHP"},{"name":"If.php","size":41,"language":"PHP"},{"name":"SandboxedModule.php","size":37,"language":"PHP"},{"name":"ForLoop.php","size":32,"language":"PHP"},{"name":"Import.php","size":27,"language":"PHP"},{"name":"Sandbox.php","size":26,"language":"PHP"},{"name":"SandboxedPrint.php","size":24,"language":"PHP"},{"name":"SetTemp.php","size":24,"language":"PHP"},{"name":"Block.php","size":21,"language":"PHP"},{"name":"Embed.php","size":20,"language":"PHP"},{"name":"Print.php","size":17,"language":"PHP"},{"name":"Do.php","size":17,"language":"PHP"},{"name":"Spaceless.php","size":17,"language":"PHP"},{"name":"Text.php","size":17,"language":"PHP"},{"name":"Flush.php","size":15,"language":"PHP"},{"name":"BlockReference.php","size":15,"language":"PHP"},{"name":"AutoEscape.php","size":12,"language":"PHP"},{"name":"Body.php","size":4,"language":"PHP"},{"name":"Expression.php","size":4,"language":"PHP"}],"size":1865},{"name":"Parser.php","size":278,"language":"PHP"},{"name":"Template.php","size":226,"language":"PHP"},{"name":"NodeVisitor","children":[{"name":"Optimizer.php","size":145,"language":"PHP"},{"name":"SafeAnalysis.php","size":109,"language":"PHP"},{"name":"Escaper.php","size":107,"language":"PHP"},{"name":"Sandbox.php","size":44,"language":"PHP"}],"size":405},{"name":"Compiler.php","size":137,"language":"PHP"},{"name":"Token.php","size":134,"language":"PHP"},{"name":"Node.php","size":120,"language":"PHP"},{"name":"Loader","children":[{"name":"Filesystem.php","size":118,"language":"PHP"},{"name":"Chain.php","size":81,"language":"PHP"},{"name":"Array.php","size":44,"language":"PHP"},{"name":"String.php","size":20,"language":"PHP"}],"size":263},{"name":"Error.php","size":118,"language":"PHP"},{"name":"Test","children":[{"name":"IntegrationTestCase.php","size":110,"language":"PHP"},{"name":"NodeTestCase.php","size":37,"language":"PHP"},{"name":"Method.php","size":17,"language":"PHP"},{"name":"Node.php","size":17,"language":"PHP"},{"name":"Function.php","size":15,"language":"PHP"}],"size":196},{"name":"Sandbox","children":[{"name":"SecurityPolicy.php","size":88,"language":"PHP"},{"name":"SecurityPolicyInterface.php","size":7,"language":"PHP"},{"name":"SecurityError.php","size":4,"language":"PHP"}],"size":99},{"name":"TokenParser","children":[{"name":"For.php","size":83,"language":"PHP"},{"name":"If.php","size":49,"language":"PHP"},{"name":"Block.php","size":43,"language":"PHP"},{"name":"AutoEscape.php","size":40,"language":"PHP"},{"name":"Include.php","size":36,"language":"PHP"},{"name":"Set.php","size":36,"language":"PHP"},{"name":"Use.php","size":36,"language":"PHP"},{"name":"From.php","size":34,"language":"PHP"},{"name":"Macro.php","size":32,"language":"PHP"},{"name":"Embed.php","size":29,"language":"PHP"},{"name":"Sandbox.php","size":29,"language":"PHP"},{"name":"Filter.php","size":24,"language":"PHP"},{"name":"Spaceless.php","size":20,"language":"PHP"},{"name":"Extends.php","size":20,"language":"PHP"},{"name":"Import.php","size":17,"language":"PHP"},{"name":"Do.php","size":14,"language":"PHP"},{"name":"Flush.php","size":13,"language":"PHP"}],"size":555},{"name":"TokenParserBroker.php","size":76,"language":"PHP"},{"name":"SimpleFilter.php","size":68,"language":"PHP"},{"name":"TokenStream.php","size":67,"language":"PHP"},{"name":"SimpleFunction.php","size":58,"language":"PHP"},{"name":"Filter.php","size":54,"language":"PHP"},{"name":"NodeTraverser.php","size":46,"language":"PHP"},{"name":"Function.php","size":44,"language":"PHP"},{"name":"Extension.php","size":35,"language":"PHP"},{"name":"SimpleTest.php","size":27,"language":"PHP"},{"name":"Markup.php","size":19,"language":"PHP"},{"name":"Autoloader.php","size":18,"language":"PHP"},{"name":"Filter","children":[{"name":"Node.php","size":17,"language":"PHP"},{"name":"Method.php","size":17,"language":"PHP"},{"name":"Function.php","size":15,"language":"PHP"}],"size":49},{"name":"Function","children":[{"name":"Node.php","size":17,"language":"PHP"},{"name":"Method.php","size":17,"language":"PHP"},{"name":"Function.php","size":15,"language":"PHP"}],"size":49},{"name":"Test.php","size":16,"language":"PHP"},{"name":"ExtensionInterface.php","size":13,"language":"PHP"},{"name":"FilterInterface.php","size":12,"language":"PHP"},{"name":"TemplateInterface.php","size":10,"language":"PHP"},{"name":"FunctionInterface.php","size":10,"language":"PHP"},{"name":"TokenParser.php","size":9,"language":"PHP"},{"name":"Error","children":[{"name":"Loader.php","size":8,"language":"PHP"},{"name":"Syntax.php","size":4,"language":"PHP"},{"name":"Runtime.php","size":4,"language":"PHP"}],"size":16},{"name":"TokenParserInterface.php","size":7,"language":"PHP"},{"name":"TokenParserBrokerInterface.php","size":7,"language":"PHP"},{"name":"NodeVisitorInterface.php","size":7,"language":"PHP"},{"name":"NodeInterface.php","size":7,"language":"PHP"},{"name":"LoaderInterface.php","size":7,"language":"PHP"},{"name":"CompilerInterface.php","size":6,"language":"PHP"},{"name":"TestInterface.php","size":5,"language":"PHP"},{"name":"LexerInterface.php","size":5,"language":"PHP"},{"name":"FunctionCallableInterface.php","size":5,"language":"PHP"},{"name":"TestCallableInterface.php","size":5,"language":"PHP"},{"name":"FilterCallableInterface.php","size":5,"language":"PHP"},{"name":"ParserInterface.php","size":5,"language":"PHP"},{"name":"ExistsLoaderInterface.php","size":5,"language":"PHP"},{"name":"NodeOutputInterface.php","size":4,"language":"PHP"}],"size":7557}],"size":7557},{"name":"test","children":[{"name":"Twig","children":[{"name":"Tests","children":[{"name":"TemplateTest.php","size":425,"language":"PHP"},{"name":"escapingTest.php","size":251,"language":"PHP"},{"name":"ExpressionParserTest.php","size":225,"language":"PHP"},{"name":"LexerTest.php","size":212,"language":"PHP"},{"name":"EnvironmentTest.php","size":186,"language":"PHP"},{"name":"Node","children":[{"name":"ForTest.php","size":169,"language":"PHP"},{"name":"ModuleTest.php","size":136,"language":"PHP"},{"name":"SandboxedModuleTest.php","size":124,"language":"PHP"},{"name":"Expression","children":[{"name":"FilterTest.php","size":86,"language":"PHP"},{"name":"FunctionTest.php","size":62,"language":"PHP"},{"name":"TestTest.php","size":41,"language":"PHP"},{"name":"GetAttrTest.php","size":38,"language":"PHP"},{"name":"ConditionalTest.php","size":28,"language":"PHP"},{"name":"NameTest.php","size":27,"language":"PHP"},{"name":"ArrayTest.php","size":27,"language":"PHP"},{"name":"Binary","children":[{"name":"MulTest.php","size":25,"language":"PHP"},{"name":"DivTest.php","size":25,"language":"PHP"},{"name":"ConcatTest.php","size":25,"language":"PHP"},{"name":"FloorDivTest.php","size":25,"language":"PHP"},{"name":"SubTest.php","size":25,"language":"PHP"},{"name":"ModTest.php","size":25,"language":"PHP"},{"name":"AddTest.php","size":25,"language":"PHP"},{"name":"AndTest.php","size":25,"language":"PHP"},{"name":"OrTest.php","size":25,"language":"PHP"}],"size":225},{"name":"Unary","children":[{"name":"NegTest.php","size":22,"language":"PHP"},{"name":"PosTest.php","size":22,"language":"PHP"},{"name":"NotTest.php","size":22,"language":"PHP"}],"size":66},{"name":"ConstantTest.php","size":20,"language":"PHP"},{"name":"AssignNameTest.php","size":20,"language":"PHP"},{"name":"ParentTest.php","size":19,"language":"PHP"},{"name":"PHP53","children":[{"name":"FunctionInclude.php","size":4,"language":"PHP"},{"name":"FilterInclude.php","size":4,"language":"PHP"},{"name":"TestInclude.php","size":4,"language":"PHP"}],"size":12}],"size":671},{"name":"IfTest.php","size":69,"language":"PHP"},{"name":"IncludeTest.php","size":63,"language":"PHP"},{"name":"SetTest.php","size":52,"language":"PHP"},{"name":"MacroTest.php","size":47,"language":"PHP"},{"name":"SandboxTest.php","size":32,"language":"PHP"},{"name":"BlockTest.php","size":29,"language":"PHP"},{"name":"ImportTest.php","size":28,"language":"PHP"},{"name":"SpacelessTest.php","size":27,"language":"PHP"},{"name":"AutoEscapeTest.php","size":23,"language":"PHP"},{"name":"DoTest.php","size":22,"language":"PHP"},{"name":"SandboxedPrintTest.php","size":22,"language":"PHP"},{"name":"BlockReferenceTest.php","size":22,"language":"PHP"},{"name":"PrintTest.php","size":20,"language":"PHP"},{"name":"TextTest.php","size":19,"language":"PHP"}],"size":1575},{"name":"IntegrationTest.php","size":163,"language":"PHP"},{"name":"Extension","children":[{"name":"SandboxTest.php","size":161,"language":"PHP"},{"name":"CoreTest.php","size":86,"language":"PHP"}],"size":247},{"name":"ParserTest.php","size":126,"language":"PHP"},{"name":"ErrorTest.php","size":124,"language":"PHP"},{"name":"NodeVisitor","children":[{"name":"OptimizerTest.php","size":73,"language":"PHP"}],"size":73},{"name":"Loader","children":[{"name":"FilesystemTest.php","size":69,"language":"PHP"},{"name":"ArrayTest.php","size":61,"language":"PHP"},{"name":"ChainTest.php","size":38,"language":"PHP"},{"name":"Fixtures","children":[{"name":"named","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"normal","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"normal_bis","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"named_final","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"normal_ter","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"named_bis","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"named_ter","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1},{"name":"normal_final","children":[{"name":"index.html","size":1,"language":"HTML"}],"size":1}],"size":8}],"size":176},{"name":"FileCachingTest.php","size":58,"language":"PHP"},{"name":"TokenStreamTest.php","size":47,"language":"PHP"},{"name":"Fixtures","children":[{"name":"filters","children":[{"name":"batch_float.php","size":31,"language":"PHP"}],"size":31},{"name":"errors","children":[{"name":"index.html","size":7,"language":"HTML"},{"name":"base.html","size":1,"language":"HTML"}],"size":8}],"size":39},{"name":"CompilerTest.php","size":19,"language":"PHP"},{"name":"NativeExtensionTest.php","size":16,"language":"PHP"},{"name":"AutoloaderTest.php","size":10,"language":"PHP"}],"size":3972}],"size":3972},{"name":"bootstrap.php","size":3,"language":"PHP"}],"size":3975},{"name":".travis.yml","size":11,"language":"YAML"}],"size":12322};
////			drawgraph = new Drawsvg();
////			
//			
////			if(Drawsvg){
////				Drawsvg.cleanup();
////			}
////			Drawsvg = new 
////			links = GraphJson.arrows;
////
////			nodes = {};
////			links.forEach(function(link) {
////			link.type = "licensing";  
////			link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
////			  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
////			});
//////			new drawsvg("#mathmldiv",900,500,nodes,links).restart();
////
////			new drawsvg("#mathmldiv",900,500).update(GraphJson);
////			Drawsvg();
////			d3.select("#mathmldiv").selectAll("svg").remove();
////			 displaygraph();			
//		}else{
			if(stringToJson(GraphJson)!=data){
//				GraphJson = data;
//				Drawsvg.update(GraphJson);
				reallinks = [];
				GraphJson = data;
				
				var links = GraphJson.arrows;

				
				links.forEach(function(link) {
//				link.type = "licensing";  
//					link.left = false;
//					link.right = true;
					var reallink = {};
					
					var node={};
					node.name = link.source;
				//	node.reflexive = false;
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
//					node1.reflexive = false;
//					var i=0;
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
//					reallink.value = Math.random()*300;
					reallinks.push(reallink);
//				link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
//				  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
//				nodes[link.source.reflexive]=false;
//				nodes[link.target.reflexive]=false;
				
				});
				drawsvg21(realnodes,reallinks);
//				d3.select("#mathmldiv").selectAll("svg").remove();
//				 displaygraph();
//				Drawsvg.update(GraphJson);
////				Drawsvg = new drawsvg("mathmldiv",900,500).update(GraphJson);
////				Drawsvg.restart();
////				drawgraph.update();
////				updategraph();
//			}	
		}
		
	}
};

function displaygraph(){
var width  = 900,
height = 700;
  colors = d3.scale.category10();



//http://blog.thomsonreuters.com/index.php/mobile-patent-suits-graphic-of-the-day/

links = GraphJson.arrows;

nodes = {};
links.forEach(function(link) {
link.type = "licensing";  
link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
});

var force = d3.layout.force()
.nodes(d3.values(nodes))
.links(links)
.size([width, height])
.linkDistance(30)
.charge(-250)
.on("tick", tick)
.start();
var drag  = force.drag()
.on("dragstart", dragstart);


//var svg = d3.select("svg");

//var svg = d3.select("body").append("svg")
//.attr("x",410)
//.attr("y",100)
//.attr("width", width)
//.attr("height", height);

//.call(zoom);

var svg = d3.select('#mathmldiv')
.append('svg')
.attr('width', width)
 .attr('height', height);
svg.append("defs").selectAll("marker")
.attr("pointer-events", "all")
.data(["suit", "licensing", "resolved"])
.enter().append("marker")
.attr("id", function(d) { return d; })
.attr("viewBox", "0 -5 10 10")
.attr("refX", 15)
.attr("refY", -1.5)
.attr("markerWidth", 6)
.attr("markerHeight", 6)
.attr("orient", "auto")
.append("path")
.attr("d", "M0,-5L10,0L0,5")
.append("g")
.call(d3.behavior.zoom().on("zoom", zoom))
.append("g");

//svg.append("rect")
//.attr("class", "overlay")
//.attr("width", width)
//.attr("height", height);
//
function zoom() {
svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
}
var path = svg.append("g").selectAll("path")
.data(force.links())
.enter().append("path")
.attr("class", function(d) { return "link " + d.type; })
.attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

var circle = svg.append("g").selectAll("circle")

.data(force.nodes())
.enter().append("circle")
.attr("r", 6)
.call(drag)
.on("click",click);

var text = svg.append("g").selectAll("text")
.data(force.nodes())
.enter().append("text")
.attr("x", 8)
.attr("y", ".31em")
.text(function(d) { return d.name; });

function click(d){
if(d3.event.defaultPrevented) return;
alert(d.name);

}
//Use elliptical arc path segments to doubly-encode directionality.
function tick() {

path.attr("d", linkArc);
circle.attr("transform", transform);
text.attr("transform", transform);
force.stop();
//circle.attr("transform", transform);
//text.attr("transform", transform);
}
function dragstart(d) {
d3.select(this).classed("fixed", d.fixed = true);
}
function linkArc(d) {
var dx = d.target.x - d.source.x,
dy = d.target.y - d.source.y,
dr = Math.sqrt(dx * dx + dy * dy);
//return "M" + d.target.x + "," + d.target.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
}

function transform(d) {
return "translate(" + d.x + "," + d.y + ")";
}
}
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
//	var obj = "{}";
//    var canvas = document.getElementById("canvas");
//    var context = canvas.getContext("2d");
//    context.clearRect(0, 0, canvas.width, canvas.height);
	conceptobj = {};
//	conceptobj = stringToJson(conceptobj);
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
					
//					if(reallinks[k].source.name==realnodes[i].name){
//						var node1 = {};
//						var link = {};
//						node1.name  = reallinks[k].target.name;
//						node1.reflexive = false;
//						var j;
//						for(j=0;j<nodes.length;j++){
//							if(nodes[j].name==node1.name){
//								break;
//							}
//						}
//						if(j==nodes.length){
//							nodes.push(node1);
//						}
//						link.source = node;
//						link.target = nodes[j];
//						link.left = false;
//						link.right = true;
//						link.value = 20;
//						links.push(link);
////						nodes.push(reallinks[j].target);
////						links.push(reallinks[j]);
//					}
//					if(reallinks[k].target.name==realnodes[i].name){
//						var node1 = {};
//						var link = {};
//						node1.name  = reallinks[k].source.name;
//						node1.reflexive = false;
//						var j;
//						for(j=0;j<nodes.length;j++){
//							if(nodes[j].name==node1.name){
//								break;
//							}
//						}
//						if(j==nodes.length){
//							nodes.push(node1);
//						}
//						link.source = nodes[j];
//						link.target = node;
//						link.left = false;
//						link.right = true;
//						link.value = 20;
//						links.push(link);
////						nodes.push(reallinks[j].source);
////						links.push(reallinks[j]);
//					}
//				}
////				break;
//			}
//			if(realnodes[i].name.indexOf(search)>=0){
//				nodes.push(realnodes[i]);
//				for(var j=0;j<reallinks.length;j++){
//					//		if(reallinks[j].source.name==realnodes[i].name){
//					//			nodes.push(reallinks[j].target);
//					//			links.push(reallinks[j]);
//					//		}
//							if(reallinks[j].target.name==realnodes[i].name){
//								nodes.push(reallinks[j].source);
//								links.push(reallinks[j]);
//								for(var k=0;k<reallinks.length;k++){
//									if(reallinks[k].target.name==reallinks[j].source.name){
//										nodes.push(reallinks[k].source);
//										links.push(reallinks[k]);
//									}
//								}
//							}
//						}
////				break;
//			}
//		}
//		if(i==realnodes.length){
//			alert("无此条知识");
//			return;
//		}
		
	}
	drawsvg21(nodes,links);
	document.getElementById("back").style.display = "block";
	
//	for(i=0;i<RelationList.data.length;i++){
//		var obj = RelationList.data[i];
//		if(search!=""&&(obj["@ID"].indexOf(search)>=0||obj["@Name"].indexOf(search)>=0||(obj["@ID"]+obj["@Name"]).indexOf(search)>=0)){
//			if(obj["Variables"]!=null){
//				if(obj["Variables"] instanceof Array){
//					for(var j=0;j<obj["Variables"].length;j++){
//						var arr=[];
//						var obj1 = obj["Variables"][j]["Variable"];
//						if(obj1 instanceof Array){
//							for(var k=0;k<obj1.length;k++){
//								var varobj = obj1[k];
//								var id = varobj["@ID"];
//								var name = varobj["@Name"];
//								arr.push(id+"."+name);
//							}
//						}else{
//							var varobj = obj1["Conception"];
//							var id = varobj["@ID"];
//							var name = varobj["@Name"];
//							arr.push(id+"."+name);
//						}
//						var obj2 = obj["Variables"][i]["Description"];
//						var varfor = obj2["Formula"];
//						var namefor = "formula"+formulanumber+"."+varfor["@ID"]+"."+varfor["@Name"];
//						formulanumber++;
//						var node1 = {};
//						node1.name = namefor;
//						node1.reflexive = false;
//						var m=0;
//						for(m=0;m<nodes.length;m++){
//							if(nodes[m].name==node1.name){
////								link.source = nodes[m];
//								break;
//							}
//						}
//						if(m==nodes.length){
//							nodes.push(node1);
////							link.source = node1;
//						}
//						for(var l=0;l<arr.length;l++){
//							var node = {};
//							var link = {};
//							node.name = arr[l];
//							node.reflexive = false;
//							var m1=0;
//							for(m1=0;m1<nodes.length;m1++){
//								if(nodes[m1].name==node.name){
//									link.source = nodes[m1];
//									break;
//								}
//							}
//							if(m1==nodes.length){
//								nodes.push(node);
//								link.source = node;
//							}
////							link.target = node1;
//							link.target = nodes[m];
//							link.left = false;
//							link.right = true;
//							link.value = 20;
//							links.push(link);
//						}
//						var node2 = {};
//						node2.name = obj["@ID"]+"."+obj["@Name"];
//						node2.reflexive = false;
//						var link2 = {};
//						link2.source = nodes[m];
//						var m1=0;
//						for(m1=0;m1<nodes.length;m1++){
//							if(nodes[m1].name==node2.name){
//								link2.target = nodes[m1];
//								break;
//							}
//						}
//						if(m1==nodes.length){
//							nodes.push(node2);
//							link2.target = node2;
//						}
//						
//						link2.left = false;
//						link2.right = true;
//						link2.value = 20;
//						links.push(link2);
//						drawsvg21(nodes,links);
//						document.getElementById("back").style.display = "block";
//						break;
////						arr.push(namefor);
//					}
//				}else{
//				var obj1 = obj["Variables"]["VariableDes"]["Variable"];
//				var arr=[];
//				if(obj1 instanceof Array){
//					for(var k=0;k<obj1.length;k++){
//						var varobj = obj1[k];
//						var id = varobj["@ID"];
//						var name = varobj["@Name"];
//						arr.push(id+"."+name);
//					}
//				}else{
//					var varobj = obj1["Conception"];
//					var id = varobj["@ID"];
//					var name = varobj["@Name"];
//					arr.push(id+"."+name);
//				}
//				var obj2 = obj["Variables"]["VariableDes"]["Description"];
//				var varfor = obj2["Formula"];
//				var namefor = "formula"+formulanumber+"."+varfor["@ID"]+"."+varfor["@Name"];
//				formulanumber++;
//				var node1 = {};
//				node1.name = namefor;
//				node1.reflexive = false;
//				var m=0;
//				for(m=0;m<nodes.length;m++){
//					if(nodes[m].name==node1.name){
////						link.source = nodes[m];
//						break;
//					}
//				}
//				if(m==nodes.length){
//					nodes.push(node1);
////					link.source = node1;
//				}
//				for(var l=0;l<arr.length;l++){
//					var node = {};
//					var link = {};
//					node.name = arr[l];
//					node.reflexive = false;
//					var m1=0;
//					for(m1=0;m1<nodes.length;m1++){
//						if(nodes[m1].name==node.name){
//							link.source = nodes[m1];
//							break;
//						}
//					}
//					if(m1==nodes.length){
//						nodes.push(node);
//						link.source = node;
//					}
//					link.target = nodes[m];
//					link.left = false;
//					link.right = true;
//					link.value = 20;
//					links.push(link);
//				}
//				var node2 = {};
//				node2.name = obj["@ID"]+"."+obj["@Name"];
//				node2.reflexive = false;
//				var link2 = {};
//				link2.source = nodes[m];
//				var m1=0;
//				for(m1=0;m1<nodes.length;m1++){
//					if(nodes[m1].name==node2.name){
//						link2.target = nodes[m1];
//						break;
//					}
//				}
//				if(m1==nodes.length){
//					nodes.push(node2);
//					link2.target = node2;
//				}
//				link2.left = false;
//				link2.right = true;
//				link2.value = 20;
//				links.push(link2);
//				drawsvg21(nodes,links);
//				document.getElementById("back").style.display = "block";
//				break;
//			}
//			
//		}
//	
//	}
//	}
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
          
        },  
        error : function(data, status, e) {  
            alert('上传出错');  
        }  
    });
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
	var sankey = d3.sankey()
	    .nodeWidth(10)
	    .nodePadding(0)
	    .size([width, height]);

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
	//.on("dragstart", function() { this.parentNode.appendChild(this); })
	.on("drag", dragmove);
	//  .on("dragstart", dragstart);
	//  function dragstart(d) {
//		  d3.select(this).attr("transform", "translate(" + d.x + "," + (d.y = Math.max(0, Math.min(height - d.dy, d3.event.y))) + ")");
//		    sankey.relayout();
//		    link.attr("d", path);
////		  d3.select(this).classed("fixed", d.fixed = true);
////		  restart();
//		  }
	  var node = svg.append("g").selectAll(".node")
	      .data(nodes)
	    .enter().append("g")
	      .attr("class", "node")
	      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
	      .on("click",click)
	      .call(drag);
//	    .call(d3.behavior.drag()
//	      .origin(function(d) { return d; })
//	      .on("dragstart", function() { this.parentNode.appendChild(this); })
//	      .on("drag", dragmove))
//	      ;

	//  node.append("rect")
//	      .attr("height", function(d) { return d.dy; })
//	      .attr("width", sankey.nodeWidth())
//	      .style("fill", function(d) { return d.color = color(d.name.replace(/ .*/, "")); })
//	      .style("stroke", function(d) { return d3.rgb(d.color).darker(2); })
//	    .append("title")
//	      .text(function(d) { return d.name + "\n" + format(d.value); });
	  var fillstyle = function(d) { if(d==selected_node){ return  "#000";}
	  	else if(d.name.indexOf("formula")>=0){ return d3.rgb(colors(1)).brighter().toString();}
	  	else{return d3.rgb(colors(2)).brighter().toString();}};
	  node.append("circle")
//	  	.attr('class','nodestyle')
	  	.style("stroke-width",1)
	  	.attr('r', sankey.nodeWidth())
	//  .attr("height", function(d) { return d.dy; })
	//  .attr("width", sankey.nodeWidth())
	  	.style('fill',fillstyle)
	//  .style("fill", function(d) { return d.color = color(d.name.replace(/ .*/, "")); })
	  .style("stroke", function(d) { return d3.rgb(d.color).darker(2); })
	  .classed("reflexive",function(d){return d.reflexive;})
	.append("title")
	  .text(function(d) { return d.name + "\n" + format(d.value); });

	  node.append("text")
	      .attr("x", -6)
	      .attr("y",0)
//	      .attr("y", function(d) { return d.dy / 2; })
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
//	      selected_link = null;
//	      restart();
//		  if(d3.event.defaultPrevented) return;
	      if(d3.event.defaultPrevented) return;
//	      var div=document.getElementById("att");    
//	 	 if(div!=null){ 
//	 		 document.getElementById("Attributes").removeChild(div);
//	 	 }
	      node.selectAll('circle').style("fill", function(d) { if(d==selected_node){ return "#000";}
	    	else if(d.name.indexOf("formula")>=0){ return d3.rgb(colors(1)).brighter().toString();}
	      	else{return d3.rgb(colors(2)).brighter().toString();}});
	 	 if(d.name.indexOf("formula")>=0){
	 		if(RuleList.data instanceof Array){
				var i;
				var len = RuleList.data.length;
//				var createDiv=document.createElement("div");  
//				createDiv.id = "att";
				for(i=0;i<len;i++){
					if(d.name.indexOf(RuleList.data[i].rulename)>=0){
						
//						createDiv.innerHTML = RuleList.data[i].rulestr;
						document.getElementById("conceptproperty").style.display = "none";
						document.getElementById("mathproperty").style.display = "block";
						document.getElementById("mathproperty").innerHTML = RuleList.data[i].rulestr;
						break;
					}
				}
				if(i==RuleList.data.length){
//					createDiv.innerHTML = "none";
					alert("没有"+d.name+"formula");
					document.getElementById("conceptproperty").style.display = "none";
					document.getElementById("mathproperty").style.display = "block";
					document.getElementById("mathproperty").innerHTML ="none";
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
	//})
	}
	;