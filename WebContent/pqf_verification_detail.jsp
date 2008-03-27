<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>PQF Verification for <s:property value="contractor.name" /></title>
</head>
<body>
<h1>PQF Verification for <s:property value="contractor.name" /></h1>
<p class="blueMain"><a href="pqf_verification.jsp">Return to List</a></p>

<s:property value="contractor.email" />

<s:iterator value="contractor.generalContractors">
	<s:property value="account.name" /><br />
</s:iterator>

</body>
</html>
