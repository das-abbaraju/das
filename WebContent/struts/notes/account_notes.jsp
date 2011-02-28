<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="account.name" /><s:text name="%{scope}.ContractorNotes.title" /> Notes</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript">
var accountID = '<s:property value="id"/>';
var accountType = '<s:property value="account.type"/>';
</script>
</head>
<body>
<s:if test="account.contractor">
	<s:include value="../contractors/conHeader.jsp" />
</s:if>
<s:else>
	<s:include value="../operators/opHeader.jsp" />
</s:else>
<h3>Notes</h3>
<div id="notesList">
	<s:include value="account_notes_notes.jsp"></s:include>
</div>
<br />
<s:if test="account.contractor">
	<h3>Email History</h3>
	<div id="notesList">
		<s:include value="account_notes_email.jsp"></s:include>
	</div>
</s:if>

</body>
</html>
