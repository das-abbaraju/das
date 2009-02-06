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
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<script src="js/CalendarPopup.js" type="text/javascript"></script>

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

<div id="emailList">
<s:include value="con_notes_email.jsp"></s:include>
</div>

<pics:permission perm="EditNotes" type="Edit">
<a name="edit" />
<div id="noteEdit"><div id="thinking_noteEdit"></div></div>
<br clear="all"/>
</pics:permission>


<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
