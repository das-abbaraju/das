<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
</head>
<body>
<br />
<div id="main">
<div id="bodyholder">
<div id="content">

<h1>Upload Certificate for <s:property value="contractor.name" /></h1>
<s:include value="../actionMessages.jsp" />

<s:form enctype="multipart/form-data" method="POST">
	<s:hidden name="id" />
	<br />
	File Name : <s:textfield name="fileName" size="50"/><br/>
	<s:file name="file" size="50"></s:file>
	<br />
	<div class="buttons"><a href="javascript: closePage();">Close
	and Return to Form</a> <s:if test="file != null && file.exists()">
		<button class="negative" name="button" value="Delete" type="submit"
			onclick="return confirm('Are you sure you want to delete this file?');">Delete
		File</button>
	</s:if>
	<button class="positive right" name="button" value="Save"
		type="submit">Save</button>
	</div>
</s:form> <br clear="all" />
<br clear="all" />
<s:if test="file != null && file.exists()">
	<div><a
		href="CertificateUpload.action?id=<s:property value="id"/>&certID=<s:property value="certID"/>&button=download"
		target="_BLANK">Open Existing <s:property value="fileSize" />
	File</a></div>
</s:if>
</div>
</div>
</div>
</body>
</html>
