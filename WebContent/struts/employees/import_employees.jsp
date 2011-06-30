<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#content').delegate('.closeButton', 'click', function(e) {
		e.preventDefault();
		closePage();
	});
});

function closePage() {
	window.opener.location.reload();
	self.close();
}
</script>
</head>
<body>
<br />
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1>Import Employees</h1>
			<s:include value="../actionMessages.jsp" />
			<div class="info">
				Click on the link below to download the excel template used 
			</div>
			<s:if test="account.requiresCompetencyReview">
				<a href="<s:property value="scope" />!download.action?account=<s:property value="account.id" />" target="_blank" class="excel">
					<s:text name="%{scope}.button.DownloadExcelTemplate" />
				</a>
			</s:if>
			<s:else>
				<a href="resources/ImportEmployees.xls"><s:text name="%{scope}.button.DownloadExcelTemplate" /></a>
			</s:else>
			<div>
				<s:form enctype="multipart/form-data" method="POST">
					<s:hidden name="account" />
					<div style="background-color: #F9F9F9;">
						<div class="question">
							<label>File:</label>
							<s:file name="upload" size="50"></s:file><br /><br />
							<input type="button" class="picsbutton closeButton" value="<s:text name="%{scope}.button.CloseAndReturn" />" />
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