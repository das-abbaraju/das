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
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function closePage() {
	window.opener.location.reload(true);
	self.close();
}
</script>
</head>
<body>
<br />
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1>Upload Audit File <span class="sub"><s:property value="contractor.name" /></span></h1>
			<s:if test="auditQuestion != null">
				<h4><s:property value="auditQuestion.question"/></h4>
			</s:if>
			<s:include value="../actionMessages.jsp" />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<div style="background-color: #F9F9F9;"><s:hidden name="auditID" />
				<s:hidden name="fileID" />
				<s:hidden name="desc"/>
				<div class="question"><label>File:</label> <s:file name="file"
					value="%{file}" size="50"></s:file><br />
				</div>
				<s:if test="file != null && file.exists()">
					<div class="question"><a
						href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>&fileID=<s:property value="fileID"/>&button=download"
						target="_BLANK">Open Existing <s:property value="fileSize" />
					File</a></div>
				</s:if>
				<div class="question shaded"><label>File Title:</label>
				<s:if test="fileID == 0">
					 <s:textfield
						name="fileName" value="%{contractorAuditFile.description}" size="50" />
				</s:if>
				<s:else>
					<s:property value="contractorAuditFile.description"/>
				</s:else>
				<br/>
				</div>

				<div>
				<button class="picsbutton" onclick="closePage(); return false;">Close and Return to Page</button>
				<s:if test="file != null && file.exists()">
					<button class="picsbutton negative" name="button" value="Delete"
						type="submit"
						onclick="return confirm('Are you sure you want to delete this file?');">DeleteFile</button>
				</s:if>
				<button class="picsbutton positive" name="button" value="Save"
					type="submit">Save</button>
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
