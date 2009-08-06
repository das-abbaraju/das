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
<script type="text/javascript" src="js/audit_cat_edit.js"></script>
<script type="text/javascript">
function closePage() {
	try {
		opener.reloadQuestion('<s:property value="divId"/>', '<s:property value="answer.id"/>');
		opener.focus();
	} catch(err) {}
	self.close();
}
</script>

</head>
<body>
<br />
<div id="main">
<div id="bodyholder">
<div id="content">

<h1>Upload <s:property value="conAudit.auditFor" /> <s:property
	value="answer.question.columnHeader" /> File <span class="sub"><s:property
	value="answer.question.subCategory.category.category" /> &gt; <s:property
	value="answer.question.subCategory.subCategory" /></span></h1>
<s:include value="../actionMessages.jsp" />

<div><b><s:property
	value="answer.question.subCategory.category.number" />.<s:property
	value="answer.question.subCategory.number" />.<s:property
	value="answer.question.number" /></b> &nbsp;&nbsp;<s:property
	value="answer.question.question" escape="false"/>
</div>

<div id="content">
<div style="width:45%;float:left; border-right:1px solid #eee; margin-top:0px; text-align:center">
<h3 style="margin-top:0px;">Upload a New File</h3>
<s:form enctype="multipart/form-data" method="POST">
	<s:hidden name="auditID" />
	<s:hidden name="divId" />
	<s:hidden name="answer.question.id" />
	<s:file name="file" size="15%"></s:file>
	<div>
	<br>
	<button class="picsbutton positive" name="button" value="Save" type="submit">Upload File</button>
	</div>
	</div>
<div style="width:50%; float:right;text-align:center"/>	
<h3 style="margin-top:0px;">Edit Existing File</h3>
	<div>
	<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.question.id=<s:property value="answer.question.id"/>"
		target="_BLANK">Open Existing <s:property value="fileSize" />
	File</a>
	<br/><br>
	<s:if test="file != null && file.exists()">
	<button class="picsbutton negative" name="button" value="Delete" type="submit"
			onclick="return confirm('Are you sure you want to delete this file?');">Delete File</button>
	</s:if>
	</div>
	</div>
	
</s:form> <br clear="all" />
<s:if test="file != null && file.exists()">
</s:if>
</div>

<div style="text-align:center; width:100%;>
<div style="text-align:center;>
<button style="text-align:center; width:100%" class="picsbutton" name="button" value="Close" OnClick="javascript: closePage()">Close & Return</button>
</div>
</div>
</div>	
</div></div>
</body>
</html>
