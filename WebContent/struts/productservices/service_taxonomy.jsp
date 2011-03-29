<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Service Taxonomy</title>
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
				"success": function(data) {
					return data.result;
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
		"plugins": ["themes", "json_data"]
	});
	$('.classification').change(function() {
		tree.jstree('refresh');
	});
});
</script>
</head>
<body>
<h1>Service Taxonomy</h1>

<div id="search">
<form id="filter">
<div class="filteroption">
	<s:radio list="@com.picsauditing.jpa.entities.ClassificationType@values()" name="classification" cssClass="classification"/>
</div>
</form>
</div>
<br />
<div id="services"></div>
</body>
</html>