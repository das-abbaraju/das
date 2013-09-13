<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<title><s:text name="ManageEmployeesUpload.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/jquery.min.js?v=${version}"></script>
<script type="text/javascript">
$(function() {
	$('#content').delegate('.closeButton', 'click', function(e) {
		e.preventDefault();
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
			<h1><s:text name="ManageEmployeesUpload.title" /></h1>
			<s:include value="../actionMessages.jsp" />
			<div class="info">
				<s:text name="ManageEmployeesUpload.help.ClickLinkBelow" />
			</div>
			<s:if test="account.requiresCompetencyReview">
				<a href="<s:property value="scope" />!download.action?account=<s:property value="account.id" />" target="_blank" class="excel">
					<s:text name="ManageEmployeesUpload.button.DownloadExcelTemplate" />
				</a>
			</s:if>
			<s:else>
				<a href="resources/ImportEmployees.xls"><s:text name="ManageEmployeesUpload.button.DownloadExcelTemplate" /></a>
			</s:else>
			<div>
				<s:form enctype="multipart/form-data" method="POST">
					<s:hidden name="account" />
					<div style="background-color: #F9F9F9;">
						<div class="question">
							<label><s:text name="global.File" />:</label>
							<s:file name="upload" size="50"></s:file><br /><br />
							<input type="button" class="picsbutton closeButton" value="<s:text name="ManageEmployeesUpload.button.CloseAndReturn" />" />
							<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Upload')}" />
						</div>
					</div>
				</s:form>
			</div>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
</html>