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
	var tree = $('#trade-nav').jstree({
		"themes": {
			theme: "classic"	
		},
		"json_data": {
			"ajax": {
				"url": 'TradeTaxonomy!json.action',
				"dataType": "json",
				"success": function(json) {
					return json.result;
				},
				"data": function(node) {
					result = $('#filter').serialize();
					if (node.attr) {
						result += "&trade=" + node.attr("id");
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
	$('#trade-nav').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { trade: $(this).parent().data('jstree').id };
		startThinking({div: 'trade-detail', type: 'large'});
		$('#trade-detail').load('TradeTaxonomy!tradeAjax.action', data);
	});
	$(document).ready(function () {  
	  var top = $('#trade-info').offset().top - parseFloat($('#trade-info').css('marginTop').replace(/auto/, 0));
	  $(window).scroll(function (event) {
	    // what the y position of the scroll is
	    var y = $(this).scrollTop();
	  
	    // whether that's below the form
	    if (y >= top) {
	      // if so, ad the fixed class
	      $('#trade-info').addClass('fixed');
	    } else {
	      // otherwise remove it
	      $('#trade-info').removeClass('fixed');
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
#trades .nav li {
	font-size: 14px;
	line-height: 1.5;
}

#info-wrapper {
  position: absolute;
  width: 280px;
  right: 22px;
}

#trade-info {
  position: absolute;
  top: 0;
  /* just used to show how to include the margin in the effect */
  margin-top: 20px;
  border: 1px solid #A84D10;
  padding: 10px;
  background-color: #eee;
  width: 280px;
}

#trade-info.fixed {
  position: fixed;
  top: 0;
}

#trade-info label {
	font-weight: bold;
}
#trade-info li label {
	color: #808285;
}
#trade-info ul {
	line-height: 14px;
	padding-top: 10px;
}

#wrapper {
	position: relative
}

#trade-info .center {
	text-align: center;
}

</style>
</head>
<body>
<h1>Trade Taxonomy</h1>

<div id="suggest">
<form>
	<label>Trade Search:</label>
	<input class="psAutocomplete" name="tradeSearch" style="width: 400px" />
</form>
</div>

<table id="trades">
	<tr>
		<td id="trade-nav"></td>
		<td id="trade-detail">Click a trade on the left</td>
	</tr>
</table>

</body>
</html>