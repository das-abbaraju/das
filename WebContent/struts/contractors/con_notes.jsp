<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /> Notes</title>

<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />

<script src="js/notes.js" type="text/javascript"></script>
<script type="text/javascript">
var conID = '<s:property value="id"/>';
</script>
</head>
<body>
<s:include value="conHeader.jsp" />

<div id="notesList">
<s:include value="con_notes_notes.jsp"></s:include>
</div>

<s:if test="emailList.size > 0">
<div id="emailList">
<s:include value="con_notes_email.jsp"></s:include>
</div>
</s:if>

</body>
</html>
