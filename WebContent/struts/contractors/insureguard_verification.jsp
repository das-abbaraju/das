<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" /> 
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
small {
	font-size: smaller;
}
.buttonRow {
	float: right;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp" />

<s:if test="caoList.size > 0">
	<s:form>
		<div id="emailPreview"><s:include value="verification_mail.jsp" /></div>
		<s:hidden name="id" />
		<input type="submit" id="sendEmailButton" class="picsbutton positive" value="Send Email" name="button" />
	</s:form>
</s:if>
<s:else>
	<div class="alert">No rejected InsureGuard policies found.</div>
</s:else>

</body>
</html>