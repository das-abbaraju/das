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

<script type="text/javascript">
	function closePage() {
		try {
			opener.reloadQuestion(<s:property value="question.id"/>);
			opener.focus();
		} catch(err) {}
	}
</script>

</head>
<body onunload="closePage()">
<br />
<div id="main">
<div id="bodyholder">
<div id="content">

<h1> Upload <s:property value="conAudit.auditFor"/> <s:property value="question.columnHeader"/> File 
<span class="sub"><s:property value="question.subCategory.category.category"/> &gt; <s:property value="question.subCategory.subCategory"/></span>
</h1>
<s:include value="../actionMessages.jsp" />

<div>
<b><s:property value="question.subCategory.category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/></b>
&nbsp;&nbsp;<s:property value="question.question"/>
</div>
<s:form enctype="multipart/form-data" method="POST">
	<s:hidden name="auditID"/>
	<s:hidden name="question.id"/>
	<br />
	<s:file name="file" size="50"></s:file>
	<div class="buttons">
		<a href="javascript: self.close();">Close and Return to Form</a>
		<s:if test="file != null && file.exists()">
			<button class="negative" name="button" value="Delete" type="submit" onclick="return confirm('Are you sure you want to delete this file?');">Delete File</button>
		</s:if>
		<button class="positive right" name="button" value="Upload" type="submit">Upload File</button>
	</div>
</s:form>
<br clear="all"/>
<s:if test="file != null && file.exists()">
	<div><a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&question.id=<s:property value="question.id"/>" target="_BLANK">Open Existing <s:property value="fileSize" /> File</a></div>
</s:if>

</div>
</div>
</div>
</body>
</html>
