<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="EmployeeNCCERUpload.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<script type="text/javascript" src="js/jquery/jquery.min.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript">
$(function() {
	$('#content').delegate('.closeButton', 'click', function(e) {
		e.preventDefault;
		window.opener.location.reload();
		self.close();
	});
});
</script>
</head>
<body>
<br />
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1><s:text name="EmployeeNCCERUpload.title" /></h1>
			<s:include value="../actionMessages.jsp" />
			<div class="info"><s:text name="EmployeeNCCERUpload.help.ClickLinkBelow" /></div>
			<a href="resources/NCCERAssessmentTests.xls"><s:text name="EmployeeNCCERUpload.link.NCCERAssessmentTests" /></a>
			<br /><br />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<s:hidden name="employee" />
				<div style="background-color: #F9F9F9;">
					<div class="question">
						<label><s:text name="global.File" />:</label>
						<s:file name="upload" value="%{upload}" size="50"></s:file><br /><br />
						<input type="button" value="<s:text name="EmployeeNCCERUpload.button.CloseAndReturn" />" class="picsbutton closeButton" />
						<s:submit cssClass="picsbutton positive" value="%{getText('button.Upload')}" method="save" />
					</div>
				</div>
			</s:form>
			</div>
			<br clear="all" />
		</div>
	</div>
</div>
</body>