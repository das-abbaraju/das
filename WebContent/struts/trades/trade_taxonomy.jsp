<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Taxonomy</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.form.js"></script>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>

<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/trade_taxonomy_admin.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy_common.js?v=<s:property value="version"/>"></script>
<script>
</script>
<style>
img.trade {
	margin: 10px;
}
</style>
</head>
<body>
<h1>Trade Taxonomy</h1>

<s:include value="../actionMessages.jsp"></s:include>

<div class="clearfix">
	<a href="#" class="add trade">Add Top Level Trade</a>
</div>

<s:include value="trade_search.jsp"/>

<div id="trade-view">
	<div class="info">Click a trade on the left</div>
</div>

<div class="clear">
<s:form>
	<s:submit action="TradeTaxonomy!index" value="Index Trade Taxonomy" cssClass="picsbutton" />
</s:form>
</div>

</body>
</html>