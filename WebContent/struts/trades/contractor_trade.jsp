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
<script type="text/javascript" src="js/trade_taxonomy_contractor.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy_common.js?v=<s:property value="version"/>"></script>
</head>
<body>

<s:include value="../contractors/conRegistrationHeader.jsp"/>

<s:if test="permissions.contractor">
	<s:if test="registrationStep.done">
		<s:if test="contractor.needsTradesUpdated">
			<div class="alert"><s:text name="ContractorTrades.NeedsUpdating" />
				<s:form><s:submit action="ContractorTrades!nextStep" cssClass="picsbutton positive" value="%{getText('button.Confirm')}"></s:submit></s:form>
			</div>
		</s:if>
	</s:if>
	<s:else>
		<s:form><s:submit action="ContractorTrades!nextStep" cssClass="picsbutton positive" value="%{getText('button.Next')}"></s:submit></s:form>
	</s:else>
</s:if>


<h4>What are your business trades?</h4>
<s:include value="trade_search.jsp"/>

<div id="trade-view">
	<s:include value="contractor_trade_cloud.jsp"/>
</div>
</body>
</html>