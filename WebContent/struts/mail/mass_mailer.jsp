<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Mass Mailer</title>
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<style>
td.selected {
	background-color: #FEC;
}
</style>
</head>
<body>
<h1>Mass Mailer</h1>

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="form1" method="post">
<div>





	<div class="buttons">
		<button class="positive" name="button" type="submit" value="send">Send Email</button>
	</div>		
</div>
</s:form>

</body>
</html>