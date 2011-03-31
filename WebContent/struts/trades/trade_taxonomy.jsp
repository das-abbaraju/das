<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Taxonomy</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js"></script>
<script type="text/javascript">
$(function() {
	var tree = $('#services').jstree({
		"themes": {
			theme: "classic"	
		},
		"json_data": {
			"ajax": {
				"url": 'ServiceTaxonomy!json.action',
				"dataType": "json",
				"success": function(json) {
					return json.result;
				},
				"data": function(node) {
					result = $('#filter').serialize();
					if (node.attr) {
						result += "&service=" + node.attr("id");
					}
					return result;
				}
			}
		},
		"types": {
			"default": {
			"select_node": true,
			"hover_node":true
			}
		},
		"plugins": ["themes", "json_data", "types"]
	});
	$('#services').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { service: $(this).parent().data('jstree').id };
		startThinking({div: 'service-info', type: 'large'});
		$('#service-info').load('ServiceTaxonomy!serviceAjax.action', data);
	});
	$('.classification').change(function() {
		tree.jstree('refresh');
		$('#service-info').text("Click a service on the left to view more.");
	});
	$(document).ready(function () {  
	  var top = $('#service-info').offset().top - parseFloat($('#service-info').css('marginTop').replace(/auto/, 0));
	  $(window).scroll(function (event) {
	    // what the y position of the scroll is
	    var y = $(this).scrollTop();
	  
	    // whether that's below the form
	    if (y >= top) {
	      // if so, ad the fixed class
	      $('#service-info').addClass('fixed');
	    } else {
	      // otherwise remove it
	      $('#service-info').removeClass('fixed');
	    }
	  });
	});
	$('.psAutocomplete').autocomplete('TradeAutocomplete.action', {
    	minChars: 2,
    	max: 100,
    	formatResult: function(data,i,count) { return data[1]; }
    });
});
</script>
<style>
#services li {
	font-size: 14px;
	line-height: 1.5;
}

#info-wrapper { 
  position: absolute;
  width: 280px;
  right: 22px;
}

#service-info {
  position: absolute;
  top: 0;
  /* just used to show how to include the margin in the effect */
  margin-top: 20px;
  border: 1px solid #A84D10;
  padding: 10px;
  background-color: #eee;
  width: 280px;
}

#service-info.fixed {
  position: fixed;
  top: 0;
}

#service-info h3 {
	font-size: 18px;
}
#service-info label {
	font-weight: bold;
}
#service-info li label {
	color: #808285;
}
#service-info ul {
	line-height: 14px;
	padding-top: 10px;
}

#wrapper {
	margin-top: 20px;
	position: relative
}
 
#service-info .center {
	text-align: center;
}

</style>
</head>
<body>
<h1>Service Taxonomy</h1>

<div id="suggest">
<form>
	<label>Product/Service Search:</label>
	<input class="psAutocomplete" name="tradeSearch" style="width: 400px" />
</form>
</div>

<div id="wrapper">
<div id="search">
<form id="filter">
<div class="filteroption">
	<s:radio list="listTypes" name="listType" listValue="description" cssClass="classification"/>
</div>
</form>
</div>
<div id="info-wrapper">
	<div id="service-info"> Click a service on the left to view more. </div>
</div>
<br />

<div id="services"></div>

</div>

</body>
</html>