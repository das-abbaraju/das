<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects,controls"></script>
</head>
<body>

<s:include value="../contractors/conHeader.jsp" />

<div>
<s:form>
	<s:hidden name="auditID"/>
<span class="blueMain">Select a Contractor</span>&nbsp;
<s:textfield id="contractor_select" name="contractorSelect" size="60"></s:textfield>
<div id="contractor_select_choices" class="autocomplete"></div>
<script type="text/javascript">
new Ajax.Autocompleter('contractor_select', 'contractor_select_choices', 'ContractorSelectAjax.action', {
	tokens: ',',
	paramName: "filter.accountName",
	minChars: 3
});
</script>
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
