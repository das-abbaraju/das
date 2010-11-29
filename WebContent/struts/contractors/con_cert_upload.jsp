<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta http-equiv="Cache-Control" content="no-cache" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="stylesheet" type="text/css" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" href="css/audit.css?v=<s:property value="version"/>" />
<script type="text/javascript">
var questionID  = '<s:property value="questionID"/>';
var certID = '<s:property value="certID"/>';
var message = 'You have NOT SAVED your file.\nPlease click CANCEL to stay on this page and then click SAVE to save your file.';

<s:if test="changed">
window.onload = function (event) {
	if (questionID > 0) { <% // Audit.action %>
		window.opener.setAnswer(questionID, certID);
		window.opener.reloadQuestion(questionID);
	} else { <% // ConInsureGUARD.action %>
		<s:if test="!duplicate">
			window.opener.location.reload();
		</s:if>
	}
	<s:if test="!duplicate">
		self.close();
	</s:if>
}
</s:if>

function closePage() {
	self.close();
}
</script>
</head>
<body>
<div id="main">
	<div id="bodyholder">
		<div id="content">
			<h1>Upload Certificate <span class="sub"><s:property value="contractor.name" /></span></h1>
			<s:include value="../actionMessages.jsp" />
			<div>
			<s:form enctype="multipart/form-data" method="POST">
				<div style="background-color: #F9F9F9;">
					<s:hidden name="id" />
					<s:hidden name="certID" />
					<s:hidden name="caoID" />
					<s:hidden name="questionID" />
					<s:hidden name="auditID" />
					<s:if test="certificate == null || certificate.caos == null || certificate.caos.size() == 0">
						<div class="question">
							<label>File:</label>
								<s:file id="fileTextbox" name="file" value="%{file}" size="50" ></s:file><br />
						</div>
					</s:if>
					<s:if test="file != null && file.exists()">
						<div class="question">
							<a href="CertificateUpload.action?id=<s:property value="id"/>&certID=<s:property value="certID"/>&button=download"
								target="_BLANK">Open Existing <s:property value="fileSize" /> File</a>
						</div>
					</s:if>
					<div class="question shaded">
						<label>Description:</label> <s:textfield name="fileName" value="%{certificate.description}" size="50"/><br/>
						<div align="center" style="font-size: 10px;font-style: italic;">
							<table>
								<tr><td align="left">Example: </td><td>Certificate For BP Cherry Point</td></tr>
								<tr><td></td><td align="left">Workers Comp Letter</td></tr> 
								<tr><td></td><td align="left">Generic Certificate</td></tr>
							</table>
						</div>
					</div>
					<div>
						<div>
							<button class="picsbutton" onclick="closePage()">
								<s:if test="!changed">
									Cancel
								</s:if>
								<s:else>
									Close
								</s:else>
							</button>
							<s:if test="file != null && file.exists()">
								<s:if test="certificate.caos == null || certificate.caos.size() == 0">
									<button class="picsbutton negative" name="button" value="Delete" type="submit" 
									onclick="return confirm('Are you sure you want to delete this file?');">DeleteFile</button>
								</s:if>
							</s:if>
							<button class="picsbutton positive" name="button" value="Save" type="submit">Save</button>
						</div>
					</div>
					<s:if test="certificate.caos != null && certificate.caos.size() > 0">
						<div class="alert">
							This certificate has been attached to a policy, it cannot be deleted.
						</div>
					</s:if>
				</div>
			</s:form>
			</div>
			<br clear="all" />
		</div>
	</div>
</div>
</body>
</html>
