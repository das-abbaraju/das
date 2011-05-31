<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ContractorTrades.title"></s:text> </title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<script>
var conID = '<s:property value="id"/>';
</script>
<s:include value="trade_cloud_header.jsp" />

<script type="text/javascript" src="js/trade_taxonomy_contractor.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy_common.js?v=<s:property value="version"/>"></script>
<style>
#indexTrades {display: none;}
</style>
</head>
<body>

<s:include value="../contractors/conRegistrationHeader.jsp"/>

<s:if test="!permissions.operator">
<h4>What are your business trades?</h4>
<s:include value="trade_search.jsp"/>
</s:if>

<div id="trade-view" <s:if test="permissions.operator">class="fullwidth"</s:if>>
	<s:include value="contractor_trade_view.jsp"/>
</div>

<s:if test="permissions.contractor">
	<div class="navigationButtons">
		<s:if test="registrationStep.done">
			<s:if test="contractor.needsTradesUpdated">
				<div class="alert"><s:text name="ContractorTrades.NeedsUpdating" /></div>
			</s:if>
		</s:if>
	</div>
</s:if>
<s:include value="../contractors/registrationFooter.jsp" />
</body>
</html>