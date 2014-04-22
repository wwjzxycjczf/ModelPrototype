function drawsvg22(nodes,links){
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

//      .style("stroke-width", function(d) { return Math.max(1, d.dy); })
      .sort(function(a, b) { return b.dy - a.dy; });

  link.append("title")
      .text(function(d) { return d.source.name + " → " + d.target.name + "\n" + format(d.value); });

  var drag =d3.behavior.drag()
.origin(function(d) { return d; })
//.on("dragstart", function() { this.parentNode.appendChild(this); })
.on("drag", dragmove);
//  .on("dragstart", dragstart);
//  function dragstart(d) {
//	  d3.select(this).attr("transform", "translate(" + d.x + "," + (d.y = Math.max(0, Math.min(height - d.dy, d3.event.y))) + ")");
//	    sankey.relayout();
//	    link.attr("d", path);
////	  d3.select(this).classed("fixed", d.fixed = true);
////	  restart();
//	  }
  var node = svg.append("g").selectAll(".node")
      .data(nodes)
    .enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
      .on("click",click)
      .call(drag);
//    .call(d3.behavior.drag()
//      .origin(function(d) { return d; })
//      .on("dragstart", function() { this.parentNode.appendChild(this); })
//      .on("drag", dragmove))
//      ;

//  node.append("rect")
//      .attr("height", function(d) { return d.dy; })
//      .attr("width", sankey.nodeWidth())
//      .style("fill", function(d) { return d.color = color(d.name.replace(/ .*/, "")); })
//      .style("stroke", function(d) { return d3.rgb(d.color).darker(2); })
//    .append("title")
//      .text(function(d) { return d.name + "\n" + format(d.value); });
  var fillstyle = function(d) { if(d==selected_node){ return  "#000";}
  	else if(d.name.indexOf("formula")>=0){ return d3.rgb(colors(1)).brighter().toString();}
  	else{return d3.rgb(colors(2)).brighter().toString();}};
  node.append("circle")
//  	.attr('class','nodestyle')
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
//      .attr("y", function(d) { return d.dy / 2; })
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
//      selected_link = null;
//      restart();
//	  if(d3.event.defaultPrevented) return;
      if(d3.event.defaultPrevented) return;
//      var div=document.getElementById("att");    
// 	 if(div!=null){ 
// 		 document.getElementById("Attributes").removeChild(div);
// 	 }
      node.selectAll('circle').style("fill", function(d) { if(d==selected_node){ return "#000";}
    	else if(d.name.indexOf("formula")>=0){ return d3.rgb(colors(1)).brighter().toString();}
      	else{return d3.rgb(colors(2)).brighter().toString();}});
 	 if(d.name.indexOf("formula")>=0){
 		if(RuleList.data instanceof Array){
			var i;
			var len = RuleList.data.length;
//			var createDiv=document.createElement("div");  
//			createDiv.id = "att";
			for(i=0;i<len;i++){
				if(d.name.indexOf(RuleList.data[i].rulename)>=0){
					
//					createDiv.innerHTML = RuleList.data[i].rulestr;
					document.getElementById("conceptproperty").style.display = "none";
					document.getElementById("mathproperty").style.display = "block";
					document.getElementById("mathproperty").innerHTML = RuleList.data[i].rulestr;
					break;
				}
			}
			if(i==RuleList.data.length){
//				createDiv.innerHTML = "none";
				alert("没有"+d.name+"formula");
				document.getElementById("conceptproperty").style.display = "none";
				document.getElementById("mathproperty").style.display = "block";
				document.getElementById("mathproperty").innerHTML ="none";
//				document.getElementById("Attributes").appendChild(createDiv);
			}
		}
 	 }else{
//			var createDiv=document.createElement("div");  
//			createDiv.id = "att";
 		 var i=0;
 		 for(i=0;i<ConceptList.data.length;i++){
 			 if((ConceptList.data[i]["@ID"]+"."+ConceptList.data[i]["@Name"]).indexOf(d.name)>=0){
 				document.getElementById("conceptproperty").style.display = "block";
				document.getElementById("mathproperty").style.display = "none";
 				document.getElementById("id").value = ConceptList.data[i]["@ID"];
 	 			document.getElementById("name").value = ConceptList.data[i]["@Name"];
 	 			document.getElementById("Accuracy").value = ConceptList.data[i]["property"]["Performance"]["Accuracy"];
 	 			document.getElementById("RefreshRate").value = ConceptList.data[i]["property"]["Performance"]["RefreshRate"];
 	 			document.getElementById("Range").value = ConceptList.data[i]["property"]["Performance"]["Range"];
 	 			document.getElementById("MaxTransDelay").value = ConceptList.data[i]["property"]["Performance"]["MaxTransDelay"];
 	 			document.getElementById("SigBits").value = ConceptList.data[i]["property"]["Performance"]["SigBits"];
 	 			document.getElementById("Redundancy").value = ConceptList.data[i]["property"]["Safety"][0];
 	 			document.getElementById("Interface").value = ConceptList.data[i]["property"]["Interface"][0]["@BUS"];
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