<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>Upload NCCER Assessments</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript">
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
			<h1>Upload NCCER Assessments</h1>
			<s:include value="../actionMessages.jsp" />
			<a href="resources/NCCERAssessmentTests.xls">Please fill out both the effective dates and expiration dates on this form.</a>
			<br /><br />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<s:hidden name="employee" />
				<div style="background-color: #F9F9F9;">
					<div class="question">
						<label>File:</label>
						<s:file name="upload" value="%{upload}" size="50"></s:file><br /><br />
						<button class="picsbutton" onclick="closePage(); return false;">Close and Return to Page</button>
						<s:submit cssClass="picsbutton positive" value="Upload" method="save" />
					</div>
				</div>
			</s:form>
			</div>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
