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
<script type="text/javascript" src="js/audit_cat_edit.js?v=20091105"></script>
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

<s:form enctype="multipart/form-data" method="POST">
<div style="text-align:center;">
	<s:hidden name="auditID" />
	<s:hidden name="divId" />
	<s:hidden name="answer.question.id" />
	<table style="margin-left: auto; margin-right: auto;">
		<tr>
			<td style="text-align:center;vertical-align:top;width: 45%">
				<h3 style="margin-top:0px;">Upload a New File</h3>
				<s:if test="file != null && file.exists()">
					<p>This will replace an existing file.</p>
				</s:if>
				<s:file name="file" size="15%"></s:file>
				<div>
					<input type="submit" class="picsbutton positive" name="button" value="Upload File" />
				</div>
			</td>
			
			<s:if test="file != null && file.exists()">	
				<td style="text-align:center;vertical-align:top; width: 45%;border-left: 1px solid #eee;">
					<h3 style="margin-top:0px;">View Existing File</h3>
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.question.id=<s:property value="answer.question.id"/>"
						target="_BLANK">Open Existing <s:property value="fileSize" />
					File</a>
					<br/><br/>
				
					<button class="picsbutton negative" name="button" value="Delete" type="submit"
							onclick="return confirm('Are you sure you want to delete this file?');">Delete File</button>
					<br clear="all" />
				</td>
			</s:if>
		</tr>
	</table>
</div> 
</s:form>

<div style="text-align:center; width:100%;">
	<div style="text-align:center;">
		<button style="text-align:center; width:100%" class="picsbutton" name="button" value="Close" onclick="javascript: closePage()">Close & Return</button>
	</div>
</div>

</div>	
</div>
</div>
</body>
</html>
