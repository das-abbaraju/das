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
var caoID  = '<s:property value="caoID"/>';
var certID = '<s:property value="certID"/>';
function closePage() {
	try {
		if (caoID > 0 && certID > 0)
			window.opener.saveCert(certID, caoID);
		else
			window.opener.location.reload(true);
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

			<h1>Upload Certificate for <s:property value="contractor.name" /></h1>
			<s:include value="../actionMessages.jsp" />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<s:hidden name="id" />
				<s:hidden name="certID" />
				<s:hidden name="caoID" />
				<br />
				<s:if test="certificate == null || certificate.caos == null || certificate.caos.size() == 0">
				<label>File:</label>	<s:file name="file" value="%{file}" size="50"></s:file><br />
				</s:if>
				<label>Description:</label> <s:textfield name="fileName" value="%{certificate.description}" size="50"/><br/>
					<div align="center" style="font-size=11px;"><table><tr><td>Example: </td><td>Certificate For BP Cherry Point</td></tr>
							<tr><td></td><td>Workers Comp Letter</td></tr> 
							<tr><td></td><td>Generic Certificate</td></tr></table></div>
					
					<div class="buttons">
					<a href="javascript: closePage();">Close and Return to Form</a> 
					<s:if test="file != null && file.exists()">
						<s:if test="certificate.caos == null || certificate.caos.size() == 0">
							<button class="negative" name="button" value="Delete" type="submit" 
							onclick="return confirm('Are you sure you want to delete this file?');">DeleteFile</button>
						</s:if>
					</s:if>
					<button class="positive right" name="button" value="Save" type="submit">Save</button>
				</div>
			</s:form>
			</div>
			<br clear="all" />
			<br clear="all" />
			<s:if test="file != null && file.exists()">
				<div>
					<a href="CertificateUpload.action?id=<s:property value="id"/>&certID=<s:property value="certID"/>&button=download"
						target="_BLANK">Open Existing <s:property value="fileSize" /> File</a>
				</div>
			</s:if>
		</div>
	</div>
</div>
</body>
</html>
