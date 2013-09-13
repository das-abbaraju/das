<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<jsp:include page="/struts/jquery.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/forms.css?v=${version}" />
<title>PICS Error</title>
</head>

<body>
	<s:form id="response_form" method="post" style="width:450px;">
		<fieldset class="form" >
			<h2 class="formLegend">Response Submitted</h2>
			<div id='message1' style="padding:2ex;">
				<p>Thank you for your assistance.</p>
			</div>
		</fieldset>
		<fieldset class="form submit">
			<input id="backButton" class="picsbutton left" type="button" value="&lt;&lt; Back" onclick="history.go(-2)" />
		</fieldset>
	</s:form>
</body>
</html>
