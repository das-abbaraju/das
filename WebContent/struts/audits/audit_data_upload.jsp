<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/audit_cat_edit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
function closePage() {
	try {
		opener.reloadQuestion('<s:property value="auditData.question.id"/>');
		opener.triggerDependent('<s:property value="auditData.question.id"/>');
		if (<s:property value="auditData.question.id"/> == 1331)
		    opener.reloadQuestion('10217');
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

<h1><s:text name="AuditDataUpload.UploadFile">
	<s:param><s:property value="conAudit.auditFor" /></s:param>
	<s:param><s:if test="auditData.question.columnHeader.exists" ><s:property value="auditData.question.columnHeader.toString()" /></s:if></s:param>
	</s:text> <span class="sub">
	<s:iterator value="auditData.question.category.ancestors" status="stat">
		<s:property value="name"/> <s:if test="!#stat.last">&gt;</s:if>
	</s:iterator>
	</span>
</h1>
<s:include value="../actionMessages.jsp" />

<div><b><s:property
	value="auditData.question.expandedNumber" />
	</b> &nbsp;&nbsp;<s:property
	value="auditData.question.name" escape="false"/>
</div>

<s:form enctype="multipart/form-data" method="POST">
<div style="text-align:center;">
	<s:hidden name="auditID" />
	<s:hidden name="divId" />
	<s:hidden name="auditData.question.id" />
	<table style="margin-left: auto; margin-right: auto;">
		<tr>
			<td style="text-align:center;vertical-align:top;width: 45%">
				<h3 style="margin-top:0px;"><s:text name="AuditDataUpload.UploadNew"></s:text></h3>
				<s:if test="file != null && file.exists()">
					<s:text name="AuditDataUpload.WillReplaceFile"></s:text>
				</s:if>
                <div style="margin-bottom:20px;text-align:center; font-style:normal; font-weight:normal;"><s:text name="global.maxFileUploadBytes">
                    <s:param><s:property value="maxFileUploadSize" /> </s:param>
                </s:text></div>
				<s:file name="file" size="15%"></s:file>
                <div>
					<s:submit method="uploadFile" value="%{getText('button.UploadFile')}" cssClass="picsbutton positive"></s:submit>
				</div>
			</td>
			
			<s:if test="file != null && file.exists()">	
				<td style="text-align:center;vertical-align:top; width: 45%;border-left: 1px solid #eee;">
					<h3 style="margin-top:0px;"><s:text name="AuditDataUpload.ViewFile"></s:text></h3>
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="auditData.question.id"/>"
						target="_BLANK"><s:text name="AuditDataUpload.OpenExisting"><s:param><s:property value="fileSize" /></s:param></s:text></a>
					<br/><br/>
				
					<s:submit method="deleteFile" value="%{getText('button.DeleteFile')}" cssClass="picsbutton positive" onclick="return confirm(translate('JS.ConfirmDeletion'));"></s:submit>
					<br clear="all" />
				</td>
			</s:if>
		</tr>
	</table>
</div> 
</s:form>

<div style="text-align:center; width:100%;">
	<div style="text-align:center;">
		<button style="text-align:center; width:100%" class="picsbutton" name="button" value="Close" onclick="javascript: closePage()"><s:text name="button.CloseReturn" /></button>
	</div>
</div>

</div>	
</div>
</div>
</body>
</html>
