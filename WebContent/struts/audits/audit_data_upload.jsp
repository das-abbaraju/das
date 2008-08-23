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
<h1>File Upload
<span class="sub"><s:property value="question.subCategory.category.category"/> &gt; <s:property value="question.subCategory.subCategory"/></span>
</h1>
<s:include value="../actionMessages.jsp" />

<div>
<s:property value="question.subCategory.category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>
&nbsp;&nbsp;<s:property value="question.question"/>
</div>
<s:form enctype="multipart/form-data" method="POST">
	<s:hidden name="auditID"/>
	<s:hidden name="question.questionID"/>
	<br />
	<s:file name="file" size="50"></s:file>
	<div class="buttons" style="text-align: center;">
		<s:if test="data.dataID > 0">
			<button name="button" value="Delete" type="submit">Delete File</button>
		</s:if>
		<button class="positive" name="button" value="Upload" type="submit">Upload File</button>
	</div>
</s:form>
</div>
</div>
</div>
</body>
</html>
