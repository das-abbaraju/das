<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('#contractor_select').autocomplete('ContractorSelectAjax.action', 
			{
				minChars: 3,
				extraParams: {'filter.accountName': function() {return $('#contractor_select').val();} }
			}
	);
});
</script>
</head>
<body>

<s:include value="../contractors/conHeader.jsp" />

<div>
<s:form>
	<s:hidden name="auditID"/>
<span class="blueMain">Select a Contractor</span>&nbsp;
<s:textfield id="contractor_select" name="contractorSelect" size="60"></s:textfield>
<s:if test="hasDuplicate">
	<s:submit name="button" value="Copy Audit Anyway"/>
</s:if>
<s:else>
	<s:submit name="button" value="Copy Audit"/>
</s:else>
</s:form>
</div>
</body>
</html>
