<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="account.name" /> Notes</title>

<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />

<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script src="js/notes.js" type="text/javascript"></script>
<script type="text/javascript">
var accountID = '<s:property value="id"/>';
var accountType = '<s:property value="account.type"/>';
</script>
</head>
<body>
<s:if test="account.contractor">
	<s:include value="../contractors/conHeader.jsp" />
	<h3>Notes</h3>
</s:if>
<s:else>
	<s:include value="../operators/opHeader.jsp" />
</s:else>

<div id="notesList">
<s:include value="account_notes_notes.jsp"></s:include>
</div>

<s:if test="account.contractor">
	<h3>Email History</h3>
	<div id="notesList">
	<s:include value="account_notes_email.jsp"></s:include>
	</div>
</s:if>

</body>
</html>
